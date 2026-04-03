import { useEffect } from 'react'
import { useLocation, useNavigate } from 'react-router-dom'
import { AuthLayout } from '../components/layout/AuthLayout'
import { PageMeta } from '../components/layout/PageMeta'
import { AuthCard } from '../components/molecules/AuthCard'
import { AuthHeader } from '../components/molecules/AuthHeader'
import { LoginForm } from '../components/organisms/LoginForm'
import { AlertBanner } from '../components/molecules/AlertBanner'
import { useAuth } from '../hooks/useAuth'
import { ROUTES } from '../constants/authConstants'

export const LoginPage = () => {
  const location = useLocation()
  const navigate = useNavigate()
  const { isAuthenticated } = useAuth()
  const message = location.state?.message

  // Redirect if already authenticated
  useEffect(() => {
    if (isAuthenticated) {
      navigate(ROUTES.DASHBOARD)
    }
  }, [isAuthenticated, navigate])

  return (
    <AuthLayout>
      <PageMeta title="Login" />
      <AuthCard>
        <AuthHeader title="Welcome back" subtitle="Sign in to your account" />
        {message && <AlertBanner type="success" message={message} className="mb-5" />}
        <LoginForm />
      </AuthCard>
    </AuthLayout>
  )
}
