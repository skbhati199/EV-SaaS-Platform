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

# Station Service - OCPP Implementation

## Overview

The Station Service manages EV charging stations and implements OCPP 1.6 (Open Charge Point Protocol) for communication with charging stations. This service is responsible for:

- Station registration and management
- Connector (EVSE) management
- OCPP communication via WebSocket
- Charging session management
- Heartbeat and status monitoring

## OCPP Implementation

The service implements OCPP 1.6 over WebSocket, providing the following core functionality:

### OCPP Message Types

- **BootNotification**: Handles station registration and startup
- **Heartbeat**: Monitors station connectivity
- **StatusNotification**: Tracks connector status changes
- **StartTransaction**: Initiates charging sessions
- **StopTransaction**: Concludes charging sessions
- **MeterValues**: Collects energy consumption data

### WebSocket Endpoint

The OCPP WebSocket endpoint is available at:
```
ws://{host}:{port}/ocpp/{stationId}/{ocppVersion}
```

Where:
- `{stationId}` is the unique identifier of the charging station
- `{ocppVersion}` should be "1.6" for OCPP 1.6 communication

### Station Lifecycle

1. **Connection**: Stations connect via WebSocket with the appropriate headers
2. **Registration**: Stations send BootNotification to register themselves
3. **Heartbeat**: Stations send periodic heartbeats to maintain connection
4. **Status Updates**: Stations notify about connector status changes
5. **Transactions**: Stations handle start/stop of charging sessions
6. **Metering**: Stations report energy consumption during charging

## API Endpoints

### Station Management

- `GET /api/v1/stations` - Get all stations
- `GET /api/v1/stations/{id}` - Get station by ID
- `POST /api/v1/stations` - Register a new station
- `PUT /api/v1/stations/{id}` - Update station information
- `DELETE /api/v1/stations/{id}` - Remove a station

### Connector Management

- `GET /api/v1/stations/{stationId}/connectors` - Get all connectors for a station
- `GET /api/v1/stations/{stationId}/connectors/{id}` - Get connector by ID
- `POST /api/v1/stations/{stationId}/connectors` - Add a connector to a station
- `PUT /api/v1/stations/{stationId}/connectors/{id}` - Update connector information
- `DELETE /api/v1/stations/{stationId}/connectors/{id}` - Remove a connector

### Session Management

- `GET /api/v1/stations/{stationId}/sessions` - Get all sessions for a station
- `GET /api/v1/stations/{stationId}/sessions/{id}` - Get session by ID
- `GET /api/v1/stations/{stationId}/sessions/transaction/{transactionId}` - Get session by transaction ID
- `POST /api/v1/stations/{stationId}/sessions/start` - Start a charging session
- `PUT /api/v1/stations/{stationId}/sessions/{transactionId}/stop` - Stop a charging session

## Security

The service implements role-based access control:

- **ADMIN**: Full access to all APIs
- **CPO** (Charge Point Operator): Access to manage their own stations
- **USER**: Limited access to public station information and their own sessions

## OCPP Integration Testing

To test the OCPP implementation with a simulator:

1. Use an OCPP simulator like OCPP-J-Simulator or Node-OCPP
2. Configure the simulator to connect to the WebSocket endpoint
3. Send BootNotification, Heartbeat, and other OCPP messages
4. Verify the server responses in logs or through the admin API

## Configuration

The service can be configured via the `application.properties` file, with options for:

- Database connection
- Authentication settings
- OCPP parameters
- Metrics collection intervals 