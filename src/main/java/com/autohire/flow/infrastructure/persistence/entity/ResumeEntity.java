package com.autohire.flow.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.postgresql.util.PGobject;

import java.time.Instant;

/**
 * JPA Entity for Resume persistence with pgvector support.
 * Maps to 'resumes' table with vector embeddings.
 */
@Entity
@Table(name = "resumes", indexes = {
    @Index(name = "idx_resumes_user_id", columnList = "user_id"),
    @Index(name = "idx_resumes_created_at", columnList = "created_at"),
    @Index(name = "idx_resumes_embedding_hnsw", columnList = "embedding")
})
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"embedding", "parsedText"})
public class ResumeEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", unique = true, nullable = false)
    private Long userId;
    
    @Column(name = "original_filename", nullable = false)
    private String originalFilename;
    
    @Column(name = "s3_key", nullable = false)
    private String s3Key;
    
    @Column(columnDefinition = "TEXT")
    private String parsedText;
    
    @Column(columnDefinition = "JSONB")
    private String skills;
    
    @Column(columnDefinition = "JSONB")
    private String experiences;
    
    @Column(columnDefinition = "JSONB")
    private String educations;
    
    @Column(columnDefinition = "JSONB")
    private String projects;
    
    @Column(columnDefinition = "vector(768)")
    private String embedding;  // Stored as vector type in PostgreSQL
    
    @CreationTimestamp
    @Column(name = "uploaded_at", nullable = false, updatable = false)
    private Instant uploadedAt;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;
}
