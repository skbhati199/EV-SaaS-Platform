'use client';

import React, { useState } from 'react';
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { CheckCircle, XCircle, AlertCircle, Loader2 } from "lucide-react";
import { apiService } from "@/app/services";

const services = [
  { name: "API Gateway", endpoint: "/api-gateway/health" },
  { name: "Auth Service", endpoint: "/auth/health" },
  { name: "User Service", endpoint: "/api/users/health" },
  { name: "Station Service", endpoint: "/api/stations/health" },
  { name: "Billing Service", endpoint: "/api/billing/health" },
  { name: "Notification Service", endpoint: "/api/notifications/health" },
  { name: "Roaming Service", endpoint: "/api/roaming/health" },
  { name: "Smart Charging Service", endpoint: "/api/smart-charging/health" }
];

export default function ServicesTestPage() {
  const [results, setResults] = useState({});
  const [testing, setTesting] = useState(false);
  
  const testAllServices = async () => {
    setTesting(true);
    const newResults = {};
    
    for (const service of services) {
      try {
        const startTime = performance.now();
        const response = await apiService.api.get(service.endpoint);
        const endTime = performance.now();
        
        newResults[service.name] = {
          status: response.status === 200 ? 'success' : 'error',
          responseTime: Math.round(endTime - startTime),
          data: response.data
        };
      } catch (error) {
        newResults[service.name] = {
          status: 'error',
          message: error.message
        };
      }
    }
    
    setResults(newResults);
    setTesting(false);
  };
  
  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-2xl font-bold">Services Connectivity Test</h1>
          <p className="text-gray-600">Test connectivity to all backend services</p>
        </div>
        <Button 
          onClick={testAllServices} 
          disabled={testing}
        >
          {testing ? (
            <>
              <Loader2 className="mr-2 h-4 w-4 animate-spin" />
              Testing...
            </>
          ) : "Test All Services"}
        </Button>
      </div>
      
      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        {services.map((service) => (
          <Card key={service.name}>
            <CardHeader className="pb-2">
              <CardTitle className="text-sm font-medium">{service.name}</CardTitle>
              <CardDescription className="text-xs">{service.endpoint}</CardDescription>
            </CardHeader>
            <CardContent>
              {!results[service.name] ? (
                <div className="text-gray-500">Not tested yet</div>
              ) : results[service.name].status === 'success' ? (
                <div className="flex items-center space-x-2">
                  <CheckCircle className="h-5 w-5 text-green-500" />
                  <div>
                    <div className="font-medium">Connected</div>
                    <div className="text-xs text-gray-500">
                      Response time: {results[service.name].responseTime}ms
                    </div>
                  </div>
                </div>
              ) : (
                <div className="flex items-center space-x-2">
                  <XCircle className="h-5 w-5 text-red-500" />
                  <div>
                    <div className="font-medium">Failed</div>
                    <div className="text-xs text-gray-500">
                      {results[service.name].message}
                    </div>
                  </div>
                </div>
              )}
            </CardContent>
          </Card>
        ))}
      </div>
    </div>
  );
}
