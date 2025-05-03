# EV Station Service

This service manages charging stations, connectors, and charging sessions for the EV Platform.

## Features

- CRUD operations for charging stations
- Management of charging station connectors (EVSEs)
- Tracking of charging sessions
- Station heartbeat monitoring
- Integration with OCPP for charging station communication
- Geolocation-based station search

## Architecture

The Station Service follows a layered architecture:

- **Controller Layer**: REST endpoints for clients to interact with the service
- **Service Layer**: Business logic implementation
- **Repository Layer**: Data access using Spring Data JPA
- **Model Layer**: Entity definitions and database mapping

## API Endpoints

### Stations

- `GET /api/v1/stations` - Get all stations
- `GET /api/v1/stations/{id}` - Get station by ID
- `GET /api/v1/stations/serial/{serialNumber}` - Get station by serial number
- `GET /api/v1/stations/status/{status}` - Get stations by status
- `GET /api/v1/stations/cpo/{cpoId}` - Get stations by CPO ID
- `GET /api/v1/stations/nearby` - Find stations near a location
- `POST /api/v1/stations` - Create a new station
- `PUT /api/v1/stations/{id}` - Update a station
- `DELETE /api/v1/stations/{id}` - Delete a station
- `POST /api/v1/stations/{id}/heartbeat` - Process station heartbeat
- `PUT /api/v1/stations/{id}/status` - Update station status

### Connectors

- `GET /api/v1/stations/{stationId}/connectors` - Get all connectors for a station
- `GET /api/v1/stations/{stationId}/connectors/{connectorId}` - Get connector by ID
- `GET /api/v1/stations/{stationId}/connectors/status/{status}` - Get connectors by status
- `POST /api/v1/stations/{stationId}/connectors` - Create a new connector
- `PUT /api/v1/stations/{stationId}/connectors/{id}` - Update a connector
- `DELETE /api/v1/stations/{stationId}/connectors/{id}` - Delete a connector
- `PUT /api/v1/stations/{stationId}/connectors/{id}/status` - Update connector status

### Charging Sessions

- `GET /api/v1/stations/{stationId}/sessions` - Get all sessions for a station
- `GET /api/v1/stations/{stationId}/sessions/{id}` - Get session by ID
- `GET /api/v1/stations/{stationId}/sessions/transaction/{transactionId}` - Get session by transaction ID
- `POST /api/v1/stations/{stationId}/sessions/start` - Start a charging session
- `PUT /api/v1/stations/{stationId}/sessions/{transactionId}/stop` - Stop a charging session

## Database Schema

The service uses PostgreSQL with the following main tables:

- `charging_stations` - Stores station information
- `connectors` - Stores connector/EVSE information
- `charging_sessions` - Stores charging session data
- `station_heartbeats` - Stores heartbeat history
- `station_metrics` - Stores station-level metrics
- `connector_metrics` - Stores connector-level metrics

## Security

The service implements role-based access control:

- **ADMIN**: Full access to all APIs
- **CPO** (Charge Point Operator): Access to manage their own stations
- **USER**: Limited access to public station information and their own sessions

## OCPP Integration

The service implements a WebSocket endpoint for OCPP communication with charging stations. It supports:

- Station registration
- Heartbeats
- Status notifications
- Transaction management
- Meter values
- Remote operation (start/stop/reset)

## Configuration

The service can be configured via the `application.properties` file, with options for:

- Database connection
- Authentication settings
- OCPP parameters
- Metrics collection intervals 