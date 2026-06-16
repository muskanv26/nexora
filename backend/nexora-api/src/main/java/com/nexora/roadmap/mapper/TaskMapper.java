package com.nexora.roadmap.mapper;

import com.nexora.roadmap.dto.request.CreateTaskRequest;
import com.nexora.roadmap.dto.request.UpdateTaskRequest;
import com.nexora.roadmap.dto.response.TaskResponse;
import com.nexora.roadmap.entity.Task;
import org.springframework.stereotype.Component;

/**
 * <h3>Purpose</h3>
 * A manual mapper component translating between Task Entities and Request/Response DTOs.
 * Avoiding reflection frameworks preserves fast compilation and run-time optimization.
 *
 * <h3>Flow</h3>
 * 1. <b>toEntity</b>: Maps CreateTaskRequest into a fresh Task entity.<br/>
 * 2. <b>toResponse</b>: Converts Task entity into a TaskResponse, including mapping the parent milestone identifier.<br/>
 * 3. <b>updateEntity</b>: Merges UpdateTaskRequest properties into an existing Task entity.
 *
 * <h3>Testing Approach</h3>
 * 1. Unit testing verifying field mappings are copied correctly (e.g. title, status, estimatedHours).<br/>
 * 2. Assert null inputs are handled gracefully and safely return null.
 */
@Component
public class TaskMapper {

    /**
     * Map a {@link Task} entity into a {@link TaskResponse} DTO.
     *
     * @param task The database entity.
     * @return The response DTO payload, or null if input is null.
     */
    public TaskResponse toResponse(Task task) {
        if (task == null) {
            return null;
        }
        return TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .estimatedHours(task.getEstimatedHours())
                .milestoneId(task.getMilestone() != null ? task.getMilestone().getId() : null)
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }

    /**
     * Map a {@link CreateTaskRequest} DTO into a fresh {@link Task} entity.
     *
     * @param request The create payload.
     * @return The new entity, or null if input is null.
     */
    public Task toEntity(CreateTaskRequest request) {
        if (request == null) {
            return null;
        }
        return Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .status(request.getStatus())
                .estimatedHours(request.getEstimatedHours())
                .build();
    }

    /**
     * Merges update parameters from {@link UpdateTaskRequest} into an existing {@link Task} entity.
     *
     * @param request The update payload.
     * @param task    The target entity to update.
     */
    public void updateEntity(UpdateTaskRequest request, Task task) {
        if (request == null || task == null) {
            return;
        }
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus(request.getStatus());
        task.setEstimatedHours(request.getEstimatedHours());
    }
}
