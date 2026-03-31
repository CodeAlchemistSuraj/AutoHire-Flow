package com.autohire.flow.infrastructure.persistence.adapter;

import com.autohire.flow.application.port.outgoing.ResumePort;
import com.autohire.flow.domain.model.Resume;
import com.autohire.flow.domain.exception.ResumeNotFoundException;
import com.autohire.flow.infrastructure.persistence.entity.ResumeEntity;
import com.autohire.flow.infrastructure.persistence.repository.JpaResumeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Resume Repository Adapter
 * 
 * Implements the ResumePort interface, adapting between the domain model (Resume)
 * and persistence layer (ResumeEntity). Handles conversion, error handling, and
 * database operations.
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class ResumeRepositoryAdapter implements ResumePort {

    private final JpaResumeRepository jpaResumeRepository;

    @Override
    @Transactional
    public Resume save(Resume resume) {
        log.info("Saving resume for user: {}", resume.getUserId());
        
        try {
            ResumeEntity entity = convertDomainToEntity(resume);
            ResumeEntity savedEntity = jpaResumeRepository.save(entity);
            log.info("Resume saved successfully with ID: {}", savedEntity.getId());
            return convertEntityToDomain(savedEntity);
        } catch (Exception e) {
            log.error("Error saving resume for user: {}", resume.getUserId(), e);
            throw new RuntimeException("Failed to save resume", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Resume findByUserId(Long userId) throws ResumeNotFoundException {
        log.info("Fetching resume for user: {}", userId);
        
        ResumeEntity entity = jpaResumeRepository.findByUserId(userId)
            .orElseThrow(() -> {
                log.warn("Resume not found for user: {}", userId);
                return new ResumeNotFoundException(
                    "Resume not found for user ID: " + userId
                );
            });
        
        return convertEntityToDomain(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public Resume findById(Long resumeId) throws ResumeNotFoundException {
        log.info("Fetching resume by ID: {}", resumeId);
        
        ResumeEntity entity = jpaResumeRepository.findById(resumeId)
            .orElseThrow(() -> {
                log.warn("Resume not found with ID: {}", resumeId);
                return new ResumeNotFoundException(
                    "Resume not found with ID: " + resumeId
                );
            });
        
        return convertEntityToDomain(entity);
    }

    @Override
    @Transactional
    public void delete(Long resumeId) throws ResumeNotFoundException {
        log.info("Deleting resume with ID: {}", resumeId);
        
        if (!jpaResumeRepository.existsById(resumeId)) {
            log.warn("Resume not found for deletion with ID: {}", resumeId);
            throw new ResumeNotFoundException(
                "Resume not found with ID: " + resumeId
            );
        }
        
        jpaResumeRepository.deleteById(resumeId);
        log.info("Resume deleted successfully with ID: {}", resumeId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByUserId(Long userId) {
        boolean exists = jpaResumeRepository.existsByUserId(userId);
        log.debug("Resume exists for user {}: {}", userId, exists);
        return exists;
    }

    @Override
    @Transactional
    public Resume update(Resume resume) throws ResumeNotFoundException {
        log.info("Updating resume with ID: {}", resume.getId());
        
        if (!jpaResumeRepository.existsById(resume.getId())) {
            log.warn("Resume not found for update with ID: {}", resume.getId());
            throw new ResumeNotFoundException(
                "Resume not found with ID: " + resume.getId()
            );
        }
        
        try {
            ResumeEntity entity = convertDomainToEntity(resume);
            ResumeEntity updatedEntity = jpaResumeRepository.save(entity);
            log.info("Resume updated successfully with ID: {}", updatedEntity.getId());
            return convertEntityToDomain(updatedEntity);
        } catch (Exception e) {
            log.error("Error updating resume with ID: {}", resume.getId(), e);
            throw new RuntimeException("Failed to update resume", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Resume> findAllByUserId(Long userId) {
        log.info("Fetching all resumes for user: {}", userId);
        
        List<ResumeEntity> entities = jpaResumeRepository.findAll()
            .stream()
            .filter(entity -> entity.getUserId().equals(userId))
            .collect(Collectors.toList());
        
        log.debug("Found {} resumes for user: {}", entities.size(), userId);
        return entities.stream()
            .map(this::convertEntityToDomain)
            .collect(Collectors.toList());
    }

    /**
     * Convert domain Resume model to JPA entity
     */
    private ResumeEntity convertDomainToEntity(Resume resume) {
        ResumeEntity entity = new ResumeEntity();
        entity.setId(resume.getId());
        entity.setUserId(resume.getUserId());
        entity.setContent(resume.getContent());
        entity.setEmbedding(resume.getEmbedding()); // Will be stored as vector(768)
        entity.setSkills(resume.getSkills());
        entity.setCreatedAt(resume.getCreatedAt());
        entity.setUpdatedAt(resume.getUpdatedAt());
        return entity;
    }

    /**
     * Convert JPA entity to domain Resume model
     */
    private Resume convertEntityToDomain(ResumeEntity entity) {
        Resume resume = new Resume(
            entity.getId(),
            entity.getUserId(),
            entity.getContent()
        );
        resume.setEmbedding(entity.getEmbedding());
        resume.setSkills(entity.getSkills());
        resume.setCreatedAt(entity.getCreatedAt());
        resume.setUpdatedAt(entity.getUpdatedAt());
        return resume;
    }
}
