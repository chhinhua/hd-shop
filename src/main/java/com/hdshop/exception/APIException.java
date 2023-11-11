package com.hdshop.exception;

import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@NoArgsConstructor
public class APIException extends RuntimeException {
    private HttpStatus httpStatus;
    private String message;

    public APIException(String message) {
        super(message);
    }

    public APIException(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public APIException(String message, HttpStatus httpStatus, String message1) {
        super(message);
        this.httpStatus = httpStatus;
        this.message = message1;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
