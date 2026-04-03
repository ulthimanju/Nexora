package com.nexora.ai.prompt;

import com.nexora.ai.dto.request.GenerateAssessmentRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AssessmentPromptBuilder {

    private final PromptSanitizer sanitizer;

    public String buildSystemPrompt(GenerateAssessmentRequest request) {
        String topic = sanitizer.sanitize(request.getTopic());
        String subtopicsStr = request.getSubtopics() != null && !request.getSubtopics().isEmpty()
                ? request.getSubtopics().stream()
                    .map(sanitizer::sanitize)
                    .collect(Collectors.joining(", "))
                : "";

        String difficultyHint = request.getDifficultyHint() != null
                ? request.getDifficultyHint()
                : "MIXED";

        return String.format("""
                You are an expert computer science educator and assessment designer.
                Generate a course classification assessment for the topic: %s.
                %s

                STRICT OUTPUT RULES:
                - Return ONLY valid JSON. No markdown, no explanation, no code fences.
                - Follow the schema exactly as provided.
                - Generate exactly %d MCQ questions and %d coding problems.
                - MCQ: exactly 4 options (A/B/C/D), exactly 1 correct answer.
                - Coding: include starter method signature, 3-5 test cases with edge cases.
                - Difficulty distribution for MIXED: 30%% EASY, 50%% MEDIUM, 20%% HARD.
                - Questions must test conceptual depth, not trivia.
                - Coding problems must be solvable in under 30 minutes.
                - Do NOT repeat questions across calls for the same topic.

                JSON Schema to follow strictly:
                {
                  "topic": "string",
                  "generatedAt": "ISO-8601 timestamp",
                  "provider": "GEMINI|CLAUDE|OPENAI",
                  "mcqQuestions": [
                    {
                      "orderIndex": "number (starting from 1)",
                      "questionText": "string",
                      "marks": "number (1 for MCQ)",
                      "difficulty": "EASY|MEDIUM|HARD",
                      "options": [
                        {"label": "A|B|C|D", "text": "string", "isCorrect": "boolean"}
                      ]
                    }
                  ],
                  "codingQuestions": [
                    {
                      "orderIndex": "number (continue from MCQ count + 1)",
                      "questionText": "string",
                      "marks": "number (5 for coding)",
                      "difficulty": "EASY|MEDIUM|HARD",
                      "starterCode": "string (method signature for %s)",
                      "language": "%s",
                      "testCases": [
                        {"input": "string", "expectedOutput": "string"}
                      ]
                    }
                  ]
                }

                Generate the assessment now for topic: %s with difficulty: %s.
                """,
                topic,
                !subtopicsStr.isEmpty() ? "Focus on these subtopics: " + subtopicsStr + "." : "",
                request.getMcqCount(),
                request.getCodingCount(),
                request.getLanguage(),
                request.getLanguage(),
                topic,
                difficultyHint
        );
    }
}
