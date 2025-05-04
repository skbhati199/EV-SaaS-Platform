import { ApiService } from './api';

// Type definitions for station data
export interface Connector {
  id: string;
  type: string; // Type 2, CCS, CHAdeMO, etc.
  maxPower: number;
  status: 'AVAILABLE' | 'OCCUPIED' | 'RESERVED' | 'UNAVAILABLE' | 'FAULTED';
  lastStatusUpdate?: string;
}

export interface Station {
  id: string;
  name: string;
  location: {
    address: string;
    city: string;
    zipCode: string;
    country: string;
    latitude: number;
    longitude: number;
  };
  status: 'ONLINE' | 'OFFLINE' | 'PARTIALLY_AVAILABLE';
  connectors: Connector[];
  vendor: string;
  model: string;
  serialNumber?: string;
  firmwareVersion?: string;
  lastHeartbeat?: string;
  lastConnected?: string;
  createdAt: string;
  updatedAt: string;
}

export interface StationFilter {
  status?: string;
  location?: string;
  vendor?: string;
  search?: string;
  page?: number;
  limit?: number;
}

export interface StationResponse {
  stations: Station[];
  total: number;
  page: number;
  limit: number;
}

// CRUD operations for stations
class StationService extends ApiService {
  constructor() {
    super();
    // Set base URL for station requests
    this.api.defaults.baseURL = `${this.api.defaults.baseURL}/stations`;
  }

  // Fetch all stations with optional filtering
  async getStations(filters: StationFilter = {}): Promise<StationResponse> {
    return this.get<StationResponse>('', { params: filters });
  }

  // Get a single station by ID
  async getStation(id: string): Promise<Station> {
    return this.get<Station>(`/${id}`);
  }

  // Create a new station
  async createStation(stationData: Partial<Station>): Promise<Station> {
    return this.post<Station>('', stationData);
  }

  // Update an existing station
  async updateStation(id: string, stationData: Partial<Station>): Promise<Station> {
    return this.put<Station>(`/${id}`, stationData);
  }

  // Delete a station
  async deleteStation(id: string): Promise<void> {
    return this.delete<void>(`/${id}`);
  }

  // Send remote command to a station (e.g. reboot, unlock connector)
  async sendCommand(id: string, command: string, params?: any): Promise<any> {
    return this.post<any>(`/${id}/commands`, { command, params });
  }
  
  // Reboot a station
  async rebootStation(id: string): Promise<boolean> {
    return this.sendCommand(id, 'Reboot');
  }
  
  // Update station firmware
  async updateFirmware(id: string, firmwareVersion: string): Promise<boolean> {
    return this.sendCommand(id, 'UpdateFirmware', { firmwareVersion });
  }
  
  // Reset a connector
  async resetConnector(stationId: string, connectorId: string): Promise<boolean> {
    return this.sendCommand(stationId, 'ResetConnector', { connectorId });
  }
  
  // Enable a connector
  async enableConnector(stationId: string, connectorId: string): Promise<boolean> {
    return this.sendCommand(stationId, 'EnableConnector', { connectorId });
  }
  
  // Disable a connector
  async disableConnector(stationId: string, connectorId: string): Promise<boolean> {
    return this.sendCommand(stationId, 'DisableConnector', { connectorId });
  }

  // Get station statistics (counts by status, etc.)
  async getStationStats(): Promise<any> {
    return this.get<any>('/stats');
  }
}

// Export a singleton instance
export const stationService = new StationService(); 