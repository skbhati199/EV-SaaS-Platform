#!/usr/bin/env python3

import asyncio
import websockets
import json
import uuid
import sys
import time
from datetime import datetime

# OCPP message types
CALL = 2
CALLRESULT = 3
CALLERROR = 4

async def ocpp_client():
    """OCPP WebSocket client to test communication without JWT auth"""
    
    # Station ID (typically used in OCPP connections)
    station_id = "TEST_STATION_001"
    
    # Try both WebSocket paths
    paths = [
        f"ws://localhost:8082/ws/ocpp/{station_id}",  # Path configured in WebSocketConfig
        f"ws://localhost:8082/ocpp/{station_id}"      # Path permitted in SecurityConfig
    ]
    
    connected = False
    websocket = None
    
    # Try each path until one works
    for uri in paths:
        try:
            print(f"Attempting to connect to {uri}...")
            websocket = await websockets.connect(
                uri,
                subprotocols=["ocpp1.6"],  # Required by StationHandshakeInterceptor
                ping_interval=None
            )
            print(f"Connected successfully to {uri}")
            connected = True
            break
        except Exception as e:
            print(f"Failed to connect to {uri}: {e}")
    
    if not connected:
        print("Failed to connect to any endpoint")
        return False
        
    try:
        # Generate a unique message ID
        message_id = str(uuid.uuid4())
        
        # Create BootNotification request (typical first message from a station)
        boot_request = [
            CALL,
            message_id,
            "BootNotification",
            {
                "chargePointVendor": "Test Vendor",
                "chargePointModel": "Test Model",
                "chargePointSerialNumber": "SN123456",
                "firmwareVersion": "1.0.0",
                "iccid": "",
                "imsi": "",
                "meterType": "Test Meter",
                "meterSerialNumber": "MSN123456"
            }
        ]
        
        # Send the BootNotification
        print(f"Sending BootNotification: {json.dumps(boot_request)}")
        await websocket.send(json.dumps(boot_request))
        
        # Wait for response
        response = await websocket.recv()
        print(f"Received: {response}")
        
        # Send Heartbeat
        heartbeat_id = str(uuid.uuid4())
        heartbeat_request = [
            CALL,
            heartbeat_id,
            "Heartbeat",
            {}
        ]
        
        print(f"Sending Heartbeat: {json.dumps(heartbeat_request)}")
        await websocket.send(json.dumps(heartbeat_request))
        
        # Wait for response
        response = await websocket.recv()
        print(f"Received: {response}")
        
        # Send StatusNotification
        status_id = str(uuid.uuid4())
        status_request = [
            CALL,
            status_id,
            "StatusNotification",
            {
                "connectorId": 1,
                "errorCode": "NoError",
                "status": "Available",
                "timestamp": datetime.utcnow().isoformat()
            }
        ]
        
        print(f"Sending StatusNotification: {json.dumps(status_request)}")
        await websocket.send(json.dumps(status_request))
        
        # Wait for response
        response = await websocket.recv()
        print(f"Received: {response}")
        
        print("Tests completed successfully!")
        await websocket.close()
        
    except Exception as e:
        print(f"Error: {e}")
        if websocket and websocket.open:
            await websocket.close()
        return False
        
    return True

# Run the async function
if __name__ == "__main__":
    print("Testing OCPP WebSocket (no JWT required)")
    success = asyncio.run(ocpp_client())
    
    if success:
        print("OCPP WebSocket test completed successfully!")
        sys.exit(0)
    else:
        print("OCPP WebSocket test failed.")
        sys.exit(1) 