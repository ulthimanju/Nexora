import { useState } from 'react'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { Link } from 'react-router-dom'
import { CheckCircle, Mail } from 'lucide-react'
import { magicLinkRequestSchema } from '../../lib/validators'
import { requestMagicLink, resendOtp } from '../../services/authService'
import { FormField } from '../molecules/FormField'
import { Button } from '../atoms/Button'
import { ResendTimer } from '../molecules/ResendTimer'
import { AlertBanner } from '../molecules/AlertBanner'
import { ROUTES, TIMERS } from '../../constants/authConstants'

export const MagicLinkRequestForm = () => {
  const [sent, setSent] = useState(false)
  const [email, setEmail] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)
  const [resendLoading, setResendLoading] = useState(false)

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm({
    resolver: zodResolver(magicLinkRequestSchema),
  })

  const onSubmit = async (data) => {
    try {
      setLoading(true)
      setError('')

      await requestMagicLink(data.email)

      setEmail(data.email)
      setSent(true)
    } catch (err) {
      setError(err.message || 'Failed to send magic link. Please try again.')
    } finally {
      setLoading(false)
    }
  }

  const handleResend = async () => {
    try {
      setResendLoading(true)
      setError('')

      await requestMagicLink(email)
    } catch (err) {
      setError(err.message || 'Failed to resend magic link. Please try again.')
    } finally {
      setResendLoading(false)
    }
  }

  if (sent) {
    return (
      <div className="space-y-6 text-center animate-fadeIn">
        <div className="flex justify-center">
          <div className="w-16 h-16 bg-success/10 rounded-full flex items-center justify-center">
            <CheckCircle size={32} className="text-success" />
          </div>
        </div>

        <div>
          <h3 className="text-lg font-semibold text-primary mb-2">Magic link sent!</h3>
          <p className="text-sm text-secondary">Check your inbox at</p>
          <p className="text-sm font-medium text-primary mt-1">{email}</p>
        </div>

        <div className="bg-accent-light border border-accent/20 rounded-lg p-4">
          <div className="flex items-start gap-3">
            <Mail size={20} className="text-accent flex-shrink-0 mt-0.5" />
            <div className="text-left text-sm text-secondary">
              <p className="mb-2">Click the link in your email to sign in.</p>
              <p className="text-xs">Link expires in 15 minutes.</p>
            </div>
          </div>
        </div>

        {error && <AlertBanner type="error" message={error} onClose={() => setError('')} />}

        <ResendTimer
          seconds={TIMERS.MAGIC_LINK_RESEND}
          onResend={handleResend}
          loading={resendLoading}
        />

        <p className="text-center text-sm text-secondary mt-6">
          <Link
            to={ROUTES.LOGIN}
            className="text-accent hover:text-accent-hover hover:underline underline-offset-4"
          >
            Back to login
          </Link>
        </p>
      </div>
    )
  }

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="space-y-5">
      {error && <AlertBanner type="error" message={error} onClose={() => setError('')} />}

      <FormField
        label="Email"
        name="email"
        type="email"
        placeholder="Enter your email"
        error={errors.email?.message}
        required
        {...register('email')}
      />

      <Button type="submit" variant="primary" fullWidth loading={loading}>
        Send Magic Link
      </Button>

      <p className="text-center text-sm text-secondary mt-6">
        <Link
          to={ROUTES.LOGIN}
          className="text-accent hover:text-accent-hover hover:underline underline-offset-4"
        >
          Back to login
        </Link>
      </p>
    </form>
  )
}
