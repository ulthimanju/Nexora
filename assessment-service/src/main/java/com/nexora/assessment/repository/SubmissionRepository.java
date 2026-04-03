package com.nexora.assessment.repository;

import com.nexora.assessment.domain.entity.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, UUID> {

    Optional<Submission> findByAttemptId(UUID attemptId);

    Optional<Submission> findByUserIdAndAssessmentId(UUID userId, UUID assessmentId);
}
