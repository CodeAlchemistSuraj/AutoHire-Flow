package com.autohire.flow.application.port.outgoing;

import com.autohire.flow.domain.model.Resume;
import java.util.Optional;

/**
 * Output port for resume persistence operations.
 */
public interface ResumePort {
    
    /**
     * Persists a resume.
     * @param resume resume to save
     * @return saved resume with generated ID
     */
    Resume save(Resume resume);
    
    /**
     * Finds resume by user ID.
     * @param userId user ID
     * @return Optional containing resume if found
     */
    Optional<Resume> findByUserId(Long userId);
    
    /**
     * Finds resume by ID.
     * @param resumeId resume ID
     * @return Optional containing resume if found
     */
    Optional<Resume> findById(Long resumeId);
    
    /**
     * Deletes a resume.
     * @param resumeId resume ID to delete
     */
    void delete(Long resumeId);
    
    /**
     * Checks if resume exists for user.
     * @param userId user ID
     * @return true if exists, false otherwise
     */
    boolean existsByUserId(Long userId);
    
    /**
     * Updates a resume.
     * @param resume resume with updated fields
     * @return updated resume
     */
    Resume update(Resume resume);
}
