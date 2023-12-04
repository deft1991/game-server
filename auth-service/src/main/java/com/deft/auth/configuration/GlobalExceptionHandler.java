package com.deft.auth.configuration;

import com.deft.auth.data.dto.ApiError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * @author Sergey Golitsyn
 * created on 03.12.2023
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleException(Exception ex) {
        // Create a JSON response
        ApiError apiError = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", ex.getMessage());
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    // You can add more @ExceptionHandler methods for specific exception types

    // Example for handling custom exceptions:
//    @ExceptionHandler(CustomException.class)
//    public ResponseEntity<Object> handleCustomException(CustomException ex) {
    // Create a JSON response
//        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage());
//        return new ResponseEntity<>(apiError, apiError.getStatus());
//    }

    // Add more @ExceptionHandler methods for specific exception types if needed
}
