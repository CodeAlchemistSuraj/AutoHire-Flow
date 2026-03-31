package com.autohire.flow.domain.model;

import lombok.*;
import java.time.Instant;
import java.util.List;

/**
 * Resume domain entity representing a user's resume/CV.
 * Contains parsed skills, experiences, and AI-generated embedding.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"embedding", "parsedText"})
public class Resume {
    
    private Long id;
    
    private Long userId;
    
    private String originalFileName;
    
    private String s3Key;
    
    private String parsedText;
    
    private List<String> skills;
    
    private List<Experience> experiences;
    
    private List<Education> educations;
    
    private List<String> projects;
    
    private float[] embedding;
    
    private Instant uploadedAt;
    
    private Instant updatedAt;
    
    private Instant createdAt;
    
    /**
     * Validates resume has minimum required fields.
     * @return true if resume is valid, false otherwise
     */
    public boolean isValid() {
        return this.userId != null &&
               this.parsedText != null && !this.parsedText.isBlank() &&
               this.skills != null && !this.skills.isEmpty() &&
               this.embedding != null && this.embedding.length == 768;
    }
    
    /**
     * Gets the skill count from the resume.
     * @return number of skills extracted
     */
    public int getSkillsCount() {
        return this.skills != null ? this.skills.size() : 0;
    }
    
    /**
     * Gets total years of experience from resume experiences.
     * @return total years of experience
     */
    public int getTotalExperienceYears() {
        if (this.experiences == null || this.experiences.isEmpty()) {
            return 0;
        }
        return this.experiences.stream()
            .mapToInt(this::extractYears)
            .sum();
    }
    
    private int extractYears(Experience exp) {
        if (exp.getDuration() == null) {
            return 0;
        }
        try {
            return Integer.parseInt(exp.getDuration().replaceAll("\\D", ""));
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    
    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Experience {
        private String title;
        private String company;
        private String duration;
        private List<String> responsibilities;
    }
    
    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Education {
        private String degree;
        private String institution;
        private String year;
    }
}
