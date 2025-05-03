import { ApiService } from './api';
import { PaginatedResponse } from './userService';

export interface StationLocation {
  latitude: number;
  longitude: number;
  address: string;
  city: string;
  state: string;
  country: string;
  postalCode: string;
}

export interface Connector {
  id: string;
  connectorNumber: number;
  type: string;
  status: string;
  maxPower: number;
  currentPower?: number;
  lastUpdated?: string;
}

export interface Station {
  id: string;
  name: string;
  serialNumber: string;
  model: string;
  manufacturer: string;
  location: StationLocation;
  connectors: Connector[];
  status: string;
  lastConnected?: string;
  firmwareVersion?: string;
  owner: string;
  createdAt: string;
  updatedAt: string;
}

export interface StationCreateData {
  name: string;
  serialNumber: string;
  model: string;
  manufacturer: string;
  location: StationLocation;
  connectors: {
    connectorNumber: number;
    type: string;
    maxPower: number;
  }[];
}

export interface StationUpdateData {
  name?: string;
  location?: Partial<StationLocation>;
  status?: string;
}

export interface StationFilterParams {
  name?: string;
  status?: string;
  city?: string;
  state?: string;
  country?: string;
}

export interface StationStats {
  totalStations: number;
  activeStations: number;
  totalConnectors: number;
  availableConnectors: number;
  chargingConnectors: number;
  faultedConnectors: number;
}

class StationService extends ApiService {
  constructor() {
    super();
    // Use station-service as base URL
    this.api.defaults.baseURL = `${this.api.defaults.baseURL}/api/stations`;
  }

  // Get all stations with pagination and filtering
  async getStations(
    page = 0,
    size = 10,
    filters?: StationFilterParams
  ): Promise<PaginatedResponse<Station>> {
    return this.get<PaginatedResponse<Station>>('', {
      params: {
        page,
        size,
        ...filters
      }
    });
  }

  // Get station by ID
  async getStation(id: string): Promise<Station> {
    return this.get<Station>(`/${id}`);
  }

  // Create new station
  async createStation(stationData: StationCreateData): Promise<Station> {
    return this.post<Station>('', stationData);
  }

  // Update station
  async updateStation(id: string, stationData: StationUpdateData): Promise<Station> {
    return this.put<Station>(`/${id}`, stationData);
  }

  // Delete station
  async deleteStation(id: string): Promise<void> {
    return this.delete(`/${id}`);
  }

  // Get station statistics
  async getStationStats(): Promise<StationStats> {
    return this.get<StationStats>('/stats');
  }

  // Remote station commands
  async rebootStation(id: string): Promise<void> {
    return this.post(`/${id}/reboot`);
  }

  async updateFirmware(id: string, firmwareVersion: string): Promise<void> {
    return this.post(`/${id}/update-firmware`, { firmwareVersion });
  }

  // Connector operations
  async resetConnector(stationId: string, connectorId: string): Promise<void> {
    return this.post(`/${stationId}/connectors/${connectorId}/reset`);
  }

  async enableConnector(stationId: string, connectorId: string): Promise<void> {
    return this.post(`/${stationId}/connectors/${connectorId}/enable`);
  }

  async disableConnector(stationId: string, connectorId: string): Promise<void> {
    return this.post(`/${stationId}/connectors/${connectorId}/disable`);
  }
}

export default new StationService(); 