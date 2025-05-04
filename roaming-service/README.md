# Roaming Service

## Overview
The Roaming Service is responsible for handling EV roaming functionality using the OCPI (Open Charge Point Interface) protocol. It enables interoperability between Charge Point Operators (CPOs) and eMobility Service Providers (EMSPs), allowing EV drivers to charge at stations outside their home network.

## Event-Driven Architecture
The service implements a comprehensive event-driven architecture using Apache Kafka, enabling real-time event processing and integration with other services in the platform.

### Event Types
The service produces and consumes the following event types:

#### Produced Events
1. **Location Events**
   - Created, Updated, Deleted
   - EVSE and Connector-related events

2. **Token Events**
   - Created, Updated, Revoked, Validated, Expired

3. **Roaming Partner Events**
   - Created, Updated, Deleted
   - Connection Established, Failed, Suspended, Resumed

4. **CDR (Charge Detail Record) Events**
   - Created, Updated, Sent, Received, Settled, Disputed, Corrected

#### Consumed Events
1. **Charging Session Events** - From Station Service
   - Used to generate CDRs for roaming partners

2. **Payment Events** - From Billing Service
   - Used for roaming settlement

### Error Handling
All Kafka event producers and consumers include comprehensive error handling with:
- Graceful error recovery
- Event logging
- Acknowledgment-based message processing
- Retry mechanisms for failed operations

## OCPI Modules
The service implements the following OCPI modules:

1. **Credentials Module**
   - Token management for B2B authentication
   - Secure partner registration and connection management

2. **Locations Module**
   - Location, EVSE, and connector management
   - Real-time updates via Kafka events

3. **CDR Module**
   - Charge Detail Record generation from charging sessions
   - Status tracking and updates

4. **Tokens Module** (partial implementation)
   - Token validation and authorization

## Architecture Components

### Services

1. **KafkaProducerService**
   - Handles all event publishing to Kafka topics
   - Provides helper methods to create different event types

2. **KafkaConsumerService**
   - Consumes events from other services
   - Routes events to appropriate handlers

3. **CdrService**
   - Processes charging session events
   - Generates and manages CDRs

4. **TokenService**
   - Creates, validates, and manages tokens
   - Publishes token-related events

5. **RoamingPartnerService**
   - Manages CPO and EMSP connections
   - Publishes partner-related events

## Getting Started

### Prerequisites
- Java 21
- Spring Boot 3
- Kafka cluster
- PostgreSQL database

### Configuration
Configure the service through `application.yml`:

```yaml
spring:
  kafka:
    bootstrap-servers: ${KAFKA_BOOTSTRAP_SERVERS:localhost:9092}
    consumer:
      auto-offset-reset: earliest
      group-id: roaming-service-group
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer

ocpi:
  base-path: /ocpi
  country-code: US
  party-id: EVC
  role: CPO
  external-url: https://ev-saas-platform.com
```

### Testing
The service includes unit tests for all Kafka event functionality. Run the tests with:

```bash
./mvnw test
``` 