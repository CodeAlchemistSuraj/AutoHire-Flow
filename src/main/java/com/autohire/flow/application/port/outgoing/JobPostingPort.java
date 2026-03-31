package com.autohire.flow.application.port.outgoing;

import com.autohire.flow.domain.model.JobPosting;
import java.util.List;
import java.util.Optional;

/**
 * Output port for job posting persistence operations.
 */
public interface JobPostingPort {
    
    /**
     * Persists a job posting.
     * @param jobPosting job to save
     * @return saved job with generated ID
     */
    JobPosting save(JobPosting jobPosting);
    
    /**
     * Saves multiple job postings in batch.
     * @param jobs list of jobs to save
     * @return list of saved jobs
     */
    List<JobPosting> saveAll(List<JobPosting> jobs);
    
    /**
     * Finds a job by ID.
     * @param jobId job ID
     * @return Optional containing job if found
     */
    Optional<JobPosting> findById(Long jobId);
    
    /**
     * Searches for jobs by title.
     * @param title job title to search
     * @return list of matching jobs
     */
    List<JobPosting> searchByTitle(String title);
    
    /**
     * Searches for jobs by company.
     * @param company company name
     * @return list of jobs from that company
     */
    List<JobPosting> searchByCompany(String company);
    
    /**
     * Finds jobs by location.
     * @param location job location
     * @return list of jobs in that location
     */
    List<JobPosting> findByLocation(String location);
    
    /**
     * Gets active jobs (non-expired).
     * @return list of active jobs
     */
    List<JobPosting> findActiveJobs();
    
    /**
     * Updates a job posting.
     * @param jobPosting job with updated fields
     * @return updated job
     */
    JobPosting update(JobPosting jobPosting);
}
