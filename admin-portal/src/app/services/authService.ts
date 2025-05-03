import { ApiService } from './api';

export interface LoginCredentials {
  email: string;
  password: string;
}

export interface RegisterData {
  email: string;
  password: string;
  firstName: string;
  lastName: string;
  role?: string;
}

export interface TokenResponse {
  accessToken: string;
  tokenType: string;
  expiresIn: number;
  refreshToken?: string;
}

export interface UserProfile {
  id: string;
  email: string;
  firstName: string;
  lastName: string;
  role: string;
  createdAt: string;
  updatedAt: string;
}

class AuthService extends ApiService {
  constructor() {
    super();
    // Use auth-service as base URL
    this.api.defaults.baseURL = `${this.api.defaults.baseURL}/api/auth`;
  }

  // Login user and get JWT token
  async login(credentials: LoginCredentials): Promise<TokenResponse> {
    const response = await this.post<TokenResponse>('/login', credentials);
    
    // Store token in localStorage
    if (response.accessToken) {
      localStorage.setItem('token', response.accessToken);
      if (response.refreshToken) {
        localStorage.setItem('refreshToken', response.refreshToken);
      }
    }
    
    return response;
  }

  // Register new user
  async register(userData: RegisterData): Promise<TokenResponse> {
    const response = await this.post<TokenResponse>('/register', userData);
    
    // Store token in localStorage if registration automatically logs in
    if (response.accessToken) {
      localStorage.setItem('token', response.accessToken);
      if (response.refreshToken) {
        localStorage.setItem('refreshToken', response.refreshToken);
      }
    }
    
    return response;
  }

  // Get current user profile
  async getCurrentUser(): Promise<UserProfile> {
    return this.get<UserProfile>('/me');
  }

  // Refresh JWT token
  async refreshToken(): Promise<TokenResponse> {
    const refreshToken = localStorage.getItem('refreshToken');
    
    if (!refreshToken) {
      throw new Error('No refresh token available');
    }
    
    const response = await this.post<TokenResponse>('/refresh', { refreshToken });
    
    if (response.accessToken) {
      localStorage.setItem('token', response.accessToken);
      if (response.refreshToken) {
        localStorage.setItem('refreshToken', response.refreshToken);
      }
    }
    
    return response;
  }

  // Logout user
  async logout(): Promise<void> {
    try {
      await this.post('/logout');
    } finally {
      // Clear stored tokens
      localStorage.removeItem('token');
      localStorage.removeItem('refreshToken');
    }
  }

  // Check if user is authenticated
  isAuthenticated(): boolean {
    return !!localStorage.getItem('token');
  }
}

export default new AuthService(); 