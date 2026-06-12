package com.nexora.roadmap.dto;

import com.nexora.roadmap.entity.TaskStatus;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Data Transfer Object representing a learning Task.
 * Implemented as an immutable Java Record.
 */
public record TaskDto(
        UUID id,
        String title,
        String description,
        TaskStatus status,
        Integer estimatedHours,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
