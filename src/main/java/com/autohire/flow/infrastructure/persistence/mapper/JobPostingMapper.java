package com.autohire.flow.infrastructure.persistence.mapper;

import com.autohire.flow.domain.model.JobPosting;
import com.autohire.flow.infrastructure.persistence.entity.JobPostingEntity;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Manual mapper for JobPosting domain model and JobPostingEntity
 */
@Component
public class JobPostingMapper {
    
    /**
     * Convert domain JobPosting to JPA entity
     */
    public JobPostingEntity toEntity(JobPosting jobPosting) {
        if (jobPosting == null) {
            return null;
        }
        
        JobPostingEntity entity = new JobPostingEntity();
        entity.setId(jobPosting.getId());
        entity.setTitle(jobPosting.getTitle());
        entity.setCompany(jobPosting.getCompany());
        entity.setDescription(jobPosting.getDescription());
        entity.setLocation(jobPosting.getLocation());
        entity.setSalaryMin(jobPosting.getSalaryMin());
        entity.setSalaryMax(jobPosting.getSalaryMax());
        entity.setEmploymentType(jobPosting.getEmploymentType());
        entity.setExpiresAt(jobPosting.getExpiresAt());
        entity.setCreatedAt(jobPosting.getCreatedAt());
        entity.setUpdatedAt(jobPosting.getUpdatedAt());
        
        // Convert List<String> to comma-separated String
        if (jobPosting.getRequiredSkills() != null && !jobPosting.getRequiredSkills().isEmpty()) {
            entity.setRequiredSkills(String.join(",", jobPosting.getRequiredSkills()));
        }
        
        // Convert float[] to comma-separated String
        if (jobPosting.getEmbedding() != null && jobPosting.getEmbedding().length > 0) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < jobPosting.getEmbedding().length; i++) {
                if (i > 0) sb.append(",");
                sb.append(jobPosting.getEmbedding()[i]);
            }
            entity.setEmbedding(sb.toString());
        }
        
        return entity;
    }
    
    /**
     * Convert JPA entity to domain JobPosting
     */
    public JobPosting toDomain(JobPostingEntity entity) {
        if (entity == null) {
            return null;
        }
        
        JobPosting jobPosting = new JobPosting(
            entity.getId(),
            entity.getTitle(),
            entity.getCompany(),
            entity.getDescription()
        );
        jobPosting.setLocation(entity.getLocation());
        jobPosting.setSalaryMin(entity.getSalaryMin());
        jobPosting.setSalaryMax(entity.getSalaryMax());
        jobPosting.setEmploymentType(entity.getEmploymentType());
        jobPosting.setExpiresAt(entity.getExpiresAt());
        jobPosting.setCreatedAt(entity.getCreatedAt());
        jobPosting.setUpdatedAt(entity.getUpdatedAt());
        
        // Convert comma-separated String to List<String>
        if (entity.getRequiredSkills() != null && !entity.getRequiredSkills().isEmpty()) {
            List<String> skills = Arrays.asList(entity.getRequiredSkills().split(","));
            jobPosting.setRequiredSkills(skills);
        }
        
        // Convert comma-separated String to float[]
        if (entity.getEmbedding() != null && !entity.getEmbedding().isEmpty()) {
            String[] parts = entity.getEmbedding().split(",");
            float[] embedding = new float[parts.length];
            for (int i = 0; i < parts.length; i++) {
                embedding[i] = Float.parseFloat(parts[i].trim());
            }
            jobPosting.setEmbedding(embedding);
        }
        
        return jobPosting;
    }
}