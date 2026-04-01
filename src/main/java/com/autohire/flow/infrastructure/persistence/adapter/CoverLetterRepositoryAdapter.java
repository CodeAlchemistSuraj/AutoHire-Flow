package com.autohire.flow.infrastructure.persistence.adapter;

import com.autohire.flow.application.port.outgoing.CoverLetterPort;
import com.autohire.flow.domain.model.CoverLetter;
import com.autohire.flow.infrastructure.persistence.entity.CoverLetterEntity;
import com.autohire.flow.infrastructure.persistence.repository.JpaCoverLetterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
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
        
        // Convert Long IDs to UUID for repository query
        UUID userUuid = convertToUuid(userId);
        UUID jobUuid = convertToUuid(jobId);
        
        Optional<CoverLetterEntity> entity = jpaCoverLetterRepository.findByUserIdAndJobPostingId(userUuid, jobUuid);
        
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
        
        UUID uuid = convertToUuid(coverLetterId);
        Optional<CoverLetterEntity> entity = jpaCoverLetterRepository.findById(uuid);
        
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
        
        UUID userUuid = convertToUuid(userId);
        List<CoverLetterEntity> entities = jpaCoverLetterRepository.findByUserId(userUuid);
        log.debug("Found {} cover letters for user: {}", entities.size(), userId);
        
        return entities.stream()
            .map(this::convertEntityToDomain)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CoverLetter> findByJobId(Long jobId) {
        log.info("Fetching all cover letters for job: {}", jobId);
        
        // Since repository uses jobPostingId, we need to get by job ID
        // This might need a custom repository method
        UUID jobUuid = convertToUuid(jobId);
        List<CoverLetterEntity> entities = jpaCoverLetterRepository.findAll()
            .stream()
            .filter(entity -> entity.getJobPostingId().equals(jobUuid))
            .collect(Collectors.toList());
        
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
        
        UUID uuid = convertToUuid(coverLetterId);
        jpaCoverLetterRepository.deleteById(uuid);
        log.info("Cover letter deleted successfully with ID: {}", coverLetterId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsForUserAndJob(Long userId, Long jobId) {
        UUID userUuid = convertToUuid(userId);
        UUID jobUuid = convertToUuid(jobId);
        boolean exists = jpaCoverLetterRepository.findByUserIdAndJobPostingId(userUuid, jobUuid).isPresent();
        log.debug("Cover letter exists for user {} and job {}: {}", userId, jobId, exists);
        return exists;
    }

    /**
     * Convert domain CoverLetter model to JPA entity
     */
    private CoverLetterEntity convertDomainToEntity(CoverLetter coverLetter) {
        CoverLetterEntity entity = new CoverLetterEntity();
        entity.setId(coverLetter.getId() != null ? convertToUuid(coverLetter.getId()) : null);
        entity.setUserId(convertToUuid(coverLetter.getUserId()));
        entity.setJobPostingId(convertToUuid(coverLetter.getJobId()));
        entity.setContent(coverLetter.getContent());
        entity.setTone(coverLetter.getTone());
        entity.setWordCount(coverLetter.getWordCount());
        entity.setParagraphCount(coverLetter.getParagraphCount());
        entity.setStatus(CoverLetterEntity.CoverLetterStatus.GENERATED);
        entity.setGeneratedAt(coverLetter.getGeneratedAt());
        entity.setCreatedAt(coverLetter.getCreatedAt());
        entity.setUpdatedAt(Instant.now());
        return entity;
    }

    /**
     * Convert JPA entity to domain CoverLetter model
     */
    private CoverLetter convertEntityToDomain(CoverLetterEntity entity) {
        CoverLetter coverLetter = new CoverLetter(
            convertToLong(entity.getId()),
            convertToLong(entity.getUserId()),
            convertToLong(entity.getJobPostingId()),
            entity.getContent(),
            entity.getTone(),
            entity.getGeneratedAt(),
            entity.getCreatedAt()
        );
        return coverLetter;
    }
    
    /**
     * Convert Long ID to UUID (simplified - in production, use proper mapping)
     */
    private UUID convertToUuid(Long id) {
        if (id == null) {
            return null;
        }
        // Simple conversion - in production, you might have a proper ID mapping
        return UUID.nameUUIDFromBytes(id.toString().getBytes());
    }
    
    /**
     * Convert UUID to Long (simplified - in production, use proper mapping)
     */
    private Long convertToLong(UUID uuid) {
        if (uuid == null) {
            return null;
        }
        // Simple conversion - in production, you might have a proper ID mapping
        return (long) uuid.hashCode();
    }
}