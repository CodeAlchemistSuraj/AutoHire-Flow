package com.autohire.flow.domain.exception;

/**
 * Exception thrown for user-related errors.
 */
public class UserNotSavedException extends DomainException {
    
    public UserNotSavedException(String email) {
        super(String.format("Failed to save user with email: %s", email));
    }
    
    public UserNotSavedException(String message, Throwable cause) {
        super(message, cause);
    }
}
