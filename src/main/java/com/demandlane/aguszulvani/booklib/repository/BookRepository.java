package com.demandlane.aguszulvani.booklib.repository;

import com.demandlane.aguszulvani.booklib.model.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BookRepository extends JpaRepository<Book, UUID> {
    Optional<Book> findById(UUID id);
    boolean existsById(UUID id);

    Optional<Book> findByIsbn(String isbn);
    boolean existsByIsbn(String isbn);
}
