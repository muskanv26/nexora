package com.nexora.roadmap.service.impl;

import com.nexora.common.exception.ApiException;
import com.nexora.roadmap.dto.request.CreateMilestoneRequest;
import com.nexora.roadmap.dto.request.UpdateMilestoneRequest;
import com.nexora.roadmap.dto.response.MilestoneResponse;
import com.nexora.roadmap.entity.Milestone;
import com.nexora.roadmap.entity.Roadmap;
import com.nexora.roadmap.mapper.MilestoneMapper;
import com.nexora.roadmap.repository.MilestoneRepository;
import com.nexora.roadmap.repository.RoadmapRepository;
import com.nexora.roadmap.service.MilestoneService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * <h3>Purpose</h3>
 * Concrete implementation of the MilestoneService business layer.
 * Contains transactional logic, queries repository, maps models, and validates entities presence.
 *
 * <h3>Flow</h3>
 * 1. Coordinates with {@link MilestoneRepository} and {@link RoadmapRepository} to query and persist entities.<br/>
 * 2. Uses {@link MilestoneMapper} to convert request payloads to entities and entities to response payloads.<br/>
 * 3. Enforces entity validation check and raises {@link ApiException} if a lookup fails.
 *
 * <h3>Testing Approach</h3>
 * 1. Mockito Unit Testing: mock the repository and mapper dependencies, verify save/delete invocations,
 *    and assert that exceptions are raised for unknown IDs.<br/>
 * 2. Integration Testing: verify real database transactions, check generated schema mappings, and ensure
 *    JPA auditing fields populate.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MilestoneServiceImpl implements MilestoneService {

    private final MilestoneRepository milestoneRepository;
    private final RoadmapRepository roadmapRepository;
    private final MilestoneMapper milestoneMapper;

    @Override
    @Transactional
    public MilestoneResponse createMilestone(UUID roadmapId, CreateMilestoneRequest request) {
        log.info("Attempting to create a Milestone for Roadmap ID: {}", roadmapId);

        Roadmap roadmap = roadmapRepository.findById(roadmapId)
                .orElseThrow(() -> {
                    log.warn("Milestone creation failed: Roadmap ID {} not found", roadmapId);
                    return new ApiException(
                            "Roadmap not found with id " + roadmapId,
                            "ROADMAP_NOT_FOUND",
                            HttpStatus.NOT_FOUND
                    );
                });

        Milestone milestone = milestoneMapper.toEntity(request);
        milestone.setRoadmap(roadmap);
        
        Milestone savedMilestone = milestoneRepository.save(milestone);
        log.info("Milestone created successfully with ID: {} under Roadmap ID: {}", savedMilestone.getId(), roadmapId);
        
        return milestoneMapper.toResponse(savedMilestone);
    }

    @Override
    @Transactional(readOnly = true)
    public MilestoneResponse getMilestoneById(UUID id) {
        log.info("Fetching Milestone with ID: {}", id);

        Milestone milestone = milestoneRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Milestone lookup failed: ID {} not found", id);
                    return new ApiException(
                            "Milestone not found with id " + id,
                            "MILESTONE_NOT_FOUND",
                            HttpStatus.NOT_FOUND
                    );
                });

        return milestoneMapper.toResponse(milestone);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MilestoneResponse> getMilestonesByRoadmapId(UUID roadmapId) {
        log.info("Fetching all Milestones for Roadmap ID: {}", roadmapId);

        if (!roadmapRepository.existsById(roadmapId)) {
            log.warn("Milestone query failed: Roadmap ID {} not found", roadmapId);
            throw new ApiException(
                    "Roadmap not found with id " + roadmapId,
                    "ROADMAP_NOT_FOUND",
                    HttpStatus.NOT_FOUND
            );
        }

        return milestoneRepository.findByRoadmapIdOrderBySequenceOrderAsc(roadmapId).stream()
                .map(milestoneMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public MilestoneResponse updateMilestone(UUID id, UpdateMilestoneRequest request) {
        log.info("Attempting to update Milestone with ID: {}", id);

        Milestone milestone = milestoneRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Milestone update failed: ID {} not found", id);
                    return new ApiException(
                            "Milestone not found with id " + id,
                            "MILESTONE_NOT_FOUND",
                            HttpStatus.NOT_FOUND
                    );
                });

        milestoneMapper.updateEntity(request, milestone);
        Milestone updatedMilestone = milestoneRepository.save(milestone);
        
        log.info("Milestone with ID {} updated successfully", id);
        return milestoneMapper.toResponse(updatedMilestone);
    }

    @Override
    @Transactional
    public void deleteMilestone(UUID id) {
        log.info("Attempting to delete Milestone with ID: {}", id);

        if (!milestoneRepository.existsById(id)) {
            log.warn("Milestone deletion failed: ID {} not found", id);
            throw new ApiException(
                    "Milestone not found with id " + id,
                    "MILESTONE_NOT_FOUND",
                    HttpStatus.NOT_FOUND
            );
        }

        milestoneRepository.deleteById(id);
        log.info("Milestone with ID {} deleted successfully", id);
    }
}
