package com.nexora.ai.router;

import com.nexora.ai.exception.AIGenerationFailedException;
import com.nexora.ai.provider.AIProvider;
import com.nexora.ai.provider.ClaudeProvider;
import com.nexora.ai.provider.GeminiProvider;
import com.nexora.ai.provider.OpenAIProvider;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
@Slf4j
public class AIProviderRouter {

    private final GeminiProvider geminiProvider;
    private final ClaudeProvider claudeProvider;
    private final OpenAIProvider openAIProvider;
    private final CircuitBreakerRegistry circuitBreakerRegistry;

    public ProviderResponse routeRequest(String prompt) {
        // Try Gemini first (primary)
        try {
            String response = executeWithCircuitBreaker("gemini",
                () -> geminiProvider.generateContent(prompt));
            log.info("Successfully generated content using Gemini");
            return new ProviderResponse(response, AIProvider.GEMINI);
        } catch (Exception e) {
            log.warn("Gemini provider failed, falling back to Claude: {}", e.getMessage());
        }

        // Try Claude (fallback #1)
        try {
            String response = executeWithCircuitBreaker("claude",
                () -> claudeProvider.generateContent(prompt));
            log.info("Successfully generated content using Claude");
            return new ProviderResponse(response, AIProvider.CLAUDE);
        } catch (Exception e) {
            log.warn("Claude provider failed, falling back to OpenAI: {}", e.getMessage());
        }

        // Try OpenAI (fallback #2)
        try {
            String response = executeWithCircuitBreaker("openai",
                () -> openAIProvider.generateContent(prompt));
            log.info("Successfully generated content using OpenAI");
            return new ProviderResponse(response, AIProvider.OPENAI);
        } catch (Exception e) {
            log.error("All AI providers failed", e);
            throw new AIGenerationFailedException("All AI providers failed to generate content");
        }
    }

    private String executeWithCircuitBreaker(String providerName, Supplier<String> supplier) {
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker(providerName);
        return circuitBreaker.executeSupplier(supplier);
    }

    public record ProviderResponse(String content, AIProvider provider) {}
}
