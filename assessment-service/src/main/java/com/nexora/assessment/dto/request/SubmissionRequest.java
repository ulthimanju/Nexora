package com.nexora.assessment.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubmissionRequest {

    @NotNull(message = "Assessment ID is required")
    private UUID assessmentId;

    @NotNull(message = "Attempt ID is required")
    private UUID attemptId;

    @NotNull(message = "Answers are required")
    private Map<UUID, Object> answers; // questionId -> answer (UUID for MCQ, String for coding)
}
