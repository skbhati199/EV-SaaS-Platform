# EV SaaS Platform Kubernetes Configuration

This directory contains all Kubernetes configurations and deployment scripts for the EV SaaS Platform services.

## Directory Structure

```
/k8s
├── namespaces/             # Namespace definitions
├── infrastructure/         # Infrastructure components (Postgres, Kafka, etc.)
├── services/               # Microservice deployments
│   ├── auth-service.yaml       # Authentication service
│   ├── billing-service.yaml    # Billing service
│   ├── notification-service.yaml  # Notification service
│   ├── roaming-service.yaml   # Roaming service
│   ├── station-service.yaml   # Station service
│   ├── scheduler-service.yaml # Scheduler service
│   ├── user-service.yaml      # User service
│   ├── smart-charging-service.yaml # Smart charging service
│   └── eureka-server.yaml     # Service discovery
├── ingress/                # Gateway and ingress configurations
│   ├── nginx-gateway.yaml     # NGINX API gateway
│   └── service-proxy.yaml     # Direct service proxy
├── utils/                  # Utility services (Swagger UI, etc.)
└── scripts/                # Deployment and management scripts
```

## Quick Start

### 1. Set up infrastructure

```bash
./scripts/deploy-infrastructure.sh
```

### 2. Deploy all services

```bash
./scripts/deploy-all-services.sh
```

### 3. Deploy gateway and proxy

```bash
./scripts/deploy-gateways.sh
```

## Service Endpoints

- API Gateway: http://localhost:30080
- Service Proxy: http://localhost:30090

## Individual Service Deployment

To deploy a specific service:

```bash
./scripts/deploy-service.sh <service-name>
```

Example:
```bash
./scripts/deploy-service.sh auth-service
```

## Accessing Services

### Option 1: Via API Gateway

```
http://localhost:30080/<service-path>/
```

Example:
```
http://localhost:30080/auth/
http://localhost:30080/billing/
```

### Option 2: Via Service Proxy

```
http://localhost:30090/<service-path>/
```

## Troubleshooting

To check service status:

```bash
./scripts/check-services.sh
```

To restart a service:

```bash
./scripts/restart-service.sh <service-name>
```

To view logs for a service:

```bash
./scripts/view-logs.sh <service-name>
```
