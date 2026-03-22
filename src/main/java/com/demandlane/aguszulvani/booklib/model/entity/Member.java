package com.demandlane.aguszulvani.booklib.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Entity
@Table(name = "member")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "max_active_loans", nullable = false)
    private int maxActiveLoans;

    @Column(name = "allow_member_to_borrow_when_overdue_loan", nullable = false)
    private boolean allowMemberToBorrowWhenOverdueLoan;

    @Column(name = "loan_due_days", nullable = false)
    private int loanDueDays;
}
