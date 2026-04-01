package com.autohire.flow.infrastructure.web.controller;

import com.autohire.flow.application.dto.request.FeedbackRequest;
import com.autohire.flow.application.dto.response.ApiResponse;
import com.autohire.flow.application.port.incoming.SubmitFeedbackUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

/**
 * Feedback Controller
 * 
 * Handles user feedback on match results.
 * Endpoints: POST /api/v1/feedback/submit, GET /api/v1/feedback
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/feedback")
@RequiredArgsConstructor
public class FeedbackController {

    private final SubmitFeedbackUseCase submitFeedbackUseCase;

    /**
     * Submit feedback on match result
     * 
     * @param request Feedback request with matchResultId, feedbackType, comments
     * @param httpRequest HTTP request with user context
     * @return Success response with feedback tracking
     */
    @PostMapping("/submit")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<String>> submitFeedback(
            @Valid @RequestBody FeedbackRequest request,
            HttpServletRequest httpRequest) {
        
        Long userId = (Long) httpRequest.getAttribute("userId");
        String email = (String) httpRequest.getAttribute("email");
        
        log.info("Feedback submission initiated for user: {} on match result: {} with type: {}", 
            userId, request.getMatchResultId(), request.getFeedbackType());
        
        try {
            // Create feedback command
            SubmitFeedbackUseCase.FeedbackCommand command = 
                new SubmitFeedbackUseCase.FeedbackCommand(
                    userId,
                    request.getMatchResultId(),
                    request.getFeedbackType(),
                    request.getComments()
                );
            
            // Execute use case
           SubmitFeedbackUseCase.FeedbackResult result = submitFeedbackUseCase.execute(command);
            
            log.info("Feedback submitted successfully for user: {} on match result: {} with ID: {}", 
                userId, request.getMatchResultId(), result.feedbackId());
            
            return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                    "Feedback submitted with ID: " + result.feedbackId(),
                    "Feedback recorded successfully"
                ));
                
        } catch (Exception e) {
            log.error("Feedback submission failed for user: {} on match result: {}", 
                userId, request.getMatchResultId(), e);
            throw e;
        }
    }

    /**
     * Get user's feedback history
     * 
     * @param feedbackType Filter by feedback type (optional)
     * @param page Page number (default: 0)
     * @param size Page size (default: 20)
     * @param httpRequest HTTP request with user context
     * @return Paginated list of feedback
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<String>> getFeedbackHistory(
            @RequestParam(required = false) String feedbackType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            HttpServletRequest httpRequest) {
        
        Long userId = (Long) httpRequest.getAttribute("userId");
        String email = (String) httpRequest.getAttribute("email");
        
        log.info("Fetching feedback for user: {} with type filter: {} (page: {}, size: {})", 
            userId, feedbackType, page, size);
        
        try {
            // TODO: Implement get feedback use case with pagination
            // For now, return simple response
            
            return ResponseEntity
                .ok()
                .body(ApiResponse.success(
                    "Feedback retrieved successfully", 
                    "Retrieved feedback history for user"
                ));
                
        } catch (Exception e) {
            log.error("Failed to fetch feedback for user: {}", userId, e);
            throw e;
        }
    }
}
