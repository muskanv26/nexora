package com.nexora.roadmap.dto.request;

import com.nexora.roadmap.entity.DifficultyLevel;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO carrying parameters for AI roadmap generation requests.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenerateRoadmapRequest {

    @NotBlank(message = "Goal is required")
    private String goal;

    private List<String> currentSkills;

    @NotNull(message = "Difficulty level is required")
    private DifficultyLevel difficultyLevel;

    @NotNull(message = "Timeline in months is required")
    @Min(value = 1, message = "Timeline must be at least 1 month")
    @Max(value = 36, message = "Timeline cannot exceed 36 months")
    private Integer timelineMonths;
}
