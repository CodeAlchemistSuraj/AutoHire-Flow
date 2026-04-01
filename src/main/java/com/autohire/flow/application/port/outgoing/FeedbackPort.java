package com.autohire.flow.application.port.outgoing;

import com.autohire.flow.domain.model.Feedback;
import java.util.List;
import java.util.Optional;

/**
 * Output port for feedback persistence operations.
 */
public interface FeedbackPort {
    
    /**
     * Persists feedback.
     * @param feedback feedback to save
     * @return saved feedback with generated ID
     */
    Feedback save(Feedback feedback);
    
    /**
     * Finds feedback by ID.
     * @param feedbackId feedback ID
     * @return Optional containing feedback if found
     */
    Optional<Feedback> findById(Long feedbackId);
    
    /**
     * Finds all feedback for a match result.
     * @param matchResultId match result ID
     * @return list of feedback for that match
     */
    List<Feedback> findByMatchResultId(Long matchResultId);
    
    /**
     * Finds all feedback submitted by a user.
     * @param userId user ID
     * @return list of user's feedback
     */
    List<Feedback> findByUserId(Long userId);
    
    /**
     * Updates feedback.
     * @param feedback feedback with updated fields
     * @return updated feedback
     */
    Feedback update(Feedback feedback);
    
    /**
     * Deletes feedback.
     * @param feedbackId ID of feedback to delete
     */
    void delete(Long feedbackId);
    
    /**
     * Checks if feedback exists for a match result.
     * @param matchResultId match result ID
     * @return true if feedback exists
     */
    boolean existsByMatchResultId(Long matchResultId);
}