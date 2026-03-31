package com.autohire.flow.domain.exception;

/**
 * Exception thrown when a job posting is not found.
 */
public class JobNotFoundException extends DomainException {
    
    public JobNotFoundException(Long jobId) {
        super(String.format("Job posting not found with ID: %d", jobId));
    }
    
    public JobNotFoundException(String message) {
        super(message);
    }
}
