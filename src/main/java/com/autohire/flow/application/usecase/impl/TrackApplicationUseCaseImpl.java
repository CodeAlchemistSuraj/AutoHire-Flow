package com.autohire.flow.application.usecase.impl;

import com.autohire.flow.application.port.incoming.TrackApplicationUseCase;
import com.autohire.flow.application.port.outgoing.MatchResultPort;
import com.autohire.flow.common.constant.AppConstants;
import com.autohire.flow.domain.model.MatchResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

/**
 * Track Application Use Case Implementation
 * 
 * Handles application status tracking with validation of status transitions
 * and audit logging.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TrackApplicationUseCaseImpl implements TrackApplicationUseCase {

    private final MatchResultPort matchResultPort;

    private static final List<String> VALID_STATUSES = Arrays.asList(
        "PENDING",
        "APPLIED",
        "REJECTED",
        "INTERVIEW",
        "OFFER",
        "ACCEPTED"
    );

    @Override
    @Transactional
    public TrackingResult trackApplication(TrackingCommand command) {
        log.info("Tracking application for user: {} and job: {} with status: {}", 
            command.userId(), command.jobId(), command.status());
        
        // Validate status
        validateStatus(command.status());
        
        try {
            // Find existing match result
            var existingMatch = matchResultPort.findByUserAndJob(command.userId(), command.jobId());
            
            MatchResult matchResult;
            if (existingMatch.isPresent()) {
                matchResult = existingMatch.get();
                
                // Validate status transition
                validateStatusTransition(matchResult.getStatus(), command.status());
                
                // Update status and notes
                matchResult.setStatus(command.status());
                matchResult.setUpdatedAt(Instant.now());
                
                matchResult = matchResultPort.update(matchResult);
                
                log.info("Application status updated for user: {} and job: {} to status: {}", 
                    command.userId(), command.jobId(), command.status());
                
            } else {
                // Create new match result if doesn't exist
                matchResult = new MatchResult(
                    null,
                    command.userId(),
                    command.jobId(),
                    0 // Default score
                );
                matchResult.setStatus(command.status());
                matchResult.setCreatedAt(Instant.now());
                matchResult.setUpdatedAt(Instant.now());
                
                matchResult = matchResultPort.save(matchResult);
                
                log.info("New application tracked for user: {} and job: {} with status: {}", 
                    command.userId(), command.jobId(), command.status());
            }
            
            return new TrackingResult(
                matchResult.getId(),
                command.userId(),
                command.jobId(),
                command.status(),
                matchResult.getUpdatedAt()
            );
            
        } catch (IllegalArgumentException e) {
            log.warn("Application tracking validation failed: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Application tracking failed for user: {} and job: {}", 
                command.userId(), command.jobId(), e);
            throw new RuntimeException("Failed to track application: " + e.getMessage(), e);
        }
    }

    /**
     * Validate application status
     */
    private void validateStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException("Status cannot be empty");
        }
        
        if (!VALID_STATUSES.contains(status)) {
            throw new IllegalArgumentException(
                "Invalid status: " + status + 
                ". Valid statuses: " + String.join(", ", VALID_STATUSES)
            );
        }
    }

    /**
     * Validate status transition is allowed
     */
    private void validateStatusTransition(String currentStatus, String newStatus) {
        if (currentStatus == null || currentStatus.isEmpty()) {
            return; // First time setting status
        }
        
        // Define allowed transitions
        boolean isAllowed = isValidTransition(currentStatus, newStatus);
        
        if (!isAllowed) {
            log.warn("Invalid status transition from {} to {}", currentStatus, newStatus);
            throw new IllegalArgumentException(
                "Cannot transition from " + currentStatus + " to " + newStatus
            );
        }
    }

    /**
     * Check if status transition is valid
     */
    private boolean isValidTransition(String from, String to) {
        // If status is the same, allow
        if (from.equals(to)) {
            return true;
        }
        
        // Define allowed state transitions
        return switch (from) {
            case "PENDING" -> to.equals("APPLIED") || to.equals("REJECTED");
            case "APPLIED" -> to.equals("INTERVIEW") || to.equals("REJECTED");
            case "INTERVIEW" -> to.equals("OFFER") || to.equals("REJECTED");
            case "OFFER" -> to.equals("ACCEPTED") || to.equals("REJECTED");
            case "REJECTED" -> to.equals("PENDING"); // Allow reapply
            case "ACCEPTED" -> false; // Final state
            default -> false;
        };
    }
}
