package com.nexora.roadmap.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <h3>Purpose</h3>
 * DTO carrying the data payload required to update an existing Milestone.
 *
 * <h3>Flow</h3>
 * 1. Received as a JSON payload in the PUT request body `/api/v1/milestones/{id}`.<br/>
 * 2. Deserialized and validated by Spring MVC using JSR-380 annotations.<br/>
 * 3. Passed to the MilestoneService.updateMilestone method.<br/>
 * 4. Merged with the existing Milestone entity state by the MilestoneMapper.
 *
 * <h3>Testing Approach</h3>
 * 1. Unit testing with JSR-380 Validator (ValidatorFactory) to verify validation rule violations on invalid inputs.<br/>
 * 2. Integration testing using MockMvc to check that invalid updates trigger BAD_REQUEST (400).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateMilestoneRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 150, message = "Title cannot exceed 150 characters")
    private String title;

    private String description;

    @NotNull(message = "Sequence order is required")
    @Min(value = 0, message = "Sequence order must be 0 or greater")
    private Integer sequenceOrder;
}
