package com.deft.auth.data.dto;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * @author Sergey Golitsyn
 * created on 04.12.2023
 */

@Getter
public class ApiError {

    private final HttpStatus status;
    private final String error;
    private final String message;

    public ApiError(HttpStatus status, String error, String message) {
        this.status = status;
        this.error = error;
        this.message = message;
    }
}
