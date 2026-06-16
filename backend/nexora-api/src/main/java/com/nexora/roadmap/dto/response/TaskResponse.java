package com.nexora.roadmap.dto.response;

import com.nexora.roadmap.entity.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * <h3>Purpose</h3>
 * DTO defining the response structure returned to clients when querying Tasks.
 *
 * <h3>Flow</h3>
 * 1. Generated in the TaskMapper when mapping a Task entity to a response.<br/>
 * 2. Sent by the service layer back to the controller.<br/>
 * 3. Enveloped within the standard ApiResponse and serialized to JSON.
 *
 * <h3>Testing Approach</h3>
 * 1. Unit testing the mapping from entity to response DTO to verify correct field copies.<br/>
 * 2. Integration testing verifying the JSON structure serializes with all correct fields.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponse {
    private UUID id;
    private String title;
    private String description;
    private TaskStatus status;
    private Integer estimatedHours;
    private UUID milestoneId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
