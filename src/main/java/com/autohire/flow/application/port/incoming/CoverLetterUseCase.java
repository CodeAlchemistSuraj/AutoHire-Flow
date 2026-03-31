package com.autohire.flow.application.port.incoming;

/**
 * Use case for generating tailored cover letters
 */
public interface CoverLetterUseCase {
    
    CoverLetterResponse generate(Long userId, Long matchResultId, Long jobId, 
                                 String tone);
    
    record CoverLetterResponse(
        Long coverLetterId,
        String content,
        String tone,
        String status,
        String message
    ) {}
}
