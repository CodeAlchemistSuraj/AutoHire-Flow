package com.autohire.flow.application.port.outgoing;

import com.autohire.flow.domain.model.CoverLetter;

import java.util.List;
import java.util.Optional;

public interface CoverLetterPort {
    CoverLetter save(CoverLetter coverLetter);
    Optional<CoverLetter> findByUserAndJob(Long userId, Long jobId);
    Optional<CoverLetter> findById(Long coverLetterId);
    List<CoverLetter> findByUserId(Long userId);
    List<CoverLetter> findByJobId(Long jobId);
    CoverLetter update(CoverLetter coverLetter);
    void delete(Long coverLetterId);
    boolean existsForUserAndJob(Long userId, Long jobId);
}