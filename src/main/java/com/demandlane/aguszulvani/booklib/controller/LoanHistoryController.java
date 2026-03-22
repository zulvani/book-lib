package com.demandlane.aguszulvani.booklib.controller;

import com.demandlane.aguszulvani.booklib.exception.BusinessException;
import com.demandlane.aguszulvani.booklib.model.response.LoanHistoryResponse;
import com.demandlane.aguszulvani.booklib.service.LoanHistoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/loan/history")
public class LoanHistoryController {

    private final LoanHistoryService loanHistoryService;

    public LoanHistoryController(LoanHistoryService loanHistoryService) {
        this.loanHistoryService = loanHistoryService;
    }

    // ==================== Member Loan History ====================

    @GetMapping("/member/{member-id}")
    public ResponseEntity<LoanHistoryResponse> getMemberLoanHistory(@PathVariable("member-id") UUID memberId) {
        try {
            return ResponseEntity.ok(LoanHistoryResponse.builder()
                    .loans(loanHistoryService.getMemberLoanHistory(memberId))
                    .build());
        } catch (BusinessException e) {
            return ResponseEntity.status(e.getHttpStatus()).body(LoanHistoryResponse.builder()
                    .message(e.getMessage()).build());
        }
    }

    @GetMapping("/member/{member-id}/active")
    public ResponseEntity<LoanHistoryResponse> getMemberActiveLoanHistory(@PathVariable("member-id") UUID memberId) {
        try {
            return ResponseEntity.ok(LoanHistoryResponse.builder()
                    .loans(loanHistoryService.getMemberActiveLoanHistory(memberId))
                    .build());
        } catch (BusinessException e) {
            return ResponseEntity.status(e.getHttpStatus()).body(LoanHistoryResponse.builder()
                    .message(e.getMessage()).build());
        }
    }

    @GetMapping("/member/{member-id}/returned")
    public ResponseEntity<LoanHistoryResponse> getMemberReturnedLoanHistory(@PathVariable("member-id") UUID memberId) {
        try {
            return ResponseEntity.ok(LoanHistoryResponse.builder()
                    .loans(loanHistoryService.getMemberReturnedLoanHistory(memberId))
                    .build());
        } catch (BusinessException e) {
            return ResponseEntity.status(e.getHttpStatus()).body(LoanHistoryResponse.builder()
                    .message(e.getMessage()).build());
        }
    }

    // ==================== Book Loan History ====================

    @GetMapping("/book/{book-id}")
    public ResponseEntity<LoanHistoryResponse> getBookLoanHistory(@PathVariable("book-id") UUID bookId) {
        try {
            return ResponseEntity.ok(LoanHistoryResponse.builder()
                    .loans(loanHistoryService.getBookLoanHistory(bookId))
                    .build());
        } catch (BusinessException e) {
            return ResponseEntity.status(e.getHttpStatus()).body(LoanHistoryResponse.builder()
                    .message(e.getMessage()).build());
        }
    }

    @GetMapping("/book/{book-id}/active")
    public ResponseEntity<LoanHistoryResponse> getBookActiveLoanHistory(@PathVariable("book-id") UUID bookId) {
        try {
            return ResponseEntity.ok(LoanHistoryResponse.builder()
                    .loans(loanHistoryService.getBookActiveLoanHistory(bookId))
                    .build());
        } catch (BusinessException e) {
            return ResponseEntity.status(e.getHttpStatus()).body(LoanHistoryResponse.builder()
                    .message(e.getMessage()).build());
        }
    }

    @GetMapping("/book/{book-id}/returned")
    public ResponseEntity<LoanHistoryResponse> getBookReturnedLoanHistory(@PathVariable("book-id") UUID bookId) {
        try {
            return ResponseEntity.ok(LoanHistoryResponse.builder()
                    .loans(loanHistoryService.getBookReturnedLoanHistory(bookId))
                    .build());
        } catch (BusinessException e) {
            return ResponseEntity.status(e.getHttpStatus()).body(LoanHistoryResponse.builder()
                    .message(e.getMessage()).build());
        }
    }
}
