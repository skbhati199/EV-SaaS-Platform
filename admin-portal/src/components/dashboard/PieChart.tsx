"use client";

import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Cell, Pie, PieChart as RechartsPieChart, ResponsiveContainer, Tooltip } from "recharts";

// Sample connector types data
const connectorData = [
  { name: "Type 2", value: 45, color: "#22c55e" },
  { name: "CCS", value: 30, color: "#16a34a" },
  { name: "CHAdeMO", value: 15, color: "#86efac" },
  { name: "Tesla", value: 10, color: "#4ade80" },
];

export function PieChart() {
  return (
    <Card className="col-span-2">
      <CardHeader>
        <CardTitle>Connector Types</CardTitle>
        <CardDescription>Distribution of connector types across stations</CardDescription>
      </CardHeader>
      <CardContent>
        <div className="h-[300px] flex items-center justify-center">
          <ResponsiveContainer width="100%" height="100%">
            <RechartsPieChart>
              <Pie
                data={connectorData}
                cx="50%"
                cy="50%"
                labelLine={false}
                label={({ name, percent }) => `${name} ${(percent * 100).toFixed(0)}%`}
                outerRadius={100}
                fill="#8884d8"
                dataKey="value"
              >
                {connectorData.map((entry, index) => (
                  <Cell key={`cell-${index}`} fill={entry.color} />
                ))}
              </Pie>
              <Tooltip 
                contentStyle={{ 
                  backgroundColor: "hsl(var(--card))",
                  borderColor: "hsl(var(--border))",
                  borderRadius: "var(--radius)",
                  fontSize: 12,
                }}
                formatter={(value: number) => [`${value}%`, 'Percentage']} 
              />
            </RechartsPieChart>
          </ResponsiveContainer>
        </div>
      </CardContent>
    </Card>
  );
} 