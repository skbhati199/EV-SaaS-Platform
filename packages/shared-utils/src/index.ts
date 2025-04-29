/**
 * EV SaaS Platform Shared Utilities
 * 
 * This package contains shared utilities, types, and functions used across
 * the EV SaaS Platform microservices and frontend applications.
 */

/**
 * Common response structure for API responses
 */
export interface ApiResponse<T> {
  success: boolean;
  data?: T;
  error?: string;
  timestamp: string;
}

/**
 * Create a standardized API response
 */
export function createApiResponse<T>(success: boolean, data?: T, error?: string): ApiResponse<T> {
  return {
    success,
    data,
    error,
    timestamp: new Date().toISOString()
  };
}

/**
 * Common error handling utility
 */
export function handleError(error: unknown): string {
  if (error instanceof Error) {
    return error.message;
  }
  return String(error);
}

/**
 * Date formatting utility
 */
export function formatDate(date: Date, format: string = 'yyyy-MM-dd'): string {
  // Simple implementation - in a real app, use a library like date-fns
  return date.toISOString().split('T')[0];
}

export * from './types';