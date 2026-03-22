package com.demandlane.aguszulvani.booklib.controller;

import com.demandlane.aguszulvani.booklib.exception.BusinessException;
import com.demandlane.aguszulvani.booklib.model.entity.Member;
import com.demandlane.aguszulvani.booklib.model.request.MemberRequest;
import com.demandlane.aguszulvani.booklib.service.MemberService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class MemberControllerTest {

    @Mock
    MemberService memberService;

    @InjectMocks
    MemberController memberController;

    MockMvc mockMvc;
    ObjectMapper objectMapper;

    private UUID memberId;
    private Member member;
    private MemberRequest request;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(memberController).build();
        objectMapper = new ObjectMapper();

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

    // ==================== GET /member ====================

    @Test
    void getAllMembers_success_returns200() throws Exception {
        when(memberService.getAllMembers()).thenReturn(List.of(member));

        mockMvc.perform(get("/member"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(memberId.toString()))
                .andExpect(jsonPath("$[0].name").value("Agus Zulvani"));
    }

    @Test
    void getAllMembers_emptyList_returns200() throws Exception {
        when(memberService.getAllMembers()).thenReturn(List.of());

        mockMvc.perform(get("/member"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    // ==================== GET /member/{id} ====================

    @Test
    void getMemberById_success_returns200() throws Exception {
        when(memberService.getMemberById(memberId)).thenReturn(member);

        mockMvc.perform(get("/member/{id}", memberId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.member.id").value(memberId.toString()))
                .andExpect(jsonPath("$.member.name").value("Agus Zulvani"));
    }

    @Test
    void getMemberById_notFound_returns404() throws Exception {
        when(memberService.getMemberById(memberId))
                .thenThrow(new BusinessException(HttpStatus.NOT_FOUND, "Member not found"));

        mockMvc.perform(get("/member/{id}", memberId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Member not found"));
    }

    // ==================== GET /member/email/{email} ====================

    @Test
    void getMemberByEmail_success_returns200() throws Exception {
        when(memberService.getMemberByEmail("agus@example.com")).thenReturn(member);

        mockMvc.perform(get("/member/email/{email}", "agus@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.member.email").value("agus@example.com"));
    }

    @Test
    void getMemberByEmail_notFound_returns404() throws Exception {
        when(memberService.getMemberByEmail("agus@example.com"))
                .thenThrow(new BusinessException(HttpStatus.NOT_FOUND, "Member not found"));

        mockMvc.perform(get("/member/email/{email}", "agus@example.com"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Member not found"));
    }

    // ==================== POST /member ====================

    @Test
    void createMember_success_returns201() throws Exception {
        when(memberService.createMember(any(MemberRequest.class))).thenReturn(member);

        mockMvc.perform(post("/member")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.member.id").value(memberId.toString()))
                .andExpect(jsonPath("$.member.name").value("Agus Zulvani"));
    }

    @Test
    void createMember_duplicateEmail_returns409() throws Exception {
        when(memberService.createMember(any(MemberRequest.class)))
                .thenThrow(new BusinessException(HttpStatus.CONFLICT, "Member already exists with this email"));

        mockMvc.perform(post("/member")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Member already exists with this email"));
    }

    // ==================== PUT /member/{id} ====================

    @Test
    void updateMember_success_returns200() throws Exception {
        when(memberService.updateMember(eq(memberId), any(MemberRequest.class))).thenReturn(member);

        mockMvc.perform(put("/member/{id}", memberId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.member.id").value(memberId.toString()));
    }

    @Test
    void updateMember_notFound_returns404() throws Exception {
        when(memberService.updateMember(eq(memberId), any(MemberRequest.class)))
                .thenThrow(new BusinessException(HttpStatus.NOT_FOUND, "Member not found"));

        mockMvc.perform(put("/member/{id}", memberId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Member not found"));
    }

    @Test
    void updateMember_emailConflict_returns409() throws Exception {
        when(memberService.updateMember(eq(memberId), any(MemberRequest.class)))
                .thenThrow(new BusinessException(HttpStatus.CONFLICT, "Email already used by another member"));

        mockMvc.perform(put("/member/{id}", memberId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Email already used by another member"));
    }

    // ==================== DELETE /member/{id} ====================

    @Test
    void deleteMember_success_returns200() throws Exception {
        doNothing().when(memberService).deleteMember(memberId);

        mockMvc.perform(delete("/member/{id}", memberId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Member deleted successfully"));
    }

    @Test
    void deleteMember_notFound_returns404() throws Exception {
        doThrow(new BusinessException(HttpStatus.NOT_FOUND, "Member not found"))
                .when(memberService).deleteMember(memberId);

        mockMvc.perform(delete("/member/{id}", memberId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Member not found"));
    }

    @Test
    void deleteMember_hasActiveLoans_returns409() throws Exception {
        doThrow(new BusinessException(HttpStatus.CONFLICT, "Cannot delete member with active loans"))
                .when(memberService).deleteMember(memberId);

        mockMvc.perform(delete("/member/{id}", memberId))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Cannot delete member with active loans"));
    }
}
