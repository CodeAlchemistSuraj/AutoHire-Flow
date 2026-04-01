package com.autohire.flow.infrastructure.persistence.repository;

import com.autohire.flow.infrastructure.persistence.entity.JobPostingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

/**
 * JPA Repository for JobPosting entity persistence.
 */
@Repository
public interface JpaJobPostingRepository extends JpaRepository<JobPostingEntity, Long> {
    
    List<JobPostingEntity> findByTitleContainingIgnoreCase(String title);
    
    List<JobPostingEntity> findByCompanyContainingIgnoreCase(String company);
    
    // Alias for findByCompanyContainingIgnoreCase
    default List<JobPostingEntity> findByCompanyNameContainingIgnoreCase(String company) {
        return findByCompanyContainingIgnoreCase(company);
    }
    
    List<JobPostingEntity> findByLocationContainingIgnoreCase(String location);
    
    @Query("""
        SELECT j FROM JobPostingEntity j 
        WHERE j.expiresAt IS NULL OR j.expiresAt > :now
        ORDER BY j.createdAt DESC
        """)
    List<JobPostingEntity> findAllActiveJobs(@Param("now") Instant now);
}