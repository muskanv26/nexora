package com.nexora.roadmap.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexora.common.exception.ApiException;
import com.nexora.roadmap.dto.request.CareerReadinessRequest;
import com.nexora.roadmap.dto.response.CareerReadinessResponse;
import com.nexora.roadmap.service.CareerReadinessService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Concrete implementation of {@link CareerReadinessService} performing hybrid career evaluations.
 */
@Slf4j
@Service
public class CareerReadinessServiceImpl implements CareerReadinessService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${gemini.api.url}")
    private String geminiUrl;

    @Value("${gemini.api.key}")
    private String geminiKey;

    // Predefined role-skill mappings
    private static final Map<String, List<String>> ROLE_SKILLS_MAP = Map.of(
            "backend engineer", List.of("Java", "Spring Boot", "SQL", "REST APIs", "Git", "Docker", "AWS"),
            "frontend engineer", List.of("HTML", "CSS", "JavaScript", "React", "TypeScript", "Git", "Web Performance"),
            "full stack engineer", List.of("Java", "React", "Spring Boot", "SQL", "Git", "Docker", "REST APIs"),
            "data scientist", List.of("Python", "SQL", "Pandas", "Machine Learning", "Statistics", "Git", "Data Visualization"),
            "ml engineer", List.of("Python", "Machine Learning", "Deep Learning", "SQL", "TensorFlow", "Git", "Docker"),
            "devops engineer", List.of("Linux", "Git", "Docker", "Kubernetes", "CI/CD", "AWS", "Terraform")
    );

    // Fallback required skills if target role is not recognized
    private static final List<String> FALLBACK_SKILLS = List.of(
            "Problem Solving", "Programming", "Git", "Data Structures", "System Design"
    );

    /**
     * Default constructor used by Spring container.
     */
    public CareerReadinessServiceImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.restTemplate = new RestTemplate();
    }

    /**
     * Dependency-injected constructor used for mock testing.
     */
    public CareerReadinessServiceImpl(ObjectMapper objectMapper, RestTemplate restTemplate) {
        this.objectMapper = objectMapper;
        this.restTemplate = restTemplate;
    }

    @Override
    public CareerReadinessResponse evaluateCareerReadiness(CareerReadinessRequest request) {
        log.info("Evaluating career readiness for role: {}", request.getTargetRole());

        // 1. Get required skills for the target role
        String normalizedRole = request.getTargetRole().trim().toLowerCase().replaceAll("\\s+", " ");
        List<String> requiredSkills = ROLE_SKILLS_MAP.get(normalizedRole);
        if (requiredSkills == null) {
            log.warn("Role '{}' is not recognized. Falling back to default skills list.", request.getTargetRole());
            requiredSkills = FALLBACK_SKILLS;
        }

        // 2. Compute matching and missing skills case-insensitively
        List<String> userSkills = request.getCurrentSkills() != null ? request.getCurrentSkills() : List.of();
        List<String> matchedSkills = new ArrayList<>();
        List<String> missingSkills = new ArrayList<>();

        for (String reqSkill : requiredSkills) {
            boolean hasSkill = userSkills.stream()
                    .anyMatch(userSkill -> userSkill.trim().equalsIgnoreCase(reqSkill.trim()));
            if (hasSkill) {
                matchedSkills.add(reqSkill);
            } else {
                missingSkills.add(reqSkill);
            }
        }

        // 3. Compute score and level deterministically
        int totalRequired = requiredSkills.size();
        int score = totalRequired == 0 ? 0 : (matchedSkills.size() * 100) / totalRequired;
        String level = calculateReadinessLevel(score);

        // 4. Gemini AI Call for summary, recommended next skills, and priority action plan
        if (geminiKey == null || geminiKey.trim().isEmpty()) {
            log.error("Gemini API key is not configured.");
            throw new ApiException(
                    "Gemini API key is not configured.",
                    "GEMINI_API_KEY_MISSING",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }

        String prompt = String.format(
                "You are a career adviser. A user wants to become a '%s'.\n" +
                "Their current skills: %s.\n" +
                "The required skills for this role are: %s.\n" +
                "Their matched skills: %s.\n" +
                "Their missing skills: %s.\n\n" +
                "Please analyze their profile and return a JSON object containing:\n" +
                "1. 'summary': a brief evaluation of their readiness and general advice.\n" +
                "2. 'recommendedNextSkills': a list of skills they should focus on next to bridge the gap.\n" +
                "3. 'priorityActionPlan': a list of sequential, concrete steps they should take.\n\n" +
                "Return ONLY JSON matching this schema:\n" +
                "{\n" +
                "  \"summary\": \"Summary text here\",\n" +
                "  \"recommendedNextSkills\": [\"Skill 1\", \"Skill 2\"],\n" +
                "  \"priorityActionPlan\": [\"Action 1\", \"Action 2\"]\n" +
                "}\n\n" +
                "Return JSON only. No markdown. No explanations. No additional text.",
                request.getTargetRole(),
                String.join(", ", userSkills),
                String.join(", ", requiredSkills),
                String.join(", ", matchedSkills),
                String.join(", ", missingSkills)
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

        CareerReadinessResponse aiResponse;
        try {
            aiResponse = objectMapper.readValue(generatedJsonText, CareerReadinessResponse.class);
        } catch (Exception ex) {
            log.error("Failed to parse AI generated career readiness JSON: {}", ex.getMessage());
            throw new ApiException(
                    "Failed to parse AI generated career readiness content JSON",
                    "INVALID_AI_RESPONSE",
                    HttpStatus.UNPROCESSABLE_ENTITY
            );
        }

        if (aiResponse == null) {
            throw new ApiException(
                    "Deserialized career readiness response is empty",
                    "INVALID_AI_RESPONSE",
                    HttpStatus.UNPROCESSABLE_ENTITY
            );
        }

        // Merge deterministic results with AI advice
        return CareerReadinessResponse.builder()
                .readinessScore(score)
                .readinessLevel(level)
                .roleMatchedSkills(matchedSkills)
                .missingSkills(missingSkills)
                .recommendedNextSkills(aiResponse.getRecommendedNextSkills())
                .priorityActionPlan(aiResponse.getPriorityActionPlan())
                .summary(aiResponse.getSummary())
                .build();
    }

    private String calculateReadinessLevel(int score) {
        if (score <= 30) {
            return "BEGINNER";
        } else if (score <= 60) {
            return "INTERMEDIATE";
        } else if (score <= 85) {
            return "ADVANCED";
        } else {
            return "INTERVIEW_READY";
        }
    }
}
