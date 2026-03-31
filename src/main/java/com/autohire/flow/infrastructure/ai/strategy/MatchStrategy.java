package com.autohire.flow.infrastructure.ai.strategy;

import com.autohire.flow.domain.model.JobPosting;
import com.autohire.flow.domain.model.Resume;

/**
 * Strategy interface for different matching algorithms
 */
public interface MatchStrategy {
    
    /**
     * Calculate match score between resume and job posting
     * @return Score between 0 and 100
     */
    double calculateScore(Resume resume, JobPosting job);
    
    /**
     * Get strategy name
     */
    String getName();
}
