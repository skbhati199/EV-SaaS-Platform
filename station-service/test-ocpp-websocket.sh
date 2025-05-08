#!/bin/bash

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${YELLOW}Testing OCPP WebSocket Connection${NC}"

# Install websocat if not available
if ! command -v websocat &> /dev/null; then
    echo "websocat not found, trying to install..."
    if command -v brew &> /dev/null; then
        brew install websocat
    else
        echo -e "${RED}Please install websocat manually to test WebSockets:${NC}"
        echo "https://github.com/vi/websocat"
        exit 1
    fi
fi

# Test OCPP WebSocket connection
echo "Connecting to ws://localhost:8082/ocpp..."
echo "Sending a test message. Press Ctrl+C to exit after testing."

# Connect to the WebSocket endpoint and allow sending messages
websocat -v ws://localhost:8082/ocpp

# Note: This is interactive, the user will need to type messages
# and can see the responses in real-time. 