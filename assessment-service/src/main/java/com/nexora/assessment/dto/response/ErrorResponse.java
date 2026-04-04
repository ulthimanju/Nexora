package com.nexora.assessment.dto.response;

public record ErrorResponse(
        String timestamp,
        int status,
        String error,
        String message,
        String path
) {}
