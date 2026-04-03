package com.nexora.assessment.constants;

public final class LogMessages {

    private LogMessages() {}

    // Assessment Logs
    public static final String ASSESSMENT_FETCHED = "Assessment fetched: assessmentId={}";
    public static final String ASSESSMENT_STARTED = "Assessment started: userId={}, assessmentId={}";
    public static final String ASSESSMENT_SUBMITTED = "Assessment submitted: userId={}, assessmentId={}, score={}";

    // Attempt Logs
    public static final String ATTEMPT_CREATED = "Attempt created: attemptId={}, userId={}, assessmentId={}";
    public static final String ATTEMPT_CACHED = "Attempt cached: attemptId={}";
    public static final String ATTEMPT_EXPIRED_CHECK = "Checking attempt expiry: attemptId={}";

    // Evaluation Logs
    public static final String MCQ_EVALUATION_START = "Starting MCQ evaluation: attemptId={}";
    public static final String MCQ_EVALUATION_COMPLETE = "MCQ evaluation complete: score={}";
    public static final String CODING_EVALUATION_START = "Starting coding evaluation: attemptId={}";
    public static final String CODING_EVALUATION_COMPLETE = "Coding evaluation complete: score={}";
    public static final String PISTON_EXECUTION_REQUEST = "Piston execution request: language={}, questionId={}";
    public static final String PISTON_EXECUTION_RESPONSE = "Piston execution response: passed={}/{}";

    // Category Logs
    public static final String CATEGORY_COMPUTED = "Category computed: score={}, category={}";

    // Event Logs
    public static final String EVENT_PUBLISHED = "Event published: eventType={}, userId={}, assessmentId={}";
    public static final String EVENT_PUBLISH_FAILED = "Failed to publish event: {}";

    // Error Logs
    public static final String EXCEPTION_OCCURRED = "Exception occurred: {}";
    public static final String UNAUTHORIZED_ACCESS_LOG = "Unauthorized access attempt: userId={}, resource={}";
}
