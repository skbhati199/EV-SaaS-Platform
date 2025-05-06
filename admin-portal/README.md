# EV SaaS Platform Admin Portal

## Authentication Integration

This project integrates the EV SaaS Auth Service API into the admin portal, providing a complete authentication and authorization system.

### Features Implemented

- **User Authentication**
  - Login with email and password
  - User registration
  - Token-based authentication with JWT
  - Automatic token refresh
  - Logout functionality

- **Two-Factor Authentication (2FA)**
  - 2FA setup with QR code generation
  - 2FA verification during login
  - Enable/disable 2FA functionality

- **Authorization**
  - Role-based access control
  - Protected routes based on authentication status
  - Role-specific route protection

### Project Structure

```
src/app/
├── components/auth/
│   ├── LoginForm.tsx         # Login form component
│   ├── RegisterForm.tsx      # Registration form component
│   ├── TwoFactorSetup.tsx    # 2FA setup and verification component
│   └── ProtectedRoute.tsx    # Route protection component
├── hooks/
│   └── useAuth.ts            # Custom hook for auth functionality
├── services/
│   └── authService.ts        # Service for API communication
├── store/
│   └── authStore.ts          # Zustand store for auth state management
├── login/
│   └── page.tsx              # Login/Register page
└── profile/
    └── page.tsx              # User profile page with 2FA setup
```

### Authentication Flow

1. **Registration**:
   - User submits registration form
   - API creates a new user account
   - User is redirected to login

2. **Login**:
   - User submits login credentials
   - API validates credentials and returns tokens
   - Tokens are stored in localStorage
   - User is authenticated in the application

3. **Token Refresh**:
   - Automatic refresh of access tokens using refresh tokens
   - Axios interceptor handles 401 errors and refreshes tokens

4. **Two-Factor Authentication**:
   - User sets up 2FA in profile page
   - QR code is scanned with authenticator app
   - User verifies setup with a code from the app
   - Subsequent logins require 2FA code verification

### API Integration

The admin portal integrates with the following Auth Service API endpoints:

- `POST /api/v1/auth/register` - Register a new user
- `POST /api/v1/auth/login` - Login with credentials
- `POST /api/v1/auth/refresh` - Refresh access token
- `GET /api/v1/auth/validate` - Validate token
- `POST /api/v1/auth/2fa/setup` - Set up 2FA
- `POST /api/v1/auth/2fa/enable` - Enable 2FA
- `POST /api/v1/auth/2fa/verify` - Verify 2FA code
- `POST /api/v1/auth/2fa/disable` - Disable 2FA

### Usage

#### Protected Routes

Wrap any component or page that requires authentication with the `ProtectedRoute` component:

```tsx
<ProtectedRoute>
  <YourSecurePage />
</ProtectedRoute>
```

For role-specific protection:

```tsx
<ProtectedRoute requiredRole="ADMIN">
  <AdminOnlyPage />
</ProtectedRoute>
```

#### Authentication Hook

Use the `useAuth` hook in your components to access authentication functionality:

```tsx
const { 
  user, 
  isAuthenticated, 
  login, 
  logout, 
  register,
  setup2FA,
  enable2FA 
} = useAuth();
```

### Configuration

The authentication service is configured to use `http://localhost:8080` as the default API base URL. To change this, modify the `baseUrl` parameter in the `authService.ts` file.

```typescript
// In src/app/services/authService.ts
const authService = new AuthService('https://your-api-url.com');
```

### Security Considerations

- Access tokens are short-lived (1 hour by default)
- Refresh tokens are used for obtaining new access tokens
- Two-factor authentication adds an extra layer of security
- Protected routes prevent unauthorized access to sensitive pages
- Token validation occurs on application startup
- Automatic logout on token expiration
