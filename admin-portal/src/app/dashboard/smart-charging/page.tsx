'use client';

import React from 'react';
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import PowerControlPanel from '@/app/components/PowerControlPanel';
import { GanttChartSquare, BarChart4, Settings, Grid, Zap } from "lucide-react";
import GrafanaEmbed from '@/app/components/GrafanaEmbed';

export default function SmartChargingPage() {
  return (
    <div className="container space-y-6">
      <div className="mb-8">
        <h1 className="text-3xl font-bold mb-2">Smart Charging</h1>
        <p className="text-gray-600">Configure load balancing, dynamic pricing, and monitor grid interface</p>
      </div>
      
      <Tabs defaultValue="power-control" className="space-y-4">
        <TabsList className="grid grid-cols-2 md:grid-cols-5 mb-4">
          <TabsTrigger value="power-control" className="flex items-center">
            <Zap className="mr-2 h-4 w-4" />
            Power Control
          </TabsTrigger>
          <TabsTrigger value="load-balancing" className="flex items-center">
            <GanttChartSquare className="mr-2 h-4 w-4" />
            Load Balancing
          </TabsTrigger>
          <TabsTrigger value="dynamic-pricing" className="flex items-center">
            <BarChart4 className="mr-2 h-4 w-4" />
            Dynamic Pricing
          </TabsTrigger>
          <TabsTrigger value="grid-interface" className="flex items-center">
            <Grid className="mr-2 h-4 w-4" />
            Grid Interface
          </TabsTrigger>
          <TabsTrigger value="settings" className="flex items-center">
            <Settings className="mr-2 h-4 w-4" />
            Settings
          </TabsTrigger>
        </TabsList>
        
        <TabsContent value="power-control">
          <div className="grid grid-cols-1 gap-6">
            <PowerControlPanel />
            
            <Card>
              <CardHeader>
                <CardTitle>Real-time Power Distribution</CardTitle>
                <CardDescription>
                  Current power allocation across all charging stations
                </CardDescription>
              </CardHeader>
              <CardContent>
                <GrafanaEmbed 
                  dashboardUrl="/d/smart-charging-power/power-distribution?orgId=1&refresh=5s&theme=light" 
                  height="400px"
                />
              </CardContent>
            </Card>
          </div>
        </TabsContent>
        
        <TabsContent value="load-balancing">
          <Card>
            <CardHeader>
              <CardTitle>Load Balancing Configuration</CardTitle>
              <CardDescription>
                Configure load balancing algorithms and settings
              </CardDescription>
            </CardHeader>
            <CardContent className="h-[400px] flex items-center justify-center">
              <p className="text-gray-500">Load balancing configuration will be available in the next release</p>
            </CardContent>
          </Card>
        </TabsContent>
        
        <TabsContent value="dynamic-pricing">
          <Card>
            <CardHeader>
              <CardTitle>Dynamic Pricing</CardTitle>
              <CardDescription>
                Configure dynamic pricing rules based on grid load, time-of-use, and energy costs
              </CardDescription>
            </CardHeader>
            <CardContent className="h-[400px] flex items-center justify-center">
              <p className="text-gray-500">Dynamic pricing configuration will be available in the next release</p>
            </CardContent>
          </Card>
        </TabsContent>
        
        <TabsContent value="grid-interface">
          <Card>
            <CardHeader>
              <CardTitle>Grid Interface</CardTitle>
              <CardDescription>
                Configure integration with grid management systems
              </CardDescription>
            </CardHeader>
            <CardContent className="h-[400px] flex items-center justify-center">
              <p className="text-gray-500">Grid interface configuration will be available in the next release</p>
            </CardContent>
          </Card>
        </TabsContent>
        
        <TabsContent value="settings">
          <Card>
            <CardHeader>
              <CardTitle>Smart Charging Settings</CardTitle>
              <CardDescription>
                Configure general smart charging settings
              </CardDescription>
            </CardHeader>
            <CardContent className="h-[400px] flex items-center justify-center">
              <p className="text-gray-500">Smart charging settings will be available in the next release</p>
            </CardContent>
          </Card>
        </TabsContent>
      </Tabs>
    </div>
  );
} 