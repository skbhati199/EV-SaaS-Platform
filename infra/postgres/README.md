# PostgreSQL and TimescaleDB Management

This directory contains initialization scripts and configuration for the PostgreSQL and TimescaleDB databases used in the EV SaaS Platform.

## Database Structure

The platform uses two database instances:

1. **PostgreSQL (Standard)**: For user data, authentication, and general application data.
   - Host: `postgres`
   - Port: `5432`
   - Database: `evsaas_db`
   - Username: `evsaas`
   - Password: `evsaas_password`

2. **TimescaleDB**: For time-series data like charging sessions, telemetry data, and metrics.
   - Host: `timescaledb`
   - Port: `5432`
   - Database: `evsaas_timeseries_db`
   - Username: `evsaas`
   - Password: `evsaas_password`

## pgAdmin Access

pgAdmin is included in the Docker Compose setup for easy database management:

1. Access pgAdmin in your browser at: http://localhost:5050
2. Login with:
   - Email: `admin@evplatform.com`
   - Password: `admin`

### Setting up Database Connections in pgAdmin

1. Right-click on "Servers" and select "Create" -> "Server..."
2. In the "General" tab, give it a name (e.g., "EV Platform PostgreSQL")
3. In the "Connection" tab, enter:
   - Host: `postgres` (for PostgreSQL) or `timescaledb` (for TimescaleDB)
   - Port: `5432`
   - Maintenance Database: `evsaas_db` or `evsaas_timeseries_db`
   - Username: `evsaas`
   - Password: `evsaas_password`
4. Click "Save"

## TimescaleDB UI

The TimescaleDB Admin UI provides management and monitoring tools specific to TimescaleDB:

1. Access the UI in your browser at: http://localhost:8091
2. The connection to the database is pre-configured

## Important Tables

### Station Service Tables (in TimescaleDB)

The station service uses TimescaleDB for time-series data storage:

- `charging_stations`: Information about charging stations
- `connectors`: Details about connectors (EVSEs) for each station
- `charging_sessions`: Records of charging sessions
- `meter_values`: Time-series data of meter readings during sessions

### Schema Initialization

The database schema is created using the initialization scripts in:
- `init/postgres/`: Scripts for the standard PostgreSQL database
- `init/timescaledb/`: Scripts for the TimescaleDB database

## Manual Database Operations

### Connecting via Command Line

To connect to PostgreSQL:
```bash
docker exec -it ev-saas-postgres psql -U evsaas -d evsaas_db
```

To connect to TimescaleDB:
```bash
docker exec -it ev-saas-timescaledb psql -U evsaas -d evsaas_timeseries_db
```

### Useful SQL Commands

- List all tables: `\dt`
- Describe a table: `\d table_name`
- Show all charging stations: `SELECT * FROM charging_stations;`
- Show all connectors: `SELECT * FROM connectors;`
- Show active charging sessions: `SELECT * FROM charging_sessions WHERE status = 'ACTIVE';`

## Troubleshooting

1. **Connection Issues**:
   - Ensure the database containers are running: `docker ps | grep postgres`
   - Check logs: `docker logs ev-saas-postgres` or `docker logs ev-saas-timescaledb`

2. **Data Persistence**:
   - Data is stored in Docker volumes: `postgres_data` and `timescaledb_data`
   - To reset data, remove these volumes: `docker-compose down -v` (caution: this deletes all data)

3. **pgAdmin Issues**:
   - If pgAdmin can't connect, ensure you're using the Docker network hostnames (`postgres`, `timescaledb`)
   - Check pgAdmin logs: `docker logs ev-saas-pgadmin` 