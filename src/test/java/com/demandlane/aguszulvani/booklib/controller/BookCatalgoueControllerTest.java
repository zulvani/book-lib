package com.demandlane.aguszulvani.booklib.controller;

import com.demandlane.aguszulvani.booklib.exception.BusinessException;
import com.demandlane.aguszulvani.booklib.model.entity.Book;
import com.demandlane.aguszulvani.booklib.model.request.BookRequest;
import com.demandlane.aguszulvani.booklib.service.BookCatalogueService;
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
class BookCatalogueControllerTest {

    @Mock
    BookCatalogueService bookCatalogueService;

    @InjectMocks
    BookCatalogueController bookCatalogueController;

    MockMvc mockMvc;
    ObjectMapper objectMapper;

    private UUID bookId;
    private Book book;
    private BookRequest request;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(bookCatalogueController).build();
        objectMapper = new ObjectMapper();

        bookId = UUID.randomUUID();

        book = Book.builder()
                .id(bookId)
                .title("Clean Code")
                .author("Robert C. Martin")
                .isbn("978-0132350884")
                .totalCopies(5)
                .availableCopies(5)
                .build();

        request = new BookRequest();
        request.setTitle("Clean Code");
        request.setAuthor("Robert C. Martin");
        request.setIsbn("978-0132350884");
        request.setTotalCopies(5);
    }

    // ==================== GET /book ====================

    @Test
    void getAllBooks_success_returns200() throws Exception {
        when(bookCatalogueService.getAllBooks()).thenReturn(List.of(book));

        mockMvc.perform(get("/book"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(bookId.toString()))
                .andExpect(jsonPath("$[0].title").value("Clean Code"));

        verify(bookCatalogueService).getAllBooks();
    }

    @Test
    void getAllBooks_emptyList_returns200() throws Exception {
        when(bookCatalogueService.getAllBooks()).thenReturn(List.of());

        mockMvc.perform(get("/book"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    // ==================== GET /book/{id} ====================

    @Test
    void getBookById_success_returns200() throws Exception {
        when(bookCatalogueService.getBookById(bookId)).thenReturn(book);

        mockMvc.perform(get("/book/{id}", bookId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.book.id").value(bookId.toString()))
                .andExpect(jsonPath("$.book.title").value("Clean Code"));
    }

    @Test
    void getBookById_notFound_returns404() throws Exception {
        when(bookCatalogueService.getBookById(bookId))
                .thenThrow(new BusinessException(HttpStatus.NOT_FOUND, "Book not found"));

        mockMvc.perform(get("/book/{id}", bookId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Book not found"));
    }

    // ==================== GET /book/isbn/{isbn} ====================

    @Test
    void getBookByIsbn_success_returns200() throws Exception {
        when(bookCatalogueService.getBookByIsbn("978-0132350884")).thenReturn(book);

        mockMvc.perform(get("/book/isbn/{isbn}", "978-0132350884"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.book.isbn").value("978-0132350884"));
    }

    @Test
    void getBookByIsbn_notFound_returns404() throws Exception {
        when(bookCatalogueService.getBookByIsbn("978-0132350884"))
                .thenThrow(new BusinessException(HttpStatus.NOT_FOUND, "Book not found"));

        mockMvc.perform(get("/book/isbn/{isbn}", "978-0132350884"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Book not found"));
    }

    // ==================== POST /book ====================

    @Test
    void createBook_success_returns201() throws Exception {
        when(bookCatalogueService.createBook(any(BookRequest.class))).thenReturn(book);

        mockMvc.perform(post("/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.book.id").value(bookId.toString()))
                .andExpect(jsonPath("$.book.title").value("Clean Code"));

        verify(bookCatalogueService).createBook(any(BookRequest.class));
    }

    @Test
    void createBook_duplicateIsbn_returns409() throws Exception {
        when(bookCatalogueService.createBook(any(BookRequest.class)))
                .thenThrow(new BusinessException(HttpStatus.CONFLICT, "Book already exists with this ISBN"));

        mockMvc.perform(post("/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Book already exists with this ISBN"));
    }

    // ==================== PUT /book/{id} ====================

    @Test
    void updateBook_success_returns200() throws Exception {
        when(bookCatalogueService.updateBook(eq(bookId), any(BookRequest.class))).thenReturn(book);

        mockMvc.perform(put("/book/{id}", bookId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.book.id").value(bookId.toString()));

        verify(bookCatalogueService).updateBook(eq(bookId), any(BookRequest.class));
    }

    @Test
    void updateBook_notFound_returns404() throws Exception {
        when(bookCatalogueService.updateBook(eq(bookId), any(BookRequest.class)))
                .thenThrow(new BusinessException(HttpStatus.NOT_FOUND, "Book not found"));

        mockMvc.perform(put("/book/{id}", bookId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Book not found"));
    }

    @Test
    void updateBook_isbnConflict_returns409() throws Exception {
        when(bookCatalogueService.updateBook(eq(bookId), any(BookRequest.class)))
                .thenThrow(new BusinessException(HttpStatus.CONFLICT, "ISBN already used by another book"));

        mockMvc.perform(put("/book/{id}", bookId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("ISBN already used by another book"));
    }

    // ==================== DELETE /book/{id} ====================

    @Test
    void deleteBook_success_returns200() throws Exception {
        doNothing().when(bookCatalogueService).deleteBook(bookId);

        mockMvc.perform(delete("/book/{id}", bookId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Book deleted successfully"));

        verify(bookCatalogueService).deleteBook(bookId);
    }

    @Test
    void deleteBook_notFound_returns404() throws Exception {
        doThrow(new BusinessException(HttpStatus.NOT_FOUND, "Book not found"))
                .when(bookCatalogueService).deleteBook(bookId);

        mockMvc.perform(delete("/book/{id}", bookId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Book not found"));
    }

    @Test
    void deleteBook_hasActiveLoans_returns409() throws Exception {
        doThrow(new BusinessException(HttpStatus.CONFLICT, "Cannot delete book with active loans"))
                .when(bookCatalogueService).deleteBook(bookId);

        mockMvc.perform(delete("/book/{id}", bookId))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Cannot delete book with active loans"));
    }
}
