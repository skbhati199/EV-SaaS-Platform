'use client';

import React, { useState, useEffect } from 'react';
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { AlertCircle, CheckCircle, AlertTriangle, Loader2 } from 'lucide-react';

// Service health status types
type HealthStatus = 'UP' | 'DOWN' | 'DEGRADED' | 'UNKNOWN';

interface ServiceHealth {
  name: string;
  status: HealthStatus;
  details?: string;
  lastChecked: Date;
}

/**
 * SystemHealthOverview component displays real-time health status of all system services
 */
const SystemHealthOverview: React.FC = () => {
  const [services, setServices] = useState<ServiceHealth[]>([]);
  const [loading, setLoading] = useState(true);
  const [lastUpdated, setLastUpdated] = useState<Date>(new Date());

  // Fetch service health data
  const fetchHealthData = async () => {
    try {
      // In production, this would make an API call to your backend
      // For now, use mock data
      
      // Simulate API delay
      await new Promise(resolve => setTimeout(resolve, 500));
      
      // Mock data for demo
      const mockServices: ServiceHealth[] = [
        {
          name: 'API Gateway',
          status: Math.random() > 0.1 ? 'UP' : (Math.random() > 0.5 ? 'DEGRADED' : 'DOWN'),
          lastChecked: new Date()
        },
        {
          name: 'Auth Service',
          status: Math.random() > 0.1 ? 'UP' : 'DOWN',
          details: Math.random() > 0.8 ? 'High CPU usage' : undefined,
          lastChecked: new Date()
        },
        {
          name: 'User Service',
          status: Math.random() > 0.1 ? 'UP' : 'DEGRADED',
          lastChecked: new Date()
        },
        {
          name: 'Station Service',
          status: Math.random() > 0.1 ? 'UP' : 'DOWN',
          lastChecked: new Date()
        },
        {
          name: 'Billing Service',
          status: Math.random() > 0.1 ? 'UP' : 'DEGRADED',
          details: Math.random() > 0.8 ? 'Database connection issues' : undefined,
          lastChecked: new Date()
        },
        {
          name: 'Smart Charging',
          status: Math.random() > 0.2 ? 'UP' : 'DEGRADED',
          lastChecked: new Date()
        },
        {
          name: 'Notification Service',
          status: Math.random() > 0.1 ? 'UP' : 'DOWN',
          lastChecked: new Date()
        },
        {
          name: 'Roaming Service',
          status: Math.random() > 0.1 ? 'UP' : 'DEGRADED',
          lastChecked: new Date()
        },
        {
          name: 'Scheduler Service',
          status: 'UP',
          lastChecked: new Date()
        },
        {
          name: 'Auth Service',
          status: 'UP',
          lastChecked: new Date()
        }
      ];
      
      setServices(mockServices);
      setLoading(false);
      setLastUpdated(new Date());
      
    } catch (error) {
      console.error('Error fetching health data:', error);
      setLoading(false);
    }
  };

  // Initial load and refresh interval
  useEffect(() => {
    fetchHealthData();
    
    // Set up refresh interval (every 30 seconds)
    const intervalId = setInterval(fetchHealthData, 30000);
    
    // Cleanup interval on component unmount
    return () => clearInterval(intervalId);
  }, []);

  // Get appropriate icon for health status
  const getStatusIcon = (status: HealthStatus) => {
    switch (status) {
      case 'UP':
        return <CheckCircle className="w-5 h-5 text-green-500" />;
      case 'DOWN':
        return <AlertCircle className="w-5 h-5 text-red-500" />;
      case 'DEGRADED':
        return <AlertTriangle className="w-5 h-5 text-amber-500" />;
      case 'UNKNOWN':
      default:
        return <AlertCircle className="w-5 h-5 text-gray-400" />;
    }
  };

  // Get CSS class for status background
  const getStatusClass = (status: HealthStatus) => {
    switch (status) {
      case 'UP':
        return 'bg-green-50 border-green-200';
      case 'DOWN':
        return 'bg-red-50 border-red-200';
      case 'DEGRADED':
        return 'bg-amber-50 border-amber-200';
      case 'UNKNOWN':
      default:
        return 'bg-gray-50 border-gray-200';
    }
  };

  // Get overall system status
  const getOverallStatus = (): HealthStatus => {
    if (services.length === 0) return 'UNKNOWN';
    if (services.some(s => s.status === 'DOWN')) return 'DOWN';
    if (services.some(s => s.status === 'DEGRADED')) return 'DEGRADED';
    return 'UP';
  };
  
  // Get percentage of healthy services
  const getHealthyPercentage = (): number => {
    if (services.length === 0) return 0;
    const healthyCount = services.filter(s => s.status === 'UP').length;
    return Math.round((healthyCount / services.length) * 100);
  };

  return (
    <Card>
      <CardHeader className="pb-2">
        <CardTitle className="text-lg flex items-center justify-between">
          <span>System Health</span>
          {loading ? (
            <Loader2 className="w-4 h-4 animate-spin text-gray-500" />
          ) : (
            <button 
              onClick={fetchHealthData}
              className="text-xs text-gray-500 hover:text-gray-800 flex items-center"
            >
              Refresh
            </button>
          )}
        </CardTitle>
      </CardHeader>
      <CardContent>
        {loading ? (
          <div className="flex justify-center items-center h-40">
            <Loader2 className="w-8 h-8 animate-spin text-gray-400" />
          </div>
        ) : (
          <>
            <div className="flex justify-between items-center mb-4">
              <div className="flex items-center">
                {getStatusIcon(getOverallStatus())}
                <span className="ml-2 font-medium">
                  Overall: {getOverallStatus()}
                </span>
              </div>
              <div className="text-sm text-gray-500">
                {getHealthyPercentage()}% Healthy
              </div>
            </div>
            
            <div className="space-y-3">
              {services.map((service) => (
                <div 
                  key={service.name}
                  className={`flex justify-between items-center p-2 rounded border ${getStatusClass(service.status)}`}
                >
                  <div className="flex items-center">
                    {getStatusIcon(service.status)}
                    <span className="ml-2 font-medium text-sm">{service.name}</span>
                  </div>
                  {service.details && (
                    <span className="text-xs text-gray-500">{service.details}</span>
                  )}
                </div>
              ))}
            </div>
            
            <div className="mt-4 text-xs text-gray-500 text-right">
              Last updated: {lastUpdated.toLocaleTimeString()}
            </div>
          </>
        )}
      </CardContent>
    </Card>
  );
};

export default SystemHealthOverview; 