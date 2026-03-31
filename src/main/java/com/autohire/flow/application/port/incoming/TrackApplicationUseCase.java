package com.autohire.flow.application.port.incoming;

/**
 * Use case for tracking job applications.
 */
public interface TrackApplicationUseCase {
    
    /**
     * Tracks application status for a job.
     * @param command tracking command with status update
     * @return TrackingResult with updated status
     */
    TrackingResult execute(TrackingCommand command);
    
    record TrackingCommand(
        Long userId,
        Long jobId,
        String status,  // APPLIED, REJECTED, INTERVIEW
        String notes
    ) {}
    
    record TrackingResult(
        Long applicationId,
        String status,
        java.time.Instant updatedAt
    ) {}
}
