// Refactored: extracted 16 constants
package com.nexora.auth.constants;

public final class ErrorMessages {

    private ErrorMessages() {}

    // Auth errors
    public static final String USERNAME_EXISTS = "Username already exists";
    public static final String EMAIL_EXISTS = "Email already exists";
    public static final String DEFAULT_ROLE_NOT_FOUND = "Default role not found";
    public static final String INVALID_CREDENTIALS = "Invalid username or password";
    public static final String USER_NOT_FOUND = "User not found";
    public static final String USER_NOT_FOUND_WITH_USERNAME = "User not found: %s";
    public static final String USER_NOT_FOUND_WITH_EMAIL = "User not found with email: %s";
    public static final String ROLE_NOT_FOUND_WITH_NAME = "Role not found: %s";
    public static final String HASH_TOKEN_FAILED = "Failed to hash token";
    public static final String ACCOUNT_PENDING_VERIFICATION = "Account pending email verification";
    public static final String ACCOUNT_SUSPENDED = "Account is suspended";
    public static final String ACCOUNT_ALREADY_VERIFIED = "Account already verified";
    public static final String EMAIL_SEND_FAILED = "Failed to send OTP email";

    // Token errors
    public static final String REFRESH_TOKEN_NOT_FOUND = "Refresh token not found";
    public static final String REFRESH_TOKEN_REVOKED = "Refresh token has been revoked";
    public static final String REFRESH_TOKEN_EXPIRED = "Refresh token has expired";
    public static final String REFRESH_TOKEN_CACHE_MISS = "Refresh token not found in cache";
    public static final String INVALID_REFRESH_TOKEN = "Invalid refresh token";
    public static final String LOGOUT_FAILED = "Logout failed";
    public static final String OTP_INVALID = "Invalid OTP";
    public static final String OTP_EXPIRED = "OTP expired, please request a new one";
    public static final String OTP_ATTEMPTS_EXCEEDED = "Too many attempts, request a new OTP";
    public static final String OTP_RESEND_LIMIT_REACHED = "OTP resend limit reached. Try again later";
    public static final String OTP_TYPE_NOT_SUPPORTED = "Unsupported OTP type";

    // System errors
    public static final String UNEXPECTED_ERROR = "An unexpected error occurred";
    public static final String FAILED_INIT_JWT_CONFIG = "Failed to initialize JWT configuration";
}
