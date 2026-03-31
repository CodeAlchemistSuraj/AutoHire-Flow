package com.autohire.flow.infrastructure.persistence.adapter;

import com.autohire.flow.application.port.outgoing.CoverLetterPort;
import com.autohire.flow.domain.model.CoverLetter;
import com.autohire.flow.infrastructure.persistence.entity.CoverLetterEntity;
import com.autohire.flow.infrastructure.persistence.repository.JpaCoverLetterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Cover Letter Repository Adapter
 * 
 * Implements the CoverLetterPort interface, adapting between the domain model (CoverLetter)
 * and persistence layer (CoverLetterEntity). Handles generated cover letter persistence and retrieval.
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class CoverLetterRepositoryAdapter implements CoverLetterPort {

    private final JpaCoverLetterRepository jpaCoverLetterRepository;

    @Override
    @Transactional
    public CoverLetter save(CoverLetter coverLetter) {
        log.info("Saving cover letter for user: {} and job: {}", 
            coverLetter.getUserId(), coverLetter.getJobId());
        
        try {
            CoverLetterEntity entity = convertDomainToEntity(coverLetter);
            CoverLetterEntity savedEntity = jpaCoverLetterRepository.save(entity);
            log.info("Cover letter saved successfully with ID: {}", savedEntity.getId());
            return convertEntityToDomain(savedEntity);
        } catch (Exception e) {
            log.error("Error saving cover letter for user: {} and job: {}", 
                coverLetter.getUserId(), coverLetter.getJobId(), e);
            throw new RuntimeException("Failed to save cover letter", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CoverLetter> findByUserAndJob(Long userId, Long jobId) {
        log.info("Fetching cover letter for user: {} and job: {}", userId, jobId);
        
        Optional<CoverLetterEntity> entity = jpaCoverLetterRepository.findByUserIdAndJobId(userId, jobId);
        
        if (entity.isPresent()) {
            log.debug("Cover letter found with ID: {}", entity.get().getId());
            return entity.map(this::convertEntityToDomain);
        }
        
        log.debug("No cover letter found for user: {} and job: {}", userId, jobId);
        return Optional.empty();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CoverLetter> findById(Long coverLetterId) {
        log.info("Fetching cover letter by ID: {}", coverLetterId);
        
        Optional<CoverLetterEntity> entity = jpaCoverLetterRepository.findById(coverLetterId);
        
        if (entity.isPresent()) {
            log.debug("Cover letter found with ID: {}", coverLetterId);
            return entity.map(this::convertEntityToDomain);
        }
        
        log.warn("Cover letter not found with ID: {}", coverLetterId);
        return Optional.empty();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CoverLetter> findByUserId(Long userId) {
        log.info("Fetching all cover letters for user: {}", userId);
        
        List<CoverLetterEntity> entities = jpaCoverLetterRepository.findByUserId(userId);
        log.debug("Found {} cover letters for user: {}", entities.size(), userId);
        
        return entities.stream()
            .map(this::convertEntityToDomain)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CoverLetter> findByJobId(Long jobId) {
        log.info("Fetching all cover letters for job: {}", jobId);
        
        List<CoverLetterEntity> entities = jpaCoverLetterRepository.findByJobId(jobId);
        log.debug("Found {} cover letters for job: {}", entities.size(), jobId);
        
        return entities.stream()
            .map(this::convertEntityToDomain)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CoverLetter update(CoverLetter coverLetter) {
        log.info("Updating cover letter with ID: {}", coverLetter.getId());
        
        try {
            CoverLetterEntity entity = convertDomainToEntity(coverLetter);
            CoverLetterEntity updatedEntity = jpaCoverLetterRepository.save(entity);
            log.info("Cover letter updated successfully with ID: {}", updatedEntity.getId());
            return convertEntityToDomain(updatedEntity);
        } catch (Exception e) {
            log.error("Error updating cover letter with ID: {}", coverLetter.getId(), e);
            throw new RuntimeException("Failed to update cover letter", e);
        }
    }

    @Override
    @Transactional
    public void delete(Long coverLetterId) {
        log.info("Deleting cover letter with ID: {}", coverLetterId);
        
        jpaCoverLetterRepository.deleteById(coverLetterId);
        log.info("Cover letter deleted successfully with ID: {}", coverLetterId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsForUserAndJob(Long userId, Long jobId) {
        boolean exists = jpaCoverLetterRepository.findByUserIdAndJobId(userId, jobId).isPresent();
        log.debug("Cover letter exists for user {} and job {}: {}", userId, jobId, exists);
        return exists;
    }

    /**
     * Convert domain CoverLetter model to JPA entity
     */
    private CoverLetterEntity convertDomainToEntity(CoverLetter coverLetter) {
        CoverLetterEntity entity = new CoverLetterEntity();
        entity.setId(coverLetter.getId());
        entity.setUserId(coverLetter.getUserId());
        entity.setJobId(coverLetter.getJobId());
        entity.setContent(coverLetter.getContent());
        entity.setTone(coverLetter.getTone());
        entity.setWordCount(coverLetter.getWordCount());
        entity.setParagraphCount(coverLetter.getParagraphCount());
        entity.setCreatedAt(coverLetter.getCreatedAt());
        entity.setUpdatedAt(coverLetter.getUpdatedAt());
        return entity;
    }

    /**
     * Convert JPA entity to domain CoverLetter model
     */
    private CoverLetter convertEntityToDomain(CoverLetterEntity entity) {
        CoverLetter coverLetter = new CoverLetter(
            entity.getId(),
            entity.getUserId(),
            entity.getJobId(),
            entity.getContent()
        );
        coverLetter.setTone(entity.getTone());
        coverLetter.setWordCount(entity.getWordCount());
        coverLetter.setParagraphCount(entity.getParagraphCount());
        coverLetter.setCreatedAt(entity.getCreatedAt());
        coverLetter.setUpdatedAt(entity.getUpdatedAt());
        return coverLetter;
    }
}
