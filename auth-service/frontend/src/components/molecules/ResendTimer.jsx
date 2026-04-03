import { useCountdown } from '../../hooks/useCountdown'
import { useEffect } from 'react'
import { cn } from '../../lib/cn'

export const ResendTimer = ({ seconds = 60, onResend, loading = false, className }) => {
  const { seconds: remaining, isComplete, start } = useCountdown(seconds)

  useEffect(() => {
    start(seconds)
  }, [])

  const handleResend = () => {
    if (!loading && isComplete) {
      onResend?.()
      start(seconds)
    }
  }

  const formatTime = (sec) => {
    const mins = Math.floor(sec / 60)
    const secs = sec % 60
    return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`
  }

  return (
    <div className={cn('text-center text-sm', className)}>
      {isComplete ? (
        <button
          onClick={handleResend}
          disabled={loading}
          className="text-accent hover:text-accent-hover hover:underline underline-offset-4 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
        >
          {loading ? 'Sending...' : 'Resend OTP'}
        </button>
      ) : (
        <span className="text-secondary">Resend OTP in {formatTime(remaining)}</span>
      )}
    </div>
  )
}
