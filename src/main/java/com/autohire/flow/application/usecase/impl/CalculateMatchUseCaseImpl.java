package com.autohire.flow.application.usecase.impl;

import com.autohire.flow.application.port.incoming.CalculateMatchUseCase;
import com.autohire.flow.application.port.outgoing.JobPostingPort;
import com.autohire.flow.application.port.outgoing.MatchResultPort;
import com.autohire.flow.application.port.outgoing.ResumePort;
import com.autohire.flow.common.util.ValidationUtil;
import com.autohire.flow.domain.exception.JobNotFoundException;
import com.autohire.flow.domain.exception.MatchCalculationException;
import com.autohire.flow.domain.exception.ResumeNotFoundException;
import com.autohire.flow.domain.model.JobPosting;
import com.autohire.flow.domain.model.MatchResult;
import com.autohire.flow.domain.model.Resume;
import com.autohire.flow.domain.service.MatchEngine;
import com.autohire.flow.domain.valueobjects.MatchScore;
import com.autohire.flow.infrastructure.ai.strategy.KeywordMatchStrategy;
import com.autohire.flow.infrastructure.ai.strategy.SemanticMatchStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;

/**
 * Calculate Match Use Case Implementation
 * 
 * Performs semantic matching between resume and job posting using:
 * 1. Vector embedding cosine similarity
 * 2. Skill matching
 * 3. Experience validation
 * 
 * Uses the domain MatchEngine to orchestrate multiple matching strategies.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CalculateMatchUseCaseImpl implements CalculateMatchUseCase {

    private final ResumePort resumePort;
    private final JobPostingPort jobPostingPort;
    private final ValidationUtil validationUtil;
    private final SemanticMatchStrategy semanticStrategy;
    private final KeywordMatchStrategy keywordStrategy;

    @Override
    @Transactional
    public MatchResult execute(CalculateMatchCommand command) {
        log.info("Calculating match for user: {} and job: {}", command.userId(), command.jobId());

        // Validate input
        validationUtil.validateNotNull(command.userId(), "userId cannot be null");
        validationUtil.validateNotNull(command.jobId(), "jobId cannot be null");

        try {
            // Fetch resume and job posting from ports - handle Optional returns
            Resume resume = resumePort.findByUserId(command.userId())
                    .orElseThrow(() -> new ResumeNotFoundException("Resume not found for user: " + command.userId()));

            JobPosting job = jobPostingPort.findById(command.jobId())
                    .orElseThrow(() -> new JobNotFoundException("Job not found with id: " + command.jobId()));

            // Validate that job is not expired
            if (job.isExpired(Instant.now())) {
                log.warn("Job posting has expired: {}", command.jobId());
                throw new IllegalArgumentException("Job posting has expired");
            }

            // Validate resume is valid
            if (!resume.isValid()) {
                log.warn("Resume is invalid for user: {}", command.userId());
                throw new MatchCalculationException("Resume is invalid or incomplete");
            }

            // Use domain MatchEngine to calculate match score using multiple strategies
            MatchEngine matchEngine = new MatchEngine(semanticStrategy, keywordStrategy);
            MatchScore matchScore = matchEngine.calculateMatchScore(resume, job);

            // Get the score as double and convert to int for the response
            int finalScore = matchScore.getScore() != null ? matchScore.getScore().intValue() : 0;

            // Determine quality level based on score
            String qualityLevel = determineQualityLevel(finalScore);

            // Extract matching and missing skills
            List<String> matchingSkills = getMatchingSkills(resume, job);
            List<String> missingSkills = getMissingSkills(resume, job);

            // Generate human-readable explanation
            String explanation = generateExplanation(finalScore, matchingSkills, missingSkills, matchScore);

            // Return the use case MatchResult record
            return new CalculateMatchUseCase.MatchResult(
                    null,  // matchId will be set by persistence layer
                    command.userId(),
                    command.jobId(),
                    finalScore,  // Use int directly (will auto-box to Integer)
                    qualityLevel,
                    matchingSkills,
                    missingSkills,
                    explanation,
                    "CALCULATED",
                    Instant.now(),
                    Instant.now()
            );

        } catch (ResumeNotFoundException e) {
            log.error("Resume not found for user: {}", command.userId());
            throw new MatchCalculationException("Resume not found for user: " + command.userId(), e);
        } catch (JobNotFoundException e) {
            log.error("Job not found: {}", command.jobId());
            throw new MatchCalculationException("Job not found with id: " + command.jobId(), e);
        } catch (IllegalArgumentException e) {
            log.error("Validation error: {}", e.getMessage());
            throw new MatchCalculationException("Validation error: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Match calculation failed", e);
            throw new MatchCalculationException("Failed to calculate match: " + e.getMessage(), e);
        }
    }

    /**
     * Determine quality level based on score
     */
    private String determineQualityLevel(int score) {
        if (score >= 80)
            return "EXCELLENT";
        if (score >= 60)
            return "GOOD";
        if (score >= 40)
            return "MODERATE";
        return "LOW";
    }

    /**
     * Extract matching skills between resume and job
     */
    private List<String> getMatchingSkills(Resume resume, JobPosting job) {
        Set<String> resumeSkills = new HashSet<>(resume.getSkills() != null ? resume.getSkills() : List.of());
        Set<String> jobSkills = new HashSet<>(job.getRequiredSkills() != null ? job.getRequiredSkills() : List.of());

        return jobSkills.stream()
                .filter(resumeSkills::contains)
                .collect(Collectors.toList());
    }

    /**
     * Extract missing skills from resume for the job
     */
    private List<String> getMissingSkills(Resume resume, JobPosting job) {
        Set<String> resumeSkills = new HashSet<>(resume.getSkills() != null ? resume.getSkills() : List.of());
        Set<String> jobSkills = new HashSet<>(job.getRequiredSkills() != null ? job.getRequiredSkills() : List.of());

        return jobSkills.stream()
                .filter(skill -> !resumeSkills.contains(skill))
                .collect(Collectors.toList());
    }

    /**
     * Generate human-readable explanation for match result
     */
    private String generateExplanation(int score, List<String> matchingSkills,
            List<String> missingSkills, MatchScore matchScore) {
        StringBuilder explanation = new StringBuilder();

        explanation.append("Match Score: ").append(score).append("/100. ");

        // Add strategy breakdown if available
        if (matchScore.getSemanticScore() != null && matchScore.getKeywordScore() != null) {
            explanation.append("Semantic match: ").append(matchScore.getSemanticScore())
                    .append("/100, Keyword match: ").append(matchScore.getKeywordScore())
                    .append("/100. ");
        }

        if (!matchingSkills.isEmpty()) {
            explanation.append("Matching skills: ")
                    .append(String.join(", ", matchingSkills)).append(". ");
        }

        if (!missingSkills.isEmpty()) {
            explanation.append("Missing skills: ")
                    .append(String.join(", ", missingSkills)).append(".");
        }

        if (matchingSkills.isEmpty() && missingSkills.isEmpty()) {
            explanation.append("No specific skills matched or missing.");
        }

        return explanation.toString();
    }
}