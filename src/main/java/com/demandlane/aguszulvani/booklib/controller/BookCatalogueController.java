package com.demandlane.aguszulvani.booklib.controller;

import com.demandlane.aguszulvani.booklib.exception.BusinessException;
import com.demandlane.aguszulvani.booklib.model.entity.Book;
import com.demandlane.aguszulvani.booklib.model.request.BookRequest;
import com.demandlane.aguszulvani.booklib.model.response.BookResponse;
import com.demandlane.aguszulvani.booklib.service.BookCatalogueService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/book")
public class BookCatalogueController {

    private final BookCatalogueService bookCatalogueService;

    public BookCatalogueController(BookCatalogueService bookCatalogueService) {
        this.bookCatalogueService = bookCatalogueService;
    }

    @GetMapping
    public ResponseEntity<List<Book>> getAllBooks() {
        return ResponseEntity.ok(bookCatalogueService.getAllBooks());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookResponse> getBookById(@PathVariable UUID id) {
        try {
            Book book = bookCatalogueService.getBookById(id);
            return ResponseEntity.ok(BookResponse.builder().book(book).build());
        } catch (BusinessException e) {
            return ResponseEntity.status(e.getHttpStatus()).body(BookResponse.builder().message(e.getMessage()).build());
        }
    }

    @GetMapping("/isbn/{isbn}")
    public ResponseEntity<BookResponse> getBookByIsbn(@PathVariable String isbn) {
        try {
            Book book = bookCatalogueService.getBookByIsbn(isbn);
            return ResponseEntity.ok(BookResponse.builder().book(book).build());
        } catch (BusinessException e) {
            return ResponseEntity.status(e.getHttpStatus()).body(BookResponse.builder().message(e.getMessage()).build());
        }
    }

    @PostMapping
    public ResponseEntity<BookResponse> createBook(@RequestBody BookRequest request) {
        try {
            Book book = bookCatalogueService.createBook(request);
            return ResponseEntity.status(201).body(BookResponse.builder().book(book).build());
        } catch (BusinessException e) {
            return ResponseEntity.status(e.getHttpStatus()).body(BookResponse.builder().message(e.getMessage()).build());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookResponse> updateBook(@PathVariable UUID id, @RequestBody BookRequest request) {
        try {
            Book book = bookCatalogueService.updateBook(id, request);
            return ResponseEntity.ok(BookResponse.builder().book(book).build());
        } catch (BusinessException e) {
            return ResponseEntity.status(e.getHttpStatus()).body(BookResponse.builder().message(e.getMessage()).build());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BookResponse> deleteBook(@PathVariable UUID id) {
        try {
            bookCatalogueService.deleteBook(id);
            return ResponseEntity.ok(BookResponse.builder().message("Book deleted successfully").build());
        } catch (BusinessException e) {
            return ResponseEntity.status(e.getHttpStatus()).body(BookResponse.builder().message(e.getMessage()).build());
        }
    }
}
