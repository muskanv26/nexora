package com.nexora.roadmap.controller;

import com.nexora.common.response.ApiResponse;
import com.nexora.roadmap.dto.request.CreateRoadmapRequest;
import com.nexora.roadmap.dto.request.UpdateRoadmapRequest;
import com.nexora.roadmap.dto.response.RoadmapResponse;
import com.nexora.roadmap.service.RoadmapService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * <h3>Purpose</h3>
 * REST Controller exposing API endpoints for learning Roadmaps management under /api/v1/roadmaps.
 * Wraps responses inside the standard {@link ApiResponse} contract.
 *
 * <h3>Flow</h3>
 * 1. Resolves and matches HTTP request verbs (POST, GET, PUT, DELETE) and path patterns.<br/>
 * 2. Parses and triggers JSR-380 validation rules via {@code @Valid}.<br/>
 * 3. Delegates logic to {@link RoadmapService} using constructor-based injection.<br/>
 * 4. Forms and wraps results into ResponseEntity containing {@link ApiResponse}.
 *
 * <h3>Testing Approach</h3>
 * 1. MVC Integration testing using {@code MockMvc} to verify status codes, payload mapping, validation failures,
 *    and HTTP error responses.<br/>
 * 2. End-to-end integration tests hitting endpoints directly against a test database.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/roadmaps")
@RequiredArgsConstructor
public class RoadmapController {

    private final RoadmapService roadmapService;

    /**
     * Create a new Roadmap.
     *
     * @param request The Roadmap parameters.
     * @return Standard ApiResponse wrapper containing the created Roadmap.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<RoadmapResponse>> createRoadmap(
            @Valid @RequestBody CreateRoadmapRequest request) {
        log.info("REST request to create a new Roadmap: {}", request.getTitle());
        
        RoadmapResponse response = roadmapService.createRoadmap(request);
        return new ResponseEntity<>(ApiResponse.success(response), HttpStatus.CREATED);
    }

    /**
     * Fetch all learning Roadmaps.
     *
     * @return Standard ApiResponse wrapper containing a list of all Roadmaps.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<RoadmapResponse>>> getAllRoadmaps() {
        log.info("REST request to fetch all Roadmaps");
        
        List<RoadmapResponse> response = roadmapService.getAllRoadmaps();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Fetch a specific Roadmap by its UUID.
     *
     * @param id The Roadmap UUID.
     * @return Standard ApiResponse wrapper containing the Roadmap matching the given ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RoadmapResponse>> getRoadmapById(@PathVariable UUID id) {
        log.info("REST request to fetch Roadmap with ID: {}", id);
        
        RoadmapResponse response = roadmapService.getRoadmapById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Update an existing Roadmap.
     *
     * @param id      The Roadmap UUID.
     * @param request The updated parameters.
     * @return Standard ApiResponse wrapper containing the updated Roadmap representation.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<RoadmapResponse>> updateRoadmap(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateRoadmapRequest request) {
        log.info("REST request to update Roadmap with ID: {}", id);
        
        RoadmapResponse response = roadmapService.updateRoadmap(id, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Delete an existing Roadmap.
     *
     * @param id The Roadmap UUID.
     * @return Standard ApiResponse wrapper indicating success (empty data field).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteRoadmap(@PathVariable UUID id) {
        log.info("REST request to delete Roadmap with ID: {}", id);
        
        roadmapService.deleteRoadmap(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
