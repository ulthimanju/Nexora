package com.nexora.ai.prompt;

import com.nexora.ai.exception.PromptInjectionDetectedException;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class PromptSanitizer {

    private static final Pattern DANGEROUS_PATTERN = Pattern.compile(
            "(?i)(ignore|disregard|forget|system|prompt|instruction|previous|above|below|" +
            "\\bact as\\b|\\bpretend\\b|\\broleplay\\b|\\bscript\\b|\\bexecute\\b)",
            Pattern.CASE_INSENSITIVE
    );

    public String sanitize(String input) {
        if (input == null) {
            return "";
        }

        // Check for prompt injection patterns
        if (DANGEROUS_PATTERN.matcher(input).find()) {
            throw new PromptInjectionDetectedException(
                    "Potential prompt injection detected in input"
            );
        }

        // Remove special characters that could break JSON
        String sanitized = input
                .replace("\\", "")
                .replace("\"", "'")
                .replace("\n", " ")
                .replace("\r", " ")
                .replace("\t", " ");

        // Limit length
        if (sanitized.length() > 200) {
            sanitized = sanitized.substring(0, 200);
        }

        return sanitized.trim();
    }
}
