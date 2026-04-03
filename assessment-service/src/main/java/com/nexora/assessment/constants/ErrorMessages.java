package com.nexora.assessment.constants;

public final class ErrorMessages {

    private ErrorMessages() {}

    // Assessment Errors
    public static final String ASSESSMENT_NOT_FOUND = "Assessment not found";
    public static final String ASSESSMENT_NOT_FOUND_FOR_COURSE = "No published assessment found for this course";
    public static final String ASSESSMENT_NOT_PUBLISHED = "Assessment is not published";
    public static final String ASSESSMENT_ALREADY_ATTEMPTED = "You have already attempted this assessment";

    // Attempt Errors
    public static final String ATTEMPT_NOT_FOUND = "Attempt not found";
    public static final String ATTEMPT_EXPIRED = "Assessment session has expired";
    public static final String ATTEMPT_ALREADY_SUBMITTED = "Assessment has already been submitted";
    public static final String ATTEMPT_NOT_ACTIVE = "No active attempt found";

    // Submission Errors
    public static final String SUBMISSION_INVALID = "Invalid submission";
    public static final String SUBMISSION_NOT_FOUND = "Submission not found";
    public static final String MISSING_ANSWERS = "Missing answers for some questions";

    // Authorization Errors
    public static final String UNAUTHORIZED_ACCESS = "Unauthorized access to this resource";
    public static final String INVALID_TOKEN = "Invalid or expired token";
    public static final String USER_ID_NOT_FOUND_IN_TOKEN = "User ID not found in token";

    // Validation Errors
    public static final String INVALID_COURSE_ID = "Invalid course ID";
    public static final String INVALID_ASSESSMENT_ID = "Invalid assessment ID";
    public static final String INVALID_QUESTION_ANSWER = "Invalid answer for question";

    // External Service Errors
    public static final String PISTON_EXECUTION_FAILED = "Code execution failed";
    public static final String RABBITMQ_PUBLISH_FAILED = "Failed to publish event to message queue";

    // Generic Errors
    public static final String UNEXPECTED_ERROR = "An unexpected error occurred";
}
