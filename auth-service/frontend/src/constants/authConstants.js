// API endpoints
export const API_ENDPOINTS = {
  LOGIN: '/api/auth/login',
  REGISTER: '/api/auth/register',
  VERIFY_EMAIL: '/api/auth/verify-email',
  RESEND_OTP: '/api/auth/resend-otp',
  REQUEST_OTP_LOGIN: '/api/auth/otp/request',
  VERIFY_OTP_LOGIN: '/api/auth/otp/verify',
  REQUEST_MAGIC_LINK: '/api/auth/magic-link/request',
  VERIFY_MAGIC_LINK: '/api/auth/magic-link/verify',
  REFRESH_TOKEN: '/api/auth/refresh',
  LOGOUT: '/api/auth/logout',
}

// OTP types
export const OTP_TYPES = {
  EMAIL_VERIFICATION: 'EMAIL_VERIFICATION',
  LOGIN: 'LOGIN',
}

// Error codes and messages
export const AUTH_ERRORS = {
  INVALID_CREDENTIALS: 'Invalid credentials',
  USER_EXISTS: 'User already exists',
  INVALID_OTP: 'Invalid or expired OTP',
  OTP_EXPIRED: 'OTP has expired',
  MAX_ATTEMPTS_EXCEEDED: 'Maximum verification attempts exceeded',
  RATE_LIMIT_EXCEEDED: 'Too many requests. Please try again later',
  TOKEN_EXPIRED: 'Session expired. Please login again',
  NETWORK_ERROR: 'Network error. Please check your connection',
  SERVER_ERROR: 'Server error. Please try again later',
}

// Success messages
export const AUTH_SUCCESS = {
  REGISTRATION_SUCCESS: 'Registration successful! Please verify your email',
  EMAIL_VERIFIED: 'Email verified successfully!',
  OTP_SENT: 'OTP sent to your email',
  MAGIC_LINK_SENT: 'Magic link sent to your email',
  LOGIN_SUCCESS: 'Login successful!',
}

// Timers (in seconds)
export const TIMERS = {
  OTP_RESEND: 60,
  MAGIC_LINK_RESEND: 60,
  OTP_EXPIRY: 300, // 5 minutes
  MAGIC_LINK_EXPIRY: 900, // 15 minutes
}

// Routes
export const ROUTES = {
  LOGIN: '/login',
  REGISTER: '/register',
  VERIFY_EMAIL: '/verify-email',
  OTP_LOGIN: '/login/otp',
  MAGIC_LINK_REQUEST: '/login/magic-link',
  MAGIC_LINK_VERIFY: '/auth/magic-link/verify',
  DASHBOARD: '/dashboard',
  NOT_FOUND: '/404',
}
