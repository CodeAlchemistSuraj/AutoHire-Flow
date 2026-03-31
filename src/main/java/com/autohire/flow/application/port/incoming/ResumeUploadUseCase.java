package com.autohire.flow.application.port.incoming;

import org.springframework.web.multipart.MultipartFile;

/**
 * Use case for uploading and parsing resumes
 */
public interface ResumeUploadUseCase {
    
    ResumeUploadResponse upload(Long userId, MultipartFile file);
    
    record ResumeUploadResponse(
        Long resumeId,
        String status,
        Integer parsedSkillsCount,
        String message
    ) {}
}
