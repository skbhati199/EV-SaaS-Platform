import axios, { AxiosError, AxiosInstance, AxiosRequestConfig, AxiosResponse } from 'axios';

// Define the API base URL
const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://192.168.29.133:8081/api';

// Create API service class
export class ApiService {
  protected api: AxiosInstance;
  
  constructor() {
    // Create axios instance
    this.api = axios.create({
      baseURL: API_BASE_URL,
      headers: {
        'Content-Type': 'application/json',
      },
    });

    // Add request interceptor to add auth token to requests
    this.api.interceptors.request.use(
      (config) => {
        // Get token from localStorage
        const token = localStorage.getItem('token');
        
        // Add token to headers if it exists
        if (token) {
          config.headers['Authorization'] = `Bearer ${token}`;
        }
        
        return config;
      },
      (error) => Promise.reject(error)
    );

    // Add response interceptor to handle auth errors
    this.api.interceptors.response.use(
      (response) => response,
      (error: AxiosError) => {
        // Handle auth errors (401, 403)
        if (error.response && (error.response.status === 401 || error.response.status === 403)) {
          // Clear auth data from localStorage
          localStorage.removeItem('token');
          localStorage.removeItem('isAuthenticated');
          
          // Redirect to login page if we're in a browser environment
          if (typeof window !== 'undefined') {
            window.location.href = '/login';
          }
        }
        
        return Promise.reject(error);
      }
    );
  }

  // Helper methods for API calls with proper typing
  protected async get<T>(url: string, config?: AxiosRequestConfig): Promise<T> {
    const response: AxiosResponse<T> = await this.api.get(url, config);
    return response.data;
  }

  protected async post<T>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T> {
    const response: AxiosResponse<T> = await this.api.post(url, data, config);
    return response.data;
  }

  protected async put<T>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T> {
    const response: AxiosResponse<T> = await this.api.put(url, data, config);
    return response.data;
  }

  protected async delete<T>(url: string, config?: AxiosRequestConfig): Promise<T> {
    const response: AxiosResponse<T> = await this.api.delete(url, config);
    return response.data;
  }

  protected async patch<T>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T> {
    const response: AxiosResponse<T> = await this.api.patch(url, data, config);
    return response.data;
  }
}