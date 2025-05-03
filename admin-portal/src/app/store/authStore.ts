import { create } from 'zustand';
import { authService, UserProfile } from '@/app/services';

type AuthState = {
  user: UserProfile | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  error: string | null;
  login: (user: UserProfile) => void;
  logout: () => Promise<void>;
  clearError: () => void;
  checkAuth: () => Promise<void>;
};

export const useAuthStore = create<AuthState>((set, get) => ({
  user: null,
  isAuthenticated: false,
  isLoading: false,
  error: null,

  login: (user: UserProfile) => {
    set({ 
      user, 
      isAuthenticated: true, 
      isLoading: false,
      error: null
    });
  },

  logout: async () => {
    set({ isLoading: true });
    
    try {
      await authService.logout();
      set({ 
        user: null, 
        isAuthenticated: false, 
        isLoading: false,
        error: null 
      });
    } catch (err) {
      console.error('Logout error:', err);
      set({ 
        isLoading: false,
        error: err instanceof Error ? err.message : 'An error occurred during logout'
      });
    }
  },

  clearError: () => {
    set({ error: null });
  },

  checkAuth: async () => {
    // Skip if already authenticated
    if (get().isAuthenticated) return;
    
    // Skip if no token
    if (!authService.isAuthenticated()) return;
    
    set({ isLoading: true });
    
    try {
      const user = await authService.getCurrentUser();
      if (user) {
        set({ 
          user, 
          isAuthenticated: true, 
          isLoading: false,
          error: null 
        });
      } else {
        // Clear invalid token
        await authService.logout();
        set({ 
          user: null, 
          isAuthenticated: false, 
          isLoading: false,
          error: null 
        });
      }
    } catch (err) {
      console.error('Auth check error:', err);
      // Clear invalid token
      await authService.logout();
      set({ 
        user: null, 
        isAuthenticated: false, 
        isLoading: false,
        error: null
      });
    }
  }
}));
