#!/bin/bash
set -e

# Colors for terminal output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo -e "${GREEN}Starting minimal EV-SaaS Platform services...${NC}"

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo -e "${RED}Error: Docker is not running or not accessible${NC}"
    exit 1
fi

# Navigate to project root
cd "$(dirname "$0")/../.."

# Start the minimal services
echo -e "${YELLOW}Starting minimal infrastructure (Postgres, Redis, Eureka)...${NC}"
docker-compose -f docker-compose-minimal.yml up -d

echo -e "${GREEN}Minimal services started successfully!${NC}"
echo -e "${GREEN}Eureka: http://localhost:8761${NC}"
echo -e "${GREEN}Keycloak: http://localhost:8090${NC}"
echo -e "${YELLOW}Postgres is available on port 5432${NC}"
echo -e "${YELLOW}Redis is available on port 6379${NC}"

echo -e "\n${YELLOW}To view logs: docker-compose -f docker-compose-minimal.yml logs -f${NC}"
echo -e "${YELLOW}To stop: docker-compose -f docker-compose-minimal.yml down${NC}" 