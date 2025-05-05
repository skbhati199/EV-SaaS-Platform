# EV SaaS Platform

domain: chargingev.app

A comprehensive Electric Vehicle Charging Station Management Platform built with a microservices architecture using TurboRepo for monorepo management.

## Project Structure

This project is set up as a monorepo using TurboRepo, with the following structure:

```
.
├── admin-portal/         # Next.js 14 Admin UI
├── api-gateway/          # API Gateway service (Spring Boot)
├── eureka-server/        # Eureka Server service (Spring Boot)
├── auth-service/         # Authentication service (Spring Boot)
├── billing-service/      # Billing management service (Spring Boot)
├── infra/                # Infrastructure configuration
│   ├── docker/           # Docker configuration
│   │   └── nginx/        # Nginx API Gateway configuration (alternative to Spring Cloud Gateway)
├── notification-service/ # Notification handling service (Spring Boot)
├── packages/             # Shared packages and utilities
├── libs/                 # Shared packages and utilities
├── roaming-service/      # OCPI implementation service (Spring Boot)
├── scheduler-service/    # Scheduling service (Spring Boot)
├── smart-charging/       # Smart charging algorithms (Spring Boot)
├── station-service/      # OCPP station management service (Spring Boot)
└── user-service/         # User management service (Spring Boot)
```

## Getting Started

### Prerequisites

- Node.js (v18 or later)
- Yarn (v1.22.19 or later)
- Java 17
- Docker and Docker Compose
- PostgreSQL
- Redis
- Kafka microservices
- TimescaleDB
- Keycloak
- OCPP/OCPI protocol implementation
- Next.js 14 (admin console, user console)
- GitBook (for documentation)
- TurboRepo (for monorepo management)

### Setup

1. Clone the repository

```bash
git clone <repository-url>
cd ev-saas-platform
```

2. Install dependencies

```bash
yarn install
```

3. Start development servers

```bash
yarn dev
```

## Development Workflow

### Running Services

- To run all services: `yarn dev`
- To build all services: `yarn build`
- To run tests: `yarn test`
- To lint code: `yarn lint`

### API Gateway Options

The platform supports two API Gateway options:

1. **Spring Cloud Gateway** (default): A Java-based API Gateway with built-in service discovery, circuit breaking, and JWT validation.
2. **Nginx Gateway**: A lightweight, high-performance alternative with lower resource usage.

To switch between gateways, use the included script:

```bash
# Switch to Nginx as the API Gateway
./switch-gateway.sh nginx

# Switch back to Spring Cloud Gateway
./switch-gateway.sh spring
```

See `infra/docker/nginx/README.md` for more details on the Nginx Gateway implementation.

### Adding Dependencies

- Add a workspace dependency: `yarn workspace <workspace-name> add <package-name>`
- Add a dev dependency to a workspace: `yarn workspace <workspace-name> add -D <package-name>`
- Add a dependency to the root: `yarn add -W <package-name>`

## Project Rules

Please refer to the following documents for project guidelines:

- [Task Management](./TASK_MANAGEMENT.md)
- [Task Progress](./TASK_PROGRESS.md)
- [TODO List](./TODO.md)

## License

This project is proprietary and confidential.