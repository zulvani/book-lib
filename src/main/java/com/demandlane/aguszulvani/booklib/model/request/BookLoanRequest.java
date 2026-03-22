package com.demandlane.aguszulvani.booklib.model.request;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookLoanRequest {
    UUID bookId;
    /**
     * optionally, we can get member id from a JWT token,
     * but if you want to use library staff to make the loan for a member, you can pass member id here
     */
    UUID memberId;
    LocalDateTime borrowDateTime;
}
