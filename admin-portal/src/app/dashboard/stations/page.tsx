"use client";

import React, { useEffect, useState, useCallback } from "react";
import { useRouter } from "next/navigation";
import { 
  Card, 
  CardContent, 
  CardHeader, 
  CardTitle 
} from "@/components/ui/card";
import { 
  Table, 
  TableBody, 
  TableCell, 
  TableHead, 
  TableHeader, 
  TableRow 
} from "@/components/ui/table";
import { 
  DropdownMenu, 
  DropdownMenuContent, 
  DropdownMenuItem, 
  DropdownMenuTrigger 
} from "@/components/ui/dropdown-menu";
import { 
  Select, 
  SelectContent, 
  SelectItem, 
  SelectTrigger, 
  SelectValue 
} from "@/components/ui/select";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { stationService, type Station, type StationFilter } from "@/app/services/stationService";
import { 
  ChevronRight, 
  MoreVertical, 
  Plus, 
  RefreshCw, 
  Search, 
  Zap, 
  Filter 
} from "lucide-react";

export default function StationsPage() {
  const router = useRouter();
  const [stations, setStations] = useState<Station[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [totalStations, setTotalStations] = useState(0);
  const [currentPage, setCurrentPage] = useState(1);
  const [filters, setFilters] = useState<StationFilter>({
    page: 0,
    limit: 10,
  });

  const fetchStations = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await stationService.getStations({
        ...filters,
        page: currentPage - 1, // API uses 0-based indexing
      });
      setStations(response.stations);
      setTotalStations(response.total);
    } catch (err) {
      console.error("Failed to fetch stations:", err);
      setError("Failed to load stations. Please try again.");
    } finally {
      setLoading(false);
    }
  }, [currentPage, filters]);

  useEffect(() => {
    fetchStations();
  }, [fetchStations]);

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault();
    fetchStations();
  };

  const handleStatusChange = (status: string) => {
    setFilters({ 
      ...filters, 
      status: status === "all" ? undefined : status 
    });
    setCurrentPage(1);
  };

  const handleLocationChange = (location: string) => {
    setFilters({ 
      ...filters, 
      location: location === "all" ? undefined : location 
    });
    setCurrentPage(1);
  };

  const handleSearchChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setFilters({ ...filters, search: e.target.value });
  };

  const viewStationDetails = (id: string) => {
    router.push(`/dashboard/stations/${id}`);
  };

  const getStatusBadge = (status: string) => {
    switch (status) {
      case "ONLINE":
        return <Badge className="bg-green-500">Online</Badge>;
      case "OFFLINE":
        return <Badge variant="destructive">Offline</Badge>;
      case "PARTIALLY_AVAILABLE":
        return <Badge variant="outline" className="bg-yellow-100 text-yellow-800 border-yellow-300">Partially Available</Badge>;
      default:
        return <Badge variant="secondary">{status}</Badge>;
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <h1 className="text-3xl font-bold tracking-tight">Stations Management</h1>
          <p className="text-muted-foreground">
            Manage and monitor all EV charging stations
          </p>
        </div>
        <div className="flex items-center gap-2">
          <Button variant="outline" onClick={fetchStations}>
            <RefreshCw className="mr-2 h-4 w-4" /> Refresh
          </Button>
          <Button className="bg-accent hover:bg-accent/90" onClick={() => router.push("/dashboard/stations/create")}>
            <Plus className="mr-2 h-4 w-4" /> Add Station
          </Button>
        </div>
      </div>

      <Card>
        <CardHeader className="pb-2">
          <CardTitle>Charging Stations</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="flex flex-col md:flex-row gap-4 mb-6">
            <form onSubmit={handleSearch} className="flex-1 flex gap-2">
              <div className="relative flex-1">
                <Search className="absolute left-2.5 top-2.5 h-4 w-4 text-muted-foreground" />
                <Input
                  type="search"
                  placeholder="Search by name or serial number..."
                  className="pl-8"
                  value={filters.search || ""}
                  onChange={handleSearchChange}
                />
              </div>
              <Button type="submit">Search</Button>
            </form>
            <div className="flex gap-2">
              <Select onValueChange={handleStatusChange} defaultValue="all">
                <SelectTrigger className="w-[180px]">
                  <Filter className="h-4 w-4 mr-2" />
                  <SelectValue placeholder="Filter by status" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="all">All statuses</SelectItem>
                  <SelectItem value="ONLINE">Online</SelectItem>
                  <SelectItem value="OFFLINE">Offline</SelectItem>
                  <SelectItem value="PARTIALLY_AVAILABLE">Partially Available</SelectItem>
                </SelectContent>
              </Select>

              <Select onValueChange={handleLocationChange} defaultValue="all">
                <SelectTrigger className="w-[180px]">
                  <Filter className="h-4 w-4 mr-2" />
                  <SelectValue placeholder="Filter by location" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="all">All locations</SelectItem>
                  <SelectItem value="San Francisco">San Francisco</SelectItem>
                  <SelectItem value="New York">New York</SelectItem>
                  <SelectItem value="Los Angeles">Los Angeles</SelectItem>
                </SelectContent>
              </Select>
            </div>
          </div>

          {error && (
            <div className="bg-red-50 dark:bg-red-900/30 border-l-4 border-red-500 p-4 mb-6 text-red-700 dark:text-red-400">
              <p>{error}</p>
            </div>
          )}

          {loading ? (
            <div className="flex justify-center items-center py-12">
              <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-accent"></div>
            </div>
          ) : stations.length === 0 ? (
            <div className="text-center py-12">
              <Zap className="h-12 w-12 mx-auto text-muted-foreground mb-4" />
              <h3 className="text-lg font-medium">No stations found</h3>
              <p className="text-muted-foreground mb-4">There are no stations matching your criteria.</p>
              <Button onClick={() => setFilters({ page: 0, limit: 10 })}>Clear filters</Button>
            </div>
          ) : (
            <div className="rounded-md border">
              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead>Name</TableHead>
                    <TableHead>Status</TableHead>
                    <TableHead>Location</TableHead>
                    <TableHead>Connectors</TableHead>
                    <TableHead>Last Connection</TableHead>
                    <TableHead className="text-right">Actions</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {stations.map((station) => (
                    <TableRow 
                      key={station.id} 
                      className="cursor-pointer hover:bg-muted/50"
                      onClick={() => viewStationDetails(station.id)}
                    >
                      <TableCell className="font-medium">
                        <div>{station.name}</div>
                        <div className="text-xs text-muted-foreground">
                          {station.serialNumber || 'No serial number'}
                        </div>
                      </TableCell>
                      <TableCell>{getStatusBadge(station.status)}</TableCell>
                      <TableCell>
                        <div>{station.location.city}</div>
                        <div className="text-xs text-muted-foreground truncate max-w-[200px]">
                          {station.location.address}
                        </div>
                      </TableCell>
                      <TableCell>
                        <div className="flex gap-1">
                          {station.connectors.map((connector) => (
                            <Badge key={connector.id} variant="outline" className="text-xs">
                              {connector.type} ({connector.status.charAt(0)})
                            </Badge>
                          ))}
                        </div>
                      </TableCell>
                      <TableCell>
                        {station.lastConnected ? (
                          new Date(station.lastConnected).toLocaleString()
                        ) : (
                          <span className="text-muted-foreground">Never</span>
                        )}
                      </TableCell>
                      <TableCell className="text-right">
                        <DropdownMenu>
                          <DropdownMenuTrigger asChild onClick={(e) => e.stopPropagation()}>
                            <Button variant="ghost" className="h-8 w-8 p-0">
                              <MoreVertical className="h-4 w-4" />
                            </Button>
                          </DropdownMenuTrigger>
                          <DropdownMenuContent align="end">
                            <DropdownMenuItem onClick={(e) => {
                              e.stopPropagation();
                              router.push(`/dashboard/stations/${station.id}`);
                            }}>
                              View details
                            </DropdownMenuItem>
                            <DropdownMenuItem onClick={(e) => {
                              e.stopPropagation();
                              router.push(`/dashboard/stations/${station.id}/edit`);
                            }}>
                              Edit
                            </DropdownMenuItem>
                            <DropdownMenuItem onClick={(e) => {
                              e.stopPropagation();
                              // Add reboot station functionality
                              stationService.sendCommand(station.id, "reboot")
                                .then(() => alert(`Station ${station.name} is rebooting`))
                                .catch(err => console.error("Failed to reboot station:", err));
                            }}>
                              Reboot
                            </DropdownMenuItem>
                          </DropdownMenuContent>
                        </DropdownMenu>
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </div>
          )}

          <div className="flex items-center justify-between mt-4">
            <div className="text-sm text-muted-foreground">
              Showing {stations.length} of {totalStations} stations
            </div>
            <div className="flex items-center space-x-2">
              <Button
                variant="outline"
                size="sm"
                disabled={currentPage === 1}
                onClick={() => setCurrentPage(currentPage - 1)}
              >
                Previous
              </Button>
              <Button
                variant="outline"
                size="sm"
                disabled={currentPage * filters.limit! >= totalStations}
                onClick={() => setCurrentPage(currentPage + 1)}
              >
                Next
              </Button>
            </div>
          </div>
        </CardContent>
      </Card>
    </div>
  );
} 