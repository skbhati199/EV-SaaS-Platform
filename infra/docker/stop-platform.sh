#!/bin/bash

# Colors for terminal output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo -e "${YELLOW}Stopping EV-SaaS Platform...${NC}"

# Navigate to project root
cd "$(dirname "$0")/../.."

# Stop all containers
docker-compose down

echo -e "${GREEN}All services stopped${NC}"

# Ask if user wants to clean volumes
read -p "Do you want to clean all volumes? This will DELETE ALL DATA! (y/N): " clean_volumes

if [[ $clean_volumes == "y" || $clean_volumes == "Y" ]]; then
    echo -e "${RED}Removing all volumes...${NC}"
    docker-compose down -v
    
    # Remove local volume directories
    rm -rf ./volumes/*
    
    echo -e "${GREEN}All volumes removed${NC}"
fi

echo -e "${GREEN}Platform shutdown complete${NC}" 