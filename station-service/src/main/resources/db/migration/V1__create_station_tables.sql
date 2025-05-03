-- Create charging_stations table
CREATE TABLE charging_stations (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    serial_number VARCHAR(100) NOT NULL UNIQUE,
    model VARCHAR(100),
    vendor VARCHAR(100),
    firmware_version VARCHAR(50),
    location_latitude DECIMAL(10, 7),
    location_longitude DECIMAL(10, 7),
    address VARCHAR(255),
    city VARCHAR(100),
    state VARCHAR(100),
    country VARCHAR(100),
    postal_code VARCHAR(20),
    cpo_id UUID,
    status VARCHAR(50) NOT NULL,
    last_heartbeat TIMESTAMP,
    registration_date TIMESTAMP NOT NULL DEFAULT NOW(),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Create connectors (EVSE) table
CREATE TABLE connectors (
    id UUID PRIMARY KEY,
    station_id UUID NOT NULL,
    connector_id INTEGER NOT NULL,
    connector_type VARCHAR(50) NOT NULL,
    power_type VARCHAR(50) NOT NULL,
    max_voltage INTEGER,
    max_amperage INTEGER,
    max_power_kw DECIMAL(10, 2),
    status VARCHAR(50) NOT NULL,
    last_status_update TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_station_connector
        FOREIGN KEY(station_id)
        REFERENCES charging_stations(id)
        ON DELETE CASCADE,
    CONSTRAINT unique_station_connector UNIQUE(station_id, connector_id)
);

-- Create charging_sessions table
CREATE TABLE charging_sessions (
    id UUID PRIMARY KEY,
    station_id UUID NOT NULL,
    connector_id INTEGER NOT NULL,
    transaction_id VARCHAR(255) NOT NULL UNIQUE,
    id_tag VARCHAR(255),
    user_id UUID,
    start_timestamp TIMESTAMP NOT NULL,
    stop_timestamp TIMESTAMP,
    meter_start INTEGER NOT NULL,
    meter_stop INTEGER,
    start_reason VARCHAR(50),
    stop_reason VARCHAR(50),
    total_energy_kwh DECIMAL(10, 2),
    total_cost DECIMAL(10, 2),
    currency VARCHAR(3) DEFAULT 'USD',
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_station_session
        FOREIGN KEY(station_id)
        REFERENCES charging_stations(id)
);

-- Create station_metrics table for station-level data
CREATE TABLE station_metrics (
    id UUID PRIMARY KEY,
    station_id UUID NOT NULL,
    timestamp TIMESTAMP NOT NULL,
    metric_type VARCHAR(50) NOT NULL,
    metric_value DECIMAL(10, 2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_station_metrics
        FOREIGN KEY(station_id)
        REFERENCES charging_stations(id)
        ON DELETE CASCADE
);

-- Create connector_metrics table for connector-level data
CREATE TABLE connector_metrics (
    id UUID PRIMARY KEY,
    station_id UUID NOT NULL,
    connector_id INTEGER NOT NULL,
    timestamp TIMESTAMP NOT NULL,
    meter_value DECIMAL(10, 2) NOT NULL,
    current_a DECIMAL(10, 2),
    voltage_v DECIMAL(10, 2),
    power_kw DECIMAL(10, 2),
    temperature_c DECIMAL(10, 2),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_connector_metrics
        FOREIGN KEY(station_id)
        REFERENCES charging_stations(id)
        ON DELETE CASCADE
);

-- Create station_heartbeats table
CREATE TABLE station_heartbeats (
    id UUID PRIMARY KEY,
    station_id UUID NOT NULL,
    timestamp TIMESTAMP NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_station_heartbeats
        FOREIGN KEY(station_id)
        REFERENCES charging_stations(id)
        ON DELETE CASCADE
);

-- Create indexes for better performance
CREATE INDEX idx_stations_cpo_id ON charging_stations(cpo_id);
CREATE INDEX idx_stations_status ON charging_stations(status);
CREATE INDEX idx_connectors_station_id ON connectors(station_id);
CREATE INDEX idx_connectors_status ON connectors(status);
CREATE INDEX idx_sessions_station_id ON charging_sessions(station_id);
CREATE INDEX idx_sessions_user_id ON charging_sessions(user_id);
CREATE INDEX idx_sessions_transaction_id ON charging_sessions(transaction_id);
CREATE INDEX idx_sessions_start_timestamp ON charging_sessions(start_timestamp);
CREATE INDEX idx_sessions_status ON charging_sessions(status);
CREATE INDEX idx_metrics_station_id ON station_metrics(station_id, timestamp);
CREATE INDEX idx_connector_metrics_station_connector ON connector_metrics(station_id, connector_id, timestamp);
CREATE INDEX idx_heartbeats_station_id ON station_heartbeats(station_id, timestamp); 