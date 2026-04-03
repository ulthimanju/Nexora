import { useState } from 'react'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { useNavigate } from 'react-router-dom'
import { otpRequestSchema, otpVerifySchema } from '../../lib/validators'
import { requestOtpLogin, verifyOtpLogin, resendOtp } from '../../services/authService'
import { useAuth } from '../../hooks/useAuth'
import { FormField } from '../molecules/FormField'
import { OtpInput } from '../molecules/OtpInput'
import { Button } from '../atoms/Button'
import { ResendTimer } from '../molecules/ResendTimer'
import { AlertBanner } from '../molecules/AlertBanner'
import { ROUTES, OTP_TYPES, TIMERS } from '../../constants/authConstants'

export const OtpLoginForm = () => {
  const navigate = useNavigate()
  const { setTokens, setUser } = useAuth()
  const [phase, setPhase] = useState(1) // 1: request, 2: verify
  const [email, setEmail] = useState('')
  const [error, setError] = useState('')
  const [success, setSuccess] = useState('')
  const [loading, setLoading] = useState(false)
  const [resendLoading, setResendLoading] = useState(false)

  // Phase 1 form
  const requestForm = useForm({
    resolver: zodResolver(otpRequestSchema),
  })

  // Phase 2 form
  const verifyForm = useForm({
    resolver: zodResolver(otpVerifySchema),
    defaultValues: { email: '', otp: '' },
  })

  const onRequestSubmit = async (data) => {
    try {
      setLoading(true)
      setError('')
      setSuccess('')

      await requestOtpLogin(data.email)

      setEmail(data.email)
      verifyForm.setValue('email', data.email)
      setPhase(2)
      setSuccess('OTP sent to your email!')
    } catch (err) {
      setError(err.message || 'Failed to send OTP. Please try again.')
    } finally {
      setLoading(false)
    }
  }

  const onVerifySubmit = async (data) => {
    try {
      setLoading(true)
      setError('')
      setSuccess('')

      const response = await verifyOtpLogin(data.email, data.otp)

      // Store tokens and user
      setTokens(response.accessToken, response.refreshToken)
      setUser(response.user)

      // Redirect to dashboard
      navigate(ROUTES.DASHBOARD)
    } catch (err) {
      setError(err.message || 'Verification failed. Please try again.')
    } finally {
      setLoading(false)
    }
  }

  const handleResend = async () => {
    try {
      setResendLoading(true)
      setError('')
      setSuccess('')

      await resendOtp(email, OTP_TYPES.LOGIN)

      setSuccess('OTP resent successfully!')
    } catch (err) {
      setError(err.message || 'Failed to resend OTP. Please try again.')
    } finally {
      setResendLoading(false)
    }
  }

  if (phase === 1) {
    return (
      <form onSubmit={requestForm.handleSubmit(onRequestSubmit)} className="space-y-5">
        {error && <AlertBanner type="error" message={error} onClose={() => setError('')} />}

        <FormField
          label="Email"
          name="email"
          type="email"
          placeholder="Enter your email"
          error={requestForm.formState.errors.email?.message}
          required
          {...requestForm.register('email')}
        />

        <Button type="submit" variant="primary" fullWidth loading={loading}>
          Send OTP
        </Button>

        <p className="text-center text-sm text-secondary mt-6">
          <button
            type="button"
            onClick={() => navigate(ROUTES.LOGIN)}
            className="text-accent hover:text-accent-hover hover:underline underline-offset-4"
          >
            Back to login
          </button>
        </p>
      </form>
    )
  }

  return (
    <form onSubmit={verifyForm.handleSubmit(onVerifySubmit)} className="space-y-6 animate-fadeIn">
      {error && <AlertBanner type="error" message={error} onClose={() => setError('')} />}
      {success && <AlertBanner type="success" message={success} />}

      <div className="text-center mb-6">
        <p className="text-sm text-secondary">Enter the 6-digit code sent to</p>
        <p className="text-sm font-medium text-primary mt-1">{email}</p>
      </div>

      <OtpInput
        length={6}
        value={verifyForm.watch('otp')}
        onChange={(value) => verifyForm.setValue('otp', value)}
        onComplete={() => verifyForm.handleSubmit(onVerifySubmit)()}
        error={verifyForm.formState.errors.otp?.message || error}
        disabled={loading}
      />

      <Button type="submit" variant="primary" fullWidth loading={loading}>
        Verify & Login
      </Button>

      <ResendTimer seconds={TIMERS.OTP_RESEND} onResend={handleResend} loading={resendLoading} />

      <p className="text-center text-sm text-secondary mt-6">
        <button
          type="button"
          onClick={() => {
            setPhase(1)
            setEmail('')
            setError('')
            setSuccess('')
          }}
          className="text-accent hover:text-accent-hover hover:underline underline-offset-4"
        >
          Back to login
        </button>
      </p>
    </form>
  )
}
