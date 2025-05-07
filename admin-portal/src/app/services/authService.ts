import axios from 'axios';

// Use API URL with proper versioning to match Nginx config
const API_URL = process.env.NEXT_PUBLIC_API_URL || '/api';
const API_V1_URL = `${API_URL}/v1`;

interface LoginRequest {
  email: string;
  password: string;
}

interface RegisterRequest {
  username: string;
  email: string;
  password: string;
  firstName: string;
  lastName: string;
  role: string;
}

interface TokenResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresIn: number;
}

interface TwoFactorSetupResponse {
  secret: string;
  qrCodeImage: string;
}

interface LoginResponse {
  user: {
    id: string;
    username: string;
    email: string;
    firstName: string;
    lastName: string;
    role: string;
    twoFactorEnabled: boolean;
    active: boolean;
    createdAt: string;
  };
  accessToken: string;
  refreshToken: string;
  requiresTwoFactor: boolean;
  tempToken?: string;
}

export const authService = {
  // Authentication
  async login(email: string, password: string): Promise<LoginResponse> {
    try {
      const response = await axios.post(`${API_V1_URL}/auth/login`, { email, password });
      
      // Convert backend response to match frontend expectations
      const tokenResponse = response.data;
      
      // If it's just a token response with no user info, make a follow-up call
      if (!tokenResponse.user && tokenResponse.accessToken) {
        // Extract user info from token or make a call to get user profile
        const userResponse = await this.getUserProfile(tokenResponse.accessToken);
        
        return {
          user: userResponse,
          accessToken: tokenResponse.accessToken,
          refreshToken: tokenResponse.refreshToken,
          requiresTwoFactor: false // Set based on user.twoFactorEnabled if available
        };
      }
      
      return response.data;
    } catch (error) {
      console.error('Login error:', error);
      throw error;
    }
  },

  async getUserProfile(token: string): Promise<any> {
    try {
      const response = await axios.get(`${API_V1_URL}/users/me`, {
        headers: {
          Authorization: `Bearer ${token}`
        }
      });
      return response.data;
    } catch (error) {
      console.error('Failed to get user profile:', error);
      throw error;
    }
  },

  async register(userData: RegisterRequest): Promise<any> {
    try {
      const response = await axios.post(`${API_V1_URL}/auth/register`, userData, {
        withCredentials: false,
        headers: {
          'Content-Type': 'application/json',
          'Accept': 'application/json'
        }
      });
      return response.data;
    } catch (error) {
      console.error('Registration error:', error);
      throw error;
    }
  },

  async refreshToken(refreshToken: string): Promise<TokenResponse> {
    try {
      const response = await axios.post(`${API_V1_URL}/auth/refresh?refreshToken=${refreshToken}`);
      return response.data;
    } catch (error) {
      throw error;
    }
  },

  async validateToken(token?: string): Promise<boolean> {
    try {
      // If no token is provided, check localStorage
      const savedToken = token || localStorage.getItem('accessToken');
      if (!savedToken) return false;
      
      const response = await axios.get(`${API_V1_URL}/auth/validate?token=${savedToken}`);
      return response.data;
    } catch (error) {
      return false;
    }
  },

  async forgotPassword(email: string): Promise<boolean> {
    try {
      const response = await axios.post(`${API_V1_URL}/auth/forgot-password`, { email });
      return response.data;
    } catch (error) {
      console.error('Forgot password error:', error);
      throw error;
    }
  },

  async resetPassword(token: string, newPassword: string): Promise<boolean> {
    try {
      const response = await axios.post(`${API_V1_URL}/auth/reset-password`, { 
        token, 
        newPassword 
      });
      return response.data;
    } catch (error) {
      console.error('Reset password error:', error);
      throw error;
    }
  },

  // Two-Factor Authentication
  async setup2FA(): Promise<TwoFactorSetupResponse> {
    try {
      const token = localStorage.getItem('accessToken');
      const response = await axios.post(
        `${API_V1_URL}/auth/2fa/setup`,
        {},
        {
          headers: {
            Authorization: `Bearer ${token}`
          }
        }
      );
      return response.data;
    } catch (error) {
      throw error;
    }
  },

  async enable2FA(secret: string, code: string): Promise<boolean> {
    try {
      const token = localStorage.getItem('accessToken');
      const response = await axios.post(
        `${API_V1_URL}/auth/2fa/enable`,
        { secret, code },
        {
          headers: {
            Authorization: `Bearer ${token}`
          }
        }
      );
      return response.data;
    } catch (error) {
      throw error;
    }
  },

  async verify2FA(tempToken: string, code: string): Promise<any> {
    try {
      const response = await axios.post(
        `${API_V1_URL}/auth/2fa/verify`,
        { code, tempToken }
      );
      
      if (response.data.accessToken) {
        localStorage.setItem('accessToken', response.data.accessToken);
        if (response.data.refreshToken) {
          localStorage.setItem('refreshToken', response.data.refreshToken);
        }
      }
      
      return response.data;
    } catch (error) {
      throw error;
    }
  },

  async disable2FA(): Promise<void> {
    try {
      const token = localStorage.getItem('accessToken');
      await axios.post(
        `${API_V1_URL}/auth/2fa/disable`,
        {},
        {
          headers: {
            Authorization: `Bearer ${token}`
          }
        }
      );
    } catch (error) {
      throw error;
    }
  },
  
  // Utility function to save tokens
  saveTokens(accessToken: string, refreshToken: string): void {
    localStorage.setItem('accessToken', accessToken);
    localStorage.setItem('refreshToken', refreshToken);
  },
  
  // Utility function to get stored tokens
  getAccessToken(): string | null {
    return localStorage.getItem('accessToken');
  },
  
  // Logout
  logout(): void {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('auth-storage');
  }
};

export default authService;
