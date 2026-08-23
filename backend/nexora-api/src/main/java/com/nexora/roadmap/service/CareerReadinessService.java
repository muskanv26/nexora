package com.nexora.roadmap.service;

import com.nexora.roadmap.dto.request.CareerReadinessRequest;
import com.nexora.roadmap.dto.response.CareerReadinessResponse;

/**
 * Service defining operations for evaluating career readiness based on target role and skills.
 */
public interface CareerReadinessService {

    /**
     * Evaluates a user's skills against a target role, calculating readiness score and retrieving recommendations.
     *
     * @param request Target role and current skills.
     * @return Evaluation details including score, level, matched/missing skills, recommendations and summary.
     */
    CareerReadinessResponse evaluateCareerReadiness(CareerReadinessRequest request);
}
