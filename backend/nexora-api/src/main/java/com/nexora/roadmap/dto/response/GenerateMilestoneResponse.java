package com.nexora.roadmap.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO representing an AI-generated learning milestone.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenerateMilestoneResponse {
    private String milestoneTitle;
    private String milestoneDescription;
    private List<GenerateTaskResponse> tasks;
}
