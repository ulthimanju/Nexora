package com.nexora.assessment.service;

import com.nexora.assessment.dto.request.SubmissionRequest;
import com.nexora.assessment.dto.response.SubmissionResultResponse;

import java.util.UUID;

public interface SubmissionService {
    SubmissionResultResponse submitAssessment(SubmissionRequest request, UUID userId);
    SubmissionResultResponse getSubmissionResult(UUID attemptId, UUID userId);
}
