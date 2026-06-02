package com.horgaring.diplombackednd.exception;

import org.springframework.http.HttpStatus;

public class DuplicateResourceException extends AppException {

    public DuplicateResourceException(String message) {
        super(message, HttpStatus.CONFLICT);
    }

    public DuplicateResourceException(String resource, String field, Object value) {
        super(resource + " с " + field + " '" + value + "' уже существует", HttpStatus.CONFLICT);
    }
}
