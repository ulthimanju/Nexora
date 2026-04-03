import { useState } from 'react'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { Link, useNavigate } from 'react-router-dom'
import { loginSchema } from '../../lib/validators'
import { login } from '../../services/authService'
import { useAuth } from '../../hooks/useAuth'
import { FormField } from '../molecules/FormField'
import { PasswordField } from '../molecules/PasswordField'
import { Button } from '../atoms/Button'
import { Divider } from '../atoms/Divider'
import { AlertBanner } from '../molecules/AlertBanner'
import { ROUTES } from '../../constants/authConstants'

export const LoginForm = () => {
  const navigate = useNavigate()
  const { setTokens, setUser } = useAuth()
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm({
    resolver: zodResolver(loginSchema),
  })

  const onSubmit = async (data) => {
    try {
      setLoading(true)
      setError('')

      const response = await login(data.usernameOrEmail, data.password)

      // Store tokens and user
      setTokens(response.accessToken, response.refreshToken)
      setUser(response.user)

      // Redirect to dashboard
      navigate(ROUTES.DASHBOARD)
    } catch (err) {
      setError(err.message || 'Login failed. Please try again.')
    } finally {
      setLoading(false)
    }
  }

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="space-y-5">
      {error && <AlertBanner type="error" message={error} onClose={() => setError('')} />}

      <FormField
        label="Username or Email"
        name="usernameOrEmail"
        placeholder="Enter your username or email"
        error={errors.usernameOrEmail?.message}
        required
        {...register('usernameOrEmail')}
      />

      <PasswordField
        label="Password"
        name="password"
        placeholder="Enter your password"
        error={errors.password?.message}
        required
        {...register('password')}
      />

      <div className="flex justify-end">
        <Link
          to="#"
          className="text-sm text-accent hover:text-accent-hover hover:underline underline-offset-4"
        >
          Forgot password?
        </Link>
      </div>

      <Button type="submit" variant="primary" fullWidth loading={loading}>
        Login
      </Button>

      <Divider text="or continue with" />

      <div className="space-y-3">
        <Button
          type="button"
          variant="ghost"
          fullWidth
          onClick={() => navigate(ROUTES.OTP_LOGIN)}
        >
          Login with OTP
        </Button>
        <Button
          type="button"
          variant="ghost"
          fullWidth
          onClick={() => navigate(ROUTES.MAGIC_LINK_REQUEST)}
        >
          Login with Magic Link
        </Button>
      </div>

      <p className="text-center text-sm text-secondary mt-6">
        Don't have an account?{' '}
        <Link
          to={ROUTES.REGISTER}
          className="text-accent hover:text-accent-hover hover:underline underline-offset-4 font-medium"
        >
          Register
        </Link>
      </p>
    </form>
  )
}
