package com.autohire.flow.domain.exception;

public class AiServiceException extends DomainException {
    
    public AiServiceException(String message) {
        super(message);
    }
    
    public AiServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}