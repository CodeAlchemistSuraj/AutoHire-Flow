package com.autohire.flow.application.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

/**
 * DTO for application tracking request.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TrackApplicationRequest {
    
    @NotNull(message = "Job ID is required")
    @Positive(message = "Job ID must be positive")
    private Long jobId;
    
    @NotBlank(message = "Status is required")
    @Pattern(
        regexp = "APPLIED|REJECTED|INTERVIEW",
        message = "Status must be APPLIED, REJECTED, or INTERVIEW"
    )
    private String status;
    
    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    private String notes;
}
