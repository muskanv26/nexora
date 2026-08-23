package com.nexora.roadmap.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Request payload carrying details to evaluate a candidate's career readiness.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CareerReadinessRequest {

    @NotBlank(message = "Target role is required")
    private String targetRole;

    @NotEmpty(message = "Current skills list cannot be empty")
    private List<String> currentSkills;
}
