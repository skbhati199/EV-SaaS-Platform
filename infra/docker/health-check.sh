#!/bin/bash

# Colors for terminal output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Navigate to project root
cd "$(dirname "$0")/../.."

echo -e "${BLUE}EV-SaaS Platform Health Check${NC}"
echo "=================================="

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo -e "${RED}Error: Docker is not running or not accessible${NC}"
    exit 1
fi

# Get all services
SERVICES=$(docker-compose ps --services)

# Check the status of each service
echo -e "${YELLOW}Service Status:${NC}"
printf "%-25s %-20s %-20s\n" "SERVICE" "STATUS" "HEALTH CHECK"
printf "%-25s %-20s %-20s\n" "-------" "------" "------------"

for service in $SERVICES; do
    # Get container ID if service is running
    CONTAINER_ID=$(docker-compose ps -q $service 2>/dev/null)
    
    if [ -z "$CONTAINER_ID" ]; then
        printf "%-25s ${RED}%-20s${NC} %-20s\n" "$service" "NOT RUNNING" "N/A"
        continue
    fi
    
    # Get container status
    STATUS=$(docker inspect --format='{{.State.Status}}' $CONTAINER_ID 2>/dev/null)
    
    if [ "$STATUS" == "running" ]; then
        STATUS_COLOR=$GREEN
    else
        STATUS_COLOR=$RED
    fi
    
    # Check health status if available
    HEALTH="N/A"
    HEALTH_COLOR=$NC
    
    if docker inspect --format='{{if .State.Health}}{{.State.Health.Status}}{{end}}' $CONTAINER_ID 2>/dev/null | grep -q .; then
        HEALTH=$(docker inspect --format='{{.State.Health.Status}}' $CONTAINER_ID)
        
        if [ "$HEALTH" == "healthy" ]; then
            HEALTH_COLOR=$GREEN
        elif [ "$HEALTH" == "starting" ]; then
            HEALTH_COLOR=$YELLOW
        else
            HEALTH_COLOR=$RED
        fi
    fi
    
    printf "%-25s ${STATUS_COLOR}%-20s${NC} ${HEALTH_COLOR}%-20s${NC}\n" "$service" "$STATUS" "$HEALTH"
done

echo -e "\n${YELLOW}API Health Checks:${NC}"

# Check API Gateway
echo -e "${BLUE}API Gateway:${NC}"
if curl -s -f http://localhost:8080/actuator/health > /dev/null 2>&1; then
    echo -e "  ${GREEN}Reachable at http://localhost:8080${NC}"
else
    echo -e "  ${RED}Not reachable at http://localhost:8080${NC}"
fi

# Check Keycloak
echo -e "${BLUE}Keycloak:${NC}"
if curl -s -f http://localhost:8090/auth > /dev/null 2>&1; then
    echo -e "  ${GREEN}Reachable at http://localhost:8090${NC}"
else
    echo -e "  ${RED}Not reachable at http://localhost:8090${NC}"
fi

# Check Grafana
echo -e "${BLUE}Grafana:${NC}"
if curl -s -f http://localhost:3000 > /dev/null 2>&1; then
    echo -e "  ${GREEN}Reachable at http://localhost:3000${NC}"
else
    echo -e "  ${RED}Not reachable at http://localhost:3000${NC}"
fi

# Check Admin Portal
echo -e "${BLUE}Admin Portal:${NC}"
if curl -s -f http://localhost:3001 > /dev/null 2>&1; then
    echo -e "  ${GREEN}Reachable at http://localhost:3001${NC}"
else
    echo -e "  ${RED}Not reachable at http://localhost:3001${NC}"
fi

echo -e "\n${YELLOW}Resource Usage:${NC}"
docker stats --no-stream $(docker-compose ps -q)

echo -e "\n${GREEN}Health check complete${NC}" 