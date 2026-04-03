package com.nexora.assessment.exception;

public class AssessmentAlreadyAttemptedException extends RuntimeException {

    public AssessmentAlreadyAttemptedException(String message) {
        super(message);
    }
}
