# EV SaaS Platform Test Environment

This document describes the test environment setup for the EV SaaS Platform and how to use it for testing and quality assurance.

## Overview

The test environment provides a comprehensive setup for testing all microservices in the EV SaaS Platform with:

- System health monitoring
- API integration tests
- Load testing
- Performance monitoring

## Quick Start

To start the test environment:

```bash
docker-compose -f docker-compose.test.yml up -d
```

To stop the test environment:

```bash
docker-compose -f docker-compose.test.yml down
```

## Test Components

### 1. System Health Tests

The `system-health-test` service performs continuous health checks on all microservices and stores the results in JSON format.

- **Frequency**: Every 5 minutes (configurable via `TEST_INTERVAL_SECONDS`)
- **Results Location**: `/app/test-results` inside the container, mounted to `health_test_results` volume
- **Services Tested**: All microservices defined in `SERVICES_TO_TEST` environment variable

### 2. Integration Tests

The `integration-tests` service runs Newman/Postman tests against the API endpoints to verify functional requirements.

- **Collection**: Uses the Postman collection at `EV SaaS Auth Service API.postman_collection.json`
- **Results Location**: `./integration-test-results` directory
- **Reports**: Generates JUnit XML and HTML reports for CI/CD integration

### 3. Load Testing

The `load-testing` service uses k6 to simulate load on the services and measure performance.

- **Script**: `./infra/testing/k6-scripts/load-test.js`
- **Configuration**:
  - `VUS`: 10 (virtual users)
  - `DURATION`: 30s
  - Thresholds: 95% of requests under 500ms, 95% success rate
- **Results**: Stored in InfluxDB and visualized in Grafana

### 4. Monitoring

- **Prometheus**: Collects metrics from all services
- **Grafana**: Provides dashboards for system metrics at `http://localhost:3000`
- **Grafana-K6**: Provides dashboards for load test results at `http://localhost:3002`
- **MailHog**: Captures test emails at `http://localhost:8025`

## Test URLs

- Main API Gateway: http://localhost:8090
- Eureka Dashboard: http://localhost:8761
- Keycloak Admin: http://localhost:8080
- Grafana Dashboard: http://localhost:3000
- Load Test Dashboard: http://localhost:3002
- MailHog (Email Testing): http://localhost:8025
- Prometheus: http://localhost:9090

## Test Database Credentials

- **PostgreSQL**:
  - Host: localhost:5432
  - Database: evtest_db
  - Username: evtest
  - Password: evtest_password

- **TimescaleDB**:
  - Host: localhost:5431
  - Database: evtest_timeseries_db
  - Username: evtest
  - Password: evtest_password

## Running Individual Tests

### To run just the integration tests:

```bash
docker-compose -f docker-compose.test.yml up integration-tests
```

### To run just the load tests:

```bash
docker-compose -f docker-compose.test.yml up load-testing
```

## Extending the Tests

### Adding New Integration Tests

1. Update the Postman collection with new test cases
2. Restart the integration-tests container:
   ```bash
   docker-compose -f docker-compose.test.yml restart integration-tests
   ```

### Adding New Load Tests

1. Modify the k6 script at `./infra/testing/k6-scripts/load-test.js`
2. Restart the load-testing container:
   ```bash
   docker-compose -f docker-compose.test.yml restart load-testing
   ```

## Troubleshooting

If a service fails to start, check its logs:

```bash
docker-compose -f docker-compose.test.yml logs [service-name]
```

Common service names:
- `auth-service`
- `user-service`
- `station-service`
- `billing-service`
- `notification-service`
- `smart-charging`
- `roaming-service`
- `system-health-test`
- `integration-tests`
- `load-testing` 