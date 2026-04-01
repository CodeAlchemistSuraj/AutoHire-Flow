package com.autohire.flow.infrastructure.persistence.adapter;

import com.autohire.flow.application.port.outgoing.JobPostingPort;
import com.autohire.flow.domain.model.JobPosting;
import com.autohire.flow.domain.exception.JobNotFoundException;
import com.autohire.flow.infrastructure.persistence.entity.JobPostingEntity;
import com.autohire.flow.infrastructure.persistence.repository.JpaJobPostingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Job Posting Repository Adapter
 * 
 * Implements the JobPostingPort interface, adapting between the domain model (JobPosting)
 * and persistence layer (JobPostingEntity). Handles conversion, filtering, and database operations.
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class JobPostingRepositoryAdapter implements JobPostingPort {

    private final JpaJobPostingRepository jpaJobPostingRepository;

    @Override
    @Transactional
    public JobPosting save(JobPosting jobPosting) {
        log.info("Saving job posting: {} at company: {}", 
            jobPosting.getTitle(), jobPosting.getCompanyName());
        
        try {
            JobPostingEntity entity = convertDomainToEntity(jobPosting);
            JobPostingEntity savedEntity = jpaJobPostingRepository.save(entity);
            log.info("Job posting saved successfully with ID: {}", savedEntity.getId());
            return convertEntityToDomain(savedEntity);
        } catch (Exception e) {
            log.error("Error saving job posting: {} at {}", 
                jobPosting.getTitle(), jobPosting.getCompanyName(), e);
            throw new RuntimeException("Failed to save job posting", e);
        }
    }

    @Override
    @Transactional
    public List<JobPosting> saveAll(List<JobPosting> jobPostings) {
        log.info("Saving batch of {} job postings", jobPostings.size());
        
        try {
            List<JobPostingEntity> entities = jobPostings.stream()
                .map(this::convertDomainToEntity)
                .collect(Collectors.toList());
            List<JobPostingEntity> savedEntities = jpaJobPostingRepository.saveAll(entities);
            log.info("Successfully saved {} job postings", savedEntities.size());
            return savedEntities.stream()
                .map(this::convertEntityToDomain)
                .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error saving batch of job postings", e);
            throw new RuntimeException("Failed to save job postings batch", e);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<JobPosting> findById(Long jobPostingId) {
        log.info("Fetching job posting by ID: {}", jobPostingId);
        
        return jpaJobPostingRepository.findById(jobPostingId)
            .map(this::convertEntityToDomain);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<JobPosting> findAll() {
        log.info("Fetching all job postings");
        
        List<JobPostingEntity> entities = jpaJobPostingRepository.findAll();
        log.debug("Found {} job postings", entities.size());
        
        return entities.stream()
            .map(this::convertEntityToDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<JobPosting> searchByTitle(String title) {
        log.info("Searching job postings by title: {}", title);
        
        List<JobPostingEntity> entities = jpaJobPostingRepository.findByTitleContainingIgnoreCase(title);
        log.debug("Found {} job postings matching title: {}", entities.size(), title);
        
        return entities.stream()
            .map(this::convertEntityToDomain)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobPosting> findByCompany(String company) {
        log.info("Searching job postings by company: {}", company);
        
        List<JobPostingEntity> entities = jpaJobPostingRepository.findByCompanyContainingIgnoreCase(company);
        log.debug("Found {} job postings for company: {}", entities.size(), company);
        
        return entities.stream()
            .map(this::convertEntityToDomain)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobPosting> findByLocation(String location) {
        log.info("Searching job postings by location: {}", location);
        
        List<JobPostingEntity> entities = jpaJobPostingRepository.findByLocationContainingIgnoreCase(location);
        log.debug("Found {} job postings in location: {}", entities.size(), location);
        
        return entities.stream()
            .map(this::convertEntityToDomain)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobPosting> findActiveJobs() {
        log.info("Fetching all active job postings");
        
        Instant now = Instant.now();
        List<JobPostingEntity> entities = jpaJobPostingRepository.findAllActiveJobs(now);
        
        log.debug("Found {} active job postings", entities.size());
        
        return entities.stream()
            .map(this::convertEntityToDomain)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public JobPosting update(JobPosting jobPosting) throws JobNotFoundException {
        log.info("Updating job posting with ID: {}", jobPosting.getId());
        
        if (!jpaJobPostingRepository.existsById(jobPosting.getId())) {
            log.warn("Job posting not found for update with ID: {}", jobPosting.getId());
            throw new JobNotFoundException(
                "Job posting not found with ID: " + jobPosting.getId()
            );
        }
        
        try {
            JobPostingEntity entity = convertDomainToEntity(jobPosting);
            JobPostingEntity updatedEntity = jpaJobPostingRepository.save(entity);
            log.info("Job posting updated successfully with ID: {}", updatedEntity.getId());
            return convertEntityToDomain(updatedEntity);
        } catch (Exception e) {
            log.error("Error updating job posting with ID: {}", jobPosting.getId(), e);
            throw new RuntimeException("Failed to update job posting", e);
        }
    }
    
    @Override
    @Transactional
    public void deleteById(Long jobId) {
        log.info("Deleting job posting with ID: {}", jobId);
        
        jpaJobPostingRepository.deleteById(jobId);
        log.info("Job posting deleted successfully with ID: {}", jobId);
    }

    /**
     * Convert domain JobPosting model to JPA entity
     */
    private JobPostingEntity convertDomainToEntity(JobPosting jobPosting) {
        JobPostingEntity entity = new JobPostingEntity();
        entity.setId(jobPosting.getId());
        entity.setTitle(jobPosting.getTitle());
        entity.setCompany(jobPosting.getCompany());  // Use setCompany, not setCompanyName
        entity.setDescription(jobPosting.getDescription());
        entity.setLocation(jobPosting.getLocation());
        entity.setSalaryMin(jobPosting.getSalaryMin());
        entity.setSalaryMax(jobPosting.getSalaryMax());
        entity.setEmploymentType(jobPosting.getEmploymentType());
        // Convert List<String> to comma-separated String for requiredSkills
        if (jobPosting.getRequiredSkills() != null && !jobPosting.getRequiredSkills().isEmpty()) {
            entity.setRequiredSkills(String.join(",", jobPosting.getRequiredSkills()));
        }
        // Convert float[] to String for vector storage
        if (jobPosting.getEmbedding() != null) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < jobPosting.getEmbedding().length; i++) {
                if (i > 0) sb.append(",");
                sb.append(jobPosting.getEmbedding()[i]);
            }
            entity.setEmbedding(sb.toString());
        }
        entity.setExpiresAt(jobPosting.getExpiresAt());
        entity.setCreatedAt(jobPosting.getCreatedAt());
        entity.setUpdatedAt(jobPosting.getUpdatedAt());
        return entity;
    }

    /**
     * Convert JPA entity to domain JobPosting model
     */
    private JobPosting convertEntityToDomain(JobPostingEntity entity) {
        // Create job posting with basic info
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
        
        // Convert comma-separated String to List<String> for requiredSkills
        if (entity.getRequiredSkills() != null && !entity.getRequiredSkills().isEmpty()) {
            List<String> skills = Arrays.asList(entity.getRequiredSkills().split(","));
            jobPosting.setRequiredSkills(skills);
        }
        
        // Convert String to float[] for embedding
        if (entity.getEmbedding() != null && !entity.getEmbedding().isEmpty()) {
            String[] parts = entity.getEmbedding().split(",");
            float[] embedding = new float[parts.length];
            for (int i = 0; i < parts.length; i++) {
                embedding[i] = Float.parseFloat(parts[i].trim());
            }
            jobPosting.setEmbedding(embedding);
        }
        
        jobPosting.setExpiresAt(entity.getExpiresAt());
        jobPosting.setCreatedAt(entity.getCreatedAt());
        jobPosting.setUpdatedAt(entity.getUpdatedAt());
        return jobPosting;
    }
}