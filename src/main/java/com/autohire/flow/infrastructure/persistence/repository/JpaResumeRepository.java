package com.autohire.flow.infrastructure.persistence.repository;

import com.autohire.flow.infrastructure.persistence.entity.ResumeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * JPA Repository for Resume entity persistence.
 */
@Repository
public interface JpaResumeRepository extends JpaRepository<ResumeEntity, Long> {
    
    Optional<ResumeEntity> findByUserId(Long userId);
    
    boolean existsByUserId(Long userId);
}
