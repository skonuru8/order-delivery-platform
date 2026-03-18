package com.platform.main.shared.exception;

public class UnauthorizedOrderAccessException extends RuntimeException {
    public UnauthorizedOrderAccessException() {
        super("You are not authorized to access this order");
    }
}