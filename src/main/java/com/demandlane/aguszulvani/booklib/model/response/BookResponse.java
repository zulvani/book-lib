package com.demandlane.aguszulvani.booklib.model.response;

import com.demandlane.aguszulvani.booklib.model.entity.Book;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookResponse {
    private Book book;
    private String message;
}
