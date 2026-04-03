package com.nexora.auth.exception;

import org.springframework.http.HttpStatus;

public class MagicLinkRateLimitException extends RuntimeException {
    private final HttpStatus status = HttpStatus.TOO_MANY_REQUESTS;

    public MagicLinkRateLimitException(String message) {
        super(message);
    }

    public HttpStatus getStatus() {
        return status;
    }
}
