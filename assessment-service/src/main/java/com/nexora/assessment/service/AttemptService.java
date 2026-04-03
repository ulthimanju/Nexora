package com.nexora.assessment.service;

import com.nexora.assessment.domain.entity.Attempt;
import com.nexora.assessment.dto.response.AttemptResponse;

import java.util.UUID;

public interface AttemptService {
    AttemptResponse startAttempt(UUID assessmentId, UUID userId);
    Attempt getAttemptById(UUID attemptId);
    void validateAttempt(UUID attemptId, UUID userId);
}
