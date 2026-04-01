package com.autohire.flow.infrastructure.ai.strategy;

import com.autohire.flow.domain.model.JobPosting;
import com.autohire.flow.domain.model.Resume;
import com.autohire.flow.domain.service.MatchStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Semantic matching strategy using vector embeddings.
 * Implementation of the MatchStrategy domain interface.
 */
@Component
@Slf4j
public class SemanticMatchStrategy implements MatchStrategy {
    
    private static final double SIMILARITY_THRESHOLD = 0.5;
    
    @Override
    public double calculateScore(Resume resume, JobPosting job) {
        if (resume.getEmbedding() == null || job.getEmbedding() == null) {
            log.warn("Missing embeddings for semantic matching");
            return 0.0;
        }
        
        double cosineSimilarity = calculateCosineSimilarity(resume.getEmbedding(), job.getEmbedding());
        
        // Convert cosine similarity [-1, 1] to score [0, 100]
        double score = (cosineSimilarity + 1) / 2 * 100;
        
        log.debug("Semantic match score: {}", score);
        return Math.min(100.0, Math.max(0.0, score));
    }
    
    /**
     * Calculate cosine similarity between two vectors
     */
    private double calculateCosineSimilarity(float[] vector1, float[] vector2) {
        if (vector1.length != vector2.length) {
            log.warn("Vector dimensions do not match: {} vs {}", vector1.length, vector2.length);
            return 0.0;
        }
        
        double dotProduct = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;
        
        for (int i = 0; i < vector1.length; i++) {
            dotProduct += vector1[i] * vector2[i];
            norm1 += vector1[i] * vector1[i];
            norm2 += vector2[i] * vector2[i];
        }
        
        double denominator = Math.sqrt(norm1) * Math.sqrt(norm2);
        
        if (denominator == 0.0) {
            return 0.0;
        }
        
        return dotProduct / denominator;
    }
    
    @Override
    public String getName() {
        return "SEMANTIC";
    }
}
