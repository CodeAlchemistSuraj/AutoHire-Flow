package com.autohire.flow.application.port.outgoing;

import com.autohire.flow.domain.model.MatchResult;
import java.util.List;
import java.util.Optional;

/**
 * Output port for match result persistence operations.
 */
public interface MatchResultPort {
    
    /**
     * Persists a match result.
     * @param matchResult match to save
     * @return saved match with generated ID
     */
    MatchResult save(MatchResult matchResult);
    
    /**
     * Finds match by user and job.
     * @param userId user ID
     * @param jobId job ID
     * @return Optional containing match if found
     */
    Optional<MatchResult> findByUserAndJob(Long userId, Long jobId);
    
    /**
     * Finds match by ID.
     * @param matchId match ID
     * @return Optional containing match if found
     */
    Optional<MatchResult> findById(Long matchId);
    
    /**
     * Finds all matches for a user.
     * @param userId user ID
     * @return list of user's matches
     */
    List<MatchResult> findByUserId(Long userId);
    
    /**
     * Finds matches for a user by status.
     * @param userId user ID
     * @param status match status
     * @return list of matches with that status
     */
    List<MatchResult> findByUserIdAndStatus(Long userId, String status);
    
    /**
     * Updates a match result.
     * @param matchResult match with updated fields
     * @return updated match
     */
    MatchResult update(MatchResult matchResult);
    
    /**
     * Finds high-scoring matches for a user.
     * @param userId user ID
     * @param minScore minimum score threshold
     * @return list of high-scoring matches
     */
    List<MatchResult> findHighScoringMatches(Long userId, Double minScore);
}
