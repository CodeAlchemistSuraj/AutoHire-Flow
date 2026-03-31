package com.autohire.flow.domain.exception;

/**
 * Base exception for all domain-related errors.
 * Used for Clean Architecture - domain layer should not depend on framework exceptions.
 */
public class DomainException extends RuntimeException {
    
    public DomainException(String message) {
        super(message);
    }
    
    public DomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
