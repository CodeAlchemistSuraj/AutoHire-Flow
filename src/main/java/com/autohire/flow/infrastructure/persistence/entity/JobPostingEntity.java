package com.autohire.flow.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

/**
 * JPA Entity for JobPosting persistence with pgvector support.
 * Maps to 'job_postings' table.
 */
@Entity
@Table(name = "job_postings", indexes = {
    @Index(name = "idx_jobs_title", columnList = "title"),
    @Index(name = "idx_jobs_company", columnList = "company"),
    @Index(name = "idx_jobs_location", columnList = "location"),
    @Index(name = "idx_jobs_created_at", columnList = "created_at"),
    @Index(name = "idx_jobs_embedding_hnsw", columnList = "embedding")
})
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"description", "embedding"})
public class JobPostingEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "external_id")
    private String externalId;
    
    @Column(nullable = false)
    private String title;
    
    @Column(name = "company", nullable = false)
    private String company;
    
    // Alias for company field for compatibility
    public String getCompanyName() {
        return this.company;
    }
    
    public void setCompanyName(String companyName) {
        this.company = companyName;
    }
    
    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;
    
    @Column
    private String location;
    
    @Column(name = "salary_range")
    private String salaryRange;
    
    @Column(name = "salary_min")
    private Double salaryMin;
    
    @Column(name = "salary_max")
    private Double salaryMax;
    
    @Column(name = "employment_type")
    private String employmentType;
    
    @Column(name = "required_skills")
    private String requiredSkills;  // Stored as JSON or comma-separated
    
    @Column(columnDefinition = "vector(768)")
    private String embedding;  // Stored as vector type in PostgreSQL
    
    @Column
    private String source;
    
    @Column(name = "posted_at")
    private Instant postedAt;
    
    @Column(name = "expires_at")
    private Instant expiresAt;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;
}