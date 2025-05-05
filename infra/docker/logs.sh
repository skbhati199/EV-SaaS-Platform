#!/bin/bash

# Colors for terminal output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Navigate to project root
cd "$(dirname "$0")/../.."

# Function to display available services
function show_services() {
    echo -e "${YELLOW}Available services:${NC}"
    docker-compose ps --services
}

# Check if a service name was provided
if [ "$1" == "" ]; then
    # No service specified, show all logs
    echo -e "${GREEN}Showing logs for all services (press Ctrl+C to exit)...${NC}"
    docker-compose logs -f
elif [ "$1" == "list" ]; then
    # List available services
    show_services
else
    # Check if the specified service exists
    if docker-compose ps --services | grep -q "^$1$"; then
        echo -e "${GREEN}Showing logs for $1 (press Ctrl+C to exit)...${NC}"
        docker-compose logs -f "$1"
    else
        echo -e "${RED}Service '$1' not found${NC}"
        show_services
        exit 1
    fi
fi 