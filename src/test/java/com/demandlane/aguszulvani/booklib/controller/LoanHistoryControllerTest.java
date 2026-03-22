package com.demandlane.aguszulvani.booklib.controller;

import com.demandlane.aguszulvani.booklib.exception.BusinessException;
import com.demandlane.aguszulvani.booklib.model.entity.Book;
import com.demandlane.aguszulvani.booklib.model.entity.Loan;
import com.demandlane.aguszulvani.booklib.model.entity.Member;
import com.demandlane.aguszulvani.booklib.service.LoanHistoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class LoanHistoryControllerTest {

    @Mock
    LoanHistoryService loanHistoryService;

    @InjectMocks
    LoanHistoryController loanHistoryController;

    MockMvc mockMvc;

    private UUID memberId;
    private UUID bookId;
    private Loan activeLoan;
    private Loan returnedLoan;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(loanHistoryController).build();

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
    void getMemberLoanHistory_success_returns200() throws Exception {
        when(loanHistoryService.getMemberLoanHistory(memberId)).thenReturn(List.of(activeLoan, returnedLoan));

        mockMvc.perform(get("/loan/history/member/{member-id}", memberId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.loans.length()").value(2));
    }

    @Test
    void getMemberLoanHistory_memberNotFound_returns404() throws Exception {
        when(loanHistoryService.getMemberLoanHistory(memberId))
                .thenThrow(new BusinessException(HttpStatus.NOT_FOUND, "Member not found"));

        mockMvc.perform(get("/loan/history/member/{member-id}", memberId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Member not found"));
    }

    @Test
    void getMemberActiveLoanHistory_success_returns200() throws Exception {
        when(loanHistoryService.getMemberActiveLoanHistory(memberId)).thenReturn(List.of(activeLoan));

        mockMvc.perform(get("/loan/history/member/{member-id}/active", memberId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.loans.length()").value(1));
    }

    @Test
    void getMemberActiveLoanHistory_memberNotFound_returns404() throws Exception {
        when(loanHistoryService.getMemberActiveLoanHistory(memberId))
                .thenThrow(new BusinessException(HttpStatus.NOT_FOUND, "Member not found"));

        mockMvc.perform(get("/loan/history/member/{member-id}/active", memberId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Member not found"));
    }

    @Test
    void getMemberReturnedLoanHistory_success_returns200() throws Exception {
        when(loanHistoryService.getMemberReturnedLoanHistory(memberId)).thenReturn(List.of(returnedLoan));

        mockMvc.perform(get("/loan/history/member/{member-id}/returned", memberId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.loans.length()").value(1));
    }

    @Test
    void getMemberReturnedLoanHistory_memberNotFound_returns404() throws Exception {
        when(loanHistoryService.getMemberReturnedLoanHistory(memberId))
                .thenThrow(new BusinessException(HttpStatus.NOT_FOUND, "Member not found"));

        mockMvc.perform(get("/loan/history/member/{member-id}/returned", memberId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Member not found"));
    }

    // ==================== Book Loan History ====================

    @Test
    void getBookLoanHistory_success_returns200() throws Exception {
        when(loanHistoryService.getBookLoanHistory(bookId)).thenReturn(List.of(activeLoan, returnedLoan));

        mockMvc.perform(get("/loan/history/book/{book-id}", bookId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.loans.length()").value(2));
    }

    @Test
    void getBookLoanHistory_bookNotFound_returns404() throws Exception {
        when(loanHistoryService.getBookLoanHistory(bookId))
                .thenThrow(new BusinessException(HttpStatus.NOT_FOUND, "Book not found"));

        mockMvc.perform(get("/loan/history/book/{book-id}", bookId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Book not found"));
    }

    @Test
    void getBookActiveLoanHistory_success_returns200() throws Exception {
        when(loanHistoryService.getBookActiveLoanHistory(bookId)).thenReturn(List.of(activeLoan));

        mockMvc.perform(get("/loan/history/book/{book-id}/active", bookId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.loans.length()").value(1));
    }

    @Test
    void getBookActiveLoanHistory_bookNotFound_returns404() throws Exception {
        when(loanHistoryService.getBookActiveLoanHistory(bookId))
                .thenThrow(new BusinessException(HttpStatus.NOT_FOUND, "Book not found"));

        mockMvc.perform(get("/loan/history/book/{book-id}/active", bookId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Book not found"));
    }

    @Test
    void getBookReturnedLoanHistory_success_returns200() throws Exception {
        when(loanHistoryService.getBookReturnedLoanHistory(bookId)).thenReturn(List.of(returnedLoan));

        mockMvc.perform(get("/loan/history/book/{book-id}/returned", bookId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.loans.length()").value(1));
    }

    @Test
    void getBookReturnedLoanHistory_bookNotFound_returns404() throws Exception {
        when(loanHistoryService.getBookReturnedLoanHistory(bookId))
                .thenThrow(new BusinessException(HttpStatus.NOT_FOUND, "Book not found"));

        mockMvc.perform(get("/loan/history/book/{book-id}/returned", bookId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Book not found"));
    }
}
