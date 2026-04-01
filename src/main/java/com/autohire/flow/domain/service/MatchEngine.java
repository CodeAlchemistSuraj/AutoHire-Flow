package com.autohire.flow.domain.service;

import com.autohire.flow.domain.model.JobPosting;
import com.autohire.flow.domain.model.Resume;
import com.autohire.flow.domain.valueobjects.MatchScore;

/**
 * Domain service for calculating match scores.
 * Orchestrates multiple matching strategies to produce a combined score.
 * This is a domain service - pure business logic with no Spring infrastructure dependencies.
 */
public class MatchEngine {
    
    private final MatchStrategy semanticStrategy;
    private final MatchStrategy keywordStrategy;
    
    /**
     * Constructor for MatchEngine
     * @param semanticStrategy strategy for semantic matching
     * @param keywordStrategy strategy for keyword-based matching
     */
    public MatchEngine(MatchStrategy semanticStrategy, MatchStrategy keywordStrategy) {
        this.semanticStrategy = semanticStrategy;
        this.keywordStrategy = keywordStrategy;
    }
    
    /**
     * Calculate combined match score using multiple strategies.
     * Default weights: Semantic 60%, Keyword 40%
     * @param resume the resume to evaluate
     * @param job the job posting to match against
     * @return combined match score
     */
    public MatchScore calculateMatchScore(Resume resume, JobPosting job) {
        return calculateMatchScore(resume, job, 0.6, 0.4);
    }
    
    /**
     * Calculate combined match score with custom weights.
     * @param resume the resume to evaluate
     * @param job the job posting to match against
     * @param semanticWeight weight for semantic score (0-1)
     * @param keywordWeight weight for keyword score (0-1)
     * @return combined match score
     */
    public MatchScore calculateMatchScore(Resume resume, JobPosting job, 
                                         double semanticWeight, double keywordWeight) {
        double semanticScore = semanticStrategy.calculateScore(resume, job);
        double keywordScore = keywordStrategy.calculateScore(resume, job);
        
        // Normalize weights
        double totalWeight = semanticWeight + keywordWeight;
        if (totalWeight == 0) {
            totalWeight = 1.0;
        }
        double normalizedSemanticWeight = semanticWeight / totalWeight;
        double normalizedKeywordWeight = keywordWeight / totalWeight;
        
        double combinedScore = (semanticScore * normalizedSemanticWeight) + 
                              (keywordScore * normalizedKeywordWeight);
        
        // Ensure score is within bounds [0, 100]
        double boundedScore = Math.min(100.0, Math.max(0.0, combinedScore));
        
        return MatchScore.builder()
            .score(boundedScore)
            .semanticScore((int) semanticScore)
            .keywordScore((int) keywordScore)
            .build();
    }
}
