-- Create a separate schema and assign necessary permissions
CREATE SCHEMA IF NOT EXISTS stations;
CREATE SCHEMA IF NOT EXISTS sessions;
CREATE SCHEMA IF NOT EXISTS smart_charging;

-- Grant permissions
GRANT ALL PRIVILEGES ON SCHEMA stations TO evsaas;
GRANT ALL PRIVILEGES ON SCHEMA sessions TO evsaas;
GRANT ALL PRIVILEGES ON SCHEMA smart_charging TO evsaas;

-- Set search path
ALTER ROLE evsaas SET search_path TO stations, sessions, smart_charging, public;

-- Extensions (TimescaleDB is auto-loaded)
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pgcrypto";
CREATE EXTENSION IF NOT EXISTS "postgis";

-- Create hypertable for charging sessions
CREATE TABLE IF NOT EXISTS sessions.charging_sessions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    evse_id UUID NOT NULL,
    user_id UUID,
    start_time TIMESTAMPTZ NOT NULL,
    end_time TIMESTAMPTZ,
    energy_kwh DECIMAL,
    cost DECIMAL,
    status TEXT,
    metadata JSONB,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- Convert to TimescaleDB hypertable
SELECT create_hypertable('sessions.charging_sessions', 'start_time', if_not_exists => TRUE); 