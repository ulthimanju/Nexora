import { useState, useEffect, useRef } from 'react'

export const useCountdown = (initialSeconds = 60) => {
  const [seconds, setSeconds] = useState(initialSeconds)
  const [isActive, setIsActive] = useState(false)
  const intervalRef = useRef(null)

  useEffect(() => {
    if (isActive && seconds > 0) {
      intervalRef.current = setInterval(() => {
        setSeconds((prev) => {
          if (prev <= 1) {
            setIsActive(false)
            return 0
          }
          return prev - 1
        })
      }, 1000)
    } else {
      if (intervalRef.current) {
        clearInterval(intervalRef.current)
      }
    }

    return () => {
      if (intervalRef.current) {
        clearInterval(intervalRef.current)
      }
    }
  }, [isActive, seconds])

  const start = (customSeconds = initialSeconds) => {
    setSeconds(customSeconds)
    setIsActive(true)
  }

  const reset = () => {
    setSeconds(initialSeconds)
    setIsActive(false)
  }

  const stop = () => {
    setIsActive(false)
  }

  return {
    seconds,
    isActive,
    isComplete: seconds === 0,
    start,
    reset,
    stop,
  }
}
