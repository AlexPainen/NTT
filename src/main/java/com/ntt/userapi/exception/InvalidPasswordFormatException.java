package com.ntt.userapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST) // 400 Bad Request
public class InvalidPasswordFormatException extends RuntimeException {
    public InvalidPasswordFormatException() {
        super("Invalid password format");
    }
}