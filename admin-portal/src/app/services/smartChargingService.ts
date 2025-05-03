import { ApiService } from './api';
import { PaginatedResponse } from './userService';

export interface ChargingProfile {
  id: string;
  stationId: string;
  connectorId?: string;
  profileType: 'ABSOLUTE' | 'RECURRING' | 'RELATIVE';
  chargingProfileKind: 'CHARGE_POINT_MAX_PROFILE' | 'TX_DEFAULT_PROFILE' | 'TX_PROFILE';
  chargingRateUnit: 'A' | 'W';
  stackLevel: number;
  description: string;
  validFrom?: string;
  validTo?: string;
  transactionId?: string;
  status: 'ACTIVE' | 'INACTIVE' | 'SCHEDULED';
  chargingSchedule: {
    duration?: number;
    startSchedule?: string;
    chargingRateUnit: 'A' | 'W';
    minChargingRate?: number;
    chargingSchedulePeriods: {
      startPeriod: number;
      limit: number;
      numberPhases?: number;
    }[];
  };
  createdAt: string;
  updatedAt: string;
}

export interface ChargingProfileCreateData {
  stationId: string;
  connectorId?: string;
  profileType: 'ABSOLUTE' | 'RECURRING' | 'RELATIVE';
  chargingProfileKind: 'CHARGE_POINT_MAX_PROFILE' | 'TX_DEFAULT_PROFILE' | 'TX_PROFILE';
  chargingRateUnit: 'A' | 'W';
  stackLevel: number;
  description: string;
  validFrom?: string;
  validTo?: string;
  transactionId?: string;
  chargingSchedule: {
    duration?: number;
    startSchedule?: string;
    chargingRateUnit: 'A' | 'W';
    minChargingRate?: number;
    chargingSchedulePeriods: {
      startPeriod: number;
      limit: number;
      numberPhases?: number;
    }[];
  };
}

export interface SmartChargingGroup {
  id: string;
  name: string;
  description: string;
  capacity: number;
  unit: 'A' | 'W';
  stationIds: string[];
  strategy: 'FIFO' | 'EQUAL_DISTRIBUTION' | 'PRIORITY' | 'ENERGY_DEMAND';
  status: 'ACTIVE' | 'INACTIVE';
  createdAt: string;
  updatedAt: string;
}

export interface SmartChargingGroupCreateData {
  name: string;
  description: string;
  capacity: number;
  unit: 'A' | 'W';
  stationIds: string[];
  strategy: 'FIFO' | 'EQUAL_DISTRIBUTION' | 'PRIORITY' | 'ENERGY_DEMAND';
}

export interface GridConstraint {
  id: string;
  name: string;
  startDateTime: string;
  endDateTime: string;
  capacity: number;
  unit: 'A' | 'W';
  groupId?: string;
  stationId?: string;
  status: 'ACTIVE' | 'INACTIVE' | 'SCHEDULED';
  createdAt: string;
  updatedAt: string;
}

export interface GridConstraintCreateData {
  name: string;
  startDateTime: string;
  endDateTime: string;
  capacity: number;
  unit: 'A' | 'W';
  groupId?: string;
  stationId?: string;
}

export interface EnergyRate {
  id: string;
  startDate: string;
  endDate: string;
  timeOfUseRates: {
    startTime: string; // HH:mm format
    endTime: string; // HH:mm format
    price: number;
    currency: string;
    isRenewable?: boolean;
    carbonIntensity?: number; // g CO2/kWh
  }[];
  createdAt: string;
  updatedAt: string;
}

export interface EnergyRateCreateData {
  startDate: string;
  endDate: string;
  timeOfUseRates: {
    startTime: string;
    endTime: string;
    price: number;
    currency: string;
    isRenewable?: boolean;
    carbonIntensity?: number;
  }[];
}

class SmartChargingService extends ApiService {
  constructor() {
    super();
    // Use smart-charging as base URL
    this.api.defaults.baseURL = `${this.api.defaults.baseURL}/api/smart-charging`;
  }

  // Charging Profiles
  async getChargingProfiles(
    page = 0,
    size = 10,
    stationId?: string
  ): Promise<PaginatedResponse<ChargingProfile>> {
    return this.get<PaginatedResponse<ChargingProfile>>('/profiles', {
      params: {
        page,
        size,
        stationId
      }
    });
  }

  async getChargingProfile(id: string): Promise<ChargingProfile> {
    return this.get<ChargingProfile>(`/profiles/${id}`);
  }

  async createChargingProfile(profileData: ChargingProfileCreateData): Promise<ChargingProfile> {
    return this.post<ChargingProfile>('/profiles', profileData);
  }

  async updateChargingProfile(id: string, profileData: Partial<ChargingProfileCreateData>): Promise<ChargingProfile> {
    return this.put<ChargingProfile>(`/profiles/${id}`, profileData);
  }

  async deleteChargingProfile(id: string): Promise<void> {
    return this.delete(`/profiles/${id}`);
  }

  async activateChargingProfile(id: string): Promise<ChargingProfile> {
    return this.post<ChargingProfile>(`/profiles/${id}/activate`);
  }

  async deactivateChargingProfile(id: string): Promise<ChargingProfile> {
    return this.post<ChargingProfile>(`/profiles/${id}/deactivate`);
  }

  // Smart Charging Groups
  async getSmartChargingGroups(): Promise<SmartChargingGroup[]> {
    return this.get<SmartChargingGroup[]>('/groups');
  }

  async getSmartChargingGroup(id: string): Promise<SmartChargingGroup> {
    return this.get<SmartChargingGroup>(`/groups/${id}`);
  }

  async createSmartChargingGroup(groupData: SmartChargingGroupCreateData): Promise<SmartChargingGroup> {
    return this.post<SmartChargingGroup>('/groups', groupData);
  }

  async updateSmartChargingGroup(id: string, groupData: Partial<SmartChargingGroupCreateData>): Promise<SmartChargingGroup> {
    return this.put<SmartChargingGroup>(`/groups/${id}`, groupData);
  }

  async deleteSmartChargingGroup(id: string): Promise<void> {
    return this.delete(`/groups/${id}`);
  }

  async addStationToGroup(groupId: string, stationId: string): Promise<SmartChargingGroup> {
    return this.post<SmartChargingGroup>(`/groups/${groupId}/stations/${stationId}`);
  }

  async removeStationFromGroup(groupId: string, stationId: string): Promise<SmartChargingGroup> {
    return this.delete<SmartChargingGroup>(`/groups/${groupId}/stations/${stationId}`);
  }

  // Grid Constraints
  async getGridConstraints(
    page = 0,
    size = 10,
    groupId?: string,
    stationId?: string
  ): Promise<PaginatedResponse<GridConstraint>> {
    return this.get<PaginatedResponse<GridConstraint>>('/grid-constraints', {
      params: {
        page,
        size,
        groupId,
        stationId
      }
    });
  }

  async getGridConstraint(id: string): Promise<GridConstraint> {
    return this.get<GridConstraint>(`/grid-constraints/${id}`);
  }

  async createGridConstraint(constraintData: GridConstraintCreateData): Promise<GridConstraint> {
    return this.post<GridConstraint>('/grid-constraints', constraintData);
  }

  async updateGridConstraint(id: string, constraintData: Partial<GridConstraintCreateData>): Promise<GridConstraint> {
    return this.put<GridConstraint>(`/grid-constraints/${id}`, constraintData);
  }

  async deleteGridConstraint(id: string): Promise<void> {
    return this.delete(`/grid-constraints/${id}`);
  }

  // Energy Rates
  async getEnergyRates(
    page = 0,
    size = 10,
    startDate?: string,
    endDate?: string
  ): Promise<PaginatedResponse<EnergyRate>> {
    return this.get<PaginatedResponse<EnergyRate>>('/energy-rates', {
      params: {
        page,
        size,
        startDate,
        endDate
      }
    });
  }

  async getEnergyRate(id: string): Promise<EnergyRate> {
    return this.get<EnergyRate>(`/energy-rates/${id}`);
  }

  async createEnergyRate(rateData: EnergyRateCreateData): Promise<EnergyRate> {
    return this.post<EnergyRate>('/energy-rates', rateData);
  }

  async updateEnergyRate(id: string, rateData: Partial<EnergyRateCreateData>): Promise<EnergyRate> {
    return this.put<EnergyRate>(`/energy-rates/${id}`, rateData);
  }

  async deleteEnergyRate(id: string): Promise<void> {
    return this.delete(`/energy-rates/${id}`);
  }

  // Optimization
  async optimizeChargingSchedule(stationId: string, connectorId?: string): Promise<ChargingProfile> {
    return this.post<ChargingProfile>('/optimize', {
      stationId,
      connectorId
    });
  }

  async optimizeGroupChargingSchedules(groupId: string): Promise<ChargingProfile[]> {
    return this.post<ChargingProfile[]>(`/groups/${groupId}/optimize`);
  }
}

export default new SmartChargingService(); 