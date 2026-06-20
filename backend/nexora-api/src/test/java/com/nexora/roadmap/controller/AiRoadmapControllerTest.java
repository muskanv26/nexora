package com.nexora.roadmap.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexora.common.exception.GlobalExceptionHandler;
import com.nexora.roadmap.dto.request.GenerateRoadmapRequest;
import com.nexora.roadmap.dto.response.GenerateRoadmapResponse;
import com.nexora.roadmap.dto.response.RoadmapResponse;
import com.nexora.roadmap.entity.DifficultyLevel;
import com.nexora.roadmap.service.AiRoadmapService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Controller tests for {@link AiRoadmapController}.
 */
@WebMvcTest(AiRoadmapController.class)
@Import(GlobalExceptionHandler.class)
class AiRoadmapControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AiRoadmapService aiRoadmapService;

    @Autowired
    private ObjectMapper objectMapper;

    private GenerateRoadmapRequest validRequest;
    private GenerateRoadmapResponse generatedResponse;
    private RoadmapResponse persistedResponse;

    @BeforeEach
    void setUp() {
        validRequest = GenerateRoadmapRequest.builder()
                .goal("Fullstack Engineer")
                .currentSkills(Collections.singletonList("React"))
                .difficultyLevel(DifficultyLevel.INTERMEDIATE)
                .timelineMonths(12)
                .build();

        generatedResponse = GenerateRoadmapResponse.builder()
                .generatedForGoal("Fullstack Engineer")
                .difficultyLevel(DifficultyLevel.INTERMEDIATE)
                .timelineMonths(12)
                .roadmapTitle("Fullstack Learning Path")
                .milestones(Collections.emptyList())
                .build();

        persistedResponse = RoadmapResponse.builder()
                .id(UUID.randomUUID())
                .title("Fullstack Learning Path")
                .difficultyLevel(DifficultyLevel.INTERMEDIATE)
                .estimatedWeeks(8)
                .build();
    }

    @Test
    void generateRoadmap_ShouldReturn200OkAndResponse_WhenPayloadIsValid() throws Exception {
        when(aiRoadmapService.generateRoadmap(any(GenerateRoadmapRequest.class))).thenReturn(generatedResponse);

        mockMvc.perform(post("/api/v1/ai/generate-roadmap")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.roadmapTitle").value("Fullstack Learning Path"))
                .andExpect(jsonPath("$.data.generatedForGoal").value("Fullstack Engineer"));
    }

    @Test
    void generateRoadmap_ShouldReturn400BadRequest_WhenGoalIsBlank() throws Exception {
        GenerateRoadmapRequest invalidRequest = GenerateRoadmapRequest.builder()
                .goal("")
                .difficultyLevel(DifficultyLevel.BEGINNER)
                .timelineMonths(3)
                .build();

        mockMvc.perform(post("/api/v1/ai/generate-roadmap")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.message").value("Validation failed"));
    }

    @Test
    void generateRoadmap_ShouldReturn400BadRequest_WhenTimelineIsInvalid() throws Exception {
        GenerateRoadmapRequest invalidRequest = GenerateRoadmapRequest.builder()
                .goal("Mobile Dev")
                .difficultyLevel(DifficultyLevel.BEGINNER)
                .timelineMonths(0) // must be >= 1
                .build();

        mockMvc.perform(post("/api/v1/ai/generate-roadmap")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void persistRoadmap_ShouldReturn201CreatedAndRoadmapResponse_WhenPayloadIsValid() throws Exception {
        when(aiRoadmapService.persistRoadmap(any(GenerateRoadmapResponse.class))).thenReturn(persistedResponse);

        mockMvc.perform(post("/api/v1/ai/persist-roadmap")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(generatedResponse)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.title").value("Fullstack Learning Path"));
    }

    @Test
    void generateAndSaveRoadmap_ShouldReturn201CreatedAndRoadmapResponse_WhenPayloadIsValid() throws Exception {
        when(aiRoadmapService.generateAndSaveRoadmap(any(GenerateRoadmapRequest.class))).thenReturn(persistedResponse);

        mockMvc.perform(post("/api/v1/ai/generate-and-save-roadmap")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.title").value("Fullstack Learning Path"));
    }
}
