-- Create charging_stations table if it doesn't exist
CREATE TABLE IF NOT EXISTS charging_stations (
    id SERIAL PRIMARY KEY,
    station_id VARCHAR(100) UNIQUE NOT NULL,
    serial_number VARCHAR(100) UNIQUE,
    model VARCHAR(100),
    vendor VARCHAR(100),
    firmware_version VARCHAR(50),
    modem_imsi VARCHAR(50),
    modem_iccid VARCHAR(50),
    status VARCHAR(20) NOT NULL DEFAULT 'OFFLINE',
    last_heartbeat TIMESTAMP WITH TIME ZONE,
    last_boot_time TIMESTAMP WITH TIME ZONE,
    ip_address VARCHAR(45),
    latitude DECIMAL(10, 8),
    longitude DECIMAL(11, 8),
    address VARCHAR(255),
    city VARCHAR(100),
    postal_code VARCHAR(20),
    country VARCHAR(2),
    timezone VARCHAR(50) DEFAULT 'UTC',
    registration_status VARCHAR(20) DEFAULT 'REGISTERED',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    cpo_id VARCHAR(100),
    ocpp_version VARCHAR(10) DEFAULT '1.6',
    ocpp_protocol VARCHAR(10) DEFAULT 'JSON',
    configuration JSONB
);

-- Create index on station_id for faster lookups
CREATE INDEX IF NOT EXISTS idx_station_id ON charging_stations(station_id);
CREATE INDEX IF NOT EXISTS idx_station_status ON charging_stations(status);
CREATE INDEX IF NOT EXISTS idx_station_cpo ON charging_stations(cpo_id);
CREATE INDEX IF NOT EXISTS idx_station_location ON charging_stations(latitude, longitude);

-- Create connectors table
CREATE TABLE IF NOT EXISTS connectors (
    id SERIAL PRIMARY KEY,
    station_id VARCHAR(100) REFERENCES charging_stations(station_id) ON DELETE CASCADE,
    connector_id INTEGER NOT NULL,
    connector_type VARCHAR(50) NOT NULL,
    max_power DECIMAL(10, 2),
    status VARCHAR(20) DEFAULT 'AVAILABLE',
    current_transaction_id VARCHAR(100),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    last_status_change TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    UNIQUE(station_id, connector_id)
);

CREATE INDEX IF NOT EXISTS idx_connector_station ON connectors(station_id);
CREATE INDEX IF NOT EXISTS idx_connector_status ON connectors(status);

-- Create charging_sessions table
CREATE TABLE IF NOT EXISTS charging_sessions (
    id SERIAL PRIMARY KEY,
    transaction_id VARCHAR(100) UNIQUE NOT NULL,
    station_id VARCHAR(100) REFERENCES charging_stations(station_id),
    connector_id INTEGER,
    start_timestamp TIMESTAMP WITH TIME ZONE NOT NULL,
    stop_timestamp TIMESTAMP WITH TIME ZONE,
    auth_method VARCHAR(50),
    auth_id VARCHAR(100),
    meter_start INTEGER NOT NULL,
    meter_stop INTEGER,
    energy_delivered DECIMAL(10, 2),
    duration_seconds INTEGER,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    stop_reason VARCHAR(50),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    user_id VARCHAR(100),
    cost DECIMAL(10, 2),
    tariff_id VARCHAR(100),
    ocpi_session_id VARCHAR(100),
    FOREIGN KEY (station_id, connector_id) REFERENCES connectors(station_id, connector_id)
);

CREATE INDEX IF NOT EXISTS idx_session_transaction ON charging_sessions(transaction_id);
CREATE INDEX IF NOT EXISTS idx_session_station ON charging_sessions(station_id);
CREATE INDEX IF NOT EXISTS idx_session_user ON charging_sessions(user_id);
CREATE INDEX IF NOT EXISTS idx_session_dates ON charging_sessions(start_timestamp, stop_timestamp);

-- Create hypertable for charging_sessions to leverage TimescaleDB time-series capabilities
SELECT create_hypertable('charging_sessions', 'start_timestamp', if_not_exists => TRUE);

-- Insert sample data for testing (optional - comment out if not needed in production)
INSERT INTO charging_stations (station_id, serial_number, model, vendor, status, latitude, longitude, address, city, country, ocpp_version)
VALUES 
    ('CS001', 'SN001', 'PowerCharger', 'EV Solutions', 'OFFLINE', 37.7749, -122.4194, '123 Main St', 'San Francisco', 'US', '1.6'),
    ('CS002', 'SN002', 'TurboCharge', 'ChargePro', 'OFFLINE', 40.7128, -74.0060, '456 Park Ave', 'New York', 'US', '1.6'),
    ('CS003', 'SN003', 'FastCharger', 'PowerUp', 'OFFLINE', 34.0522, -118.2437, '789 Broadway', 'Los Angeles', 'US', '1.6')
ON CONFLICT (station_id) DO NOTHING;

-- Insert sample connectors
INSERT INTO connectors (station_id, connector_id, connector_type, max_power, status)
VALUES 
    ('CS001', 1, 'Type2', 22.0, 'AVAILABLE'),
    ('CS001', 2, 'CCS', 50.0, 'AVAILABLE'),
    ('CS002', 1, 'Type2', 11.0, 'AVAILABLE'),
    ('CS002', 2, 'CHAdeMO', 50.0, 'AVAILABLE'),
    ('CS003', 1, 'Type2', 22.0, 'AVAILABLE'),
    ('CS003', 2, 'CCS', 150.0, 'AVAILABLE')
ON CONFLICT (station_id, connector_id) DO NOTHING; 