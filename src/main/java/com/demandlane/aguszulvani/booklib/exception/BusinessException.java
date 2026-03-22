package com.demandlane.aguszulvani.booklib.exception;

import org.springframework.http.HttpStatus;

public class BusinessException extends Exception{
    private HttpStatus httpStatus;

    public BusinessException(HttpStatus httpStatus, String message) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
