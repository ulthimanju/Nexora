package com.nexora.assessment.exception;

import org.springframework.http.HttpStatus;

public class EvaluationException extends RuntimeException {
    private final HttpStatus status;

    public EvaluationException(String message) {
        this(message, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    public EvaluationException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public EvaluationException(String message, HttpStatus status, Throwable cause) {
        super(message, cause);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
