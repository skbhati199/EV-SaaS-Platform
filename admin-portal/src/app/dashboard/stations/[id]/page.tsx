'use client';

import { useState, useEffect, useCallback } from 'react';
import Link from 'next/link';
import { useRouter } from 'next/navigation';
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { 
  AlertTriangle, 
  ArrowLeft, 
  Battery, 
  BatteryCharging, 
  BatteryWarning, 
  Calendar, 
  CheckCircle2, 
  Clock, 
  Edit, 
  ExternalLink, 
  MapPin, 
  MoreHorizontal, 
  Power, 
  RefreshCw, 
  Settings, 
  Wifi, 
  WifiOff, 
  ZapOff 
} from "lucide-react";
import { stationService, Station, Connector } from "@/app/services/stationService";

interface StationDetailProps {
  params: {
    id: string;
  };
}

export default function StationDetailPage({ params }: StationDetailProps) {
  const { id } = params;
  const router = useRouter();
  const [station, setStation] = useState<Station | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [activeTab, setActiveTab] = useState("overview");
  const [isRebooting, setIsRebooting] = useState(false);

  const fetchStationDetails = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await stationService.getStation(id);
      setStation(data);
    } catch (err) {
      console.error("Failed to fetch station details:", err);
      setError("Failed to load station details. Please try again.");
    } finally {
      setLoading(false);
    }
  }, [id]);

  useEffect(() => {
    fetchStationDetails();
  }, [id, fetchStationDetails]);

  const handleRebootStation = async () => {
    if (!station) return;
    
    setIsRebooting(true);
    try {
      await stationService.sendCommand(id, "reboot");
      alert(`Station ${station.name} is rebooting. This may take a few minutes.`);
      // Refresh data after 10 seconds to check for status changes
      setTimeout(() => {
        fetchStationDetails();
      }, 10000);
    } catch (err) {
      console.error("Failed to reboot station:", err);
      alert("Failed to reboot station. Please try again.");
    } finally {
      setIsRebooting(false);
    }
  };

  const handleResetConnector = async (connectorId: string) => {
    if (!station) return;
    
    try {
      await stationService.sendCommand(id, "resetConnector", { connectorId });
      alert(`Connector ${connectorId} is being reset.`);
      // Refresh data after a few seconds
      setTimeout(() => {
        fetchStationDetails();
      }, 5000);
    } catch (err) {
      console.error("Failed to reset connector:", err);
      alert("Failed to reset connector. Please try again.");
    }
  };

  const getConnectorStatusIcon = (status: string) => {
    switch (status) {
      case 'AVAILABLE':
        return <CheckCircle2 className="h-5 w-5 text-green-500" />;
      case 'OCCUPIED':
        return <BatteryCharging className="h-5 w-5 text-blue-500" />;
      case 'RESERVED':
        return <Clock className="h-5 w-5 text-orange-500" />;
      case 'UNAVAILABLE':
        return <ZapOff className="h-5 w-5 text-gray-500" />;
      case 'FAULTED':
        return <AlertTriangle className="h-5 w-5 text-red-500" />;
      default:
        return <BatteryWarning className="h-5 w-5 text-yellow-500" />;
    }
  };

  const getConnectorStatusBadge = (status: string) => {
    switch (status) {
      case 'AVAILABLE':
        return <Badge className="bg-green-500">Available</Badge>;
      case 'OCCUPIED':
        return <Badge className="bg-blue-500">Charging</Badge>;
      case 'RESERVED':
        return <Badge className="bg-orange-500">Reserved</Badge>;
      case 'UNAVAILABLE':
        return <Badge variant="outline">Unavailable</Badge>;
      case 'FAULTED':
        return <Badge variant="destructive">Faulted</Badge>;
      default:
        return <Badge variant="secondary">{status}</Badge>;
    }
  };

  const getStationStatusIcon = (status: string) => {
    switch (status) {
      case 'ONLINE':
        return <Wifi className="h-5 w-5 text-green-500" />;
      case 'OFFLINE':
        return <WifiOff className="h-5 w-5 text-red-500" />;
      case 'PARTIALLY_AVAILABLE':
        return <AlertTriangle className="h-5 w-5 text-yellow-500" />;
      default:
        return <AlertTriangle className="h-5 w-5 text-yellow-500" />;
    }
  };

  if (loading) {
    return (
      <div className="flex-1 flex items-center justify-center">
        <div className="flex flex-col items-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-accent"></div>
          <p className="mt-4 text-muted-foreground">Loading station details...</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="flex-1 flex items-center justify-center">
        <div className="text-center">
          <AlertTriangle className="h-12 w-12 text-red-500 mx-auto mb-4" />
          <p className="text-xl font-semibold mb-2">Error Loading Station</p>
          <p className="text-muted-foreground mb-4">{error}</p>
          <div className="flex justify-center gap-4">
            <Button variant="outline" onClick={fetchStationDetails}>
              <RefreshCw className="mr-2 h-4 w-4" /> Try Again
            </Button>
            <Button onClick={() => router.push('/dashboard/stations')}>
              <ArrowLeft className="mr-2 h-4 w-4" /> Back to Stations
            </Button>
          </div>
        </div>
      </div>
    );
  }

  if (!station) {
    return (
      <div className="flex-1 flex items-center justify-center">
        <div className="text-center">
          <AlertTriangle className="h-12 w-12 text-yellow-500 mx-auto mb-4" />
          <p className="text-xl font-semibold mb-2">Station Not Found</p>
          <p className="text-muted-foreground mb-4">We couldn't find a station with the ID: {id}</p>
          <Button onClick={() => router.push('/dashboard/stations')}>
            <ArrowLeft className="mr-2 h-4 w-4" /> Back to Stations
          </Button>
        </div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col md:flex-row justify-between gap-4">
        <div>
          <div className="flex items-center gap-2 mb-1">
            <Button 
              variant="ghost" 
              size="sm" 
              className="gap-1 px-0 hover:bg-transparent"
              onClick={() => router.push('/dashboard/stations')}
            >
              <ArrowLeft className="h-4 w-4" />
              <span>Back to Stations</span>
            </Button>
          </div>
          <div className="flex items-center gap-2">
            <h1 className="text-3xl font-bold tracking-tight">{station.name}</h1>
            {getStationStatusIcon(station.status)}
          </div>
          <div className="flex items-center text-muted-foreground gap-1">
            <MapPin className="h-4 w-4" />
            <span>{station.location.address}, {station.location.city}</span>
          </div>
        </div>
        <div className="flex items-center gap-2">
          <Button variant="outline" onClick={fetchStationDetails}>
            <RefreshCw className="mr-2 h-4 w-4" /> Refresh
          </Button>
          <Button 
            variant="outline" 
            onClick={() => router.push(`/dashboard/stations/${id}/edit`)}
          >
            <Edit className="mr-2 h-4 w-4" /> Edit
          </Button>
          <Button 
            variant={station.status === "ONLINE" ? "default" : "secondary"}
            className={station.status === "ONLINE" ? "bg-accent hover:bg-accent/90" : ""}
            onClick={handleRebootStation}
            disabled={isRebooting || station.status === "OFFLINE"}
          >
            <Power className="mr-2 h-4 w-4" /> {isRebooting ? "Rebooting..." : "Reboot"}
          </Button>
        </div>
      </div>

      {/* Tabs */}
      <Tabs defaultValue="overview" className="space-y-4" value={activeTab} onValueChange={setActiveTab}>
        <TabsList>
          <TabsTrigger value="overview">Overview</TabsTrigger>
          <TabsTrigger value="connectors">Connectors</TabsTrigger>
          <TabsTrigger value="settings">Settings</TabsTrigger>
          <TabsTrigger value="logs">Logs</TabsTrigger>
        </TabsList>
        
        <TabsContent value="overview" className="space-y-4">
          <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
            <Card>
              <CardHeader className="pb-2">
                <CardTitle className="text-sm font-medium">Status</CardTitle>
              </CardHeader>
              <CardContent>
                <div className="flex items-center justify-between">
                  <div className="font-semibold text-2xl">
                    {station.status === "ONLINE" ? "Online" : 
                     station.status === "OFFLINE" ? "Offline" : "Partially Available"}
                  </div>
                  {getStationStatusIcon(station.status)}
                </div>
                <p className="text-xs text-muted-foreground mt-1">
                  Last connection: {station.lastConnected ? new Date(station.lastConnected).toLocaleString() : "Never"}
                </p>
              </CardContent>
            </Card>
            
            <Card>
              <CardHeader className="pb-2">
                <CardTitle className="text-sm font-medium">Hardware</CardTitle>
              </CardHeader>
              <CardContent>
                <div className="space-y-2">
                  <div className="flex justify-between">
                    <span className="text-muted-foreground">Vendor</span>
                    <span className="font-medium">{station.vendor}</span>
                  </div>
                  <div className="flex justify-between">
                    <span className="text-muted-foreground">Model</span>
                    <span className="font-medium">{station.model}</span>
                  </div>
                  <div className="flex justify-between">
                    <span className="text-muted-foreground">Serial Number</span>
                    <span className="font-medium">{station.serialNumber || "N/A"}</span>
                  </div>
                </div>
              </CardContent>
            </Card>
            
            <Card>
              <CardHeader className="pb-2">
                <CardTitle className="text-sm font-medium">Software</CardTitle>
              </CardHeader>
              <CardContent>
                <div className="space-y-2">
                  <div className="flex justify-between">
                    <span className="text-muted-foreground">Firmware</span>
                    <span className="font-medium">{station.firmwareVersion || "N/A"}</span>
                  </div>
                  <div className="flex justify-between">
                    <span className="text-muted-foreground">Last Heartbeat</span>
                    <span className="font-medium">
                      {station.lastHeartbeat ? new Date(station.lastHeartbeat).toLocaleString() : "Never"}
                    </span>
                  </div>
                </div>
              </CardContent>
            </Card>
          </div>
          
          <Card>
            <CardHeader>
              <CardTitle>Connector Status</CardTitle>
              <CardDescription>Overview of all connectors and their current status</CardDescription>
            </CardHeader>
            <CardContent>
              <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
                {station.connectors.map((connector) => (
                  <div 
                    key={connector.id} 
                    className="flex items-center p-3 border rounded-lg hover:bg-muted/50 cursor-pointer"
                    onClick={() => setActiveTab("connectors")}
                  >
                    <div className="mr-3">
                      {getConnectorStatusIcon(connector.status)}
                    </div>
                    <div className="flex-1">
                      <div className="font-medium">
                        {connector.type} - {connector.maxPower}kW
                      </div>
                      <div className="flex items-center gap-2">
                        {getConnectorStatusBadge(connector.status)}
                        <span className="text-xs text-muted-foreground">
                          {connector.lastStatusUpdate ? new Date(connector.lastStatusUpdate).toLocaleString() : "No updates"}
                        </span>
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            </CardContent>
          </Card>
        </TabsContent>
        
        <TabsContent value="connectors" className="space-y-4">
          <Card>
            <CardHeader>
              <CardTitle>Connector Management</CardTitle>
              <CardDescription>Control and monitor individual connectors</CardDescription>
            </CardHeader>
            <CardContent>
              <div className="divide-y">
                {station.connectors.map((connector) => (
                  <div key={connector.id} className="py-4 first:pt-0 last:pb-0">
                    <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
                      <div className="flex items-center gap-3">
                        {getConnectorStatusIcon(connector.status)}
                        <div>
                          <h3 className="font-medium">Connector {connector.id}</h3>
                          <p className="text-sm text-muted-foreground">{connector.type} - {connector.maxPower}kW</p>
                        </div>
                        {getConnectorStatusBadge(connector.status)}
                      </div>
                      <div className="flex items-center gap-2">
                        <Button 
                          variant="outline" 
                          size="sm"
                          onClick={() => handleResetConnector(connector.id)}
                          disabled={station.status === "OFFLINE"}
                        >
                          Reset
                        </Button>
                        <Button 
                          variant="outline" 
                          size="sm"
                          disabled={station.status === "OFFLINE" || connector.status === "OCCUPIED"}
                        >
                          {connector.status === "AVAILABLE" ? "Disable" : "Enable"}
                        </Button>
                        <Button 
                          variant="outline" 
                          size="sm"
                          disabled={station.status === "OFFLINE" || connector.status !== "OCCUPIED"}
                        >
                          Stop Session
                        </Button>
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            </CardContent>
          </Card>
        </TabsContent>
        
        <TabsContent value="settings" className="space-y-4">
          <Card>
            <CardHeader>
              <CardTitle>Station Settings</CardTitle>
              <CardDescription>Configure station parameters</CardDescription>
            </CardHeader>
            <CardContent>
              <div className="flex justify-center items-center p-12">
                <div className="text-center">
                  <Settings className="h-12 w-12 mx-auto text-muted-foreground mb-4" />
                  <h3 className="text-lg font-medium">Settings Coming Soon</h3>
                  <p className="text-muted-foreground">
                    Advanced station configuration will be available in a future update.
                  </p>
                </div>
              </div>
            </CardContent>
          </Card>
        </TabsContent>
        
        <TabsContent value="logs" className="space-y-4">
          <Card>
            <CardHeader>
              <CardTitle>Station Logs</CardTitle>
              <CardDescription>View station event log</CardDescription>
            </CardHeader>
            <CardContent>
              <div className="flex justify-center items-center p-12">
                <div className="text-center">
                  <Calendar className="h-12 w-12 mx-auto text-muted-foreground mb-4" />
                  <h3 className="text-lg font-medium">Logs Coming Soon</h3>
                  <p className="text-muted-foreground">
                    Station logs and events will be available in a future update.
                  </p>
                </div>
              </div>
            </CardContent>
          </Card>
        </TabsContent>
      </Tabs>
    </div>
  );
}
