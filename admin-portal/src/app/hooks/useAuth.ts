import { useEffect, useState } from 'react';
import { useAuthStore } from '../store/authStore';
import authService from '../services/authService';

type LoginCredentials = {
  email: string;
  password: string;
};

type RegisterData = {
  username: string;
  email: string;
  password: string;
  firstName: string;
  lastName: string;
  role: string;
};

type TwoFactorSetup = {
  secret: string;
  qrCodeImage: string;
};

export const useAuth = () => {
  const { user, isAuthenticated, isLoading, error, login: storeLogin, logout: storeLogout, clearError } = useAuthStore();
  const [twoFactorSetup, setTwoFactorSetup] = useState<TwoFactorSetup | null>(null);
  const [tempToken, setTempToken] = useState<string | null>(null);
  const [requires2FA, setRequires2FA] = useState(false);

  // Check authentication status on mount
  useEffect(() => {
    const checkAuth = async () => {
      if (authService.getAccessToken()) {
        const isValid = await authService.validateToken();
        if (!isValid) {
          storeLogout();
        }
      }
    };
    
    checkAuth();
  }, [storeLogout]);

  // Register a new user
  const register = async (userData: RegisterData) => {
    try {
      const result = await authService.register(userData);
      return result;
    } catch (error) {
      if (error instanceof Error) {
        throw new Error(error.message);
      }
      throw new Error('Registration failed');
    }
  };

  // Login with email and password
  const login = async (credentials: LoginCredentials) => {
    try {
      const response = await authService.login(credentials);
      
      // If login is successful, update the store
      // In a real app, you would fetch user profile here
      const mockUser = {
        id: '123e4567-e89b-12d3-a456-426614174000',
        email: credentials.email,
        firstName: 'Admin',
        lastName: 'User',
        role: 'ADMIN',
      };
      
      storeLogin(credentials.email, credentials.password);
      return response;
    } catch (error) {
      if (error instanceof Error) {
        throw new Error(error.message);
      }
      throw new Error('Login failed');
    }
  };

  // Logout the user
  const logout = () => {
    authService.logout();
    storeLogout();
  };

  // Setup 2FA
  const setup2FA = async () => {
    try {
      const setup = await authService.setup2FA();
      setTwoFactorSetup(setup);
      return setup;
    } catch (error) {
      if (error instanceof Error) {
        throw new Error(error.message);
      }
      throw new Error('Failed to set up 2FA');
    }
  };

  // Enable 2FA
  const enable2FA = async (secret: string, code: string) => {
    try {
      const result = await authService.enable2FA({ secret, code });
      return result;
    } catch (error) {
      if (error instanceof Error) {
        throw new Error(error.message);
      }
      throw new Error('Failed to enable 2FA');
    }
  };

  // Verify 2FA code during login
  const verify2FA = async (code: string) => {
    if (!tempToken) {
      throw new Error('No temporary token available');
    }
    
    try {
      const response = await authService.verify2FA(code, tempToken);
      setRequires2FA(false);
      setTempToken(null);
      
      // If verification is successful, update the store
      // In a real app, you would fetch user profile here
      const mockUser = {
        id: '123e4567-e89b-12d3-a456-426614174000',
        email: 'admin@example.com',
        firstName: 'Admin',
        lastName: 'User',
        role: 'ADMIN',
      };
      
      storeLogin('admin@example.com', 'password');
      return response;
    } catch (error) {
      if (error instanceof Error) {
        throw new Error(error.message);
      }
      throw new Error('2FA verification failed');
    }
  };

  // Disable 2FA
  const disable2FA = async () => {
    try {
      const result = await authService.disable2FA();
      return result;
    } catch (error) {
      if (error instanceof Error) {
        throw new Error(error.message);
      }
      throw new Error('Failed to disable 2FA');
    }
  };

  return {
    user,
    isAuthenticated,
    isLoading,
    error,
    login,
    logout,
    register,
    clearError,
    setup2FA,
    enable2FA,
    verify2FA,
    disable2FA,
    twoFactorSetup,
    requires2FA,
  };
};