package com.docmanager.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class AppExceptions {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    public static class ResourceNotFoundException extends RuntimeException {
        public ResourceNotFoundException(String message) { super(message); }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public static class BadRequestException extends RuntimeException {
        public BadRequestException(String message) { super(message); }
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    public static class DuplicateResourceException extends RuntimeException {
        public DuplicateResourceException(String message) { super(message); }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public static class FileStorageException extends RuntimeException {
        public FileStorageException(String message) { super(message); }
        public FileStorageException(String message, Throwable cause) { super(message, cause); }
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    public static class TokenRefreshException extends RuntimeException {
        public TokenRefreshException(String message) { super(message); }
    }
}
