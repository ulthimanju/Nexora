import { cn } from '../../lib/cn'

export const ErrorText = ({ children, className, ...rest }) => {
  if (!children) return null

  return (
    <p className={cn('text-sm text-error mt-1', className)} {...rest}>
      {children}
    </p>
  )
}
