// Refactored: extracted 16 constants
package com.nexora.auth.constants;

public final class LogMessages {

    private LogMessages() {}

    // Service logs
    public static final String USER_REGISTERED = "User registered: {}";
    public static final String USER_LOGGED_IN = "User logged in: {}";
    public static final String FAILED_LOGIN_ATTEMPT = "Failed login attempt for: {}";
    public static final String TOKEN_REFRESH_FAILED = "Token refresh failed";
    public static final String USER_LOGGED_OUT = "User logged out";
    public static final String LOGOUT_FAILED = "Logout failed";
    public static final String OTP_EMAIL_SENT = "OTP email sent to: {}";
    public static final String OTP_GENERATED = "Generated OTP for: {}";
    public static final String OTP_VERIFIED = "OTP verified for: {}";
    public static final String OTP_RESEND_REQUEST = "OTP resend requested for: {}";
    public static final String OTP_ATTEMPTS_EXCEEDED = "OTP attempts exceeded for: {}";
    public static final String LOGIN_OTP_REQUEST = "Login OTP requested for: {}";
    public static final String ACCOUNT_ACTIVATED = "Account activated for: {}";

    // Exception logs
    public static final String AUTH_EXCEPTION = "Auth exception: {}";
    public static final String BAD_CREDENTIALS = "Bad credentials: {}";
    public static final String USER_NOT_FOUND = "User not found: {}";
    public static final String UNEXPECTED_EXCEPTION = "Unexpected exception: {}";

    // Security logs
    public static final String SET_AUTHENTICATION = "Set authentication for user: {}";
    public static final String CANNOT_SET_AUTHENTICATION = "Cannot set user authentication: {}";
    public static final String CREATED_ROLE = "Created role: {}";

    // JWT configuration logs
    public static final String GENERATED_KEYPAIR = "Generated RSA keypair for JWT signing";
    public static final String FAILED_GENERATE_KEYPAIR = "Failed to generate RSA keypair";
    public static final String LOAD_PRIVATE_KEY_FALLBACK = "Failed to load private key from config, using generated key";
}
