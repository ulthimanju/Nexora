package com.nexora.ai.exception;

public class PromptInjectionDetectedException extends RuntimeException {
    public PromptInjectionDetectedException(String message) {
        super(message);
    }
}
