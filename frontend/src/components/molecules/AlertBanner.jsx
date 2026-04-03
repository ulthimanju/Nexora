import { X, CheckCircle, AlertCircle, Info, AlertTriangle } from 'lucide-react'
import { cn } from '../../lib/cn'

export const AlertBanner = ({ type = 'info', message, onClose, className }) => {
  if (!message) return null

  const config = {
    success: {
      icon: CheckCircle,
      bgColor: 'bg-success/10',
      borderColor: 'border-l-success',
      textColor: 'text-success',
      iconColor: 'text-success',
    },
    error: {
      icon: AlertCircle,
      bgColor: 'bg-error/10',
      borderColor: 'border-l-error',
      textColor: 'text-error',
      iconColor: 'text-error',
    },
    warning: {
      icon: AlertTriangle,
      bgColor: 'bg-warning/10',
      borderColor: 'border-l-warning',
      textColor: 'text-warning',
      iconColor: 'text-warning',
    },
    info: {
      icon: Info,
      bgColor: 'bg-accent-light',
      borderColor: 'border-l-accent',
      textColor: 'text-accent',
      iconColor: 'text-accent',
    },
  }

  const { icon: Icon, bgColor, borderColor, textColor, iconColor } = config[type]

  return (
    <div
      className={cn(
        'flex items-start gap-3 p-4 rounded-lg border-l-4 animate-fadeIn',
        bgColor,
        borderColor,
        className
      )}
    >
      <Icon size={20} className={cn('flex-shrink-0 mt-0.5', iconColor)} />
      <p className={cn('flex-1 text-sm', textColor)}>{message}</p>
      {onClose && (
        <button
          onClick={onClose}
          className={cn('flex-shrink-0 hover:opacity-70 transition-opacity', textColor)}
        >
          <X size={18} />
        </button>
      )}
    </div>
  )
}
