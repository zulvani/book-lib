package com.demandlane.aguszulvani.booklib.model.response;

import com.demandlane.aguszulvani.booklib.model.entity.Loan;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookLoanResponse {
    // todo: use DTO instead of entity
    private Loan bookLoan;
    private String message;
}
