/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{js,jsx}'],
  theme: {
    extend: {
      colors: {
        background: '#f9f5f1',
        surface: '#ffffff',
        border: '#e8e0d8',
        primary: '#1a1612',
        secondary: '#6b5f56',
        placeholder: '#a89a8f',
        accent: '#c96442',
        'accent-hover': '#b05535',
        'accent-light': '#fdf0eb',
        success: '#2d7a4f',
        error: '#c0392b',
        warning: '#b7770d',
      },
      fontFamily: {
        sans: ['Inter', 'sans-serif'],
      },
      keyframes: {
        fadeIn: {
          from: { opacity: '0', transform: 'translateY(-8px)' },
          to: { opacity: '1', transform: 'translateY(0)' },
        },
        shake: {
          '0%, 100%': { transform: 'translateX(0)' },
          '20%, 60%': { transform: 'translateX(-6px)' },
          '40%, 80%': { transform: 'translateX(6px)' },
        },
      },
      animation: {
        fadeIn: 'fadeIn 0.2s ease-out',
        shake: 'shake 0.4s ease-in-out',
      },
    },
  },
  plugins: [],
}
