package com.autohire.flow.infrastructure.persistence.repository;

import com.autohire.flow.infrastructure.persistence.entity.CoverLetterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaCoverLetterRepository extends JpaRepository<CoverLetterEntity, UUID> {
    
    Optional<CoverLetterEntity> findByUserIdAndJobPostingId(UUID userId, UUID jobPostingId);
    
    List<CoverLetterEntity> findByUserId(UUID userId);
    
    List<CoverLetterEntity> findByUserIdAndStatus(UUID userId, CoverLetterEntity.CoverLetterStatus status);
    
    void deleteByUserIdAndJobPostingId(UUID userId, UUID jobPostingId);
}