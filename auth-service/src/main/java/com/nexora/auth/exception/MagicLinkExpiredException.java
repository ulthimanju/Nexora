package com.nexora.auth.exception;

import org.springframework.http.HttpStatus;

public class MagicLinkExpiredException extends RuntimeException {
    private final HttpStatus status = HttpStatus.GONE;

    public MagicLinkExpiredException(String message) {
        super(message);
    }

    public HttpStatus getStatus() {
        return status;
    }
}
