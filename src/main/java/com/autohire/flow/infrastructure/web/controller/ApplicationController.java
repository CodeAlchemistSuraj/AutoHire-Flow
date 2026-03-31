package com.autohire.flow.infrastructure.web.controller;

import com.autohire.flow.application.dto.request.TrackApplicationRequest;
import com.autohire.flow.application.dto.response.ApiResponse;
import com.autohire.flow.application.port.incoming.TrackApplicationUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

/**
 * Application Controller
 * 
 * Handles job application tracking.
 * Endpoints: POST /api/v1/applications/track, GET /api/v1/applications
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final TrackApplicationUseCase trackApplicationUseCase;

    /**
     * Track job application
     * 
     * @param request Application tracking request with jobId, status, notes
     * @param httpRequest HTTP request with user context
     * @return Success response with application tracking data
     */
    @PostMapping("/track")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<String>> trackApplication(
            @Valid @RequestBody TrackApplicationRequest request,
            HttpServletRequest httpRequest) {
        
        Long userId = (Long) httpRequest.getAttribute("userId");
        String email = (String) httpRequest.getAttribute("email");
        
        log.info("Application tracking initiated for user: {} and job: {} with status: {}", 
            userId, request.getJobId(), request.getStatus());
        
        try {
            // Create tracking command
            TrackApplicationUseCase.TrackingCommand command = 
                new TrackApplicationUseCase.TrackingCommand(
                    userId,
                    request.getJobId(),
                    request.getStatus(),
                    request.getNotes()
                );
            
            // Execute use case
            TrackApplicationUseCase.TrackingResult result = 
                trackApplicationUseCase.trackApplication(command);
            
            log.info("Application tracked successfully for user: {} and job: {} with ID: {}", 
                userId, request.getJobId(), result.applicationId());
            
            return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                    "Application tracked with ID: " + result.applicationId(),
                    "Application status updated successfully"
                ));
                
        } catch (Exception e) {
            log.error("Application tracking failed for user: {} and job: {}", 
                userId, request.getJobId(), e);
            throw e;
        }
    }

    /**
     * Get user's tracked applications
     * 
     * @param status Filter by application status (optional)
     * @param page Page number (default: 0)
     * @param size Page size (default: 20)
     * @param httpRequest HTTP request with user context
     * @return Paginated list of applications
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<String>> getApplications(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            HttpServletRequest httpRequest) {
        
        Long userId = (Long) httpRequest.getAttribute("userId");
        String email = (String) httpRequest.getAttribute("email");
        
        log.info("Fetching applications for user: {} with status filter: {} (page: {}, size: {})", 
            userId, status, page, size);
        
        try {
            // TODO: Implement get applications use case with pagination
            // For now, return simple response
            
            return ResponseEntity
                .ok()
                .body(ApiResponse.success(
                    "Applications retrieved successfully", 
                    "Retrieved applications for user"
                ));
                
        } catch (Exception e) {
            log.error("Failed to fetch applications for user: {}", userId, e);
            throw e;
        }
    }
}
