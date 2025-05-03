"use client";

import React from 'react';
import { useRouter } from 'next/navigation';
import { useAuth } from '../context/AuthContext';
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { PlusCircle, Activity, Users, Zap, CreditCard } from "lucide-react";

export default function DashboardPage() {
  const { isAuthenticated, logout } = useAuth();
  const router = useRouter();

  React.useEffect(() => {
    if (!isAuthenticated) {
      router.push('/login');
    }
  }, [isAuthenticated, router]);

  if (!isAuthenticated) {
    return null;
  }

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <h1 className="text-3xl font-bold tracking-tight">Dashboard</h1>
        <Button variant="default" className="bg-accent hover:bg-accent/90">
          <PlusCircle className="mr-2 h-4 w-4" /> Add Station
        </Button>
      </div>

      <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-4">
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
            <Activity className="h-4 w-4 text-accent" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">48</div>
            <p className="text-xs text-muted-foreground">+7% from last hour</p>
          </CardContent>
        </Card>
        <Card>
          <CardHeader className="flex flex-row items-center justify-between pb-2 space-y-0">
            <CardTitle className="text-sm font-medium">Total Users</CardTitle>
            <Users className="h-4 w-4 text-accent" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">2,350</div>
            <p className="text-xs text-muted-foreground">+180 from last month</p>
          </CardContent>
        </Card>
        <Card>
          <CardHeader className="flex flex-row items-center justify-between pb-2 space-y-0">
            <CardTitle className="text-sm font-medium">Revenue</CardTitle>
            <CreditCard className="h-4 w-4 text-accent" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">$12,543</div>
            <p className="text-xs text-muted-foreground">+15% from last month</p>
          </CardContent>
        </Card>
      </div>

      <div className="grid gap-6 md:grid-cols-2">
        <Card>
          <CardHeader>
            <CardTitle>Recent Activity</CardTitle>
            <CardDescription>Latest charging sessions</CardDescription>
          </CardHeader>
          <CardContent>
            <div className="space-y-4">
              {[1, 2, 3].map((i) => (
                <div key={i} className="flex items-center gap-4">
                  <div className="w-2 h-2 rounded-full bg-accent"></div>
                  <div className="flex-1">
                    <p className="text-sm font-medium">Session #{1000 + i}</p>
                    <p className="text-xs text-muted-foreground">Station #ST-{42 + i} • 23 kWh • $12.45</p>
                  </div>
                  <div className="text-xs text-muted-foreground">
                    {i === 1 ? '2 min ago' : i === 2 ? '1 hour ago' : '3 hours ago'}
                  </div>
                </div>
              ))}
            </div>
          </CardContent>
          <CardFooter>
            <Button variant="outline" size="sm" className="w-full">View All Activity</Button>
          </CardFooter>
        </Card>
        <Card>
          <CardHeader>
            <CardTitle>Station Status</CardTitle>
            <CardDescription>Current station availability</CardDescription>
          </CardHeader>
          <CardContent>
            <div className="space-y-4">
              <div className="flex items-center gap-2">
                <div className="w-2 h-2 rounded-full bg-green-500"></div>
                <div className="text-sm">Available</div>
                <div className="text-sm font-semibold ml-auto">98</div>
              </div>
              <div className="flex items-center gap-2">
                <div className="w-2 h-2 rounded-full bg-blue-500"></div>
                <div className="text-sm">Charging</div>
                <div className="text-sm font-semibold ml-auto">48</div>
              </div>
              <div className="flex items-center gap-2">
                <div className="w-2 h-2 rounded-full bg-yellow-500"></div>
                <div className="text-sm">Reserved</div>
                <div className="text-sm font-semibold ml-auto">3</div>
              </div>
              <div className="flex items-center gap-2">
                <div className="w-2 h-2 rounded-full bg-red-500"></div>
                <div className="text-sm">Offline</div>
                <div className="text-sm font-semibold ml-auto">3</div>
              </div>
            </div>
          </CardContent>
          <CardFooter>
            <Button variant="outline" size="sm" className="w-full">View All Stations</Button>
          </CardFooter>
        </Card>
      </div>
    </div>
  );
}
