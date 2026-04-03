import axios from 'axios'
import { useAuthStore } from '../store/authStore'
import { API_ENDPOINTS, AUTH_ERRORS, ROUTES } from '../constants/authConstants'

// Create axios instance
const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8081',
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 10000,
})

// Request interceptor - attach access token
apiClient.interceptors.request.use(
  (config) => {
    const { accessToken } = useAuthStore.getState()
    if (accessToken) {
      config.headers.Authorization = `Bearer ${accessToken}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// Response interceptor - handle token refresh on 401
let isRefreshing = false
let failedQueue = []

const processQueue = (error, token = null) => {
  failedQueue.forEach((prom) => {
    if (error) {
      prom.reject(error)
    } else {
      prom.resolve(token)
    }
  })
  failedQueue = []
}

apiClient.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config

    // Handle network errors
    if (!error.response) {
      return Promise.reject({
        message: AUTH_ERRORS.NETWORK_ERROR,
        code: 'NETWORK_ERROR',
      })
    }

    // Handle 401 - token expired
    if (error.response.status === 401 && !originalRequest._retry) {
      if (isRefreshing) {
        // Queue the request while refresh is in progress
        return new Promise((resolve, reject) => {
          failedQueue.push({ resolve, reject })
        })
          .then((token) => {
            originalRequest.headers.Authorization = `Bearer ${token}`
            return apiClient(originalRequest)
          })
          .catch((err) => Promise.reject(err))
      }

      originalRequest._retry = true
      isRefreshing = true

      const { refreshToken, logout } = useAuthStore.getState()

      if (!refreshToken) {
        // No refresh token, logout
        logout()
        window.location.href = ROUTES.LOGIN
        return Promise.reject({
          message: AUTH_ERRORS.TOKEN_EXPIRED,
          code: 'NO_REFRESH_TOKEN',
        })
      }

      try {
        // Attempt to refresh token
        const response = await axios.post(
          `${import.meta.env.VITE_API_BASE_URL || 'http://localhost:8081'}${API_ENDPOINTS.REFRESH_TOKEN}`,
          { refreshToken }
        )

        const { accessToken: newAccessToken, refreshToken: newRefreshToken } = response.data

        // Update tokens in store
        useAuthStore.getState().setTokens(newAccessToken, newRefreshToken)

        // Process queued requests
        processQueue(null, newAccessToken)

        // Retry original request
        originalRequest.headers.Authorization = `Bearer ${newAccessToken}`
        return apiClient(originalRequest)
      } catch (refreshError) {
        // Refresh failed, logout
        processQueue(refreshError, null)
        logout()
        window.location.href = ROUTES.LOGIN
        return Promise.reject({
          message: AUTH_ERRORS.TOKEN_EXPIRED,
          code: 'REFRESH_FAILED',
        })
      } finally {
        isRefreshing = false
      }
    }

    // Handle other errors
    const errorMessage = error.response?.data?.message || AUTH_ERRORS.SERVER_ERROR
    const errorCode = error.response?.data?.code || 'SERVER_ERROR'

    return Promise.reject({
      message: errorMessage,
      code: errorCode,
      status: error.response?.status,
      data: error.response?.data,
    })
  }
)

export default apiClient
