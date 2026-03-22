package com.demandlane.aguszulvani.booklib.repository;

import com.demandlane.aguszulvani.booklib.model.entity.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LoanRepository extends JpaRepository<Loan, UUID> {
    Optional<Loan> findById(UUID id);

    // active loans (not returned yet)
    List<Loan> findByMemberIdAndReturnedAtIsNull(UUID memberId);

    List<Loan> findByBookIdAndReturnedAtIsNull(UUID bookId);

    long countByMemberIdAndReturnedAtIsNull(UUID memberId);

    // overdue loans
    List<Loan> findByMemberIdAndReturnedAtIsNullAndDueDateBefore(
            UUID memberId,
            java.time.LocalDateTime now
    );
}
