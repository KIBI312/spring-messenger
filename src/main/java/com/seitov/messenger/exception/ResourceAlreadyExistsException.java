package com.seitov.messenger.exception;

public class ResourceAlreadyExistsException extends RuntimeException {
    
    public ResourceAlreadyExistsException(String errorMessage) {
        super(errorMessage);
    }

    public ResourceAlreadyExistsException(String errorMessage, Throwable cause) {
        super(errorMessage, cause);
    }

}