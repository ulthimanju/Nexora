import { Link } from 'react-router-dom'
import { Home } from 'lucide-react'
import { AuthLayout } from '../components/layout/AuthLayout'
import { PageMeta } from '../components/layout/PageMeta'
import { AuthCard } from '../components/molecules/AuthCard'
import { Button } from '../components/atoms/Button'
import { ROUTES } from '../constants/authConstants'

export const NotFoundPage = () => {
  return (
    <AuthLayout>
      <PageMeta title="404 - Page Not Found" />
      <AuthCard className="text-center">
        <div className="mb-6">
          <div className="text-6xl font-bold text-accent mb-2">404</div>
          <h1 className="text-2xl font-semibold text-primary mb-2">Page not found</h1>
          <p className="text-sm text-secondary">
            The page you're looking for doesn't exist or has been moved.
          </p>
        </div>

        <Link to={ROUTES.LOGIN}>
          <Button variant="primary" fullWidth leftIcon={<Home size={18} />}>
            Go back to login
          </Button>
        </Link>
      </AuthCard>
    </AuthLayout>
  )
}
