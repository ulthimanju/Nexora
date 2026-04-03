import apiClient from './apiClient'
import { API_ENDPOINTS } from '../constants/authConstants'

// Login with username/email and password
export const login = async (usernameOrEmail, password) => {
  const response = await apiClient.post(API_ENDPOINTS.LOGIN, {
    usernameOrEmail,
    password,
  })
  return response.data
}

// Register new user
export const register = async (username, email, password) => {
  const response = await apiClient.post(API_ENDPOINTS.REGISTER, {
    username,
    email,
    password,
  })
  return response.data
}

// Verify email with OTP
export const verifyEmail = async (email, otp) => {
  const response = await apiClient.post(API_ENDPOINTS.VERIFY_EMAIL, {
    email,
    otp,
  })
  return response.data
}

// Resend OTP
export const resendOtp = async (email, type) => {
  const response = await apiClient.post(API_ENDPOINTS.RESEND_OTP, {
    email,
    type,
  })
  return response.data
}

// Request OTP for login
export const requestOtpLogin = async (email) => {
  const response = await apiClient.post(API_ENDPOINTS.REQUEST_OTP_LOGIN, {
    email,
  })
  return response.data
}

// Verify OTP for login
export const verifyOtpLogin = async (email, otp) => {
  const response = await apiClient.post(API_ENDPOINTS.VERIFY_OTP_LOGIN, {
    email,
    otp,
  })
  return response.data
}

// Request magic link
export const requestMagicLink = async (email) => {
  const response = await apiClient.post(API_ENDPOINTS.REQUEST_MAGIC_LINK, {
    email,
  })
  return response.data
}

// Verify magic link token
export const verifyMagicLink = async (token) => {
  const response = await apiClient.get(API_ENDPOINTS.VERIFY_MAGIC_LINK, {
    params: { token },
  })
  return response.data
}

// Logout
export const logout = async () => {
  const response = await apiClient.post(API_ENDPOINTS.LOGOUT)
  return response.data
}
