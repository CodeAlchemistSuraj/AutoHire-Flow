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
import java.util.List;
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
    public void saveAll(List<JobPosting> jobPostings) {
        log.info("Saving batch of {} job postings", jobPostings.size());
        
        try {
            List<JobPostingEntity> entities = jobPostings.stream()
                .map(this::convertDomainToEntity)
                .collect(Collectors.toList());
            jpaJobPostingRepository.saveAll(entities);
            log.info("Successfully saved {} job postings", jobPostings.size());
        } catch (Exception e) {
            log.error("Error saving batch of job postings", e);
            throw new RuntimeException("Failed to save job postings batch", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public JobPosting findById(Long jobPostingId) throws JobNotFoundException {
        log.info("Fetching job posting by ID: {}", jobPostingId);
        
        JobPostingEntity entity = jpaJobPostingRepository.findById(jobPostingId)
            .orElseThrow(() -> {
                log.warn("Job posting not found with ID: {}", jobPostingId);
                return new JobNotFoundException(
                    "Job posting not found with ID: " + jobPostingId
                );
            });
        
        return convertEntityToDomain(entity);
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
    public List<JobPosting> searchByCompany(String company) {
        log.info("Searching job postings by company: {}", company);
        
        List<JobPostingEntity> entities = jpaJobPostingRepository.findByCompanyNameContainingIgnoreCase(company);
        log.debug("Found {} job postings for company: {}", entities.size(), company);
        
        return entities.stream()
            .map(this::convertEntityToDomain)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobPosting> searchByLocation(String location) {
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

    /**
     * Convert domain JobPosting model to JPA entity
     */
    private JobPostingEntity convertDomainToEntity(JobPosting jobPosting) {
        JobPostingEntity entity = new JobPostingEntity();
        entity.setId(jobPosting.getId());
        entity.setTitle(jobPosting.getTitle());
        entity.setCompanyName(jobPosting.getCompanyName());
        entity.setDescription(jobPosting.getDescription());
        entity.setLocation(jobPosting.getLocation());
        entity.setSalaryMin(jobPosting.getSalaryMin());
        entity.setSalaryMax(jobPosting.getSalaryMax());
        entity.setEmploymentType(jobPosting.getEmploymentType());
        entity.setRequiredSkills(jobPosting.getRequiredSkills());
        entity.setEmbedding(jobPosting.getEmbedding()); // Will be stored as vector(768)
        entity.setExpiresAt(jobPosting.getExpiresAt());
        entity.setCreatedAt(jobPosting.getCreatedAt());
        entity.setUpdatedAt(jobPosting.getUpdatedAt());
        return entity;
    }

    /**
     * Convert JPA entity to domain JobPosting model
     */
    private JobPosting convertEntityToDomain(JobPostingEntity entity) {
        JobPosting jobPosting = new JobPosting(
            entity.getId(),
            entity.getTitle(),
            entity.getCompanyName(),
            entity.getDescription()
        );
        jobPosting.setLocation(entity.getLocation());
        jobPosting.setSalaryMin(entity.getSalaryMin());
        jobPosting.setSalaryMax(entity.getSalaryMax());
        jobPosting.setEmploymentType(entity.getEmploymentType());
        jobPosting.setRequiredSkills(entity.getRequiredSkills());
        jobPosting.setEmbedding(entity.getEmbedding());
        jobPosting.setExpiresAt(entity.getExpiresAt());
        jobPosting.setCreatedAt(entity.getCreatedAt());
        jobPosting.setUpdatedAt(entity.getUpdatedAt());
        return jobPosting;
    }
}
