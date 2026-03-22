package com.demandlane.aguszulvani.booklib.repository;

import com.demandlane.aguszulvani.booklib.model.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface BookRepository extends JpaRepository<Book, UUID> {
    Optional<Book> findById(UUID id);
    boolean existsById(UUID id);
}
