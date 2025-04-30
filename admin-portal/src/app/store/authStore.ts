import { create } from 'zustand';

type User = {
  id: string;
  email: string;
  firstName: string;
  lastName: string;
  role: string;
};

type AuthState = {
  user: User | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  error: string | null;
  login: (email: string, password: string) => Promise<void>;
  logout: () => void;
  clearError: () => void;
};

export const useAuthStore = create<AuthState>((set) => ({
  user: null,
  isAuthenticated: false,
  isLoading: false,
  error: null,

  login: async (email: string, password: string) => {
    set({ isLoading: true, error: null });
    try {
      // In a real application, this would be an API call to the auth service
      // const response = await fetch('/api/auth/login', {
      //   method: 'POST',
      //   headers: { 'Content-Type': 'application/json' },
      //   body: JSON.stringify({ email, password }),
      // });
      
      // if (!response.ok) {
      //   throw new Error('Invalid credentials');
      // }
      
      // const data = await response.json();
      
      // Simulate successful login for now
      await new Promise(resolve => setTimeout(resolve, 1000));
      
      // Mock user data
      const mockUser = {
        id: '123e4567-e89b-12d3-a456-426614174000',
        email,
        firstName: 'Admin',
        lastName: 'User',
        role: 'ADMIN',
      };
      
      set({ 
        user: mockUser, 
        isAuthenticated: true, 
        isLoading: false,
        error: null
      });
    } catch (err) {
      set({ 
        isLoading: false, 
        error: err instanceof Error ? err.message : 'An error occurred during login' 
      });
    }
  },

  logout: () => {
    // In a real application, this would also call an API endpoint to invalidate the session/token
    set({ user: null, isAuthenticated: false, error: null });
  },

  clearError: () => {
    set({ error: null });
  },
}));
