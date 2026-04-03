package com.nexora.ai.controller;

import com.nexora.ai.dto.request.GenerateAssessmentRequest;
import com.nexora.ai.dto.response.GeneratedAssessmentDTO;
import com.nexora.ai.service.AssessmentGenerationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/ai")
@RequiredArgsConstructor
@Slf4j
public class AIController {

    private final AssessmentGenerationService assessmentGenerationService;

    @PostMapping("/assessment/generate")
    public ResponseEntity<GeneratedAssessmentDTO> generateAssessment(
            @Valid @RequestBody GenerateAssessmentRequest request) {
        log.info("Received request to generate assessment for topic: {}", request.getTopic());
        GeneratedAssessmentDTO assessment = assessmentGenerationService.generateAssessment(request);
        return ResponseEntity.ok(assessment);
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "ai-service"
        ));
    }
}
