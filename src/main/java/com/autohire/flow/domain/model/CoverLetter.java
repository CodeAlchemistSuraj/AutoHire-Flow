package com.autohire.flow.domain.model;

import lombok.*;
import java.time.Instant;

/**
 * CoverLetter domain entity representing an AI-generated cover letter.
 * Tailored for a specific user and job combination.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "content")
public class CoverLetter {
    
    private Long id;
    
    private Long userId;
    
    private Long jobId;
    
    private String content;
    
    private String tone;  // PROFESSIONAL, ENTHUSIASTIC, CONCISE
    
    private Instant generatedAt;
    
    private Instant createdAt;
    
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
