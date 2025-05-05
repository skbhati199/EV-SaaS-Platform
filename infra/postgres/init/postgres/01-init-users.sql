-- Create schemas for different services
CREATE SCHEMA IF NOT EXISTS auth;
CREATE SCHEMA IF NOT EXISTS user_management;
CREATE SCHEMA IF NOT EXISTS billing;
CREATE SCHEMA IF NOT EXISTS scheduler;
CREATE SCHEMA IF NOT EXISTS notification;

-- Users table
CREATE TABLE IF NOT EXISTS user_management.users (
    id UUID PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    password_hash VARCHAR(100),
    account_type VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Organizations table
CREATE TABLE IF NOT EXISTS user_management.organizations (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    org_type VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- User-Organization relationship table
CREATE TABLE IF NOT EXISTS user_management.user_organizations (
    user_id UUID REFERENCES user_management.users(id),
    organization_id UUID REFERENCES user_management.organizations(id),
    role VARCHAR(50) NOT NULL,
    PRIMARY KEY (user_id, organization_id)
);

-- Billing table for tariffs
CREATE TABLE IF NOT EXISTS billing.tariffs (
    id UUID PRIMARY KEY,
    organization_id UUID REFERENCES user_management.organizations(id),
    name VARCHAR(100) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    price_per_kwh NUMERIC(10, 4) NOT NULL,
    price_per_minute NUMERIC(10, 4),
    price_parking NUMERIC(10, 4),
    price_session_start NUMERIC(10, 4),
    is_public BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Create a default admin user
INSERT INTO user_management.users (id, username, email, account_type, status)
VALUES ('00000000-0000-0000-0000-000000000001', 'admin', 'admin@example.com', 'ADMIN', 'ACTIVE')
ON CONFLICT DO NOTHING; 