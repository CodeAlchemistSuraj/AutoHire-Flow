package com.autohire.flow.application.dto.response;

import lombok.*;
import java.util.List;

/**
 * DTO for match score calculation response.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MatchScoreResponse {
    
    private Long matchResultId;
    
    private Double matchScore;  // 0-100
    
    private String qualityLevel;  // EXCELLENT, GOOD, MODERATE, LOW
    
    private List<String> keyMatchingSkills;
    
    private List<String> missingSkills;
    
    private String explanation;
    
    private String jobTitle;
    
    private String company;
}
