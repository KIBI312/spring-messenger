package com.seitov.messenger.exception;

public class IllegalDataException extends RuntimeException {
    
    public IllegalDataException(String errorMessage) {
        super(errorMessage);
    }
    
    public IllegalDataException(String errorMessage, Throwable cause) {
        super(errorMessage, cause);
    }

}
