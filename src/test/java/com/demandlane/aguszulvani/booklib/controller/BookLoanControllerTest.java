package com.demandlane.aguszulvani.booklib.controller;

import com.demandlane.aguszulvani.booklib.exception.BusinessException;
import com.demandlane.aguszulvani.booklib.model.entity.Book;
import com.demandlane.aguszulvani.booklib.model.entity.Loan;
import com.demandlane.aguszulvani.booklib.model.entity.Member;
import com.demandlane.aguszulvani.booklib.model.request.BookLoanRequest;
import com.demandlane.aguszulvani.booklib.service.BookLoanService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class BookLoanControllerTest {

    @Mock
    BookLoanService bookLoanService;

    @InjectMocks
    BookLoanController bookLoanController;

    MockMvc mockMvc;
    ObjectMapper objectMapper;

    private UUID memberId;
    private UUID bookId;
    private UUID loanId;
    private Loan loan;
    private BookLoanRequest request;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(bookLoanController).build();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // for LocalDateTime serialization

        memberId = UUID.randomUUID();
        bookId = UUID.randomUUID();
        loanId = UUID.randomUUID();

        Member member = Member.builder()
                .id(memberId)
                .name("Agus Zulvani")
                .build();

        Book book = Book.builder()
                .id(bookId)
                .title("Clean Code")
                .availableCopies(3)
                .build();

        loan = Loan.builder()
                .id(loanId)
                .member(member)
                .book(book)
                .borrowedAt(LocalDateTime.now())
                .dueDate(LocalDateTime.now().plusDays(14))
                .build();

        request = new BookLoanRequest();
        request.setMemberId(memberId);
        request.setBookId(bookId);
        request.setBorrowDateTime(LocalDateTime.now());
    }

    // ==================== POST /book/loan ====================

    @Test
    void requestLoan_success_returns200() throws Exception {
        when(bookLoanService.loanBook(any(BookLoanRequest.class))).thenReturn(loan);

        mockMvc.perform(post("/book/loan")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookLoan.id").value(loanId.toString()))
                .andExpect(jsonPath("$.message").doesNotExist());

        verify(bookLoanService).loanBook(any(BookLoanRequest.class));
    }

    @Test
    void requestLoan_memberNotFound_returns400() throws Exception {
        when(bookLoanService.loanBook(any(BookLoanRequest.class)))
                .thenThrow(new BusinessException(HttpStatus.BAD_REQUEST, "Member not found"));

        mockMvc.perform(post("/book/loan")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Member not found"));
    }

    @Test
    void requestLoan_bookNotFound_returns400() throws Exception {
        when(bookLoanService.loanBook(any(BookLoanRequest.class)))
                .thenThrow(new BusinessException(HttpStatus.BAD_REQUEST, "Book not found"));

        mockMvc.perform(post("/book/loan")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Book not found"));
    }

    @Test
    void requestLoan_maxLoanReached_returns400() throws Exception {
        when(bookLoanService.loanBook(any(BookLoanRequest.class)))
                .thenThrow(new BusinessException(HttpStatus.BAD_REQUEST, "Member has reached the maximum loan limit"));

        mockMvc.perform(post("/book/loan")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Member has reached the maximum loan limit"));
    }

    @Test
    void requestLoan_overdueLoan_returns400() throws Exception {
        when(bookLoanService.loanBook(any(BookLoanRequest.class)))
                .thenThrow(new BusinessException(HttpStatus.BAD_REQUEST, "Member has overdue loan"));

        mockMvc.perform(post("/book/loan")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Member has overdue loan"));
    }

    @Test
    void requestLoan_bookNotAvailable_returns400() throws Exception {
        when(bookLoanService.loanBook(any(BookLoanRequest.class)))
                .thenThrow(new BusinessException(HttpStatus.BAD_REQUEST, "Book is not available"));

        mockMvc.perform(post("/book/loan")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Book is not available"));
    }

    // ==================== PUT /book/loan/returned/{loan-id} ====================

    @Test
    void returnLoan_success_returns200() throws Exception {
        when(bookLoanService.returnBook(loanId)).thenReturn(loan);

        mockMvc.perform(put("/book/loan/returned/{loanId}", loanId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookLoan.id").value(loanId.toString()))
                .andExpect(jsonPath("$.message").doesNotExist());

        verify(bookLoanService).returnBook(loanId);
    }

    @Test
    void returnLoan_loanNotFound_returns400() throws Exception {
        when(bookLoanService.returnBook(loanId))
                .thenThrow(new BusinessException(HttpStatus.BAD_REQUEST, "Load not found"));

        mockMvc.perform(put("/book/loan/returned/{loanId}", loanId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Load not found"));
    }

    @Test
    void returnLoan_alreadyReturned_returns400() throws Exception {
        when(bookLoanService.returnBook(loanId))
                .thenThrow(new BusinessException(HttpStatus.BAD_REQUEST, "Loan already returned"));

        mockMvc.perform(put("/book/loan/returned/{loanId}", loanId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Loan already returned"));
    }
}
