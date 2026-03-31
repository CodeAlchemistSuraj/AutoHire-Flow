package com.autohire.flow.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

/**
 * JPA Entity for MatchResult persistence.
 * Maps to 'match_results' table.
 */
@Entity
@Table(name = "match_results", indexes = {
    @Index(name = "idx_matches_user_id", columnList = "user_id"),
    @Index(name = "idx_matches_status", columnList = "status"),
    @Index(name = "idx_matches_score", columnList = "score"),
    @Index(name = "idx_matches_created_at", columnList = "created_at"),
    @Index(name = "idx_matches_user_job", columnList = "user_id,job_id")
},
uniqueConstraints = @UniqueConstraint(name = "uk_user_job", columnNames = {"user_id", "job_id"}))
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MatchResultEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "job_id", nullable = false)
    private Long jobId;
    
    @Column(precision = 5, scale = 2)
    private Double score;
    
    @Column(length = 20)
    private String status;
    
    @Column(columnDefinition = "TEXT")
    private String notes;
    
    @Column(name = "feedback_reason", length = 50)
    private String feedbackReason;
    
    @CreationTimestamp
    @Column(name = "matched_at", nullable = false, updatable = false)
    private Instant matchedAt;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;
}
