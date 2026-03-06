package com.horgaring.diplombackednd.exception;

import org.springframework.http.HttpStatus;

public class AccessDeniedException extends AppException {

    public AccessDeniedException(String message) {
        super(message, HttpStatus.FORBIDDEN);
    }

    public AccessDeniedException() {
        super("Access denied", HttpStatus.FORBIDDEN);
    }
}
