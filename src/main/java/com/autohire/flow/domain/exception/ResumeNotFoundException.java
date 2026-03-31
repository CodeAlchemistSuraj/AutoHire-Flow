package com.autohire.flow.domain.exception;

/**
 * Exception thrown when a resume is not found.
 */
public class ResumeNotFoundException extends DomainException {
    
    public ResumeNotFoundException(Long userId) {
        super(String.format("Resume not found for user with ID: %d", userId));
    }
    
    public ResumeNotFoundException(String message) {
        super(message);
    }
}
