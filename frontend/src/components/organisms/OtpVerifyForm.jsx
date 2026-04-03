import { useState } from 'react'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { useNavigate } from 'react-router-dom'
import { verifyEmailSchema } from '../../lib/validators'
import { verifyEmail, resendOtp } from '../../services/authService'
import { OtpInput } from '../molecules/OtpInput'
import { Button } from '../atoms/Button'
import { ResendTimer } from '../molecules/ResendTimer'
import { AlertBanner } from '../molecules/AlertBanner'
import { ROUTES, OTP_TYPES, TIMERS } from '../../constants/authConstants'

export const OtpVerifyForm = ({ email }) => {
  const navigate = useNavigate()
  const [error, setError] = useState('')
  const [success, setSuccess] = useState('')
  const [loading, setLoading] = useState(false)
  const [resendLoading, setResendLoading] = useState(false)

  const {
    setValue,
    watch,
    handleSubmit,
    formState: { errors },
  } = useForm({
    resolver: zodResolver(verifyEmailSchema),
    defaultValues: { email, otp: '' },
  })

  const otp = watch('otp')

  const onSubmit = async (data) => {
    try {
      setLoading(true)
      setError('')
      setSuccess('')

      await verifyEmail(data.email, data.otp)

      setSuccess('Email verified successfully!')

      // Redirect to login after short delay
      setTimeout(() => {
        navigate(ROUTES.LOGIN, {
          state: { message: 'Email verified! Please login to continue.' },
        })
      }, 1500)
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

      await resendOtp(email, OTP_TYPES.EMAIL_VERIFICATION)

      setSuccess('OTP resent successfully!')
    } catch (err) {
      setError(err.message || 'Failed to resend OTP. Please try again.')
    } finally {
      setResendLoading(false)
    }
  }

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
      {error && <AlertBanner type="error" message={error} onClose={() => setError('')} />}
      {success && <AlertBanner type="success" message={success} />}

      <div className="text-center mb-6">
        <p className="text-sm text-secondary">
          Enter the 6-digit code sent to
        </p>
        <p className="text-sm font-medium text-primary mt-1">{email}</p>
      </div>

      <OtpInput
        length={6}
        value={otp}
        onChange={(value) => setValue('otp', value)}
        onComplete={() => handleSubmit(onSubmit)()}
        error={errors.otp?.message || error}
        disabled={loading}
      />

      <Button type="submit" variant="primary" fullWidth loading={loading}>
        Verify Email
      </Button>

      <ResendTimer
        seconds={TIMERS.OTP_RESEND}
        onResend={handleResend}
        loading={resendLoading}
      />
    </form>
  )
}
