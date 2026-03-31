package com.autohire.flow.infrastructure.web.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Generic API response wrapper for all successful responses
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    
    private boolean success;
    private String message;
    private T data;
    private int status;
    private Instant timestamp;
    private String traceId;
    
    public static <T> ApiResponse<T> ok(T data) {
        return ApiResponse.<T>builder()
            .success(true)
            .message("Success")
            .data(data)
            .status(200)
            .timestamp(Instant.now())
            .build();
    }
    
    public static <T> ApiResponse<T> ok(T data, String message) {
        return ApiResponse.<T>builder()
            .success(true)
            .message(message)
            .data(data)
            .status(200)
            .timestamp(Instant.now())
            .build();
    }
    
    public static <T> ApiResponse<T> created(T data) {
        return ApiResponse.<T>builder()
            .success(true)
            .message("Created")
            .data(data)
            .status(201)
            .timestamp(Instant.now())
            .build();
    }
    
    public static <T> ApiResponse<T> accepted(T data) {
        return ApiResponse.<T>builder()
            .success(true)
            .message("Accepted")
            .data(data)
            .status(202)
            .timestamp(Instant.now())
            .build();
    }
}
