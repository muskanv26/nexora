package com.nexora.roadmap.dto.request;

import com.nexora.roadmap.entity.TaskStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * <h3>Purpose</h3>
 * DTO carrying the data payload required to update an existing Task.
 *
 * <h3>Flow</h3>
 * 1. Received as a JSON payload in the PUT request body `/api/v1/tasks/{id}`.<br/>
 * 2. Deserialized and validated by Spring MVC using JSR-380 annotations.<br/>
 * 3. Passed to the TaskService.updateTask method.<br/>
 * 4. Merged with the existing Task entity state by the TaskMapper.
 *
 * <h3>Testing Approach</h3>
 * 1. Unit testing with JSR-380 Validator (ValidatorFactory) to verify validation rule violations on invalid inputs.<br/>
 * 2. Integration testing using MockMvc to check that invalid updates trigger BAD_REQUEST (400).
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTaskRequest {

    /**
     * The title of the task.
     */
    @NotBlank(message = "Title is required")
    @Size(max = 150, message = "Title cannot exceed 150 characters")
    private String title;

    /**
     * The detailed description of the task.
     */
    private String description;

    /**
     * The progression status of the task.
     */
    @NotNull(message = "Status is required")
    private TaskStatus status;

    /**
     * The estimated number of hours required to complete the task.
     */
    @NotNull(message = "Estimated hours is required")
    @Min(value = 0, message = "Estimated hours must be 0 or greater")
    private Integer estimatedHours;
}
