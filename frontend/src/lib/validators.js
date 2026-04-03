import { z } from 'zod'

// Login validation
export const loginSchema = z.object({
  usernameOrEmail: z
    .string()
    .min(1, 'Username or email is required')
    .trim(),
  password: z.string().min(1, 'Password is required'),
})

// Register validation
export const registerSchema = z
  .object({
    username: z
      .string()
      .min(3, 'Username must be at least 3 characters')
      .max(50, 'Username must not exceed 50 characters')
      .regex(/^[a-zA-Z0-9_]+$/, 'Username can only contain letters, numbers, and underscores')
      .trim(),
    email: z
      .string()
      .min(1, 'Email is required')
      .email('Please enter a valid email address')
      .trim()
      .toLowerCase(),
    password: z
      .string()
      .min(8, 'Password must be at least 8 characters')
      .regex(/[A-Z]/, 'Password must contain at least one uppercase letter')
      .regex(/[0-9]/, 'Password must contain at least one number')
      .regex(/[^A-Za-z0-9]/, 'Password must contain at least one special character'),
    confirmPassword: z.string().min(1, 'Please confirm your password'),
  })
  .refine((data) => data.password === data.confirmPassword, {
    message: "Passwords don't match",
    path: ['confirmPassword'],
  })

// Email verification OTP
export const verifyEmailSchema = z.object({
  email: z.string().email('Invalid email').trim().toLowerCase(),
  otp: z
    .string()
    .length(6, 'OTP must be 6 digits')
    .regex(/^\d+$/, 'OTP must contain only numbers'),
})

// OTP Login - Phase 1
export const otpRequestSchema = z.object({
  email: z.string().email('Invalid email').trim().toLowerCase(),
})

// OTP Login - Phase 2
export const otpVerifySchema = z.object({
  email: z.string().email('Invalid email').trim().toLowerCase(),
  otp: z
    .string()
    .length(6, 'OTP must be 6 digits')
    .regex(/^\d+$/, 'OTP must contain only numbers'),
})

// Magic link request
export const magicLinkRequestSchema = z.object({
  email: z.string().email('Invalid email').trim().toLowerCase(),
})

// Password strength checker
export const checkPasswordStrength = (password) => {
  if (!password) return { level: 0, label: 'Too Weak', color: 'error' }

  let score = 0
  if (password.length >= 8) score++
  if (/[A-Z]/.test(password)) score++
  if (/[0-9]/.test(password)) score++
  if (/[^A-Za-z0-9]/.test(password)) score++

  if (score === 4) return { level: 4, label: 'Strong', color: 'success' }
  if (score === 3) return { level: 3, label: 'Medium', color: 'warning' }
  if (score === 2) return { level: 2, label: 'Weak', color: 'warning' }
  return { level: 1, label: 'Too Weak', color: 'error' }
}
