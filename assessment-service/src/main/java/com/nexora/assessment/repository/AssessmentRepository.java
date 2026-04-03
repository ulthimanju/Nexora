package com.nexora.assessment.repository;

import com.nexora.assessment.domain.entity.Assessment;
import com.nexora.assessment.domain.enums.AssessmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AssessmentRepository extends JpaRepository<Assessment, UUID> {

    Optional<Assessment> findByCourseIdAndStatus(UUID courseId, AssessmentStatus status);

    boolean existsByCourseId(UUID courseId);
}
