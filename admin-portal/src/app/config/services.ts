/**
 * Services configuration
 * This file centralizes service configurations for easier maintenance
 */

export interface ServiceConfig {
  id: string;
  name: string;
  description: string;
  baseUrl: string;
  basePath: string;
  version: string;
  healthEndpoint: string;
  dashboardPath: string;
  iconName?: string;
}

// Base API URL from environment variables
const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080';

export const services: Record<string, ServiceConfig> = {
  auth: {
    id: 'auth-service',
    name: 'Authentication',
    description: 'Manages user authentication, authorization, and account security',
    baseUrl: API_BASE_URL,
    basePath: '/api/v1/auth',
    version: 'v1',
    healthEndpoint: '/health',
    dashboardPath: '/dashboard/users',
    iconName: 'shield'
  },
  billing: {
    id: 'billing-service',
    name: 'Billing',
    description: 'Handles billing plans, subscriptions, invoices, and payments',
    baseUrl: API_BASE_URL,
    basePath: '/api/v1/billing',
    version: 'v1',
    healthEndpoint: '/health',
    dashboardPath: '/dashboard/billing',
    iconName: 'credit-card'
  },
  station: {
    id: 'station-service',
    name: 'Station',
    description: 'Manages charging stations, connectors, and charging sessions',
    baseUrl: API_BASE_URL,
    basePath: '/api/v1/stations',
    version: 'v1',
    healthEndpoint: '/health',
    dashboardPath: '/dashboard/stations',
    iconName: 'plug'
  },
  smartCharging: {
    id: 'smart-charging-service',
    name: 'Smart Charging',
    description: 'Handles intelligent charging schedules and load balancing',
    baseUrl: API_BASE_URL,
    basePath: '/api/v1/smart-charging',
    version: 'v1',
    healthEndpoint: '/health',
    dashboardPath: '/dashboard/smart-charging',
    iconName: 'zap'
  },
  notification: {
    id: 'notification-service',
    name: 'Notification',
    description: 'Manages user notifications, alerts, and messaging',
    baseUrl: API_BASE_URL,
    basePath: '/api/v1/notifications',
    version: 'v1',
    healthEndpoint: '/health',
    dashboardPath: '/dashboard/notifications',
    iconName: 'bell'
  },
  roaming: {
    id: 'roaming-service',
    name: 'Roaming',
    description: 'Enables charging across different networks via OCPI protocol',
    baseUrl: API_BASE_URL,
    basePath: '/api/v1/roaming',
    version: 'v1',
    healthEndpoint: '/health',
    dashboardPath: '/dashboard/roaming',
    iconName: 'globe'
  },
  user: {
    id: 'user-service',
    name: 'User',
    description: 'Manages user profiles, preferences, and RFID cards',
    baseUrl: API_BASE_URL,
    basePath: '/api/v1/users',
    version: 'v1', 
    healthEndpoint: '/health',
    dashboardPath: '/dashboard/users',
    iconName: 'users'
  },
  analytics: {
    id: 'analytics-service',
    name: 'Analytics',
    description: 'Provides data analysis and reporting capabilities',
    baseUrl: API_BASE_URL,
    basePath: '/api/v1/analytics',
    version: 'v1',
    healthEndpoint: '/health',
    dashboardPath: '/dashboard/analytics',
    iconName: 'bar-chart-2'
  }
};

// Helper function to get full service endpoint URL
export function getServiceEndpoint(serviceId: keyof typeof services, endpoint: string): string {
  const service = services[serviceId];
  // Remove leading slash from endpoint if it exists
  const normalizedEndpoint = endpoint.startsWith('/') ? endpoint.substring(1) : endpoint;
  return `${service.baseUrl}${service.basePath}/${normalizedEndpoint}`;
}

// Helper function to get service health endpoint
export function getServiceHealthEndpoint(serviceId: keyof typeof services): string {
  const service = services[serviceId];
  // Remove leading slash from health endpoint if it exists
  const healthPath = service.healthEndpoint.startsWith('/') 
    ? service.healthEndpoint.substring(1)
    : service.healthEndpoint;
  return `${service.baseUrl}${service.basePath}/${healthPath}`;
}

export default services; 