package com.gustavofelix.rest_spring_boot.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UnssuportedMathOperationException extends RuntimeException {

    public UnssuportedMathOperationException(String message) {
        super(message);
    }
}
