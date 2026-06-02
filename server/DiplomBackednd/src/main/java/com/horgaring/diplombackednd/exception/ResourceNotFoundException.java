package com.horgaring.diplombackednd.exception;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends AppException {

    public ResourceNotFoundException(String resource, Object id) {
        super(resource + " не найден с id: " + id, HttpStatus.NOT_FOUND);
    }

    public ResourceNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
