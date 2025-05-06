import axios from 'axios';

// Types based on the Postman collection
type RegisterUserRequest = {
  username: string;
  email: string;
  password: string;
  firstName: string;
  lastName: string;
  role: string;
};

type LoginRequest = {
  email: string;
  password: string;
};

type AuthResponse = {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresIn: number;
};

type User = {
  id: string;
  username: string;
  email: string;
  firstName: string;
  lastName: string;
  role: string;
  active: boolean;
  createdAt: string;
};

type TwoFactorSetupResponse = {
  secret: string;
  qrCodeImage: string;
};

type TwoFactorEnableRequest = {
  secret: string;
  code: string;
};

class AuthService {
  private baseUrl: string;
  private accessToken: string | null = null;
  private refreshToken: string | null = null;

  constructor(baseUrl: string = 'http://localhost:8080') {
    this.baseUrl = baseUrl;
    
    // Initialize tokens from localStorage if available
    this.accessToken = localStorage.getItem('access_token');
    this.refreshToken = localStorage.getItem('refresh_token');
    
    // Set up axios interceptor for automatic token refresh
    this.setupInterceptors();
  }

  private setupInterceptors() {
    axios.interceptors.response.use(
      (response) => response,
      async (error) => {
        const originalRequest = error.config;
        
        // If the error is 401 and we have a refresh token and this is not a retry
        if (
          error.response?.status === 401 &&
          this.refreshToken &&
          !originalRequest._retry
        ) {
          originalRequest._retry = true;
          
          try {
            // Try to refresh the token
            const newTokens = await this.refreshAccessToken();
            
            // Update the authorization header
            originalRequest.headers['Authorization'] = `Bearer ${newTokens.accessToken}`;
            
            // Retry the original request
            return axios(originalRequest);
          } catch (refreshError) {
            // If refresh fails, log out the user
            this.logout();
            return Promise.reject(refreshError);
          }
        }
        
        return Promise.reject(error);
      }
    );
  }

  // Register a new user
  async register(userData: RegisterUserRequest): Promise<User> {
    try {
      const response = await axios.post<User>(
        `${this.baseUrl}/api/v1/auth/register`,
        userData,
        {
          headers: {
            'Content-Type': 'application/json',
          },
        }
      );
      return response.data;
    } catch (error) {
      if (axios.isAxiosError(error) && error.response?.status === 409) {
        throw new Error('User with this email already exists');
      }
      throw new Error('Registration failed');
    }
  }

  // Login with email and password
  async login(credentials: LoginRequest): Promise<AuthResponse> {
    try {
      const response = await axios.post<AuthResponse>(
        `${this.baseUrl}/api/v1/auth/login`,
        credentials,
        {
          headers: {
            'Content-Type': 'application/json',
          },
        }
      );
      
      // Store tokens
      this.accessToken = response.data.accessToken;
      this.refreshToken = response.data.refreshToken;
      
      // Save tokens to localStorage
      localStorage.setItem('access_token', response.data.accessToken);
      localStorage.setItem('refresh_token', response.data.refreshToken);
      
      return response.data;
    } catch (error) {
      if (axios.isAxiosError(error) && error.response?.status === 401) {
        throw new Error('Invalid credentials');
      }
      throw new Error('Login failed');
    }
  }

  // Refresh the access token using a refresh token
  async refreshAccessToken(): Promise<AuthResponse> {
    if (!this.refreshToken) {
      throw new Error('No refresh token available');
    }
    
    try {
      const response = await axios.post<AuthResponse>(
        `${this.baseUrl}/api/v1/auth/refresh`,
        null,
        {
          params: {
            refreshToken: this.refreshToken,
          },
        }
      );
      
      // Update tokens
      this.accessToken = response.data.accessToken;
      this.refreshToken = response.data.refreshToken;
      
      // Update localStorage
      localStorage.setItem('access_token', response.data.accessToken);
      localStorage.setItem('refresh_token', response.data.refreshToken);
      
      return response.data;
    } catch (error) {
      // Clear tokens on refresh failure
      this.logout();
      throw new Error('Token refresh failed');
    }
  }

  // Validate if a token is valid
  async validateToken(token: string = this.accessToken || ''): Promise<boolean> {
    try {
      const response = await axios.get<boolean>(
        `${this.baseUrl}/api/v1/auth/validate`,
        {
          params: {
            token,
          },
        }
      );
      return response.data;
    } catch (error) {
      return false;
    }
  }

  // Logout - clear tokens
  logout(): void {
    this.accessToken = null;
    this.refreshToken = null;
    localStorage.removeItem('access_token');
    localStorage.removeItem('refresh_token');
  }

  // Get the current access token
  getAccessToken(): string | null {
    return this.accessToken;
  }

  // Check if user is authenticated
  isAuthenticated(): boolean {
    return !!this.accessToken;
  }

  // Get authorization header for API requests
  getAuthHeader(): Record<string, string> {
    return this.accessToken
      ? { Authorization: `Bearer ${this.accessToken}` }
      : {};
  }

  // Setup 2FA for a user
  async setup2FA(): Promise<TwoFactorSetupResponse> {
    if (!this.accessToken) {
      throw new Error('Authentication required');
    }
    
    try {
      const response = await axios.post<TwoFactorSetupResponse>(
        `${this.baseUrl}/api/v1/auth/2fa/setup`,
        null,
        {
          headers: {
            ...this.getAuthHeader(),
          },
        }
      );
      return response.data;
    } catch (error) {
      throw new Error('Failed to set up 2FA');
    }
  }

  // Enable 2FA after verifying the code
  async enable2FA(data: TwoFactorEnableRequest): Promise<boolean> {
    if (!this.accessToken) {
      throw new Error('Authentication required');
    }
    
    try {
      const response = await axios.post<boolean>(
        `${this.baseUrl}/api/v1/auth/2fa/enable`,
        data,
        {
          headers: {
            'Content-Type': 'application/json',
            ...this.getAuthHeader(),
          },
        }
      );
      return response.data;
    } catch (error) {
      throw new Error('Failed to enable 2FA');
    }
  }

  // Verify 2FA code during login
  async verify2FA(code: string, tempToken: string): Promise<AuthResponse> {
    try {
      const response = await axios.post<AuthResponse>(
        `${this.baseUrl}/api/v1/auth/2fa/verify`,
        { code },
        {
          headers: {
            'Content-Type': 'application/json',
            Authorization: `Bearer ${tempToken}`,
          },
        }
      );
      
      // Store tokens
      this.accessToken = response.data.accessToken;
      this.refreshToken = response.data.refreshToken;
      
      // Save tokens to localStorage
      localStorage.setItem('access_token', response.data.accessToken);
      localStorage.setItem('refresh_token', response.data.refreshToken);
      
      return response.data;
    } catch (error) {
      throw new Error('2FA verification failed');
    }
  }

  // Disable 2FA for a user
  async disable2FA(): Promise<boolean> {
    if (!this.accessToken) {
      throw new Error('Authentication required');
    }
    
    try {
      const response = await axios.post<boolean>(
        `${this.baseUrl}/api/v1/auth/2fa/disable`,
        null,
        {
          headers: {
            ...this.getAuthHeader(),
          },
        }
      );
      return response.data;
    } catch (error) {
      throw new Error('Failed to disable 2FA');
    }
  }
}

// Create and export a singleton instance
export const authService = new AuthService();

export default authService;
