'use client';

import React, { useState } from 'react';
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { Loader2 } from "lucide-react";

interface LogsViewerProps {
  height?: string;
  width?: string;
  defaultService?: string;
  defaultLimit?: number;
}

/**
 * Component for viewing system logs from Loki
 */
const LogsViewer: React.FC<LogsViewerProps> = ({
  height = '600px',
  width = '100%',
  defaultService = 'all',
  defaultLimit = 100
}) => {
  const [service, setService] = useState(defaultService);
  const [search, setSearch] = useState('');
  const [limit, setLimit] = useState(defaultLimit);
  const [isLoading, setIsLoading] = useState(false);
  const [logs, setLogs] = useState<Array<{ timestamp: string, message: string, service: string, level: string }>>([]);
  
  // Function to format the timestamp for display
  const formatTimestamp = (timestamp: string): string => {
    const date = new Date(timestamp);
    return date.toLocaleString();
  };
  
  // Get the CSS class for log level
  const getLogLevelClass = (level: string): string => {
    switch(level.toLowerCase()) {
      case 'error': return 'text-red-500';
      case 'warn': return 'text-amber-500';
      case 'info': return 'text-blue-500';
      case 'debug': return 'text-gray-500';
      default: return 'text-gray-700';
    }
  };

  // Function to load logs from Loki
  const loadLogs = async () => {
    setIsLoading(true);
    try {
      // In a real implementation, this would be an API call to your backend which proxies to Loki
      // For demo purposes, we're generating sample logs
      setTimeout(() => {
        const sampleLogs = generateSampleLogs(limit, service, search);
        setLogs(sampleLogs);
        setIsLoading(false);
      }, 500);
    } catch (error) {
      console.error('Error fetching logs:', error);
      setIsLoading(false);
    }
  };
  
  // Generates sample logs for demo purposes - replace with actual API call
  const generateSampleLogs = (count: number, serviceFilter: string, searchText: string) => {
    const services = ['auth-service', 'user-service', 'station-service', 'billing-service', 'api-gateway'];
    const levels = ['info', 'warn', 'error', 'debug'];
    const messages = [
      'Application started successfully',
      'User logged in successfully',
      'Failed to connect to database',
      'Charging session started for user',
      'Payment processed successfully',
      'API request received from client',
      'Charging station went offline',
      'Error processing transaction',
      'Scheduled task completed',
      'Cache invalidated'
    ];
    
    const result = [];
    const now = new Date();
    
    for (let i = 0; i < count; i++) {
      const timestamp = new Date(now.getTime() - Math.random() * 86400000).toISOString();
      const serviceLog = services[Math.floor(Math.random() * services.length)];
      const level = levels[Math.floor(Math.random() * levels.length)];
      const message = messages[Math.floor(Math.random() * messages.length)];
      
      // Apply filters
      if (serviceFilter !== 'all' && serviceLog !== serviceFilter) continue;
      if (searchText && !message.toLowerCase().includes(searchText.toLowerCase())) continue;
      
      result.push({
        timestamp,
        service: serviceLog,
        level,
        message
      });
    }
    
    return result.sort((a, b) => new Date(b.timestamp).getTime() - new Date(a.timestamp).getTime());
  };

  return (
    <div className="logs-viewer-container">
      <Card className="mb-4">
        <CardHeader>
          <CardTitle className="text-lg">Log Filters</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="flex flex-col gap-4 md:flex-row md:items-end">
            <div className="flex-1">
              <label className="text-sm font-medium mb-1 block">Service</label>
              <Select value={service} onValueChange={setService}>
                <SelectTrigger>
                  <SelectValue placeholder="Select service" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="all">All Services</SelectItem>
                  <SelectItem value="auth-service">Auth Service</SelectItem>
                  <SelectItem value="user-service">User Service</SelectItem>
                  <SelectItem value="station-service">Station Service</SelectItem>
                  <SelectItem value="billing-service">Billing Service</SelectItem>
                  <SelectItem value="api-gateway">API Gateway</SelectItem>
                </SelectContent>
              </Select>
            </div>
            
            <div className="flex-1">
              <label className="text-sm font-medium mb-1 block">Search</label>
              <Input 
                type="text" 
                value={search} 
                onChange={(e) => setSearch(e.target.value)}
                placeholder="Filter by content..." 
              />
            </div>
            
            <div className="w-32">
              <label className="text-sm font-medium mb-1 block">Limit</label>
              <Select 
                value={limit.toString()} 
                onValueChange={(val) => setLimit(parseInt(val))}
              >
                <SelectTrigger>
                  <SelectValue placeholder="Limit" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="50">50 logs</SelectItem>
                  <SelectItem value="100">100 logs</SelectItem>
                  <SelectItem value="200">200 logs</SelectItem>
                  <SelectItem value="500">500 logs</SelectItem>
                </SelectContent>
              </Select>
            </div>
            
            <Button onClick={loadLogs} disabled={isLoading}>
              {isLoading ? (
                <>
                  <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                  Loading...
                </>
              ) : (
                'Load Logs'
              )}
            </Button>
          </div>
        </CardContent>
      </Card>
      
      <div 
        className="logs-container bg-white rounded-lg shadow-md overflow-hidden border border-gray-200"
        style={{ height, width, overflowY: 'auto' }}
      >
        {logs.length === 0 && !isLoading ? (
          <div className="flex items-center justify-center h-full">
            <p className="text-gray-500">No logs to display. Click &quot;Load Logs&quot; to fetch logs.</p>
          </div>
        ) : (
          <table className="min-w-full divide-y divide-gray-200">
            <thead className="bg-gray-50 sticky top-0">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Timestamp
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Service
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Level
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Message
                </th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {logs.map((log, index) => (
                <tr key={index} className="hover:bg-gray-50">
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                    {formatTimestamp(log.timestamp)}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm font-medium">
                    <span className="px-2 inline-flex text-xs leading-5 font-semibold rounded-full bg-blue-100 text-blue-800">
                      {log.service}
                    </span>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm">
                    <span className={`font-medium ${getLogLevelClass(log.level)}`}>
                      {log.level.toUpperCase()}
                    </span>
                  </td>
                  <td className="px-6 py-4 text-sm text-gray-500">
                    {log.message}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </div>
  );
};

export default LogsViewer; 