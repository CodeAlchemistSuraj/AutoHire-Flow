package com.autohire.flow.domain.model;

import lombok.*;
import java.time.Instant;

/**
 * MatchResult domain entity representing a match between a resume and a job.
 * Tracks matching scores, statuses, and user feedback.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MatchResult {
    
    private Long id;
    
    private Long userId;
    
    private Long jobId;
    
    private Double score;  // 0-100
    
    private String status;  // PENDING, APPLIED, REJECTED, INTERVIEW
    
    private String notes;
    
    private String feedbackReason;
    
    private Instant matchedAt;
    
    private Instant updatedAt;
    
    private Instant createdAt;
    
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
    
    /**
     * Returns match quality description.
     * @return quality level description
     */
    public String getQualityLevel() {
        if (this.score == null) {
            return "UNKNOWN";
        }
        if (this.score >= 85) return "EXCELLENT";
        if (this.score >= 70) return "GOOD";
        if (this.score >= 50) return "MODERATE";
        return "LOW";
    }
}
