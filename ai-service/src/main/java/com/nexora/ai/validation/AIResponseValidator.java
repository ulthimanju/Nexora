package com.nexora.ai.validation;

import com.nexora.ai.dto.response.CodingQuestionDTO;
import com.nexora.ai.dto.response.GeneratedAssessmentDTO;
import com.nexora.ai.dto.response.MCQOptionDTO;
import com.nexora.ai.dto.response.MCQQuestionDTO;
import com.nexora.ai.exception.InvalidAIResponseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AIResponseValidator {

    public void validate(GeneratedAssessmentDTO assessment, int expectedMcqCount, int expectedCodingCount) {
        // Validate MCQ count
        if (assessment.getMcqQuestions() == null ||
            assessment.getMcqQuestions().size() != expectedMcqCount) {
            throw new InvalidAIResponseException(
                String.format("Expected %d MCQ questions, got %d",
                    expectedMcqCount,
                    assessment.getMcqQuestions() != null ? assessment.getMcqQuestions().size() : 0)
            );
        }

        // Validate Coding count
        if (assessment.getCodingQuestions() == null ||
            assessment.getCodingQuestions().size() != expectedCodingCount) {
            throw new InvalidAIResponseException(
                String.format("Expected %d coding questions, got %d",
                    expectedCodingCount,
                    assessment.getCodingQuestions() != null ? assessment.getCodingQuestions().size() : 0)
            );
        }

        // Validate each MCQ question
        for (MCQQuestionDTO mcq : assessment.getMcqQuestions()) {
            validateMCQQuestion(mcq);
        }

        // Validate each Coding question
        for (CodingQuestionDTO coding : assessment.getCodingQuestions()) {
            validateCodingQuestion(coding);
        }

        log.info("AI response validation passed");
    }

    private void validateMCQQuestion(MCQQuestionDTO mcq) {
        // Check question text
        if (mcq.getQuestionText() == null || mcq.getQuestionText().trim().isEmpty()) {
            throw new InvalidAIResponseException("MCQ question has empty questionText");
        }

        // Check options
        if (mcq.getOptions() == null || mcq.getOptions().size() != 4) {
            throw new InvalidAIResponseException(
                String.format("MCQ question must have exactly 4 options, got %d",
                    mcq.getOptions() != null ? mcq.getOptions().size() : 0)
            );
        }

        // Check exactly one correct answer
        long correctCount = mcq.getOptions().stream()
                .filter(MCQOptionDTO::isCorrect)
                .count();

        if (correctCount != 1) {
            throw new InvalidAIResponseException(
                String.format("MCQ question must have exactly 1 correct answer, got %d", correctCount)
            );
        }

        // Validate option labels are A, B, C, D
        String[] expectedLabels = {"A", "B", "C", "D"};
        for (int i = 0; i < 4; i++) {
            MCQOptionDTO option = mcq.getOptions().get(i);
            if (!expectedLabels[i].equals(option.getLabel())) {
                throw new InvalidAIResponseException(
                    String.format("Expected option label %s, got %s", expectedLabels[i], option.getLabel())
                );
            }
            if (option.getText() == null || option.getText().trim().isEmpty()) {
                throw new InvalidAIResponseException("MCQ option has empty text");
            }
        }
    }

    private void validateCodingQuestion(CodingQuestionDTO coding) {
        // Check question text
        if (coding.getQuestionText() == null || coding.getQuestionText().trim().isEmpty()) {
            throw new InvalidAIResponseException("Coding question has empty questionText");
        }

        // Check starter code
        if (coding.getStarterCode() == null || coding.getStarterCode().trim().isEmpty()) {
            throw new InvalidAIResponseException("Coding question has empty starterCode");
        }

        // Check language
        if (coding.getLanguage() == null || coding.getLanguage().trim().isEmpty()) {
            throw new InvalidAIResponseException("Coding question has empty language");
        }

        // Check test cases (at least 3)
        if (coding.getTestCases() == null || coding.getTestCases().size() < 3) {
            throw new InvalidAIResponseException(
                String.format("Coding question must have at least 3 test cases, got %d",
                    coding.getTestCases() != null ? coding.getTestCases().size() : 0)
            );
        }

        // Validate each test case
        coding.getTestCases().forEach(testCase -> {
            if (testCase.getInput() == null || testCase.getInput().trim().isEmpty()) {
                throw new InvalidAIResponseException("Test case has empty input");
            }
            if (testCase.getExpectedOutput() == null || testCase.getExpectedOutput().trim().isEmpty()) {
                throw new InvalidAIResponseException("Test case has empty expectedOutput");
            }
        });
    }
}
