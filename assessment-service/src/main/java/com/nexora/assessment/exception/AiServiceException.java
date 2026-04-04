package com.nexora.assessment.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class AiServiceException extends RuntimeException {
    private final HttpStatus status;

    public AiServiceException(String message) {
        this(message, HttpStatus.BAD_GATEWAY);
    }

    public AiServiceException(String message, HttpStatusCode statusCode) {
        super(message);
        HttpStatus resolved = HttpStatus.resolve(statusCode.value());
        this.status = resolved != null ? resolved : HttpStatus.BAD_GATEWAY;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
