package com.autohire.flow.application.port.incoming;

import java.util.List;

/**
 * Use case for calculating match scores between resume and jobs
 */
public interface MatchCalculationUseCase {
    
    MatchScoreResponse calculateForSingleJob(Long userId, String jobTitle, 
                                            String jobDescription, String company);
    
    List<MatchScoreResponse> searchAndScoreJobs(Long userId, String searchQuery, 
                                               String location, Integer limit);
    
    record MatchScoreResponse(
        Long jobId,
        String title,
        String company,
        String location,
        Double matchScore,
        List<String> matchingSkills,
        List<String> missingSkills,
        String status
    ) {}
}
