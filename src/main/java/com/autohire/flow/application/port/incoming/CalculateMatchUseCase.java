package com.autohire.flow.application.port.incoming;

import java.time.Instant;
import java.util.List;

/**
 * Use case for calculating match scores between resumes and job postings.
 * Orchestrates the matching process using multiple strategies.
 */
public interface CalculateMatchUseCase {
    
    /**
     * Calculates the match between a resume and a job posting.
     * @param command calculate match command
     * @return MatchResult enriched with score, quality level, and explanation
     */
    MatchResult execute(CalculateMatchCommand command);
    
    record CalculateMatchCommand(Long userId, Long jobId) {}
    
    record MatchResult(
        Long matchId,
        Long userId,
        Long jobId,
        Integer score,
        String qualityLevel,  // EXCELLENT, GOOD, FAIR, POOR
        List<String> matchingSkills,
        List<String> missingSkills,
        String explanation,
        String status,
        Instant createdAt,
        Instant updatedAt
    ) {}
}
