package com.nexora.assessment.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Stub implementation of EnrollmentService.
 * TODO: This should be replaced with actual enrollment verification logic,
 * either by calling a course-service API or querying an enrollment database.
 *
 * For now, this implementation logs a warning and returns true to maintain
 * backwards compatibility. In production, this MUST be replaced with proper
 * enrollment verification.
 */
@Service
@Slf4j
public class EnrollmentServiceImpl implements EnrollmentService {

    @Override
    public boolean isUserEnrolledInCourse(UUID userId, UUID courseId) {
        // TODO: Implement actual enrollment verification
        // This could involve:
        // 1. Calling a course-service REST API endpoint
        // 2. Querying an enrollment table in the database
        // 3. Checking a distributed cache

        log.warn("Enrollment verification not implemented. User {} attempting to access course {}. " +
                "This is a security concern and should be implemented before production deployment.",
                userId, courseId);

        // Temporarily return true to maintain backwards compatibility
        // In production, change this to false and implement proper verification
        return true;
    }
}
