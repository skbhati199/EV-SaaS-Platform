-- V1__initial_schema.sql
-- Initial database schema for EV SaaS Platform

-- Enable TimescaleDB extension
CREATE EXTENSION IF NOT EXISTS timescaledb CASCADE;

-- Enable PostGIS extension for spatial types (like GEOGRAPHY)
CREATE EXTENSION IF NOT EXISTS postgis;

-- Users table for authentication and authorization
CREATE TABLE users (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  email TEXT UNIQUE NOT NULL,
  password_hash TEXT NOT NULL,
  role TEXT CHECK (role IN ('admin', 'user', 'cpo', 'emsp')),
  phone TEXT,
  first_name TEXT,
  last_name TEXT,
  created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
  updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Charging stations table
CREATE TABLE charging_stations (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  cpo_id UUID REFERENCES users(id),
  name TEXT NOT NULL,
  location GEOGRAPHY(POINT, 4326),
  address TEXT,
  city TEXT,
  postal_code TEXT,
  country TEXT,
  ocpp_version TEXT,
  is_active BOOLEAN DEFAULT TRUE,
  created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
  updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- EVSEs (Electric Vehicle Supply Equipment) table
CREATE TABLE evses (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  station_id UUID REFERENCES charging_stations(id) ON DELETE CASCADE,
  connector_type TEXT,
  power_kw DECIMAL,
  status TEXT,
  ocpp_connector_id INT,
  is_available BOOLEAN DEFAULT TRUE,
  last_heartbeat TIMESTAMP WITH TIME ZONE,
  created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
  updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Charging sessions table (will be converted to TimescaleDB hypertable)
CREATE TABLE charging_sessions (
  id UUID NOT NULL DEFAULT gen_random_uuid(),
  evse_id UUID REFERENCES evses(id),
  user_id UUID REFERENCES users(id),
  start_time TIMESTAMP WITH TIME ZONE NOT NULL,
  end_time TIMESTAMP WITH TIME ZONE,
  energy_kwh DECIMAL,
  cost DECIMAL,
  status TEXT CHECK (status IN ('active', 'completed', 'error', 'cancelled')),
  created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
  updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
  -- For hypertables, primary key must include the time column
  CONSTRAINT charging_sessions_pkey PRIMARY KEY (id, start_time)
);

-- Convert charging_sessions to TimescaleDB hypertable
SELECT create_hypertable('charging_sessions', 'start_time');

-- Tariffs table for pricing
CREATE TABLE tariffs (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  cpo_id UUID REFERENCES users(id),
  name TEXT NOT NULL,
  currency TEXT DEFAULT 'USD',
  base_price DECIMAL DEFAULT 0,
  price_per_kwh DECIMAL NOT NULL,
  price_per_minute DECIMAL DEFAULT 0,
  created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
  updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- OCPI tokens table for roaming
CREATE TABLE ocpi_tokens (
  token TEXT PRIMARY KEY,
  user_id UUID REFERENCES users(id),
  issuer TEXT,
  valid BOOLEAN DEFAULT TRUE,
  type TEXT,
  created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
  updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- OCPI CDRs (Charge Detail Records) for roaming
CREATE TABLE ocpi_cdrs (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  session_id UUID, -- Remove foreign key constraint to hypertable
  cpo_id UUID REFERENCES users(id),
  emsp_id UUID REFERENCES users(id),
  total_cost DECIMAL,
  currency TEXT,
  status TEXT,
  created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
  updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Roaming partners table
CREATE TABLE roaming_partners (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  name TEXT NOT NULL,
  ocpi_endpoint TEXT,
  token TEXT,
  active BOOLEAN DEFAULT TRUE,
  created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
  updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Indexes for performance
CREATE INDEX idx_charging_sessions_user_id ON charging_sessions(user_id, start_time);
CREATE INDEX idx_charging_sessions_evse_id ON charging_sessions(evse_id, start_time);
CREATE INDEX idx_evses_station_id ON evses(station_id);
CREATE INDEX idx_charging_stations_cpo_id ON charging_stations(cpo_id);
CREATE INDEX idx_ocpi_tokens_user_id ON ocpi_tokens(user_id);
CREATE INDEX idx_ocpi_cdrs_session_id ON ocpi_cdrs(session_id);