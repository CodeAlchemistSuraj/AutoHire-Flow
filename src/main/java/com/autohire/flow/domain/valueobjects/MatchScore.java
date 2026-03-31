package com.autohire.flow.domain.valueobjects;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Value object representing match score with boundaries
 */
@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MatchScore {
    
    private Double score;  // 0.0 to 100.0
    private Integer semanticScore;  // 0 to 100
    private Integer keywordScore;  // 0 to 100
    
    public static MatchScore of(Double score) {
        if (score < 0 || score > 100) {
            throw new IllegalArgumentException("Score must be between 0 and 100");
        }
        return MatchScore.builder()
            .score(score)
            .semanticScore(0)
            .keywordScore(0)
            .build();
    }
    
    public boolean isHighMatch() {
        return score >= 75;
    }
    
    public boolean isMediumMatch() {
        return score >= 50 && score < 75;
    }
    
    public boolean isLowMatch() {
        return score < 50;
    }
}
