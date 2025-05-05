#!/bin/bash
set -e

# Colors for terminal output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo -e "${GREEN}Starting EV-SaaS Platform...${NC}"

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo -e "${RED}Error: Docker is not running or not accessible${NC}"
    exit 1
fi

# Navigate to project root (adjust if needed)
cd "$(dirname "$0")/../.."

# Create necessary directories for volumes if they don't exist
echo -e "${YELLOW}Creating directories for volume mounts...${NC}"
mkdir -p ./volumes/postgres
mkdir -p ./volumes/timescaledb
mkdir -p ./volumes/redis
mkdir -p ./volumes/keycloak
mkdir -p ./volumes/grafana
mkdir -p ./volumes/prometheus
mkdir -p ./volumes/loki

# Ensure proper permissions
chmod -R 777 ./volumes

# Start core infrastructure services first
echo -e "${YELLOW}Starting database services...${NC}"
docker-compose up -d timescaledb postgres redis

# Wait for databases to be ready
echo -e "${YELLOW}Waiting for databases to be ready...${NC}"
timeout=120
counter=0
while ! docker-compose exec -T postgres pg_isready -U evsaas > /dev/null 2>&1; do
    sleep 2
    counter=$((counter+2))
    echo -n "."
    
    if [ $counter -gt $timeout ]; then
        echo -e "${RED}Timed out waiting for Postgres to start${NC}"
        exit 1
    fi
done
echo ""

# Start Kafka and Zookeeper
echo -e "${YELLOW}Starting Kafka and Zookeeper...${NC}"
docker-compose up -d zookeeper kafka

# Wait for Kafka to be ready
echo -e "${YELLOW}Waiting for Kafka to be ready...${NC}"
counter=0
while ! docker-compose exec -T kafka bash -c 'kafka-topics --bootstrap-server localhost:9092 --list' > /dev/null 2>&1; do
    sleep 5
    counter=$((counter+5))
    echo -n "."
    
    if [ $counter -gt $timeout ]; then
        echo -e "${RED}Timed out waiting for Kafka to start${NC}"
        exit 1
    fi
done
echo ""

# Start service discovery
echo -e "${YELLOW}Starting Eureka service discovery...${NC}"
docker-compose up -d eureka-server

# Wait for Eureka to be ready
echo -e "${YELLOW}Waiting for Eureka to be ready...${NC}"
counter=0
while ! docker-compose exec -T eureka-server curl -s -f http://localhost:8761/actuator/health > /dev/null 2>&1; do
    sleep 5
    counter=$((counter+5))
    echo -n "."
    
    if [ $counter -gt $timeout ]; then
        echo -e "${RED}Timed out waiting for Eureka to start${NC}"
        exit 1
    fi
done
echo ""

# Start Keycloak
echo -e "${YELLOW}Starting Keycloak...${NC}"
docker-compose up -d keycloak

# Start core services
echo -e "${YELLOW}Starting core microservices...${NC}"
docker-compose up -d api-gateway auth-service user-service

# Wait for API gateway to be ready
echo -e "${YELLOW}Waiting for API Gateway to be ready...${NC}"
counter=0
while ! curl -s -f http://localhost:8080/actuator/health > /dev/null 2>&1; do
    sleep 5
    counter=$((counter+5))
    echo -n "."
    
    if [ $counter -gt $timeout ]; then
        echo -e "${YELLOW}API Gateway not responding, but continuing startup...${NC}"
        break
    fi
done
echo ""

# Start remaining business services
echo -e "${YELLOW}Starting remaining business services...${NC}"
docker-compose up -d station-service billing-service smart-charging scheduler-service notification-service roaming-service

# Start monitoring stack
echo -e "${YELLOW}Starting monitoring stack...${NC}"
docker-compose up -d prometheus grafana loki promtail

# Start admin portal
echo -e "${YELLOW}Starting admin portal...${NC}"
docker-compose up -d admin-portal

# Check overall system health
echo -e "${YELLOW}Checking system health...${NC}"
SERVICES=$(docker-compose ps --services)
SUCCESS=true

for service in $SERVICES; do
    if [ "$(docker-compose ps -q $service 2>/dev/null)" != "" ]; then
        status=$(docker-compose ps $service | grep -E 'Up|running' | wc -l)
        if [ $status -eq 0 ]; then
            echo -e "${RED}Service $service is not running properly${NC}"
            SUCCESS=false
        else
            echo -e "${GREEN}Service $service is running${NC}"
        fi
    fi
done

if $SUCCESS; then
    echo -e "${GREEN}\nEV-SaaS Platform started successfully!${NC}"
    echo -e "${GREEN}Admin Portal: http://localhost:3001${NC}"
    echo -e "${GREEN}API Gateway: http://localhost:8080${NC}"
    echo -e "${GREEN}Keycloak: http://localhost:8090${NC}"
    echo -e "${GREEN}Grafana: http://localhost:3000 (admin/admin)${NC}"
    echo -e "${GREEN}Prometheus: http://localhost:9090${NC}"
else
    echo -e "${YELLOW}\nSome services might not be running properly. Check logs with 'docker-compose logs <service_name>'${NC}"
fi

echo -e "${YELLOW}\nTo view logs of all services: docker-compose logs -f${NC}"
echo -e "${YELLOW}To stop all services: docker-compose down${NC}" 