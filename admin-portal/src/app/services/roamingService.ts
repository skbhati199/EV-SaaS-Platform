import { ApiService } from './api';
import { PaginatedResponse } from './userService';

export interface RoamingPartner {
  id: string;
  name: string;
  url: string;
  status: 'ACTIVE' | 'INACTIVE' | 'PENDING';
  token: string;
  countryCode: string;
  partyId: string;
  ocpiVersion: string;
  lastConnectionTime?: string;
  createdAt: string;
  updatedAt: string;
}

export interface RoamingPartnerCreateData {
  name: string;
  url: string;
  token: string;
  countryCode: string;
  partyId: string;
  ocpiVersion: string;
}

export interface OcpiCredentials {
  token: string;
  url: string;
  roles: {
    role: 'CPO' | 'EMSP' | 'HUB' | 'NAP' | 'NSP' | 'OTHER';
    businessDetails: {
      name: string;
      website: string;
      logo?: string;
    };
    partyId: string;
    countryCode: string;
  }[];
  status: 'PENDING' | 'REGISTERED' | 'SUSPENDED' | 'ACTIVE';
}

export interface RoamingLocation {
  id: string;
  name: string;
  address: string;
  city: string;
  postalCode: string;
  country: string;
  coordinates: {
    latitude: number;
    longitude: number;
  };
  operator: {
    id: string;
    name: string;
  };
  evses: RoamingEvse[];
  directions?: string[];
  facilities?: string[];
  timeZone: string;
  openingTimes?: {
    twentyfourseven: boolean;
    regularHours?: {
      weekday: number;
      periodBegin: string;
      periodEnd: string;
    }[];
    exceptionalOpenings?: {
      periodBegin: string;
      periodEnd: string;
    }[];
    exceptionalClosings?: {
      periodBegin: string;
      periodEnd: string;
    }[];
  };
  lastUpdated: string;
}

export interface RoamingEvse {
  uid: string;
  evseId: string;
  status: 'AVAILABLE' | 'CHARGING' | 'INOPERATIVE' | 'OUTOFORDER' | 'PLANNED' | 'REMOVED' | 'RESERVED' | 'UNKNOWN';
  capabilities?: string[];
  connectors: RoamingConnector[];
  coordinates?: {
    latitude: number;
    longitude: number;
  };
  floorLevel?: string;
  physicalReference?: string;
  lastUpdated: string;
}

export interface RoamingConnector {
  id: string;
  standard: string;
  format: string;
  powerType: string;
  maxVoltage: number;
  maxAmperage: number;
  maxElectricalPower?: number;
  tariffIds?: string[];
  lastUpdated: string;
}

export interface RoamingSession {
  id: string;
  startDateTime: string;
  endDateTime?: string;
  kwh: number;
  authMethod: string;
  authorizationReference: string;
  locationId: string;
  evseUid: string;
  connectorId: string;
  meterId?: string;
  currency: string;
  chargingPeriods?: {
    startDateTime: string;
    dimensions: {
      type: string;
      volume: number;
    }[];
    tariffId?: string;
  }[];
  total_cost?: number;
  status: 'ACTIVE' | 'COMPLETED' | 'INVALID' | 'PENDING';
  lastUpdated: string;
}

class RoamingService extends ApiService {
  constructor() {
    super();
    // Use roaming-service as base URL
    this.api.defaults.baseURL = `${this.api.defaults.baseURL}/api/roaming`;
  }

  // Roaming Partners
  async getRoamingPartners(): Promise<RoamingPartner[]> {
    return this.get<RoamingPartner[]>('/partners');
  }

  async getRoamingPartner(id: string): Promise<RoamingPartner> {
    return this.get<RoamingPartner>(`/partners/${id}`);
  }

  async createRoamingPartner(partnerData: RoamingPartnerCreateData): Promise<RoamingPartner> {
    return this.post<RoamingPartner>('/partners', partnerData);
  }

  async updateRoamingPartner(id: string, partnerData: Partial<RoamingPartnerCreateData>): Promise<RoamingPartner> {
    return this.put<RoamingPartner>(`/partners/${id}`, partnerData);
  }

  async deleteRoamingPartner(id: string): Promise<void> {
    return this.delete(`/partners/${id}`);
  }

  async activateRoamingPartner(id: string): Promise<RoamingPartner> {
    return this.post<RoamingPartner>(`/partners/${id}/activate`);
  }

  async deactivateRoamingPartner(id: string): Promise<RoamingPartner> {
    return this.post<RoamingPartner>(`/partners/${id}/deactivate`);
  }

  // OCPI Credentials
  async getOcpiCredentials(): Promise<OcpiCredentials> {
    return this.get<OcpiCredentials>('/credentials');
  }

  async registerOcpiCredentials(url: string, token: string): Promise<OcpiCredentials> {
    return this.post<OcpiCredentials>('/credentials/register', { url, token });
  }

  async updateOcpiCredentials(credentials: Partial<OcpiCredentials>): Promise<OcpiCredentials> {
    return this.put<OcpiCredentials>('/credentials', credentials);
  }

  // Roaming Locations
  async getRoamingLocations(
    page = 0,
    size = 10,
    countryCode?: string,
    partyId?: string
  ): Promise<PaginatedResponse<RoamingLocation>> {
    return this.get<PaginatedResponse<RoamingLocation>>('/locations', {
      params: {
        page,
        size,
        countryCode,
        partyId
      }
    });
  }

  async getRoamingLocation(id: string): Promise<RoamingLocation> {
    return this.get<RoamingLocation>(`/locations/${id}`);
  }

  // Roaming Sessions
  async getRoamingSessions(
    page = 0,
    size = 10,
    dateFrom?: string,
    dateTo?: string
  ): Promise<PaginatedResponse<RoamingSession>> {
    return this.get<PaginatedResponse<RoamingSession>>('/sessions', {
      params: {
        page,
        size,
        dateFrom,
        dateTo
      }
    });
  }

  async getRoamingSession(id: string): Promise<RoamingSession> {
    return this.get<RoamingSession>(`/sessions/${id}`);
  }
}

export default new RoamingService(); 