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
    
    uri = f"ws://localhost:8082/ocpp/{station_id}"
    
    print(f"Connecting to {uri}...")
    
    try:
        async with websockets.connect(uri) as ws:
            print(f"Connected successfully to {uri}")
            
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
            await ws.send(json.dumps(boot_request))
            
            # Wait for response
            response = await ws.recv()
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
            await ws.send(json.dumps(heartbeat_request))
            
            # Wait for response
            response = await ws.recv()
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
            await ws.send(json.dumps(status_request))
            
            # Wait for response
            response = await ws.recv()
            print(f"Received: {response}")
            
            print("Tests completed successfully!")
            
    except Exception as e:
        print(f"Error: {e}")
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