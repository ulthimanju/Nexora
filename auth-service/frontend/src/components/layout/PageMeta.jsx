import { useEffect } from 'react'

export const PageMeta = ({ title }) => {
  useEffect(() => {
    document.title = title ? `${title} | Nexora Auth` : 'Nexora Auth'
  }, [title])

  return null
}
