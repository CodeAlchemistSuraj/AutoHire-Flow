package com.autohire.flow.domain.model;

import lombok.*;
import java.time.Instant;

/**
 * Application domain entity representing a job application by a user.
 * Tracks application status and workflow.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Application {
    
    private Long id;
    
    private Long userId;
    
    private Long jobId;
    
    private String status;  // PENDING, APPLIED, REJECTED, INTERVIEW, OFFER
    
    private Instant appliedAt;
    
    private String notes;
    
    private Instant createdAt;
    
    private Instant updatedAt;
    
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
