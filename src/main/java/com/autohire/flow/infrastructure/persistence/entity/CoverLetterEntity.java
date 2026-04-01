package com.autohire.flow.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "cover_letters")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CoverLetterEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(name = "user_id", nullable = false)
    private UUID userId;
    
    @Column(name = "job_posting_id", nullable = false)
    private UUID jobPostingId;
    
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;
    
    @Column(length = 50)
    private String tone;  // PROFESSIONAL, ENTHUSIASTIC, CONCISE
    
    @Column(name = "word_count")
    private Integer wordCount;
    
    @Column(name = "paragraph_count")
    private Integer paragraphCount;
    
    @Enumerated(EnumType.STRING)
    private CoverLetterStatus status;
    
    @CreationTimestamp
    @Column(name = "generated_at", nullable = false, updatable = false)
    private Instant generatedAt;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;
    
    public enum CoverLetterStatus {
        DRAFT, GENERATED, REVIEWED, ACCEPTED, REJECTED
    }
}