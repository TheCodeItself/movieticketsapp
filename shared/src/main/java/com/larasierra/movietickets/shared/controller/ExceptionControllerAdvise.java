package com.larasierra.movietickets.shared.controller;

import com.larasierra.movietickets.shared.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class ExceptionControllerAdvise extends ResponseEntityExceptionHandler {

    @ExceptionHandler(AppResourceNotFoundException.class)
    public ResponseEntity<?> notFound(AppResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @ExceptionHandler(AppBadRequestException.class)
    public ResponseEntity<?> badRequest(AppBadRequestException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getPublicMessage());
    }

    @ExceptionHandler(UnauthorizedAccessException.class)
    public ResponseEntity<?> unauthorizedAccess(UnauthorizedAccessException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getPublicMessage());
    }

    @ExceptionHandler(AppInternalErrorException.class)
    public ResponseEntity<?> notFound(AppInternalErrorException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @ExceptionHandler(AppResourceLockedException.class)
    public ResponseEntity<?> locked(AppResourceLockedException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    // TODO: 20/01/2023 add exception handler for @PathVariable (ConstraintViolationException)
}
