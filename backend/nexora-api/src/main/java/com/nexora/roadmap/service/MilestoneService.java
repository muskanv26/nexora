package com.nexora.roadmap.service;

import com.nexora.roadmap.dto.request.CreateMilestoneRequest;
import com.nexora.roadmap.dto.request.UpdateMilestoneRequest;
import com.nexora.roadmap.dto.response.MilestoneResponse;

import java.util.List;
import java.util.UUID;

/**
 * <h3>Purpose</h3>
 * Business service layer interface defining Milestone operations.
 *
 * <h3>Flow</h3>
 * Coordinates business flow inputs from {@link com.nexora.roadmap.controller.MilestoneController} and
 * directs them to the database repositories via service implementation mappings.
 *
 * <h3>Testing Approach</h3>
 * Mock behavior checked in service implementation unit tests and controller integration tests.
 */
public interface MilestoneService {

    /**
     * Create a new Milestone linked to a specific Roadmap.
     *
     * @param roadmapId The identifier of the parent Roadmap.
     * @param request   The Milestone parameters.
     * @return The created Milestone details.
     */
    MilestoneResponse createMilestone(UUID roadmapId, CreateMilestoneRequest request);

    /**
     * Fetch a Milestone by its identifier.
     *
     * @param id The Milestone UUID.
     * @return The found Milestone details.
     */
    MilestoneResponse getMilestoneById(UUID id);

    /**
     * Fetch all Milestones associated with a specific Roadmap, sorted by sequence order.
     *
     * @param roadmapId The parent Roadmap UUID.
     * @return A list of Milestone details.
     */
    List<MilestoneResponse> getMilestonesByRoadmapId(UUID roadmapId);

    /**
     * Update an existing Milestone.
     *
     * @param id      The Milestone UUID.
     * @param request The updated parameters.
     * @return The updated Milestone details.
     */
    MilestoneResponse updateMilestone(UUID id, UpdateMilestoneRequest request);

    /**
     * Delete a Milestone by its identifier.
     *
     * @param id The Milestone UUID to delete.
     */
    void deleteMilestone(UUID id);
}
