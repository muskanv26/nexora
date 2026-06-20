package com.nexora.roadmap.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO representing an AI-generated learning task description.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenerateTaskResponse {
    private String taskTitle;
    private String taskDescription;
    private Integer estimatedHours;
}
