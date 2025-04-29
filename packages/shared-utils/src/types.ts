/**
 * Common types used across the EV SaaS Platform
 */

/**
 * User role types
 */
export enum UserRole {
  ADMIN = 'ADMIN',
  OPERATOR = 'OPERATOR',
  USER = 'USER'
}

/**
 * Charging station status
 */
export enum StationStatus {
  AVAILABLE = 'AVAILABLE',
  OCCUPIED = 'OCCUPIED',
  OFFLINE = 'OFFLINE',
  FAULTED = 'FAULTED',
  RESERVED = 'RESERVED'
}

/**
 * OCPP message types
 */
export enum OcppMessageType {
  CALL = 2,
  CALLRESULT = 3,
  CALLERROR = 4
}

/**
 * Pagination parameters for API requests
 */
export interface PaginationParams {
  page: number;
  size: number;
  sort?: string;
  direction?: 'asc' | 'desc';
}

/**
 * Pagination response structure
 */
export interface PaginatedResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
}