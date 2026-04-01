package com.autohire.flow.domain.model;

import java.time.Instant;

/**
 * CoverLetter domain entity representing an AI-generated cover letter.
 * Tailored for a specific user and job combination.
 */
public class CoverLetter {
    
    private Long id;
    
    private Long userId;
    
    private Long jobId;
    
    private String content;
    
    private String tone;  // PROFESSIONAL, ENTHUSIASTIC, CONCISE
    
    private Instant generatedAt;
    
    private Instant createdAt;
    
    // Constructors
    public CoverLetter() {
    }
    
    public CoverLetter(Long id, Long userId, Long jobId, String content, String tone, 
                       Instant generatedAt, Instant createdAt) {
        this.id = id;
        this.userId = userId;
        this.jobId = jobId;
        this.content = content;
        this.tone = tone;
        this.generatedAt = generatedAt;
        this.createdAt = createdAt;
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
    
    public String getContent() {
        return content;
    }
    
    public void setWordCount(int wordCount) {
        // Word count is derived from content, not stored separately
        // If you want to store it, add a field and setter
    }
    
    public void setParagraphCount(int paragraphCount) {
        // Paragraph count is derived from content, not stored separately
        // If you want to store it, add a field and setter
    }
    
    public void setUpdatedAt(Instant updatedAt) {
        // If you need an updatedAt field, add it to the class
        // For now, just ignore
    }

    public String getTone() {
        return tone;
    }
    
    public Instant getGeneratedAt() {
        return generatedAt;
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
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public void setTone(String tone) {
        this.tone = tone;
    }
    
    public void setGeneratedAt(Instant generatedAt) {
        this.generatedAt = generatedAt;
    }
    
    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
    
    /**
     * Validates cover letter has required fields.
     * @return true if valid, false otherwise
     */
    public boolean isValid() {
        return this.userId != null &&
               this.jobId != null &&
               this.content != null && !this.content.isBlank() &&
               this.tone != null && !this.tone.isBlank();
    }
    
    /**
     * Gets the word count of the cover letter.
     * @return number of words
     */
    public int getWordCount() {
        if (this.content == null || this.content.isBlank()) {
            return 0;
        }
        return this.content.trim().split("\\s+").length;
    }
    
    /**
     * Gets the paragraph count.
     * @return number of paragraphs
     */
    public int getParagraphCount() {
        if (this.content == null || this.content.isBlank()) {
            return 0;
        }
        return this.content.split("\\n\\n").length;
    }
    
    /**
     * Checks if cover letter meets minimum requirements.
     * @return true if meets requirements (min 200 words, 3 paragraphs)
     */
    public boolean meetsMinimumRequirements() {
        return this.getWordCount() >= 200 && this.getParagraphCount() >= 3;
    }
}