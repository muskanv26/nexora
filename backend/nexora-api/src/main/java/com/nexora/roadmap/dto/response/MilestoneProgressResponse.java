package com.nexora.roadmap.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * <h3>Purpose</h3>
 * DTO representing the progress tracking metrics of a Milestone.
 *
 * <h3>Flow</h3>
 * 1. Generated in the service layer using direct task queries.<br/>
 * 2. Sent by the service layer back to the controller.<br/>
 * 3. Wrapped in the standard ApiResponse and returned as JSON.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MilestoneProgressResponse {
    private UUID milestoneId;
    private String milestoneTitle;
    private Long totalTasks;
    private Long completedTasks;
    private Double progressPercentage;
}
