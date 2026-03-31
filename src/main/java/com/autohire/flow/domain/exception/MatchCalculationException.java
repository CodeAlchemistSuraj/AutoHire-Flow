package com.autohire.flow.domain.exception;

/**
 * Exception thrown when match calculation fails.
 */
public class MatchCalculationException extends DomainException {
    
    public MatchCalculationException(String message) {
        super(message);
    }
    
    public MatchCalculationException(String message, Throwable cause) {
        super(message, cause);
    }
}
