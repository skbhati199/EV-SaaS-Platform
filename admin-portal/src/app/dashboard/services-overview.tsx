"use client";

import React, { useState, useEffect, useCallback } from 'react';
import { Card, CardContent, CardFooter, CardHeader, CardTitle } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { CheckCircle2, XCircle, AlertCircle, ServerCrash, Server } from "lucide-react";
import { Button } from '@/components/ui/button';
import Link from 'next/link';
import services, { getServiceHealthEndpoint } from '../config/services';

// Service status definition
type ServiceStatus = 'online' | 'offline' | 'degraded' | 'unknown';

interface ServiceStatusInfo {
  id: string;
  name: string;
  description: string;
  status: ServiceStatus;
  url: string;
  dashboardPath: string;
}

export function ServicesOverview() {
  const [serviceStatuses, setServiceStatuses] = useState<ServiceStatusInfo[]>(
    Object.values(services).map(service => ({
      id: service.id,
      name: service.name,
      description: service.description,
      status: 'unknown',
      url: getServiceHealthEndpoint(service.id as keyof typeof services),
      dashboardPath: service.dashboardPath
    }))
  );

  // Use callback to avoid dependency on serviceStatuses
  const checkServices = useCallback(async () => {
    setServiceStatuses(prevServices => 
      prevServices.map(service => {
        // Simulate service status checks
        const statusOptions: ServiceStatus[] = ['online', 'degraded', 'offline'];
        const randomStatus = Math.random() > 0.7 
          ? statusOptions[Math.floor(Math.random() * statusOptions.length)]
          : 'online';
        
        return {
          ...service,
          status: randomStatus
        };
      })
    );
  }, []);

  // Simulate checking service status
  useEffect(() => {
    // Check services immediately
    checkServices();
    
    // In production, you might want to poll this every few minutes
    const interval = setInterval(checkServices, 60000);
    return () => clearInterval(interval);
  }, [checkServices]);

  const getStatusIcon = (status: ServiceStatus) => {
    switch (status) {
      case 'online':
        return <CheckCircle2 className="h-5 w-5 text-green-500" />;
      case 'offline':
        return <XCircle className="h-5 w-5 text-red-500" />;
      case 'degraded':
        return <AlertCircle className="h-5 w-5 text-amber-500" />;
      default:
        return <ServerCrash className="h-5 w-5 text-gray-400" />;
    }
  };

  const getStatusBadge = (status: ServiceStatus) => {
    switch (status) {
      case 'online':
        return <Badge className="bg-green-500">Online</Badge>;
      case 'offline':
        return <Badge variant="destructive">Offline</Badge>;
      case 'degraded':
        return <Badge variant="outline" className="text-amber-500 border-amber-500">Degraded</Badge>;
      default:
        return <Badge variant="outline">Unknown</Badge>;
    }
  };

  return (
    <Card>
      <CardHeader>
        <CardTitle className="flex items-center">
          <Server className="h-5 w-5 mr-2" />
          Microservices Status
        </CardTitle>
      </CardHeader>
      <CardContent>
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          {serviceStatuses.map((service) => (
            <Card key={service.id} className="overflow-hidden">
              <CardHeader className="p-4 pb-2 flex flex-row items-center justify-between">
                <div className="flex items-center">
                  {getStatusIcon(service.status)}
                  <h3 className="font-medium ml-2">{service.name}</h3>
                </div>
                {getStatusBadge(service.status)}
              </CardHeader>
              <CardContent className="p-4 pt-0">
                <p className="text-sm text-gray-500 dark:text-gray-400">{service.description}</p>
              </CardContent>
              <CardFooter className="p-4 pt-0 flex items-center justify-between">
                <Button variant="link" size="sm" className="p-0" asChild>
                  <Link href={service.dashboardPath}>
                    View Dashboard
                  </Link>
                </Button>
              </CardFooter>
            </Card>
          ))}
        </div>
      </CardContent>
    </Card>
  );
} 