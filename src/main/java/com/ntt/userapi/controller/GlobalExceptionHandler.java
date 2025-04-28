package com.ntt.userapi.controller;

import com.ntt.userapi.exception.EmailAlreadyExistsException;
import com.ntt.userapi.exception.InvalidEmailFormatException;
import com.ntt.userapi.exception.InvalidPasswordFormatException;
import com.ntt.userapi.model.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Handles validation errors from @Valid annotation
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, WebRequest request) {
        // Get the first validation error message (can be extended to return all messages)
        String errorMessage = ex.getBindingResult().getFieldError() != null ?
                ex.getBindingResult().getFieldError().getDefaultMessage() : "Validation failed";
        ErrorResponse error = new ErrorResponse(errorMessage);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // Handles custom business logic exceptions
    @ExceptionHandler({EmailAlreadyExistsException.class, InvalidEmailFormatException.class, InvalidPasswordFormatException.class})
    public ResponseEntity<ErrorResponse> handleCustomBusinessException(RuntimeException ex, WebRequest request) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR; // Default

        if (ex instanceof EmailAlreadyExistsException) {
            status = HttpStatus.CONFLICT; // 409
        } else if (ex instanceof InvalidEmailFormatException || ex instanceof InvalidPasswordFormatException) {
            status = HttpStatus.BAD_REQUEST; // 400
        }
        // Add more exception types here as needed for other operations (e.g., UserNotFoundException)

        ErrorResponse error = new ErrorResponse(ex.getMessage());
        return new ResponseEntity<>(error, status);
    }

    // Generic exception handler for anything else unexpected
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllExceptions(Exception ex, WebRequest request) {
        // Log the exception for debugging purposes (important!)
        ex.printStackTrace(); // Or use a logger

        ErrorResponse error = new ErrorResponse("An unexpected error occurred"); // Generic message for client
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR); // 500
    }
}