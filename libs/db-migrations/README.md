# Database Migrations for EV SaaS Platform

This directory contains the database migration scripts and configuration for the EV SaaS Platform project. We use Flyway for database schema migrations and PostgreSQL with TimescaleDB extension for time-series data.

## Setup

### Prerequisites

- Docker and Docker Compose
- PostgreSQL client (optional, for direct database access)
- Flyway CLI (optional, for running migrations manually)

### Database Structure

The database schema includes the following main tables:

- `users` - User accounts including admins, CPOs, EMSPs, and regular users
- `charging_stations` - Charging station information
- `evses` - Electric Vehicle Supply Equipment (connectors)
- `charging_sessions` - Time-series data for charging sessions (TimescaleDB hypertable)
- `tariffs` - Pricing information
- `ocpi_tokens` - RFID tokens for roaming
- `ocpi_cdrs` - Charge Detail Records for roaming
- `roaming_partners` - Information about roaming partners

## Running the Database

```bash
# Start the PostgreSQL database with TimescaleDB
cd infra/postgres
docker-compose up -d
```

## Running Migrations

### Using Flyway CLI

```bash
# Navigate to the migrations directory
cd libs/db-migrations

# Run migrations
flyway -configFiles=flyway.conf migrate
```

### Using Spring Boot Applications

Each microservice is configured to run Flyway migrations on startup. The migrations will be applied automatically when the services start.

## Migration Naming Convention

Flyway migration files follow this naming pattern:

```
V{version}__{description}.sql
```

For example:
- `V1__initial_schema.sql`
- `V2__add_billing_tables.sql`

## Connection Information

- **Host**: localhost
- **Port**: 5432
- **Database**: evsaas_db
- **Username**: evsaas
- **Password**: evsaas_password

## TimescaleDB

We use TimescaleDB for time-series data, particularly for charging sessions. This allows for efficient storage and querying of time-based data with better performance than standard PostgreSQL for time-series analytics.