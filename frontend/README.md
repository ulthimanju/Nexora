# Nexora Auth Frontend

Production-grade React authentication UI for the Nexora platform.

## Features

- **Modern Stack**: React 19, Vite 6, Tailwind CSS v4, React Router v7
- **Authentication Methods**:
  - Username/Email + Password login
  - Email verification with OTP
  - OTP-based login
  - Magic link authentication
- **Design**: Claude-inspired aesthetic with Nexora branding
- **Form Handling**: React Hook Form + Zod validation
- **State Management**: Zustand for auth state
- **API Integration**: Axios with token refresh interceptors

## Getting Started

### Prerequisites

- Node.js 18+ and npm
- Nexora auth-service backend running on port 8081 (see `../auth-service`)

### Installation

```bash
# Install dependencies
npm install

# Copy environment variables
cp .env.example .env

# Update .env with your backend URL if different
# VITE_API_BASE_URL=http://localhost:8081
```

### Development

```bash
# Start development server
npm run dev

# Build for production
npm run build

# Preview production build
npm run preview

# Lint code
npm run lint
```

The app will be available at `http://localhost:3000`.

## Project Structure

```
src/
├── assets/                     # Static assets (logos, images)
├── components/
│   ├── atoms/                  # Basic UI elements (Button, Input, etc.)
│   ├── molecules/              # Composed components (FormField, OtpInput, etc.)
│   ├── organisms/              # Complex components (LoginForm, RegisterForm, etc.)
│   └── layout/                 # Layout components
├── pages/                      # Page components
├── routes/                     # Routing configuration
├── store/                      # Zustand state management
├── services/                   # API client and services
├── hooks/                      # Custom React hooks
├── lib/                        # Utilities and validators
├── constants/                  # App constants
└── styles/                     # Global styles
```

## Authentication Flow

### Registration Flow
1. User fills registration form
2. Backend creates account
3. Redirects to email verification page
4. User enters OTP from email
5. On success, redirects to login

### Login Flow (Password)
1. User enters credentials
2. Backend validates and returns tokens
3. Tokens stored in localStorage
4. User redirected to dashboard

### OTP Login Flow
1. User requests OTP via email
2. Backend sends OTP
3. User enters OTP
4. On success, receives tokens and redirects

### Magic Link Flow
1. User requests magic link
2. Backend sends email with link
3. User clicks link in email
4. Token verified automatically
5. On success, receives tokens and redirects

## Design System

The app follows a Claude-inspired design with warm, earthy tones:

- **Background**: `#f9f5f1` (warm off-white)
- **Accent**: `#c96442` (coral-orange)
- **Typography**: Inter font family
- **Components**: Tailwind CSS utilities only

All components are built from scratch without any UI library dependencies.

## API Integration

The app connects to the Nexora auth-service backend. Make sure to:

1. Set `VITE_API_BASE_URL` in `.env` to match your backend URL
2. Ensure the backend is running and accessible
3. The magic link verify route must match: `/auth/magic-link/verify`

## License

Part of the Nexora platform.
