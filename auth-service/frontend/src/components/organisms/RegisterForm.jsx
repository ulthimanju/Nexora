import { useState, useEffect } from 'react'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { Link, useNavigate } from 'react-router-dom'
import { registerSchema, checkPasswordStrength } from '../../lib/validators'
import { register as registerUser } from '../../services/authService'
import { FormField } from '../molecules/FormField'
import { PasswordField } from '../molecules/PasswordField'
import { Button } from '../atoms/Button'
import { AlertBanner } from '../molecules/AlertBanner'
import { ROUTES } from '../../constants/authConstants'
import { cn } from '../../lib/cn'

export const RegisterForm = () => {
  const navigate = useNavigate()
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)
  const [passwordStrength, setPasswordStrength] = useState({ level: 0, label: 'Too Weak', color: 'error' })

  const {
    register,
    handleSubmit,
    watch,
    formState: { errors },
  } = useForm({
    resolver: zodResolver(registerSchema),
  })

  const password = watch('password', '')

  // Update password strength on password change
  useEffect(() => {
    const strength = checkPasswordStrength(password)
    setPasswordStrength(strength)
  }, [password])

  const onSubmit = async (data) => {
    try {
      setLoading(true)
      setError('')

      await registerUser(data.username, data.email, data.password)

      // Redirect to verify email page with email in state
      navigate(ROUTES.VERIFY_EMAIL, {
        state: { email: data.email },
      })
    } catch (err) {
      setError(err.message || 'Registration failed. Please try again.')
    } finally {
      setLoading(false)
    }
  }

  const getStrengthColor = () => {
    const colors = {
      error: 'bg-error',
      warning: 'bg-warning',
      success: 'bg-success',
    }
    return colors[passwordStrength.color] || 'bg-error'
  }

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="space-y-5">
      {error && <AlertBanner type="error" message={error} onClose={() => setError('')} />}

      <FormField
        label="Username"
        name="username"
        placeholder="Choose a username"
        error={errors.username?.message}
        required
        {...register('username')}
      />

      <FormField
        label="Email"
        name="email"
        type="email"
        placeholder="Enter your email"
        error={errors.email?.message}
        required
        {...register('email')}
      />

      <div>
        <PasswordField
          label="Password"
          name="password"
          placeholder="Create a password"
          error={errors.password?.message}
          required
          {...register('password')}
        />
        {password && (
          <div className="mt-2">
            <div className="flex gap-1 mb-1">
              {[1, 2, 3, 4].map((level) => (
                <div
                  key={level}
                  className={cn(
                    'h-1 flex-1 rounded-full transition-all duration-200',
                    level <= passwordStrength.level ? getStrengthColor() : 'bg-border'
                  )}
                />
              ))}
            </div>
            <p className={cn('text-xs', `text-${passwordStrength.color}`)}>
              {passwordStrength.label}
            </p>
          </div>
        )}
      </div>

      <PasswordField
        label="Confirm Password"
        name="confirmPassword"
        placeholder="Confirm your password"
        error={errors.confirmPassword?.message}
        required
        {...register('confirmPassword')}
      />

      <Button type="submit" variant="primary" fullWidth loading={loading}>
        Create Account
      </Button>

      <p className="text-center text-sm text-secondary mt-6">
        Already have an account?{' '}
        <Link
          to={ROUTES.LOGIN}
          className="text-accent hover:text-accent-hover hover:underline underline-offset-4 font-medium"
        >
          Login
        </Link>
      </p>
    </form>
  )
}
