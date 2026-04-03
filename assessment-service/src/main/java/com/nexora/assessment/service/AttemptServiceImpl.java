package com.nexora.assessment.service;

import com.nexora.assessment.constants.ErrorMessages;
import com.nexora.assessment.constants.LogMessages;
import com.nexora.assessment.constants.ServiceConstants;
import com.nexora.assessment.domain.entity.Assessment;
import com.nexora.assessment.domain.entity.Attempt;
import com.nexora.assessment.domain.enums.AssessmentStatus;
import com.nexora.assessment.dto.response.AttemptResponse;
import com.nexora.assessment.exception.AssessmentAlreadyAttemptedException;
import com.nexora.assessment.exception.AssessmentException;
import com.nexora.assessment.exception.AssessmentExpiredException;
import com.nexora.assessment.exception.AssessmentNotFoundException;
import com.nexora.assessment.repository.AttemptRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class AttemptServiceImpl implements AttemptService {

    private final AttemptRepository attemptRepository;
    private final AssessmentService assessmentService;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    @Transactional
    public AttemptResponse startAttempt(UUID assessmentId, UUID userId) {
        // Check if user already attempted
        if (attemptRepository.existsByUserIdAndAssessmentId(userId, assessmentId)) {
            throw new AssessmentAlreadyAttemptedException(ErrorMessages.ASSESSMENT_ALREADY_ATTEMPTED);
        }

        Assessment assessment = assessmentService.getAssessmentById(assessmentId);

        // Verify assessment is published
        if (assessment.getStatus() != AssessmentStatus.PUBLISHED) {
            throw new AssessmentException(ErrorMessages.ASSESSMENT_NOT_PUBLISHED, HttpStatus.BAD_REQUEST);
        }

        // Create attempt
        Attempt attempt = new Attempt();
        attempt.setUserId(userId);
        attempt.setAssessmentId(assessmentId);
        attempt.setExpiresAt(LocalDateTime.now().plusMinutes(assessment.getDurationMinutes()));
        attempt.setIsSubmitted(false);

        attempt = attemptRepository.save(attempt);

        log.info(LogMessages.ATTEMPT_CREATED, attempt.getId(), userId, assessmentId);

        // Cache attempt in Redis with TTL
        String redisKey = ServiceConstants.ATTEMPT_CACHE_PREFIX + attempt.getId();
        redisTemplate.opsForValue().set(redisKey, attempt, assessment.getDurationMinutes(), TimeUnit.MINUTES);

        log.info(LogMessages.ATTEMPT_CACHED, attempt.getId());

        return mapToAttemptResponse(attempt);
    }

    @Override
    @Transactional(readOnly = true)
    public Attempt getAttemptById(UUID attemptId) {
        return attemptRepository.findById(attemptId)
                .orElseThrow(() -> new AssessmentNotFoundException(ErrorMessages.ATTEMPT_NOT_FOUND));
    }

    @Override
    public void validateAttempt(UUID attemptId, UUID userId) {
        Attempt attempt = getAttemptById(attemptId);

        // Verify ownership
        if (!attempt.getUserId().equals(userId)) {
            throw new AssessmentException(ErrorMessages.UNAUTHORIZED_ACCESS, HttpStatus.FORBIDDEN);
        }

        // Check if already submitted
        if (attempt.getIsSubmitted()) {
            throw new AssessmentException(ErrorMessages.ATTEMPT_ALREADY_SUBMITTED, HttpStatus.BAD_REQUEST);
        }

        // Check expiry
        if (LocalDateTime.now().isAfter(attempt.getExpiresAt())) {
            throw new AssessmentExpiredException(ErrorMessages.ATTEMPT_EXPIRED);
        }
    }

    private AttemptResponse mapToAttemptResponse(Attempt attempt) {
        return AttemptResponse.builder()
                .attemptId(attempt.getId())
                .assessmentId(attempt.getAssessmentId())
                .startedAt(attempt.getStartedAt())
                .expiresAt(attempt.getExpiresAt())
                .isSubmitted(attempt.getIsSubmitted())
                .build();
    }
}
