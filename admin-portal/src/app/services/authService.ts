import { ApiService, apiService } from './api';

// User roles in the system
export type RoleType = 'ADMIN' | 'OPERATOR' | 'BILLING_ADMIN' | 'SUPPORT' | 'USER';

// User model
export interface User {
  id: string;
  email: string;
  firstName: string;
  lastName: string;
  role: RoleType;
  createdAt: string;
  updatedAt: string;
}

// Authentication response from the server
export interface AuthResponse {
  token: string;
  user: User;
}

// Login request parameters
export interface LoginRequest {
  email: string;
  password: string;
}

// Register request parameters
export interface RegisterRequest {
  email: string;
  password: string;
  firstName: string;
  lastName: string;
}

// Password reset request
export interface PasswordResetRequest {
  email: string;
}

// Change password request
export interface ChangePasswordRequest {
  oldPassword: string;
  newPassword: string;
}

// Authentication service class extending the base API service
class AuthService extends ApiService {
  constructor() {
    super();
    // Use auth-service endpoint as base URL for auth requests
    this.api.defaults.baseURL = `${this.api.defaults.baseURL}/auth`;
  }

  // Login with email and password
  async login(credentials: LoginRequest): Promise<AuthResponse> {
    const response = await this.post<AuthResponse>('/login', credentials);
    
    // Store token and auth state
    localStorage.setItem('token', response.token);
    localStorage.setItem('isAuthenticated', 'true');
    
    return response;
  }

  // Register a new user
  async register(userData: RegisterRequest): Promise<AuthResponse> {
    const response = await this.post<AuthResponse>('/register', userData);
    
    // Store token and auth state
    localStorage.setItem('token', response.token);
    localStorage.setItem('isAuthenticated', 'true');
    
    return response;
  }

  // Logout user
  logout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('isAuthenticated');
  }

  // Get current user profile
  async getCurrentUser(): Promise<User> {
    return this.get<User>('/me');
  }

  // Request password reset
  async requestPasswordReset(data: PasswordResetRequest): Promise<void> {
    await this.post<void>('/password-reset', data);
  }

  // Change password
  async changePassword(data: ChangePasswordRequest): Promise<void> {
    await this.post<void>('/change-password', data);
  }

  // Helper method to check if user is authenticated
  isAuthenticated(): boolean {
    if (typeof window === 'undefined') {
      return false;
    }
    return localStorage.getItem('isAuthenticated') === 'true';
  }

  // Mock login for development (use only in development)
  async mockLogin(email: string, password: string): Promise<boolean> {
    // This is a simple mock for development
    if (email === 'skbhati199@gmail.com' && password === 'admin123') {
      localStorage.setItem('isAuthenticated', 'true');
      localStorage.setItem('token', 'mock-jwt-token');
      return true;
    }
    return false;
  }
}

// Export a singleton instance
export const authService = new AuthService(); 