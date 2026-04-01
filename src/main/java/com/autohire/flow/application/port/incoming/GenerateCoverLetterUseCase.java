package com.autohire.flow.application.port.incoming;

import java.time.Instant;

/**
 * Use case for generating cover letters.
 */
public interface GenerateCoverLetterUseCase {
    
    /**
     * Generates a tailored cover letter for a job posting.
     * @param command cover letter generation command
     * @return GenerationResult containing generated cover letter and metadata
     */
    GenerationResult execute(GenerationCommand command);
    
    record GenerationCommand(
        Long userId,
        Long jobId,
        String tone  // PROFESSIONAL, ENTHUSIASTIC, CONCISE
    ) {}
    
    record GenerationResult(
        Long coverLetterId,
        String content,
        Integer wordCount,
        Integer paragraphCount,
        Instant generatedAt
    ) {
        public boolean meetsMinimumRequirements() {
            return wordCount != null && wordCount >= 200 && paragraphCount != null && paragraphCount >= 3;
        }
    }
}