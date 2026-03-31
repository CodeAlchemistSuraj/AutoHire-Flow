package com.autohire.flow.application.port.outgoing;

import com.autohire.flow.domain.model.CoverLetter;
import java.util.Optional;

/**
 * Output port for cover letter persistence operations.
 */
public interface CoverLetterPort {
    
    /**
     * Persists a cover letter.
     * @param coverLetter cover letter to save
     * @return saved cover letter with generated ID
     */
    CoverLetter save(CoverLetter coverLetter);
    
    /**
     * Finds cover letter by user and job.
     * @param userId user ID
     * @param jobId job ID
     * @return Optional containing cover letter if found
     */
    Optional<CoverLetter> findByUserAndJob(Long userId, Long jobId);
    
    /**
     * Finds cover letter by ID.
     * @param coverLetterId cover letter ID
     * @return Optional containing cover letter if found
     */
    Optional<CoverLetter> findById(Long coverLetterId);
    
    /**
     * Updates a cover letter.
     * @param coverLetter cover letter with updated fields
     * @return updated cover letter
     */
    CoverLetter update(CoverLetter coverLetter);
    
    /**
     * Deletes a cover letter.
     * @param coverLetterId ID of cover letter to delete
     */
    void delete(Long coverLetterId);
}
