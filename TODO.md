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

### Backend (Microservices - Java)
- Java 21 with Spring Boot 3
- Spring Cloud, Spring Security
- Kafka or RabbitMQ for event-driven services
- PostgreSQL + TimescaleDB
- Redis (cache, queue, sessions)
- Docker + Kubernetes
- Keycloak (SSO/OAuth2)
- OCPP/OCPI protocol handling adapters

### Frontend
- React with Next.js 14 (App Router)
- Tailwind CSS
- TypeScript, Zustand or Redux
- REST & WebSocket clients
- Admin panel & user portal

---

## 🗃️ Services Breakdown (Microservices)

### 1. **Auth-Service**
- JWT & OAuth2 with Keycloak
- Roles: Admin, CPO, EMSP, User
- 2FA, passwordless login

### 2. **Charging-Station-Service**
- Register/manage EVSEs
- OCPP server support
- Status, heartbeats, transaction logs

### 3. **Roaming-Service**
- OCPI integration (peer-to-peer roaming)
- Location, tariffs, tokens, CDRs

### 4. **User-Service**
- Profile, RFID tokens, wallets
- Charging history

### 5. **Smart-Charging-Service**
- Load balancing
- Dynamic pricing
- Smart grid interface (OpenADR)

### 6. **Billing-Service**
- Session-based billing
- Invoice, tax, discount handling
- Stripe, Razorpay integration

### 7. **Notification-Service**
- Email/SMS/Push alerts
- Kafka-based event handling

### 8. **Admin-Console**
- Admin dashboard (React + Next.js)
- Role-based UI
- Analytics & reports

---

## 📊 Database Schema (PostgreSQL)
- Use TimescaleDB for session logs
- Partitioned tables for charging sessions
- Entity: User, Station, EVSE, Session, Tariff, Token, CPO, eMSP

---

## 🔌 Protocol Support
- [x] OCPP 1.6/2.0.1 (WebSocket)
- [ ] OCPI 2.2 (REST-based P2P)
- [ ] OpenADR (for grid communication)
- [ ] ISO 15118 (V2G/Plug & Charge - future scope)

---

## 🔐 Security
- Role-based access control (RBAC)
- TLS for OCPP/OCPI endpoints
- OWASP secure headers in frontend/backend
- Database encryption at rest

---

## 📈 Scalability
- Horizontal scaling of microservices
- Load-balanced OCPP socket handlers
- Async processing with Kafka
- Caching (Redis) and DB indexing for performance

---

## 📅 Milestones

### Phase 1: Core Platform
- [x] Set up mono repo with TurboRepo
- [x] Setup PostgreSQL DB and schema migration (Flyway)
- [x] Implement Auth + EVSE registration
- [x] Integrate OCPP 1.6 backend
- [~] Basic Admin UI (Next.js 14)

### Phase 2: Protocol & Roaming
- [ ] OCPI implementation
- [ ] Roaming station listing
- [ ] Tariff and CDR exchange

### Phase 3: Smart Charging & Grid
- [ ] Real-time power balancing logic
- [ ] Smart grid interface
- [ ] V2G scheduling framework

---

ev-saas-platform/
│
├── ├── admin-portal/         # Next.js 14 frontend (React)
│   ├── api-gateway/          # Spring Cloud Gateway or Zuul ✅
│   ├── auth-service/         # Keycloak or Spring Security Auth Service ✅
│   ├── user-service/         # User management microservice ✅
│   ├── station-service/      # Charging Station + OCPP Microservice ✅
│   ├── roaming-service/      # OCPI API and peer-to-peer roaming ✅
│   ├── smart-charging/       # Demand response and smart charging ✅
│   ├── billing-service/      # Billing, CDRs, tariffs, Stripe/UPI etc. ✅
│   ├── notification-service/ # Kafka-based email/sms/push notification ✅
│   └── scheduler-service/    # Cron jobs, V2G, background tasks ✅
│
├── libs/
│   ├── common-utils/         # Shared Java/TS utility code ✅
│   ├── db-migrations/        # Flyway or Liquibase scripts
│   ├── ocpp-adapter/         # OCPP WS adapter
│   ├── ocpi-adapter/         # OCPI REST adapter
│   ├── protobuf/             # Shared gRPC/Proto contracts (optional)
│   └── types/                # Shared TypeScript types/interfaces
│
├── infra/
│   ├── k8s/                  # Kubernetes manifests, Helm charts
│   ├── docker/               # Dockerfiles for all services
│   ├── postgres/             # PG init scripts, TimescaleDB setup
│   └── monitoring/           # Prometheus, Grafana, Loki, Alertmanager
│
├── .env                     # Environment variables
├── docker-compose.yml       # Local orchestration
├── turbo.json               # Monorepo config (for TurboRepo)
├── README.md
└── TODO.md

Great! Here's a **high-level database schema** for the **EV SaaS Platform** using **PostgreSQL**, with high scalability in mind (e.g., TimescaleDB for time-series data like charging sessions). It's designed for modularity across microservices like users, stations, billing, roaming, etc.

