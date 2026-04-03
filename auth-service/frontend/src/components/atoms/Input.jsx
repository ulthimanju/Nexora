import { forwardRef } from 'react'
import { cn } from '../../lib/cn'

export const Input = forwardRef(
  ({ type = 'text', placeholder, error, disabled, leftIcon, rightIcon, className, ...rest }, ref) => {
    const baseStyles =
      'w-full h-11 px-4 text-base text-primary bg-surface border rounded-lg transition-all duration-200 ease-in-out placeholder:text-placeholder focus:outline-none disabled:opacity-50 disabled:cursor-not-allowed'

    const stateStyles = error
      ? 'border-error ring-2 ring-error/30 focus:border-error focus:ring-error/30'
      : 'border-border focus:border-accent focus:ring-2 focus:ring-accent/40'

    const paddingStyles = cn(
      leftIcon && 'pl-10',
      rightIcon && 'pr-10'
    )

    return (
      <div className="relative w-full">
        {leftIcon && (
          <div className="absolute left-3 top-1/2 -translate-y-1/2 text-secondary">
            {leftIcon}
          </div>
        )}
        <input
          ref={ref}
          type={type}
          placeholder={placeholder}
          disabled={disabled}
          className={cn(baseStyles, stateStyles, paddingStyles, className)}
          {...rest}
        />
        {rightIcon && (
          <div className="absolute right-3 top-1/2 -translate-y-1/2 text-secondary">
            {rightIcon}
          </div>
        )}
      </div>
    )
  }
)

Input.displayName = 'Input'
