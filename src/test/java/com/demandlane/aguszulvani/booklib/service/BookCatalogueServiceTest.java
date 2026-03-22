package com.demandlane.aguszulvani.booklib.service;

import com.demandlane.aguszulvani.booklib.exception.BusinessException;
import com.demandlane.aguszulvani.booklib.model.entity.Book;
import com.demandlane.aguszulvani.booklib.model.request.BookRequest;
import com.demandlane.aguszulvani.booklib.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookCatalogueServiceTest {

    @Mock
    BookRepository bookRepository;

    @InjectMocks
    BookCatalogueService bookCatalogueService;

    private UUID bookId;
    private Book book;
    private BookRequest request;

    @BeforeEach
    void setUp() {
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

    // ==================== getAllBooks ====================

    @Test
    void getAllBooks_success() {
        when(bookRepository.findAll()).thenReturn(List.of(book));

        List<Book> result = bookCatalogueService.getAllBooks();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(bookId, result.get(0).getId());
        verify(bookRepository).findAll();
    }

    @Test
    void getAllBooks_emptyList() {
        when(bookRepository.findAll()).thenReturn(List.of());

        List<Book> result = bookCatalogueService.getAllBooks();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // ==================== getBookById ====================

    @Test
    void getBookById_success() throws BusinessException {
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

        Book result = bookCatalogueService.getBookById(bookId);

        assertNotNull(result);
        assertEquals(bookId, result.getId());
        assertEquals("Clean Code", result.getTitle());
    }

    @Test
    void getBookById_notFound_throwsException() {
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> bookCatalogueService.getBookById(bookId));
        assertEquals("Book not found", ex.getMessage());
    }

    // ==================== getBookByIsbn ====================

    @Test
    void getBookByIsbn_success() throws BusinessException {
        when(bookRepository.findByIsbn("978-0132350884")).thenReturn(Optional.of(book));

        Book result = bookCatalogueService.getBookByIsbn("978-0132350884");

        assertNotNull(result);
        assertEquals("978-0132350884", result.getIsbn());
    }

    @Test
    void getBookByIsbn_notFound_throwsException() {
        when(bookRepository.findByIsbn("978-0132350884")).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> bookCatalogueService.getBookByIsbn("978-0132350884"));
        assertEquals("Book not found", ex.getMessage());
    }

    // ==================== createBook ====================

    @Test
    void createBook_success() throws BusinessException {
        when(bookRepository.existsByIsbn(request.getIsbn())).thenReturn(false);
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        Book result = bookCatalogueService.createBook(request);

        assertNotNull(result);
        assertEquals("Clean Code", result.getTitle());
        assertEquals(5, result.getAvailableCopies());
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    void createBook_duplicateIsbn_throwsException() {
        when(bookRepository.existsByIsbn(request.getIsbn())).thenReturn(true);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> bookCatalogueService.createBook(request));
        assertEquals("Book already exists with this ISBN", ex.getMessage());
        verify(bookRepository, never()).save(any());
    }

    @Test
    void createBook_availableCopiesEqualsTotal() throws BusinessException {
        when(bookRepository.existsByIsbn(request.getIsbn())).thenReturn(false);
        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Book result = bookCatalogueService.createBook(request);

        assertEquals(result.getTotalCopies(), result.getAvailableCopies());
    }

    // ==================== updateBook ====================

    @Test
    void updateBook_success() throws BusinessException {
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(bookRepository.findByIsbn(request.getIsbn())).thenReturn(Optional.of(book));
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        request.setTotalCopies(8);
        Book result = bookCatalogueService.updateBook(bookId, request);

        assertNotNull(result);
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    void updateBook_adjustsAvailableCopies_whenTotalIncreased() throws BusinessException {
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(bookRepository.findByIsbn(request.getIsbn())).thenReturn(Optional.of(book));
        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));

        request.setTotalCopies(8); // increased by 3
        Book result = bookCatalogueService.updateBook(bookId, request);

        assertEquals(8, result.getAvailableCopies()); // 5 + 3 = 8
        assertEquals(8, result.getTotalCopies());
    }

    @Test
    void updateBook_adjustsAvailableCopies_whenTotalDecreased() throws BusinessException {
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(bookRepository.findByIsbn(request.getIsbn())).thenReturn(Optional.of(book));
        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));

        request.setTotalCopies(3); // decreased by 2
        Book result = bookCatalogueService.updateBook(bookId, request);

        assertEquals(3, result.getAvailableCopies()); // 5 - 2 = 3
    }

    @Test
    void updateBook_notFound_throwsException() {
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> bookCatalogueService.updateBook(bookId, request));
        assertEquals("Book not found", ex.getMessage());
    }

    @Test
    void updateBook_isbnTakenByAnotherBook_throwsException() {
        UUID anotherId = UUID.randomUUID();
        Book anotherBook = Book.builder().id(anotherId).isbn("978-0132350884").build();

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(bookRepository.findByIsbn(request.getIsbn())).thenReturn(Optional.of(anotherBook));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> bookCatalogueService.updateBook(bookId, request));
        assertEquals("ISBN already used by another book", ex.getMessage());
    }

    // ==================== deleteBook ====================

    @Test
    void deleteBook_success() throws BusinessException {
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

        bookCatalogueService.deleteBook(bookId);

        verify(bookRepository).delete(book);
    }

    @Test
    void deleteBook_notFound_throwsException() {
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> bookCatalogueService.deleteBook(bookId));
        assertEquals("Book not found", ex.getMessage());
        verify(bookRepository, never()).delete(any());
    }

    @Test
    void deleteBook_hasActiveLoans_throwsException() {
        book.setAvailableCopies(3); // 3 available out of 5 = 2 active loans

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> bookCatalogueService.deleteBook(bookId));
        assertEquals("Cannot delete book with active loans", ex.getMessage());
        verify(bookRepository, never()).delete(any());
    }
}
