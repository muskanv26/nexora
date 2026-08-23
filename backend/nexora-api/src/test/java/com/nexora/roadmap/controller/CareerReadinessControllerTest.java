package com.nexora.roadmap.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexora.common.exception.GlobalExceptionHandler;
import com.nexora.roadmap.dto.request.CareerReadinessRequest;
import com.nexora.roadmap.dto.response.CareerReadinessResponse;
import com.nexora.roadmap.service.AiRoadmapService;
import com.nexora.roadmap.service.CareerReadinessService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Controller tests for evaluating career readiness in {@link AiRoadmapController}.
 */
@WebMvcTest(AiRoadmapController.class)
@Import(GlobalExceptionHandler.class)
class CareerReadinessControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AiRoadmapService aiRoadmapService;

    @MockBean
    private CareerReadinessService careerReadinessService;

    @Autowired
    private ObjectMapper objectMapper;

    private CareerReadinessRequest validRequest;
    private CareerReadinessResponse readinessResponse;

    @BeforeEach
    void setUp() {
        validRequest = CareerReadinessRequest.builder()
                .targetRole("Backend Engineer")
                .currentSkills(List.of("Java", "SQL"))
                .build();

        readinessResponse = CareerReadinessResponse.builder()
                .readinessScore(28)
                .readinessLevel("BEGINNER")
                .roleMatchedSkills(List.of("Java", "SQL"))
                .missingSkills(List.of("Spring Boot", "REST APIs", "Git", "Docker", "AWS"))
                .recommendedNextSkills(List.of("Spring Boot", "REST APIs"))
                .priorityActionPlan(List.of("Build a simple REST API", "Learn Spring Core"))
                .summary("Keep learning Spring Boot.")
                .build();
    }

    @Test
    void evaluateCareerReadiness_ShouldReturn200OkAndResponse_WhenPayloadIsValid() throws Exception {
        when(careerReadinessService.evaluateCareerReadiness(any(CareerReadinessRequest.class))).thenReturn(readinessResponse);

        mockMvc.perform(post("/api/v1/ai/career-readiness")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.readinessScore").value(28))
                .andExpect(jsonPath("$.data.readinessLevel").value("BEGINNER"))
                .andExpect(jsonPath("$.data.summary").value("Keep learning Spring Boot."));
    }

    @Test
    void evaluateCareerReadiness_ShouldReturn400BadRequest_WhenTargetRoleIsBlank() throws Exception {
        CareerReadinessRequest invalidRequest = CareerReadinessRequest.builder()
                .targetRole("")
                .currentSkills(List.of("Java"))
                .build();

        mockMvc.perform(post("/api/v1/ai/career-readiness")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.message").value("Validation failed"));
    }

    @Test
    void evaluateCareerReadiness_ShouldReturn400BadRequest_WhenCurrentSkillsIsEmpty() throws Exception {
        CareerReadinessRequest invalidRequest = CareerReadinessRequest.builder()
                .targetRole("Backend Engineer")
                .currentSkills(Collections.emptyList())
                .build();

        mockMvc.perform(post("/api/v1/ai/career-readiness")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }
}
