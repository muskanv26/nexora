package com.nexora.roadmap.mapper;

import com.nexora.roadmap.dto.request.CreateRoadmapRequest;
import com.nexora.roadmap.dto.request.UpdateRoadmapRequest;
import com.nexora.roadmap.dto.response.RoadmapResponse;
import com.nexora.roadmap.entity.Roadmap;
import org.springframework.stereotype.Component;

/**
 * <h3>Purpose</h3>
 * A manual mapper component translating between Roadmap Entities and Request/Response DTOs.
 * Avoiding third-party mapper reflection overhead ensures fast compilation and maximum performance.
 *
 * <h3>Flow</h3>
 * 1. <b>toEntity</b>: Converts incoming CreateRoadmapRequest into a new unpersisted Roadmap entity.<br/>
 * 2. <b>toResponse</b>: Transforms a persisted database entity into a clean RoadmapResponse payload.<br/>
 * 3. <b>updateEntity</b>: Overwrites fields of an active Roadmap entity with fields from UpdateRoadmapRequest.
 *
 * <h3>Testing Approach</h3>
 * 1. Unit tests confirming field alignment (e.g. title is copied correctly).<br/>
 * 2. Assert that null inputs safely return null without throwing NullPointerExceptions.
 */
@Component
public class RoadmapMapper {

    /**
     * Map a {@link Roadmap} entity into a {@link RoadmapResponse} DTO.
     *
     * @param roadmap The database entity.
     * @return The response DTO payload, or null if input is null.
     */
    public RoadmapResponse toResponse(Roadmap roadmap) {
        if (roadmap == null) {
            return null;
        }
        return RoadmapResponse.builder()
                .id(roadmap.getId())
                .title(roadmap.getTitle())
                .description(roadmap.getDescription())
                .difficultyLevel(roadmap.getDifficultyLevel())
                .estimatedWeeks(roadmap.getEstimatedWeeks())
                .createdAt(roadmap.getCreatedAt())
                .updatedAt(roadmap.getUpdatedAt())
                .build();
    }

    /**
     * Map a {@link CreateRoadmapRequest} DTO into a fresh {@link Roadmap} entity.
     *
     * @param request The create payload.
     * @return The new entity, or null if input is null.
     */
    public Roadmap toEntity(CreateRoadmapRequest request) {
        if (request == null) {
            return null;
        }
        return Roadmap.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .difficultyLevel(request.getDifficultyLevel())
                .estimatedWeeks(request.getEstimatedWeeks())
                .build();
    }

    /**
     * Merges update parameters from {@link UpdateRoadmapRequest} into an existing {@link Roadmap} entity.
     *
     * @param request The update payload.
     * @param roadmap The target entity to update.
     */
    public void updateEntity(UpdateRoadmapRequest request, Roadmap roadmap) {
        if (request == null || roadmap == null) {
            return;
        }
        roadmap.setTitle(request.getTitle());
        roadmap.setDescription(request.getDescription());
        roadmap.setDifficultyLevel(request.getDifficultyLevel());
        roadmap.setEstimatedWeeks(request.getEstimatedWeeks());
    }
}
