package com.autohire.flow.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * DTO for API responses with standardized error/success format.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    
    private Boolean success;
    
    private String message;
    
    private T data;
    
    private String error;
    
    private String errorCode;
    
    private Instant timestamp;
    
    private Integer statusCode;
    
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
            .success(true)
            .message("Operation completed successfully")
            .data(data)
            .timestamp(Instant.now())
            .statusCode(200)
            .build();
    }
    
    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
            .success(true)
            .message(message)
            .data(data)
            .timestamp(Instant.now())
            .statusCode(200)
            .build();
    }
    
    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
            .success(false)
            .message(message)
            .error(message)
            .timestamp(Instant.now())
            .statusCode(400)
            .build();
    }
    
    public static <T> ApiResponse<T> error(String message, int statusCode) {
        return ApiResponse.<T>builder()
            .success(false)
            .message(message)
            .error(message)
            .timestamp(Instant.now())
            .statusCode(statusCode)
            .build();
    }
    
    public static <T> ApiResponse<T> error(String errorCode, String message) {
        return ApiResponse.<T>builder()
            .success(false)
            .message(message)
            .errorCode(errorCode)
            .error(message)
            .timestamp(Instant.now())
            .statusCode(400)
            .build();
    }
    
    // Setter method for data (used by GlobalExceptionHandler)
    public void setData(T data) {
        this.data = data;
    }
}