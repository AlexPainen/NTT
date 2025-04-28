package com.ntt.userapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// Use @ResponseStatus to automatically set the HTTP status code
@ResponseStatus(HttpStatus.CONFLICT) // 409 Conflict
public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(String email) {
        super("Email '" + email + "' already exists");
    }
}