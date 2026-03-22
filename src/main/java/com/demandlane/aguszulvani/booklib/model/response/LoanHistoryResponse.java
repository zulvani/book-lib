package com.demandlane.aguszulvani.booklib.model.response;

import com.demandlane.aguszulvani.booklib.model.entity.Loan;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class LoanHistoryResponse {
    private List<Loan> loans;
    private String message;
}
