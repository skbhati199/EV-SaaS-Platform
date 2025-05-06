import { ApiService } from './api';
import { authService } from './authService';
import { userService } from './userService';
import { stationService } from './stationService';
import { schedulerService } from './schedulerService';
import { billingService } from './billingService';
import { roamingService } from './roamingService';
import { smartChargingService } from './smartChargingService';
import { notificationService } from './notificationService';

// Create a singleton instance of ApiService for use throughout the app
export const apiService = new ApiService();

export {
  ApiService,
  authService,
  userService,
  stationService,
  schedulerService,
  billingService,
  roamingService,
  smartChargingService,
  notificationService
};

// Re-export types
export * from './api';
export * from './authService';
export * from './userService';
export * from './stationService';
export * from './schedulerService';
export * from './billingService';
export * from './roamingService';
export * from './smartChargingService';
export * from './notificationService';
