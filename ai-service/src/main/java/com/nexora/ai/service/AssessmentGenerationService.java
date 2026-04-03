package com.nexora.ai.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexora.ai.cache.QuestionCacheService;
import com.nexora.ai.dto.request.GenerateAssessmentRequest;
import com.nexora.ai.dto.response.GeneratedAssessmentDTO;
import com.nexora.ai.exception.InvalidAIResponseException;
import com.nexora.ai.prompt.AssessmentPromptBuilder;
import com.nexora.ai.router.AIProviderRouter;
import com.nexora.ai.validation.AIResponseValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class AssessmentGenerationService {

    private final AIProviderRouter providerRouter;
    private final AssessmentPromptBuilder promptBuilder;
    private final AIResponseValidator responseValidator;
    private final QuestionCacheService cacheService;
    private final ObjectMapper objectMapper;

    public GeneratedAssessmentDTO generateAssessment(GenerateAssessmentRequest request) {
        log.info("Generating assessment for topic: {}", request.getTopic());

        // Check cache first
        String cachedResponse = cacheService.getCachedQuestions(request);
        if (cachedResponse != null) {
            try {
                return objectMapper.readValue(cachedResponse, GeneratedAssessmentDTO.class);
            } catch (Exception e) {
                log.warn("Failed to parse cached response, regenerating", e);
            }
        }

        // Build prompt
        String prompt = promptBuilder.buildSystemPrompt(request);

        // Call AI provider with retry logic
        GeneratedAssessmentDTO assessment = null;
        int maxRetries = 2;
        int attempt = 0;

        while (attempt < maxRetries && assessment == null) {
            attempt++;
            try {
                AIProviderRouter.ProviderResponse response = providerRouter.routeRequest(prompt);
                assessment = parseAndValidateResponse(
                        response.content(),
                        response.provider().name(),
                        request.getMcqCount(),
                        request.getCodingCount()
                );
            } catch (InvalidAIResponseException e) {
                log.warn("Attempt {} failed validation: {}", attempt, e.getMessage());
                if (attempt >= maxRetries) {
                    throw e;
                }
            }
        }

        // Cache the response
        try {
            String jsonResponse = objectMapper.writeValueAsString(assessment);
            cacheService.cacheQuestions(request, jsonResponse);
        } catch (Exception e) {
            log.error("Failed to cache response", e);
        }

        return assessment;
    }

    private GeneratedAssessmentDTO parseAndValidateResponse(
            String jsonResponse, String providerName, int mcqCount, int codingCount) {
        try {
            // Parse JSON
            GeneratedAssessmentDTO assessment = objectMapper.readValue(jsonResponse, GeneratedAssessmentDTO.class);

            // Set metadata
            assessment.setGeneratedAt(Instant.now());
            assessment.setProvider(providerName);

            // Validate
            responseValidator.validate(assessment, mcqCount, codingCount);

            return assessment;
        } catch (Exception e) {
            log.error("Failed to parse or validate AI response", e);
            throw new InvalidAIResponseException("Invalid AI response: " + e.getMessage(), e);
        }
    }
}
