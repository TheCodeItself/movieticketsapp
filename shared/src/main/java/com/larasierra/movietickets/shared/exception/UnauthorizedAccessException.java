package com.larasierra.movietickets.shared.exception;

public class UnauthorizedAccessException extends RuntimeException {
    private final String publicMessage;

    public UnauthorizedAccessException() {
        publicMessage = null;
    }

    public UnauthorizedAccessException(String message) {
        publicMessage = message;
    }

    public String getPublicMessage() {
        return publicMessage;
    }
}
