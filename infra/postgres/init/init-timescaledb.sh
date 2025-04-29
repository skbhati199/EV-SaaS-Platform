#!/bin/bash
set -e

# Create extensions and prepare database
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
    -- Enable required extensions
    CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
    CREATE EXTENSION IF NOT EXISTS "postgis";
    CREATE EXTENSION IF NOT EXISTS "timescaledb" CASCADE;
    
    -- Create schema for Flyway migrations
    CREATE SCHEMA IF NOT EXISTS flyway_schema;
    
    -- Grant privileges
    GRANT ALL PRIVILEGES ON DATABASE $POSTGRES_DB TO $POSTGRES_USER;
EOSQL

echo "PostgreSQL with TimescaleDB initialized successfully"