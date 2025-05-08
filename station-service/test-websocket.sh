#!/bin/bash

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${YELLOW}Testing OCPP WebSocket Connection${NC}"

# Run the Python client for WebSocket testing
echo -e "${YELLOW}Running Python WebSocket client test...${NC}"
python3 test-ocpp-client-local.py

# Get the exit code of the Python script
PYTHON_EXIT=$?

if [ $PYTHON_EXIT -eq 0 ]; then
    echo -e "${GREEN}Python WebSocket test successful!${NC}"
else
    echo -e "${RED}Python WebSocket test failed!${NC}"
    echo "Try using websocat for manual testing:"
    echo "websocat -v ws://localhost:8082/ocpp/TEST_STATION_001"
    echo "websocat -v ws://localhost:8082/ws/ocpp/TEST_STATION_001"
fi

exit $PYTHON_EXIT 