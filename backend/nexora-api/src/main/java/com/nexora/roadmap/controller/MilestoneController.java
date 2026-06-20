package com.nexora.roadmap.controller;

import com.nexora.common.response.ApiResponse;
import com.nexora.roadmap.dto.request.CreateMilestoneRequest;
import com.nexora.roadmap.dto.request.UpdateMilestoneRequest;
import com.nexora.roadmap.dto.response.MilestoneProgressResponse;
import com.nexora.roadmap.dto.response.MilestoneResponse;
import com.nexora.roadmap.service.MilestoneService;
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
 * REST Controller exposing API endpoints for learning Milestones management.
 * Wraps responses inside the standard {@link ApiResponse} contract.
 *
 * <h3>Flow</h3>
 * 1. Resolves and matches HTTP request verbs (POST, GET, PUT, DELETE) and path patterns.<br/>
 * 2. Parses and triggers JSR validation rules via {@code @Valid}.<br/>
 * 3. Delegates logic to {@link MilestoneService} using constructor-based injection.<br/>
 * 4. Forms and wraps results into ResponseEntity containing {@link ApiResponse}.
 *
 * <h3>Testing Approach</h3>
 * 1. MVC Integration testing using {@code MockMvc} to verify status codes, payload mapping, validation failures,
 *    and HTTP error responses.<br/>
 * 2. End-to-end integration tests hitting endpoints directly against a test database.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class MilestoneController {

    private final MilestoneService milestoneService;

    /**
     * Create a new Milestone under a specific Roadmap.
     *
     * @param roadmapId The parent Roadmap UUID.
     * @param request   The Milestone parameters.
     * @return Standard ApiResponse wrapper containing the created Milestone.
     */
    @PostMapping("/roadmaps/{roadmapId}/milestones")
    public ResponseEntity<ApiResponse<MilestoneResponse>> createMilestone(
            @PathVariable UUID roadmapId,
            @Valid @RequestBody CreateMilestoneRequest request) {
        log.info("REST request to create a Milestone under Roadmap ID: {}", roadmapId);
        
        MilestoneResponse response = milestoneService.createMilestone(roadmapId, request);
        return new ResponseEntity<>(ApiResponse.success(response), HttpStatus.CREATED);
    }

    /**
     * Fetch all Milestones for a specific Roadmap, sorted by sequence order.
     *
     * @param roadmapId The parent Roadmap UUID.
     * @return Standard ApiResponse wrapper containing a list of Milestones.
     */
    @GetMapping("/roadmaps/{roadmapId}/milestones")
    public ResponseEntity<ApiResponse<List<MilestoneResponse>>> getMilestonesByRoadmapId(
            @PathVariable UUID roadmapId) {
        log.info("REST request to fetch all Milestones for Roadmap ID: {}", roadmapId);
        
        List<MilestoneResponse> response = milestoneService.getMilestonesByRoadmapId(roadmapId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Fetch a specific Milestone by its UUID.
     *
     * @param id The Milestone UUID.
     * @return Standard ApiResponse wrapper containing the Milestone.
     */
    @GetMapping("/milestones/{id}")
    public ResponseEntity<ApiResponse<MilestoneResponse>> getMilestoneById(
            @PathVariable UUID id) {
        log.info("REST request to fetch Milestone with ID: {}", id);
        
        MilestoneResponse response = milestoneService.getMilestoneById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Update an existing Milestone.
     *
     * @param id      The Milestone UUID.
     * @param request The updated parameters.
     * @return Standard ApiResponse wrapper containing the updated Milestone.
     */
    @PutMapping("/milestones/{id}")
    public ResponseEntity<ApiResponse<MilestoneResponse>> updateMilestone(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateMilestoneRequest request) {
        log.info("REST request to update Milestone with ID: {}", id);
        
        MilestoneResponse response = milestoneService.updateMilestone(id, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Delete an existing Milestone.
     *
     * @param id The Milestone UUID.
     * @return Standard ApiResponse wrapper indicating success (empty data field).
     */
    @DeleteMapping("/milestones/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteMilestone(
            @PathVariable UUID id) {
        log.info("REST request to delete Milestone with ID: {}", id);
        
        milestoneService.deleteMilestone(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    /**
     * Calculate and fetch progress tracking information for a specific Milestone.
     *
     * @param id The Milestone UUID.
     * @return Standard ApiResponse wrapper containing the MilestoneProgressResponse.
     */
    @GetMapping("/milestones/{id}/progress")
    public ResponseEntity<ApiResponse<MilestoneProgressResponse>> getMilestoneProgress(
            @PathVariable UUID id) {
        log.info("REST request to fetch progress for Milestone with ID: {}", id);
        
        MilestoneProgressResponse response = milestoneService.getMilestoneProgress(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
