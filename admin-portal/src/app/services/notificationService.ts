import { ApiService } from './api';
import { PaginatedResponse } from './userService';

export type NotificationType = 
  | 'SYSTEM' 
  | 'STATION_STATUS' 
  | 'CHARGING_SESSION' 
  | 'BILLING' 
  | 'AUTHENTICATION' 
  | 'MAINTENANCE';

export type NotificationSeverity = 'INFO' | 'WARNING' | 'ERROR' | 'CRITICAL';

export type NotificationChannel = 'EMAIL' | 'SMS' | 'PUSH' | 'IN_APP';

export interface Notification {
  id: string;
  userId?: string;
  title: string;
  message: string;
  type: NotificationType;
  severity: NotificationSeverity;
  read: boolean;
  metadata?: Record<string, any>;
  createdAt: string;
  readAt?: string;
}

export interface NotificationTemplate {
  id: string;
  name: string;
  type: NotificationType;
  subject: string;
  bodyTemplate: string;
  active: boolean;
  channels: NotificationChannel[];
  metadata?: Record<string, any>;
  createdAt: string;
  updatedAt: string;
}

export interface NotificationTemplateCreateData {
  name: string;
  type: NotificationType;
  subject: string;
  bodyTemplate: string;
  active?: boolean;
  channels: NotificationChannel[];
  metadata?: Record<string, any>;
}

export interface NotificationSendRequest {
  userId?: string;
  userIds?: string[];
  roleIds?: string[];
  templateId: string;
  templateData?: Record<string, any>;
  channels?: NotificationChannel[];
}

export interface NotificationPreference {
  id: string;
  userId: string;
  type: NotificationType;
  emailEnabled: boolean;
  smsEnabled: boolean;
  pushEnabled: boolean;
  inAppEnabled: boolean;
  createdAt: string;
  updatedAt: string;
}

class NotificationService extends ApiService {
  constructor() {
    super();
    // Use notification-service as base URL
    this.api.defaults.baseURL = `${this.api.defaults.baseURL}/api/notifications`;
  }

  // User notifications
  async getUserNotifications(
    page = 0,
    size = 10,
    read?: boolean,
    type?: NotificationType
  ): Promise<PaginatedResponse<Notification>> {
    return this.get<PaginatedResponse<Notification>>('/user', {
      params: {
        page,
        size,
        read,
        type
      }
    });
  }

  async getNotification(id: string): Promise<Notification> {
    return this.get<Notification>(`/${id}`);
  }

  async markNotificationAsRead(id: string): Promise<Notification> {
    return this.post<Notification>(`/${id}/read`);
  }

  async markAllNotificationsAsRead(): Promise<void> {
    return this.post('/read-all');
  }

  async deleteNotification(id: string): Promise<void> {
    return this.delete(`/${id}`);
  }

  // Count unread notifications
  async getUnreadCount(): Promise<{ count: number }> {
    return this.get<{ count: number }>('/unread-count');
  }

  // Notification templates
  async getNotificationTemplates(
    page = 0,
    size = 10,
    type?: NotificationType
  ): Promise<PaginatedResponse<NotificationTemplate>> {
    return this.get<PaginatedResponse<NotificationTemplate>>('/templates', {
      params: {
        page,
        size,
        type
      }
    });
  }

  async getNotificationTemplate(id: string): Promise<NotificationTemplate> {
    return this.get<NotificationTemplate>(`/templates/${id}`);
  }

  async createNotificationTemplate(
    templateData: NotificationTemplateCreateData
  ): Promise<NotificationTemplate> {
    return this.post<NotificationTemplate>('/templates', templateData);
  }

  async updateNotificationTemplate(
    id: string,
    templateData: Partial<NotificationTemplateCreateData>
  ): Promise<NotificationTemplate> {
    return this.put<NotificationTemplate>(`/templates/${id}`, templateData);
  }

  async deleteNotificationTemplate(id: string): Promise<void> {
    return this.delete(`/templates/${id}`);
  }

  // Send notifications
  async sendNotification(request: NotificationSendRequest): Promise<void> {
    return this.post('/send', request);
  }

  // Notification preferences
  async getUserNotificationPreferences(): Promise<NotificationPreference[]> {
    return this.get<NotificationPreference[]>('/preferences');
  }

  async updateNotificationPreference(
    typeOrId: string, 
    preferences: {
      emailEnabled?: boolean;
      smsEnabled?: boolean;
      pushEnabled?: boolean;
      inAppEnabled?: boolean;
    }
  ): Promise<NotificationPreference> {
    return this.put<NotificationPreference>(`/preferences/${typeOrId}`, preferences);
  }
}

export default new NotificationService(); 