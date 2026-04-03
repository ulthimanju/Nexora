import { useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { AuthLayout } from '../components/layout/AuthLayout'
import { PageMeta } from '../components/layout/PageMeta'
import { AuthCard } from '../components/molecules/AuthCard'
import { AuthHeader } from '../components/molecules/AuthHeader'
import { MagicLinkRequestForm } from '../components/organisms/MagicLinkRequestForm'
import { useAuth } from '../hooks/useAuth'
import { ROUTES } from '../constants/authConstants'

export const MagicLinkRequestPage = () => {
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
      <PageMeta title="Login with Magic Link" />
      <AuthCard>
        <AuthHeader
          title="Login with Magic Link"
          subtitle="We'll send a secure link to your email"
        />
        <MagicLinkRequestForm />
      </AuthCard>
    </AuthLayout>
  )
}
