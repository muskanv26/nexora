package com.nexora.roadmap.controller;

import com.nexora.common.response.ApiResponse;
import com.nexora.roadmap.dto.request.GenerateRoadmapRequest;
import com.nexora.roadmap.dto.response.GenerateRoadmapResponse;
import com.nexora.roadmap.dto.response.RoadmapResponse;
import com.nexora.roadmap.service.AiRoadmapService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * REST Controller exposing API endpoints for AI-powered Roadmap generation and persistence.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/ai")
@RequiredArgsConstructor
@Tag(name = "AI Roadmap Generation", description = "Endpoints for generating and saving learning roadmaps using AI")
public class AiRoadmapController {

    private final AiRoadmapService aiRoadmapService;

    /**
     * Generates a preview roadmap using Gemini AI without saving it.
     *
     * @param request The goal and timeline criteria.
     * @return Preview GenerateRoadmapResponse enveloped in standard ApiResponse.
     */
    @PostMapping("/generate-roadmap")
    @Operation(summary = "Generate preview roadmap", description = "Generates a personalized learning roadmap using Gemini API based on goals, current skills, difficulty, and timeline. Does not save to the database.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Roadmap generated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request payload"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal configuration or AI invocation failure")
    })
    public ResponseEntity<ApiResponse<GenerateRoadmapResponse>> generateRoadmap(
            @Valid @RequestBody GenerateRoadmapRequest request) {
        log.info("REST request to generate AI roadmap preview");
        GenerateRoadmapResponse response = aiRoadmapService.generateRoadmap(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Persists a previously generated AI roadmap structure into the database.
     *
     * @param request The generated preview roadmap details.
     * @return Persisted RoadmapResponse enveloped in standard ApiResponse.
     */
    @PostMapping("/persist-roadmap")
    @Operation(summary = "Persist generated roadmap", description = "Converts a previously generated AI roadmap structure into database entities (Roadmap, Milestones, Tasks) and saves them.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Roadmap persisted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request payload")
    })
    public ResponseEntity<ApiResponse<RoadmapResponse>> persistRoadmap(
            @Valid @RequestBody GenerateRoadmapResponse request) {
        log.info("REST request to persist AI roadmap");
        RoadmapResponse response = aiRoadmapService.persistRoadmap(request);
        return new ResponseEntity<>(ApiResponse.success(response), HttpStatus.CREATED);
    }

    /**
     * Generates a learning roadmap and persists it to the database in a single request.
     *
     * @param request The goal and timeline criteria.
     * @return Persisted RoadmapResponse enveloped in standard ApiResponse.
     */
    @PostMapping("/generate-and-save-roadmap")
    @Operation(summary = "Generate and save roadmap", description = "Generates a roadmap and persists it to the database in a single transaction request.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Roadmap generated and persisted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request payload"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal configuration or AI invocation failure")
    })
    public ResponseEntity<ApiResponse<RoadmapResponse>> generateAndSaveRoadmap(
            @Valid @RequestBody GenerateRoadmapRequest request) {
        log.info("REST request to generate and save AI roadmap");
        RoadmapResponse response = aiRoadmapService.generateAndSaveRoadmap(request);
        return new ResponseEntity<>(ApiResponse.success(response), HttpStatus.CREATED);
    }
}
