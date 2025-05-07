"use client";

import React, { useState } from 'react';
import { Card, CardContent, CardDescription, CardHeader, CardTitle, CardFooter } from "@/components/ui/card";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { Button } from "@/components/ui/button";
import { 
  Input
} from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { 
  Select, 
  SelectContent, 
  SelectItem, 
  SelectTrigger, 
  SelectValue 
} from "@/components/ui/select";
import { Checkbox } from "@/components/ui/checkbox";
import { Textarea } from "@/components/ui/textarea";
import { Switch } from "@/components/ui/switch";
import { 
  Save, 
  RefreshCw, 
  Settings, 
  Key, 
  Globe,
  Bell,
  Lock,
  CreditCard,
  Database,
  Cloud,
  Plug,
  AlertCircle
} from "lucide-react";
import { Badge } from "@/components/ui/badge";
import { Separator } from "@/components/ui/separator";

export default function SettingsPage() {
  const [activeTab, setActiveTab] = useState("general");
  const [isLoading, setIsLoading] = useState(false);

  const handleSaveSettings = (section: string) => {
    setIsLoading(true);
    console.log(`Saving ${section} settings...`);
    // Simulating API call
    setTimeout(() => {
      setIsLoading(false);
    }, 1000);
  };

  return (
    <div className="space-y-6">
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <h1 className="text-3xl font-bold tracking-tight">Settings & Integrations</h1>
          <p className="text-muted-foreground">
            Configure system settings, API integrations, and preferences
          </p>
        </div>
      </div>

      <Card>
        <CardHeader className="pb-4">
          <CardTitle>System Configuration</CardTitle>
          <CardDescription>
            Manage system-wide settings and integrations
          </CardDescription>
        </CardHeader>
        <CardContent>
          <Tabs defaultValue="general" className="space-y-6" onValueChange={setActiveTab}>
            <TabsList className="grid grid-cols-2 md:grid-cols-4 lg:grid-cols-6 gap-2">
              <TabsTrigger value="general">
                <Settings className="h-4 w-4 mr-2" />
                General
              </TabsTrigger>
              <TabsTrigger value="api-keys">
                <Key className="h-4 w-4 mr-2" />
                API Keys
              </TabsTrigger>
              <TabsTrigger value="ocpi">
                <Globe className="h-4 w-4 mr-2" />
                OCPI
              </TabsTrigger>
              <TabsTrigger value="ocpp">
                <Plug className="h-4 w-4 mr-2" />
                OCPP
              </TabsTrigger>
              <TabsTrigger value="notifications">
                <Bell className="h-4 w-4 mr-2" />
                Notifications
              </TabsTrigger>
              <TabsTrigger value="security">
                <Lock className="h-4 w-4 mr-2" />
                Security
              </TabsTrigger>
            </TabsList>

            {/* General Settings */}
            <TabsContent value="general" className="space-y-6">
              <div className="space-y-4">
                <div className="space-y-2">
                  <Label htmlFor="company-name">Platform Name</Label>
                  <Input id="company-name" defaultValue="EV SaaS Platform" />
                  <p className="text-sm text-muted-foreground">
                    The name displayed throughout the platform
                  </p>
                </div>

                <div className="space-y-2">
                  <Label htmlFor="timezone">Default Timezone</Label>
                  <Select defaultValue="utc">
                    <SelectTrigger>
                      <SelectValue placeholder="Select timezone" />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem value="utc">UTC</SelectItem>
                      <SelectItem value="america_new_york">America/New York</SelectItem>
                      <SelectItem value="europe_london">Europe/London</SelectItem>
                      <SelectItem value="asia_tokyo">Asia/Tokyo</SelectItem>
                    </SelectContent>
                  </Select>
                </div>

                <div className="space-y-2">
                  <Label htmlFor="currency">Default Currency</Label>
                  <Select defaultValue="usd">
                    <SelectTrigger>
                      <SelectValue placeholder="Select currency" />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem value="usd">USD ($)</SelectItem>
                      <SelectItem value="eur">EUR (€)</SelectItem>
                      <SelectItem value="gbp">GBP (£)</SelectItem>
                      <SelectItem value="jpy">JPY (¥)</SelectItem>
                    </SelectContent>
                  </Select>
                </div>

                <Separator />

                <div className="flex items-center justify-between">
                  <div className="space-y-0.5">
                    <Label htmlFor="maintenance-mode">Maintenance Mode</Label>
                    <p className="text-sm text-muted-foreground">
                      Enable maintenance mode to prevent user access during updates
                    </p>
                  </div>
                  <Switch id="maintenance-mode" />
                </div>

                <div className="flex items-center justify-between">
                  <div className="space-y-0.5">
                    <Label htmlFor="debug-mode">Debug Mode</Label>
                    <p className="text-sm text-muted-foreground">
                      Enable verbose logging for troubleshooting
                    </p>
                  </div>
                  <Switch id="debug-mode" />
                </div>

                <Button 
                  onClick={() => handleSaveSettings('general')} 
                  className="mt-4"
                  disabled={isLoading}
                >
                  {isLoading ? <RefreshCw className="mr-2 h-4 w-4 animate-spin" /> : <Save className="mr-2 h-4 w-4" />}
                  Save General Settings
                </Button>
              </div>
            </TabsContent>

            {/* API Keys Tab */}
            <TabsContent value="api-keys" className="space-y-6">
              <div className="space-y-4">
                <div className="space-y-2">
                  <div className="flex items-center justify-between">
                    <Label htmlFor="api-key">API Key</Label>
                    <Badge variant="outline">Production</Badge>
                  </div>
                  <div className="flex space-x-2">
                    <Input 
                      id="api-key" 
                      value="evs_pk_****************************************" 
                      readOnly 
                      className="font-mono text-sm"
                    />
                    <Button variant="outline">Reveal</Button>
                    <Button variant="outline">Regenerate</Button>
                  </div>
                  <p className="text-sm text-muted-foreground">
                    Your API key for production environment. Keep this secret.
                  </p>
                </div>

                <div className="space-y-2">
                  <div className="flex items-center justify-between">
                    <Label htmlFor="test-api-key">Test API Key</Label>
                    <Badge variant="outline">Test</Badge>
                  </div>
                  <div className="flex space-x-2">
                    <Input 
                      id="test-api-key" 
                      value="evs_tk_****************************************" 
                      readOnly 
                      className="font-mono text-sm"
                    />
                    <Button variant="outline">Reveal</Button>
                    <Button variant="outline">Regenerate</Button>
                  </div>
                  <p className="text-sm text-muted-foreground">
                    Your API key for test environment.
                  </p>
                </div>

                <Separator />

                <div className="space-y-2">
                  <Label htmlFor="webhook-url">Webhook URL</Label>
                  <Input 
                    id="webhook-url" 
                    placeholder="https://your-domain.com/webhook" 
                  />
                  <p className="text-sm text-muted-foreground">
                    URL to receive event notifications from the platform
                  </p>
                </div>

                <div className="space-y-2">
                  <Label>Webhook Events</Label>
                  <div className="grid grid-cols-1 md:grid-cols-2 gap-2">
                    <div className="flex items-center space-x-2">
                      <Checkbox id="event-session" />
                      <label
                        htmlFor="event-session"
                        className="text-sm font-medium leading-none peer-disabled:cursor-not-allowed peer-disabled:opacity-70"
                      >
                        Charging Sessions
                      </label>
                    </div>
                    <div className="flex items-center space-x-2">
                      <Checkbox id="event-payment" />
                      <label
                        htmlFor="event-payment"
                        className="text-sm font-medium leading-none peer-disabled:cursor-not-allowed peer-disabled:opacity-70"
                      >
                        Payments
                      </label>
                    </div>
                    <div className="flex items-center space-x-2">
                      <Checkbox id="event-station" />
                      <label
                        htmlFor="event-station"
                        className="text-sm font-medium leading-none peer-disabled:cursor-not-allowed peer-disabled:opacity-70"
                      >
                        Station Status
                      </label>
                    </div>
                    <div className="flex items-center space-x-2">
                      <Checkbox id="event-user" />
                      <label
                        htmlFor="event-user"
                        className="text-sm font-medium leading-none peer-disabled:cursor-not-allowed peer-disabled:opacity-70"
                      >
                        User Events
                      </label>
                    </div>
                  </div>
                </div>

                <Button 
                  onClick={() => handleSaveSettings('api')} 
                  className="mt-4"
                  disabled={isLoading}
                >
                  {isLoading ? <RefreshCw className="mr-2 h-4 w-4 animate-spin" /> : <Save className="mr-2 h-4 w-4" />}
                  Save API Settings
                </Button>
              </div>
            </TabsContent>

            {/* OCPI Tab */}
            <TabsContent value="ocpi" className="space-y-6">
              <div className="space-y-4">
                <div className="flex items-center justify-between">
                  <div className="space-y-0.5">
                    <Label htmlFor="ocpi-enabled">Enable OCPI</Label>
                    <p className="text-sm text-muted-foreground">
                      Enable Open Charge Point Interface for roaming
                    </p>
                  </div>
                  <Switch id="ocpi-enabled" checked={true} />
                </div>

                <div className="space-y-2">
                  <Label htmlFor="ocpi-version">OCPI Version</Label>
                  <Select defaultValue="2.2.1">
                    <SelectTrigger>
                      <SelectValue placeholder="Select OCPI version" />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem value="2.2.1">2.2.1</SelectItem>
                      <SelectItem value="2.2">2.2</SelectItem>
                      <SelectItem value="2.1.1">2.1.1</SelectItem>
                    </SelectContent>
                  </Select>
                </div>

                <div className="space-y-2">
                  <Label htmlFor="ocpi-party-id">Party ID</Label>
                  <Input 
                    id="ocpi-party-id" 
                    defaultValue="EVSAAS" 
                  />
                </div>

                <div className="space-y-2">
                  <Label htmlFor="ocpi-country-code">Country Code</Label>
                  <Input 
                    id="ocpi-country-code" 
                    defaultValue="US" 
                    maxLength={2}
                  />
                </div>

                <div className="space-y-2">
                  <Label htmlFor="ocpi-base-url">Base URL</Label>
                  <Input 
                    id="ocpi-base-url" 
                    defaultValue="https://api.nbevc.com/ocpi" 
                  />
                </div>
                
                <Button 
                  onClick={() => handleSaveSettings('ocpi')} 
                  className="mt-4"
                  disabled={isLoading}
                >
                  {isLoading ? <RefreshCw className="mr-2 h-4 w-4 animate-spin" /> : <Save className="mr-2 h-4 w-4" />}
                  Save OCPI Settings
                </Button>
              </div>
            </TabsContent>

            {/* OCPP Settings */}
            <TabsContent value="ocpp" className="space-y-6">
              <div className="space-y-4">
                <div className="flex items-center justify-between">
                  <div className="space-y-0.5">
                    <Label htmlFor="ocpp-enabled">Enable OCPP</Label>
                    <p className="text-sm text-muted-foreground">
                      Enable Open Charge Point Protocol for station communication
                    </p>
                  </div>
                  <Switch id="ocpp-enabled" checked={true} />
                </div>

                <div className="space-y-2">
                  <Label htmlFor="ocpp-version">OCPP Version</Label>
                  <Select defaultValue="1.6">
                    <SelectTrigger>
                      <SelectValue placeholder="Select OCPP version" />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem value="2.0.1">2.0.1</SelectItem>
                      <SelectItem value="1.6">1.6</SelectItem>
                      <SelectItem value="1.5">1.5</SelectItem>
                    </SelectContent>
                  </Select>
                </div>

                <div className="space-y-2">
                  <Label htmlFor="ocpp-endpoint">OCPP Endpoint</Label>
                  <Input 
                    id="ocpp-endpoint" 
                    defaultValue="wss://api.nbevc.com/ocpp" 
                  />
                </div>

                <div className="space-y-2">
                  <Label htmlFor="heartbeat-interval">Heartbeat Interval (seconds)</Label>
                  <Input 
                    id="heartbeat-interval" 
                    defaultValue="300"
                    type="number" 
                  />
                </div>

                <div className="space-y-2">
                  <Label htmlFor="ocpp-security">Security Profile</Label>
                  <Select defaultValue="2">
                    <SelectTrigger>
                      <SelectValue placeholder="Select security profile" />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem value="0">Profile 0 (Unsecured)</SelectItem>
                      <SelectItem value="1">Profile 1 (Basic Auth)</SelectItem>
                      <SelectItem value="2">Profile 2 (TLS with Basic Auth)</SelectItem>
                      <SelectItem value="3">Profile 3 (TLS with Certificates)</SelectItem>
                    </SelectContent>
                  </Select>
                </div>
                
                <Button 
                  onClick={() => handleSaveSettings('ocpp')} 
                  className="mt-4"
                  disabled={isLoading}
                >
                  {isLoading ? <RefreshCw className="mr-2 h-4 w-4 animate-spin" /> : <Save className="mr-2 h-4 w-4" />}
                  Save OCPP Settings
                </Button>
              </div>
            </TabsContent>

            {/* Notifications Tab */}
            <TabsContent value="notifications" className="space-y-6">
              <div className="space-y-4">
                <div className="space-y-2">
                  <Label htmlFor="email-service">Email Service</Label>
                  <Select defaultValue="smtp">
                    <SelectTrigger>
                      <SelectValue placeholder="Select email service" />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem value="smtp">SMTP</SelectItem>
                      <SelectItem value="sendgrid">SendGrid</SelectItem>
                      <SelectItem value="mailgun">Mailgun</SelectItem>
                      <SelectItem value="aws-ses">AWS SES</SelectItem>
                    </SelectContent>
                  </Select>
                </div>

                <div className="space-y-2">
                  <Label htmlFor="smtp-host">SMTP Host</Label>
                  <Input 
                    id="smtp-host" 
                    defaultValue="smtp.example.com" 
                  />
                </div>

                <div className="grid grid-cols-2 gap-4">
                  <div className="space-y-2">
                    <Label htmlFor="smtp-port">SMTP Port</Label>
                    <Input 
                      id="smtp-port" 
                      defaultValue="587"
                      type="number" 
                    />
                  </div>
                  
                  <div className="space-y-2">
                    <Label htmlFor="smtp-security">Security</Label>
                    <Select defaultValue="tls">
                      <SelectTrigger>
                        <SelectValue placeholder="Select security" />
                      </SelectTrigger>
                      <SelectContent>
                        <SelectItem value="none">None</SelectItem>
                        <SelectItem value="ssl">SSL</SelectItem>
                        <SelectItem value="tls">TLS</SelectItem>
                      </SelectContent>
                    </Select>
                  </div>
                </div>

                <div className="space-y-2">
                  <Label htmlFor="smtp-username">SMTP Username</Label>
                  <Input 
                    id="smtp-username" 
                    defaultValue="notifications@example.com" 
                  />
                </div>

                <div className="space-y-2">
                  <Label htmlFor="smtp-password">SMTP Password</Label>
                  <Input 
                    id="smtp-password" 
                    type="password"
                    value="password"
                  />
                </div>

                <div className="space-y-2">
                  <Label htmlFor="sender-email">Sender Email</Label>
                  <Input 
                    id="sender-email" 
                    defaultValue="noreply@nbevc.com" 
                  />
                </div>

                <div className="space-y-2">
                  <Label htmlFor="sender-name">Sender Name</Label>
                  <Input 
                    id="sender-name" 
                    defaultValue="EV SaaS Platform" 
                  />
                </div>
                
                <Button 
                  onClick={() => handleSaveSettings('notifications')} 
                  className="mt-4"
                  disabled={isLoading}
                >
                  {isLoading ? <RefreshCw className="mr-2 h-4 w-4 animate-spin" /> : <Save className="mr-2 h-4 w-4" />}
                  Save Notification Settings
                </Button>
              </div>
            </TabsContent>

            {/* Security Tab */}
            <TabsContent value="security" className="space-y-6">
              <div className="space-y-4">
                <div className="space-y-2">
                  <Label htmlFor="session-timeout">Session Timeout (minutes)</Label>
                  <Input 
                    id="session-timeout" 
                    defaultValue="30"
                    type="number" 
                    min="5"
                    max="120"
                  />
                  <p className="text-sm text-muted-foreground">
                    Time before an inactive user is automatically logged out
                  </p>
                </div>

                <div className="space-y-2">
                  <Label htmlFor="password-policy">Password Policy</Label>
                  <Select defaultValue="strong">
                    <SelectTrigger>
                      <SelectValue placeholder="Select password policy" />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem value="basic">Basic (8+ characters)</SelectItem>
                      <SelectItem value="medium">Medium (8+ chars, mixed case, numbers)</SelectItem>
                      <SelectItem value="strong">Strong (8+ chars, mixed case, numbers, special chars)</SelectItem>
                    </SelectContent>
                  </Select>
                </div>

                <div className="space-y-2">
                  <Label htmlFor="mfa-mode">Multi-Factor Authentication</Label>
                  <Select defaultValue="optional">
                    <SelectTrigger>
                      <SelectValue placeholder="Select MFA policy" />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem value="disabled">Disabled</SelectItem>
                      <SelectItem value="optional">Optional</SelectItem>
                      <SelectItem value="required">Required for all users</SelectItem>
                      <SelectItem value="admin-only">Required for admins only</SelectItem>
                    </SelectContent>
                  </Select>
                </div>

                <Separator />

                <div className="flex items-center justify-between">
                  <div className="space-y-0.5">
                    <Label htmlFor="ip-restriction">IP Restriction</Label>
                    <p className="text-sm text-muted-foreground">
                      Restrict access to specific IP addresses
                    </p>
                  </div>
                  <Switch id="ip-restriction" />
                </div>

                <div className="space-y-2">
                  <Label htmlFor="allowed-ips">Allowed IP Addresses</Label>
                  <Textarea 
                    id="allowed-ips" 
                    placeholder="Enter IP addresses, one per line"
                    className="min-h-[100px] font-mono"
                    disabled
                  />
                  <p className="text-sm text-muted-foreground">
                    Enter IP addresses or CIDR ranges, one per line
                  </p>
                </div>
                
                <Button 
                  onClick={() => handleSaveSettings('security')} 
                  className="mt-4"
                  disabled={isLoading}
                >
                  {isLoading ? <RefreshCw className="mr-2 h-4 w-4 animate-spin" /> : <Save className="mr-2 h-4 w-4" />}
                  Save Security Settings
                </Button>
              </div>
            </TabsContent>
          </Tabs>
        </CardContent>
        <CardFooter className="border-t bg-muted/50 flex justify-between">
          <div className="flex items-center">
            <AlertCircle className="h-4 w-4 mr-2 text-amber-500" />
            <p className="text-sm text-muted-foreground">Changes to system settings may require service restart</p>
          </div>
          <div className="flex items-center space-x-2">
            <Badge variant="outline" className="text-green-600">
              System Healthy
            </Badge>
          </div>
        </CardFooter>
      </Card>

      {/* Integration Status Card */}
      <Card>
        <CardHeader>
          <CardTitle>Integration Status</CardTitle>
          <CardDescription>
            Current status of integrated systems and services
          </CardDescription>
        </CardHeader>
        <CardContent>
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
            <div className="flex items-center p-4 border rounded-md">
              <div className="h-2.5 w-2.5 rounded-full bg-green-500 mr-3"></div>
              <div className="flex-grow">
                <h3 className="text-sm font-medium">Payment Gateway</h3>
                <p className="text-xs text-muted-foreground">Connected</p>
              </div>
              <CreditCard className="h-5 w-5 text-muted-foreground" />
            </div>
            
            <div className="flex items-center p-4 border rounded-md">
              <div className="h-2.5 w-2.5 rounded-full bg-green-500 mr-3"></div>
              <div className="flex-grow">
                <h3 className="text-sm font-medium">Database</h3>
                <p className="text-xs text-muted-foreground">Operational</p>
              </div>
              <Database className="h-5 w-5 text-muted-foreground" />
            </div>
            
            <div className="flex items-center p-4 border rounded-md">
              <div className="h-2.5 w-2.5 rounded-full bg-amber-500 mr-3"></div>
              <div className="flex-grow">
                <h3 className="text-sm font-medium">Email Service</h3>
                <p className="text-xs text-muted-foreground">Degraded Performance</p>
              </div>
              <Cloud className="h-5 w-5 text-muted-foreground" />
            </div>
            
            <div className="flex items-center p-4 border rounded-md">
              <div className="h-2.5 w-2.5 rounded-full bg-green-500 mr-3"></div>
              <div className="flex-grow">
                <h3 className="text-sm font-medium">OCPP Service</h3>
                <p className="text-xs text-muted-foreground">Connected</p>
              </div>
              <Plug className="h-5 w-5 text-muted-foreground" />
            </div>
            
            <div className="flex items-center p-4 border rounded-md">
              <div className="h-2.5 w-2.5 rounded-full bg-green-500 mr-3"></div>
              <div className="flex-grow">
                <h3 className="text-sm font-medium">OCPI Service</h3>
                <p className="text-xs text-muted-foreground">Connected</p>
              </div>
              <Globe className="h-5 w-5 text-muted-foreground" />
            </div>
            
            <div className="flex items-center p-4 border rounded-md">
              <div className="h-2.5 w-2.5 rounded-full bg-red-500 mr-3"></div>
              <div className="flex-grow">
                <h3 className="text-sm font-medium">SMS Gateway</h3>
                <p className="text-xs text-muted-foreground">Disconnected</p>
              </div>
              <Bell className="h-5 w-5 text-muted-foreground" />
            </div>
          </div>
        </CardContent>
      </Card>
    </div>
  );
} 