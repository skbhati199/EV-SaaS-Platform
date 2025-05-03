"use client";

import { useTheme } from 'next-themes';
import { useEffect, useState } from 'react';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { 
  Area, 
  AreaChart as RechartsAreaChart, 
  ResponsiveContainer, 
  Tooltip, 
  XAxis, 
  YAxis 
} from "recharts";
import { format, subDays } from "date-fns";

// Generate sample data
const generateData = () => {
  const data = [];
  for (let i = 14; i >= 0; i--) {
    const date = subDays(new Date(), i);
    data.push({
      date: format(date, "MMM dd"),
      kWh: Math.floor(Math.random() * 120) + 30,
    });
  }
  return data;
};

const chartData = generateData();

export function AreaChart() {
  const { theme } = useTheme();
  const [mounted, setMounted] = useState(false);
  
  // Avoid hydration mismatch
  useEffect(() => setMounted(true), []);
  
  if (!mounted) return (
    <Card className="col-span-4">
      <CardHeader>
        <CardTitle>Energy Consumption</CardTitle>
        <CardDescription>Total kWh delivered across all stations</CardDescription>
      </CardHeader>
      <CardContent className="pb-4">
        <div className="h-[300px] flex items-center justify-center">
          <p className="text-muted-foreground">Loading chart...</p>
        </div>
      </CardContent>
    </Card>
  );
  
  // Get appropriate colors based on theme
  const gradientColor = theme === 'dark' ? '#22c55e' : '#22c55e';
  const axisColor = theme === 'dark' ? 'rgba(255, 255, 255, 0.6)' : 'rgba(0, 0, 0, 0.6)';
  
  return (
    <Card className="col-span-4">
      <CardHeader>
        <CardTitle>Energy Consumption</CardTitle>
        <CardDescription>Total kWh delivered across all stations</CardDescription>
      </CardHeader>
      <CardContent className="pb-4">
        <div className="h-[300px]">
          <ResponsiveContainer width="100%" height="100%">
            <RechartsAreaChart
              data={chartData}
              margin={{
                top: 5,
                right: 10,
                left: 0,
                bottom: 0,
              }}
            >
              <defs>
                <linearGradient id="colorKwh" x1="0" y1="0" x2="0" y2="1">
                  <stop 
                    offset="5%" 
                    stopColor={gradientColor}
                    stopOpacity={0.8} 
                  />
                  <stop 
                    offset="95%" 
                    stopColor={gradientColor}
                    stopOpacity={0} 
                  />
                </linearGradient>
              </defs>
              <XAxis 
                dataKey="date" 
                tickLine={false}
                axisLine={false}
                tick={{ fontSize: 12, fill: axisColor }}
                tickMargin={10}
              />
              <YAxis 
                tickLine={false}
                axisLine={false}
                tick={{ fontSize: 12, fill: axisColor }}
                tickMargin={10}
                unit="kWh"
              />
              <Tooltip 
                contentStyle={{ 
                  backgroundColor: "hsl(var(--card))",
                  borderColor: "hsl(var(--border))",
                  borderRadius: "var(--radius)",
                  fontSize: 12,
                  color: "hsl(var(--foreground))",
                }}
                formatter={(value: number) => [`${value} kWh`, 'Energy']} 
              />
              <Area
                type="monotone"
                dataKey="kWh"
                stroke={gradientColor}
                strokeWidth={2}
                fillOpacity={1}
                fill="url(#colorKwh)"
              />
            </RechartsAreaChart>
          </ResponsiveContainer>
        </div>
      </CardContent>
    </Card>
  );
} 