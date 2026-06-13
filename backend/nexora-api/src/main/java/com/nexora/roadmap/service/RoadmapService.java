package com.nexora.roadmap.service;

import com.nexora.roadmap.dto.request.CreateRoadmapRequest;
import com.nexora.roadmap.dto.request.UpdateRoadmapRequest;
import com.nexora.roadmap.dto.response.RoadmapResponse;

import java.util.List;
import java.util.UUID;

/**
 * <h3>Purpose</h3>
 * Business service layer interface defining Roadmap lifecycle operations.
 *
 * <h3>Flow</h3>
 * Coordinates business flow inputs from {@link com.nexora.roadmap.controller.RoadmapController} and
 * directs them to the database repositories via service execution mappings.
 *
 * <h3>Testing Approach</h3>
 * Tested by asserting the mock behavior of service implementations in controller integration tests and service unit tests.
 */
public interface RoadmapService {

    /**
     * Creates a new learning Roadmap.
     *
     * @param request The data structure for creating the Roadmap.
     * @return The created Roadmap details DTO.
     */
    RoadmapResponse createRoadmap(CreateRoadmapRequest request);

    /**
     * Fetches a Roadmap by its identifier.
     *
     * @param id The Roadmap UUID.
     * @return The found Roadmap details DTO.
     */
    RoadmapResponse getRoadmapById(UUID id);

    /**
     * Fetches all registered Roadmaps in the database.
     *
     * @return A list of Roadmap DTO responses.
     */
    List<RoadmapResponse> getAllRoadmaps();

    /**
     * Updates an existing Roadmap.
     *
     * @param id      The Roadmap UUID to update.
     * @param request The data structure containing updated values.
     * @return The updated Roadmap details DTO.
     */
    RoadmapResponse updateRoadmap(UUID id, UpdateRoadmapRequest request);

    /**
     * Deletes a Roadmap by its identifier.
     *
     * @param id The Roadmap UUID to delete.
     */
    void deleteRoadmap(UUID id);
}
