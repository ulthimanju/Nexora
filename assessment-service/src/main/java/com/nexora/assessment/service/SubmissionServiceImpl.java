package com.nexora.assessment.service;

import com.nexora.assessment.constants.ErrorMessages;
import com.nexora.assessment.constants.LogMessages;
import com.nexora.assessment.constants.ServiceConstants;
import com.nexora.assessment.domain.entity.Assessment;
import com.nexora.assessment.domain.entity.Attempt;
import com.nexora.assessment.domain.entity.Submission;
import com.nexora.assessment.domain.enums.Category;
import com.nexora.assessment.dto.request.SubmissionRequest;
import com.nexora.assessment.dto.response.SubmissionResultResponse;
import com.nexora.assessment.event.AssessmentCompletedEvent;
import com.nexora.assessment.event.AssessmentEventPublisher;
import com.nexora.assessment.exception.AssessmentException;
import com.nexora.assessment.exception.InvalidSubmissionException;
import com.nexora.assessment.repository.SubmissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubmissionServiceImpl implements SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final AttemptService attemptService;
    private final AssessmentService assessmentService;
    private final EvaluationService evaluationService;
    private final CategoryService categoryService;
    private final AssessmentEventPublisher eventPublisher;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    @Transactional
    public SubmissionResultResponse submitAssessment(SubmissionRequest request, UUID userId) {
        // Validate attempt
        attemptService.validateAttempt(request.getAttemptId(), userId);

        Attempt attempt = attemptService.getAttemptById(request.getAttemptId());
        Assessment assessment = assessmentService.getAssessmentById(request.getAssessmentId());

        // Validate answers
        if (request.getAnswers() == null || request.getAnswers().isEmpty()) {
            throw new InvalidSubmissionException(ErrorMessages.MISSING_ANSWERS);
        }

        // Evaluate MCQ
        int mcqScore = evaluationService.evaluateMcq(assessment.getId(), request.getAnswers());

        // Evaluate Coding
        int codingScore = evaluationService.evaluateCoding(assessment.getId(), request.getAnswers());

        // Total score
        int totalScore = mcqScore + codingScore;

        // Compute category
        Category category = categoryService.computeCategory(totalScore);

        // Create submission
        Submission submission = new Submission();
        submission.setAttemptId(attempt.getId());
        submission.setUserId(userId);
        submission.setAssessmentId(assessment.getId());
        submission.setAnswers(convertAnswersToMap(request.getAnswers()));
        submission.setMcqScore(mcqScore);
        submission.setCodingScore(codingScore);
        submission.setTotalScore(totalScore);
        submission.setCategory(category);

        submission = submissionRepository.save(submission);

        // Mark attempt as submitted
        attempt.setIsSubmitted(true);
        attempt.setSubmittedAt(LocalDateTime.now());

        // Clear Redis cache
        String redisKey = ServiceConstants.ATTEMPT_CACHE_PREFIX + attempt.getId();
        redisTemplate.delete(redisKey);

        log.info(LogMessages.ASSESSMENT_SUBMITTED, userId, assessment.getId(), totalScore);

        // Publish event
        AssessmentCompletedEvent event = AssessmentCompletedEvent.builder()
                .userId(userId)
                .courseId(assessment.getCourseId())
                .assessmentId(assessment.getId())
                .score(totalScore)
                .totalMarks(assessment.getTotalMarks())
                .category(category)
                .completedAt(submission.getSubmittedAt())
                .build();

        eventPublisher.publishAssessmentCompleted(event);

        return mapToSubmissionResultResponse(submission, assessment.getTotalMarks());
    }

    @Override
    @Transactional(readOnly = true)
    public SubmissionResultResponse getSubmissionResult(UUID attemptId, UUID userId) {
        Submission submission = submissionRepository.findByAttemptId(attemptId)
                .orElseThrow(() -> new AssessmentException(ErrorMessages.SUBMISSION_NOT_FOUND, HttpStatus.NOT_FOUND));

        // Verify ownership
        if (!submission.getUserId().equals(userId)) {
            throw new AssessmentException(ErrorMessages.UNAUTHORIZED_ACCESS, HttpStatus.FORBIDDEN);
        }

        Assessment assessment = assessmentService.getAssessmentById(submission.getAssessmentId());

        return mapToSubmissionResultResponse(submission, assessment.getTotalMarks());
    }

    private Map<String, Object> convertAnswersToMap(Map<UUID, Object> answers) {
        Map<String, Object> converted = new HashMap<>();
        answers.forEach((key, value) -> converted.put(key.toString(), value));
        return converted;
    }

    private SubmissionResultResponse mapToSubmissionResultResponse(Submission submission, int totalMarks) {
        return SubmissionResultResponse.builder()
                .submissionId(submission.getId())
                .attemptId(submission.getAttemptId())
                .assessmentId(submission.getAssessmentId())
                .mcqScore(submission.getMcqScore())
                .codingScore(submission.getCodingScore())
                .totalScore(submission.getTotalScore())
                .totalMarks(totalMarks)
                .category(submission.getCategory())
                .submittedAt(submission.getSubmittedAt())
                .build();
    }
}
