package com.autohire.flow.application.dto;

import lombok.*;
import java.time.Instant;

/**
 * DTO for API responses with standardized error/success format.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {
    
    private Boolean success;
    
    private String message;
    
    private T data;
    
    private String errorCode;
    
    private Instant timestamp;
    
    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
            .success(true)
            .message(message)
            .data(data)
            .timestamp(Instant.now())
            .build();
    }
    
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
            .success(true)
            .message("Operation completed successfully")
            .data(data)
            .timestamp(Instant.now())
            .build();
    }
    
    public static <T> ApiResponse<T> error(String errorCode, String message) {
        return ApiResponse.<T>builder()
            .success(false)
            .message(message)
            .errorCode(errorCode)
            .timestamp(Instant.now())
            .build();
    }
}
