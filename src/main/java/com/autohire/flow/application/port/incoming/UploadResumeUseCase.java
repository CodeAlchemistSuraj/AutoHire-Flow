package com.autohire.flow.application.port.incoming;

import org.springframework.web.multipart.MultipartFile;
import java.util.List;

/**
 * Use case for uploading and parsing resumes.
 */
public interface UploadResumeUseCase {
    
    /**
     * Uploads a resume file and extracts relevant information.
     * @param command upload command with file and user ID
     * @return UploadResult containing extracted skills and parsing status
     */
    UploadResult execute(UploadCommand command);
    
    record UploadCommand(Long userId, MultipartFile file) {}
    
    record UploadResult(
        Long resumeId,
        Integer parsedSkillsCount,
        String status,  // SUCCESS, PARTIAL, FAILED
        List<String> extractedSkills
    ) {}
}
