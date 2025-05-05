'use client';

import React, { useState } from 'react';
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import GrafanaEmbed from '@/app/components/GrafanaEmbed';
import LogsViewer from '@/app/components/LogsViewer';
import { BarChart3, Activity, ServerCrash, Zap, FileText } from "lucide-react";

/**
 * Monitoring dashboard page that integrates Grafana dashboards and Loki logs
 */
export default function MonitoringPage() {
  const [activeTab, setActiveTab] = useState("overview");

  const dashboards = [
    {
      id: "overview",
      title: "Platform Overview",
      description: "Overall system health and performance metrics",
      url: "/d/ev-platform-overview/ev-platform-overview?orgId=1&refresh=5s&from=now-1h&to=now&theme=light",
      icon: <Activity className="w-4 h-4 mr-2" />
    },
    {
      id: "services",
      title: "Services Health",
      description: "Individual microservice metrics and status",
      url: "/d/services-dashboard/ev-services-health?orgId=1&refresh=5s&theme=light",
      icon: <ServerCrash className="w-4 h-4 mr-2" />
    },
    {
      id: "charging",
      title: "Charging Stations",
      description: "EVSE status, connectivity, and utilization metrics",
      url: "/d/charging-stations/ev-charging-stations?orgId=1&refresh=10s&theme=light",
      icon: <Zap className="w-4 h-4 mr-2" />
    },
    {
      id: "transactions",
      title: "Charging Transactions",
      description: "Real-time and historical charging session data",
      url: "/d/transactions/ev-charging-transactions?orgId=1&refresh=30s&theme=light",
      icon: <BarChart3 className="w-4 h-4 mr-2" />
    },
    {
      id: "logs",
      title: "System Logs",
      description: "Real-time platform logs from all services",
      icon: <FileText className="w-4 h-4 mr-2" />
    }
  ];

  return (
    <div className="container mx-auto py-6">
      <div className="mb-8">
        <h1 className="text-3xl font-bold mb-2">Platform Monitoring</h1>
        <p className="text-gray-600">
          Real-time monitoring and analytics for the EV SaaS platform
        </p>
      </div>

      <Tabs defaultValue="overview" value={activeTab} onValueChange={setActiveTab}>
        <TabsList className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-5 mb-8">
          {dashboards.map(dashboard => (
            <TabsTrigger key={dashboard.id} value={dashboard.id} className="flex items-center">
              {dashboard.icon}
              {dashboard.title}
            </TabsTrigger>
          ))}
        </TabsList>

        {dashboards.filter(d => d.id !== "logs").map(dashboard => (
          <TabsContent key={dashboard.id} value={dashboard.id}>
            <Card>
              <CardHeader>
                <CardTitle>{dashboard.title}</CardTitle>
                <CardDescription>{dashboard.description}</CardDescription>
              </CardHeader>
              <CardContent>
                <GrafanaEmbed
                  dashboardUrl={dashboard.url}
                  height="700px"
                />
              </CardContent>
            </Card>
          </TabsContent>
        ))}
        
        <TabsContent value="logs">
          <Card>
            <CardHeader>
              <CardTitle>System Logs</CardTitle>
              <CardDescription>
                Real-time logs from all platform services powered by Loki
              </CardDescription>
            </CardHeader>
            <CardContent>
              <LogsViewer 
                height="700px"
                defaultLimit={100}
              />
            </CardContent>
          </Card>
        </TabsContent>
      </Tabs>
    </div>
  );
} 