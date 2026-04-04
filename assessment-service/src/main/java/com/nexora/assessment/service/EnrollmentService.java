package com.nexora.assessment.service;

import java.util.UUID;

/**
 * Service for verifying user enrollment in courses.
 * This interface should be implemented by a course-service client or enrollment verification logic.
 */
public interface EnrollmentService {

    /**
     * Verifies if a user is enrolled in a specific course.
     *
     * @param userId the ID of the user
     * @param courseId the ID of the course
     * @return true if the user is enrolled, false otherwise
     */
    boolean isUserEnrolledInCourse(UUID userId, UUID courseId);
}
