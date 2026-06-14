package com.nexora.roadmap.mapper;

import com.nexora.roadmap.dto.request.CreateMilestoneRequest;
import com.nexora.roadmap.dto.request.UpdateMilestoneRequest;
import com.nexora.roadmap.dto.response.MilestoneResponse;
import com.nexora.roadmap.entity.Milestone;
import org.springframework.stereotype.Component;

/**
 * <h3>Purpose</h3>
 * A manual mapper component translating between Milestone Entities and Request/Response DTOs.
 * Avoiding reflection frameworks preserves fast compilation and run-time optimization.
 *
 * <h3>Flow</h3>
 * 1. <b>toEntity</b>: Maps CreateMilestoneRequest into a fresh Milestone entity.<br/>
 * 2. <b>toResponse</b>: Converts Milestone entity into a MilestoneResponse, including mapping the parent roadmap identifier.<br/>
 * 3. <b>updateEntity</b>: Merges UpdateMilestoneRequest properties into an existing Milestone entity.
 *
 * <h3>Testing Approach</h3>
 * 1. Unit testing verifying field mappings are copied correctly (e.g. title, sequenceOrder).<br/>
 * 2. Assert null inputs are handled gracefully and safely return null.
 */
@Component
public class MilestoneMapper {

    /**
     * Map a {@link Milestone} entity into a {@link MilestoneResponse} DTO.
     *
     * @param milestone The database entity.
     * @return The response DTO payload, or null if input is null.
     */
    public MilestoneResponse toResponse(Milestone milestone) {
        if (milestone == null) {
            return null;
        }
        return MilestoneResponse.builder()
                .id(milestone.getId())
                .title(milestone.getTitle())
                .description(milestone.getDescription())
                .sequenceOrder(milestone.getSequenceOrder())
                .roadmapId(milestone.getRoadmap() != null ? milestone.getRoadmap().getId() : null)
                .createdAt(milestone.getCreatedAt())
                .updatedAt(milestone.getUpdatedAt())
                .build();
    }

    /**
     * Map a {@link CreateMilestoneRequest} DTO into a fresh {@link Milestone} entity.
     *
     * @param request The create payload.
     * @return The new entity, or null if input is null.
     */
    public Milestone toEntity(CreateMilestoneRequest request) {
        if (request == null) {
            return null;
        }
        return Milestone.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .sequenceOrder(request.getSequenceOrder())
                .build();
    }

    /**
     * Merges update parameters from {@link UpdateMilestoneRequest} into an existing {@link Milestone} entity.
     *
     * @param request   The update payload.
     * @param milestone The target entity to update.
     */
    public void updateEntity(UpdateMilestoneRequest request, Milestone milestone) {
        if (request == null || milestone == null) {
            return;
        }
        milestone.setTitle(request.getTitle());
        milestone.setDescription(request.getDescription());
        milestone.setSequenceOrder(request.getSequenceOrder());
    }
}
