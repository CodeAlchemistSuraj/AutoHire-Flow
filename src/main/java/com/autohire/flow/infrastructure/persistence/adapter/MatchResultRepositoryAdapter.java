package com.autohire.flow.infrastructure.persistence.adapter;

import com.autohire.flow.application.port.outgoing.MatchResultPort;
import com.autohire.flow.domain.model.MatchResult;
import com.autohire.flow.infrastructure.persistence.entity.MatchResultEntity;
import com.autohire.flow.infrastructure.persistence.repository.JpaMatchResultRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Match Result Repository Adapter
 * 
 * Implements the MatchResultPort interface, adapting between the domain model (MatchResult)
 * and persistence layer (MatchResultEntity). Handles conversion, filtering, and match score tracking.
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class MatchResultRepositoryAdapter implements MatchResultPort {

    private final JpaMatchResultRepository jpaMatchResultRepository;

    @Override
    @Transactional
    public MatchResult save(MatchResult matchResult) {
        log.info("Saving match result for user: {} and job: {}", 
            matchResult.getUserId(), matchResult.getJobId());
        
        try {
            MatchResultEntity entity = convertDomainToEntity(matchResult);
            MatchResultEntity savedEntity = jpaMatchResultRepository.save(entity);
            log.info("Match result saved successfully with ID: {}", savedEntity.getId());
            return convertEntityToDomain(savedEntity);
        } catch (Exception e) {
            log.error("Error saving match result for user: {} and job: {}", 
                matchResult.getUserId(), matchResult.getJobId(), e);
            throw new RuntimeException("Failed to save match result", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MatchResult> findByUserAndJob(Long userId, Long jobId) {
        log.info("Fetching match result for user: {} and job: {}", userId, jobId);
        
        Optional<MatchResultEntity> entity = jpaMatchResultRepository.findByUserIdAndJobId(userId, jobId);
        
        if (entity.isPresent()) {
            log.debug("Match result found with score: {}", entity.get().getScore());
            return entity.map(this::convertEntityToDomain);
        }
        
        log.debug("No match result found for user: {} and job: {}", userId, jobId);
        return Optional.empty();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MatchResult> findByUserIdAndStatus(Long userId, String status) {
        log.info("Fetching match results for user: {} with status: {}", userId, status);
        
        List<MatchResultEntity> entities = jpaMatchResultRepository.findByUserIdAndStatus(userId, status);
        log.debug("Found {} match results for user: {} with status: {}", entities.size(), userId, status);
        
        return entities.stream()
            .map(this::convertEntityToDomain)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MatchResult> findHighScoringMatches(Long userId, int minimumScore) {
        log.info("Fetching high-scoring matches for user: {} with minimum score: {}", userId, minimumScore);
        
        List<MatchResultEntity> entities = jpaMatchResultRepository.findHighScoringMatches(userId, minimumScore);
        log.debug("Found {} high-scoring matches for user: {}", entities.size(), userId);
        
        return entities.stream()
            .map(this::convertEntityToDomain)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public MatchResult update(MatchResult matchResult) {
        log.info("Updating match result with ID: {}", matchResult.getId());
        
        try {
            MatchResultEntity entity = convertDomainToEntity(matchResult);
            MatchResultEntity updatedEntity = jpaMatchResultRepository.save(entity);
            log.info("Match result updated successfully with ID: {}", updatedEntity.getId());
            return convertEntityToDomain(updatedEntity);
        } catch (Exception e) {
            log.error("Error updating match result with ID: {}", matchResult.getId(), e);
            throw new RuntimeException("Failed to update match result", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<MatchResult> findAllByUserId(Long userId) {
        log.info("Fetching all match results for user: {}", userId);
        
        List<MatchResultEntity> entities = jpaMatchResultRepository.findAll()
            .stream()
            .filter(entity -> entity.getUserId().equals(userId))
            .collect(Collectors.toList());
        
        log.debug("Found {} total match results for user: {}", entities.size(), userId);
        
        return entities.stream()
            .map(this::convertEntityToDomain)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MatchResult> findByJobId(Long jobId) {
        log.info("Fetching all match results for job: {}", jobId);
        
        List<MatchResultEntity> entities = jpaMatchResultRepository.findAll()
            .stream()
            .filter(entity -> entity.getJobId().equals(jobId))
            .collect(Collectors.toList());
        
        log.debug("Found {} match results for job: {}", entities.size(), jobId);
        
        return entities.stream()
            .map(this::convertEntityToDomain)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void delete(Long matchResultId) {
        log.info("Deleting match result with ID: {}", matchResultId);
        
        jpaMatchResultRepository.deleteById(matchResultId);
        log.info("Match result deleted successfully with ID: {}", matchResultId);
    }

    /**
     * Convert domain MatchResult model to JPA entity
     */
    private MatchResultEntity convertDomainToEntity(MatchResult matchResult) {
        MatchResultEntity entity = new MatchResultEntity();
        entity.setId(matchResult.getId());
        entity.setUserId(matchResult.getUserId());
        entity.setJobId(matchResult.getJobId());
        entity.setScore(matchResult.getScore());
        entity.setStatus(matchResult.getStatus());
        entity.setQualityLevel(matchResult.getQualityLevel());
        entity.setMatchingSkills(matchResult.getMatchingSkills());
        entity.setMissingSkills(matchResult.getMissingSkills());
        entity.setExplanation(matchResult.getExplanation());
        entity.setFeedbackReason(matchResult.getFeedbackReason());
        entity.setCreatedAt(matchResult.getCreatedAt());
        entity.setUpdatedAt(matchResult.getUpdatedAt());
        return entity;
    }

    /**
     * Convert JPA entity to domain MatchResult model
     */
    private MatchResult convertEntityToDomain(MatchResultEntity entity) {
        MatchResult matchResult = new MatchResult(
            entity.getId(),
            entity.getUserId(),
            entity.getJobId(),
            entity.getScore()
        );
        matchResult.setStatus(entity.getStatus());
        matchResult.setQualityLevel(entity.getQualityLevel());
        matchResult.setMatchingSkills(entity.getMatchingSkills());
        matchResult.setMissingSkills(entity.getMissingSkills());
        matchResult.setExplanation(entity.getExplanation());
        matchResult.setFeedbackReason(entity.getFeedbackReason());
        matchResult.setCreatedAt(entity.getCreatedAt());
        matchResult.setUpdatedAt(entity.getUpdatedAt());
        return matchResult;
    }
}
