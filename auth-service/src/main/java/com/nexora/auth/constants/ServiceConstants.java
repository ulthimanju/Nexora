// Refactored: extracted 37 constants
package com.nexora.auth.constants;

public final class ServiceConstants {

    private ServiceConstants() {}

    // Headers
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String BEARER_TOKEN_TYPE = "Bearer";

    // Roles
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ROLE_INSTRUCTOR = "ROLE_INSTRUCTOR";
    public static final String ROLE_STUDENT = "ROLE_STUDENT";
    public static final String ROLE_GUEST = "ROLE_GUEST";
    public static final String ROLE_ADMIN_NAME = "ADMIN";
    public static final String ADMIN_ROLE_EXPRESSION = "hasRole('ADMIN')";

    // Paths
    public static final String AUTH_BASE_PATH = "/api/v1/auth";
    public static final String ADMIN_BASE_PATH = "/api/v1/admin";
    public static final String AUTH_REGISTER_PATH = "/register";
    public static final String AUTH_LOGIN_PATH = "/login";
    public static final String AUTH_LOGIN_OTP_REQUEST_PATH = "/login-otp/request";
    public static final String AUTH_LOGIN_OTP_VERIFY_PATH = "/login-otp/verify";
    public static final String AUTH_REFRESH_PATH = "/refresh";
    public static final String AUTH_LOGOUT_PATH = "/logout";
    public static final String AUTH_VERIFY_EMAIL_PATH = "/verify-email";
    public static final String AUTH_RESEND_OTP_PATH = "/resend-otp";
    public static final String ADMIN_DASHBOARD_PATH = "/dashboard";
    public static final String ADMIN_USERS_PATH = "/users";
    public static final String JWKS_PATH = "/.well-known/jwks.json";
    public static final String ACTUATOR_HEALTH_PATH = "/actuator/health";

    // Response keys and values
    public static final String RESPONSE_MESSAGE_KEY = "message";
    public static final String RESPONSE_STATUS_KEY = "status";
    public static final String RESPONSE_STATUS_ACTIVE = "active";
    public static final String ADMIN_DASHBOARD_MESSAGE = "Welcome to Admin Dashboard";
    public static final String ADMIN_USERS_MESSAGE = "Admin users endpoint";

    // Tokens and claims
    public static final String REFRESH_TOKEN_REDIS_PREFIX = "refresh_token:";
    public static final String ROLES_CLAIM_KEY = "roles";
    public static final String OTP_REGISTER_REDIS_PREFIX = "otp:register:";
    public static final String OTP_LOGIN_REDIS_PREFIX = "otp:login:";
    public static final String OTP_ATTEMPTS_REDIS_PREFIX = "otp:attempts:";
    public static final String OTP_RESEND_REDIS_PREFIX = "otp:resend-count:";
    public static final int OTP_LENGTH = 6;
    public static final int OTP_RESEND_LIMIT = 3;
    public static final long OTP_RESEND_WINDOW_MINUTES = 60L;
    public static final String OTP_TYPE_REGISTER = "register";
    public static final String OTP_TYPE_LOGIN = "login";

    // JWKS metadata
    public static final String JWKS_KEYS_KEY = "keys";
    public static final String JWKS_KEY_TYPE = "kty";
    public static final String JWKS_USE = "use";
    public static final String JWKS_USE_SIGNATURE = "sig";
    public static final String JWKS_ALGORITHM = "alg";
    public static final String JWKS_ALGORITHM_RS256 = "RS256";
    public static final String JWKS_MODULUS = "n";
    public static final String JWKS_EXPONENT = "e";
    public static final String RSA_ALGORITHM = "RSA";

    // OTP + Email
    public static final String OTP_PURPOSE_REGISTER = "Email Verification";
    public static final String OTP_PURPOSE_LOGIN = "Login";
    public static final String OTP_EMAIL_SUBJECT = "Your Nexora verification code";
    public static final String OTP_EMAIL_TEMPLATE = """
            <html>
            <body>
            <p>Hello,</p>
            <p>Your one-time password for %s is <strong>%s</strong>.</p>
            <p>This code expires in %d minutes.</p>
            <p>If you did not request this code, please ignore this email.</p>
            </body>
            </html>
            """;
    public static final String REGISTER_INITIATED_MESSAGE = "Registration initiated. Verify email with OTP.";
    public static final String REGISTER_OTP_SENT_MESSAGE = "Verification OTP sent to email";
    public static final String EMAIL_VERIFIED_MESSAGE = "Email verified successfully";
    public static final String LOGIN_OTP_SENT_MESSAGE = "Login OTP sent to email";

    // Hashing
    public static final String SHA_256_ALGORITHM = "SHA-256";
}
