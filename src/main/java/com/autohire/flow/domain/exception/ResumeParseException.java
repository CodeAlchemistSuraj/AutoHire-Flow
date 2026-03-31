package com.autohire.flow.domain.exception;

/**
 * Exception thrown when resume parsing fails.
 */
public class ResumeParseException extends DomainException {
    
    public ResumeParseException(String message) {
        super(message);
    }
    
    public ResumeParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
