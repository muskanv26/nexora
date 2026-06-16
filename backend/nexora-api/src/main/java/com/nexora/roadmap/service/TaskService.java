package com.nexora.roadmap.service;

import com.nexora.roadmap.dto.request.CreateTaskRequest;
import com.nexora.roadmap.dto.request.UpdateTaskRequest;
import com.nexora.roadmap.dto.response.TaskResponse;

import java.util.List;
import java.util.UUID;

/**
 * <h3>Purpose</h3>
 * Business service layer interface defining Task lifecycle operations.
 *
 * <h3>Flow</h3>
 * Coordinates business flow inputs from {@link com.nexora.roadmap.controller.TaskController} and
 * directs them to the database repositories via service implementation mappings.
 *
 * <h3>Testing Approach</h3>
 * Mock behavior checked in service implementation unit tests and controller integration tests.
 */
public interface TaskService {

    /**
     * Create a new Task linked to a specific Milestone.
     *
     * @param milestoneId The identifier of the parent Milestone.
     * @param request     The Task parameters.
     * @return The created Task details.
     */
    TaskResponse createTask(UUID milestoneId, CreateTaskRequest request);

    /**
     * Fetch a Task by its identifier.
     *
     * @param id The Task UUID.
     * @return The found Task details.
     */
    TaskResponse getTaskById(UUID id);

    /**
     * Fetch all Tasks associated with a specific Milestone.
     *
     * @param milestoneId The parent Milestone UUID.
     * @return A list of Task details.
     */
    List<TaskResponse> getTasksByMilestoneId(UUID milestoneId);

    /**
     * Update an existing Task.
     *
     * @param id      The Task UUID.
     * @param request The updated parameters.
     * @return The updated Task details.
     */
    TaskResponse updateTask(UUID id, UpdateTaskRequest request);

    /**
     * Delete a Task by its identifier.
     *
     * @param id The Task UUID to delete.
     */
    void deleteTask(UUID id);
}
