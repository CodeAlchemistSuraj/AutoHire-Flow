package com.autohire.flow.domain.model;

import lombok.*;
import java.time.Instant;

/**
 * JobPosting domain entity representing a job opportunity.
 * Contains job details and embeddings for semantic matching.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"embedding", "description"})
public class JobPosting {
    
    private Long id;
    
    private String externalId;
    
    private String title;
    
    private String company;
    
    private String description;
    
    private String location;
    
    private String salaryRange;
    
    private String employmentType;
    
    private float[] embedding;
    
    private String source;
    
    private Instant postedAt;
    
    private Instant expiresAt;
    
    private Instant createdAt;
    
    private Instant updatedAt;
    
    /**
     * Validates job posting has minimum required fields.
     * @return true if job is valid, false otherwise
     */
    public boolean isValid() {
        return this.title != null && !this.title.isBlank() &&
               this.company != null && !this.company.isBlank() &&
               this.description != null && !this.description.isBlank() &&
               this.embedding != null && this.embedding.length == 768;
    }
    
    /**
     * Checks if the job posting is still active (not expired).
     * @return true if job is active, false otherwise
     */
    public boolean isActive() {
        if (this.expiresAt == null) {
            return true;
        }
        return Instant.now().isBefore(this.expiresAt);
    }
    
    /**
     * Gets a summary of the job posting.
     * @return concatenated string of key job information
     */
    public String getSummary() {
        return String.format("%s at %s in %s", this.title, this.company, this.location);
    }
}
