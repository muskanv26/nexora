package com.nexora.roadmap.service.impl;

import com.nexora.common.exception.ApiException;
import com.nexora.roadmap.dto.request.CreateTaskRequest;
import com.nexora.roadmap.dto.request.UpdateTaskRequest;
import com.nexora.roadmap.dto.response.TaskResponse;
import com.nexora.roadmap.entity.Milestone;
import com.nexora.roadmap.entity.Task;
import com.nexora.roadmap.mapper.TaskMapper;
import com.nexora.roadmap.repository.MilestoneRepository;
import com.nexora.roadmap.repository.TaskRepository;
import com.nexora.roadmap.service.TaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * <h3>Purpose</h3>
 * Concrete implementation of the TaskService business layer.
 * Contains transactional logic, queries repository, maps models, and validates entities presence.
 *
 * <h3>Flow</h3>
 * 1. Coordinates with {@link TaskRepository} and {@link MilestoneRepository} to query and persist entities.<br/>
 * 2. Uses {@link TaskMapper} to convert request payloads to entities and entities to response payloads.<br/>
 * 3. Enforces entity validation check and raises {@link ApiException} if a lookup fails.
 *
 * <h3>Testing Approach</h3>
 * 1. Mockito Unit Testing: mock the repository and mapper dependencies, verify save/delete invocations,
 *    and assert that exceptions are raised for unknown IDs.<br/>
 * 2. Integration Testing: verify real database transactions, check generated schema mappings, and ensure
 *    JPA auditing fields populate.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final MilestoneRepository milestoneRepository;
    private final TaskMapper taskMapper;

    @Override
    @Transactional
    public TaskResponse createTask(UUID milestoneId, CreateTaskRequest request) {
        log.info("Attempting to create a Task for Milestone ID: {}", milestoneId);

        Milestone milestone = milestoneRepository.findById(milestoneId)
                .orElseThrow(() -> {
                    log.warn("Task creation failed: Milestone ID {} not found", milestoneId);
                    return new ApiException(
                            "Milestone not found with id " + milestoneId,
                            "MILESTONE_NOT_FOUND",
                            HttpStatus.NOT_FOUND
                    );
                });

        Task task = taskMapper.toEntity(request);
        task.setMilestone(milestone);

        Task savedTask = taskRepository.save(task);
        log.info("Task created successfully with ID: {} under Milestone ID: {}", savedTask.getId(), milestoneId);

        return taskMapper.toResponse(savedTask);
    }

    @Override
    @Transactional(readOnly = true)
    public TaskResponse getTaskById(UUID id) {
        log.info("Fetching Task with ID: {}", id);

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Task lookup failed: ID {} not found", id);
                    return new ApiException(
                            "Task not found with id " + id,
                            "TASK_NOT_FOUND",
                            HttpStatus.NOT_FOUND
                    );
                });

        return taskMapper.toResponse(task);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponse> getTasksByMilestoneId(UUID milestoneId) {
        log.info("Fetching all Tasks for Milestone ID: {}", milestoneId);

        if (!milestoneRepository.existsById(milestoneId)) {
            log.warn("Task query failed: Milestone ID {} not found", milestoneId);
            throw new ApiException(
                    "Milestone not found with id " + milestoneId,
                    "MILESTONE_NOT_FOUND",
                    HttpStatus.NOT_FOUND
            );
        }

        return taskRepository.findByMilestoneId(milestoneId).stream()
                .map(taskMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TaskResponse updateTask(UUID id, UpdateTaskRequest request) {
        log.info("Attempting to update Task with ID: {}", id);

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Task update failed: ID {} not found", id);
                    return new ApiException(
                            "Task not found with id " + id,
                            "TASK_NOT_FOUND",
                            HttpStatus.NOT_FOUND
                    );
                });

        taskMapper.updateEntity(request, task);
        Task updatedTask = taskRepository.save(task);

        log.info("Task with ID {} updated successfully", id);
        return taskMapper.toResponse(updatedTask);
    }

    @Override
    @Transactional
    public void deleteTask(UUID id) {
        log.info("Attempting to delete Task with ID: {}", id);

        if (!taskRepository.existsById(id)) {
            log.warn("Task deletion failed: ID {} not found", id);
            throw new ApiException(
                    "Task not found with id " + id,
                    "TASK_NOT_FOUND",
                    HttpStatus.NOT_FOUND
            );
        }

        taskRepository.deleteById(id);
        log.info("Task with ID {} deleted successfully", id);
    }
}
