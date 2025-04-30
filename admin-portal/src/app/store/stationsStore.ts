import { create } from 'zustand';

type Connector = {
  id: number;
  type: string;
  status: 'Available' | 'Charging' | 'Offline';
  power: string;
};

type Station = {
  id: string;
  name: string;
  location: string;
  status: 'Online' | 'Offline';
  connectors: number;
  model?: string;
  serialNumber?: string;
  lastConnection?: string;
  firmwareVersion?: string;
  connectorDetails?: Connector[];
};

type StationsState = {
  stations: Station[];
  isLoading: boolean;
  error: string | null;
  fetchStations: () => Promise<void>;
  addStation: (station: Omit<Station, 'id'>) => Promise<void>;
  updateStation: (id: string, updates: Partial<Station>) => Promise<void>;
  deleteStation: (id: string) => Promise<void>;
  getStationById: (id: string) => Station | undefined;
  clearError: () => void;
};

export const useStationsStore = create<StationsState>((set, get) => ({
  stations: [],
  isLoading: false,
  error: null,

  fetchStations: async () => {
    set({ isLoading: true, error: null });
    try {
      // In a real application, this would be an API call to the station service
      // const response = await fetch('/api/stations');
      // if (!response.ok) {
      //   throw new Error('Failed to fetch stations');
      // }
      // const data = await response.json();
      
      // Simulate API call with mock data
      await new Promise(resolve => setTimeout(resolve, 1000));
      
      const mockStations: Station[] = [
        { id: 'station-001', name: 'EV Station Alpha', location: 'Downtown', status: 'Online', connectors: 4 },
        { id: 'station-002', name: 'EV Station Beta', location: 'Uptown', status: 'Offline', connectors: 2 },
        { id: 'station-003', name: 'EV Station Gamma', location: 'Midtown', status: 'Online', connectors: 6 },
      ];
      
      set({ stations: mockStations, isLoading: false });
    } catch (err) {
      set({ 
        isLoading: false, 
        error: err instanceof Error ? err.message : 'An error occurred while fetching stations' 
      });
    }
  },

  addStation: async (stationData) => {
    set({ isLoading: true, error: null });
    try {
      // In a real application, this would be an API call
      // const response = await fetch('/api/stations', {
      //   method: 'POST',
      //   headers: { 'Content-Type': 'application/json' },
      //   body: JSON.stringify(stationData),
      // });
      // if (!response.ok) {
      //   throw new Error('Failed to add station');
      // }
      // const newStation = await response.json();
      
      // Simulate API call
      await new Promise(resolve => setTimeout(resolve, 1000));
      
      const newId = `station-${Math.floor(Math.random() * 1000).toString().padStart(3, '0')}`;
      const newStation: Station = {
        ...stationData,
        id: newId,
        status: 'Offline', // New stations start offline until connected
      };
      
      set(state => ({ 
        stations: [...state.stations, newStation], 
        isLoading: false 
      }));
    } catch (err) {
      set({ 
        isLoading: false, 
        error: err instanceof Error ? err.message : 'An error occurred while adding the station' 
      });
    }
  },

  updateStation: async (id, updates) => {
    set({ isLoading: true, error: null });
    try {
      // In a real application, this would be an API call
      // const response = await fetch(`/api/stations/${id}`, {
      //   method: 'PATCH',
      //   headers: { 'Content-Type': 'application/json' },
      //   body: JSON.stringify(updates),
      // });
      // if (!response.ok) {
      //   throw new Error('Failed to update station');
      // }
      // const updatedStation = await response.json();
      
      // Simulate API call
      await new Promise(resolve => setTimeout(resolve, 1000));
      
      set(state => ({
        stations: state.stations.map(station => 
          station.id === id ? { ...station, ...updates } : station
        ),
        isLoading: false
      }));
    } catch (err) {
      set({ 
        isLoading: false, 
        error: err instanceof Error ? err.message : 'An error occurred while updating the station' 
      });
    }
  },

  deleteStation: async (id) => {
    set({ isLoading: true, error: null });
    try {
      // In a real application, this would be an API call
      // const response = await fetch(`/api/stations/${id}`, {
      //   method: 'DELETE',
      // });
      // if (!response.ok) {
      //   throw new Error('Failed to delete station');
      // }
      
      // Simulate API call
      await new Promise(resolve => setTimeout(resolve, 1000));
      
      set(state => ({
        stations: state.stations.filter(station => station.id !== id),
        isLoading: false
      }));
    } catch (err) {
      set({ 
        isLoading: false, 
        error: err instanceof Error ? err.message : 'An error occurred while deleting the station' 
      });
    }
  },

  getStationById: (id) => {
    return get().stations.find(station => station.id === id);
  },

  clearError: () => {
    set({ error: null });
  },
}));
