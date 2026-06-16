package com.nexora.roadmap.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexora.common.exception.ApiException;
import com.nexora.common.exception.GlobalExceptionHandler;
import com.nexora.roadmap.dto.request.CreateRoadmapRequest;
import com.nexora.roadmap.dto.request.UpdateRoadmapRequest;
import com.nexora.roadmap.dto.response.RoadmapResponse;
import com.nexora.roadmap.entity.DifficultyLevel;
import com.nexora.roadmap.service.RoadmapService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RoadmapController.class)
@Import(GlobalExceptionHandler.class)
class RoadmapControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RoadmapService roadmapService;

    private UUID roadmapId;
    private RoadmapResponse roadmapResponse;

    @BeforeEach
    void setUp() {
        roadmapId = UUID.randomUUID();
        roadmapResponse = RoadmapResponse.builder()
                .id(roadmapId)
                .title("Modern Java Development")
                .description("Roadmap for Java backend engineering")
                .difficultyLevel(DifficultyLevel.INTERMEDIATE)
                .estimatedWeeks(10)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void createRoadmap_ShouldReturn201CreatedAndResponse_WhenPayloadIsValid() throws Exception {
        // Given
        CreateRoadmapRequest request = CreateRoadmapRequest.builder()
                .title("Modern Java Development")
                .description("Roadmap for Java backend engineering")
                .difficultyLevel(DifficultyLevel.INTERMEDIATE)
                .estimatedWeeks(10)
                .build();

        when(roadmapService.createRoadmap(any(CreateRoadmapRequest.class))).thenReturn(roadmapResponse);

        // When & Then
        mockMvc.perform(post("/api/v1/roadmaps")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(roadmapId.toString()))
                .andExpect(jsonPath("$.data.title").value("Modern Java Development"))
                .andExpect(jsonPath("$.data.difficultyLevel").value("INTERMEDIATE"))
                .andExpect(jsonPath("$.data.estimatedWeeks").value(10))
                .andExpect(jsonPath("$.error").value((Object) null));

        verify(roadmapService).createRoadmap(refEq(request));
    }

    @Test
    void createRoadmap_ShouldReturn400BadRequest_WhenTitleIsBlank() throws Exception {
        // Given
        CreateRoadmapRequest request = CreateRoadmapRequest.builder()
                .title("") // Blank title
                .difficultyLevel(DifficultyLevel.BEGINNER)
                .estimatedWeeks(4)
                .build();

        // When & Then
        mockMvc.perform(post("/api/v1/roadmaps")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.data").value((Object) null))
                .andExpect(jsonPath("$.error.message").value("Validation failed"))
                .andExpect(jsonPath("$.error.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.error.details", hasSize(1)))
                .andExpect(jsonPath("$.error.details[0].field").value("title"))
                .andExpect(jsonPath("$.error.details[0].message").value("Title is required"));

        verifyNoInteractions(roadmapService);
    }

    @Test
    void createRoadmap_ShouldReturn400BadRequest_WhenEstimatedWeeksIsZeroOrNegative() throws Exception {
        // Given
        CreateRoadmapRequest request = CreateRoadmapRequest.builder()
                .title("Docker Essentials")
                .difficultyLevel(DifficultyLevel.BEGINNER)
                .estimatedWeeks(0) // Invalid
                .build();

        // When & Then
        mockMvc.perform(post("/api/v1/roadmaps")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.details[0].field").value("estimatedWeeks"))
                .andExpect(jsonPath("$.error.details[0].message").value("Estimated weeks must be greater than 0"));

        verifyNoInteractions(roadmapService);
    }

    @Test
    void getAllRoadmaps_ShouldReturn200OkAndList_WhenInvoked() throws Exception {
        // Given
        when(roadmapService.getAllRoadmaps()).thenReturn(List.of(roadmapResponse));

        // When & Then
        mockMvc.perform(get("/api/v1/roadmaps")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].id").value(roadmapId.toString()))
                .andExpect(jsonPath("$.data[0].title").value("Modern Java Development"));

        verify(roadmapService).getAllRoadmaps();
    }

    @Test
    void getRoadmapById_ShouldReturn200OkAndResponse_WhenIdExists() throws Exception {
        // Given
        when(roadmapService.getRoadmapById(roadmapId)).thenReturn(roadmapResponse);

        // When & Then
        mockMvc.perform(get("/api/v1/roadmaps/{id}", roadmapId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(roadmapId.toString()))
                .andExpect(jsonPath("$.data.title").value("Modern Java Development"));

        verify(roadmapService).getRoadmapById(roadmapId);
    }

    @Test
    void getRoadmapById_ShouldReturn404NotFound_WhenIdDoesNotExist() throws Exception {
        // Given
        when(roadmapService.getRoadmapById(roadmapId))
                .thenThrow(new ApiException("Roadmap not found with id " + roadmapId, "ROADMAP_NOT_FOUND", HttpStatus.NOT_FOUND));

        // When & Then
        mockMvc.perform(get("/api/v1/roadmaps/{id}", roadmapId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.message").value("Roadmap not found with id " + roadmapId))
                .andExpect(jsonPath("$.error.code").value("ROADMAP_NOT_FOUND"));

        verify(roadmapService).getRoadmapById(roadmapId);
    }

    @Test
    void updateRoadmap_ShouldReturn200OkAndResponse_WhenPayloadIsValidAndExists() throws Exception {
        // Given
        UpdateRoadmapRequest request = UpdateRoadmapRequest.builder()
                .title("Modern Java Development v2")
                .description("Roadmap for Java backend engineering updated")
                .difficultyLevel(DifficultyLevel.ADVANCED)
                .estimatedWeeks(12)
                .build();

        RoadmapResponse updatedResponse = RoadmapResponse.builder()
                .id(roadmapId)
                .title("Modern Java Development v2")
                .description("Roadmap for Java backend engineering updated")
                .difficultyLevel(DifficultyLevel.ADVANCED)
                .estimatedWeeks(12)
                .createdAt(roadmapResponse.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .build();

        when(roadmapService.updateRoadmap(eq(roadmapId), any(UpdateRoadmapRequest.class))).thenReturn(updatedResponse);

        // When & Then
        mockMvc.perform(put("/api/v1/roadmaps/{id}", roadmapId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.title").value("Modern Java Development v2"))
                .andExpect(jsonPath("$.data.difficultyLevel").value("ADVANCED"))
                .andExpect(jsonPath("$.data.estimatedWeeks").value(12));

        verify(roadmapService).updateRoadmap(eq(roadmapId), refEq(request));
    }

    @Test
    void updateRoadmap_ShouldReturn400BadRequest_WhenPayloadIsInvalid() throws Exception {
        // Given
        UpdateRoadmapRequest request = UpdateRoadmapRequest.builder()
                .title("") // Blank title
                .difficultyLevel(DifficultyLevel.INTERMEDIATE)
                .estimatedWeeks(0) // Invalid
                .build();

        // When & Then
        mockMvc.perform(put("/api/v1/roadmaps/{id}", roadmapId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("VALIDATION_ERROR"));

        verifyNoInteractions(roadmapService);
    }

    @Test
    void deleteRoadmap_ShouldReturn200Ok_WhenIdExists() throws Exception {
        // Given
        doNothing().when(roadmapService).deleteRoadmap(roadmapId);

        // When & Then
        mockMvc.perform(delete("/api/v1/roadmaps/{id}", roadmapId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value((Object) null));

        verify(roadmapService).deleteRoadmap(roadmapId);
    }

    @Test
    void deleteRoadmap_ShouldReturn404NotFound_WhenIdDoesNotExist() throws Exception {
        // Given
        doThrow(new ApiException("Roadmap not found with id " + roadmapId, "ROADMAP_NOT_FOUND", HttpStatus.NOT_FOUND))
                .when(roadmapService).deleteRoadmap(roadmapId);

        // When & Then
        mockMvc.perform(delete("/api/v1/roadmaps/{id}", roadmapId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("ROADMAP_NOT_FOUND"));

        verify(roadmapService).deleteRoadmap(roadmapId);
    }
}
