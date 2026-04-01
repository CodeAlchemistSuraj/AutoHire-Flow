package com.autohire.flow.domain.service;

/**
 * Domain service for scoring calculations.
 * Encapsulates business rules for calculating and manipulating match scores.
 * Pure domain logic with no infrastructure or framework dependencies.
 */
public class ScoringDomainService {
    
    /**
     * Apply a boost to a score based on specific criteria.
     * @param baseScore the original score (0-100)
     * @param boostPercentage the boost to apply (as a percentage, e.g., 10 for 10%)
     * @return the boosted score (capped at 100)
     */
    public double applyBoost(double baseScore, double boostPercentage) {
        double boost = (baseScore * boostPercentage) / 100.0;
        double boosted = baseScore + boost;
        return Math.min(100.0, boosted);
    }
    
    /**
     * Apply a penalty to a score based on specific criteria.
     * @param baseScore the original score (0-100)
     * @param penaltyPercentage the penalty to apply (as a percentage, e.g., 10 for 10%)
     * @return the penalized score (floored at 0)
     */
    public double applyPenalty(double baseScore, double penaltyPercentage) {
        double penalty = (baseScore * penaltyPercentage) / 100.0;
        double penalized = baseScore - penalty;
        return Math.max(0.0, penalized);
    }
    
    /**
     * Calculate the average of multiple scores.
     * @param scores array of scores to average
     * @return the average score
     */
    public double calculateAverage(double... scores) {
        if (scores.length == 0) {
            return 0.0;
        }
        double sum = 0;
        for (double score : scores) {
            sum += score;
        }
        return sum / scores.length;
    }
    
    /**
     * Calculate a weighted average of multiple scores.
     * @param scores the scores to average
     * @param weights the weights for each score (should sum to 1.0 or will be normalized)
     * @return the weighted average score
     */
    public double calculateWeightedAverage(double[] scores, double[] weights) {
        if (scores.length == 0 || scores.length != weights.length) {
            throw new IllegalArgumentException("Scores and weights arrays must have the same length");
        }
        
        double totalWeight = 0;
        for (double weight : weights) {
            totalWeight += weight;
        }
        
        if (totalWeight == 0) {
            throw new IllegalArgumentException("Total weight cannot be zero");
        }
        
        double weightedSum = 0;
        for (int i = 0; i < scores.length; i++) {
            weightedSum += scores[i] * (weights[i] / totalWeight);
        }
        
        return weightedSum;
    }
    
    /**
     * Normalize a score to a percentage (0-100 becomes 0-1).
     * @param score the score in range [0, 100]
     * @return normalized score in range [0, 1]
     */
    public double normalizeScore(double score) {
        return Math.min(1.0, Math.max(0.0, score / 100.0));
    }
    
    /**
     * Denormalize a score from percentage (0-1 becomes 0-100).
     * @param normalizedScore the score in range [0, 1]
     * @return denormalized score in range [0, 100]
     */
    public double denormalizeScore(double normalizedScore) {
        return Math.min(100.0, Math.max(0.0, normalizedScore * 100.0));
    }
    
    /**
     * Apply a threshold to a score - returns 0 if below threshold, otherwise returns the score.
     * @param score the score to evaluate
     * @param threshold the minimum threshold
     * @return 0 if score < threshold, otherwise the score
     */
    public double applyThreshold(double score, double threshold) {
        return score < threshold ? 0.0 : score;
    }
    
    /**
     * Check if two scores are approximately equal within a tolerance.
     * @param score1 first score
     * @param score2 second score
     * @param tolerance acceptable difference (default is 1.0)
     * @return true if scores are within tolerance of each other
     */
    public boolean areScoresEqual(double score1, double score2, double tolerance) {
        return Math.abs(score1 - score2) <= tolerance;
    }
    
    /**
     * Check if two scores are approximately equal with a 1.0 tolerance.
     * @param score1 first score
     * @param score2 second score
     * @return true if scores are within 1.0 of each other
     */
    public boolean areScoresEqual(double score1, double score2) {
        return areScoresEqual(score1, score2, 1.0);
    }
}
