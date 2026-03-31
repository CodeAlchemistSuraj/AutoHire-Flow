package com.autohire.flow.infrastructure.ai.strategy;

import com.autohire.flow.domain.model.JobPosting;
import com.autohire.flow.domain.model.Resume;
import com.autohire.flow.domain.valueobjects.MatchScore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Match engine that combines multiple matching strategies
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class MatchEngine {
    
    private final SemanticMatchStrategy semanticStrategy;
    private final KeywordMatchStrategy keywordStrategy;
    
    /**
     * Calculate combined match score using multiple strategies
     * Weights: Semantic 60%, Keyword 40%
     */
    public MatchScore calculateMatchScore(Resume resume, JobPosting job) {
        double semanticScore = semanticStrategy.calculateScore(resume, job);
        double keywordScore = keywordStrategy.calculateScore(resume, job);
        
        // Combined score with weighted average
        double combinedScore = (semanticScore * 0.6) + (keywordScore * 0.4);
        
        log.debug("Match calculation - Semantic: {}, Keyword: {}, Combined: {}", 
                  semanticScore, keywordScore, combinedScore);
        
        return MatchScore.builder()
            .score(Math.min(100.0, Math.max(0.0, combinedScore)))
            .semanticScore((int) semanticScore)
            .keywordScore((int) keywordScore)
            .build();
    }
    
    /**
     * Calculate combined match score with custom weights
     */
    public MatchScore calculateMatchScore(Resume resume, JobPosting job, 
                                         double semanticWeight, double keywordWeight) {
        double semanticScore = semanticStrategy.calculateScore(resume, job);
        double keywordScore = keywordStrategy.calculateScore(resume, job);
        
        // Normalize weights
        double totalWeight = semanticWeight + keywordWeight;
        double normalizedSemanticWeight = semanticWeight / totalWeight;
        double normalizedKeywordWeight = keywordWeight / totalWeight;
        
        double combinedScore = (semanticScore * normalizedSemanticWeight) + 
                              (keywordScore * normalizedKeywordWeight);
        
        return MatchScore.builder()
            .score(Math.min(100.0, Math.max(0.0, combinedScore)))
            .semanticScore((int) semanticScore)
            .keywordScore((int) keywordScore)
            .build();
    }
}
