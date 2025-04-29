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
- [ ] OCPP 1.6/2.0.1 (WebSocket)
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
- [ ] Set up mono repo with TurboRepo
- [ ] Setup PostgreSQL DB and schema migration (Flyway)
- [ ] Implement Auth + EVSE registration
- [ ] Integrate OCPP 1.6 backend
- [ ] Basic Admin UI (Next.js 14)

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
│   ├── api-gateway/          # Spring Cloud Gateway or Zuul
│   ├── auth-service/         # Keycloak or Spring Security Auth Service
│   ├── user-service/         # User management microservice
│   ├── station-service/      # Charging Station + OCPP Microservice
│   ├── roaming-service/      # OCPI API and peer-to-peer roaming
│   ├── smart-charging/       # Demand response and smart charging
│   ├── billing-service/      # Billing, CDRs, tariffs, Stripe/UPI etc.
│   ├── notification-service/ # Kafka-based email/sms/push notification
│   └── scheduler-service/    # Cron jobs, V2G, background tasks
│
├── libs/
│   ├── common-utils/         # Shared Java/TS utility code
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

---

## 🗃️ **High-Level DB Schema Overview**

### 🧍 `users`
Manages EV users, CPOs, and Admins
```sql
CREATE TABLE users (
  id UUID PRIMARY KEY,
  email TEXT UNIQUE NOT NULL,
  password_hash TEXT,
  role TEXT CHECK (role IN ('admin', 'user', 'cpo', 'emsp')),
  phone TEXT,
  created_at TIMESTAMP DEFAULT NOW()
);
```

---

### 🔌 `charging_stations`
Each station has one or more connectors (EVSE)
```sql
CREATE TABLE charging_stations (
  id UUID PRIMARY KEY,
  cpo_id UUID REFERENCES users(id),
  name TEXT,
  location GEOGRAPHY(POINT, 4326),
  address TEXT,
  ocpp_version TEXT,
  is_active BOOLEAN DEFAULT TRUE,
  created_at TIMESTAMP DEFAULT NOW()
);
```

---

### 🔋 `evses`
Connectors per station (socket level)
```sql
CREATE TABLE evses (
  id UUID PRIMARY KEY,
  station_id UUID REFERENCES charging_stations(id),
  connector_type TEXT,
  power_kw DECIMAL,
  status TEXT,
  ocpp_connector_id INT,
  is_available BOOLEAN,
  last_heartbeat TIMESTAMP
);
```

---

### ⚡ `charging_sessions` (TimescaleDB)
Time-series for each charging event
```sql
CREATE TABLE charging_sessions (
  id UUID PRIMARY KEY,
  evse_id UUID REFERENCES evses(id),
  user_id UUID REFERENCES users(id),
  start_time TIMESTAMPTZ,
  end_time TIMESTAMPTZ,
  energy_kwh DECIMAL,
  cost DECIMAL,
  status TEXT,
  created_at TIMESTAMP DEFAULT NOW()
);
-- TimescaleDB hypertable for time-series
SELECT create_hypertable('charging_sessions', 'start_time');
```

---

### 💸 `tariffs`
Used for billing and OCPI tariff exchange
```sql
CREATE TABLE tariffs (
  id UUID PRIMARY KEY,
  cpo_id UUID REFERENCES users(id),
  name TEXT,
  currency TEXT,
  base_price DECIMAL,
  price_per_kwh DECIMAL,
  price_per_minute DECIMAL,
  created_at TIMESTAMP DEFAULT NOW()
);
```

---

### 🌍 `ocpi_tokens`, `ocpi_cdrs`, `roaming_partners`
Support for OCPI roaming
```sql
CREATE TABLE ocpi_tokens (
  token TEXT PRIMARY KEY,
  user_id UUID REFERENCES users(id),
  issuer TEXT,
  valid BOOLEAN,
  type TEXT
);

CREATE TABLE ocpi_cdrs (
  id UUID PRIMARY KEY,
  session_id UUID REFERENCES charging_sessions(id),
  cpo_id UUID,
  emsp_id UUID,
  total_cost DECIMAL,
  currency TEXT,
  status TEXT,
  created_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE roaming_partners (
  id UUID PRIMARY KEY,
  name TEXT,
  ocpi_endpoint TEXT,
  token TEXT,
  active BOOLEAN DEFAULT TRUE
);
```
