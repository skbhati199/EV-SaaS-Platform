# EV SaaS Platform for EVSE with OCPP/OCPI

## 🚀 Project Overview
Build a scalable and modular Electric Vehicle SaaS platform to manage EVSE infrastructure, supporting:
- OCPP 1.6/2.0.1
- OCPI 2.2
- Smart Charging, V2G readiness
- Roaming, pricing, billing
- Grid integration features
- Admin and user portals

---

## 📦 Tech Stack

### Backend (Microservices - Java) ✅
- [x] Java 21 with Spring Boot 3
- [x] Spring Cloud, Spring Security
- [x] PostgreSQL + TimescaleDB
- [x] Docker + Kubernetes setup
- [x] Kafka integration for event-driven services
- [x] Redis (cache, queue, sessions)
- [ ] Keycloak (SSO/OAuth2) integration
- [ ] OCPP/OCPI protocol handling adapters completion

### Frontend
- [x] Next.js 14 (App Router) setup
- [x] TypeScript integration
- [ ] Complete Admin Dashboard UI
- [ ] User Portal Implementation
- [x] Real-time WebSocket clients
- [ ] Comprehensive UI Components
- [x] Integration with all backend services

### Monitoring & Observability
- [x] Grafana integration for dashboards
- [x] Prometheus for metrics collection
- [x] Loki for log aggregation
- [ ] Custom dashboards for each service
- [ ] Alerting setup for critical metrics
- [ ] Admin portal integration with Grafana

---

## 🗃️ Services Status

### 1. **Auth-Service** ✅
- [x] Basic authentication
- [x] JWT implementation
- [ ] Keycloak integration
- [ ] 2FA implementation
- [ ] Passwordless login

### 2. **Station-Service** ✅
- [x] Basic EVSE management
- [x] Station registration
- [ ] Complete OCPP server implementation
- [ ] Real-time status monitoring
- [x] Transaction logging system
- [x] Kafka integration for station events
- [x] Session events Kafka producers

### 3. **Roaming-Service** ✅
- [x] Basic OCPI structure
- [x] Location handling
- [ ] Complete peer-to-peer roaming
- [x] CDR handling
- [x] Token management
- [x] Kafka integration for roaming events

### 4. **User-Service** ✅
- [x] Basic user management
- [x] Profile handling
- [ ] RFID token management
- [ ] Wallet implementation
- [ ] Charging history tracking
- [x] Kafka integration for user events

### 5. **Smart-Charging-Service** ⚠️
- [x] Basic service structure
- [ ] Load balancing implementation
- [ ] Dynamic pricing system
- [ ] Smart grid interface
- [ ] V2G support
- [ ] Kafka integration for load management events
- [x] Real-time charging power control (in progress)

### 6. **Billing-Service** ✅
- [x] Basic billing structure
- [x] Session-based billing implementation
- [ ] Payment gateway integration
- [x] Invoice generation
- [ ] Tax handling
- [x] Kafka integration for payment events
- [x] Kafka consumer for session events
- [x] Kafka producer for payment/invoice events

### 7. **Notification-Service** ✅
- [x] Basic notification structure
- [x] Kafka configuration and basic implementation
- [x] Email notification template implementation
- [ ] SMS integration
- [ ] Push notifications
- [x] Consumer implementation for cross-service events
- [x] Payment event notifications
- [x] Invoice event notifications

### 8. **Admin-Portal** ⚠️
- [x] Basic Next.js setup
- [ ] Complete dashboard implementation
- [ ] Analytics integration
- [ ] Real-time monitoring
- [ ] Report generation
- [ ] Grafana dashboards integration
- [x] Real-time updates via WebSocket for Kafka events

---

## 📊 Database Implementation
- [x] PostgreSQL setup
- [ ] TimescaleDB integration
- [ ] Partitioned tables for charging sessions
- [ ] Complete schema implementation
- [ ] Performance optimization

---

## 🔌 Protocol Support
- [ ] OCPP 1.6 (80% complete)
- [ ] OCPP 2.0.1 (pending)
- [ ] OCPI 2.2 (50% complete)
- [ ] OpenADR (pending)
- [ ] ISO 15118 (future scope)

---

## 🔐 Security Implementation
- [x] Basic RBAC
- [ ] Complete TLS implementation
- [ ] OWASP security headers
- [ ] Database encryption
- [ ] Security audit

---

## 📑 API Documentation
- [ ] Implement Swagger/OpenAPI for all services
- [ ] Create API usage documentation
- [ ] Standardize API error responses
- [ ] API versioning strategy
- [ ] API testing suite

---

## 📈 Scalability Features
- [x] Microservices architecture
- [ ] Load balancer implementation
- [x] Kafka integration
- [x] Redis caching
- [ ] Performance optimization

---

## 🔍 Monitoring & Observability Implementation
- [x] Grafana setup for dashboards
- [x] Prometheus for metrics collection
- [x] Loki for centralized logging
- [ ] Create custom dashboards for:
  - [ ] Charging station status and utilization
  - [ ] Billing and revenue metrics
  - [ ] User activity and growth
  - [ ] System health and performance
- [ ] Set up alerting for critical metrics
- [ ] Embed Grafana dashboards in Admin Portal
- [ ] Create anomaly detection for charging patterns
- [ ] Implement status page for platform health

---

## 🔄 Kafka Event-Driven Architecture Implementation
- [x] Kafka and Zookeeper infrastructure setup
- [x] Notification service Kafka configuration and topics
- [x] Basic producer and consumer in Notification service
- [x] Define system-wide event schema and standards
- [x] Implement cross-service event communication:
  - [x] Station-to-Billing: Charging session events
  - [x] Billing-to-Notification: Payment events
  - [x] Billing-to-Notification: Invoice events
  - [x] User-to-Notification: User activity events
  - [ ] SmartCharging-to-Station: Load management commands
  - [ ] Station-to-SmartCharging: Telemetry events
  - [x] Station-to-Roaming: Charging session events for CDR generation
  - [x] Roaming-to-Notification: Partner connection events
- [x] Implement error handling and retry mechanisms
- [x] Add event tracking and monitoring
- [x] Setup dead-letter queues for failed events
- [ ] Implement event sourcing patterns where appropriate
- [x] Add schema validation for events
- [x] Create email templates for event notifications
- [x] Add real-time update support via WebSockets for UI

## OCPI Protocol Implementation Progress
- [x] Basic OCPI infrastructure setup
- [x] Credentials module implementation with token management
- [x] Locations module with event integration
- [x] CDR generation from charging sessions
- [ ] Tokens module for EV driver authorization (50% complete)
- [ ] Sessions module (pending)
- [ ] Tariffs module (pending)
- [ ] Commands module (pending)
- [x] Event-driven architecture for OCPI modules

## Current Focus Areas (Priority Tasks)
1. Complete OCPP implementation
2. Finish Admin Portal UI
3. Implement Smart Charging Kafka integration:
   - Create event schema and DTOs
   - Implement event producers for load management
   - Implement event consumers for station telemetry
   - Connect to Station service for control commands
4. Implement payment integration with external providers
5. Complete user portal development
6. Implement comprehensive testing suite
7. Create Grafana dashboards for all services
8. Integrate Grafana with Admin Portal
9. Continue Kafka event-driven architecture implementation:
   - [x] User service events (complete)
   - [x] Roaming service events (complete)
   - [ ] SmartCharging service events (pending)
10. Complete OCPI protocol implementation
11. Enhance WebSocket-based real-time event handling in Admin Portal

## Smart Charging Implementation Plan
The next focus area is implementing the Smart Charging service with Kafka integration:

1. **Event Schema Design**
   - Define LoadManagementEvent schema
   - Define ChargingProfileEvent schema
   - Define GridStatusEvent schema

2. **Event Producers**
   - Create KafkaProducerService for Smart Charging
   - Implement load management command production
   - Implement charging profile update events

3. **Event Consumers**
   - Implement telemetry data consumers from stations
   - Implement power availability events from grid
   - Setup processing logic for dynamic load adjustment

4. **Smart Charging Algorithms**
   - Implement fair allocation algorithm
   - Implement peak shaving capability
   - Implement grid-responsive charging

5. **Station Service Integration**
   - Integrate with Station service via Kafka for bidirectional communication
   - Implement command pattern for control operations
   - Create WebSocket-based monitoring for admin UI

## Kafka Events Implementation - Next Steps
After successfully implementing Kafka events for User service and Roaming service, the next steps are:

- Implement Smart Charging event producers and consumers
- Complete WebSocket integration for real-time monitoring
- Add event archiving for long-term analytics
- Implement additional testing and monitoring for Kafka health
- Create admin tools for event debugging and replay

---

## Project Structure Status ✅
ev-saas-platform/
├── admin-portal/         # In Progress
├── api-gateway/          # Complete ✅
├── auth-service/         # Complete ✅
├── user-service/         # Complete ✅
├── station-service/      # Complete ✅
├── roaming-service/      # Complete ✅
├── smart-charging/       # In Progress ⚠️
├── billing-service/      # Complete ✅
├── notification-service/ # Complete ✅
└── scheduler-service/    # Complete ✅

## Next Steps
1. Complete remaining UI components in admin-portal
2. Extend Kafka event-driven architecture to remaining services:
   - [x] User service event producers and consumers
   - [x] Roaming service event integration
   - [ ] Smart charging event-based control
3. Extend Redis caching to other services:
   - Implement Redis caching in station-service for status caching
   - Add Redis caching to user-service for profile data
   - Set up Redis caching in billing-service for tariff plans
   - Implement distributed rate limiting with Redis
4. Complete smart charging algorithms
5. Implement comprehensive testing suite
6. Deploy monitoring and logging infrastructure
7. Complete security implementations
8. Perform load testing and optimization
9. Create and configure Prometheus and Grafana dashboards
10. Integrate Grafana visualizations into Admin Portal UI
11. Set up alerting for critical system metrics
12. Enhance WebSocket support in Admin Portal with additional features

## Redis Caching Implementation ✅
Redis caching has been fully implemented in the API Gateway with the following features:
- [x] JWT token caching to reduce authentication overhead
- [x] Route definition caching for faster API routing
- [x] Response caching for frequently accessed endpoints
- [x] Cache statistics monitoring and reporting
- [x] Admin endpoints for cache management
- [x] Configurable TTLs for different cache types
- [x] Cache invalidation mechanisms
- [x] Health monitoring for Redis
- [x] Route-specific cache configurations

## Kafka Events Implementation ✅
The platform now features a comprehensive event-driven architecture using Apache Kafka with the following components:
- [x] User service events (user creation, updates, account status changes)
- [x] Wallet events (deposits, withdrawals, payments)
- [x] Notification service event consumers
- [x] Email notifications triggered by events
- [x] Billing service payment and invoice events
- [x] Station service charging session events
- [ ] Smart charging control events (pending)
- [x] Roaming service events (locations, tokens, partners, CDRs)
- [x] Acknowledgment-based message processing with retry logic
- [x] Event-driven OCPI implementation for roaming features

## Real-time Charging Power Control Implementation
The real-time charging power control feature is now being implemented with the following components:
- [x] Basic architecture design with PowerProfileService and SmartChargingService interfaces
- [x] Kafka configuration for power distribution events and charging profile events
- [x] Session power adjustment API in SmartChargingService
- [x] Implementation of PowerDistributionEvent producers to send control commands
- [x] REST API endpoints for manual and emergency power control
- [x] Integration with Station service to apply power limits
- [x] Real-time monitoring of power allocation through WebSocket for Admin UI
- [ ] Implementation of dynamic power adjustment algorithms based on:
  - [ ] Time-of-use pricing
  - [ ] Grid load constraints
  - [ ] User preferences and priority levels
  - [ ] Vehicle charging capabilities
- [ ] Testing framework for power control commands
- [ ] Safety mechanisms to prevent overloading circuits

## Smart Charging Event Flow Implementation
The Kafka-based smart charging event flow has been implemented with the following components:
- [x] PowerDistributionEvent DTO for standardized event structure
- [x] KafkaProducerService interface and implementation for sending control events
- [x] Integration with SmartChargingService for automatic power adjustment during session changes
- [x] Emergency power reduction capabilities for quick response to grid or site issues
- [x] Group-wide power reduction for managing multiple stations simultaneously
- [x] Consumer implementation in Station service
- [x] OCPP SetChargingProfile implementation to apply power limits to stations via OCPP
- [x] WebSocket integration for real-time UI updates
- [ ] Telemetry event consumers for dynamic response to changing conditions (pending)

## Station Service Power Control Implementation
The station service now includes the following power control components:
- [x] Kafka consumer configuration to receive power distribution events
- [x] PowerDistributionEvent consumer implementation
- [x] PowerControlService for processing power commands
- [x] OCPP SetChargingProfile request and response implementation
- [x] WebSocket handler updates to support bidirectional communication
- [x] Dynamic charging profile creation based on smart charging commands
- [x] Temporary vs. persistent power limit handling
- [x] Profile expiration management for temporary limits
- [x] WebSocket notification for Admin UI when power limits are applied

## Admin UI Integration for Power Control
The next step is to integrate real-time power control monitoring in the Admin Portal:
- [ ] Subscribe to WebSocket power control notifications in Admin Portal
- [ ] Create a real-time power control dashboard component
- [ ] Display active power limits with visual indicators for:
  - [ ] Current power limits on stations and connectors
  - [ ] Temporary vs. persistent limits with expiration timers
  - [ ] Different limit types (emergency, scheduled, load balancing)
- [ ] Provide manual control interface for operators to:
  - [ ] Set new power limits on stations or connectors
  - [ ] Clear existing power limits
  - [ ] Enable emergency power reduction
- [ ] Implement graphical visualization of power usage across stations
- [ ] Add alerts for power-related issues
- [ ] Create historical view of power control events

## Next Steps for Smart Charging Feature
1. Implement telemetry event consumers for dynamic response to changing conditions:
   - [ ] Create TelemetryEvent DTO in Station service for reporting metrics
   - [ ] Implement telemetry event producer in Station service
   - [ ] Create consumer in Smart Charging service to process telemetry
   - [ ] Develop dynamic adjustment algorithms using real-time data
   
2. Enhance testing framework for power control:
   - [ ] Create integration tests for SmartChargingService
   - [ ] Implement simulator for OCPP stations to test power limits
   - [ ] Add load testing for high-volume event processing
   - [ ] Create a test dashboard for monitoring power control functionality
   
3. Add safety mechanisms to prevent circuit overloading:
   - [ ] Implement circuit breaker pattern for power limits
   - [ ] Add max amperage constraints based on physical infrastructure
   - [ ] Create automatic failsafe mechanism for dangerous conditions
   - [ ] Implement multi-level approvals for high-risk power changes