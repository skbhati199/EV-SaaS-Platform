"use client";

import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { formatDistance } from "date-fns";

// Generate sample transaction data
const generateTransactions = () => {
  const statuses = ["completed", "in-progress", "terminated"];
  const stations = ["Downtown #01", "Midtown #03", "Westside #07", "Plaza #12", "Eastside #04"];
  
  return Array.from({ length: 5 }).map((_, i) => {
    const date = new Date();
    date.setMinutes(date.getMinutes() - (i * 30 + Math.floor(Math.random() * 20)));
    
    const status = statuses[Math.floor(Math.random() * statuses.length)];
    const energyDelivered = status === "in-progress" 
      ? (Math.random() * 15).toFixed(1) 
      : (Math.random() * 30 + 5).toFixed(1);
      
    return {
      id: `TR-${1000 + i}`,
      stationName: stations[Math.floor(Math.random() * stations.length)],
      userId: `U-${10000 + Math.floor(Math.random() * 1000)}`,
      date,
      amount: status === "in-progress" ? null : `$${(Number(energyDelivered) * 0.35).toFixed(2)}`,
      energy: `${energyDelivered} kWh`,
      status,
    };
  });
};

const transactions = generateTransactions();

export function RecentTransactions() {
  return (
    <Card className="col-span-4">
      <CardHeader>
        <CardTitle>Recent Transactions</CardTitle>
        <CardDescription>Latest charging sessions across all stations</CardDescription>
      </CardHeader>
      <CardContent>
        <div className="space-y-4">
          <div className="grid grid-cols-6 text-xs text-muted-foreground font-medium">
            <div className="col-span-2">Station</div>
            <div>User</div>
            <div>Energy</div>
            <div>Amount</div>
            <div>Status</div>
          </div>
          <div className="space-y-4">
            {transactions.map((transaction) => (
              <div key={transaction.id} className="grid grid-cols-6 items-center text-sm">
                <div className="col-span-2">
                  <div className="font-medium">{transaction.stationName}</div>
                  <div className="text-xs text-muted-foreground">
                    {formatDistance(transaction.date, new Date(), { addSuffix: true })}
                  </div>
                </div>
                <div>{transaction.userId}</div>
                <div>{transaction.energy}</div>
                <div>{transaction.amount || "Ongoing"}</div>
                <div>
                  <Badge 
                    variant="outline" 
                    className={
                      transaction.status === "completed" 
                        ? "bg-green-500/10 text-green-600 hover:bg-green-500/20 border-green-500/20" 
                        : transaction.status === "in-progress"
                        ? "bg-blue-500/10 text-blue-600 hover:bg-blue-500/20 border-blue-500/20"
                        : "bg-red-500/10 text-red-600 hover:bg-red-500/20 border-red-500/20"
                    }
                  >
                    {transaction.status === "completed" 
                      ? "Completed" 
                      : transaction.status === "in-progress" 
                      ? "In Progress" 
                      : "Terminated"}
                  </Badge>
                </div>
              </div>
            ))}
          </div>
        </div>
      </CardContent>
      <CardFooter>
        <Button variant="outline" size="sm" className="w-full">View All Transactions</Button>
      </CardFooter>
    </Card>
  );
} 