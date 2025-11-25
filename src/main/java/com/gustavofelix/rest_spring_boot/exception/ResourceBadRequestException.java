package com.gustavofelix.rest_spring_boot.exception;

public class ResourceBadRequestException extends RuntimeException {

    public ResourceBadRequestException() {
        super("It is not allowed to persist a null object!");
    }

    public ResourceBadRequestException(String message) {
        super(message);
    }
}
