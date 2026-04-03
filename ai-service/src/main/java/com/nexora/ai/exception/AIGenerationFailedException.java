package com.nexora.ai.exception;

public class AIGenerationFailedException extends RuntimeException {
    public AIGenerationFailedException(String message) {
        super(message);
    }

    public AIGenerationFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
