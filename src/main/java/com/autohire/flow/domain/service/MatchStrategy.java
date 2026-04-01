package com.autohire.flow.domain.service;

import com.autohire.flow.domain.model.JobPosting;
import com.autohire.flow.domain.model.Resume;

/**
 * Strategy interface for different matching algorithms.
 * Domain-level abstraction for matching strategies.
 * Implementations should be provided by the infrastructure layer.
 */
public interface MatchStrategy {
    
    /**
     * Calculate match score between resume and job posting
     * @param resume the resume to evaluate
     * @param job the job posting to match against
     * @return Score between 0 and 100
     */
    double calculateScore(Resume resume, JobPosting job);
    
    /**
     * Get the name/type of this strategy
     * @return strategy name
     */
    String getName();
}
