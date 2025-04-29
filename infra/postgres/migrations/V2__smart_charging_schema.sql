-- V2__smart_charging_schema.sql
-- Smart Charging and Grid Integration schema for EV SaaS Platform

-- Smart Charging Profiles
CREATE TABLE charging_profiles (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  evse_id UUID REFERENCES evses(id) ON DELETE CASCADE,
  start_time TIMESTAMP WITH TIME ZONE NOT NULL,
  end_time TIMESTAMP WITH TIME ZONE,
  max_power_kw DECIMAL NOT NULL,
  min_power_kw DECIMAL,
  priority INTEGER DEFAULT 0,
  profile_type TEXT CHECK (profile_type IN ('absolute', 'relative', 'recurring')),
  created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
  updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Charging Profile Periods (time segments within a profile)
CREATE TABLE charging_profile_periods (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  profile_id UUID REFERENCES charging_profiles(id) ON DELETE CASCADE,
  start_offset_seconds INTEGER NOT NULL,
  power_limit_kw DECIMAL NOT NULL,
  created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Grid Connection Points
CREATE TABLE grid_connection_points (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  name TEXT NOT NULL,
  location GEOGRAPHY(POINT, 4326),
  max_capacity_kw DECIMAL NOT NULL,
  current_load_kw DECIMAL DEFAULT 0,
  created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
  updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Station to Grid Connection mapping
CREATE TABLE station_grid_connections (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  station_id UUID REFERENCES charging_stations(id) ON DELETE CASCADE,
  grid_connection_id UUID REFERENCES grid_connection_points(id) ON DELETE CASCADE,
  max_power_kw DECIMAL NOT NULL,
  created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
  UNIQUE(station_id, grid_connection_id)
);

-- V2G (Vehicle to Grid) Sessions
CREATE TABLE v2g_sessions (
  id UUID NOT NULL DEFAULT gen_random_uuid(),
  charging_session_id UUID, -- Remove foreign key to charging_sessions hypertable
  start_time TIMESTAMP WITH TIME ZONE NOT NULL,
  end_time TIMESTAMP WITH TIME ZONE,
  energy_to_grid_kwh DECIMAL DEFAULT 0,
  energy_from_grid_kwh DECIMAL DEFAULT 0,
  status TEXT CHECK (status IN ('active', 'completed', 'error', 'cancelled')),
  created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
  updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
  CONSTRAINT v2g_sessions_pkey PRIMARY KEY (id, start_time)
);

-- Convert v2g_sessions to TimescaleDB hypertable
SELECT create_hypertable('v2g_sessions', 'start_time');

-- Power Measurements (time-series data for power monitoring)
CREATE TABLE power_measurements (
  id UUID NOT NULL DEFAULT gen_random_uuid(),
  evse_id UUID REFERENCES evses(id),
  grid_connection_id UUID REFERENCES grid_connection_points(id),
  timestamp TIMESTAMP WITH TIME ZONE NOT NULL,
  power_kw DECIMAL NOT NULL,
  voltage_v DECIMAL,
  current_a DECIMAL,
  frequency_hz DECIMAL,
  energy_kwh DECIMAL,
  created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
  CONSTRAINT power_measurements_pkey PRIMARY KEY (id, timestamp)
);

-- Convert power_measurements to TimescaleDB hypertable
SELECT create_hypertable('power_measurements', 'timestamp');

-- Indexes for performance
CREATE INDEX idx_charging_profiles_evse_id ON charging_profiles(evse_id);
CREATE INDEX idx_charging_profile_periods_profile_id ON charging_profile_periods(profile_id);
CREATE INDEX idx_station_grid_connections_station_id ON station_grid_connections(station_id);
CREATE INDEX idx_station_grid_connections_grid_id ON station_grid_connections(grid_connection_id);
CREATE INDEX idx_v2g_sessions_charging_session_id ON v2g_sessions(charging_session_id, start_time);
CREATE INDEX idx_power_measurements_evse_id ON power_measurements(evse_id, timestamp);
CREATE INDEX idx_power_measurements_grid_id ON power_measurements(grid_connection_id, timestamp);