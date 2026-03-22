package com.demandlane.aguszulvani.booklib.controller;

import com.demandlane.aguszulvani.booklib.exception.BusinessException;
import com.demandlane.aguszulvani.booklib.model.dto.BookLoan;
import com.demandlane.aguszulvani.booklib.model.entity.Loan;
import com.demandlane.aguszulvani.booklib.model.request.BookLoanRequest;
import com.demandlane.aguszulvani.booklib.model.response.BookLoanResponse;
import com.demandlane.aguszulvani.booklib.service.BookLoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/book/loan")
public class BookLoanController {

    @Autowired
    BookLoanService bookLoanService;

    @PostMapping
    public ResponseEntity<BookLoanResponse> requestLoan(@RequestBody BookLoanRequest bookLoanRequest) {
        try {
            Loan loan = bookLoanService.loanBook(bookLoanRequest);
            return ResponseEntity.ok(BookLoanResponse.builder()
                            .bookLoan(loan)
                            .message(null)
                    .build());
        } catch (BusinessException e) {
            return ResponseEntity.status(e.getHttpStatus()).body(BookLoanResponse.builder().message(e.getMessage()).build());
        }
    }

    @PutMapping("returned/{loan-id}")
    public ResponseEntity<BookLoan> returnLoan(@PathVariable("loan-id") UUID loanId) {
        return ResponseEntity.ok(new BookLoan());
    }
}
