package com.nexora.assessment.exception;

import org.springframework.http.HttpStatus;

public class AssessmentException extends RuntimeException {

    private final HttpStatus status;

    public AssessmentException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public AssessmentException(String message, Throwable cause, HttpStatus status) {
        super(message, cause);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
