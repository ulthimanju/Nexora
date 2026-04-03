import { useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { AuthLayout } from '../components/layout/AuthLayout'
import { PageMeta } from '../components/layout/PageMeta'
import { AuthCard } from '../components/molecules/AuthCard'
import { AuthHeader } from '../components/molecules/AuthHeader'
import { RegisterForm } from '../components/organisms/RegisterForm'
import { useAuth } from '../hooks/useAuth'
import { ROUTES } from '../constants/authConstants'

export const RegisterPage = () => {
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
      <PageMeta title="Register" />
      <AuthCard>
        <AuthHeader title="Create your account" subtitle="Join Nexora today" />
        <RegisterForm />
      </AuthCard>
    </AuthLayout>
  )
}
