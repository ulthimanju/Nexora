package com.nexora.assessment.repository;

import com.nexora.assessment.domain.entity.Attempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AttemptRepository extends JpaRepository<Attempt, UUID> {

    Optional<Attempt> findByUserIdAndAssessmentId(UUID userId, UUID assessmentId);

    boolean existsByUserIdAndAssessmentId(UUID userId, UUID assessmentId);
}
