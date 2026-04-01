package com.autohire.flow.domain.model;

import java.time.Instant;
import java.util.List;

/**
 * MatchResult domain entity representing a match between a resume and a job.
 * Tracks matching scores, statuses, and user feedback.
 */
public class MatchResult {
    
    private Long id;
    
    private Long userId;
    
    private Long jobId;
    
    private Double score;  // 0-100
    
    private String qualityLevel;  // EXCELLENT, GOOD, MODERATE, LOW
    
    private List<String> matchingSkills;
    
    private List<String> missingSkills;
    
    private String explanation;
    
    private String status;  // PENDING, APPLIED, REJECTED, INTERVIEW, CALCULATED
    
    private String notes;
    
    private String feedbackReason;
    
    private Instant matchedAt;
    
    private Instant updatedAt;
    
    private Instant createdAt;
    
    // Constructors
    public MatchResult() {
    }
    
    public MatchResult(Long id, Long userId, Long jobId, Double score, String qualityLevel,
                       List<String> matchingSkills, List<String> missingSkills, String explanation,
                       String status, String notes, String feedbackReason, Instant matchedAt,
                       Instant updatedAt, Instant createdAt) {
        this.id = id;
        this.userId = userId;
        this.jobId = jobId;
        this.score = score;
        this.qualityLevel = qualityLevel;
        this.matchingSkills = matchingSkills;
        this.missingSkills = missingSkills;
        this.explanation = explanation;
        this.status = status;
        this.notes = notes;
        this.feedbackReason = feedbackReason;
        this.matchedAt = matchedAt;
        this.updatedAt = updatedAt;
        this.createdAt = createdAt;
    }
    
    // Convenience constructor for basic match result
    public MatchResult(Long id, Long userId, Long jobId, Double score, String qualityLevel,
                       List<String> matchingSkills, List<String> missingSkills, String explanation,
                       String status, Instant matchedAt, Instant updatedAt, Instant createdAt) {
        this(id, userId, jobId, score, qualityLevel, matchingSkills, missingSkills, 
             explanation, status, null, null, matchedAt, updatedAt, createdAt);
    }
    
    // Getters
    public Long getId() {
        return id;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public Long getJobId() {
        return jobId;
    }
    
    public Double getScore() {
        return score;
    }
    
    public String getQualityLevel() {
        return qualityLevel;
    }
    
    public List<String> getMatchingSkills() {
        return matchingSkills;
    }
    
    public List<String> getMissingSkills() {
        return missingSkills;
    }
    
    public String getExplanation() {
        return explanation;
    }
    
    public String getStatus() {
        return status;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public String getFeedbackReason() {
        return feedbackReason;
    }
    
    public Instant getMatchedAt() {
        return matchedAt;
    }
    
    public Instant getUpdatedAt() {
        return updatedAt;
    }
    
    public Instant getCreatedAt() {
        return createdAt;
    }
    
    // Setters
    public void setId(Long id) {
        this.id = id;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }
    
    public void setScore(Double score) {
        this.score = score;
    }
    
    public void setQualityLevel(String qualityLevel) {
        this.qualityLevel = qualityLevel;
    }
    
    public void setMatchingSkills(List<String> matchingSkills) {
        this.matchingSkills = matchingSkills;
    }
    
    public void setMissingSkills(List<String> missingSkills) {
        this.missingSkills = missingSkills;
    }
    
    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public void setFeedbackReason(String feedbackReason) {
        this.feedbackReason = feedbackReason;
    }
    
    public void setMatchedAt(Instant matchedAt) {
        this.matchedAt = matchedAt;
    }
    
    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
    
    /**
     * Validates match result has required fields.
     * @return true if valid, false otherwise
     */
    public boolean isValid() {
        return this.userId != null &&
               this.jobId != null &&
               this.score != null &&
               this.score >= 0 && this.score <= 100 &&
               this.status != null && !this.status.isBlank();
    }
    
    /**
     * Marks the match as applied.
     */
    public void markAsApplied() {
        this.status = "APPLIED";
        this.updatedAt = Instant.now();
    }
    
    /**
     * Rejects the match with a reason.
     * @param reason the rejection reason
     */
    public void reject(String reason) {
        this.status = "REJECTED";
        this.feedbackReason = reason;
        this.updatedAt = Instant.now();
    }
    
    /**
     * Marks the match as interview scheduled.
     */
    public void markAsInterview() {
        this.status = "INTERVIEW";
        this.updatedAt = Instant.now();
    }
    
    /**
     * Adds notes to the match result.
     * @param notes additional notes
     */
    public void addNotes(String notes) {
        this.notes = notes;
        this.updatedAt = Instant.now();
    }
    
    /**
     * Checks if the match score is good (>= 70%).
     * @return true if score is >= 70, false otherwise
     */
    public boolean isGoodMatch() {
        return this.score != null && this.score >= 70.0;
    }
}