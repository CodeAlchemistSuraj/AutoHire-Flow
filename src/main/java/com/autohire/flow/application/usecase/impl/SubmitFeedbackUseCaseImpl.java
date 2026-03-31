package com.autohire.flow.application.usecase.impl;

import com.autohire.flow.application.port.incoming.SubmitFeedbackUseCase;
import com.autohire.flow.application.port.outgoing.FeedbackPort;
import com.autohire.flow.application.port.outgoing.MatchResultPort;
import com.autohire.flow.domain.model.Feedback;
import com.autohire.flow.domain.model.MatchResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

/**
 * Submit Feedback Use Case Implementation
 * 
 * Handles user feedback submission on match results with validation
 * and match result updates.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SubmitFeedbackUseCaseImpl implements SubmitFeedbackUseCase {

    private final FeedbackPort feedbackPort;
    private final MatchResultPort matchResultPort;

    private static final List<String> VALID_FEEDBACK_TYPES = Arrays.asList(
        "TOO_SENIOR",
        "NOT_INTERESTED",
        "SALARY_LOW",
        "LOCATION_MISMATCH",
        "NOT_RELEVANT",
        "OTHER"
    );

    @Override
    @Transactional
    public FeedbackResult submitFeedback(FeedbackCommand command) {
        log.info("Processing feedback for user: {} on match result: {} with type: {}", 
            command.userId(), command.matchResultId(), command.feedbackType());
        
        // Validate feedback type
        validateFeedbackType(command.feedbackType());
        
        try {
            // Find match result
            var matchResult = matchResultPort.findByUserAndJob(command.userId(), command.matchResultId())
                .or(() -> {
                    // Try direct lookup if first attempt fails
                    // In production, would need to refactor to support direct ID lookup
                    log.warn("Could not find match result by user and job, attempting direct lookup");
                    return java.util.Optional.empty();
                });
            
            if (matchResult.isEmpty()) {
                log.warn("Match result not found for ID: {}", command.matchResultId());
                throw new IllegalArgumentException(
                    "Match result not found with ID: " + command.matchResultId()
                );
            }
            
            MatchResult result = matchResult.get();
            
            // Check if feedback already exists
            var existingFeedback = feedbackPort.findByMatchResultId(command.matchResultId());
            
            Feedback feedback;
            if (existingFeedback.isPresent()) {
                // Update existing feedback
                feedback = existingFeedback.get();
                feedback.setFeedbackType(command.feedbackType());
                feedback.setComments(command.comments());
                feedback.setUpdatedAt(Instant.now());
                
                feedback = feedbackPort.update(feedback);
                
                log.info("Feedback updated for match result: {} with ID: {}", 
                    command.matchResultId(), feedback.getId());
                
            } else {
                // Create new feedback
                feedback = new Feedback(
                    null,
                    command.matchResultId(),
                    command.userId(),
                    command.feedbackType()
                );
                feedback.setComments(command.comments());
                feedback.setCreatedAt(Instant.now());
                feedback.setUpdatedAt(Instant.now());
                
                feedback = feedbackPort.save(feedback);
                
                log.info("Feedback submitted for match result: {} with ID: {}", 
                    command.matchResultId(), feedback.getId());
                
                // Update match result with feedback reason
                result.setFeedbackReason(extractFeedbackReason(command.feedbackType(), command.comments()));
                result.setUpdatedAt(Instant.now());
                matchResultPort.update(result);
            }
            
            return new FeedbackResult(
                feedback.getId(),
                command.matchResultId(),
                command.userId(),
                command.feedbackType(),
                feedback.getCreatedAt()
            );
            
        } catch (IllegalArgumentException e) {
            log.warn("Feedback submission validation failed: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Feedback submission failed for user: {} on match: {}", 
                command.userId(), command.matchResultId(), e);
            throw new RuntimeException("Failed to submit feedback: " + e.getMessage(), e);
        }
    }

    /**
     * Validate feedback type
     */
    private void validateFeedbackType(String feedbackType) {
        if (feedbackType == null || feedbackType.trim().isEmpty()) {
            throw new IllegalArgumentException("Feedback type cannot be empty");
        }
        
        if (!VALID_FEEDBACK_TYPES.contains(feedbackType)) {
            throw new IllegalArgumentException(
                "Invalid feedback type: " + feedbackType + 
                ". Valid types: " + String.join(", ", VALID_FEEDBACK_TYPES)
            );
        }
    }

    /**
     * Extract feedback reason from type and comments
     */
    private String extractFeedbackReason(String feedbackType, String comments) {
        if (comments != null && !comments.trim().isEmpty()) {
            return feedbackType + ": " + comments;
        }
        return feedbackType;
    }
}
