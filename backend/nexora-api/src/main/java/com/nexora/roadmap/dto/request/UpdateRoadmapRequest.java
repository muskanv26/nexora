package com.nexora.roadmap.dto.request;

import com.nexora.roadmap.entity.DifficultyLevel;
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
 * DTO carrying the data payload required to update an existing Roadmap.
 *
 * <h3>Flow</h3>
 * 1. Received as a JSON payload in the PUT request body with an ID in the path variable.<br/>
 * 2. Deserialized and validated by Spring MVC using JSR-380 annotations.<br/>
 * 3. Passed to the RoadmapService.updateRoadmap method.<br/>
 * 4. Merged with the existing Roadmap entity state by the RoadmapMapper.
 *
 * <h3>Testing Approach</h3>
 * 1. Unit testing with JSR-380 Validator (ValidatorFactory) to verify validation rule violations on invalid inputs.<br/>
 * 2. Integration testing using MockMvc to ensure a invalid body on PUT endpoint triggers a BAD_REQUEST (400) response.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRoadmapRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 150, message = "Title cannot exceed 150 characters")
    private String title;

    private String description;

    @NotNull(message = "Difficulty level is required")
    private DifficultyLevel difficultyLevel;

    @NotNull(message = "Estimated weeks is required")
    @Min(value = 1, message = "Estimated weeks must be greater than 0")
    private Integer estimatedWeeks;
}
