package com.nexora.assessment.service;

import com.nexora.assessment.client.PistonClient;
import com.nexora.assessment.constants.LogMessages;
import com.nexora.assessment.domain.entity.Question;
import com.nexora.assessment.domain.entity.QuestionOption;
import com.nexora.assessment.domain.enums.QuestionType;
import com.nexora.assessment.exception.EvaluationException;
import com.nexora.assessment.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class EvaluationServiceImpl implements EvaluationService {

    private final QuestionRepository questionRepository;
    private final PistonClient pistonClient;

    @Override
    public int evaluateMcq(UUID assessmentId, Map<UUID, Object> answers) {
        log.info(LogMessages.MCQ_EVALUATION_START, "assessment");

        List<Question> questions = questionRepository.findByAssessmentIdOrderByOrderIndexAsc(assessmentId);
        int score = 0;

        for (Question question : questions) {
            if (question.getType() == QuestionType.MCQ) {
                Object answer = answers.get(question.getId());
                if (answer instanceof String) {
                    UUID selectedOptionId = UUID.fromString((String) answer);
                    boolean isCorrect = question.getOptions().stream()
                            .filter(opt -> opt.getId().equals(selectedOptionId))
                            .findFirst()
                            .map(QuestionOption::getIsCorrect)
                            .orElse(false);

                    if (isCorrect) {
                        score += question.getMarks();
                    }
                }
            }
        }

        log.info(LogMessages.MCQ_EVALUATION_COMPLETE, score);
        return score;
    }

    @Override
    public int evaluateCoding(UUID assessmentId, Map<UUID, Object> answers) {
        log.info(LogMessages.CODING_EVALUATION_START, "assessment");

        List<Question> questions = questionRepository.findByAssessmentIdOrderByOrderIndexAsc(assessmentId);
        int totalScore = 0;

        for (Question question : questions) {
            if (question.getType() == QuestionType.CODING) {
                Object answer = answers.get(question.getId());
                if (answer instanceof Map) {
                    Map<String, Object> codingAnswer = (Map<String, Object>) answer;
                    String code = (String) codingAnswer.get("code");
                    String language = (String) codingAnswer.get("language");

                    if (code != null && language != null) {
                        int questionScore = evaluateCodingQuestion(question, code, language);
                        totalScore += questionScore;
                    }
                }
            }
        }

        log.info(LogMessages.CODING_EVALUATION_COMPLETE, totalScore);
        return totalScore;
    }

    private int evaluateCodingQuestion(Question question, String code, String language) {
        List<Map<String, Object>> testCases = question.getTestCases();
        if (testCases == null || testCases.isEmpty()) {
            return 0;
        }

        int passedCount = 0;
        int totalCount = testCases.size();

        for (Map<String, Object> testCase : testCases) {
            String input = (String) testCase.getOrDefault("input", "");
            String expectedOutput = (String) testCase.getOrDefault("expectedOutput", "");

            PistonClient.PistonExecutionResult result;
            try {
                result = pistonClient.execute(language, code, input);
            } catch (EvaluationException ex) {
                throw ex;
            } catch (Exception ex) {
                throw new EvaluationException("Code execution failed", HttpStatus.UNPROCESSABLE_ENTITY, ex);
            }

            if (result.success() && result.output().equals(expectedOutput.trim())) {
                passedCount++;
            }
        }

        log.info(LogMessages.PISTON_EXECUTION_RESPONSE, passedCount, totalCount);

        // Partial marking: (passed_test_cases / total_test_cases) × problem_marks
        double scoreRatio = (double) passedCount / totalCount;
        int score = (int) Math.round(scoreRatio * question.getMarks());

        return score;
    }
}
