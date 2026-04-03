import { useEffect, useState } from 'react'
import { useNavigate, useSearchParams } from 'react-router-dom'
import { AlertCircle } from 'lucide-react'
import { verifyMagicLink } from '../../services/authService'
import { useAuth } from '../../hooks/useAuth'
import { Spinner } from '../atoms/Spinner'
import { Button } from '../atoms/Button'
import { ROUTES } from '../../constants/authConstants'

export const MagicLinkVerifyHandler = () => {
  const navigate = useNavigate()
  const [searchParams] = useSearchParams()
  const { setTokens, setUser } = useAuth()
  const [status, setStatus] = useState('loading') // loading, success, error
  const [error, setError] = useState('')

  useEffect(() => {
    const token = searchParams.get('token')

    if (!token) {
      setStatus('error')
      setError('Invalid or missing token')
      return
    }

    const verify = async () => {
      try {
        const response = await verifyMagicLink(token)

        // Store tokens and user
        setTokens(response.accessToken, response.refreshToken)
        setUser(response.user)

        setStatus('success')

        // Redirect to dashboard after short delay
        setTimeout(() => {
          navigate(ROUTES.DASHBOARD)
        }, 1000)
      } catch (err) {
        setStatus('error')
        setError(err.message || 'Magic link verification failed')
      }
    }

    verify()
  }, [searchParams, setTokens, setUser, navigate])

  if (status === 'loading') {
    return (
      <div className="text-center space-y-4">
        <Spinner size="lg" className="mx-auto" />
        <p className="text-sm text-secondary">Verifying your magic link...</p>
      </div>
    )
  }

  if (status === 'success') {
    return (
      <div className="text-center space-y-4 animate-fadeIn">
        <div className="w-16 h-16 bg-success/10 rounded-full flex items-center justify-center mx-auto">
          <svg
            className="w-8 h-8 text-success"
            fill="none"
            stroke="currentColor"
            viewBox="0 0 24 24"
          >
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              strokeWidth={2}
              d="M5 13l4 4L19 7"
            />
          </svg>
        </div>
        <h3 className="text-lg font-semibold text-primary">Login successful!</h3>
        <p className="text-sm text-secondary">Redirecting to dashboard...</p>
      </div>
    )
  }

  return (
    <div className="text-center space-y-6 animate-fadeIn">
      <div className="w-16 h-16 bg-error/10 rounded-full flex items-center justify-center mx-auto">
        <AlertCircle size={32} className="text-error" />
      </div>

      <div>
        <h3 className="text-lg font-semibold text-primary mb-2">Verification failed</h3>
        <p className="text-sm text-error">{error}</p>
      </div>

      <div className="bg-error/10 border border-error/20 rounded-lg p-4">
        <p className="text-sm text-secondary">
          Your magic link may have expired or is invalid. Please request a new one.
        </p>
      </div>

      <Button variant="primary" fullWidth onClick={() => navigate(ROUTES.MAGIC_LINK_REQUEST)}>
        Request a new magic link
      </Button>

      <p className="text-center text-sm text-secondary">
        <button
          onClick={() => navigate(ROUTES.LOGIN)}
          className="text-accent hover:text-accent-hover hover:underline underline-offset-4"
        >
          Back to login
        </button>
      </p>
    </div>
  )
}
