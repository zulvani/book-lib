package com.demandlane.aguszulvani.booklib.repository;

import com.demandlane.aguszulvani.booklib.model.entity.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface LoanRepository extends JpaRepository<Loan, UUID> {

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
