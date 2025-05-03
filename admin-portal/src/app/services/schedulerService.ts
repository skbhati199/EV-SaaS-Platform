import { ApiService } from './api';
import { PaginatedResponse } from './userService';

export interface ScheduleTimeWindow {
  startTime: string; // ISO string
  endTime: string; // ISO string
}

export interface ScheduleTask {
  id: string;
  name: string;
  description?: string;
  stationId: string;
  connectorId?: string;
  userId: string;
  status: 'SCHEDULED' | 'RUNNING' | 'COMPLETED' | 'CANCELLED' | 'FAILED';
  timeWindow: ScheduleTimeWindow;
  energyRequiredKwh?: number;
  priority: 'LOW' | 'MEDIUM' | 'HIGH';
  recurring: boolean;
  recurrencePattern?: string; // CRON expression
  createdAt: string;
  updatedAt: string;
}

export interface V2GSchedule extends ScheduleTask {
  vehicleId: string;
  gridConnection: {
    maxExportKw: number;
    maxImportKw: number;
    exportPreference: 'PRICE' | 'GRID_DEMAND' | 'BATTERY_LEVEL';
  };
  bidirectional: boolean;
}

export interface ScheduleCreateData {
  name: string;
  description?: string;
  stationId: string;
  connectorId?: string;
  timeWindow: ScheduleTimeWindow;
  energyRequiredKwh?: number;
  priority?: 'LOW' | 'MEDIUM' | 'HIGH';
  recurring?: boolean;
  recurrencePattern?: string;
}

export interface V2GScheduleCreateData extends ScheduleCreateData {
  vehicleId: string;
  gridConnection: {
    maxExportKw: number;
    maxImportKw: number;
    exportPreference: 'PRICE' | 'GRID_DEMAND' | 'BATTERY_LEVEL';
  };
  bidirectional: boolean;
}

export interface ScheduleFilterParams {
  stationId?: string;
  userId?: string;
  status?: string;
  startDate?: string;
  endDate?: string;
}

class SchedulerService extends ApiService {
  constructor() {
    super();
    // Use scheduler-service as base URL
    this.api.defaults.baseURL = `${this.api.defaults.baseURL}/api/scheduler`;
  }

  // Get all scheduled tasks with pagination and filtering
  async getScheduledTasks(
    page = 0,
    size = 10,
    filters?: ScheduleFilterParams
  ): Promise<PaginatedResponse<ScheduleTask>> {
    return this.get<PaginatedResponse<ScheduleTask>>('/tasks', {
      params: {
        page,
        size,
        ...filters
      }
    });
  }

  // Get scheduled task by ID
  async getScheduledTask(id: string): Promise<ScheduleTask> {
    return this.get<ScheduleTask>(`/tasks/${id}`);
  }

  // Create new scheduled task
  async createScheduledTask(taskData: ScheduleCreateData): Promise<ScheduleTask> {
    return this.post<ScheduleTask>('/tasks', taskData);
  }

  // Update scheduled task
  async updateScheduledTask(id: string, taskData: Partial<ScheduleCreateData>): Promise<ScheduleTask> {
    return this.put<ScheduleTask>(`/tasks/${id}`, taskData);
  }

  // Delete scheduled task
  async deleteScheduledTask(id: string): Promise<void> {
    return this.delete(`/tasks/${id}`);
  }

  // Cancel scheduled task
  async cancelScheduledTask(id: string): Promise<ScheduleTask> {
    return this.post<ScheduleTask>(`/tasks/${id}/cancel`);
  }

  // V2G Schedules
  async getV2GSchedules(
    page = 0,
    size = 10,
    filters?: ScheduleFilterParams
  ): Promise<PaginatedResponse<V2GSchedule>> {
    return this.get<PaginatedResponse<V2GSchedule>>('/v2g', {
      params: {
        page,
        size,
        ...filters
      }
    });
  }

  async getV2GSchedule(id: string): Promise<V2GSchedule> {
    return this.get<V2GSchedule>(`/v2g/${id}`);
  }

  async createV2GSchedule(scheduleData: V2GScheduleCreateData): Promise<V2GSchedule> {
    return this.post<V2GSchedule>('/v2g', scheduleData);
  }

  async updateV2GSchedule(id: string, scheduleData: Partial<V2GScheduleCreateData>): Promise<V2GSchedule> {
    return this.put<V2GSchedule>(`/v2g/${id}`, scheduleData);
  }

  async deleteV2GSchedule(id: string): Promise<void> {
    return this.delete(`/v2g/${id}`);
  }

  async cancelV2GSchedule(id: string): Promise<V2GSchedule> {
    return this.post<V2GSchedule>(`/v2g/${id}/cancel`);
  }
}

export default new SchedulerService(); 