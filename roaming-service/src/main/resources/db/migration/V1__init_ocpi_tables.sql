-- OCPI Party table (CPO/eMSP)
CREATE TABLE IF NOT EXISTS ocpi_party (
    id SERIAL PRIMARY KEY,
    party_id VARCHAR(3) NOT NULL,
    country_code VARCHAR(2) NOT NULL,
    role VARCHAR(10) NOT NULL,
    name VARCHAR(100),
    logo_url VARCHAR(255),
    website VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (party_id, country_code)
);

-- OCPI Endpoint table
CREATE TABLE IF NOT EXISTS ocpi_endpoint (
    id SERIAL PRIMARY KEY,
    party_id INT REFERENCES ocpi_party(id),
    version VARCHAR(10) NOT NULL,
    endpoint_type VARCHAR(50) NOT NULL,
    url VARCHAR(255) NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- OCPI Token table
CREATE TABLE IF NOT EXISTS ocpi_token (
    id SERIAL PRIMARY KEY,
    party_id INT REFERENCES ocpi_party(id),
    token_type VARCHAR(20) NOT NULL,
    token_a VARCHAR(255),
    token_b VARCHAR(255),
    token_c VARCHAR(255),
    valid_until TIMESTAMP WITH TIME ZONE,
    status VARCHAR(20) DEFAULT 'PENDING',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- OCPI Location table
CREATE TABLE IF NOT EXISTS ocpi_location (
    id SERIAL PRIMARY KEY,
    party_id INT REFERENCES ocpi_party(id),
    country_code VARCHAR(2) NOT NULL,
    party_id_text VARCHAR(3) NOT NULL,
    location_id VARCHAR(36) NOT NULL,
    name VARCHAR(255),
    address VARCHAR(255) NOT NULL,
    city VARCHAR(255) NOT NULL,
    postal_code VARCHAR(10),
    country VARCHAR(255) NOT NULL,
    coordinates VARCHAR(50) NOT NULL,
    last_updated TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (country_code, party_id_text, location_id)
);

-- OCPI EVSE table
CREATE TABLE IF NOT EXISTS ocpi_evse (
    id SERIAL PRIMARY KEY,
    location_id INT REFERENCES ocpi_location(id),
    evse_id VARCHAR(36) NOT NULL,
    status VARCHAR(20) NOT NULL,
    status_schedule_id VARCHAR(36),
    capabilities VARCHAR(255)[],
    floor_level VARCHAR(4),
    coordinates VARCHAR(50),
    physical_reference VARCHAR(16),
    directions VARCHAR(255)[],
    last_updated TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (location_id, evse_id)
);

-- OCPI Connector table
CREATE TABLE IF NOT EXISTS ocpi_connector (
    id SERIAL PRIMARY KEY,
    evse_id INT REFERENCES ocpi_evse(id),
    connector_id VARCHAR(36) NOT NULL,
    connector_type VARCHAR(20) NOT NULL,
    format VARCHAR(20) NOT NULL,
    power_type VARCHAR(20) NOT NULL,
    max_voltage INTEGER,
    max_amperage INTEGER,
    max_electric_power INTEGER,
    tariff_ids VARCHAR(36)[],
    last_updated TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (evse_id, connector_id)
);

-- OCPI Tariff table
CREATE TABLE IF NOT EXISTS ocpi_tariff (
    id SERIAL PRIMARY KEY,
    party_id INT REFERENCES ocpi_party(id),
    country_code VARCHAR(2) NOT NULL,
    party_id_text VARCHAR(3) NOT NULL,
    tariff_id VARCHAR(36) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    last_updated TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (country_code, party_id_text, tariff_id)
);

-- OCPI Session table
CREATE TABLE IF NOT EXISTS ocpi_session (
    id SERIAL PRIMARY KEY,
    country_code VARCHAR(2) NOT NULL,
    party_id_text VARCHAR(3) NOT NULL,
    session_id VARCHAR(36) NOT NULL,
    start_datetime TIMESTAMP WITH TIME ZONE NOT NULL,
    end_datetime TIMESTAMP WITH TIME ZONE,
    kwh DECIMAL(10, 2) DEFAULT 0.0,
    cdr_token_id VARCHAR(36),
    auth_id VARCHAR(36),
    auth_method VARCHAR(20),
    location_id VARCHAR(36),
    evse_uid VARCHAR(36),
    connector_id VARCHAR(36),
    status VARCHAR(20) NOT NULL,
    last_updated TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (country_code, party_id_text, session_id)
);

-- OCPI CDR (Charge Detail Record) table
CREATE TABLE IF NOT EXISTS ocpi_cdr (
    id SERIAL PRIMARY KEY,
    country_code VARCHAR(2) NOT NULL,
    party_id_text VARCHAR(3) NOT NULL,
    cdr_id VARCHAR(36) NOT NULL,
    session_id VARCHAR(36),
    start_datetime TIMESTAMP WITH TIME ZONE NOT NULL,
    end_datetime TIMESTAMP WITH TIME ZONE NOT NULL,
    total_cost DECIMAL(10, 2),
    total_energy DECIMAL(10, 2),
    currency VARCHAR(3),
    status VARCHAR(20) NOT NULL,
    last_updated TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (country_code, party_id_text, cdr_id)
); 