package com.autohire.flow.domain.exception;

public class AiServiceException extends DomainException {
    
    public AiServiceException(String message) {
        super(message, "AI_SERVICE_ERROR");
    }
    
    public AiServiceException(String message, Throwable cause) {
        super(message, "AI_SERVICE_ERROR", cause);
    }
}
