package com.autohire.flow.infrastructure.web.controller;

import com.autohire.flow.application.dto.request.MatchRequest;
import com.autohire.flow.application.dto.response.ApiResponse;
import com.autohire.flow.application.dto.response.MatchScoreResponse;
import com.autohire.flow.application.port.incoming.CalculateMatchUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

/**
 * Matcher Controller
 * 
 * Handles resume-to-job matching operations.
 * Endpoints: POST /api/v1/matcher/score, GET /api/v1/matches
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/matcher")
@RequiredArgsConstructor
public class MatcherController {

    private final CalculateMatchUseCase calculateMatchUseCase;

    /**
     * Calculate match score between resume and job
     * 
     * @param request Match calculation request with jobId
     * @param httpRequest HTTP request with user context
     * @return MatchScoreResponse with score, qualityLevel, matching/missing skills
     */
    @PostMapping("/score")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<MatchScoreResponse>> calculateScore(
            @Valid @RequestBody MatchRequest request,
            HttpServletRequest httpRequest) {
        
        Long userId = (Long) httpRequest.getAttribute("userId");
        String email = (String) httpRequest.getAttribute("email");
        
        log.info("Match calculation initiated for user: {} and job: {}", userId, request.getJobId());
        
        try {
            // Create match command
            CalculateMatchUseCase.MatchCommand command = new CalculateMatchUseCase.MatchCommand(
                userId,
                request.getJobId()
            );
            
            // Execute use case
            CalculateMatchUseCase.MatchResult result = calculateMatchUseCase.calculateMatch(command);
            
            // Build response
            MatchScoreResponse response = new MatchScoreResponse(
                result.score(),
                result.qualityLevel(),
                result.matchingSkills(),
                result.missingSkills(),
                result.explanation()
            );
            
            log.info("Match calculated successfully for user: {} and job: {} with score: {}", 
                userId, request.getJobId(), result.score());
            
            return ResponseEntity
                .ok()
                .body(ApiResponse.success(response, "Match score calculated successfully"));
                
        } catch (Exception e) {
            log.error("Match calculation failed for user: {} and job: {}", userId, request.getJobId(), e);
            throw e;
        }
    }

    /**
     * Get user's matches with pagination
     * 
     * @param status Filter by match status (optional)
     * @param page Page number (default: 0)
     * @param size Page size (default: 20)
     * @param httpRequest HTTP request with user context
     * @return Paginated list of matches
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<String>> getMatches(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            HttpServletRequest httpRequest) {
        
        Long userId = (Long) httpRequest.getAttribute("userId");
        String email = (String) httpRequest.getAttribute("email");
        
        log.info("Fetching matches for user: {} with status filter: {} (page: {}, size: {})", 
            userId, status, page, size);
        
        try {
            // TODO: Implement get matches use case with pagination
            // For now, return simple response
            
            return ResponseEntity
                .ok()
                .body(ApiResponse.success(
                    "Matches retrieved successfully", 
                    "Retrieved matches for user"
                ));
                
        } catch (Exception e) {
            log.error("Failed to fetch matches for user: {}", userId, e);
            throw e;
        }
    }
}
