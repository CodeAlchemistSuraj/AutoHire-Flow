package com.autohire.flow.domain.model;

import java.time.Instant;
import java.util.List;

/**
 * JobPosting domain entity representing a job opportunity.
 * Contains job details and embeddings for semantic matching.
 */
public class JobPosting {
    
    private Long id;
    
    private String externalId;
    
    private String title;
    
    private String company;
    
    private String description;
    
    private String location;
    
    private String salaryRange;
    
    private Double salaryMin;  // Added for min salary
    private Double salaryMax;  // Added for max salary
    
    private String employmentType;
    
    private float[] embedding;
    
    private String source;
    
    private Instant postedAt;
    
    private Instant expiresAt;
    
    private Instant createdAt;
    
    private Instant updatedAt;
    
    private List<String> requiredSkills;
    
    // Constructors
    public JobPosting() {
    }
    
    // Main constructor for full object
    public JobPosting(Long id, String externalId, String title, String company, String description,
                      String location, String salaryRange, Double salaryMin, Double salaryMax,
                      String employmentType, float[] embedding, String source, Instant postedAt, 
                      Instant expiresAt, Instant createdAt, Instant updatedAt,
                      List<String> requiredSkills) {
        this.id = id;
        this.externalId = externalId;
        this.title = title;
        this.company = company;
        this.description = description;
        this.location = location;
        this.salaryRange = salaryRange;
        this.salaryMin = salaryMin;
        this.salaryMax = salaryMax;
        this.employmentType = employmentType;
        this.embedding = embedding;
        this.source = source;
        this.postedAt = postedAt;
        this.expiresAt = expiresAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.requiredSkills = requiredSkills;
    }
    
    // Simplified constructor for basic job posting
    public JobPosting(Long id, String title, String company, String description) {
        this.id = id;
        this.title = title;
        this.company = company;
        this.description = description;
    }
    
    // Getters
    public Long getId() {
        return id;
    }
    
    public String getExternalId() {
        return externalId;
    }
    
    public String getTitle() {
        return title;
    }
    
    public String getCompany() {
        return company;
    }
    
    public String getDescription() {
        return description;
    }
    
    public String getLocation() {
        return location;
    }
    
    public String getSalaryRange() {
        return salaryRange;
    }
    
    public Double getSalaryMin() {
        return salaryMin;
    }
    
    public Double getSalaryMax() {
        return salaryMax;
    }
    
    public String getEmploymentType() {
        return employmentType;
    }
    
    /**
     * Get company name (alias for getCompany)
     */
    public String getCompanyName() {
        return this.company;
    }
    
    public float[] getEmbedding() {
        return embedding;
    }
    
    public String getSource() {
        return source;
    }
    
    public Instant getPostedAt() {
        return postedAt;
    }
    
    public Instant getExpiresAt() {
        return expiresAt;
    }
    
    public Instant getCreatedAt() {
        return createdAt;
    }
    
    public Instant getUpdatedAt() {
        return updatedAt;
    }
    
    public List<String> getRequiredSkills() {
        return requiredSkills;
    }
    
    // Setters
    public void setId(Long id) {
        this.id = id;
    }
    
    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public void setCompany(String company) {
        this.company = company;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public void setSalaryRange(String salaryRange) {
        this.salaryRange = salaryRange;
    }
    
    public void setSalaryMin(Double salaryMin) {
        this.salaryMin = salaryMin;
    }
    
    public void setSalaryMax(Double salaryMax) {
        this.salaryMax = salaryMax;
    }
    
    public void setEmploymentType(String employmentType) {
        this.employmentType = employmentType;
    }
    
    public void setEmbedding(float[] embedding) {
        this.embedding = embedding;
    }
    
    public void setSource(String source) {
        this.source = source;
    }
    
    public void setPostedAt(Instant postedAt) {
        this.postedAt = postedAt;
    }
    
    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }
    
    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
    
    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public void setRequiredSkills(List<String> requiredSkills) {
        this.requiredSkills = requiredSkills;
    }
    
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
     * Checks if the job posting is expired.
     * @param now the instant to check against
     * @return true if job is expired, false otherwise
     */
    public boolean isExpired(Instant now) {
        if (this.expiresAt == null) {
            return false;
        }
        return now.isAfter(this.expiresAt);
    }
    
    /**
     * Gets a summary of the job posting.
     * @return concatenated string of key job information
     */
    public String getSummary() {
        return String.format("%s at %s in %s", this.title, this.company, this.location);
    }
}