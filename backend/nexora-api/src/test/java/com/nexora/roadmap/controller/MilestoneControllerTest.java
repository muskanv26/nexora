package com.nexora.roadmap.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexora.common.exception.ApiException;
import com.nexora.common.exception.GlobalExceptionHandler;
import com.nexora.roadmap.dto.response.MilestoneProgressResponse;
import com.nexora.roadmap.service.MilestoneService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MilestoneController.class)
@Import(GlobalExceptionHandler.class)
class MilestoneControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MilestoneService milestoneService;

    private UUID milestoneId;
    private MilestoneProgressResponse progressResponse;

    @BeforeEach
    void setUp() {
        milestoneId = UUID.randomUUID();
        progressResponse = MilestoneProgressResponse.builder()
                .milestoneId(milestoneId)
                .milestoneTitle("Database Design")
                .totalTasks(4L)
                .completedTasks(2L)
                .progressPercentage(50.0)
                .build();
    }

    @Test
    void getMilestoneProgress_ShouldReturn200OkAndResponse_WhenIdExists() throws Exception {
        // Given
        when(milestoneService.getMilestoneProgress(milestoneId)).thenReturn(progressResponse);

        // When & Then
        mockMvc.perform(get("/api/v1/milestones/{id}/progress", milestoneId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.milestoneId").value(milestoneId.toString()))
                .andExpect(jsonPath("$.data.milestoneTitle").value("Database Design"))
                .andExpect(jsonPath("$.data.totalTasks").value(4))
                .andExpect(jsonPath("$.data.completedTasks").value(2))
                .andExpect(jsonPath("$.data.progressPercentage").value(50.0))
                .andExpect(jsonPath("$.error").value((Object) null));

        verify(milestoneService).getMilestoneProgress(milestoneId);
    }

    @Test
    void getMilestoneProgress_ShouldReturn404NotFound_WhenIdDoesNotExist() throws Exception {
        // Given
        when(milestoneService.getMilestoneProgress(milestoneId))
                .thenThrow(new ApiException("Milestone not found with id " + milestoneId, "MILESTONE_NOT_FOUND", HttpStatus.NOT_FOUND));

        // When & Then
        mockMvc.perform(get("/api/v1/milestones/{id}/progress", milestoneId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.message").value("Milestone not found with id " + milestoneId))
                .andExpect(jsonPath("$.error.code").value("MILESTONE_NOT_FOUND"));

        verify(milestoneService).getMilestoneProgress(milestoneId);
    }
}
