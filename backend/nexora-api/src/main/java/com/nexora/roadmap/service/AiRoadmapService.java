package com.nexora.roadmap.service;

import com.nexora.roadmap.dto.request.GenerateRoadmapRequest;
import com.nexora.roadmap.dto.response.GenerateRoadmapResponse;
import com.nexora.roadmap.dto.response.RoadmapResponse;

/**
 * Service defining operations for AI-driven roadmap generation and persistence.
 */
public interface AiRoadmapService {

    /**
     * Generates a preview roadmap using AI without saving it.
     *
     * @param request The goal and timeline criteria.
     * @return The preview GenerateRoadmapResponse.
     */
    GenerateRoadmapResponse generateRoadmap(GenerateRoadmapRequest request);

    /**
     * Persists a previously generated AI roadmap structure to the database.
     *
     * @param response The generated preview roadmap details.
     * @return Standard RoadmapResponse metadata.
     */
    RoadmapResponse persistRoadmap(GenerateRoadmapResponse response);

    /**
     * Generates and persists a learning roadmap in a single request.
     *
     * @param request The goal and timeline criteria.
     * @return Standard RoadmapResponse metadata.
     */
    RoadmapResponse generateAndSaveRoadmap(GenerateRoadmapRequest request);
}
