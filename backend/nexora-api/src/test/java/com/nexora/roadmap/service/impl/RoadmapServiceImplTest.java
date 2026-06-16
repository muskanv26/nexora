package com.nexora.roadmap.service.impl;

import com.nexora.common.exception.ApiException;
import com.nexora.roadmap.dto.request.CreateRoadmapRequest;
import com.nexora.roadmap.dto.request.UpdateRoadmapRequest;
import com.nexora.roadmap.dto.response.RoadmapResponse;
import com.nexora.roadmap.entity.DifficultyLevel;
import com.nexora.roadmap.entity.Roadmap;
import com.nexora.roadmap.mapper.RoadmapMapper;
import com.nexora.roadmap.repository.RoadmapRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoadmapServiceImplTest {

    @Mock
    private RoadmapRepository roadmapRepository;

    @Mock
    private RoadmapMapper roadmapMapper;

    @InjectMocks
    private RoadmapServiceImpl roadmapService;

    private Roadmap roadmap;
    private RoadmapResponse roadmapResponse;
    private UUID roadmapId;

    @BeforeEach
    void setUp() {
        roadmapId = UUID.randomUUID();
        roadmap = Roadmap.builder()
                .title("Spring Boot Essentials")
                .description("Fundamentals of Spring Boot")
                .difficultyLevel(DifficultyLevel.INTERMEDIATE)
                .estimatedWeeks(6)
                .build();
        roadmap.setId(roadmapId);

        roadmapResponse = RoadmapResponse.builder()
                .id(roadmapId)
                .title("Spring Boot Essentials")
                .description("Fundamentals of Spring Boot")
                .difficultyLevel(DifficultyLevel.INTERMEDIATE)
                .estimatedWeeks(6)
                .build();
    }

    @Test
    void createRoadmap_ShouldSaveAndReturnResponse_WhenRequestIsValid() {
        // Given
        CreateRoadmapRequest request = CreateRoadmapRequest.builder()
                .title("Spring Boot Essentials")
                .description("Fundamentals of Spring Boot")
                .difficultyLevel(DifficultyLevel.INTERMEDIATE)
                .estimatedWeeks(6)
                .build();

        when(roadmapMapper.toEntity(request)).thenReturn(roadmap);
        when(roadmapRepository.save(roadmap)).thenReturn(roadmap);
        when(roadmapMapper.toResponse(roadmap)).thenReturn(roadmapResponse);

        // When
        RoadmapResponse result = roadmapService.createRoadmap(request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(roadmapId);
        assertThat(result.getTitle()).isEqualTo("Spring Boot Essentials");
        verify(roadmapMapper).toEntity(request);
        verify(roadmapRepository).save(roadmap);
        verify(roadmapMapper).toResponse(roadmap);
    }

    @Test
    void getRoadmapById_ShouldReturnResponse_WhenIdExists() {
        // Given
        when(roadmapRepository.findById(roadmapId)).thenReturn(Optional.of(roadmap));
        when(roadmapMapper.toResponse(roadmap)).thenReturn(roadmapResponse);

        // When
        RoadmapResponse result = roadmapService.getRoadmapById(roadmapId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(roadmapId);
        verify(roadmapRepository).findById(roadmapId);
        verify(roadmapMapper).toResponse(roadmap);
    }

    @Test
    void getRoadmapById_ShouldThrowApiException_WhenIdDoesNotExist() {
        // Given
        when(roadmapRepository.findById(roadmapId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> roadmapService.getRoadmapById(roadmapId))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("Roadmap not found with id " + roadmapId)
                .satisfies(ex -> {
                    ApiException apiException = (ApiException) ex;
                    assertThat(apiException.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
                    assertThat(apiException.getCode()).isEqualTo("ROADMAP_NOT_FOUND");
                });
        verify(roadmapRepository).findById(roadmapId);
        verifyNoInteractions(roadmapMapper);
    }

    @Test
    void getAllRoadmaps_ShouldReturnList_WhenInvoked() {
        // Given
        when(roadmapRepository.findAll()).thenReturn(List.of(roadmap));
        when(roadmapMapper.toResponse(roadmap)).thenReturn(roadmapResponse);

        // When
        List<RoadmapResponse> results = roadmapService.getAllRoadmaps();

        // Then
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getId()).isEqualTo(roadmapId);
        verify(roadmapRepository).findAll();
        verify(roadmapMapper).toResponse(roadmap);
    }

    @Test
    void updateRoadmap_ShouldSaveAndReturnResponse_WhenIdExistsAndRequestIsValid() {
        // Given
        UpdateRoadmapRequest request = UpdateRoadmapRequest.builder()
                .title("Spring Boot Advanced")
                .description("Advanced Spring Boot features")
                .difficultyLevel(DifficultyLevel.ADVANCED)
                .estimatedWeeks(10)
                .build();

        RoadmapResponse updatedResponse = RoadmapResponse.builder()
                .id(roadmapId)
                .title("Spring Boot Advanced")
                .description("Advanced Spring Boot features")
                .difficultyLevel(DifficultyLevel.ADVANCED)
                .estimatedWeeks(10)
                .build();

        when(roadmapRepository.findById(roadmapId)).thenReturn(Optional.of(roadmap));
        doNothing().when(roadmapMapper).updateEntity(request, roadmap);
        when(roadmapRepository.save(roadmap)).thenReturn(roadmap);
        when(roadmapMapper.toResponse(roadmap)).thenReturn(updatedResponse);

        // When
        RoadmapResponse result = roadmapService.updateRoadmap(roadmapId, request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Spring Boot Advanced");
        assertThat(result.getDifficultyLevel()).isEqualTo(DifficultyLevel.ADVANCED);
        verify(roadmapRepository).findById(roadmapId);
        verify(roadmapMapper).updateEntity(request, roadmap);
        verify(roadmapRepository).save(roadmap);
        verify(roadmapMapper).toResponse(roadmap);
    }

    @Test
    void updateRoadmap_ShouldThrowApiException_WhenIdDoesNotExist() {
        // Given
        UpdateRoadmapRequest request = UpdateRoadmapRequest.builder()
                .title("Spring Boot Advanced")
                .build();
        when(roadmapRepository.findById(roadmapId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> roadmapService.updateRoadmap(roadmapId, request))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("Roadmap not found with id " + roadmapId)
                .satisfies(ex -> {
                    ApiException apiException = (ApiException) ex;
                    assertThat(apiException.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
                    assertThat(apiException.getCode()).isEqualTo("ROADMAP_NOT_FOUND");
                });
        verify(roadmapRepository).findById(roadmapId);
        verifyNoMoreInteractions(roadmapRepository);
        verifyNoInteractions(roadmapMapper);
    }

    @Test
    void deleteRoadmap_ShouldDelete_WhenIdExists() {
        // Given
        when(roadmapRepository.existsById(roadmapId)).thenReturn(true);
        doNothing().when(roadmapRepository).deleteById(roadmapId);

        // When
        roadmapService.deleteRoadmap(roadmapId);

        // Then
        verify(roadmapRepository).existsById(roadmapId);
        verify(roadmapRepository).deleteById(roadmapId);
    }

    @Test
    void deleteRoadmap_ShouldThrowApiException_WhenIdDoesNotExist() {
        // Given
        when(roadmapRepository.existsById(roadmapId)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> roadmapService.deleteRoadmap(roadmapId))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("Roadmap not found with id " + roadmapId)
                .satisfies(ex -> {
                    ApiException apiException = (ApiException) ex;
                    assertThat(apiException.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
                    assertThat(apiException.getCode()).isEqualTo("ROADMAP_NOT_FOUND");
                });
        verify(roadmapRepository).existsById(roadmapId);
        verify(roadmapRepository, never()).deleteById(any());
    }
}
