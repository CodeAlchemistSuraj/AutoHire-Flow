package com.autohire.flow.domain.model;

import java.time.Instant;

/**
 * Application domain entity representing a job application by a user.
 * Tracks application status and workflow.
 */
public class Application {
    
    private Long id;
    
    private Long userId;
    
    private Long jobId;
    
    private String status;  // PENDING, APPLIED, REJECTED, INTERVIEW, OFFER
    
    private Instant appliedAt;
    
    private String notes;
    
    private Instant createdAt;
    
    private Instant updatedAt;
    
    // Constructors
    public Application() {
    }
    
    public Application(Long id, Long userId, Long jobId, String status, Instant appliedAt, 
                       String notes, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.userId = userId;
        this.jobId = jobId;
        this.status = status;
        this.appliedAt = appliedAt;
        this.notes = notes;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
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
    
    public String getStatus() {
        return status;
    }
    
    public Instant getAppliedAt() {
        return appliedAt;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public Instant getCreatedAt() {
        return createdAt;
    }
    
    public Instant getUpdatedAt() {
        return updatedAt;
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
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public void setAppliedAt(Instant appliedAt) {
        this.appliedAt = appliedAt;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
    
    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    /**
     * Validates application has required fields.
     * @return true if valid, false otherwise
     */
    public boolean isValid() {
        return this.userId != null &&
               this.jobId != null &&
               this.status != null && !this.status.isBlank();
    }
    
    /**
     * Marks the application as applied.
     */
    public void markAsApplied() {
        this.status = "APPLIED";
        this.appliedAt = Instant.now();
        this.updatedAt = Instant.now();
    }
    
    /**
     * Moves application to interview stage.
     */
    public void moveToInterview() {
        this.status = "INTERVIEW";
        this.updatedAt = Instant.now();
    }
    
    /**
     * Marks application as having received an offer.
     */
    public void moveToOffer() {
        this.status = "OFFER";
        this.updatedAt = Instant.now();
    }
    
    /**
     * Rejects the application.
     */
    public void reject() {
        this.status = "REJECTED";
        this.updatedAt = Instant.now();
    }
    
    /**
     * Adds notes to the application.
     * @param notes application notes
     */
    public void addNotes(String notes) {
        this.notes = notes;
        this.updatedAt = Instant.now();
    }
    
    /**
     * Gets the number of days since application was submitted.
     * @return days elapsed since applying
     */
    public long getDaysSinceApplied() {
        if (this.appliedAt == null) {
            return 0;
        }
        return java.time.temporal.ChronoUnit.DAYS.between(
            this.appliedAt,
            Instant.now()
        );
    }
}
