package com.autohire.flow.infrastructure.persistence.repository;

import com.autohire.flow.infrastructure.persistence.entity.JobPostingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * JPA Repository for JobPosting entity persistence.
 */
@Repository
public interface JpaJobPostingRepository extends JpaRepository<JobPostingEntity, Long> {
    
    List<JobPostingEntity> findByTitleContainingIgnoreCase(String title);
    
    List<JobPostingEntity> findByCompanyContainingIgnoreCase(String company);
    
    List<JobPostingEntity> findByLocation(String location);
    
    @Query("""
        SELECT j FROM JobPostingEntity j 
        WHERE j.expiresAt IS NULL OR j.expiresAt > CURRENT_TIMESTAMP
        ORDER BY j.createdAt DESC
        """)
    List<JobPostingEntity> findActiveJobs();
}
