package com.nexora.roadmap.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response payload carrying details of the candidate's career readiness evaluation.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CareerReadinessResponse {
    private Integer readinessScore;
    private String readinessLevel;
    private List<String> roleMatchedSkills;
    private List<String> missingSkills;
    private List<String> recommendedNextSkills;
    private List<String> priorityActionPlan;
    private String summary;
}
