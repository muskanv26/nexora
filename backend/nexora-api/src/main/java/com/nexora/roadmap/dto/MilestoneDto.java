package com.nexora.roadmap.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Data Transfer Object representing a learning Milestone.
 * Implemented as an immutable Java Record.
 */
public record MilestoneDto(
        UUID id,
        String title,
        String description,
        Integer sequenceOrder,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<TaskDto> tasks
) {
}
