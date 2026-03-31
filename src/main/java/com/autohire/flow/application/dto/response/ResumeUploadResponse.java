package com.autohire.flow.application.dto.response;

import lombok.*;
import java.util.List;

/**
 * DTO for resume upload response.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResumeUploadResponse {
    
    private Long resumeId;
    
    private String originalFilename;
    
    private Integer parsedSkillsCount;
    
    private String status;  // SUCCESS, PARTIAL, FAILED
    
    private List<String> extractedSkills;
    
    private Integer experienceCount;
    
    private Integer educationCount;
}
