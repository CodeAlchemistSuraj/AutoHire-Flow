package com.autohire.flow.infrastructure.web.controller;

import com.autohire.flow.application.dto.request.CoverLetterRequest;
import com.autohire.flow.application.dto.response.ApiResponse;
import com.autohire.flow.application.dto.response.CoverLetterResponse;
import com.autohire.flow.application.port.incoming.GenerateCoverLetterUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

/**
 * Cover Letter Controller
 * 
 * Handles AI-powered cover letter generation.
 * Endpoints: POST /api/v1/cover-letter/generate
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/cover-letter")
@RequiredArgsConstructor
public class CoverLetterController {

    private final GenerateCoverLetterUseCase generateCoverLetterUseCase;

    /**
     * Generate cover letter for job application
     * 
     * @param request Cover letter generation request with jobId and tone
     * @param httpRequest HTTP request with user context
     * @return CoverLetterResponse with generated content, word count, paragraph count
     */
    @PostMapping("/generate")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<CoverLetterResponse>> generateCoverLetter(
            @Valid @RequestBody CoverLetterRequest request,
            HttpServletRequest httpRequest) {
        
        Long userId = (Long) httpRequest.getAttribute("userId");
        String email = (String) httpRequest.getAttribute("email");
        
        log.info("Cover letter generation initiated for user: {} and job: {} with tone: {}", 
            userId, request.getJobId(), request.getTone());
        
        try {
            // Create generation command
            GenerateCoverLetterUseCase.GenerationCommand command = 
                new GenerateCoverLetterUseCase.GenerationCommand(
                    userId,
                    request.getJobId(),
                    request.getTone()
                );
            
            // Execute use case - FIXED: Use execute() instead of generateCoverLetter()
            GenerateCoverLetterUseCase.GenerationResult result = 
                generateCoverLetterUseCase.execute(command);
            
            // Build response
    CoverLetterResponse response = new CoverLetterResponse(
        result.coverLetterId(),
        result.content(),
        result.wordCount(),
        result.paragraphCount(),
        null, // tone - not in result, use default
        result.generatedAt(),
        result.meetsMinimumRequirements()
    );
            
            log.info("Cover letter generated successfully for user: {} and job: {}", 
                userId, request.getJobId());
            
            return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Cover letter generated successfully", response));
                
        } catch (Exception e) {
            log.error("Cover letter generation failed for user: {} and job: {}", 
                userId, request.getJobId(), e);
            throw e;
        }
    }

    /**
     * Get generated cover letters for user
     * 
     * @param jobId Filter by jobId (optional)
     * @param page Page number (default: 0)
     * @param size Page size (default: 20)
     * @param httpRequest HTTP request with user context
     * @return Paginated list of cover letters
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<String>> getCoverLetters(
            @RequestParam(required = false) Long jobId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            HttpServletRequest httpRequest) {
        
        Long userId = (Long) httpRequest.getAttribute("userId");
        String email = (String) httpRequest.getAttribute("email");
        
        log.info("Fetching cover letters for user: {} with jobId filter: {} (page: {}, size: {})", 
            userId, jobId, page, size);
        
        try {
            // TODO: Implement get cover letters use case with pagination
            // For now, return simple response
            
            return ResponseEntity
                .ok()
                .body(ApiResponse.success(
                    "Cover letters retrieved successfully", 
                    "Retrieved cover letters for user"
                ));
                
        } catch (Exception e) {
            log.error("Failed to fetch cover letters for user: {}", userId, e);
            throw e;
        }
    }
}