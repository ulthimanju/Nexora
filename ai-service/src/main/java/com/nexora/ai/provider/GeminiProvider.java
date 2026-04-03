package com.nexora.ai.provider;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexora.ai.config.GeminiConfig;
import com.nexora.ai.exception.AIGenerationFailedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class GeminiProvider {

    private final WebClient.Builder webClientBuilder;
    private final GeminiConfig config;
    private final ObjectMapper objectMapper;

    public String generateContent(String prompt) {
        try {
            String endpoint = String.format("%s/models/%s:generateContent?key=%s",
                    config.getBaseUrl(), config.getModel(), config.getApiKey());

            Map<String, Object> requestBody = Map.of(
                    "contents", new Object[]{
                            Map.of("parts", new Object[]{
                                    Map.of("text", prompt)
                            })
                    },
                    "generationConfig", Map.of(
                            "temperature", config.getTemperature(),
                            "topP", config.getTopP(),
                            "responseMimeType", "application/json"
                    )
            );

            String response = webClientBuilder.build()
                    .post()
                    .uri(endpoint)
                    .header("Content-Type", "application/json")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(10))
                    .block();

            return extractTextFromResponse(response);
        } catch (Exception e) {
            log.error("Gemini API call failed", e);
            throw new AIGenerationFailedException("Gemini provider failed: " + e.getMessage(), e);
        }
    }

    private String extractTextFromResponse(String response) {
        try {
            JsonNode root = objectMapper.readTree(response);
            JsonNode candidates = root.get("candidates");
            if (candidates != null && candidates.isArray() && candidates.size() > 0) {
                JsonNode content = candidates.get(0).get("content");
                if (content != null) {
                    JsonNode parts = content.get("parts");
                    if (parts != null && parts.isArray() && parts.size() > 0) {
                        JsonNode text = parts.get(0).get("text");
                        if (text != null) {
                            return text.asText();
                        }
                    }
                }
            }
            throw new AIGenerationFailedException("Invalid response structure from Gemini");
        } catch (Exception e) {
            throw new AIGenerationFailedException("Failed to parse Gemini response", e);
        }
    }
}
