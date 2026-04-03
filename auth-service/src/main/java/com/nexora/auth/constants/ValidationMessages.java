// Refactored: extracted 7 constants
package com.nexora.auth.constants;

public final class ValidationMessages {

    private ValidationMessages() {}

    // RegisterRequest
    public static final String USERNAME_REQUIRED = "Username is required";
    public static final String USERNAME_SIZE = "Username must be between 3 and 50 characters";
    public static final String EMAIL_REQUIRED = "Email is required";
    public static final String EMAIL_INVALID = "Email must be valid";
    public static final String PASSWORD_REQUIRED = "Password is required";
    public static final String PASSWORD_LENGTH = "Password must be at least 8 characters";
    public static final String ROLE_REQUIRED = "Role is required";

    // LoginRequest
    public static final String USERNAME_OR_EMAIL_REQUIRED = "Username or email is required";

    // OTP
    public static final String OTP_REQUIRED = "OTP is required";
    public static final String OTP_LENGTH = "OTP must be 6 digits";
}
