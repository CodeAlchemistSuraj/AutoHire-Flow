package com.autohire.flow.infrastructure.persistence.repository;

import com.autohire.flow.infrastructure.persistence.entity.MatchResultEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * JPA Repository for MatchResult entity persistence.
 */
@Repository
public interface JpaMatchResultRepository extends JpaRepository<MatchResultEntity, Long> {
    
    Optional<MatchResultEntity> findByUserIdAndJobId(Long userId, Long jobId);
    
    List<MatchResultEntity> findByUserId(Long userId);
    
    List<MatchResultEntity> findByUserIdAndStatus(Long userId, String status);
    
    @Query("""
        SELECT m FROM MatchResultEntity m 
        WHERE m.userId = :userId AND m.score >= :minScore 
        ORDER BY m.score DESC
        """)
    List<MatchResultEntity> findHighScoringMatches(Long userId, Double minScore);
}
