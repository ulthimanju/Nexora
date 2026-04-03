package com.nexora.assessment.client;

import com.nexora.assessment.constants.LogMessages;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class PistonClient {

    private final WebClient.Builder webClientBuilder;

    @Value("${nexora.piston.base-url}")
    private String pistonBaseUrl;

    public PistonExecutionResult execute(String language, String code, String stdin) {
        log.info(LogMessages.PISTON_EXECUTION_REQUEST, language, "code-execution");

        WebClient webClient = webClientBuilder.baseUrl(pistonBaseUrl).build();

        try {
            Map<String, Object> requestBody = Map.of(
                    "language", language,
                    "version", "*",
                    "files", List.of(Map.of(
                            "name", "main." + getFileExtension(language),
                            "content", code
                    )),
                    "stdin", stdin != null ? stdin : ""
            );

            Map<String, Object> response = webClient.post()
                    .uri("/execute")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response == null) {
                return new PistonExecutionResult(false, "", "No response from Piston API");
            }

            Map<String, Object> run = (Map<String, Object>) response.get("run");
            String stdout = (String) run.getOrDefault("stdout", "");
            String stderr = (String) run.getOrDefault("stderr", "");
            Integer exitCode = (Integer) run.getOrDefault("code", 1);

            boolean success = exitCode == 0 && (stderr == null || stderr.isEmpty());

            return new PistonExecutionResult(success, stdout.trim(), stderr);

        } catch (Exception e) {
            log.error("Piston execution error: {}", e.getMessage(), e);
            return new PistonExecutionResult(false, "", e.getMessage());
        }
    }

    private String getFileExtension(String language) {
        return switch (language.toLowerCase()) {
            case "java" -> "java";
            case "python" -> "py";
            case "javascript" -> "js";
            case "cpp", "c++" -> "cpp";
            case "c" -> "c";
            case "go" -> "go";
            case "rust" -> "rs";
            default -> "txt";
        };
    }

    public record PistonExecutionResult(boolean success, String output, String error) {}
}
