package com.nexora.roadmap.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexora.common.exception.ApiException;
import com.nexora.roadmap.dto.request.CareerReadinessRequest;
import com.nexora.roadmap.dto.response.CareerReadinessResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * Unit tests verifying the functionality of {@link CareerReadinessServiceImpl}.
 */
@ExtendWith(MockitoExtension.class)
class CareerReadinessServiceImplTest {

    @Mock
    private RestTemplate restTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private CareerReadinessServiceImpl careerReadinessService;

    @BeforeEach
    void setUp() {
        careerReadinessService = new CareerReadinessServiceImpl(objectMapper, restTemplate);
        ReflectionTestUtils.setField(careerReadinessService, "geminiUrl", "http://mock-gemini.com");
        ReflectionTestUtils.setField(careerReadinessService, "geminiKey", "mock-key");
    }

    @Test
    void evaluateCareerReadiness_ShouldReturnCorrectScoreAndLevel_WhenMatched() {
        // Backend Engineer required: Java, Spring Boot, SQL, REST APIs, Git, Docker, AWS (total 7)
        // User has: Java, SQL, Git (matches 3 out of 7 => score = 42 => level = INTERMEDIATE)
        CareerReadinessRequest request = CareerReadinessRequest.builder()
                .targetRole("Backend Engineer")
                .currentSkills(List.of("java", "sql", "git")) // check case-insensitive match
                .build();

        String geminiJsonText = "{"
                + "\"summary\": \"Good backend foundation, but Docker/AWS missing.\","
                + "\"recommendedNextSkills\": [\"Docker\", \"AWS\"],"
                + "\"priorityActionPlan\": [\"Learn Docker basic commands\", \"Deploy project to AWS\"]"
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
        CareerReadinessResponse response = careerReadinessService.evaluateCareerReadiness(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getReadinessScore()).isEqualTo(42);
        assertThat(response.getReadinessLevel()).isEqualTo("INTERMEDIATE");
        assertThat(response.getRoleMatchedSkills()).containsExactlyInAnyOrder("Java", "SQL", "Git");
        assertThat(response.getMissingSkills()).containsExactlyInAnyOrder("Spring Boot", "REST APIs", "Docker", "AWS");
        assertThat(response.getRecommendedNextSkills()).containsExactlyInAnyOrder("Docker", "AWS");
        assertThat(response.getSummary()).isEqualTo("Good backend foundation, but Docker/AWS missing.");
    }

    @Test
    void evaluateCareerReadiness_ShouldMapReadinessLevelCorrectlyBasedOnScores() {
        // Score 0-30 -> BEGINNER (e.g. 0 matches out of 7 required)
        CareerReadinessRequest requestBeginner = CareerReadinessRequest.builder()
                .targetRole("Backend Engineer")
                .currentSkills(List.of("React")) // 0 matches => score = 0 => BEGINNER
                .build();

        // Score 61-85 -> ADVANCED (matches 5 out of 7 => 5*100/7 = 71 => ADVANCED)
        CareerReadinessRequest requestAdvanced = CareerReadinessRequest.builder()
                .targetRole("Backend Engineer")
                .currentSkills(List.of("Java", "Spring Boot", "SQL", "REST APIs", "Git"))
                .build();

        // Score 86-100 -> INTERVIEW_READY (matches 7 out of 7 => 100 => INTERVIEW_READY)
        CareerReadinessRequest requestReady = CareerReadinessRequest.builder()
                .targetRole("Backend Engineer")
                .currentSkills(List.of("Java", "Spring Boot", "SQL", "REST APIs", "Git", "Docker", "AWS"))
                .build();

        String geminiJsonText = "{"
                + "\"summary\": \"Evaluation summary\","
                + "\"recommendedNextSkills\": [],"
                + "\"priorityActionPlan\": []"
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

        // Evaluate Beginner
        CareerReadinessResponse responseBeginner = careerReadinessService.evaluateCareerReadiness(requestBeginner);
        assertThat(responseBeginner.getReadinessLevel()).isEqualTo("BEGINNER");

        // Evaluate Advanced
        CareerReadinessResponse responseAdvanced = careerReadinessService.evaluateCareerReadiness(requestAdvanced);
        assertThat(responseAdvanced.getReadinessLevel()).isEqualTo("ADVANCED");

        // Evaluate Interview Ready
        CareerReadinessResponse responseReady = careerReadinessService.evaluateCareerReadiness(requestReady);
        assertThat(responseReady.getReadinessLevel()).isEqualTo("INTERVIEW_READY");
    }

    @Test
    void evaluateCareerReadiness_ShouldUseFallbackSkills_WhenRoleIsUnrecognized() {
        // Unrecognized role -> Fallback required: Problem Solving, Programming, Git, Data Structures, System Design (total 5)
        // User has: Programming, Git (matches 2 out of 5 => score = 40 => INTERMEDIATE)
        CareerReadinessRequest request = CareerReadinessRequest.builder()
                .targetRole("Quantum Computing Architect")
                .currentSkills(List.of("Programming", "Git"))
                .build();

        String geminiJsonText = "{"
                + "\"summary\": \"Generic assessment.\","
                + "\"recommendedNextSkills\": [\"System Design\"],"
                + "\"priorityActionPlan\": []"
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

        CareerReadinessResponse response = careerReadinessService.evaluateCareerReadiness(request);

        assertThat(response.getReadinessScore()).isEqualTo(40);
        assertThat(response.getRoleMatchedSkills()).containsExactlyInAnyOrder("Programming", "Git");
        assertThat(response.getMissingSkills()).containsExactlyInAnyOrder("Problem Solving", "Data Structures", "System Design");
    }

    @Test
    void evaluateCareerReadiness_ShouldThrowApiException_WhenGeminiKeyIsMissing() {
        ReflectionTestUtils.setField(careerReadinessService, "geminiKey", "");

        CareerReadinessRequest request = CareerReadinessRequest.builder()
                .targetRole("Backend Engineer")
                .currentSkills(List.of("Java"))
                .build();

        assertThatThrownBy(() -> careerReadinessService.evaluateCareerReadiness(request))
                .isInstanceOf(ApiException.class)
                .hasFieldOrPropertyWithValue("code", "GEMINI_API_KEY_MISSING")
                .hasFieldOrPropertyWithValue("status", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    void evaluateCareerReadiness_ShouldThrowApiException_WhenGeminiResponseIsMalformed() {
        CareerReadinessRequest request = CareerReadinessRequest.builder()
                .targetRole("Backend Engineer")
                .currentSkills(List.of("Java"))
                .build();

        String responseBody = "{"
                + "\"candidates\": ["
                + "  {"
                + "    \"content\": {"
                + "      \"parts\": ["
                + "        {\"text\": \"This is plain text not matching schema JSON\"}"
                + "      ]"
                + "    }"
                + "  }"
                + "]"
                + "}";

        when(restTemplate.postForEntity(any(String.class), any(HttpEntity.class), eq(String.class)))
                .thenReturn(ResponseEntity.ok(responseBody));

        assertThatThrownBy(() -> careerReadinessService.evaluateCareerReadiness(request))
                .isInstanceOf(ApiException.class)
                .hasFieldOrPropertyWithValue("code", "INVALID_AI_RESPONSE")
                .hasFieldOrPropertyWithValue("status", HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
