-- Create charging groups table
CREATE TABLE charging_groups (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    max_power_kw DOUBLE PRECISION NOT NULL,
    current_power_kw DOUBLE PRECISION,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    load_balancing_strategy VARCHAR(50),
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

-- Create charging stations table
CREATE TABLE charging_stations (
    id UUID PRIMARY KEY,
    max_power_kw DOUBLE PRECISION NOT NULL,
    current_power_kw DOUBLE PRECISION,
    priority_level INTEGER,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    smart_charging_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    charging_group_id UUID,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT fk_charging_group
        FOREIGN KEY(charging_group_id)
        REFERENCES charging_groups(id)
);

-- Create power profiles table
CREATE TABLE power_profiles (
    id UUID PRIMARY KEY,
    station_id UUID,
    group_id UUID,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    max_power_kw DOUBLE PRECISION NOT NULL,
    min_power_kw DOUBLE PRECISION,
    day_of_week VARCHAR(20), -- comma-separated values, e.g. "1,2,3,4,5" for weekdays
    price_tier VARCHAR(20),
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT fk_station
        FOREIGN KEY(station_id)
        REFERENCES charging_stations(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_group
        FOREIGN KEY(group_id)
        REFERENCES charging_groups(id)
        ON DELETE CASCADE
);

-- Create charging sessions table
CREATE TABLE charging_sessions (
    id UUID PRIMARY KEY,
    station_id UUID NOT NULL,
    connector_id INTEGER,
    user_id UUID,
    start_time TIMESTAMP,
    end_time TIMESTAMP,
    allocated_power_kw DOUBLE PRECISION,
    max_power_kw DOUBLE PRECISION,
    energy_delivered_kwh DOUBLE PRECISION,
    priority_level INTEGER,
    session_status VARCHAR(50),
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT fk_station
        FOREIGN KEY(station_id)
        REFERENCES charging_stations(id)
);

-- Create indexes for performance
CREATE INDEX idx_charging_station_group ON charging_stations(charging_group_id);
CREATE INDEX idx_charging_session_station ON charging_sessions(station_id);
CREATE INDEX idx_charging_session_status ON charging_sessions(session_status);
CREATE INDEX idx_power_profile_station ON power_profiles(station_id);
CREATE INDEX idx_power_profile_group ON power_profiles(group_id); 