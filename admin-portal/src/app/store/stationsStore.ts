import { create } from 'zustand';
import { 
  stationService, 
  Station, 
  Connector,
  StationCreateData,
  StationUpdateData,
  StationFilterParams 
} from '@/app/services';

type StationsState = {
  stations: Station[];
  totalCount: number;
  currentPage: number;
  pageSize: number;
  isLoading: boolean;
  error: string | null;
  fetchStations: (page?: number, size?: number, filters?: StationFilterParams) => Promise<void>;
  addStation: (stationData: StationCreateData) => Promise<void>;
  updateStation: (id: string, updates: StationUpdateData) => Promise<void>;
  deleteStation: (id: string) => Promise<void>;
  getStationById: (id: string) => Promise<Station | null>;
  clearError: () => void;
  rebootStation: (id: string) => Promise<void>;
  updateFirmware: (id: string, firmwareVersion: string) => Promise<void>;
  resetConnector: (stationId: string, connectorId: string) => Promise<void>;
  enableConnector: (stationId: string, connectorId: string) => Promise<void>;
  disableConnector: (stationId: string, connectorId: string) => Promise<void>;
};

export const useStationsStore = create<StationsState>((set, get) => ({
  stations: [],
  totalCount: 0,
  currentPage: 0,
  pageSize: 10,
  isLoading: false,
  error: null,

  fetchStations: async (page = 0, size = 10, filters) => {
    set({ isLoading: true, error: null });
    try {
      const response = await stationService.getStations(page, size, filters);
      
      set({ 
        stations: response.content, 
        totalCount: response.totalElements,
        currentPage: page,
        pageSize: size,
        isLoading: false 
      });
    } catch (err) {
      console.error('Failed to fetch stations:', err);
      set({ 
        isLoading: false, 
        error: err instanceof Error ? err.message : 'An error occurred while fetching stations' 
      });
    }
  },

  addStation: async (stationData) => {
    set({ isLoading: true, error: null });
    try {
      const newStation = await stationService.createStation(stationData);
      
      set(state => ({ 
        stations: [...state.stations, newStation],
        totalCount: state.totalCount + 1,
        isLoading: false 
      }));
    } catch (err) {
      console.error('Failed to add station:', err);
      set({ 
        isLoading: false, 
        error: err instanceof Error ? err.message : 'An error occurred while adding the station' 
      });
    }
  },

  updateStation: async (id, updates) => {
    set({ isLoading: true, error: null });
    try {
      const updatedStation = await stationService.updateStation(id, updates);
      
      set(state => ({
        stations: state.stations.map(station => 
          station.id === id ? updatedStation : station
        ),
        isLoading: false
      }));
    } catch (err) {
      console.error('Failed to update station:', err);
      set({ 
        isLoading: false, 
        error: err instanceof Error ? err.message : 'An error occurred while updating the station' 
      });
    }
  },

  deleteStation: async (id) => {
    set({ isLoading: true, error: null });
    try {
      await stationService.deleteStation(id);
      
      set(state => ({
        stations: state.stations.filter(station => station.id !== id),
        totalCount: state.totalCount - 1,
        isLoading: false
      }));
    } catch (err) {
      console.error('Failed to delete station:', err);
      set({ 
        isLoading: false, 
        error: err instanceof Error ? err.message : 'An error occurred while deleting the station' 
      });
    }
  },

  getStationById: async (id) => {
    try {
      return await stationService.getStation(id);
    } catch (err) {
      console.error('Failed to get station:', err);
      set({ 
        error: err instanceof Error ? err.message : `An error occurred while fetching station ${id}` 
      });
      return null;
    }
  },

  clearError: () => {
    set({ error: null });
  },

  rebootStation: async (id) => {
    set({ isLoading: true, error: null });
    try {
      await stationService.rebootStation(id);
      set({ isLoading: false });
    } catch (err) {
      console.error('Failed to reboot station:', err);
      set({ 
        isLoading: false, 
        error: err instanceof Error ? err.message : `An error occurred while rebooting station ${id}` 
      });
    }
  },

  updateFirmware: async (id, firmwareVersion) => {
    set({ isLoading: true, error: null });
    try {
      await stationService.updateFirmware(id, firmwareVersion);
      set({ isLoading: false });
    } catch (err) {
      console.error('Failed to update firmware:', err);
      set({ 
        isLoading: false, 
        error: err instanceof Error ? err.message : `An error occurred while updating firmware for station ${id}` 
      });
    }
  },

  resetConnector: async (stationId, connectorId) => {
    set({ isLoading: true, error: null });
    try {
      await stationService.resetConnector(stationId, connectorId);
      
      // Refresh station details to get updated connector status
      const updatedStation = await stationService.getStation(stationId);
      
      set(state => ({
        stations: state.stations.map(station => 
          station.id === stationId ? updatedStation : station
        ),
        isLoading: false
      }));
    } catch (err) {
      console.error('Failed to reset connector:', err);
      set({ 
        isLoading: false, 
        error: err instanceof Error ? err.message : `An error occurred while resetting connector` 
      });
    }
  },

  enableConnector: async (stationId, connectorId) => {
    set({ isLoading: true, error: null });
    try {
      await stationService.enableConnector(stationId, connectorId);
      
      // Refresh station details to get updated connector status
      const updatedStation = await stationService.getStation(stationId);
      
      set(state => ({
        stations: state.stations.map(station => 
          station.id === stationId ? updatedStation : station
        ),
        isLoading: false
      }));
    } catch (err) {
      console.error('Failed to enable connector:', err);
      set({ 
        isLoading: false, 
        error: err instanceof Error ? err.message : `An error occurred while enabling connector` 
      });
    }
  },

  disableConnector: async (stationId, connectorId) => {
    set({ isLoading: true, error: null });
    try {
      await stationService.disableConnector(stationId, connectorId);
      
      // Refresh station details to get updated connector status
      const updatedStation = await stationService.getStation(stationId);
      
      set(state => ({
        stations: state.stations.map(station => 
          station.id === stationId ? updatedStation : station
        ),
        isLoading: false
      }));
    } catch (err) {
      console.error('Failed to disable connector:', err);
      set({ 
        isLoading: false, 
        error: err instanceof Error ? err.message : `An error occurred while disabling connector` 
      });
    }
  }
}));
