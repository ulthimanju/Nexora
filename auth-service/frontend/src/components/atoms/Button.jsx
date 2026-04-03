import { cn } from '../../lib/cn'
import { Spinner } from './Spinner'

export const Button = ({
  variant = 'primary',
  size = 'md',
  loading = false,
  disabled = false,
  fullWidth = false,
  leftIcon,
  rightIcon,
  children,
  className,
  ...rest
}) => {
  const baseStyles =
    'inline-flex items-center justify-center font-medium transition-all duration-200 ease-in-out focus:outline-none focus:ring-2 focus:ring-offset-2 disabled:opacity-50 disabled:cursor-not-allowed'

  const variantStyles = {
    primary:
      'bg-accent text-white hover:bg-accent-hover focus:ring-accent/40 active:bg-accent-hover',
    ghost:
      'bg-transparent border border-border text-primary hover:bg-surface focus:ring-accent/40',
    danger:
      'bg-error text-white hover:bg-error/90 focus:ring-error/40 active:bg-error/90',
  }

  const sizeStyles = {
    sm: 'h-9 px-3 text-sm rounded-lg',
    md: 'h-11 px-4 text-base rounded-lg',
    lg: 'h-12 px-6 text-lg rounded-lg',
  }

  return (
    <button
      disabled={disabled || loading}
      className={cn(
        baseStyles,
        variantStyles[variant],
        sizeStyles[size],
        fullWidth && 'w-full',
        className
      )}
      {...rest}
    >
      {loading ? (
        <Spinner size={size === 'sm' ? 'sm' : 'md'} />
      ) : (
        <>
          {leftIcon && <span className="mr-2">{leftIcon}</span>}
          {children}
          {rightIcon && <span className="ml-2">{rightIcon}</span>}
        </>
      )}
    </button>
  )
}
