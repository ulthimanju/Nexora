import { forwardRef, useState } from 'react'
import { Eye, EyeOff } from 'lucide-react'
import { FormField } from './FormField'
import { Input } from '../atoms/Input'

export const PasswordField = forwardRef(({ label = 'Password', name = 'password', error, required, ...rest }, ref) => {
  const [showPassword, setShowPassword] = useState(false)

  const togglePassword = () => setShowPassword((prev) => !prev)

  return (
    <FormField label={label} name={name} error={error} required={required}>
      <Input
        ref={ref}
        id={name}
        name={name}
        type={showPassword ? 'text' : 'password'}
        error={!!error}
        rightIcon={
          <button
            type="button"
            onClick={togglePassword}
            className="text-secondary hover:text-primary transition-colors"
            tabIndex={-1}
          >
            {showPassword ? <EyeOff size={18} /> : <Eye size={18} />}
          </button>
        }
        {...rest}
      />
    </FormField>
  )
})

PasswordField.displayName = 'PasswordField'
