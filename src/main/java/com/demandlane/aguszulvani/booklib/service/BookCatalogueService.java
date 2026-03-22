package com.demandlane.aguszulvani.booklib.service;

import com.demandlane.aguszulvani.booklib.exception.BusinessException;
import com.demandlane.aguszulvani.booklib.model.entity.Book;
import com.demandlane.aguszulvani.booklib.model.request.BookRequest;
import com.demandlane.aguszulvani.booklib.repository.BookRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@Transactional(readOnly = true)
public class BookCatalogueService {

    private final BookRepository bookRepository;

    public BookCatalogueService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<Book> getAllBooks() {
        log.info("Fetching all books");
        return bookRepository.findAll();
    }

    public Book getBookById(UUID id) throws BusinessException {
        log.info("Fetching book by id: {}", id);
        return bookRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Book not found: {}", id);
                    return new BusinessException(HttpStatus.NOT_FOUND, "Book not found");
                });
    }

    public Book getBookByIsbn(String isbn) throws BusinessException {
        log.info("Fetching book by isbn: {}", isbn);
        return bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> {
                    log.error("Book not found with isbn: {}", isbn);
                    return new BusinessException(HttpStatus.NOT_FOUND, "Book not found");
                });
    }

    @Transactional
    public Book createBook(BookRequest request) throws BusinessException {
        log.info("Creating book: {}", request);

        if (bookRepository.existsByIsbn(request.getIsbn())) {
            log.error("Book already exists with isbn: {}", request.getIsbn());
            throw new BusinessException(HttpStatus.CONFLICT, "Book already exists with this ISBN");
        }

        Book book = Book.builder()
                .title(request.getTitle())
                .author(request.getAuthor())
                .isbn(request.getIsbn())
                .totalCopies(request.getTotalCopies())
                .availableCopies(request.getTotalCopies())
                .build();

        log.info("Book created: {}", book);
        return bookRepository.save(book);
    }

    @Transactional
    public Book updateBook(UUID id, BookRequest request) throws BusinessException {
        log.info("Updating book: {}", id);

        Book existingBook = bookRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Book not found: {}", id);
                    return new BusinessException(HttpStatus.NOT_FOUND, "Book not found");
                });

        // check if ISBN is taken by another book
        Optional<Book> bookWithSameIsbn = bookRepository.findByIsbn(request.getIsbn());
        if (bookWithSameIsbn.isPresent() && !bookWithSameIsbn.get().getId().equals(id)) {
            log.error("ISBN already used by another book: {}", request.getIsbn());
            throw new BusinessException(HttpStatus.CONFLICT, "ISBN already used by another book");
        }

        int copyDifference = request.getTotalCopies() - existingBook.getTotalCopies();

        existingBook.setTitle(request.getTitle());
        existingBook.setAuthor(request.getAuthor());
        existingBook.setIsbn(request.getIsbn());
        existingBook.setTotalCopies(request.getTotalCopies());
        existingBook.setAvailableCopies(existingBook.getAvailableCopies() + copyDifference);

        log.info("Book updated: {}", existingBook);
        return bookRepository.save(existingBook);
    }

    @Transactional
    public void deleteBook(UUID id) throws BusinessException {
        log.info("Deleting book: {}", id);

        Book book = bookRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Book not found: {}", id);
                    return new BusinessException(HttpStatus.NOT_FOUND, "Book not found");
                });

        if (book.getAvailableCopies() < book.getTotalCopies()) {
            log.error("Cannot delete book with active loans: {}", id);
            throw new BusinessException(HttpStatus.CONFLICT, "Cannot delete book with active loans");
        }

        bookRepository.delete(book);
        log.info("Book deleted: {}", id);
    }
}
