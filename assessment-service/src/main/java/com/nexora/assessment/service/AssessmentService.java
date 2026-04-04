package com.nexora.assessment.service;

import com.nexora.assessment.client.dto.AssessmentGenerationRequest;
import com.nexora.assessment.client.dto.GeneratedAssessmentDTO;
import com.nexora.assessment.domain.entity.Assessment;
import com.nexora.assessment.dto.response.AssessmentResponse;
import com.nexora.assessment.dto.response.QuestionResponse;
import com.nexora.assessment.dto.response.QuestionsResponse;

import java.util.UUID;

public interface AssessmentService {
    AssessmentResponse getAssessmentByCourseId(UUID courseId);
    QuestionsResponse getQuestionsForAttempt(UUID assessmentId, UUID userId);
    Assessment getAssessmentById(UUID assessmentId);
    GeneratedAssessmentDTO generateAssessment(AssessmentGenerationRequest request);
}
