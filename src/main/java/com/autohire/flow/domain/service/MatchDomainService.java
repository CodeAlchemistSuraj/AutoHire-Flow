package com.autohire.flow.domain.service;

import com.autohire.flow.domain.model.JobPosting;
import com.autohire.flow.domain.model.MatchResult;
import com.autohire.flow.domain.model.Resume;
import com.autohire.flow.domain.valueobjects.MatchScore;

/**
 * Domain service for matching logic.
 * Encapsulates core business rules for evaluating matches between resumes and job postings.
 * Pure domain logic with no infrastructure or framework dependencies.
 */
public class MatchDomainService {
    
    private final MatchEngine matchEngine;
    
    /**
     * Constructor for MatchDomainService
     * @param matchEngine the engine that performs match calculations
     */
    public MatchDomainService(MatchEngine matchEngine) {
        this.matchEngine = matchEngine;
    }
    
    /**
     * Evaluate if a resume matches a job posting and create a match result.
     * @param resume the user's resume
     * @param job the job posting
     * @param userId the user ID
     * @return a MatchResult domain entity with calculated score
     */
    public MatchResult evaluateMatch(Resume resume, JobPosting job, Long userId) {
        validateMatch(resume, job, userId);
        
        MatchScore score = matchEngine.calculateMatchScore(resume, job);
        
        MatchResult result = new MatchResult();
        result.setUserId(userId);
        result.setJobId(job.getId());
        result.setScore(score.getScore());
        result.setStatus("PENDING");
        
        return result;
    }
    
    /**
     * Validate that a match can be evaluated.
     * @param resume the resume to validate
     * @param job the job posting to validate
     * @param userId the user ID to validate
     * @throws IllegalArgumentException if validation fails
     */
    private void validateMatch(Resume resume, JobPosting job, Long userId) {
        if (resume == null) {
            throw new IllegalArgumentException("Resume cannot be null");
        }
        if (job == null) {
            throw new IllegalArgumentException("Job posting cannot be null");
        }
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("Invalid user ID");
        }
        if (!resume.isValid()) {
            throw new IllegalArgumentException("Resume is not valid for matching");
        }
        if (!job.isValid()) {
            throw new IllegalArgumentException("Job posting is not valid for matching");
        }
        if (!job.isActive()) {
            throw new IllegalArgumentException("Job posting has expired");
        }
    }
    
    /**
     * Check if a match score qualifies for consideration.
     * @param score the match score to evaluate
     * @return true if score is >= 50, false otherwise
     */
    public boolean isViableMatch(double score) {
        return score >= 50.0;
    }
    
    /**
     * Get quality level description for a score.
     * @param score the score to evaluate
     * @return quality level description
     */
    public String getQualityLevel(double score) {
        if (score >= 85) return "EXCELLENT";
        if (score >= 70) return "GOOD";
        if (score >= 50) return "MODERATE";
        return "LOW";
    }
}
