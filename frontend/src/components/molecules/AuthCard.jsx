import { cn } from '../../lib/cn'

export const AuthCard = ({ children, className, shake = false }) => {
  return (
    <div
      className={cn(
        'bg-surface border border-border rounded-xl p-8 shadow-sm max-w-md w-full mx-auto',
        shake && 'animate-shake',
        className
      )}
    >
      {children}
    </div>
  )
}
