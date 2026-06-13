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
 * DTO carrying the data payload required to create a new Milestone.
 *
 * <h3>Flow</h3>
 * 1. Received as a JSON payload in the POST request body `/api/v1/roadmaps/{roadmapId}/milestones`.<br/>
 * 2. Deserialized and validated by Spring MVC using JSR-380 annotations.<br/>
 * 3. Passed to the MilestoneService.createMilestone method.<br/>
 * 4. Mapped to the Milestone entity by the MilestoneMapper.
 *
 * <h3>Testing Approach</h3>
 * 1. Unit testing with JSR-380 Validator (ValidatorFactory) to assert validation rule violations on blank title or negative sequence order.<br/>
 * 2. Integration testing using MockMvc to check that invalid body triggers BAD_REQUEST (400).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateMilestoneRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 150, message = "Title cannot exceed 150 characters")
    private String title;

    private String description;

    @NotNull(message = "Sequence order is required")
    @Min(value = 0, message = "Sequence order must be 0 or greater")
    private Integer sequenceOrder;
}
