package com.autohire.flow.domain.model;

import java.time.Instant;

/**
 * Feedback domain entity representing user feedback on a match.
 * Used to improve matching algorithms and track user preferences.
 */
public class Feedback {
    
    private Long id;
    
    private Long userId;
    
    private Long matchResultId;
    
    private String feedbackType;  // TOO_SENIOR, NOT_INTERESTED, SALARY_LOW, OTHER
    
    private String comments;
    
    private Instant submittedAt;
    
    private Instant createdAt;
    
    // Constructors
    public Feedback() {
    }
    
    public Feedback(Long id, Long userId, Long matchResultId, String feedbackType, 
                    String comments, Instant submittedAt, Instant createdAt) {
        this.id = id;
        this.userId = userId;
        this.matchResultId = matchResultId;
        this.feedbackType = feedbackType;
        this.comments = comments;
        this.submittedAt = submittedAt;
        this.createdAt = createdAt;
    }
    
    // Getters
    public Long getId() {
        return id;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public Long getMatchResultId() {
        return matchResultId;
    }
    
    public String getFeedbackType() {
        return feedbackType;
    }
    
    public String getComments() {
        return comments;
    }
    
    public Instant getSubmittedAt() {
        return submittedAt;
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
    
    public void setMatchResultId(Long matchResultId) {
        this.matchResultId = matchResultId;
    }
    
    public void setFeedbackType(String feedbackType) {
        this.feedbackType = feedbackType;
    }
    
    public void setComments(String comments) {
        this.comments = comments;
    }
    
    public void setSubmittedAt(Instant submittedAt) {
        this.submittedAt = submittedAt;
    }
    
    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
    
    /**
     * Validates feedback has required fields.
     * @return true if valid, false otherwise
     */
    public boolean isValid() {
        return this.userId != null &&
               this.matchResultId != null &&
               this.feedbackType != null && !this.feedbackType.isBlank();
    }
    
    /**
     * Checks if feedback includes additional comments.
     * @return true if comments are provided, false otherwise
     */
    public boolean hasComments() {
        return this.comments != null && !this.comments.isBlank();
    }
    
    /**
     * Gets a human-readable feedback description.
     * @return feedback description
     */
    public String getDescription() {
        return switch (this.feedbackType) {
            case "TOO_SENIOR" -> "This position is too senior for my current level";
            case "NOT_INTERESTED" -> "I'm not interested in this opportunity";
            case "SALARY_LOW" -> "The salary range is too low";
            case "OTHER" -> "Other reason: " + (this.comments != null ? this.comments : "");
            default -> "Unknown feedback type";
        };
    }
}
