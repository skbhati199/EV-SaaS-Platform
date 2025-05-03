import { ApiService } from './api';
import { UserProfile } from './authService';

export interface UserCreateData {
  email: string;
  password: string;
  firstName: string;
  lastName: string;
  role: string;
}

export interface UserUpdateData {
  firstName?: string;
  lastName?: string;
  email?: string;
  role?: string;
}

export interface PaginatedResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
}

class UserService extends ApiService {
  constructor() {
    super();
    // Use user-service as base URL
    this.api.defaults.baseURL = `${this.api.defaults.baseURL}/api/users`;
  }

  // Get all users with pagination
  async getUsers(page = 0, size = 10): Promise<PaginatedResponse<UserProfile>> {
    return this.get<PaginatedResponse<UserProfile>>('', { params: { page, size } });
  }

  // Get user by ID
  async getUser(id: string): Promise<UserProfile> {
    return this.get<UserProfile>(`/${id}`);
  }

  // Create new user (admin function)
  async createUser(userData: UserCreateData): Promise<UserProfile> {
    return this.post<UserProfile>('', userData);
  }

  // Update user
  async updateUser(id: string, userData: UserUpdateData): Promise<UserProfile> {
    return this.put<UserProfile>(`/${id}`, userData);
  }

  // Delete user
  async deleteUser(id: string): Promise<void> {
    return this.delete(`/${id}`);
  }

  // Change user password
  async changePassword(id: string, currentPassword: string, newPassword: string): Promise<void> {
    return this.post(`/${id}/change-password`, {
      currentPassword,
      newPassword
    });
  }

  // Reset password (admin function)
  async resetPassword(id: string): Promise<{ temporaryPassword: string }> {
    return this.post(`/${id}/reset-password`, {});
  }
}

export default new UserService(); 