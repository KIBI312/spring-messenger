package com.seitov.messenger.exception;

public class IllegalDataFormatException extends RuntimeException {
    
    public IllegalDataFormatException(String errorMessage) {
        super(errorMessage);
    }
    
    public IllegalDataFormatException(String errorMessage, Throwable cause) {
        super(errorMessage, cause);
    }

}