package com.nexora.assessment.service;

import java.util.Map;
import java.util.UUID;

public interface EvaluationService {
    int evaluateMcq(UUID assessmentId, Map<UUID, Object> answers);
    int evaluateCoding(UUID assessmentId, Map<UUID, Object> answers);
}
