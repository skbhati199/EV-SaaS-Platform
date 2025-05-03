"use client";

import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { 
  Bar, 
  BarChart as RechartsBarChart, 
  ResponsiveContainer, 
  Tooltip, 
  XAxis, 
  YAxis 
} from "recharts";

// Sample station usage data
const stationData = [
  { name: "Station #1", sessions: 32 },
  { name: "Station #2", sessions: 27 },
  { name: "Station #3", sessions: 45 },
  { name: "Station #4", sessions: 18 },
  { name: "Station #5", sessions: 38 },
  { name: "Station #6", sessions: 24 },
];

export function BarChart() {
  return (
    <Card className="col-span-3">
      <CardHeader>
        <CardTitle>Station Usage</CardTitle>
        <CardDescription>Number of charging sessions per station</CardDescription>
      </CardHeader>
      <CardContent className="pb-4">
        <div className="h-[300px]">
          <ResponsiveContainer width="100%" height="100%">
            <RechartsBarChart
              data={stationData}
              margin={{
                top: 5,
                right: 10,
                left: 0,
                bottom: 25,
              }}
            >
              <XAxis 
                dataKey="name" 
                tickLine={false}
                axisLine={false}
                tick={{ fontSize: 12 }}
                angle={-45}
                textAnchor="end"
                interval={0}
              />
              <YAxis
                tickLine={false}
                axisLine={false}
                tick={{ fontSize: 12 }}
                tickMargin={10}
              />
              <Tooltip
                contentStyle={{ 
                  backgroundColor: "hsl(var(--card))",
                  borderColor: "hsl(var(--border))",
                  borderRadius: "var(--radius)",
                  fontSize: 12,
                }}
                formatter={(value: number) => [`${value} sessions`, 'Usage']}
              />
              <Bar 
                dataKey="sessions" 
                fill="#22c55e" 
                radius={[4, 4, 0, 0]}
              />
            </RechartsBarChart>
          </ResponsiveContainer>
        </div>
      </CardContent>
    </Card>
  );
} 