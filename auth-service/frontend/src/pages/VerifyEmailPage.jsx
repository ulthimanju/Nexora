import { useLocation, useNavigate } from 'react-router-dom'
import { useEffect } from 'react'
import { AuthLayout } from '../components/layout/AuthLayout'
import { PageMeta } from '../components/layout/PageMeta'
import { AuthCard } from '../components/molecules/AuthCard'
import { AuthHeader } from '../components/molecules/AuthHeader'
import { OtpVerifyForm } from '../components/organisms/OtpVerifyForm'
import { ROUTES } from '../constants/authConstants'

export const VerifyEmailPage = () => {
  const location = useLocation()
  const navigate = useNavigate()
  const email = location.state?.email || new URLSearchParams(location.search).get('email')

  // Redirect to register if no email
  useEffect(() => {
    if (!email) {
      navigate(ROUTES.REGISTER)
    }
  }, [email, navigate])

  if (!email) return null

  return (
    <AuthLayout>
      <PageMeta title="Verify Email" />
      <AuthCard>
        <AuthHeader
          title="Verify your email"
          subtitle="We've sent a verification code to your email"
        />
        <OtpVerifyForm email={email} />
      </AuthCard>
    </AuthLayout>
  )
}
