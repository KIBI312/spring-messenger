package com.seitov.messenger.exception;

public class UserRegistrationFailureException extends RuntimeException {
    public UserRegistrationFailureException(String errorMessage) {
        super(errorMessage);
    }
}
