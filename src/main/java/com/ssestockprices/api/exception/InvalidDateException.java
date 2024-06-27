package com.ssestockprices.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class InvalidDateException extends ResponseStatusException {
    public InvalidDateException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
