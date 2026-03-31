package com.autohire.flow.application.port.incoming;

/**
 * Use case for calculating match score between resume and job.
 */
public interface CalculateMatchUseCase {
    
    /**
     * Calculates semantic matching score for user's resume against a job.
     * @param command match command with job and user details
     * @return MatchResult containing match score and details
     */
    MatchResult execute(MatchCommand command);
    
    record MatchCommand(
        Long userId,
        Long jobId
    ) {}
    
    record MatchResult(
        Long matchId,
        Double matchScore,  // 0-100
        String qualityLevel,  // EXCELLENT, GOOD, MODERATE, LOW
        java.util.List<String> matchingSkills,
        java.util.List<String> missingSkills,
        String explanation
    ) {}
}
