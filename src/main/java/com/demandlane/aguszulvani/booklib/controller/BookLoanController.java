package com.demandlane.aguszulvani.booklib.controller;

import com.demandlane.aguszulvani.booklib.model.dto.BookLoan;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/book/loan")
public class BookLoanController {

    @PostMapping
    public ResponseEntity<BookLoan> requestLoan() {
        return ResponseEntity.ok(new BookLoan());
    }

    @PutMapping("returned/{loan-id}")
    public ResponseEntity<BookLoan> returnLoan(@PathVariable("loan-id") UUID loanId) {
        return ResponseEntity.ok(new BookLoan());
    }
}
