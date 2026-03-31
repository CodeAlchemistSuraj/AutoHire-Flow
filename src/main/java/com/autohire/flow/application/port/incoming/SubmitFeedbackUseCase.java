package com.autohire.flow.application.port.incoming;

/**
 * Use case for submitting feedback on matches.
 */
public interface SubmitFeedbackUseCase {
    
    /**
     * Submits feedback for a match result.
     * @param command feedback submission command
     * @return FeedbackResult with submission confirmation
     */
    FeedbackResult execute(FeedbackCommand command);
    
    record FeedbackCommand(
        Long userId,
        Long matchResultId,
        String feedbackType,  // TOO_SENIOR, NOT_INTERESTED, SALARY_LOW, OTHER
        String comments
    ) {}
    
    record FeedbackResult(
        Long feedbackId,
        String message,
        java.time.Instant submittedAt
    ) {}
}
