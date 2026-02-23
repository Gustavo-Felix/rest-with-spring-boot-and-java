package com.gustavofelix.rest_spring_boot.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ResourceBadRequestException extends RuntimeException {

    public ResourceBadRequestException() {
        super("It is not allowed to persist a null object!");
    }

    public ResourceBadRequestException(String message) {
        super(message);
    }
}
