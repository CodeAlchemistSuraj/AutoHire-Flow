package com.autohire.flow.infrastructure.web.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;

/**
 * Standard error response wrapper for all API errors
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    
    private String errorCode;
    private String message;
    private String path;
    private int status;
    private Instant timestamp;
    private Map<String, String> details;
    private String traceId;
    
    public static ErrorResponse of(String errorCode, String message, int status) {
        return ErrorResponse.builder()
            .errorCode(errorCode)
            .message(message)
            .status(status)
            .timestamp(Instant.now())
            .build();
    }
    
    public static ErrorResponse of(String errorCode, String message, int status, Map<String, String> details) {
        return ErrorResponse.builder()
            .errorCode(errorCode)
            .message(message)
            .status(status)
            .timestamp(Instant.now())
            .details(details)
            .build();
    }
}
