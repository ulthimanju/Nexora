import { useRef, useEffect, useState } from 'react'
import { cn } from '../../lib/cn'
import { ErrorText } from '../atoms/ErrorText'

export const OtpInput = ({ length = 6, value = '', onChange, onComplete, error, disabled }) => {
  const [otp, setOtp] = useState(value.split('').slice(0, length))
  const inputRefs = useRef([])

  useEffect(() => {
    setOtp(value.split('').slice(0, length))
  }, [value, length])

  useEffect(() => {
    if (inputRefs.current[0]) {
      inputRefs.current[0].focus()
    }
  }, [])

  const handleChange = (index, newValue) => {
    // Only allow digits
    if (newValue && !/^\d$/.test(newValue)) return

    const newOtp = [...otp]
    newOtp[index] = newValue
    setOtp(newOtp)

    const otpString = newOtp.join('')
    onChange?.(otpString)

    // Auto-focus next input
    if (newValue && index < length - 1) {
      inputRefs.current[index + 1]?.focus()
    }

    // Auto-submit when all filled
    if (newValue && index === length - 1 && newOtp.every((digit) => digit)) {
      onComplete?.(otpString)
    }
  }

  const handleKeyDown = (index, e) => {
    if (e.key === 'Backspace') {
      if (!otp[index] && index > 0) {
        // Focus previous input on backspace if current is empty
        inputRefs.current[index - 1]?.focus()
      } else {
        // Clear current input
        const newOtp = [...otp]
        newOtp[index] = ''
        setOtp(newOtp)
        onChange?.(newOtp.join(''))
      }
    } else if (e.key === 'ArrowLeft' && index > 0) {
      inputRefs.current[index - 1]?.focus()
    } else if (e.key === 'ArrowRight' && index < length - 1) {
      inputRefs.current[index + 1]?.focus()
    }
  }

  const handlePaste = (e) => {
    e.preventDefault()
    const pastedData = e.clipboardData.getData('text/plain').slice(0, length)
    if (!/^\d+$/.test(pastedData)) return

    const newOtp = pastedData.split('').concat(Array(length).fill('')).slice(0, length)
    setOtp(newOtp)
    onChange?.(newOtp.join(''))

    // Focus last filled input or first empty
    const nextIndex = Math.min(pastedData.length, length - 1)
    inputRefs.current[nextIndex]?.focus()

    // Auto-submit if all filled
    if (pastedData.length === length) {
      onComplete?.(newOtp.join(''))
    }
  }

  return (
    <div>
      <div className="flex gap-2 justify-center">
        {Array.from({ length }).map((_, index) => (
          <input
            key={index}
            ref={(el) => (inputRefs.current[index] = el)}
            type="text"
            inputMode="numeric"
            maxLength={1}
            value={otp[index] || ''}
            onChange={(e) => handleChange(index, e.target.value)}
            onKeyDown={(e) => handleKeyDown(index, e)}
            onPaste={handlePaste}
            disabled={disabled}
            className={cn(
              'w-12 h-12 text-center text-lg font-semibold bg-surface border rounded-lg transition-all duration-200 ease-in-out focus:outline-none disabled:opacity-50 disabled:cursor-not-allowed',
              error
                ? 'border-error ring-2 ring-error/30 focus:border-error focus:ring-error/30 animate-shake'
                : 'border-border focus:border-accent focus:ring-2 focus:ring-accent/40'
            )}
          />
        ))}
      </div>
      <ErrorText className="text-center mt-2">{error}</ErrorText>
    </div>
  )
}
