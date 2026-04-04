package com.nexora.assessment.client;

import com.nexora.assessment.constants.LogMessages;
import com.nexora.assessment.exception.EvaluationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
                .timeout(Duration.ofSeconds(15))
                .onErrorMap(java.util.concurrent.TimeoutException.class,
                        ex -> new EvaluationException("Code execution timed out", HttpStatus.UNPROCESSABLE_ENTITY, ex))
                .block();

        if (response == null) {
            throw new EvaluationException("No response from Piston API", HttpStatus.UNPROCESSABLE_ENTITY);
        }

        Object runObject = response.get("run");
        if (!(runObject instanceof Map)) {
            throw new EvaluationException("Unexpected Piston response format", HttpStatus.UNPROCESSABLE_ENTITY);
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> run = (Map<String, Object>) runObject;

        String stdout = Objects.toString(run.getOrDefault("stdout", ""), "");
        String stderr = Objects.toString(run.getOrDefault("stderr", ""), "");
        Object exitCodeObj = run.getOrDefault("code", 1);
        int exitCode = exitCodeObj instanceof Number ? ((Number) exitCodeObj).intValue() : 1;

        boolean success = exitCode == 0 && (stderr.isEmpty());

        return new PistonExecutionResult(success, stdout.trim(), stderr);
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
