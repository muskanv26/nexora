package com.nexora.roadmap.dto.response;

import com.nexora.roadmap.entity.DifficultyLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO carrying the complete response details for generated roadmaps.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenerateRoadmapResponse {
    private String generatedForGoal;
    private DifficultyLevel difficultyLevel;
    private Integer timelineMonths;
    private String roadmapTitle;
    private List<GenerateMilestoneResponse> milestones;
}
