import { create } from 'zustand'

const AUTH_STORAGE_KEY = 'nexora_auth'

// Helper to load from localStorage
const loadFromStorage = () => {
  try {
    const stored = localStorage.getItem(AUTH_STORAGE_KEY)
    if (stored) {
      const { accessToken, refreshToken, user } = JSON.parse(stored)
      return {
        accessToken: accessToken || null,
        refreshToken: refreshToken || null,
        user: user || null,
        isAuthenticated: !!accessToken,
      }
    }
  } catch (error) {
    console.error('Failed to load auth from storage:', error)
  }
  return {
    accessToken: null,
    refreshToken: null,
    user: null,
    isAuthenticated: false,
  }
}

// Helper to save to localStorage
const saveToStorage = (data) => {
  try {
    localStorage.setItem(AUTH_STORAGE_KEY, JSON.stringify(data))
  } catch (error) {
    console.error('Failed to save auth to storage:', error)
  }
}

// Helper to clear from localStorage
const clearStorage = () => {
  try {
    localStorage.removeItem(AUTH_STORAGE_KEY)
  } catch (error) {
    console.error('Failed to clear auth from storage:', error)
  }
}

export const useAuthStore = create((set, get) => ({
  ...loadFromStorage(),
  isLoading: false,

  // Set tokens
  setTokens: (accessToken, refreshToken) => {
    const state = get()
    const newState = {
      accessToken,
      refreshToken,
      user: state.user,
      isAuthenticated: !!accessToken,
    }
    saveToStorage(newState)
    set(newState)
  },

  // Set user
  setUser: (user) => {
    const state = get()
    const newState = {
      accessToken: state.accessToken,
      refreshToken: state.refreshToken,
      user,
      isAuthenticated: state.isAuthenticated,
    }
    saveToStorage(newState)
    set({ user })
  },

  // Set loading state
  setLoading: (isLoading) => set({ isLoading }),

  // Logout
  logout: () => {
    clearStorage()
    set({
      user: null,
      accessToken: null,
      refreshToken: null,
      isAuthenticated: false,
      isLoading: false,
    })
  },

  // Initialize (restore session on app start)
  initialize: () => {
    const stored = loadFromStorage()
    set(stored)
  },
}))
