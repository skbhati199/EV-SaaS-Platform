import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api/v1/auth';

class AuthService {
  private accessToken: string | null = null;
  private refreshToken: string | null = null;

  constructor(private baseUrl: string = API_BASE_URL) {
    // Initialize tokens from localStorage if available
    if (typeof window !== 'undefined') {
      this.accessToken = localStorage.getItem('accessToken');
      this.refreshToken = localStorage.getItem('refreshToken');
    }

    // Set up axios interceptor for token refresh
    axios.interceptors.response.use(
      (response) => response,
      async (error) => {
        const originalRequest = error.config;
        
        // If error is 401 and not already retrying
        if (error.response?.status === 401 && !originalRequest._retry && this.refreshToken) {
          originalRequest._retry = true;
          
          try {
            // Try to refresh the token
            const response = await axios.post(`${this.baseUrl}/refresh`, null, {
              params: { refreshToken: this.refreshToken }
            });
            
            const { accessToken, refreshToken } = response.data;
            this.setTokens(accessToken, refreshToken);
            
            // Retry the original request with new token
            originalRequest.headers['Authorization'] = `Bearer ${accessToken}`;
            return axios(originalRequest);
          } catch (refreshError) {
            // If refresh fails, logout
            this.logout();
            return Promise.reject(refreshError);
          }
        }
        
        return Promise.reject(error);
      }
    );
  }

  private setTokens(accessToken: string, refreshToken: string) {
    this.accessToken = accessToken;
    this.refreshToken = refreshToken;
    
    if (typeof window !== 'undefined') {
      localStorage.setItem('accessToken', accessToken);
      localStorage.setItem('refreshToken', refreshToken);
    }
  }

  private clearTokens() {
    this.accessToken = null;
    this.refreshToken = null;
    
    if (typeof window !== 'undefined') {
      localStorage.removeItem('accessToken');
      localStorage.removeItem('refreshToken');
    }
  }

  private getAuthHeader() {
    return this.accessToken ? { Authorization: `Bearer ${this.accessToken}` } : {};
  }

  async register(userData: any) {
    try {
      const response = await axios.post(`${this.baseUrl}/register`, userData);
      return response.data;
    } catch (error: any) {
      throw new Error(error.response?.data?.message || 'Registration failed');
    }
  }

  async login(email: string, password: string) {
    try {
      const response = await axios.post(`${this.baseUrl}/login`, { email, password });
      
      // Check if 2FA is required
      if (response.data.requiresTwoFactor) {
        return {
          requiresTwoFactor: true,
          tempToken: response.data.tempToken
        };
      }
      
      const { accessToken, refreshToken, user } = response.data;
      this.setTokens(accessToken, refreshToken);
      
      return {
        requiresTwoFactor: false,
        user
      };
    } catch (error: any) {
      throw new Error(error.response?.data?.message || 'Login failed');
    }
  }

  async verify2FA(tempToken: string, code: string) {
    try {
      const response = await axios.post(`${this.baseUrl}/2fa/verify`, { code }, {
        headers: { Authorization: `Bearer ${tempToken}` }
      });
      
      const { accessToken, refreshToken, user } = response.data;
      this.setTokens(accessToken, refreshToken);
      
      return { user };
    } catch (error: any) {
      throw new Error(error.response?.data?.message || 'Verification failed');
    }
  }

  async validateToken() {
    if (!this.accessToken) {
      return false;
    }
    
    try {
      const response = await axios.get(`${this.baseUrl}/validate`, {
        params: { token: this.accessToken }
      });
      return response.data === true;
    } catch (error) {
      return false;
    }
  }

  async setup2FA() {
    try {
      const response = await axios.post(`${this.baseUrl}/2fa/setup`, null, {
        headers: this.getAuthHeader()
      });
      return response.data;
    } catch (error: any) {
      throw new Error(error.response?.data?.message || 'Failed to set up 2FA');
    }
  }

  async enable2FA(secret: string, code: string) {
    try {
      const response = await axios.post(`${this.baseUrl}/2fa/enable`, { secret, code }, {
        headers: this.getAuthHeader()
      });
      return response.data;
    } catch (error: any) {
      throw new Error(error.response?.data?.message || 'Failed to enable 2FA');
    }
  }

  async disable2FA() {
    try {
      const response = await axios.post(`${this.baseUrl}/2fa/disable`, null, {
        headers: this.getAuthHeader()
      });
      return response.data;
    } catch (error: any) {
      throw new Error(error.response?.data?.message || 'Failed to disable 2FA');
    }
  }

  logout() {
    this.clearTokens();
  }
}

const authService = new AuthService();
export default authService;
