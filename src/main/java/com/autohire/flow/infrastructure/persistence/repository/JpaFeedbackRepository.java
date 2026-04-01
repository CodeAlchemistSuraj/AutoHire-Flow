package com.autohire.flow.infrastructure.persistence.repository;

import com.autohire.flow.infrastructure.persistence.entity.FeedbackEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for Feedback entities.
 * Provides database access for feedback persistence.
 */
@Repository
public interface JpaFeedbackRepository extends JpaRepository<FeedbackEntity, Long> {
}
