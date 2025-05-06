"use client";

import React from 'react';
import { useRouter } from 'next/navigation';
import { useAuth } from '../context/AuthContext';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { PlusCircle, Activity, Users, Zap, CreditCard, ExternalLink } from "lucide-react";
import { AreaChart } from "@/components/dashboard/AreaChart";
import { BarChart } from "@/components/dashboard/BarChart";
import { PieChart } from "@/components/dashboard/PieChart";
import { StationMap } from "@/components/dashboard/StationMap";
import { RecentTransactions } from "@/components/dashboard/RecentTransactions";
import { RealTimeEventViewer } from "@/components/dashboard/RealTimeEventViewer";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import SystemHealthOverview from '@/app/components/SystemHealthOverview';
import { ServicesOverview } from './services-overview';
import Link from 'next/link';

export default function DashboardPage() {
  const { isAuthenticated, logout } = useAuth();
  const router = useRouter();

  React.useEffect(() => {
    if (!isAuthenticated) {
      // router.push('/login');
    }
  }, [isAuthenticated, router]);

  if (!isAuthenticated) {
    return null;
  }

  return (
    <div className="space-y-6">
      {/* Header section */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <h1 className="text-3xl font-bold tracking-tight">Dashboard</h1>
          <p className="text-muted-foreground">
            Overview of your EV charging network
          </p>
        </div>
        <div className="flex items-center gap-2">
          <Button variant="outline">
            <Activity className="mr-2 h-4 w-4" /> Generate Report
          </Button>
          <Button className="bg-accent hover:bg-accent/90">
            <PlusCircle className="mr-2 h-4 w-4" /> Add Station
          </Button>
        </div>
      </div>

      {/* Tabs */}
      <Tabs defaultValue="overview" className="space-y-4">
        <TabsList>
          <TabsTrigger value="overview">Overview</TabsTrigger>
          <TabsTrigger value="services">Services</TabsTrigger>
          <TabsTrigger value="analytics">Analytics</TabsTrigger>
          <TabsTrigger value="reports">Reports</TabsTrigger>
          <TabsTrigger value="notifications">Notifications</TabsTrigger>
        </TabsList>
        <TabsContent value="overview" className="space-y-6">
          {/* Stats cards */}
          <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
            <Card>
              <CardHeader className="flex flex-row items-center justify-between pb-2 space-y-0">
                <CardTitle className="text-sm font-medium">Total Stations</CardTitle>
                <Zap className="h-4 w-4 text-accent" />
              </CardHeader>
              <CardContent>
                <div className="text-2xl font-bold">152</div>
                <p className="text-xs text-muted-foreground">+12 from last month</p>
              </CardContent>
            </Card>
            <Card>
              <CardHeader className="flex flex-row items-center justify-between pb-2 space-y-0">
                <CardTitle className="text-sm font-medium">Active Sessions</CardTitle>
                <Activity className="h-4 w-4 text-blue-500" />
              </CardHeader>
              <CardContent>
                <div className="text-2xl font-bold">48</div>
                <p className="text-xs text-muted-foreground">+7% from last hour</p>
              </CardContent>
            </Card>
            <Card>
              <CardHeader className="flex flex-row items-center justify-between pb-2 space-y-0">
                <CardTitle className="text-sm font-medium">Total Users</CardTitle>
                <Users className="h-4 w-4 text-violet-500" />
              </CardHeader>
              <CardContent>
                <div className="text-2xl font-bold">2,350</div>
                <p className="text-xs text-muted-foreground">+180 from last month</p>
              </CardContent>
            </Card>
            <Card>
              <CardHeader className="flex flex-row items-center justify-between pb-2 space-y-0">
                <CardTitle className="text-sm font-medium">Revenue</CardTitle>
                <CreditCard className="h-4 w-4 text-emerald-500" />
              </CardHeader>
              <CardContent>
                <div className="text-2xl font-bold">$12,543</div>
                <p className="text-xs text-muted-foreground">+15% from last month</p>
              </CardContent>
            </Card>
          </div>
          
          {/* System status and real-time events */}
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div className="flex flex-col">
              <SystemHealthOverview />
              <div className="mt-2 flex justify-end">
                <Link 
                  href="/dashboard/monitoring" 
                  className="text-xs text-blue-500 hover:text-blue-700 flex items-center"
                >
                  View monitoring dashboard
                  <ExternalLink className="ml-1 h-3 w-3" />
                </Link>
              </div>
            </div>
            
            {/* Real-time Events */}
            <RealTimeEventViewer />
          </div>
          
          {/* Charts section */}
          <div className="grid grid-cols-1 md:grid-cols-7 gap-4">
            <AreaChart />
            <PieChart />
          </div>
          
          <div className="grid grid-cols-1 md:grid-cols-6 gap-4">
            <StationMap />
            <BarChart />
          </div>
          
          {/* Transactions section */}
          <RecentTransactions />
        </TabsContent>

        <TabsContent value="services" className="space-y-4">
          <ServicesOverview />
        </TabsContent>
        
        <TabsContent value="analytics">
          <Card>
            <CardHeader>
              <CardTitle>Advanced Analytics</CardTitle>
              <CardDescription>
                Detailed metrics and trends for your charging network
              </CardDescription>
            </CardHeader>
            <CardContent className="h-[400px] flex items-center justify-center border-t">
              <p className="text-muted-foreground">
                Analytics dashboard will be available in the next release
              </p>
            </CardContent>
          </Card>
        </TabsContent>
        
        <TabsContent value="notifications" className="space-y-4">
          <Card>
            <CardHeader>
              <CardTitle>Notifications</CardTitle>
              <CardDescription>
                System notifications and alerts
              </CardDescription>
            </CardHeader>
            <CardContent>
              <RealTimeEventViewer className="md:col-span-2" />
            </CardContent>
          </Card>
        </TabsContent>
        
        <TabsContent value="reports" className="space-y-4">
          <Card>
            <CardHeader>
              <CardTitle>Reports</CardTitle>
              <CardDescription>
                Generate and download reports for your charging network
              </CardDescription>
            </CardHeader>
            <CardContent className="h-[400px] flex items-center justify-center border-t">
              <p className="text-muted-foreground">
                Reports dashboard will be available in the next release
              </p>
            </CardContent>
          </Card>
        </TabsContent>
      </Tabs>
    </div>
  );
}
