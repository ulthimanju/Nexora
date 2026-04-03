package com.nexora.assessment.controller;

import com.nexora.assessment.constants.ServiceConstants;
import com.nexora.assessment.dto.request.SubmissionRequest;
import com.nexora.assessment.dto.response.SubmissionResultResponse;
import com.nexora.assessment.service.SubmissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(ServiceConstants.API_V1_BASE + ServiceConstants.SUBMISSIONS_PATH)
@RequiredArgsConstructor
public class SubmissionController {

    private final SubmissionService submissionService;

    @PostMapping
    public ResponseEntity<SubmissionResultResponse> submitAssessment(
            @Valid @RequestBody SubmissionRequest request,
            @RequestAttribute("userId") UUID userId
    ) {
        SubmissionResultResponse response = submissionService.submitAssessment(request, userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{attemptId}/result")
    public ResponseEntity<SubmissionResultResponse> getResult(
            @PathVariable UUID attemptId,
            @RequestAttribute("userId") UUID userId
    ) {
        SubmissionResultResponse response = submissionService.getSubmissionResult(attemptId, userId);
        return ResponseEntity.ok(response);
    }
}
