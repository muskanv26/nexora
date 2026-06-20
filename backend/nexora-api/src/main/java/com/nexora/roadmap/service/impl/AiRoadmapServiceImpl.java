package com.nexora.roadmap.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexora.common.exception.ApiException;
import com.nexora.roadmap.dto.request.GenerateRoadmapRequest;
import com.nexora.roadmap.dto.response.GenerateRoadmapResponse;
import com.nexora.roadmap.dto.response.GenerateMilestoneResponse;
import com.nexora.roadmap.dto.response.GenerateTaskResponse;
import com.nexora.roadmap.dto.response.RoadmapResponse;
import com.nexora.roadmap.entity.Milestone;
import com.nexora.roadmap.entity.Roadmap;
import com.nexora.roadmap.entity.Task;
import com.nexora.roadmap.entity.TaskStatus;
import com.nexora.roadmap.mapper.RoadmapMapper;
import com.nexora.roadmap.repository.RoadmapRepository;
import com.nexora.roadmap.service.AiRoadmapService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

/**
 * Concrete implementation of {@link AiRoadmapService} integrating with Google Gemini API.
 */
@Slf4j
@Service
public class AiRoadmapServiceImpl implements AiRoadmapService {

    private final RestTemplate restTemplate;
    private final RoadmapRepository roadmapRepository;
    private final RoadmapMapper roadmapMapper;
    private final ObjectMapper objectMapper;

    @Value("${gemini.api.url}")
    private String geminiUrl;

    @Value("${gemini.api.key}")
    private String geminiKey;

    /**
     * Default constructor used by Spring container.
     */
    public AiRoadmapServiceImpl(RoadmapRepository roadmapRepository,
                                RoadmapMapper roadmapMapper,
                                ObjectMapper objectMapper) {
        this.roadmapRepository = roadmapRepository;
        this.roadmapMapper = roadmapMapper;
        this.objectMapper = objectMapper;
        this.restTemplate = new RestTemplate();
    }

    /**
     * Dependency-injected constructor used for mock testing.
     */
    public AiRoadmapServiceImpl(RoadmapRepository roadmapRepository,
                                RoadmapMapper roadmapMapper,
                                ObjectMapper objectMapper,
                                RestTemplate restTemplate) {
        this.roadmapRepository = roadmapRepository;
        this.roadmapMapper = roadmapMapper;
        this.objectMapper = objectMapper;
        this.restTemplate = restTemplate;
    }

    @Override
    @Transactional(readOnly = true)
    public GenerateRoadmapResponse generateRoadmap(GenerateRoadmapRequest request) {
        log.info("Request to generate AI roadmap for goal: {}", request.getGoal());

        if (geminiKey == null || geminiKey.trim().isEmpty()) {
            log.error("Gemini API key is not configured.");
            throw new ApiException(
                    "Gemini API key is not configured.",
                    "GEMINI_API_KEY_MISSING",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }

        String prompt = String.format(
                "Generate a personalized learning roadmap.\n\n" +
                "Goal: %s\n" +
                "Current Skills: %s\n" +
                "Difficulty: %s\n" +
                "Timeline: %d Months\n\n" +
                "Return ONLY valid JSON matching this schema:\n" +
                "{\n" +
                "  \"roadmapTitle\": \"\",\n" +
                "  \"milestones\": [\n" +
                "    {\n" +
                "      \"milestoneTitle\": \"\",\n" +
                "      \"milestoneDescription\": \"\",\n" +
                "      \"tasks\": [\n" +
                "        {\n" +
                "          \"taskTitle\": \"\",\n" +
                "          \"taskDescription\": \"\",\n" +
                "          \"estimatedHours\": 0\n" +
                "        }\n" +
                "      ]\n" +
                "    }\n" +
                "  ]\n" +
                "}\n\n" +
                "Return JSON only. No markdown. No explanations. No additional text.",
                request.getGoal(),
                request.getCurrentSkills() != null && !request.getCurrentSkills().isEmpty() 
                        ? String.join(", ", request.getCurrentSkills()) : "None",
                request.getDifficultyLevel().name(),
                request.getTimelineMonths()
        );

        Map<String, Object> textPart = Map.of("text", prompt);
        Map<String, Object> content = Map.of("parts", List.of(textPart));
        Map<String, Object> generationConfig = Map.of("responseMimeType", "application/json");
        Map<String, Object> payload = Map.of(
                "contents", List.of(content),
                "generationConfig", generationConfig
        );

        String url = geminiUrl + "?key=" + geminiKey;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

        ResponseEntity<String> responseEntity;
        try {
            responseEntity = restTemplate.postForEntity(url, entity, String.class);
        } catch (Exception ex) {
            log.error("Gemini API invocation failed: {}", ex.getMessage(), ex);
            throw new ApiException(
                    "Gemini API invocation failed: " + ex.getMessage(),
                    "GEMINI_API_ERROR",
                    HttpStatus.BAD_GATEWAY
            );
        }

        String body = responseEntity.getBody();
        if (body == null || body.trim().isEmpty()) {
            log.error("Empty response received from Gemini API.");
            throw new ApiException(
                    "Empty response received from Gemini API",
                    "INVALID_AI_RESPONSE",
                    HttpStatus.BAD_GATEWAY
            );
        }

        String generatedJsonText;
        try {
            JsonNode rootNode = objectMapper.readTree(body);
            JsonNode candidates = rootNode.path("candidates");
            if (candidates.isMissingNode() || !candidates.isArray() || candidates.isEmpty()) {
                throw new ApiException(
                        "Invalid response format from Gemini API: missing candidates array",
                        "INVALID_AI_RESPONSE",
                        HttpStatus.BAD_GATEWAY
                );
            }
            JsonNode textNode = candidates.get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text");
            if (textNode.isMissingNode()) {
                throw new ApiException(
                        "Invalid response format from Gemini API: missing text content in candidate",
                        "INVALID_AI_RESPONSE",
                        HttpStatus.BAD_GATEWAY
                );
            }
            generatedJsonText = textNode.asText();
        } catch (ApiException e) {
            throw e;
        } catch (Exception ex) {
            log.error("Failed to parse Gemini API response envelope: {}", ex.getMessage());
            throw new ApiException(
                    "Failed to parse Gemini API response envelope",
                    "INVALID_AI_RESPONSE",
                    HttpStatus.BAD_GATEWAY
            );
        }

        GenerateRoadmapResponse parsedResponse;
        try {
            parsedResponse = objectMapper.readValue(generatedJsonText, GenerateRoadmapResponse.class);
        } catch (Exception ex) {
            log.error("Failed to parse AI generated roadmap JSON content: {}", ex.getMessage());
            throw new ApiException(
                    "Failed to parse AI generated roadmap content JSON",
                    "INVALID_AI_RESPONSE",
                    HttpStatus.UNPROCESSABLE_ENTITY
            );
        }

        if (parsedResponse == null) {
            throw new ApiException(
                    "Deserialized roadmap response is empty",
                    "INVALID_AI_RESPONSE",
                    HttpStatus.UNPROCESSABLE_ENTITY
            );
        }

        parsedResponse.setGeneratedForGoal(request.getGoal());
        parsedResponse.setDifficultyLevel(request.getDifficultyLevel());
        parsedResponse.setTimelineMonths(request.getTimelineMonths());

        return parsedResponse;
    }

    @Override
    @Transactional
    public RoadmapResponse persistRoadmap(GenerateRoadmapResponse response) {
        log.info("Persisting AI-generated roadmap to database: {}", response.getRoadmapTitle());

        if (response.getRoadmapTitle() == null || response.getRoadmapTitle().trim().isEmpty()) {
            throw new ApiException(
                    "Roadmap title cannot be empty when persisting",
                    "INVALID_AI_RESPONSE",
                    HttpStatus.BAD_REQUEST
            );
        }

        Roadmap roadmap = Roadmap.builder()
                .title(response.getRoadmapTitle())
                .description("AI generated learning roadmap for " + response.getGeneratedForGoal())
                .difficultyLevel(response.getDifficultyLevel() != null ? response.getDifficultyLevel() : com.nexora.roadmap.entity.DifficultyLevel.INTERMEDIATE)
                .build();

        int totalHours = 0;
        if (response.getMilestones() != null) {
            for (int i = 0; i < response.getMilestones().size(); i++) {
                GenerateMilestoneResponse msDto = response.getMilestones().get(i);
                Milestone milestone = Milestone.builder()
                        .title(msDto.getMilestoneTitle() != null ? msDto.getMilestoneTitle() : "Milestone " + (i + 1))
                        .description(msDto.getMilestoneDescription())
                        .sequenceOrder(i + 1)
                        .build();

                if (msDto.getTasks() != null) {
                    for (GenerateTaskResponse taskDto : msDto.getTasks()) {
                        int hours = taskDto.getEstimatedHours() != null ? taskDto.getEstimatedHours() : 0;
                        totalHours += hours;

                        Task task = Task.builder()
                                .title(taskDto.getTaskTitle() != null ? taskDto.getTaskTitle() : "Learning Task")
                                .description(taskDto.getTaskDescription())
                                .status(TaskStatus.NOT_STARTED)
                                .estimatedHours(hours)
                                .build();
                        milestone.addTask(task);
                    }
                }
                roadmap.addMilestone(milestone);
            }
        }

        // Calculate estimated weeks: assume 15 study hours per week, minimum 1 week
        int estimatedWeeks = Math.max(1, (int) Math.ceil(totalHours / 15.0));
        roadmap.setEstimatedWeeks(estimatedWeeks);

        Roadmap savedRoadmap = roadmapRepository.save(roadmap);
        log.info("AI-generated roadmap persisted successfully with ID: {}", savedRoadmap.getId());

        return roadmapMapper.toResponse(savedRoadmap);
    }

    @Override
    @Transactional
    public RoadmapResponse generateAndSaveRoadmap(GenerateRoadmapRequest request) {
        log.info("One-click generate and save roadmap request for goal: {}", request.getGoal());
        GenerateRoadmapResponse generated = generateRoadmap(request);
        return persistRoadmap(generated);
    }
}
