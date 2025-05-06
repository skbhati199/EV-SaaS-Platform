import axios from 'axios';

const API_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080';

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

export const authService = {
  // Authentication
  async login(credentials: LoginRequest): Promise<TokenResponse> {
    try {
      const response = await axios.post(`${API_URL}/api/v1/auth/login`, credentials);
      return response.data;
    } catch (error) {
      throw error;
    }
  },

  async register(userData: RegisterRequest): Promise<any> {
    try {
      const response = await axios.post(`${API_URL}/api/v1/auth/register`, userData);
      return response.data;
    } catch (error) {
      throw error;
    }
  },

  async refreshToken(refreshToken: string): Promise<TokenResponse> {
    try {
      const response = await axios.post(`${API_URL}/api/v1/auth/refresh?refreshToken=${refreshToken}`);
      return response.data;
    } catch (error) {
      throw error;
    }
  },

  async validateToken(token: string): Promise<boolean> {
    try {
      const response = await axios.get(`${API_URL}/api/v1/auth/validate?token=${token}`);
      return response.data;
    } catch (error) {
      return false;
    }
  },

  // Two-Factor Authentication
  async setup2FA(token: string): Promise<TwoFactorSetupResponse> {
    try {
      const response = await axios.post(
        `${API_URL}/api/v1/auth/2fa/setup`,
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

  async enable2FA(token: string, secret: string, code: string): Promise<boolean> {
    try {
      const response = await axios.post(
        `${API_URL}/api/v1/auth/2fa/enable`,
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

  async verify2FA(username: string, code: string, secret: string): Promise<boolean> {
    try {
      const response = await axios.post(
        `${API_URL}/api/v1/auth/2fa/verify`,
        { username, code, secret }
      );
      return response.data;
    } catch (error) {
      throw error;
    }
  },

  async disable2FA(token: string): Promise<void> {
    try {
      await axios.post(
        `${API_URL}/api/v1/auth/2fa/disable`,
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
  }
};

export default authService;
