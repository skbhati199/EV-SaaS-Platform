'use client';

import React, { useState, useEffect } from 'react';
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Slider } from "@/components/ui/slider";
import { Button } from "@/components/ui/button";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { Badge } from "@/components/ui/badge";
import { Loader2, AlertTriangle, Zap, ZapOff, BatteryCharging } from "lucide-react";

interface PowerLimit {
  stationId: string;
  connectorId: number;
  limit: number;
  maxLimit: number;
  unit: 'A' | 'kW';
  type: 'emergency' | 'scheduled' | 'dynamic';
  expiresAt?: string;
  startedAt: string;
}

interface StationPower {
  stationId: string;
  name: string;
  location: string;
  totalPower: number;
  connectors: ConnectorPower[];
  maxPower: number;
}

interface ConnectorPower {
  connectorId: number;
  power: number;
  status: 'Available' | 'Charging' | 'Faulted' | 'Unavailable';
  currentLimit?: number;
  maxLimit: number;
  activeSession?: {
    sessionId: string;
    userId: string;
    energy: number;
    startTime: string;
    vehicle?: string;
  };
}

/**
 * PowerControlPanel component manages real-time power distribution and smart charging features
 */
const PowerControlPanel: React.FC = () => {
  const [loading, setLoading] = useState(true);
  const [stations, setStations] = useState<StationPower[]>([]);
  const [activePowerLimits, setActivePowerLimits] = useState<PowerLimit[]>([]);
  const [totalSitePower, setTotalSitePower] = useState({ current: 0, limit: 0 });
  const [selectedStation, setSelectedStation] = useState<string | null>(null);
  const [selectedConnector, setSelectedConnector] = useState<number | null>(null);
  const [powerLimit, setPowerLimit] = useState<number | null>(null);
  const [limitType, setLimitType] = useState<'emergency' | 'scheduled' | 'dynamic'>('dynamic');
  const [limitDuration, setLimitDuration] = useState<string>('1h');
  const [isApplying, setIsApplying] = useState(false);
  
  // Load station data
  useEffect(() => {
    const fetchData = async () => {
      setLoading(true);
      try {
        // Simulate API call
        await new Promise(resolve => setTimeout(resolve, 1000));
        
        // Mock data
        const mockStations: StationPower[] = [
          {
            stationId: 'ST001',
            name: 'Main Office Station',
            location: 'Headquarters',
            totalPower: 32,
            maxPower: 44,
            connectors: [
              {
                connectorId: 1,
                power: 11,
                status: 'Charging',
                maxLimit: 22,
                currentLimit: 16,
                activeSession: {
                  sessionId: 'SES001',
                  userId: 'USR123',
                  energy: 8.5,
                  startTime: new Date(Date.now() - 45 * 60000).toISOString(),
                  vehicle: 'Tesla Model 3'
                }
              },
              {
                connectorId: 2,
                power: 0,
                status: 'Available',
                maxLimit: 22
              }
            ]
          },
          {
            stationId: 'ST002',
            name: 'Shopping Mall Station',
            location: 'Downtown',
            totalPower: 36,
            maxPower: 88,
            connectors: [
              {
                connectorId: 1,
                power: 12,
                status: 'Charging',
                maxLimit: 22,
                currentLimit: 16,
                activeSession: {
                  sessionId: 'SES002',
                  userId: 'USR345',
                  energy: 4.2,
                  startTime: new Date(Date.now() - 22 * 60000).toISOString(),
                  vehicle: 'Nissan Leaf'
                }
              },
              {
                connectorId: 2,
                power: 16,
                status: 'Charging',
                maxLimit: 22,
                activeSession: {
                  sessionId: 'SES003',
                  userId: 'USR456',
                  energy: 12.7,
                  startTime: new Date(Date.now() - 67 * 60000).toISOString(),
                  vehicle: 'BMW i4'
                }
              },
              {
                connectorId: 3,
                power: 8,
                status: 'Charging',
                maxLimit: 22,
                currentLimit: 11,
                activeSession: {
                  sessionId: 'SES004',
                  userId: 'USR789',
                  energy: 3.1,
                  startTime: new Date(Date.now() - 18 * 60000).toISOString()
                }
              },
              {
                connectorId: 4,
                power: 0,
                status: 'Faulted',
                maxLimit: 22
              }
            ]
          },
          {
            stationId: 'ST003',
            name: 'Residential Complex',
            location: 'Suburbs',
            totalPower: 7,
            maxPower: 22,
            connectors: [
              {
                connectorId: 1,
                power: 7,
                status: 'Charging',
                maxLimit: 11,
                activeSession: {
                  sessionId: 'SES005',
                  userId: 'USR567',
                  energy: 5.8,
                  startTime: new Date(Date.now() - 132 * 60000).toISOString(),
                  vehicle: 'Hyundai Ioniq'
                }
              }
            ]
          }
        ];
        
        const mockPowerLimits: PowerLimit[] = [
          {
            stationId: 'ST001',
            connectorId: 1,
            limit: 16,
            maxLimit: 22,
            unit: 'kW',
            type: 'dynamic',
            startedAt: new Date(Date.now() - 15 * 60000).toISOString()
          },
          {
            stationId: 'ST002',
            connectorId: 1,
            limit: 16,
            maxLimit: 22,
            unit: 'kW',
            type: 'scheduled',
            startedAt: new Date(Date.now() - 30 * 60000).toISOString(),
            expiresAt: new Date(Date.now() + 60 * 60000).toISOString()
          },
          {
            stationId: 'ST002',
            connectorId: 3,
            limit: 11,
            maxLimit: 22,
            unit: 'kW',
            type: 'emergency',
            startedAt: new Date(Date.now() - 10 * 60000).toISOString()
          }
        ];
        
        setStations(mockStations);
        setActivePowerLimits(mockPowerLimits);
        setTotalSitePower({
          current: mockStations.reduce((total, station) => total + station.totalPower, 0),
          limit: 150
        });
        
        // Default select the first station
        if (mockStations.length > 0) {
          setSelectedStation(mockStations[0].stationId);
        }
        
        setLoading(false);
      } catch (error) {
        console.error('Error fetching power data:', error);
        setLoading(false);
      }
    };
    
    fetchData();
    
    // Set up polling for real-time updates (every 10 seconds)
    const intervalId = setInterval(() => {
      fetchData();
    }, 10000);
    
    return () => clearInterval(intervalId);
  }, []);
  
  // Reset power limit when station or connector changes
  useEffect(() => {
    setPowerLimit(null);
  }, [selectedStation, selectedConnector]);
  
  // Get station by ID
  const getStation = (stationId: string) => {
    return stations.find(s => s.stationId === stationId);
  };
  
  // Get connector by ID
  const getConnector = (stationId: string, connectorId: number) => {
    const station = getStation(stationId);
    return station?.connectors.find(c => c.connectorId === connectorId);
  };
  
  // Get active power limit for a station connector
  const getActivePowerLimit = (stationId: string, connectorId: number) => {
    return activePowerLimits.find(
      limit => limit.stationId === stationId && limit.connectorId === connectorId
    );
  };
  
  // Format time duration from ISO string
  const formatDuration = (startTimeISO: string, endTimeISO?: string) => {
    const startTime = new Date(startTimeISO);
    const endTime = endTimeISO ? new Date(endTimeISO) : new Date();
    
    const durationMs = endTime.getTime() - startTime.getTime();
    const hours = Math.floor(durationMs / (1000 * 60 * 60));
    const minutes = Math.floor((durationMs % (1000 * 60 * 60)) / (1000 * 60));
    
    if (hours > 0) {
      return `${hours}h ${minutes}m`;
    }
    return `${minutes}m`;
  };
  
  // Calculate time remaining
  const getTimeRemaining = (expiresAtISO: string) => {
    const expiresAt = new Date(expiresAtISO);
    const now = new Date();
    
    if (expiresAt <= now) return 'Expired';
    
    const diffMs = expiresAt.getTime() - now.getTime();
    const diffMins = Math.floor(diffMs / 60000);
    const hours = Math.floor(diffMins / 60);
    const mins = diffMins % 60;
    
    if (hours > 0) {
      return `${hours}h ${mins}m remaining`;
    }
    return `${mins}m remaining`;
  };
  
  // Apply power limit to selected connector
  const applyPowerLimit = async () => {
    if (!selectedStation || selectedConnector === null || powerLimit === null) {
      return;
    }
    
    setIsApplying(true);
    try {
      // Simulate API call to set power limit
      await new Promise(resolve => setTimeout(resolve, 1500));
      
      // In production, this would be an API call to your power control service
      console.log(`Applied ${powerLimit}kW ${limitType} limit to Station ${selectedStation} Connector ${selectedConnector} for ${limitDuration}`);
      
      // Calculate expiration time based on duration
      let expiresAt: string | undefined;
      if (limitType !== 'dynamic') {
        const now = new Date();
        let durationMs = 0;
        
        switch (limitDuration) {
          case '15m': durationMs = 15 * 60 * 1000; break;
          case '30m': durationMs = 30 * 60 * 1000; break;
          case '1h': durationMs = 60 * 60 * 1000; break;
          case '2h': durationMs = 2 * 60 * 60 * 1000; break;
          case '4h': durationMs = 4 * 60 * 60 * 1000; break;
          default: durationMs = 60 * 60 * 1000;
        }
        
        expiresAt = new Date(now.getTime() + durationMs).toISOString();
      }
      
      // Update the local state with the new limit
      const newLimit: PowerLimit = {
        stationId: selectedStation,
        connectorId: selectedConnector,
        limit: powerLimit,
        maxLimit: getConnector(selectedStation, selectedConnector)?.maxLimit || 0,
        unit: 'kW',
        type: limitType,
        startedAt: new Date().toISOString(),
        expiresAt
      };
      
      // Update the active power limits
      setActivePowerLimits(prev => {
        // Remove any existing limit for this connector
        const filtered = prev.filter(
          limit => !(limit.stationId === selectedStation && limit.connectorId === selectedConnector)
        );
        // Add the new limit
        return [...filtered, newLimit];
      });
      
      // For demo purposes, also update the connector's current limit
      setStations(prev => {
        return prev.map(station => {
          if (station.stationId === selectedStation) {
            return {
              ...station,
              connectors: station.connectors.map(connector => {
                if (connector.connectorId === selectedConnector) {
                  return {
                    ...connector,
                    currentLimit: powerLimit
                  };
                }
                return connector;
              })
            };
          }
          return station;
        });
      });
      
      // Reset form
      setPowerLimit(null);
      
    } catch (error) {
      console.error('Error applying power limit:', error);
    } finally {
      setIsApplying(false);
    }
  };
  
  // Clear power limit
  const clearPowerLimit = async (stationId: string, connectorId: number) => {
    try {
      // Simulate API call to clear power limit
      await new Promise(resolve => setTimeout(resolve, 1000));
      
      // In production, this would be an API call to your power control service
      console.log(`Cleared power limit for Station ${stationId} Connector ${connectorId}`);
      
      // Update local state
      setActivePowerLimits(prev => 
        prev.filter(limit => !(limit.stationId === stationId && limit.connectorId === connectorId))
      );
      
      // Update connector state
      setStations(prev => {
        return prev.map(station => {
          if (station.stationId === stationId) {
            return {
              ...station,
              connectors: station.connectors.map(connector => {
                if (connector.connectorId === connectorId) {
                  const { currentLimit, ...rest } = connector;
                  return rest;
                }
                return connector;
              })
            };
          }
          return station;
        });
      });
      
    } catch (error) {
      console.error('Error clearing power limit:', error);
    }
  };

  // Get color class based on power usage percentage
  const getPowerBarColor = (current: number, max: number) => {
    const percentage = (current / max) * 100;
    if (percentage < 50) return 'bg-green-500';
    if (percentage < 80) return 'bg-amber-500';
    return 'bg-red-500';
  };
  
  // Get badge color for limit type
  const getLimitTypeBadgeColor = (type: string) => {
    switch (type) {
      case 'emergency': return 'bg-red-100 text-red-800 hover:bg-red-200';
      case 'scheduled': return 'bg-blue-100 text-blue-800 hover:bg-blue-200';
      case 'dynamic': return 'bg-green-100 text-green-800 hover:bg-green-200';
      default: return 'bg-gray-100 text-gray-800 hover:bg-gray-200';
    }
  };

  return (
    <Card className="w-full">
      <CardHeader>
        <CardTitle className="flex items-center justify-between">
          <span>Power Control</span>
          {loading && <Loader2 className="w-4 h-4 animate-spin" />}
        </CardTitle>
      </CardHeader>
      <CardContent>
        {loading ? (
          <div className="flex items-center justify-center h-60">
            <Loader2 className="w-8 h-8 animate-spin text-gray-400" />
          </div>
        ) : (
          <Tabs defaultValue="overview">
            <TabsList className="mb-4">
              <TabsTrigger value="overview">Overview</TabsTrigger>
              <TabsTrigger value="controls">Control Panel</TabsTrigger>
              <TabsTrigger value="limits">Active Limits</TabsTrigger>
            </TabsList>
            
            <TabsContent value="overview">
              <div className="space-y-6">
                {/* Total Site Power */}
                <div className="space-y-2">
                  <div className="flex justify-between items-center">
                    <span className="font-medium">Total Site Power</span>
                    <span className="text-sm">
                      {totalSitePower.current} kW / {totalSitePower.limit} kW
                    </span>
                  </div>
                  <div className="w-full bg-gray-200 rounded-full h-2.5">
                    <div 
                      className={`h-2.5 rounded-full ${getPowerBarColor(totalSitePower.current, totalSitePower.limit)}`}
                      style={{ width: `${Math.min(100, (totalSitePower.current / totalSitePower.limit) * 100)}%` }}
                    ></div>
                  </div>
                </div>
                
                {/* Station Power */}
                <div className="space-y-4">
                  <h3 className="text-sm font-medium">Stations</h3>
                  {stations.map(station => (
                    <div key={station.stationId} className="border rounded-lg p-3 space-y-3">
                      <div className="flex justify-between items-center">
                        <span className="font-medium">{station.name}</span>
                        <span className="text-sm">
                          {station.totalPower} kW / {station.maxPower} kW
                        </span>
                      </div>
                      <div className="w-full bg-gray-200 rounded-full h-2">
                        <div 
                          className={`h-2 rounded-full ${getPowerBarColor(station.totalPower, station.maxPower)}`}
                          style={{ width: `${Math.min(100, (station.totalPower / station.maxPower) * 100)}%` }}
                        ></div>
                      </div>
                      
                      <div className="pl-4 space-y-2 mt-2">
                        {station.connectors.map(connector => {
                          const hasLimit = !!connector.currentLimit;
                          const limitInfo = getActivePowerLimit(station.stationId, connector.connectorId);
                          
                          return (
                            <div key={connector.connectorId} className="text-sm flex items-center justify-between">
                              <div className="flex items-center gap-2">
                                <span className={`h-2 w-2 rounded-full ${
                                  connector.status === 'Charging' ? 'bg-green-500' : 
                                  connector.status === 'Available' ? 'bg-blue-500' : 
                                  connector.status === 'Faulted' ? 'bg-red-500' : 'bg-gray-500'
                                }`}></span>
                                <span>Connector {connector.connectorId}</span>
                                {connector.status === 'Charging' && (
                                  <span className="text-xs text-gray-500">
                                    ({connector.power} kW)
                                  </span>
                                )}
                              </div>
                              
                              {hasLimit && limitInfo && (
                                <Badge className={getLimitTypeBadgeColor(limitInfo.type)}>
                                  {limitInfo.limit} kW {limitInfo.type === 'emergency' ? '(Emergency)' : ''}
                                </Badge>
                              )}
                            </div>
                          );
                        })}
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            </TabsContent>
            
            <TabsContent value="controls">
              <div className="space-y-6">
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  <div className="space-y-4">
                    <div>
                      <label className="text-sm font-medium mb-1 block">Station</label>
                      <Select 
                        value={selectedStation || undefined} 
                        onValueChange={setSelectedStation}
                      >
                        <SelectTrigger>
                          <SelectValue placeholder="Select station" />
                        </SelectTrigger>
                        <SelectContent>
                          {stations.map(station => (
                            <SelectItem key={station.stationId} value={station.stationId}>
                              {station.name}
                            </SelectItem>
                          ))}
                        </SelectContent>
                      </Select>
                    </div>
                    
                    {selectedStation && (
                      <div>
                        <label className="text-sm font-medium mb-1 block">Connector</label>
                        <Select 
                          value={selectedConnector?.toString() || undefined} 
                          onValueChange={(val) => setSelectedConnector(parseInt(val, 10))}
                        >
                          <SelectTrigger>
                            <SelectValue placeholder="Select connector" />
                          </SelectTrigger>
                          <SelectContent>
                            {getStation(selectedStation)?.connectors.map(connector => (
                              <SelectItem 
                                key={connector.connectorId} 
                                value={connector.connectorId.toString()}
                              >
                                Connector {connector.connectorId} 
                                {connector.status === 'Charging' && ' (Charging)'}
                                {connector.status === 'Faulted' && ' (Faulted)'}
                              </SelectItem>
                            ))}
                          </SelectContent>
                        </Select>
                      </div>
                    )}
                  </div>
                  
                  <div className="space-y-4">
                    <div>
                      <label className="text-sm font-medium mb-1 block">Limit Type</label>
                      <Select 
                        value={limitType} 
                        onValueChange={(val: 'emergency' | 'scheduled' | 'dynamic') => setLimitType(val)}
                      >
                        <SelectTrigger>
                          <SelectValue placeholder="Select limit type" />
                        </SelectTrigger>
                        <SelectContent>
                          <SelectItem value="dynamic">Dynamic (ongoing)</SelectItem>
                          <SelectItem value="scheduled">Scheduled (temporary)</SelectItem>
                          <SelectItem value="emergency">Emergency</SelectItem>
                        </SelectContent>
                      </Select>
                    </div>
                    
                    {limitType !== 'dynamic' && (
                      <div>
                        <label className="text-sm font-medium mb-1 block">Duration</label>
                        <Select 
                          value={limitDuration} 
                          onValueChange={setLimitDuration}
                        >
                          <SelectTrigger>
                            <SelectValue placeholder="Select duration" />
                          </SelectTrigger>
                          <SelectContent>
                            <SelectItem value="15m">15 minutes</SelectItem>
                            <SelectItem value="30m">30 minutes</SelectItem>
                            <SelectItem value="1h">1 hour</SelectItem>
                            <SelectItem value="2h">2 hours</SelectItem>
                            <SelectItem value="4h">4 hours</SelectItem>
                          </SelectContent>
                        </Select>
                      </div>
                    )}
                  </div>
                </div>
                
                {selectedStation && selectedConnector !== null && (
                  <div className="space-y-4 pt-4">
                    <div>
                      <div className="flex justify-between mb-2">
                        <label className="text-sm font-medium">Power Limit (kW)</label>
                        <span className="text-sm">
                          {powerLimit !== null ? `${powerLimit} kW` : 'No limit'}
                        </span>
                      </div>
                      <Slider 
                        value={powerLimit !== null ? [powerLimit] : [0]}
                        min={0}
                        max={getConnector(selectedStation, selectedConnector)?.maxLimit || 22}
                        step={1}
                        onValueChange={(val) => setPowerLimit(val[0])}
                        disabled={!selectedStation || selectedConnector === null}
                      />
                    </div>
                    
                    <div className="flex justify-end gap-2 pt-2">
                      {getActivePowerLimit(selectedStation, selectedConnector) && (
                        <Button 
                          variant="outline" 
                          onClick={() => clearPowerLimit(selectedStation, selectedConnector)}
                        >
                          Clear Limit
                        </Button>
                      )}
                      <Button 
                        onClick={applyPowerLimit}
                        disabled={powerLimit === null || isApplying}
                        className={limitType === 'emergency' ? 'bg-red-600 hover:bg-red-700' : ''}
                      >
                        {isApplying ? (
                          <>
                            <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                            Applying...
                          </>
                        ) : limitType === 'emergency' ? (
                          <>
                            <AlertTriangle className="mr-2 h-4 w-4" />
                            Apply Emergency Limit
                          </>
                        ) : (
                          <>
                            <BatteryCharging className="mr-2 h-4 w-4" />
                            Apply Power Limit
                          </>
                        )}
                      </Button>
                    </div>
                  </div>
                )}
              </div>
            </TabsContent>
            
            <TabsContent value="limits">
              {activePowerLimits.length === 0 ? (
                <div className="flex flex-col items-center justify-center h-40 text-gray-500">
                  <ZapOff className="h-8 w-8 mb-2" />
                  <p>No active power limits</p>
                </div>
              ) : (
                <div className="space-y-4">
                  <h3 className="text-sm font-medium">Active Power Limits</h3>
                  {activePowerLimits.map((limit, idx) => {
                    const station = getStation(limit.stationId);
                    return (
                      <div key={idx} className="border rounded-lg p-3 space-y-2">
                        <div className="flex justify-between items-center">
                          <span className="font-medium">
                            {station?.name} - Connector {limit.connectorId}
                          </span>
                          <Badge className={getLimitTypeBadgeColor(limit.type)}>
                            {limit.type}
                          </Badge>
                        </div>
                        
                        <div className="flex justify-between items-center text-sm">
                          <span>Current limit: <strong>{limit.limit} {limit.unit}</strong></span>
                          <span>Max: {limit.maxLimit} {limit.unit}</span>
                        </div>
                        
                        <div className="text-xs text-gray-500 flex justify-between">
                          <span>Started {formatDuration(limit.startedAt)} ago</span>
                          {limit.expiresAt && (
                            <span>{getTimeRemaining(limit.expiresAt)}</span>
                          )}
                        </div>
                        
                        <div className="pt-2 flex justify-end">
                          <Button 
                            variant="outline" 
                            size="sm"
                            onClick={() => clearPowerLimit(limit.stationId, limit.connectorId)}
                          >
                            Clear Limit
                          </Button>
                        </div>
                      </div>
                    );
                  })}
                </div>
              )}
            </TabsContent>
          </Tabs>
        )}
      </CardContent>
    </Card>
  );
};

export default PowerControlPanel; 