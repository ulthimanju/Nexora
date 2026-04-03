package com.nexora.assessment.constants;

public final class ServiceConstants {

    private ServiceConstants() {}

    // API Base Paths
    public static final String API_V1_BASE = "/api/v1";
    public static final String ASSESSMENTS_PATH = "/assessments";
    public static final String SUBMISSIONS_PATH = "/submissions";

    // Headers
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";

    // JWT Claims
    public static final String USER_ID_CLAIM = "userId";
    public static final String ROLES_CLAIM = "roles";

    // Redis Prefixes
    public static final String ATTEMPT_CACHE_PREFIX = "attempt:";

    // RabbitMQ
    public static final String ASSESSMENT_EXCHANGE = "nexora.assessment";
    public static final String ASSESSMENT_COMPLETED_ROUTING_KEY = "assessment.completed";

    // Marks Configuration
    public static final int TOTAL_MARKS = 20;
    public static final int MCQ_MARKS = 10;
    public static final int CODING_MARKS = 10;
    public static final int MCQ_COUNT = 10;
    public static final int CODING_COUNT = 2;
    public static final int MCQ_MARKS_PER_QUESTION = 1;
    public static final int CODING_MARKS_PER_QUESTION = 5;
}
