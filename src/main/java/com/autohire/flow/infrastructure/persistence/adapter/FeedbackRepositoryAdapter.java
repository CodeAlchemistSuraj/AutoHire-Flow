package com.autohire.flow.infrastructure.persistence.adapter;

import com.autohire.flow.application.port.outgoing.FeedbackPort;
import com.autohire.flow.domain.model.Feedback;
import com.autohire.flow.infrastructure.persistence.entity.FeedbackEntity;
import com.autohire.flow.infrastructure.persistence.repository.JpaFeedbackRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Feedback Repository Adapter
 * 
 * Implements the FeedbackPort interface, adapting between the domain model (Feedback)
 * and persistence layer (FeedbackEntity). Handles user feedback on match results.
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class FeedbackRepositoryAdapter implements FeedbackPort {

    private final JpaFeedbackRepository jpaFeedbackRepository;

    @Override
    @Transactional
    public Feedback save(Feedback feedback) {
        log.info("Saving feedback for match result: {}", feedback.getMatchResultId());
        
        try {
            FeedbackEntity entity = convertDomainToEntity(feedback);
            FeedbackEntity savedEntity = jpaFeedbackRepository.save(entity);
            log.info("Feedback saved successfully with ID: {}", savedEntity.getId());
            return convertEntityToDomain(savedEntity);
        } catch (Exception e) {
            log.error("Error saving feedback for match result: {}", feedback.getMatchResultId(), e);
            throw new RuntimeException("Failed to save feedback", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Feedback> findById(Long feedbackId) {
        log.info("Fetching feedback by ID: {}", feedbackId);
        
        Optional<FeedbackEntity> entity = jpaFeedbackRepository.findById(feedbackId);
        
        if (entity.isPresent()) {
            log.debug("Feedback found with ID: {}", feedbackId);
            return entity.map(this::convertEntityToDomain);
        }
        
        log.warn("Feedback not found with ID: {}", feedbackId);
        return Optional.empty();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Feedback> findByMatchResultId(Long matchResultId) {
        log.info("Fetching feedback for match result: {}", matchResultId);
        
        Optional<FeedbackEntity> entity = jpaFeedbackRepository.findByMatchResultId(matchResultId);
        
        if (entity.isPresent()) {
            log.debug("Feedback found for match result: {}", matchResultId);
            return entity.map(this::convertEntityToDomain);
        }
        
        log.debug("No feedback found for match result: {}", matchResultId);
        return Optional.empty();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Feedback> findByUserId(Long userId) {
        log.info("Fetching all feedback for user: {}", userId);
        
        List<FeedbackEntity> entities = jpaFeedbackRepository.findByUserId(userId);
        log.debug("Found {} feedback entries for user: {}", entities.size(), userId);
        
        return entities.stream()
            .map(this::convertEntityToDomain)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Feedback> findByMatchResultIdAndUserId(Long matchResultId, Long userId) {
        log.info("Fetching feedback for match result: {} and user: {}", matchResultId, userId);
        
        List<FeedbackEntity> entities = jpaFeedbackRepository.findAll()
            .stream()
            .filter(entity -> entity.getMatchResultId().equals(matchResultId) 
                && entity.getUserId().equals(userId))
            .collect(Collectors.toList());
        
        log.debug("Found {} feedback entries", entities.size());
        
        return entities.stream()
            .map(this::convertEntityToDomain)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Feedback> findByFeedbackType(String feedbackType) {
        log.info("Fetching feedback by type: {}", feedbackType);
        
        List<FeedbackEntity> entities = jpaFeedbackRepository.findByFeedbackType(feedbackType);
        log.debug("Found {} feedback entries with type: {}", entities.size(), feedbackType);
        
        return entities.stream()
            .map(this::convertEntityToDomain)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Feedback update(Feedback feedback) {
        log.info("Updating feedback with ID: {}", feedback.getId());
        
        try {
            FeedbackEntity entity = convertDomainToEntity(feedback);
            FeedbackEntity updatedEntity = jpaFeedbackRepository.save(entity);
            log.info("Feedback updated successfully with ID: {}", updatedEntity.getId());
            return convertEntityToDomain(updatedEntity);
        } catch (Exception e) {
            log.error("Error updating feedback with ID: {}", feedback.getId(), e);
            throw new RuntimeException("Failed to update feedback", e);
        }
    }

    @Override
    @Transactional
    public void delete(Long feedbackId) {
        log.info("Deleting feedback with ID: {}", feedbackId);
        
        jpaFeedbackRepository.deleteById(feedbackId);
        log.info("Feedback deleted successfully with ID: {}", feedbackId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByMatchResultId(Long matchResultId) {
        boolean exists = jpaFeedbackRepository.findByMatchResultId(matchResultId).isPresent();
        log.debug("Feedback exists for match result {}: {}", matchResultId, exists);
        return exists;
    }

    /**
     * Convert domain Feedback model to JPA entity
     */
    private FeedbackEntity convertDomainToEntity(Feedback feedback) {
        FeedbackEntity entity = new FeedbackEntity();
        entity.setId(feedback.getId());
        entity.setMatchResultId(feedback.getMatchResultId());
        entity.setUserId(feedback.getUserId());
        entity.setFeedbackType(feedback.getFeedbackType());
        entity.setComments(feedback.getComments());
        entity.setCreatedAt(feedback.getCreatedAt());
        entity.setUpdatedAt(feedback.getUpdatedAt());
        return entity;
    }

    /**
     * Convert JPA entity to domain Feedback model
     */
    private Feedback convertEntityToDomain(FeedbackEntity entity) {
        Feedback feedback = new Feedback(
            entity.getId(),
            entity.getMatchResultId(),
            entity.getUserId(),
            entity.getFeedbackType()
        );
        feedback.setComments(entity.getComments());
        feedback.setCreatedAt(entity.getCreatedAt());
        feedback.setUpdatedAt(entity.getUpdatedAt());
        return feedback;
    }
}
