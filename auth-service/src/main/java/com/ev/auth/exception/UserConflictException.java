package com.ev.auth.exception;

public class UserConflictException extends RuntimeException {
    
    public UserConflictException(String message) {
        super(message);
    }
    
    public UserConflictException(String message, Throwable cause) {
        super(message, cause);
    }
} 