package com.demandlane.aguszulvani.booklib.model.request;

import lombok.Data;

@Data
public class MemberRequest {
    private String name;
    private String email;
    private Integer maxActiveLoans;
    private boolean allowMemberToBorrowWhenOverdueLoan;
    private Integer loanDueDays;
}
