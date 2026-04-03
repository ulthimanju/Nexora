package com.nexora.ai.provider;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexora.ai.config.OpenAIConfig;
import com.nexora.ai.exception.AIGenerationFailedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class OpenAIProvider {

    private final WebClient.Builder webClientBuilder;
    private final OpenAIConfig config;
    private final ObjectMapper objectMapper;

    public String generateContent(String prompt) {
        try {
            String endpoint = config.getBaseUrl() + "/chat/completions";

            Map<String, Object> requestBody = Map.of(
                    "model", config.getModel(),
                    "messages", new Object[]{
                            Map.of(
                                    "role", "system",
                                    "content", prompt
                            ),
                            Map.of(
                                    "role", "user",
                                    "content", "Generate the assessment as specified."
                            )
                    },
                    "response_format", Map.of("type", "json_object")
            );

            String response = webClientBuilder.build()
                    .post()
                    .uri(endpoint)
                    .header("Authorization", "Bearer " + config.getApiKey())
                    .header("Content-Type", "application/json")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(10))
                    .block();

            return extractTextFromResponse(response);
        } catch (Exception e) {
            log.error("OpenAI API call failed", e);
            throw new AIGenerationFailedException("OpenAI provider failed: " + e.getMessage(), e);
        }
    }

    private String extractTextFromResponse(String response) {
        try {
            JsonNode root = objectMapper.readTree(response);
            JsonNode choices = root.get("choices");
            if (choices != null && choices.isArray() && choices.size() > 0) {
                JsonNode message = choices.get(0).get("message");
                if (message != null) {
                    JsonNode content = message.get("content");
                    if (content != null) {
                        return content.asText();
                    }
                }
            }
            throw new AIGenerationFailedException("Invalid response structure from OpenAI");
        } catch (Exception e) {
            throw new AIGenerationFailedException("Failed to parse OpenAI response", e);
        }
    }
}
