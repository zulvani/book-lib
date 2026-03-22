package com.demandlane.aguszulvani.booklib.service;

import com.demandlane.aguszulvani.booklib.exception.BusinessException;
import com.demandlane.aguszulvani.booklib.model.entity.Book;
import com.demandlane.aguszulvani.booklib.model.entity.Loan;
import com.demandlane.aguszulvani.booklib.model.entity.Member;
import com.demandlane.aguszulvani.booklib.repository.BookRepository;
import com.demandlane.aguszulvani.booklib.repository.LoanRepository;
import com.demandlane.aguszulvani.booklib.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanHistoryServiceTest {

    @Mock
    LoanRepository loanRepository;

    @Mock
    MemberRepository memberRepository;

    @Mock
    BookRepository bookRepository;

    @InjectMocks
    LoanHistoryService loanHistoryService;

    private UUID memberId;
    private UUID bookId;
    private Loan activeLoan;
    private Loan returnedLoan;

    @BeforeEach
    void setUp() {
        memberId = UUID.randomUUID();
        bookId = UUID.randomUUID();

        Member member = Member.builder().id(memberId).name("Agus Zulvani").build();
        Book book = Book.builder().id(bookId).title("Clean Code").build();

        activeLoan = Loan.builder()
                .id(UUID.randomUUID())
                .member(member)
                .book(book)
                .borrowedAt(LocalDateTime.now().minusDays(5))
                .dueDate(LocalDateTime.now().plusDays(9))
                .returnedAt(null)
                .build();

        returnedLoan = Loan.builder()
                .id(UUID.randomUUID())
                .member(member)
                .book(book)
                .borrowedAt(LocalDateTime.now().minusDays(20))
                .dueDate(LocalDateTime.now().minusDays(6))
                .returnedAt(LocalDateTime.now().minusDays(7))
                .build();
    }

    // ==================== Member Loan History ====================

    @Test
    void getMemberLoanHistory_success() throws BusinessException {
        when(memberRepository.existsById(memberId)).thenReturn(true);
        when(loanRepository.findByMemberId(memberId)).thenReturn(List.of(activeLoan, returnedLoan));

        List<Loan> result = loanHistoryService.getMemberLoanHistory(memberId);

        assertEquals(2, result.size());
        verify(loanRepository).findByMemberId(memberId);
    }

    @Test
    void getMemberLoanHistory_memberNotFound_throwsException() {
        when(memberRepository.existsById(memberId)).thenReturn(false);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> loanHistoryService.getMemberLoanHistory(memberId));
        assertEquals("Member not found", ex.getMessage());
        verify(loanRepository, never()).findByMemberId(any());
    }

    @Test
    void getMemberLoanHistory_emptyList() throws BusinessException {
        when(memberRepository.existsById(memberId)).thenReturn(true);
        when(loanRepository.findByMemberId(memberId)).thenReturn(List.of());

        List<Loan> result = loanHistoryService.getMemberLoanHistory(memberId);

        assertTrue(result.isEmpty());
    }

    @Test
    void getMemberActiveLoanHistory_success() throws BusinessException {
        when(memberRepository.existsById(memberId)).thenReturn(true);
        when(loanRepository.findByMemberIdAndReturnedAtIsNull(memberId)).thenReturn(List.of(activeLoan));

        List<Loan> result = loanHistoryService.getMemberActiveLoanHistory(memberId);

        assertEquals(1, result.size());
        assertNull(result.get(0).getReturnedAt());
    }

    @Test
    void getMemberActiveLoanHistory_memberNotFound_throwsException() {
        when(memberRepository.existsById(memberId)).thenReturn(false);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> loanHistoryService.getMemberActiveLoanHistory(memberId));
        assertEquals("Member not found", ex.getMessage());
    }

    @Test
    void getMemberReturnedLoanHistory_success() throws BusinessException {
        when(memberRepository.existsById(memberId)).thenReturn(true);
        when(loanRepository.findByMemberIdAndReturnedAtIsNotNull(memberId)).thenReturn(List.of(returnedLoan));

        List<Loan> result = loanHistoryService.getMemberReturnedLoanHistory(memberId);

        assertEquals(1, result.size());
        assertNotNull(result.get(0).getReturnedAt());
    }

    @Test
    void getMemberReturnedLoanHistory_memberNotFound_throwsException() {
        when(memberRepository.existsById(memberId)).thenReturn(false);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> loanHistoryService.getMemberReturnedLoanHistory(memberId));
        assertEquals("Member not found", ex.getMessage());
    }

    // ==================== Book Loan History ====================

    @Test
    void getBookLoanHistory_success() throws BusinessException {
        when(bookRepository.existsById(bookId)).thenReturn(true);
        when(loanRepository.findByBookId(bookId)).thenReturn(List.of(activeLoan, returnedLoan));

        List<Loan> result = loanHistoryService.getBookLoanHistory(bookId);

        assertEquals(2, result.size());
        verify(loanRepository).findByBookId(bookId);
    }

    @Test
    void getBookLoanHistory_bookNotFound_throwsException() {
        when(bookRepository.existsById(bookId)).thenReturn(false);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> loanHistoryService.getBookLoanHistory(bookId));
        assertEquals("Book not found", ex.getMessage());
        verify(loanRepository, never()).findByBookId(any());
    }

    @Test
    void getBookLoanHistory_emptyList() throws BusinessException {
        when(bookRepository.existsById(bookId)).thenReturn(true);
        when(loanRepository.findByBookId(bookId)).thenReturn(List.of());

        List<Loan> result = loanHistoryService.getBookLoanHistory(bookId);

        assertTrue(result.isEmpty());
    }

    @Test
    void getBookActiveLoanHistory_success() throws BusinessException {
        when(bookRepository.existsById(bookId)).thenReturn(true);
        when(loanRepository.findByBookIdAndReturnedAtIsNull(bookId)).thenReturn(List.of(activeLoan));

        List<Loan> result = loanHistoryService.getBookActiveLoanHistory(bookId);

        assertEquals(1, result.size());
        assertNull(result.get(0).getReturnedAt());
    }

    @Test
    void getBookActiveLoanHistory_bookNotFound_throwsException() {
        when(bookRepository.existsById(bookId)).thenReturn(false);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> loanHistoryService.getBookActiveLoanHistory(bookId));
        assertEquals("Book not found", ex.getMessage());
    }

    @Test
    void getBookReturnedLoanHistory_success() throws BusinessException {
        when(bookRepository.existsById(bookId)).thenReturn(true);
        when(loanRepository.findByBookIdAndReturnedAtIsNotNull(bookId)).thenReturn(List.of(returnedLoan));

        List<Loan> result = loanHistoryService.getBookReturnedLoanHistory(bookId);

        assertEquals(1, result.size());
        assertNotNull(result.get(0).getReturnedAt());
    }

    @Test
    void getBookReturnedLoanHistory_bookNotFound_throwsException() {
        when(bookRepository.existsById(bookId)).thenReturn(false);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> loanHistoryService.getBookReturnedLoanHistory(bookId));
        assertEquals("Book not found", ex.getMessage());
    }
}
