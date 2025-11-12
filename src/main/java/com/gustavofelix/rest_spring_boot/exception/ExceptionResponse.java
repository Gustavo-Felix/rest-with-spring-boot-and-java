package com.gustavofelix.rest_spring_boot.exception;

import java.util.Date;

public record ExceptionResponse(Date timeStamp, String message, String details) {}
