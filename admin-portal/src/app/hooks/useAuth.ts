'use client';

import { create } from 'zustand';
import { persist } from 'zustand/middleware';
import authService from '../services/authService';

interface User {
  id: string;
  username: string;
  email: string;
  firstName: string;
  lastName: string;
  role: string;
  twoFactorEnabled?: boolean;
  active: boolean;
  createdAt: string;
}

interface AuthState {
  user: User | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  tempTwoFactorToken: string | null;
  login: (email: string, password: string) => Promise<{ requiresTwoFactor: boolean }>;
  verify2FA: (code: string) => Promise<void>;
  logout: () => void;
  register: (userData: any) => Promise<void>;
  setup2FA: () => Promise<{ qrCodeImage: string; secret: string }>;
  enable2FA: (secret: string, code: string) => Promise<void>;
  disable2FA: () => Promise<void>;
}

const useAuthStore = create<AuthState>()(
  persist(
    (set, get) => ({
      user: null,
      isAuthenticated: false,
      isLoading: true,
      tempTwoFactorToken: null,
      
      login: async (email, password) => {
        try {
          const response = await authService.login(email, password);
          
          if (response.requiresTwoFactor) {
            set({ tempTwoFactorToken: response.tempToken });
            return { requiresTwoFactor: true };
          }
          
          set({ 
            user: response.user, 
            isAuthenticated: true,
            isLoading: false,
            tempTwoFactorToken: null
          });
          
          return { requiresTwoFactor: false };
        } catch (error) {
          throw error;
        }
      },
      
      verify2FA: async (code) => {
        try {
          const tempToken = get().tempTwoFactorToken;
          if (!tempToken) {
            throw new Error('No temporary token found');
          }
          
          const response = await authService.verify2FA(tempToken, code);
          
          set({ 
            user: response.user, 
            isAuthenticated: true,
            isLoading: false,
            tempTwoFactorToken: null
          });
        } catch (error) {
          throw error;
        }
      },
      
      logout: () => {
        authService.logout();
        set({ user: null, isAuthenticated: false, tempTwoFactorToken: null });
      },
      
      register: async (userData) => {
        try {
          await authService.register(userData);
        } catch (error) {
          throw error;
        }
      },
      
      setup2FA: async () => {
        try {
          return await authService.setup2FA();
        } catch (error) {
          throw error;
        }
      },
      
      enable2FA: async (secret, code) => {
        try {
          await authService.enable2FA(secret, code);
          
          // Update user object with 2FA enabled
          const user = get().user;
          if (user) {
            set({ user: { ...user, twoFactorEnabled: true } });
          }
        } catch (error) {
          throw error;
        }
      },
      
      disable2FA: async () => {
        try {
          await authService.disable2FA();
          
          // Update user object with 2FA disabled
          const user = get().user;
          if (user) {
            set({ user: { ...user, twoFactorEnabled: false } });
          }
        } catch (error) {
          throw error;
        }
      }
    }),
    {
      name: 'auth-storage',
      partialize: (state) => ({ 
        user: state.user,
        isAuthenticated: state.isAuthenticated
      })
    }
  )
);

// Hook for components to use
export const useAuth = () => {
  const auth = useAuthStore();
  
  // Initialize auth state on first load
  if (auth.isLoading) {
    authService.validateToken()
      .then(isValid => {
        if (!isValid) {
          auth.logout();
        }
        useAuthStore.setState({ isLoading: false });
      })
      .catch(() => {
        auth.logout();
        useAuthStore.setState({ isLoading: false });
      });
  }
  
  return auth;
};
