import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { LoginPage } from '../pages/LoginPage'
import { RegisterPage } from '../pages/RegisterPage'
import { VerifyEmailPage } from '../pages/VerifyEmailPage'
import { OtpLoginPage } from '../pages/OtpLoginPage'
import { MagicLinkRequestPage } from '../pages/MagicLinkRequestPage'
import { MagicLinkVerifyPage } from '../pages/MagicLinkVerifyPage'
import { NotFoundPage } from '../pages/NotFoundPage'
import { ProtectedRoute } from './ProtectedRoute'
import { ROUTES } from '../constants/authConstants'

// Placeholder Dashboard component
const DashboardPage = () => {
  return (
    <div className="min-h-screen bg-background flex items-center justify-center">
      <div className="bg-surface border border-border rounded-xl p-8 shadow-sm max-w-2xl w-full mx-4">
        <h1 className="text-3xl font-semibold text-primary mb-4">Dashboard</h1>
        <p className="text-secondary mb-4">
          Welcome to Nexora! This is a placeholder dashboard page.
        </p>
        <p className="text-sm text-secondary">
          The authentication system is fully functional. You can now integrate this with your
          backend services.
        </p>
      </div>
    </div>
  )
}

export const AppRouter = () => {
  return (
    <BrowserRouter>
      <Routes>
        {/* Public routes */}
        <Route path={ROUTES.LOGIN} element={<LoginPage />} />
        <Route path={ROUTES.REGISTER} element={<RegisterPage />} />
        <Route path={ROUTES.VERIFY_EMAIL} element={<VerifyEmailPage />} />
        <Route path={ROUTES.OTP_LOGIN} element={<OtpLoginPage />} />
        <Route path={ROUTES.MAGIC_LINK_REQUEST} element={<MagicLinkRequestPage />} />
        <Route path={ROUTES.MAGIC_LINK_VERIFY} element={<MagicLinkVerifyPage />} />

        {/* Protected routes */}
        <Route
          path={ROUTES.DASHBOARD}
          element={
            <ProtectedRoute>
              <DashboardPage />
            </ProtectedRoute>
          }
        />

        {/* Default redirect */}
        <Route path="/" element={<Navigate to={ROUTES.LOGIN} replace />} />

        {/* 404 */}
        <Route path="*" element={<NotFoundPage />} />
      </Routes>
    </BrowserRouter>
  )
}
