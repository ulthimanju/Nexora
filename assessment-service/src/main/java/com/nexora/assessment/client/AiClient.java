package com.nexora.assessment.client;

import com.nexora.assessment.client.dto.AssessmentGenerationRequest;
import com.nexora.assessment.client.dto.GeneratedAssessmentDTO;
import com.nexora.assessment.exception.AiServiceException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
public class AiClient {

    private final WebClient aiServiceWebClient;

    public AiClient(@Qualifier("aiServiceWebClient") WebClient aiServiceWebClient) {
        this.aiServiceWebClient = aiServiceWebClient;
    }

    public Mono<GeneratedAssessmentDTO> generateAssessment(AssessmentGenerationRequest request) {
        return aiServiceWebClient.post()
                .uri("/api/v1/ai/assessment/generate")
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatusCode::isError, response ->
                        response.bodyToMono(String.class)
                                .defaultIfEmpty(HttpStatus.resolve(response.statusCode().value()) != null
                                        ? HttpStatus.resolve(response.statusCode().value()).getReasonPhrase()
                                        : "AI service error (" + response.statusCode().value() + ")")
                                .flatMap(body -> Mono.error(new AiServiceException("AI generation failed: " + body, response.statusCode())))
                )
                .bodyToMono(GeneratedAssessmentDTO.class)
                .timeout(Duration.ofSeconds(30));
    }
}
