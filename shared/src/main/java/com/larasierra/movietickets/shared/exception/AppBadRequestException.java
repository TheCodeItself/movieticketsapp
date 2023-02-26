package com.larasierra.movietickets.shared.exception;

public class AppBadRequestException extends RuntimeException {
    private final String publicMessage;

    public AppBadRequestException(String message) {
        publicMessage = message;
    }

    public String getPublicMessage() {
        return publicMessage;
    }
}
