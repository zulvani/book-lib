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
    UUID memberId;
    LocalDateTime borrowDateTime;

}
