package com.seitov.messenger.exception;

public class NotAuthorizedException extends RuntimeException {
    
    public NotAuthorizedException(String errorMessage) {
        super(errorMessage);
    }

    public NotAuthorizedException(String errorMessage, Throwable cause) {
        super(errorMessage, cause);
    }

}