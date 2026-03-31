package com.autohire.flow.infrastructure.web.exception;

import com.autohire.flow.domain.exception.*;
import com.autohire.flow.infrastructure.web.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Global exception handler for all REST API endpoints
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    
    @ExceptionHandler(ResumeNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorResponse> handleResumeNotFound(
            ResumeNotFoundException e, WebRequest request) {
        log.warn("Resume not found: {}", e.getMessage());
        ErrorResponse response = ErrorResponse.builder()
            .errorCode(e.getErrorCode())
            .message(e.getMessage())
            .status(HttpStatus.NOT_FOUND.value())
            .timestamp(Instant.now())
            .path(request.getDescription(false).replace("uri=", ""))
            .build();
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
    
    @ExceptionHandler(JobNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorResponse> handleJobNotFound(
            JobNotFoundException e, WebRequest request) {
        log.warn("Job not found: {}", e.getMessage());
        ErrorResponse response = ErrorResponse.builder()
            .errorCode("JOB_NOT_FOUND")
            .message(e.getMessage())
            .status(HttpStatus.NOT_FOUND.value())
            .timestamp(Instant.now())
            .path(request.getDescription(false).replace("uri=", ""))
            .build();
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
    
    @ExceptionHandler(FileTooLargeException.class)
    @ResponseStatus(HttpStatus.PAYLOAD_TOO_LARGE)
    public ResponseEntity<ErrorResponse> handleFileTooLarge(
            FileTooLargeException e, WebRequest request) {
        log.warn("File too large: {}", e.getMessage());
        ErrorResponse response = ErrorResponse.builder()
            .errorCode(e.getErrorCode())
            .message(e.getMessage())
            .status(HttpStatus.PAYLOAD_TOO_LARGE.value())
            .timestamp(Instant.now())
            .path(request.getDescription(false).replace("uri=", ""))
            .build();
        return new ResponseEntity<>(response, HttpStatus.PAYLOAD_TOO_LARGE);
    }
    
    @ExceptionHandler(ResumeParseException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleResumeParse(
            ResumeParseException e, WebRequest request) {
        log.error("Resume parse error: {}", e.getMessage(), e);
        ErrorResponse response = ErrorResponse.builder()
            .errorCode(e.getErrorCode())
            .message(e.getMessage())
            .status(HttpStatus.BAD_REQUEST.value())
            .timestamp(Instant.now())
            .path(request.getDescription(false).replace("uri=", ""))
            .build();
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(MatchCalculationException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorResponse> handleMatchCalculation(
            MatchCalculationException e, WebRequest request) {
        log.error("Match calculation error: {}", e.getMessage(), e);
        ErrorResponse response = ErrorResponse.builder()
            .errorCode(e.getErrorCode())
            .message(e.getMessage())
            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .timestamp(Instant.now())
            .path(request.getDescription(false).replace("uri=", ""))
            .build();
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    @ExceptionHandler(AiServiceException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public ResponseEntity<ErrorResponse> handleAiError(
            AiServiceException e, WebRequest request) {
        log.error("AI service error: {}", e.getMessage(), e);
        ErrorResponse response = ErrorResponse.builder()
            .errorCode(e.getErrorCode())
            .message("AI service temporarily unavailable")
            .status(HttpStatus.SERVICE_UNAVAILABLE.value())
            .timestamp(Instant.now())
            .path(request.getDescription(false).replace("uri=", ""))
            .build();
        return new ResponseEntity<>(response, HttpStatus.SERVICE_UNAVAILABLE);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException e, WebRequest request) {
        log.warn("Validation error: {}", e.getMessage());
        
        Map<String, String> errors = e.getBindingResult()
            .getFieldErrors()
            .stream()
            .collect(Collectors.toMap(
                FieldError::getField,
                FieldError::getDefaultMessage,
                (a, b) -> a + ", " + b
            ));
        
        ErrorResponse response = ErrorResponse.builder()
            .errorCode("VALIDATION_FAILED")
            .message("Invalid request parameters")
            .status(HttpStatus.BAD_REQUEST.value())
            .timestamp(Instant.now())
            .path(request.getDescription(false).replace("uri=", ""))
            .details(errors)
            .build();
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<ErrorResponse> handleBadCredentials(
            BadCredentialsException e, WebRequest request) {
        log.warn("Authentication failed: {}", e.getMessage());
        ErrorResponse response = ErrorResponse.builder()
            .errorCode("INVALID_CREDENTIALS")
            .message("Invalid email or password")
            .status(HttpStatus.UNAUTHORIZED.value())
            .timestamp(Instant.now())
            .path(request.getDescription(false).replace("uri=", ""))
            .build();
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }
    
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(
            IllegalArgumentException e, WebRequest request) {
        log.warn("Illegal argument: {}", e.getMessage());
        ErrorResponse response = ErrorResponse.builder()
            .errorCode("INVALID_ARGUMENT")
            .message(e.getMessage())
            .status(HttpStatus.BAD_REQUEST.value())
            .timestamp(Instant.now())
            .path(request.getDescription(false).replace("uri=", ""))
            .build();
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorResponse> handleGeneric(
            Exception e, WebRequest request) {
        log.error("Unexpected error: {}", e.getMessage(), e);
        ErrorResponse response = ErrorResponse.builder()
            .errorCode("INTERNAL_ERROR")
            .message("An unexpected error occurred")
            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .timestamp(Instant.now())
            .path(request.getDescription(false).replace("uri=", ""))
            .build();
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
