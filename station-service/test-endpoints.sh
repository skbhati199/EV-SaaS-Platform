#!/bin/bash

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Base URL for the API
BASE_URL="http://localhost:8082"

# JWT Token - Replace with a valid token when testing
JWT_TOKEN="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIwMDAwMDAwMC0wMDAwLTAwMDAtMDAwMC0wMDAwMDAwMDAwMDIiLCJyb2xlcyI6WyJDUE8iLCJVU0VSIl0sImlhdCI6MTUxNjIzOTAyMn0.fake-signature"

# Function to make API requests and format response
test_endpoint() {
    local method=$1
    local endpoint=$2
    local payload=$3
    local description=$4
    local no_auth=${5:-false}  # Optional parameter for endpoints that don't need auth

    echo -e "${YELLOW}Testing: ${description}${NC}"
    echo "${method} ${endpoint}"
    
    # Define empty data parameter for POST/PUT requests if payload is provided
    local data_param=""
    if [ "$payload" != "" ]; then
        data_param="-d '$payload'"
    fi
    
    # Add authentication header if needed
    local auth_header=""
    if [ "$no_auth" = false ]; then
        auth_header="-H \"Authorization: Bearer ${JWT_TOKEN}\""
    fi
    
    # Execute curl command with appropriate method
    if [ "$method" == "GET" ]; then
        if [ "$no_auth" = true ]; then
            response=$(curl -s -w "\n%{http_code}" -X ${method} ${BASE_URL}${endpoint})
        else
            response=$(curl -s -w "\n%{http_code}" -X ${method} -H "Authorization: Bearer ${JWT_TOKEN}" ${BASE_URL}${endpoint})
        fi
    else
        if [ "$no_auth" = true ]; then
            response=$(curl -s -w "\n%{http_code}" -X ${method} -H "Content-Type: application/json" ${data_param} ${BASE_URL}${endpoint})
        else
            response=$(curl -s -w "\n%{http_code}" -X ${method} -H "Content-Type: application/json" -H "Authorization: Bearer ${JWT_TOKEN}" ${data_param} ${BASE_URL}${endpoint})
        fi
    fi
    
    # Extract status code (last line)
    http_code=$(echo "$response" | tail -n1)
    # Extract response body (all but last line)
    body=$(echo "$response" | sed '$d')
    
    # Check if successful
    if [[ $http_code -ge 200 && $http_code -lt 300 ]]; then
        echo -e "${GREEN}Success (${http_code})${NC}"
    else
        echo -e "${RED}Error (${http_code})${NC}"
    fi
    
    # Print a shortened version of the response
    echo "Response: ${body:0:100}..."
    echo "------------------------"
}

echo "=== Testing Station Service API Endpoints ==="

# Charging Station Endpoints
station_id="00000000-0000-0000-0000-000000000001" # Example UUID
cpo_id="00000000-0000-0000-0000-000000000002" # Example UUID

test_endpoint "GET" "/api/v1/stations" "" "Get all stations"
test_endpoint "GET" "/api/v1/stations/${station_id}" "" "Get station by ID"
test_endpoint "GET" "/api/v1/stations/serial/ABC123" "" "Get station by serial number"
test_endpoint "GET" "/api/v1/stations/status/AVAILABLE" "" "Get stations by status"
test_endpoint "GET" "/api/v1/stations/cpo/${cpo_id}" "" "Get stations by CPO ID"
test_endpoint "GET" "/api/v1/stations/nearby?latitude=52.5200&longitude=13.4050&radiusInKm=10.0" "" "Get stations near location"

create_station_payload='{
  "serialNumber": "TEST123",
  "model": "ChargeStation X1",
  "manufacturer": "EV Solutions",
  "firmwareVersion": "1.0.0",
  "cpoId": "00000000-0000-0000-0000-000000000002",
  "location": {
    "latitude": 52.5200,
    "longitude": 13.4050,
    "address": "123 Main St",
    "city": "Berlin",
    "postalCode": "10115",
    "country": "Germany"
  }
}'

test_endpoint "POST" "/api/v1/stations" "$create_station_payload" "Create station"

update_station_payload='{
  "model": "ChargeStation X2",
  "firmwareVersion": "1.0.1",
  "location": {
    "address": "124 Main St",
    "city": "Berlin",
    "postalCode": "10115",
    "country": "Germany"
  }
}'

test_endpoint "PUT" "/api/v1/stations/${station_id}" "$update_station_payload" "Update station"
test_endpoint "PUT" "/api/v1/stations/${station_id}/status?status=UNAVAILABLE" "" "Update station status"

heartbeat_payload='{
  "timestamp": "2023-10-01T12:00:00Z",
  "status": "AVAILABLE"
}'

test_endpoint "POST" "/api/v1/stations/${station_id}/heartbeat" "$heartbeat_payload" "Process heartbeat"

# EVSE Endpoints
evse_id="EV0001"
evse_uuid="00000000-0000-0000-0000-000000000003"

registration_payload='{
  "evseId": "EV0002",
  "stationId": "00000000-0000-0000-0000-000000000001",
  "connectorType": "TYPE_2",
  "maxPower": 22.0,
  "status": "AVAILABLE"
}'

test_endpoint "POST" "/api/v1/evse" "$registration_payload" "Register EVSE"
test_endpoint "GET" "/api/v1/evse/${evse_uuid}" "" "Get EVSE by ID"
test_endpoint "GET" "/api/v1/evse/evse-id/${evse_id}" "" "Get EVSE by EVSE ID"
test_endpoint "GET" "/api/v1/evse/owner/${cpo_id}" "" "Get EVSEs by owner ID"
test_endpoint "PUT" "/api/v1/evse/heartbeat/${evse_id}" "" "Update EVSE heartbeat"

# Connector Endpoints
connector_id="1"
connector_uuid="00000000-0000-0000-0000-000000000004"

test_endpoint "GET" "/api/v1/stations/${station_id}/connectors" "" "Get connectors by station ID"
test_endpoint "GET" "/api/v1/stations/${station_id}/connectors/${connector_id}" "" "Get connector by station ID and connector ID"
test_endpoint "GET" "/api/v1/stations/${station_id}/connectors/status/AVAILABLE" "" "Get connectors by station ID and status"

create_connector_payload='{
  "connectorId": 2,
  "type": "CCS",
  "maxPower": 50.0,
  "status": "AVAILABLE"
}'

test_endpoint "POST" "/api/v1/stations/${station_id}/connectors" "$create_connector_payload" "Create connector"

update_connector_payload='{
  "maxPower": 75.0,
  "status": "AVAILABLE"
}'

test_endpoint "PUT" "/api/v1/stations/${station_id}/connectors/${connector_uuid}" "$update_connector_payload" "Update connector"
test_endpoint "PUT" "/api/v1/stations/${station_id}/connectors/${connector_uuid}/status?status=UNAVAILABLE" "" "Update connector status"

# Test WebSocket (should work without auth)
echo -e "${YELLOW}Testing: WebSocket endpoint (no auth required)${NC}"
echo "WebSocket endpoint /ocpp should be accessible without JWT"

# Swagger documentation
echo -e "${YELLOW}Testing Swagger Documentation${NC}"
test_endpoint "GET" "/v3/api-docs" "" "Get OpenAPI JSON" true
test_endpoint "GET" "/swagger-ui.html" "" "Access Swagger UI" true

echo "=== Testing Complete ===" 