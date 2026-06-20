package com.nexora.roadmap.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexora.common.exception.ApiException;
import com.nexora.roadmap.dto.request.GenerateRoadmapRequest;
import com.nexora.roadmap.dto.response.GenerateRoadmapResponse;
import com.nexora.roadmap.dto.response.GenerateMilestoneResponse;
import com.nexora.roadmap.dto.response.GenerateTaskResponse;
import com.nexora.roadmap.dto.response.RoadmapResponse;
import com.nexora.roadmap.entity.DifficultyLevel;
import com.nexora.roadmap.entity.Roadmap;
import com.nexora.roadmap.mapper.RoadmapMapper;
import com.nexora.roadmap.repository.RoadmapRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests verifying the functionality of {@link AiRoadmapServiceImpl}.
 */
@ExtendWith(MockitoExtension.class)
class AiRoadmapServiceImplTest {

    @Mock
    private RoadmapRepository roadmapRepository;

    @Mock
    private RoadmapMapper roadmapMapper;

    @Mock
    private RestTemplate restTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private AiRoadmapServiceImpl aiRoadmapService;

    @BeforeEach
    void setUp() {
        aiRoadmapService = new AiRoadmapServiceImpl(roadmapRepository, roadmapMapper, objectMapper, restTemplate);
        ReflectionTestUtils.setField(aiRoadmapService, "geminiUrl", "http://mock-gemini.com");
        ReflectionTestUtils.setField(aiRoadmapService, "geminiKey", "mock-key");
    }

    @Test
    void generateRoadmap_ShouldReturnStructuredResponse_WhenGeminiRespondsSuccessfully() {
        // Given
        GenerateRoadmapRequest request = GenerateRoadmapRequest.builder()
                .goal("Frontend Developer")
                .currentSkills(List.of("HTML", "CSS"))
                .difficultyLevel(DifficultyLevel.BEGINNER)
                .timelineMonths(3)
                .build();

        String geminiJsonText = "{"
                + "\"roadmapTitle\": \"Frontend Developer Plan\","
                + "\"milestones\": ["
                + "  {"
                + "    \"milestoneTitle\": \"Basics of JS\","
                + "    \"milestoneDescription\": \"Learn Javascript basic syntax\","
                + "    \"tasks\": ["
                + "      {\"taskTitle\": \"Variables\", \"taskDescription\": \"Let/Const\", \"estimatedHours\": 5}"
                + "    ]"
                + "  }"
                + "]"
                + "}";

        String responseBody = "{"
                + "\"candidates\": ["
                + "  {"
                + "    \"content\": {"
                + "      \"parts\": ["
                + "        {\"text\": \"" + geminiJsonText.replace("\"", "\\\"") + "\"}"
                + "      ]"
                + "    }"
                + "  }"
                + "]"
                + "}";

        when(restTemplate.postForEntity(any(String.class), any(HttpEntity.class), eq(String.class)))
                .thenReturn(ResponseEntity.ok(responseBody));

        // When
        GenerateRoadmapResponse response = aiRoadmapService.generateRoadmap(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getRoadmapTitle()).isEqualTo("Frontend Developer Plan");
        assertThat(response.getGeneratedForGoal()).isEqualTo("Frontend Developer");
        assertThat(response.getDifficultyLevel()).isEqualTo(DifficultyLevel.BEGINNER);
        assertThat(response.getTimelineMonths()).isEqualTo(3);
        assertThat(response.getMilestones()).hasSize(1);
        assertThat(response.getMilestones().get(0).getMilestoneTitle()).isEqualTo("Basics of JS");
        assertThat(response.getMilestones().get(0).getTasks()).hasSize(1);
        assertThat(response.getMilestones().get(0).getTasks().get(0).getTaskTitle()).isEqualTo("Variables");
    }

    @Test
    void generateRoadmap_ShouldThrowApiException_WhenGeminiKeyIsMissing() {
        // Given
        ReflectionTestUtils.setField(aiRoadmapService, "geminiKey", "");

        GenerateRoadmapRequest request = GenerateRoadmapRequest.builder()
                .goal("Java Dev")
                .difficultyLevel(DifficultyLevel.INTERMEDIATE)
                .timelineMonths(6)
                .build();

        // When & Then
        assertThatThrownBy(() -> aiRoadmapService.generateRoadmap(request))
                .isInstanceOf(ApiException.class)
                .hasFieldOrPropertyWithValue("code", "GEMINI_API_KEY_MISSING")
                .hasFieldOrPropertyWithValue("status", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    void generateRoadmap_ShouldThrowApiException_WhenGeminiResponseIsMalformed() {
        // Given
        GenerateRoadmapRequest request = GenerateRoadmapRequest.builder()
                .goal("Java Dev")
                .difficultyLevel(DifficultyLevel.INTERMEDIATE)
                .timelineMonths(6)
                .build();

        // Non-JSON content text
        String responseBody = "{"
                + "\"candidates\": ["
                + "  {"
                + "    \"content\": {"
                + "      \"parts\": ["
                + "        {\"text\": \"This is not a JSON roadmap structure\"}"
                + "      ]"
                + "    }"
                + "  }"
                + "]"
                + "}";

        when(restTemplate.postForEntity(any(String.class), any(HttpEntity.class), eq(String.class)))
                .thenReturn(ResponseEntity.ok(responseBody));

        // When & Then
        assertThatThrownBy(() -> aiRoadmapService.generateRoadmap(request))
                .isInstanceOf(ApiException.class)
                .hasFieldOrPropertyWithValue("code", "INVALID_AI_RESPONSE")
                .hasFieldOrPropertyWithValue("status", HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @Test
    void generateRoadmap_ShouldThrowApiException_WhenGeminiResponseEnvelopeIsMalformed() {
        // Given
        GenerateRoadmapRequest request = GenerateRoadmapRequest.builder()
                .goal("Java Dev")
                .difficultyLevel(DifficultyLevel.INTERMEDIATE)
                .timelineMonths(6)
                .build();

        // Missing content or parts
        String responseBody = "{\"candidates\": []}";

        when(restTemplate.postForEntity(any(String.class), any(HttpEntity.class), eq(String.class)))
                .thenReturn(ResponseEntity.ok(responseBody));

        // When & Then
        assertThatThrownBy(() -> aiRoadmapService.generateRoadmap(request))
                .isInstanceOf(ApiException.class)
                .hasFieldOrPropertyWithValue("code", "INVALID_AI_RESPONSE")
                .hasFieldOrPropertyWithValue("status", HttpStatus.BAD_GATEWAY);
    }

    @Test
    void generateRoadmap_ShouldThrowApiException_WhenGeminiApiFails() {
        // Given
        GenerateRoadmapRequest request = GenerateRoadmapRequest.builder()
                .goal("Java Dev")
                .difficultyLevel(DifficultyLevel.INTERMEDIATE)
                .timelineMonths(6)
                .build();

        when(restTemplate.postForEntity(any(String.class), any(HttpEntity.class), eq(String.class)))
                .thenThrow(new RuntimeException("Connection timeout"));

        // When & Then
        assertThatThrownBy(() -> aiRoadmapService.generateRoadmap(request))
                .isInstanceOf(ApiException.class)
                .hasFieldOrPropertyWithValue("code", "GEMINI_API_ERROR")
                .hasFieldOrPropertyWithValue("status", HttpStatus.BAD_GATEWAY);
    }

    @Test
    void persistRoadmap_ShouldPersistEntitiesAndReturnResponse() {
        // Given
        GenerateRoadmapResponse roadmapResponseDto = GenerateRoadmapResponse.builder()
                .roadmapTitle("Spring Boot Roadmap")
                .generatedForGoal("Spring Expert")
                .difficultyLevel(DifficultyLevel.ADVANCED)
                .timelineMonths(6)
                .milestones(List.of(
                        GenerateMilestoneResponse.builder()
                                .milestoneTitle("Security")
                                .milestoneDescription("Spring Security basic config")
                                .tasks(List.of(
                                        GenerateTaskResponse.builder()
                                                .taskTitle("JWT Authentication")
                                                .taskDescription("Implement stateless JWT token logic")
                                                .estimatedHours(15)
                                                .build()
                                ))
                                .build()
                ))
                .build();

        Roadmap mockSavedRoadmap = new Roadmap();
        mockSavedRoadmap.setId(UUID.randomUUID());
        mockSavedRoadmap.setTitle("Spring Boot Roadmap");

        RoadmapResponse expectedResponse = RoadmapResponse.builder()
                .id(mockSavedRoadmap.getId())
                .title("Spring Boot Roadmap")
                .difficultyLevel(DifficultyLevel.ADVANCED)
                .estimatedWeeks(1) // calculated: 15 hours / 15.0 = 1 week
                .build();

        when(roadmapRepository.save(any(Roadmap.class))).thenReturn(mockSavedRoadmap);
        when(roadmapMapper.toResponse(any(Roadmap.class))).thenReturn(expectedResponse);

        // When
        RoadmapResponse response = aiRoadmapService.persistRoadmap(roadmapResponseDto);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isEqualTo("Spring Boot Roadmap");

        ArgumentCaptor<Roadmap> roadmapCaptor = ArgumentCaptor.forClass(Roadmap.class);
        verify(roadmapRepository).save(roadmapCaptor.capture());

        Roadmap savedEntity = roadmapCaptor.getValue();
        assertThat(savedEntity.getTitle()).isEqualTo("Spring Boot Roadmap");
        assertThat(savedEntity.getDifficultyLevel()).isEqualTo(DifficultyLevel.ADVANCED);
        assertThat(savedEntity.getEstimatedWeeks()).isEqualTo(1);
        assertThat(savedEntity.getMilestones()).hasSize(1);
        assertThat(savedEntity.getMilestones().get(0).getTitle()).isEqualTo("Security");
        assertThat(savedEntity.getMilestones().get(0).getTasks()).hasSize(1);
        assertThat(savedEntity.getMilestones().get(0).getTasks().get(0).getTitle()).isEqualTo("JWT Authentication");
    }

    @Test
    void generateAndSaveRoadmap_ShouldRunBothSteps() {
        // Given
        GenerateRoadmapRequest request = GenerateRoadmapRequest.builder()
                .goal("Java Developer")
                .difficultyLevel(DifficultyLevel.INTERMEDIATE)
                .timelineMonths(6)
                .build();

        String geminiJsonText = "{"
                + "\"roadmapTitle\": \"Intermediate Java Plan\","
                + "\"milestones\": []"
                + "}";

        String responseBody = "{"
                + "\"candidates\": ["
                + "  {"
                + "    \"content\": {"
                + "      \"parts\": ["
                + "        {\"text\": \"" + geminiJsonText.replace("\"", "\\\"") + "\"}"
                + "      ]"
                + "    }"
                + "  }"
                + "]"
                + "}";

        when(restTemplate.postForEntity(any(String.class), any(HttpEntity.class), eq(String.class)))
                .thenReturn(ResponseEntity.ok(responseBody));

        Roadmap mockSavedRoadmap = new Roadmap();
        mockSavedRoadmap.setId(UUID.randomUUID());
        mockSavedRoadmap.setTitle("Intermediate Java Plan");

        RoadmapResponse mockResponse = RoadmapResponse.builder()
                .id(mockSavedRoadmap.getId())
                .title("Intermediate Java Plan")
                .difficultyLevel(DifficultyLevel.INTERMEDIATE)
                .estimatedWeeks(1)
                .build();

        when(roadmapRepository.save(any(Roadmap.class))).thenReturn(mockSavedRoadmap);
        when(roadmapMapper.toResponse(any(Roadmap.class))).thenReturn(mockResponse);

        // When
        RoadmapResponse result = aiRoadmapService.generateAndSaveRoadmap(request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Intermediate Java Plan");
        verify(roadmapRepository).save(any(Roadmap.class));
    }
}
