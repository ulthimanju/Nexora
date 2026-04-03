import React from 'react'
import ReactDOM from 'react-dom/client'
import { AppRouter } from './routes/AppRouter'
import { useAuthStore } from './store/authStore'
import './styles/index.css'

// Initialize auth store on app start
useAuthStore.getState().initialize()

ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    <AppRouter />
  </React.StrictMode>
)
