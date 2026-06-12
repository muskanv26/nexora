package com.nexora.roadmap.dto;

import com.nexora.roadmap.entity.DifficultyLevel;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Data Transfer Object representing a Roadmap.
 * Implemented as an immutable Java Record.
 */
public record RoadmapDto(
        UUID id,
        String title,
        String description,
        DifficultyLevel difficultyLevel,
        Integer estimatedWeeks,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<MilestoneDto> milestones
) {
}
