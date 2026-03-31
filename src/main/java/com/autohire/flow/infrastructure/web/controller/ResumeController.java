package com.autohire.flow.infrastructure.web.controller;

import com.autohire.flow.application.dto.response.ApiResponse;
import com.autohire.flow.application.dto.response.ResumeUploadResponse;
import com.autohire.flow.application.port.incoming.UploadResumeUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Resume Controller
 * 
 * Handles resume upload operations.
 * Endpoints: POST /api/v1/resume/upload, GET /api/v1/resume
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/resume")
@RequiredArgsConstructor
public class ResumeController {

    private final UploadResumeUseCase uploadResumeUseCase;

    /**
     * Upload user resume
     * 
     * @param file Multipart file (PDF or DOCX)
     * @param request HTTP request with user context
     * @return ResumeUploadResponse with resumeId, skillsCount, status
     */
    @PostMapping("/upload")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<ResumeUploadResponse>> uploadResume(
            @RequestParam("file") MultipartFile file,
            HttpServletRequest request) {
        
        Long userId = (Long) request.getAttribute("userId");
        String email = (String) request.getAttribute("email");
        
        log.info("Resume upload initiated for user: {} ({})", userId, email);
        
        try {
            // Create upload command
            UploadResumeUseCase.UploadCommand command = new UploadResumeUseCase.UploadCommand(
                userId,
                file.getOriginalFilename(),
                file.getBytes(),
                file.getContentType()
            );
            
            // Execute use case
            UploadResumeUseCase.UploadResult result = uploadResumeUseCase.uploadResume(command);
            
            // Build response
            ResumeUploadResponse response = new ResumeUploadResponse(
                result.resumeId(),
                result.skillsCount(),
                "UPLOADED",
                result.extractedSkills()
            );
            
            log.info("Resume uploaded successfully for user: {} with ID: {}", userId, result.resumeId());
            
            return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Resume uploaded successfully"));
                
        } catch (Exception e) {
            log.error("Resume upload failed for user: {}", userId, e);
            throw new RuntimeException("Failed to upload resume", e);
        }
    }

    /**
     * Get user's resume
     * 
     * @param request HTTP request with user context
     * @return ResumeUploadResponse with resume details
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<ResumeUploadResponse>> getResume(
            HttpServletRequest request) {
        
        Long userId = (Long) request.getAttribute("userId");
        String email = (String) request.getAttribute("email");
        
        log.info("Fetching resume for user: {} ({})", userId, email);
        
        try {
            // TODO: Implement fetch resume use case
            // For now, return sample response
            ResumeUploadResponse response = new ResumeUploadResponse(
                1L,
                5,
                "UPLOADED",
                java.util.List.of("Java", "Spring Boot", "PostgreSQL")
            );
            
            return ResponseEntity
                .ok()
                .body(ApiResponse.success(response, "Resume retrieved successfully"));
                
        } catch (Exception e) {
            log.error("Failed to fetch resume for user: {}", userId, e);
            throw e;
        }
    }
}
