package com.autohire.flow.application.dto.response;

import lombok.*;
import java.util.List;

/**
 * DTO for resume upload response.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
public class ResumeUploadResponse {
    
    private Long resumeId;
    
    private String originalFilename;
    
    private Integer parsedSkillsCount;
    
    private String status;  // SUCCESS, PARTIAL, FAILED
    
    private List<String> extractedSkills;
    
    private Integer experienceCount;
    
    private Integer educationCount;
    
    // Constructor for basic response (used in upload)
    public ResumeUploadResponse(Long resumeId, Integer parsedSkillsCount, String status, List<String> extractedSkills) {
        this.resumeId = resumeId;
        this.parsedSkillsCount = parsedSkillsCount;
        this.status = status;
        this.extractedSkills = extractedSkills;
        this.originalFilename = null;
        this.experienceCount = null;
        this.educationCount = null;
    }
    
    // Constructor for full response (used in get)
    public ResumeUploadResponse(Long resumeId, String originalFilename, Integer parsedSkillsCount, 
                                String status, List<String> extractedSkills, 
                                Integer experienceCount, Integer educationCount) {
        this.resumeId = resumeId;
        this.originalFilename = originalFilename;
        this.parsedSkillsCount = parsedSkillsCount;
        this.status = status;
        this.extractedSkills = extractedSkills;
        this.experienceCount = experienceCount;
        this.educationCount = educationCount;
    }
}