import { useAuthStore } from '../store/authStore'

export const useAuth = () => {
  const user = useAuthStore((state) => state.user)
  const accessToken = useAuthStore((state) => state.accessToken)
  const refreshToken = useAuthStore((state) => state.refreshToken)
  const isAuthenticated = useAuthStore((state) => state.isAuthenticated)
  const isLoading = useAuthStore((state) => state.isLoading)
  const setTokens = useAuthStore((state) => state.setTokens)
  const setUser = useAuthStore((state) => state.setUser)
  const setLoading = useAuthStore((state) => state.setLoading)
  const logout = useAuthStore((state) => state.logout)
  const initialize = useAuthStore((state) => state.initialize)

  return {
    user,
    accessToken,
    refreshToken,
    isAuthenticated,
    isLoading,
    setTokens,
    setUser,
    setLoading,
    logout,
    initialize,
  }
}
