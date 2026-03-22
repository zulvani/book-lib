package com.demandlane.aguszulvani.booklib.controller;

import com.demandlane.aguszulvani.booklib.model.dto.BookLoan;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/book/loan")
public class BookLoanController {

    @PostMapping
    public ResponseEntity<BookLoan> requestLoan() {
        return ResponseEntity.ok(new BookLoan());
    }
}
