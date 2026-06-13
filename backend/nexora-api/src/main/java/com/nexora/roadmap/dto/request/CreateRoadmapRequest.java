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
 * DTO carrying the data payload required to create a new Roadmap.
 *
 * <h3>Flow</h3>
 * 1. Received as a JSON payload in the POST request body.<br/>
 * 2. Deserialized and validated by Spring MVC using JSR-380 annotations.<br/>
 * 3. Passed to the RoadmapService.createRoadmap method.<br/>
 * 4. Mapped to the Roadmap entity by the RoadmapMapper.
 *
 * <h3>Testing Approach</h3>
 * 1. Unit testing with JSR-380 Validator (ValidatorFactory) to assert that blank titles, titles exceeding 150 characters,
 *    and non-positive estimated weeks generate appropriate validation constraints violations.<br/>
 * 2. Integration testing using MockMvc checking that invalid payloads result in a BAD_REQUEST (400) response.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateRoadmapRequest {

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
