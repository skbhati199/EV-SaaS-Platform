"use client";

import React, { useState } from 'react';
import { Card, CardContent, CardDescription, CardHeader, CardTitle, CardFooter } from "@/components/ui/card";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { Button } from "@/components/ui/button";
import { 
  Table, 
  TableBody, 
  TableCell, 
  TableHead, 
  TableHeader, 
  TableRow 
} from "@/components/ui/table";
import { 
  Select, 
  SelectContent, 
  SelectItem, 
  SelectTrigger, 
  SelectValue 
} from "@/components/ui/select";
import { Input } from "@/components/ui/input";
import { Badge } from "@/components/ui/badge";
import { 
  Search, 
  Plus, 
  Globe, 
  RefreshCw, 
  Zap, 
  Activity, 
  Upload, 
  Download, 
  MoreHorizontal 
} from "lucide-react";
import { 
  DropdownMenu, 
  DropdownMenuContent, 
  DropdownMenuItem, 
  DropdownMenuTrigger 
} from "@/components/ui/dropdown-menu";

export default function RoamingPage() {
  const [activeTab, setActiveTab] = useState("partners");
  const [searchQuery, setSearchQuery] = useState("");

  // Mock data for partners
  const ocpiPartners = [
    { id: 1, name: "EV Connect", country: "US", status: "ACTIVE", lastSync: "2 hours ago", locations: 245, evses: 1270 },
    { id: 2, name: "ChargePoint", country: "US", status: "ACTIVE", lastSync: "5 hours ago", locations: 512, evses: 2830 },
    { id: 3, name: "EVBox", country: "NL", status: "ACTIVE", lastSync: "1 day ago", locations: 178, evses: 890 },
    { id: 4, name: "NewMotion", country: "NL", status: "INACTIVE", lastSync: "3 days ago", locations: 130, evses: 645 },
    { id: 5, name: "Virta", country: "FI", status: "PENDING", lastSync: "Never", locations: 0, evses: 0 },
  ];

  // Mock data for CDRs
  const roamingCdrs = [
    { id: "CDR-001", partner: "EV Connect", sessionStart: "2023-06-12 08:45", sessionEnd: "2023-06-12 10:15", energy: 22.4, cost: "$6.72", status: "SETTLED" },
    { id: "CDR-002", partner: "ChargePoint", sessionStart: "2023-06-12 11:20", sessionEnd: "2023-06-12 13:45", energy: 35.1, cost: "$10.53", status: "PENDING" },
    { id: "CDR-003", partner: "EVBox", sessionStart: "2023-06-12 15:30", sessionEnd: "2023-06-12 16:15", energy: 10.2, cost: "$3.06", status: "SETTLED" },
    { id: "CDR-004", partner: "EV Connect", sessionStart: "2023-06-11 19:05", sessionEnd: "2023-06-11 21:30", energy: 28.7, cost: "$8.61", status: "REJECTED" },
    { id: "CDR-005", partner: "ChargePoint", sessionStart: "2023-06-11 09:15", sessionEnd: "2023-06-11 10:00", energy: 8.9, cost: "$2.67", status: "SETTLED" },
  ];

  // Mock data for tokens
  const roamingTokens = [
    { id: "TOKEN-001", uid: "04A2B9C132", type: "RFID", partner: "EV Connect", status: "ACTIVE", lastUsed: "2023-06-12 14:25" },
    { id: "TOKEN-002", uid: "04B2C9D133", type: "RFID", partner: "ChargePoint", status: "ACTIVE", lastUsed: "2023-06-11 18:30" },
    { id: "TOKEN-003", uid: "APP34215", type: "APP_USER", partner: "EVBox", status: "ACTIVE", lastUsed: "2023-06-12 09:15" },
    { id: "TOKEN-004", uid: "04D2E9F134", type: "RFID", partner: "NewMotion", status: "EXPIRED", lastUsed: "2023-05-30 11:45" },
    { id: "TOKEN-005", uid: "APP45126", type: "APP_USER", partner: "EV Connect", status: "ACTIVE", lastUsed: "2023-06-12 16:10" },
  ];

  // Get status badge based on status
  const getStatusBadge = (status: string) => {
    switch (status.toUpperCase()) {
      case "ACTIVE":
        return <Badge className="bg-green-500">Active</Badge>;
      case "INACTIVE":
        return <Badge variant="secondary">Inactive</Badge>;
      case "PENDING":
        return <Badge variant="outline" className="bg-yellow-100 text-yellow-800 border-yellow-300">Pending</Badge>;
      case "SETTLED":
        return <Badge className="bg-green-500">Settled</Badge>;
      case "REJECTED":
        return <Badge variant="destructive">Rejected</Badge>;
      case "EXPIRED":
        return <Badge variant="destructive">Expired</Badge>;
      default:
        return <Badge>{status}</Badge>;
    }
  };

  const handleSearch = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    // Perform search logic here
    console.log("Searching for:", searchQuery);
  };

  const handleAddPartner = () => {
    // Handle adding new roaming partner
    console.log("Add new partner clicked");
  };

  const handleSynchronize = (partnerId: string) => {
    // Handle synchronizing with a partner
    console.log("Synchronize with partner:", partnerId);
  };

  return (
    <div className="space-y-6">
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <h1 className="text-3xl font-bold tracking-tight">Roaming Management</h1>
          <p className="text-muted-foreground">
            Manage OCPI partners and view roaming sessions
          </p>
        </div>
        <div className="flex items-center gap-2">
          <Button variant="outline" onClick={() => console.log("Refresh roaming data")}>
            <RefreshCw className="mr-2 h-4 w-4" /> Refresh
          </Button>
          <Button className="bg-accent hover:bg-accent/90" onClick={handleAddPartner}>
            <Plus className="mr-2 h-4 w-4" /> Add Partner
          </Button>
        </div>
      </div>

      <Card>
        <CardHeader className="pb-2">
          <CardTitle>OCPI Roaming</CardTitle>
          <CardDescription>
            Manage partnerships and view roaming activity across connected networks
          </CardDescription>
        </CardHeader>
        <CardContent>
          <div className="flex flex-col md:flex-row gap-4 mb-6">
            <form onSubmit={handleSearch} className="flex-1 flex gap-2">
              <div className="relative flex-1">
                <Search className="absolute left-2.5 top-2.5 h-4 w-4 text-muted-foreground" />
                <Input
                  type="search"
                  placeholder="Search by name or ID..."
                  className="pl-8"
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                />
              </div>
              <Button type="submit">Search</Button>
            </form>
          </div>

          <Tabs defaultValue="partners" className="space-y-4" onValueChange={setActiveTab}>
            <TabsList>
              <TabsTrigger value="partners">Partners</TabsTrigger>
              <TabsTrigger value="cdrs">CDRs</TabsTrigger>
              <TabsTrigger value="tokens">Tokens</TabsTrigger>
              <TabsTrigger value="locations">Locations</TabsTrigger>
            </TabsList>

            {/* Partners Tab */}
            <TabsContent value="partners" className="space-y-4">
              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead>Partner Name</TableHead>
                    <TableHead>Country</TableHead>
                    <TableHead>Status</TableHead>
                    <TableHead>Last Synchronization</TableHead>
                    <TableHead>Locations</TableHead>
                    <TableHead>EVSEs</TableHead>
                    <TableHead>Actions</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {ocpiPartners.map((partner) => (
                    <TableRow key={partner.id}>
                      <TableCell className="font-medium">{partner.name}</TableCell>
                      <TableCell>{partner.country}</TableCell>
                      <TableCell>{getStatusBadge(partner.status)}</TableCell>
                      <TableCell>{partner.lastSync}</TableCell>
                      <TableCell>{partner.locations}</TableCell>
                      <TableCell>{partner.evses}</TableCell>
                      <TableCell>
                        <DropdownMenu>
                          <DropdownMenuTrigger asChild>
                            <Button variant="ghost" size="sm">
                              <MoreHorizontal className="h-4 w-4" />
                            </Button>
                          </DropdownMenuTrigger>
                          <DropdownMenuContent align="end">
                            <DropdownMenuItem onClick={() => handleSynchronize(partner.id.toString())}>
                              <RefreshCw className="mr-2 h-4 w-4" /> Synchronize
                            </DropdownMenuItem>
                            <DropdownMenuItem>
                              <Globe className="mr-2 h-4 w-4" /> View Details
                            </DropdownMenuItem>
                          </DropdownMenuContent>
                        </DropdownMenu>
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </TabsContent>

            {/* CDRs Tab */}
            <TabsContent value="cdrs" className="space-y-4">
              <div className="flex justify-end mb-4">
                <Select defaultValue="last7days">
                  <SelectTrigger className="w-[180px]">
                    <SelectValue placeholder="Filter by period" />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="last7days">Last 7 days</SelectItem>
                    <SelectItem value="last30days">Last 30 days</SelectItem>
                    <SelectItem value="last90days">Last 90 days</SelectItem>
                  </SelectContent>
                </Select>
              </div>

              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead>CDR ID</TableHead>
                    <TableHead>Partner</TableHead>
                    <TableHead>Session Start</TableHead>
                    <TableHead>Session End</TableHead>
                    <TableHead>Energy (kWh)</TableHead>
                    <TableHead>Cost</TableHead>
                    <TableHead>Status</TableHead>
                    <TableHead>Actions</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {roamingCdrs.map((cdr) => (
                    <TableRow key={cdr.id}>
                      <TableCell className="font-medium">{cdr.id}</TableCell>
                      <TableCell>{cdr.partner}</TableCell>
                      <TableCell>{cdr.sessionStart}</TableCell>
                      <TableCell>{cdr.sessionEnd}</TableCell>
                      <TableCell>{cdr.energy}</TableCell>
                      <TableCell>{cdr.cost}</TableCell>
                      <TableCell>{getStatusBadge(cdr.status)}</TableCell>
                      <TableCell>
                        <Button variant="ghost" size="sm">
                          <Download className="h-4 w-4" />
                        </Button>
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </TabsContent>

            {/* Tokens Tab */}
            <TabsContent value="tokens" className="space-y-4">
              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead>Token ID</TableHead>
                    <TableHead>UID</TableHead>
                    <TableHead>Type</TableHead>
                    <TableHead>Partner</TableHead>
                    <TableHead>Status</TableHead>
                    <TableHead>Last Used</TableHead>
                    <TableHead>Actions</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {roamingTokens.map((token) => (
                    <TableRow key={token.id}>
                      <TableCell className="font-medium">{token.id}</TableCell>
                      <TableCell>{token.uid}</TableCell>
                      <TableCell>{token.type}</TableCell>
                      <TableCell>{token.partner}</TableCell>
                      <TableCell>{getStatusBadge(token.status)}</TableCell>
                      <TableCell>{token.lastUsed}</TableCell>
                      <TableCell>
                        <DropdownMenu>
                          <DropdownMenuTrigger asChild>
                            <Button variant="ghost" size="sm">
                              <MoreHorizontal className="h-4 w-4" />
                            </Button>
                          </DropdownMenuTrigger>
                          <DropdownMenuContent align="end">
                            <DropdownMenuItem>
                              <Activity className="mr-2 h-4 w-4" /> View Usage
                            </DropdownMenuItem>
                            <DropdownMenuItem>
                              <Zap className="mr-2 h-4 w-4" /> Authorize
                            </DropdownMenuItem>
                          </DropdownMenuContent>
                        </DropdownMenu>
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </TabsContent>

            {/* Locations Tab */}
            <TabsContent value="locations" className="space-y-4">
              <div className="flex justify-between mb-4">
                <Button variant="outline">
                  <Download className="mr-2 h-4 w-4" /> Export Locations
                </Button>
                <Button variant="outline">
                  <Upload className="mr-2 h-4 w-4" /> Upload Locations
                </Button>
              </div>
              
              <Card>
                <CardContent className="p-6">
                  <p className="text-center text-muted-foreground mb-4">
                    Locations module displays EVSE locations shared through OCPI
                  </p>
                  <div className="flex justify-center">
                    <Button variant="default">View Location Map</Button>
                  </div>
                </CardContent>
              </Card>
            </TabsContent>
          </Tabs>
        </CardContent>
        <CardFooter className="flex justify-between">
          <p className="text-sm text-muted-foreground">OCPI Version: 2.2.1</p>
          <p className="text-sm text-muted-foreground">Last synchronized: Today at 08:35</p>
        </CardFooter>
      </Card>
    </div>
  );
} 