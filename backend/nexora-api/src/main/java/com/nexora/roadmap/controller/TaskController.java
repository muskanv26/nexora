package com.nexora.roadmap.controller;

import com.nexora.common.response.ApiResponse;
import com.nexora.roadmap.dto.request.CreateTaskRequest;
import com.nexora.roadmap.dto.request.UpdateTaskRequest;
import com.nexora.roadmap.dto.response.TaskResponse;
import com.nexora.roadmap.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * <h3>Purpose</h3>
 * REST Controller exposing API endpoints for learning Tasks management.
 * Wraps responses inside the standard {@link ApiResponse} contract.
 *
 * <h3>Flow</h3>
 * 1. Resolves and matches HTTP request verbs (POST, GET, PUT, DELETE) and path patterns.<br/>
 * 2. Parses and triggers JSR validation rules via {@code @Valid}.<br/>
 * 3. Delegates logic to {@link TaskService} using constructor-based injection.<br/>
 * 4. Forms and wraps results into ResponseEntity containing {@link ApiResponse}.
 *
 * <h3>Testing Approach</h3>
 * 1. MVC Integration testing using {@code MockMvc} to verify status codes, payload mapping, validation failures,
 *    and HTTP error responses.<br/>
 * 2. End-to-end integration tests hitting endpoints directly against a test database.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Tasks", description = "Operations related to tasks within milestones")
public class TaskController {

    private final TaskService taskService;

    /**
     * Create a new Task under a specific Milestone.
     *
     * @param milestoneId The parent Milestone UUID.
     * @param request     The Task parameters.
     * @return Standard ApiResponse wrapper containing the created Task.
     */
    @PostMapping("/milestones/{milestoneId}/tasks")
    @Operation(summary = "Create a new Task under a Milestone", description = "Creates a new task associated with a parent milestone UUID.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Task created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request body or validation constraints failed"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Parent milestone not found")
    })
    public ResponseEntity<ApiResponse<TaskResponse>> createTask(
            @PathVariable UUID milestoneId,
            @Valid @RequestBody CreateTaskRequest request) {
        log.info("REST request to create a Task under Milestone ID: {}", milestoneId);

        TaskResponse response = taskService.createTask(milestoneId, request);
        return new ResponseEntity<>(ApiResponse.success(response), HttpStatus.CREATED);
    }

    /**
     * Fetch all Tasks for a specific Milestone.
     *
     * @param milestoneId The parent Milestone UUID.
     * @return Standard ApiResponse wrapper containing a list of Tasks.
     */
    @GetMapping("/milestones/{milestoneId}/tasks")
    @Operation(summary = "Get tasks by Milestone ID", description = "Retrieves a list of tasks for a given milestone UUID.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "List of tasks retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Parent milestone not found")
    })
    public ResponseEntity<ApiResponse<List<TaskResponse>>> getTasksByMilestoneId(
            @PathVariable UUID milestoneId) {
        log.info("REST request to fetch all Tasks for Milestone ID: {}", milestoneId);

        List<TaskResponse> response = taskService.getTasksByMilestoneId(milestoneId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Fetch a specific Task by its UUID.
     *
     * @param id The Task UUID.
     * @return Standard ApiResponse wrapper containing the Task.
     */
    @GetMapping("/tasks/{id}")
    @Operation(summary = "Get task by ID", description = "Retrieves a specific task using its UUID.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Task retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Task not found with the given ID")
    })
    public ResponseEntity<ApiResponse<TaskResponse>> getTaskById(
            @PathVariable UUID id) {
        log.info("REST request to fetch Task with ID: {}", id);

        TaskResponse response = taskService.getTaskById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Update an existing Task.
     *
     * @param id      The Task UUID.
     * @param request The updated parameters.
     * @return Standard ApiResponse wrapper containing the updated Task.
     */
    @PutMapping("/tasks/{id}")
    @Operation(summary = "Update task", description = "Updates the details of an existing task using its UUID.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Task updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request body or validation constraints failed"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Task not found with the given ID")
    })
    public ResponseEntity<ApiResponse<TaskResponse>> updateTask(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateTaskRequest request) {
        log.info("REST request to update Task with ID: {}", id);

        TaskResponse response = taskService.updateTask(id, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Delete an existing Task.
     *
     * @param id The Task UUID.
     * @return Standard ApiResponse wrapper indicating success (empty data field).
     */
    @DeleteMapping("/tasks/{id}")
    @Operation(summary = "Delete task", description = "Deletes a specific task by its UUID.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Task deleted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Task not found with the given ID")
    })
    public ResponseEntity<ApiResponse<Void>> deleteTask(
            @PathVariable UUID id) {
        log.info("REST request to delete Task with ID: {}", id);

        taskService.deleteTask(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
