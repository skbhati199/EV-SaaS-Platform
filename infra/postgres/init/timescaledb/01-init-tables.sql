-- Create schema and extensions
CREATE EXTENSION IF NOT EXISTS timescaledb CASCADE;

-- Create tables for charging sessions
CREATE TABLE IF NOT EXISTS charging_sessions (
    id UUID PRIMARY KEY,
    station_id VARCHAR(36) NOT NULL,
    connector_id INTEGER NOT NULL,
    transaction_id VARCHAR(36),
    auth_id VARCHAR(64),
    start_time TIMESTAMP WITH TIME ZONE NOT NULL,
    end_time TIMESTAMP WITH TIME ZONE,
    start_meter_value NUMERIC(10, 2),
    end_meter_value NUMERIC(10, 2),
    energy_delivered_kwh NUMERIC(10, 2),
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Create hypertable
SELECT create_hypertable('charging_sessions', 'start_time', if_not_exists => TRUE);

-- Create telemetry table
CREATE TABLE IF NOT EXISTS telemetry_data (
    id UUID PRIMARY KEY,
    session_id UUID REFERENCES charging_sessions(id),
    station_id VARCHAR(36) NOT NULL,
    connector_id INTEGER NOT NULL,
    measured_at TIMESTAMP WITH TIME ZONE NOT NULL,
    meter_value NUMERIC(10, 2),
    current NUMERIC(10, 2),
    voltage NUMERIC(10, 2),
    power_kw NUMERIC(10, 2),
    temperature NUMERIC(5, 2),
    current NUMERIC(10, 2),
    voltage NUMERIC(10, 2),
    power_kw NUMERIC(10, 2),
    temperature NUMERIC(5, 2)
); 