package com.demandlane.aguszulvani.booklib.service;

import com.demandlane.aguszulvani.booklib.exception.BusinessException;
import com.demandlane.aguszulvani.booklib.model.entity.Member;
import com.demandlane.aguszulvani.booklib.model.request.MemberRequest;
import com.demandlane.aguszulvani.booklib.repository.LoanRepository;
import com.demandlane.aguszulvani.booklib.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    MemberRepository memberRepository;

    @Mock
    LoanRepository loanRepository;

    @InjectMocks
    MemberService memberService;

    private UUID memberId;
    private Member member;
    private MemberRequest request;

    @BeforeEach
    void setUp() {
        memberId = UUID.randomUUID();

        member = Member.builder()
                .id(memberId)
                .name("Agus Zulvani")
                .email("agus@example.com")
                .maxActiveLoans(3)
                .allowMemberToBorrowWhenOverdueLoan(false)
                .loanDueDays(14)
                .build();

        request = new MemberRequest();
        request.setName("Agus Zulvani");
        request.setEmail("agus@example.com");
        request.setMaxActiveLoans(3);
        request.setAllowMemberToBorrowWhenOverdueLoan(false);
        request.setLoanDueDays(14);
    }

    // ==================== getAllMembers ====================

    @Test
    void getAllMembers_success() {
        when(memberRepository.findAll()).thenReturn(List.of(member));

        List<Member> result = memberService.getAllMembers();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(memberId, result.get(0).getId());
    }

    @Test
    void getAllMembers_emptyList() {
        when(memberRepository.findAll()).thenReturn(List.of());

        List<Member> result = memberService.getAllMembers();

        assertTrue(result.isEmpty());
    }

    // ==================== getMemberById ====================

    @Test
    void getMemberById_success() throws BusinessException {
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));

        Member result = memberService.getMemberById(memberId);

        assertNotNull(result);
        assertEquals(memberId, result.getId());
        assertEquals("Agus Zulvani", result.getName());
    }

    @Test
    void getMemberById_notFound_throwsException() {
        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> memberService.getMemberById(memberId));
        assertEquals("Member not found", ex.getMessage());
    }

    // ==================== getMemberByEmail ====================

    @Test
    void getMemberByEmail_success() throws BusinessException {
        when(memberRepository.findByEmail("agus@example.com")).thenReturn(Optional.of(member));

        Member result = memberService.getMemberByEmail("agus@example.com");

        assertNotNull(result);
        assertEquals("agus@example.com", result.getEmail());
    }

    @Test
    void getMemberByEmail_notFound_throwsException() {
        when(memberRepository.findByEmail("agus@example.com")).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> memberService.getMemberByEmail("agus@example.com"));
        assertEquals("Member not found", ex.getMessage());
    }

    // ==================== createMember ====================

    @Test
    void createMember_success() throws BusinessException {
        when(memberRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(memberRepository.save(any(Member.class))).thenReturn(member);

        Member result = memberService.createMember(request);

        assertNotNull(result);
        assertEquals("Agus Zulvani", result.getName());
        verify(memberRepository).save(any(Member.class));
    }

    @Test
    void createMember_duplicateEmail_throwsException() {
        when(memberRepository.existsByEmail(request.getEmail())).thenReturn(true);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> memberService.createMember(request));
        assertEquals("Member already exists with this email", ex.getMessage());
        verify(memberRepository, never()).save(any());
    }

    // ==================== updateMember ====================

    @Test
    void updateMember_success() throws BusinessException {
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(memberRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(member));
        when(memberRepository.save(any(Member.class))).thenReturn(member);

        Member result = memberService.updateMember(memberId, request);

        assertNotNull(result);
        verify(memberRepository).save(any(Member.class));
    }

    @Test
    void updateMember_notFound_throwsException() {
        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> memberService.updateMember(memberId, request));
        assertEquals("Member not found", ex.getMessage());
    }

    @Test
    void updateMember_emailTakenByAnotherMember_throwsException() {
        UUID anotherId = UUID.randomUUID();
        Member anotherMember = Member.builder().id(anotherId).email("agus@example.com").build();

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(memberRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(anotherMember));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> memberService.updateMember(memberId, request));
        assertEquals("Email already used by another member", ex.getMessage());
    }

    // ==================== deleteMember ====================

    @Test
    void deleteMember_success() throws BusinessException {
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(loanRepository.countByMemberIdAndReturnedAtIsNull(memberId)).thenReturn(0L);

        memberService.deleteMember(memberId);

        verify(memberRepository).delete(member);
    }

    @Test
    void deleteMember_notFound_throwsException() {
        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> memberService.deleteMember(memberId));
        assertEquals("Member not found", ex.getMessage());
        verify(memberRepository, never()).delete(any());
    }

    @Test
    void deleteMember_hasActiveLoans_throwsException() {
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(loanRepository.countByMemberIdAndReturnedAtIsNull(memberId)).thenReturn(2L);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> memberService.deleteMember(memberId));
        assertEquals("Cannot delete member with active loans", ex.getMessage());
        verify(memberRepository, never()).delete(any());
    }
}
