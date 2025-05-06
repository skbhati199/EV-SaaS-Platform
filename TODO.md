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
- [x] Java 17 with Spring Boot 3
- [x] Spring Cloud, Spring Security
- [x] PostgreSQL + TimescaleDB
- [x] Docker + Kubernetes setup
- [x] Kafka integration for event-driven services
- [x] Redis (cache, queue, sessions)
- [x] Keycloak (SSO/OAuth2) integration
- [ ] OCPP/OCPI protocol handling adapters completion
- [ ] Swagger/OpenAPI for API documentation
- [x] Prometheus and Grafana for monitoring and observability

### Frontend
- [x] Next.js 14 (App Router) setup
- [x] TypeScript integration
- [x] Complete Admin Dashboard UI
- [ ] User Portal Implementation
- [x] Real-time WebSocket clients
- [x] Comprehensive UI Components
- [ ] Integration with all backend services
- [x] Admin Portal Integration with Grafana


### Monitoring & Observability
- [x] Grafana integration for dashboards
- [x] Prometheus for metrics collection
- [x] Loki for log aggregation
- [x] Custom dashboards for each service
- [ ] Alerting setup for critical metrics
- [x] Admin portal integration with Grafana

---

## 🗃️ Services Status

### 1. **Auth-Service** ✅
- [x] Basic authentication
- [x] JWT implementation
- [x] Keycloak integration
- [x] 2FA implementation
- [x] Passwordless login

### 2. **Station-Service** ✅
- [x] Basic EVSE management
- [x] Station registration
- [ ] Complete OCPP server implementation
- [x] Real-time status monitoring
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

### 5. **Smart-Charging-Service** ✅
- [x] Basic service structure
- [x] Load balancing implementation
- [ ] Dynamic pricing system
- [ ] Smart grid interface
- [ ] V2G support
- [x] Kafka integration for load management events
- [x] Real-time charging power control

### 6. **Billing-Service** ✅
- [x] Basic billing structure
- [x] Session-based billing implementation
- [x] Payment gateway integration (Stripe)
- [x] Invoice generation
- [ ] Tax handling
- [x] Kafka integration for payment events
- [x] Kafka consumer for session events
- [x] Kafka producer for payment/invoice events

### 7. **Notification-Service** ✅
- [x] Basic notification structure
- [x] Kafka configuration and basic implementation
- [ ] Email notification template implementation
- [ ] SMS integration (Twilio)
- [ ] Push notifications
- [x] Consumer implementation for cross-service events
- [x] Payment event notifications
- [x] Invoice event notifications

### 8. **Admin-Portal** ✅
- [x] Basic Next.js setup
- [x] Complete dashboard implementation
- [x] Analytics integration
- [x] Real-time monitoring
- [ ] Report generation
- [x] Grafana dashboards integration
- [x] Real-time updates via WebSocket for Kafka events
- [x] System health monitoring
- [x] Logs viewer with Loki integration
- [x] Smart charging power control UI

---

## 📊 Database Implementation
- [x] PostgreSQL setup
- [x] TimescaleDB integration
- [x] Partitioned tables for charging sessions
- [x] Complete schema implementation
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
- [x] Implement Swagger/OpenAPI for all services
- [ ] Create API usage documentation
- [x] Standardize API error responses
- [x] API versioning strategy
- [ ] API testing suite

---

## 📈 Scalability Features
- [x] Microservices architecture
- [x] Load balancer implementation
- [x] Kafka integration
- [x] Redis caching
- [ ] Performance optimization

---

## 🔍 Monitoring & Observability Implementation
- [x] Grafana setup for dashboards
- [x] Prometheus for metrics collection
- [x] Loki for centralized logging
- [x] Create custom dashboards for:
  - [x] Charging station status and utilization
  - [x] Billing and revenue metrics
  - [x] User activity and growth
  - [x] System health and performance
- [ ] Set up alerting for critical metrics
- [x] Embed Grafana dashboards in Admin Portal
- [ ] Create anomaly detection for charging patterns
- [x] Implement status page for platform health

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
  - [x] SmartCharging-to-Station: Load management commands
  - [x] Station-to-SmartCharging: Telemetry events
  - [x] Station-to-Roaming: Charging session events for CDR generation
  - [x] Roaming-to-Notification: Partner connection events
- [x] Implement error handling and retry mechanisms
- [x] Add event tracking and monitoring
- [x] Setup dead-letter queues for failed events
- [x] Implement event sourcing patterns where appropriate
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
2. ✅ Finish Admin Portal UI
3. ✅ Implement Smart Charging Kafka integration:
   - ✅ Create event schema and DTOs
   - ✅ Implement event producers for load management
   - ✅ Implement event consumers for station telemetry
   - ✅ Connect to Station service for control commands
4. ✅ Implement payment integration with external providers (Stripe)
5. ✅ Implement SMS notifications with Twilio
6. Implement comprehensive testing suite
7. ✅ Create Grafana dashboards for all services
8. ✅ Integrate Grafana with Admin Portal
9. ✅ Continue Kafka event-driven architecture implementation:
   - [x] User service events (complete)
   - [x] Roaming service events (complete)
   - [x] SmartCharging service events (complete)
10. Complete OCPI protocol implementation
11. ✅ Enhance WebSocket-based real-time event handling in Admin Portal

## Smart Charging Implementation Plan
The next focus area is implementing the Smart Charging service with Kafka integration:

1. **Event Schema Design** ✅
   - ✅ Define LoadManagementEvent schema
   - ✅ Define ChargingProfileEvent schema
   - ✅ Define GridStatusEvent schema

2. **Event Producers** ✅
   - ✅ Create KafkaProducerService for Smart Charging
   - ✅ Implement load management command production
   - ✅ Implement charging profile update events

3. **Event Consumers** ✅
   - ✅ Implement telemetry data consumers from stations
   - ✅ Implement power availability events from grid
   - ✅ Setup processing logic for dynamic load adjustment

4. **Smart Charging Algorithms** ✅
   - ✅ Implement fair allocation algorithm
   - ✅ Implement peak shaving capability
   - ✅ Implement grid-responsive charging

5. **Station Service Integration** ✅
   - ✅ Integrate with Station service via Kafka for bidirectional communication
   - ✅ Implement command pattern for control operations
   - ✅ Create WebSocket-based monitoring for admin UI

## Kafka Events Implementation - Next Steps
After successfully implementing Kafka events for User service and Roaming service, the next steps are:

- ✅ Implement Smart Charging event producers and consumers
- ✅ Complete WebSocket integration for real-time monitoring
- [ ] Add event archiving for long-term analytics
- [x] Implement additional testing and monitoring for Kafka health
- [ ] Create admin tools for event debugging and replay

---

## Project Structure Status ✅
ev-saas-platform/
├── admin-portal/         # Complete ✅
├── api-gateway/          # Complete ✅
├── auth-service/         # Complete ✅
├── user-service/         # Complete ✅
├── station-service/      # Complete ✅
├── roaming-service/      # Complete ✅
├── smart-charging/       # Complete ✅
├── billing-service/      # Complete ✅
├── notification-service/ # Complete ✅
└── scheduler-service/    # Complete ✅

## Next Steps
1. ✅ Complete remaining UI components in admin-portal
2. Extend Kafka event-driven architecture to remaining services:
   - [x] User service event producers and consumers
   - [x] Roaming service event integration
   - [x] Smart charging event-based control
3. Extend Redis caching to other services:
   - [x] Implement Redis caching in station-service for status caching
   - [x] Add Redis caching to user-service for profile data
   - [x] Set up Redis caching in billing-service for tariff plans
   - [x] Implement distributed rate limiting with Redis
4. ✅ Complete smart charging algorithms
5. Implement comprehensive testing suite
6. ✅ Deploy monitoring and logging infrastructure
7. Complete security implementations
8. Perform load testing and optimization
9. ✅ Create and configure Prometheus and Grafana dashboards
10. ✅ Integrate Grafana visualizations into Admin Portal UI
11. Set up alerting for critical system metrics
12. ✅ Enhance WebSocket support in Admin Portal with additional features

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
- [x] Smart charging control events
- [x] Roaming service events (locations, tokens, partners, CDRs)
- [x] Acknowledgment-based message processing with retry logic
- [x] Event-driven OCPI implementation for roaming features

## Real-time Charging Power Control Implementation ✅
The real-time charging power control feature is now implemented with the following components:
- [x] Basic architecture design with PowerProfileService and SmartChargingService interfaces
- [x] Kafka configuration for power distribution events and charging profile events
- [x] Session power adjustment API in SmartChargingService
- [x] Implementation of PowerDistributionEvent producers to send control commands
- [x] REST API endpoints for manual and emergency power control
- [x] Integration with Station service to apply power limits
- [x] Real-time monitoring of power allocation through WebSocket for Admin UI
- [x] Implementation of dynamic power adjustment algorithms based on:
  - [x] Time-of-use pricing
  - [x] Grid load constraints
  - [x] User preferences and priority levels
  - [x] Vehicle charging capabilities
- [x] Testing framework for power control commands
- [x] Safety mechanisms to prevent overloading circuits

## Smart Charging Event Flow Implementation ✅
The Kafka-based smart charging event flow has been implemented with the following components:
- [x] PowerDistributionEvent DTO for standardized event structure
- [x] KafkaProducerService interface and implementation for sending control events
- [x] Integration with SmartChargingService for automatic power adjustment during session changes
- [x] Emergency power reduction capabilities for quick response to grid or site issues
- [x] Group-wide power reduction for managing multiple stations simultaneously
- [x] Consumer implementation in Station service
- [x] OCPP SetChargingProfile implementation to apply power limits to stations via OCPP
- [x] WebSocket integration for real-time UI updates
- [x] Telemetry event consumers for dynamic response to changing conditions

## Station Service Power Control Implementation ✅
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

## Admin UI Integration for Power Control ✅
The Admin Portal now has real-time power control monitoring:
- [x] Subscribe to WebSocket power control notifications in Admin Portal
- [x] Create a real-time power control dashboard component
- [x] Display active power limits with visual indicators for:
  - [x] Current power limits on stations and connectors
  - [x] Temporary vs. persistent limits with expiration timers
  - [x] Different limit types (emergency, scheduled, load balancing)
- [x] Provide manual control interface for operators to:
  - [x] Set new power limits on stations or connectors
  - [x] Clear existing power limits
  - [x] Enable emergency power reduction
- [x] Implement graphical visualization of power usage across stations
- [x] Add alerts for power-related issues
- [x] Create historical view of power control events

## Future Smart Charging Features
1. Implement advanced features for the Smart Charging service:
   - [ ] Implement V2G (Vehicle-to-Grid) capabilities
   - [ ] Add predictive load balancing using machine learning
   - [ ] Implement integration with energy markets for real-time pricing
   - [ ] Create advanced scheduling algorithms for fleet charging
   - [ ] Develop grid service capabilities for demand response