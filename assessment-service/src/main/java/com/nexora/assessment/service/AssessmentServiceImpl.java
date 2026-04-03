package com.nexora.assessment.service;

import com.nexora.assessment.constants.ErrorMessages;
import com.nexora.assessment.constants.LogMessages;
import com.nexora.assessment.domain.entity.Assessment;
import com.nexora.assessment.domain.entity.Question;
import com.nexora.assessment.domain.entity.QuestionOption;
import com.nexora.assessment.domain.enums.AssessmentStatus;
import com.nexora.assessment.dto.response.AssessmentResponse;
import com.nexora.assessment.dto.response.QuestionOptionResponse;
import com.nexora.assessment.dto.response.QuestionResponse;
import com.nexora.assessment.dto.response.QuestionsResponse;
import com.nexora.assessment.exception.AssessmentNotFoundException;
import com.nexora.assessment.exception.AssessmentException;
import com.nexora.assessment.repository.AssessmentRepository;
import com.nexora.assessment.repository.AttemptRepository;
import com.nexora.assessment.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AssessmentServiceImpl implements AssessmentService {

    private final AssessmentRepository assessmentRepository;
    private final QuestionRepository questionRepository;
    private final AttemptRepository attemptRepository;

    @Override
    @Transactional(readOnly = true)
    public AssessmentResponse getAssessmentByCourseId(UUID courseId) {
        Assessment assessment = assessmentRepository
                .findByCourseIdAndStatus(courseId, AssessmentStatus.PUBLISHED)
                .orElseThrow(() -> new AssessmentNotFoundException(ErrorMessages.ASSESSMENT_NOT_FOUND_FOR_COURSE));

        log.info(LogMessages.ASSESSMENT_FETCHED, assessment.getId());

        return mapToAssessmentResponse(assessment);
    }

    @Override
    @Transactional(readOnly = true)
    public QuestionsResponse getQuestionsForAttempt(UUID assessmentId, UUID userId) {
        // Verify user has an active attempt
        boolean hasAttempt = attemptRepository.existsByUserIdAndAssessmentId(userId, assessmentId);
        if (!hasAttempt) {
            throw new AssessmentException(ErrorMessages.ATTEMPT_NOT_ACTIVE, HttpStatus.FORBIDDEN);
        }

        Assessment assessment = getAssessmentById(assessmentId);

        List<Question> questions = questionRepository.findByAssessmentIdOrderByOrderIndexAsc(assessmentId);

        List<QuestionResponse> questionResponses = questions.stream()
                .map(this::mapToQuestionResponse)
                .collect(Collectors.toList());

        return new QuestionsResponse(assessmentId, questionResponses);
    }

    @Override
    @Transactional(readOnly = true)
    public Assessment getAssessmentById(UUID assessmentId) {
        return assessmentRepository.findById(assessmentId)
                .orElseThrow(() -> new AssessmentNotFoundException(ErrorMessages.ASSESSMENT_NOT_FOUND));
    }

    private AssessmentResponse mapToAssessmentResponse(Assessment assessment) {
        return AssessmentResponse.builder()
                .id(assessment.getId())
                .courseId(assessment.getCourseId())
                .title(assessment.getTitle())
                .description(assessment.getDescription())
                .totalMarks(assessment.getTotalMarks())
                .mcqMarks(assessment.getMcqMarks())
                .codingMarks(assessment.getCodingMarks())
                .durationMinutes(assessment.getDurationMinutes())
                .status(assessment.getStatus())
                .build();
    }

    private QuestionResponse mapToQuestionResponse(Question question) {
        List<QuestionOptionResponse> optionResponses = question.getOptions().stream()
                .map(this::mapToQuestionOptionResponse)
                .collect(Collectors.toList());

        return QuestionResponse.builder()
                .id(question.getId())
                .type(question.getType())
                .text(question.getText())
                .codeTemplate(question.getCodeTemplate())
                .marks(question.getMarks())
                .orderIndex(question.getOrderIndex())
                .options(optionResponses)
                .build();
    }

    private QuestionOptionResponse mapToQuestionOptionResponse(QuestionOption option) {
        return QuestionOptionResponse.builder()
                .id(option.getId())
                .text(option.getText())
                .orderIndex(option.getOrderIndex())
                // NEVER expose isCorrect
                .build();
    }
}
