#!/bin/bash

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}==== Running Roaming Service Kafka Event Tests ====${NC}"
echo -e "${BLUE}This script will run unit tests for the Kafka event implementation${NC}"
echo -e "${BLUE}in the Roaming service to validate event production and consumption.${NC}"
echo ""

# Move to the roaming-service directory
cd "$(dirname "$0")" || exit 1

# Make sure we have maven wrapper
if [ ! -f "./mvnw" ]; then
    echo -e "${RED}Maven wrapper not found, creating one...${NC}"
    if command -v mvn &> /dev/null; then
        mvn wrapper:wrapper
    else
        echo -e "${RED}Maven not found. Please install maven or run from a parent directory with mvnw${NC}"
        exit 1
    fi
fi

# Run tests with the maven wrapper
echo -e "${BLUE}Running Kafka event tests...${NC}"
./mvnw test -Dtest=RoamingEventTest

# Check if tests passed
if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ All Kafka event tests passed!${NC}"
    echo -e "${GREEN}✓ The Roaming service Kafka event-driven architecture is working correctly.${NC}"
else
    echo -e "${RED}✗ Some tests failed. Please check the output above for details.${NC}"
    exit 1
fi

echo ""
echo -e "${BLUE}==== Testing Complete ====${NC}"
echo -e "${BLUE}The Roaming service supports the following events:${NC}"
echo -e "${GREEN}- Location Events: Creation, updates, deletions of charging locations${NC}"
echo -e "${GREEN}- Token Events: Authentication token management for partners${NC}"
echo -e "${GREEN}- Roaming Partner Events: CPO/EMSP partnership management${NC}"
echo -e "${GREEN}- CDR Events: Charge Detail Record creation and processing${NC}"
echo ""
echo -e "${BLUE}Next Steps:${NC}"
echo -e "${GREEN}1. Implement Smart Charging Kafka integration${NC}"
echo -e "${GREEN}2. Set up real-time monitoring with WebSockets${NC}"
echo -e "${GREEN}3. Integrate with the Admin Portal${NC}" 