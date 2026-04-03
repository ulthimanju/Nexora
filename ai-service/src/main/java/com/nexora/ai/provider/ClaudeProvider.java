package com.nexora.ai.provider;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexora.ai.config.ClaudeConfig;
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
public class ClaudeProvider {

    private final WebClient.Builder webClientBuilder;
    private final ClaudeConfig config;
    private final ObjectMapper objectMapper;

    public String generateContent(String systemPrompt) {
        try {
            String endpoint = config.getBaseUrl() + "/messages";

            Map<String, Object> requestBody = Map.of(
                    "model", config.getModel(),
                    "max_tokens", config.getMaxTokens(),
                    "system", systemPrompt,
                    "messages", new Object[]{
                            Map.of(
                                    "role", "user",
                                    "content", "Generate the assessment as specified in the system prompt."
                            )
                    }
            );

            String response = webClientBuilder.build()
                    .post()
                    .uri(endpoint)
                    .header("x-api-key", config.getApiKey())
                    .header("anthropic-version", "2023-06-01")
                    .header("Content-Type", "application/json")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(10))
                    .block();

            return extractTextFromResponse(response);
        } catch (Exception e) {
            log.error("Claude API call failed", e);
            throw new AIGenerationFailedException("Claude provider failed: " + e.getMessage(), e);
        }
    }

    private String extractTextFromResponse(String response) {
        try {
            JsonNode root = objectMapper.readTree(response);
            JsonNode content = root.get("content");
            if (content != null && content.isArray() && content.size() > 0) {
                JsonNode text = content.get(0).get("text");
                if (text != null) {
                    return text.asText();
                }
            }
            throw new AIGenerationFailedException("Invalid response structure from Claude");
        } catch (Exception e) {
            throw new AIGenerationFailedException("Failed to parse Claude response", e);
        }
    }
}
