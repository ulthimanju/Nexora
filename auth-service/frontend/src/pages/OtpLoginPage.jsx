import { useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { AuthLayout } from '../components/layout/AuthLayout'
import { PageMeta } from '../components/layout/PageMeta'
import { AuthCard } from '../components/molecules/AuthCard'
import { AuthHeader } from '../components/molecules/AuthHeader'
import { OtpLoginForm } from '../components/organisms/OtpLoginForm'
import { useAuth } from '../hooks/useAuth'
import { ROUTES } from '../constants/authConstants'

export const OtpLoginPage = () => {
  const navigate = useNavigate()
  const { isAuthenticated } = useAuth()

  // Redirect if already authenticated
  useEffect(() => {
    if (isAuthenticated) {
      navigate(ROUTES.DASHBOARD)
    }
  }, [isAuthenticated, navigate])

  return (
    <AuthLayout>
      <PageMeta title="Login with OTP" />
      <AuthCard>
        <AuthHeader title="Login with OTP" subtitle="We'll send a code to your email" />
        <OtpLoginForm />
      </AuthCard>
    </AuthLayout>
  )
}
