package com.nexora.assessment.controller;

import com.nexora.assessment.constants.ServiceConstants;
import com.nexora.assessment.dto.request.SubmissionRequest;
import com.nexora.assessment.dto.response.AssessmentResponse;
import com.nexora.assessment.dto.response.AttemptResponse;
import com.nexora.assessment.dto.response.QuestionsResponse;
import com.nexora.assessment.dto.response.SubmissionResultResponse;
import com.nexora.assessment.service.AssessmentService;
import com.nexora.assessment.service.AttemptService;
import com.nexora.assessment.service.SubmissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(ServiceConstants.API_V1_BASE + ServiceConstants.ASSESSMENTS_PATH)
@RequiredArgsConstructor
public class AssessmentController {

    private final AssessmentService assessmentService;
    private final AttemptService attemptService;
    private final SubmissionService submissionService;

    @GetMapping("/{courseId}")
    public ResponseEntity<AssessmentResponse> getAssessmentByCourse(@PathVariable UUID courseId) {
        AssessmentResponse response = assessmentService.getAssessmentByCourseId(courseId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{assessmentId}/start")
    public ResponseEntity<AttemptResponse> startAttempt(
            @PathVariable UUID assessmentId,
            @RequestAttribute("userId") UUID userId
    ) {
        AttemptResponse response = attemptService.startAttempt(assessmentId, userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{assessmentId}/questions")
    public ResponseEntity<QuestionsResponse> getQuestions(
            @PathVariable UUID assessmentId,
            @RequestAttribute("userId") UUID userId
    ) {
        QuestionsResponse response = assessmentService.getQuestionsForAttempt(assessmentId, userId);
        return ResponseEntity.ok(response);
    }
}
