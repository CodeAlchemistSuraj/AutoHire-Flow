package com.autohire.flow.domain.model;

import java.time.Instant;
import java.util.List;

/**
 * Resume domain entity representing a user's resume/CV.
 * Contains parsed skills, experiences, and AI-generated embedding.
 */
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
    
    // Constructors
    public Resume() {
    }
    
    public Resume(Long id, Long userId, String originalFileName, String s3Key, String parsedText,
                  List<String> skills, List<Experience> experiences, List<Education> educations,
                  List<String> projects, float[] embedding, Instant uploadedAt, Instant updatedAt,
                  Instant createdAt) {
        this.id = id;
        this.userId = userId;
        this.originalFileName = originalFileName;
        this.s3Key = s3Key;
        this.parsedText = parsedText;
        this.skills = skills;
        this.experiences = experiences;
        this.educations = educations;
        this.projects = projects;
        this.embedding = embedding;
        this.uploadedAt = uploadedAt;
        this.updatedAt = updatedAt;
        this.createdAt = createdAt;
    }
    
    // Getters
    public Long getId() {
        return id;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public String getOriginalFileName() {
        return originalFileName;
    }
    
    public String getS3Key() {
        return s3Key;
    }
    
    public String getParsedText() {
        return parsedText;
    }
    
    public List<String> getSkills() {
        return skills;
    }
    
    public List<Experience> getExperiences() {
        return experiences;
    }
    
    public List<Education> getEducations() {
        return educations;
    }
    
    public List<String> getProjects() {
        return projects;
    }
    
    public float[] getEmbedding() {
        return embedding;
    }
    
    public Instant getUploadedAt() {
        return uploadedAt;
    }
    
    public Instant getUpdatedAt() {
        return updatedAt;
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
    
    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }
    
    public void setS3Key(String s3Key) {
        this.s3Key = s3Key;
    }
    
    public void setParsedText(String parsedText) {
        this.parsedText = parsedText;
    }
    
    public void setSkills(List<String> skills) {
        this.skills = skills;
    }
    
    public void setExperiences(List<Experience> experiences) {
        this.experiences = experiences;
    }
    
    public void setEducations(List<Education> educations) {
        this.educations = educations;
    }
    
    public void setProjects(List<String> projects) {
        this.projects = projects;
    }
    
    public void setEmbedding(float[] embedding) {
        this.embedding = embedding;
    }
    
    public void setUploadedAt(Instant uploadedAt) {
        this.uploadedAt = uploadedAt;
    }
    
    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
    
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
    
    /**
     * Experience nested class
     */
    public static class Experience {
        private String title;
        private String company;
        private String duration;
        private List<String> responsibilities;
        
        // Constructors
        public Experience() {
        }
        
        public Experience(String title, String company, String duration, List<String> responsibilities) {
            this.title = title;
            this.company = company;
            this.duration = duration;
            this.responsibilities = responsibilities;
        }
        
        // Getters
        public String getTitle() {
            return title;
        }
        
        public String getCompany() {
            return company;
        }
        
        public String getDuration() {
            return duration;
        }
        
        public List<String> getResponsibilities() {
            return responsibilities;
        }
        
        // Setters
        public void setTitle(String title) {
            this.title = title;
        }
        
        public void setCompany(String company) {
            this.company = company;
        }
        
        public void setDuration(String duration) {
            this.duration = duration;
        }
        
        public void setResponsibilities(List<String> responsibilities) {
            this.responsibilities = responsibilities;
        }
        
        // Builder pattern
        public static ExperienceBuilder builder() {
            return new ExperienceBuilder();
        }
    }
    
    /**
     * Builder for Experience
     */
    public static class ExperienceBuilder {
        private String title;
        private String company;
        private String duration;
        private List<String> responsibilities;
        
        public ExperienceBuilder title(String title) {
            this.title = title;
            return this;
        }
        
        public ExperienceBuilder company(String company) {
            this.company = company;
            return this;
        }
        
        public ExperienceBuilder duration(String duration) {
            this.duration = duration;
            return this;
        }
        
        public ExperienceBuilder responsibilities(List<String> responsibilities) {
            this.responsibilities = responsibilities;
            return this;
        }
        
        public Experience build() {
            return new Experience(title, company, duration, responsibilities);
        }
    }
    
    /**
     * Education nested class
     */
    public static class Education {
        private String degree;
        private String institution;
        private String year;
        
        // Constructors
        public Education() {
        }
        
        public Education(String degree, String institution, String year) {
            this.degree = degree;
            this.institution = institution;
            this.year = year;
        }
        
        // Getters
        public String getDegree() {
            return degree;
        }
        
        public String getInstitution() {
            return institution;
        }
        
        public String getYear() {
            return year;
        }
        
        // Setters
        public void setDegree(String degree) {
            this.degree = degree;
        }
        
        public void setInstitution(String institution) {
            this.institution = institution;
        }
        
        public void setYear(String year) {
            this.year = year;
        }
    }
}
