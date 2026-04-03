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
    public static final String AUTH_REFRESH_PATH = "/refresh";
    public static final String AUTH_LOGOUT_PATH = "/logout";
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

    // Hashing
    public static final String SHA_256_ALGORITHM = "SHA-256";
}
