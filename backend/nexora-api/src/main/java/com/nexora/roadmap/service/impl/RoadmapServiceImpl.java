package com.nexora.roadmap.service.impl;

import com.nexora.common.exception.ApiException;
import com.nexora.roadmap.dto.request.CreateRoadmapRequest;
import com.nexora.roadmap.dto.request.UpdateRoadmapRequest;
import com.nexora.roadmap.dto.response.RoadmapResponse;
import com.nexora.roadmap.entity.Roadmap;
import com.nexora.roadmap.mapper.RoadmapMapper;
import com.nexora.roadmap.repository.RoadmapRepository;
import com.nexora.roadmap.service.RoadmapService;
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
 * Concrete implementation of the RoadmapService business layer.
 * Contains transactional logic, queries repository, maps models, and validates entity presence.
 *
 * <h3>Flow</h3>
 * 1. Coordinates with {@link RoadmapRepository} to query and persist entities.<br/>
 * 2. Uses {@link RoadmapMapper} to convert request payloads to entities and entities to response payloads.<br/>
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
public class RoadmapServiceImpl implements RoadmapService {

    private final RoadmapRepository roadmapRepository;
    private final RoadmapMapper roadmapMapper;

    @Override
    @Transactional
    public RoadmapResponse createRoadmap(CreateRoadmapRequest request) {
        log.info("Attempting to create a new Roadmap with title: {}", request.getTitle());
        
        Roadmap roadmap = roadmapMapper.toEntity(request);
        Roadmap savedRoadmap = roadmapRepository.save(roadmap);
        
        log.info("Roadmap created successfully with ID: {}", savedRoadmap.getId());
        return roadmapMapper.toResponse(savedRoadmap);
    }

    @Override
    @Transactional(readOnly = true)
    public RoadmapResponse getRoadmapById(UUID id) {
        log.info("Fetching Roadmap with ID: {}", id);
        
        Roadmap roadmap = roadmapRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Roadmap lookup failed: ID {} not found", id);
                    return new ApiException(
                            "Roadmap not found with id " + id,
                            "ROADMAP_NOT_FOUND",
                            HttpStatus.NOT_FOUND
                    );
                });
                
        return roadmapMapper.toResponse(roadmap);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoadmapResponse> getAllRoadmaps() {
        log.info("Fetching all registered Roadmaps");
        
        return roadmapRepository.findAll().stream()
                .map(roadmapMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public RoadmapResponse updateRoadmap(UUID id, UpdateRoadmapRequest request) {
        log.info("Attempting to update Roadmap with ID: {}", id);
        
        Roadmap roadmap = roadmapRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Roadmap update failed: ID {} not found", id);
                    return new ApiException(
                            "Roadmap not found with id " + id,
                            "ROADMAP_NOT_FOUND",
                            HttpStatus.NOT_FOUND
                    );
                });
                
        roadmapMapper.updateEntity(request, roadmap);
        Roadmap updatedRoadmap = roadmapRepository.save(roadmap);
        
        log.info("Roadmap with ID {} updated successfully", id);
        return roadmapMapper.toResponse(updatedRoadmap);
    }

    @Override
    @Transactional
    public void deleteRoadmap(UUID id) {
        log.info("Attempting to delete Roadmap with ID: {}", id);
        
        if (!roadmapRepository.existsById(id)) {
            log.warn("Roadmap deletion failed: ID {} not found", id);
            throw new ApiException(
                    "Roadmap not found with id " + id,
                    "ROADMAP_NOT_FOUND",
                    HttpStatus.NOT_FOUND
            );
        }
        
        roadmapRepository.deleteById(id);
        log.info("Roadmap with ID {} deleted successfully", id);
    }
}
