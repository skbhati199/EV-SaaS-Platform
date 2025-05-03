"use client";

import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { MapPin, ZoomIn } from "lucide-react";

export function StationMap() {
  return (
    <Card className="col-span-3">
      <CardHeader>
        <div className="flex justify-between items-center">
          <div>
            <CardTitle>Station Locations</CardTitle>
            <CardDescription>Geographic distribution of charging stations</CardDescription>
          </div>
          <Button variant="outline" size="sm" className="h-8 gap-1">
            <ZoomIn className="h-3.5 w-3.5" />
            <span>Full View</span>
          </Button>
        </div>
      </CardHeader>
      <CardContent>
        <div className="h-[300px] rounded-md bg-muted flex items-center justify-center relative overflow-hidden">
          {/* Map placeholder - in production, this would be replaced with a real map component */}
          <div className="absolute inset-0 bg-green-50 dark:bg-green-950/20">
            <div className="absolute w-full h-full" style={{ backgroundImage: "url(\"data:image/svg+xml,%3Csvg width='60' height='60' viewBox='0 0 60 60' xmlns='http://www.w3.org/2000/svg'%3E%3Cg fill='none' fill-rule='evenodd'%3E%3Cg fill='%2322c55e' fill-opacity='0.1'%3E%3Cpath d='M36 34v-4h-2v4h-4v2h4v4h2v-4h4v-2h-4zm0-30V0h-2v4h-4v2h4v4h2V6h4V4h-4zM6 34v-4H4v4H0v2h4v4h2v-4h4v-2H6zM6 4V0H4v4H0v2h4v4h2V6h4V4H6z'/%3E%3C/g%3E%3C/g%3E%3C/svg%3E\")" }}></div>
          </div>
          
          {/* Sample station markers */}
          {[
            { id: 1, x: '20%', y: '30%', name: 'Downtown Station' },
            { id: 2, x: '50%', y: '20%', name: 'Midtown Hub' },
            { id: 3, x: '70%', y: '60%', name: 'Eastside Plaza' },
            { id: 4, x: '40%', y: '70%', name: 'South Mall' },
            { id: 5, x: '80%', y: '40%', name: 'Business District' },
          ].map((station) => (
            <div 
              key={station.id}
              className="absolute bg-accent text-white p-1 rounded-full cursor-pointer transform hover:scale-110 transition-transform"
              style={{ left: station.x, top: station.y }}
              title={station.name}
            >
              <MapPin className="h-5 w-5" />
            </div>
          ))}
          
          <div className="text-center text-muted-foreground">
            <p>Interactive map will be integrated here</p>
            <p className="text-xs mt-1">Showing 5 of 152 stations</p>
          </div>
        </div>
      </CardContent>
    </Card>
  );
} 