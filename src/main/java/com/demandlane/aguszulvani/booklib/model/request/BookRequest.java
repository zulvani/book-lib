package com.demandlane.aguszulvani.booklib.model.request;

import lombok.Data;

@Data
public class BookRequest {
    private String title;
    private String author;
    private String isbn;
    private int totalCopies;
}
