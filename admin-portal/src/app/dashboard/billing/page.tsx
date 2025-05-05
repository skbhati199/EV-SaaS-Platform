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
import { Label } from "@/components/ui/label";
import { 
  Search, 
  Plus, 
  Download, 
  CreditCard, 
  Calendar, 
  ChevronDown, 
  FileText, 
  Eye, 
  MoreHorizontal, 
  RefreshCw, 
  DollarSign, 
  Users, 
  FileSpreadsheet 
} from "lucide-react";
import { 
  DropdownMenu, 
  DropdownMenuContent, 
  DropdownMenuItem, 
  DropdownMenuTrigger 
} from "@/components/ui/dropdown-menu";
import { DatePickerWithRange } from "@/components/ui/date-range-picker";
import { AreaChart } from "@/components/dashboard/AreaChart";
import { BarChart } from "@/components/dashboard/BarChart";

export default function BillingPage() {
  const [activeTab, setActiveTab] = useState("invoices");
  const [searchQuery, setSearchQuery] = useState("");
  const [dateRange, setDateRange] = useState({ from: undefined, to: undefined });
  const [period, setPeriod] = useState("thisMonth");

  // Mock data for invoices
  const invoices = [
    { id: "INV-00123", customer: "XYZ Corp", amount: "$1,250.75", date: "2023-06-15", status: "PAID", dueDate: "2023-07-15", sessions: 45 },
    { id: "INV-00124", customer: "ABC Inc", amount: "$872.40", date: "2023-06-16", status: "PENDING", dueDate: "2023-07-16", sessions: 32 },
    { id: "INV-00125", customer: "Green Energy Co", amount: "$3,426.90", date: "2023-06-18", status: "PAID", dueDate: "2023-07-18", sessions: 97 },
    { id: "INV-00126", customer: "Fleet Solutions Ltd", amount: "$754.20", date: "2023-06-20", status: "OVERDUE", dueDate: "2023-07-20", sessions: 28 },
    { id: "INV-00127", customer: "City Municipal", amount: "$1,875.60", date: "2023-06-22", status: "PAID", dueDate: "2023-07-22", sessions: 65 },
  ];

  // Mock data for transactions
  const transactions = [
    { id: "TRX-00567", customer: "XYZ Corp", amount: "$45.75", date: "2023-06-25", method: "Credit Card", status: "COMPLETED", sessionId: "S-123456" },
    { id: "TRX-00568", customer: "John Smith", amount: "$21.40", date: "2023-06-25", method: "Mobile Payment", status: "COMPLETED", sessionId: "S-123457" },
    { id: "TRX-00569", customer: "ABC Inc", amount: "$32.90", date: "2023-06-24", method: "Credit Card", status: "FAILED", sessionId: "S-123458" },
    { id: "TRX-00570", customer: "Jane Doe", amount: "$18.20", date: "2023-06-24", method: "Credit Card", status: "COMPLETED", sessionId: "S-123459" },
    { id: "TRX-00571", customer: "Fleet Solutions Ltd", amount: "$67.60", date: "2023-06-23", method: "Direct Billing", status: "PENDING", sessionId: "S-123460" },
  ];

  // Mock data for tariff plans
  const tariffPlans = [
    { id: "T001", name: "Standard Rate", energyRate: "$0.30/kWh", timeFee: "$0.05/min", connectionFee: "$1.00", status: "ACTIVE" },
    { id: "T002", name: "Premium Fast Charging", energyRate: "$0.45/kWh", timeFee: "$0.00/min", connectionFee: "$2.00", status: "ACTIVE" },
    { id: "T003", name: "Night Saver", energyRate: "$0.20/kWh", timeFee: "$0.00/min", connectionFee: "$1.00", status: "ACTIVE" },
    { id: "T004", name: "Business Account", energyRate: "$0.25/kWh", timeFee: "$0.03/min", connectionFee: "$0.00", status: "ACTIVE" },
    { id: "T005", name: "Summer Special", energyRate: "$0.28/kWh", timeFee: "$0.04/min", connectionFee: "$0.50", status: "INACTIVE" },
  ];

  // Get status badge based on status
  const getStatusBadge = (status) => {
    switch (status.toUpperCase()) {
      case "PAID":
      case "COMPLETED":
      case "ACTIVE":
        return <Badge className="bg-green-500">Paid</Badge>;
      case "PENDING":
        return <Badge variant="outline" className="bg-yellow-100 text-yellow-800 border-yellow-300">Pending</Badge>;
      case "OVERDUE":
      case "FAILED":
        return <Badge variant="destructive">Overdue</Badge>;
      case "INACTIVE":
        return <Badge variant="secondary">Inactive</Badge>;
      default:
        return <Badge>{status}</Badge>;
    }
  };

  const handleSearch = (e) => {
    e.preventDefault();
    // Perform search logic here
    console.log("Searching for:", searchQuery);
  };

  const handleCreateInvoice = () => {
    // Handle creating new invoice
    console.log("Create new invoice clicked");
  };

  const handleExportData = (type) => {
    // Handle exporting data
    console.log(`Exporting ${type} data`);
  };

  const viewInvoiceDetails = (invoiceId) => {
    // Handle viewing invoice details
    console.log("View invoice details:", invoiceId);
  };

  return (
    <div className="space-y-6">
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <h1 className="text-3xl font-bold tracking-tight">Billing & Payments</h1>
          <p className="text-muted-foreground">
            Manage invoices, transactions, and pricing
          </p>
        </div>
        <div className="flex items-center gap-2">
          <Button variant="outline" onClick={() => handleExportData("billing")}>
            <Download className="mr-2 h-4 w-4" /> Export
          </Button>
          <Button className="bg-accent hover:bg-accent/90" onClick={handleCreateInvoice}>
            <Plus className="mr-2 h-4 w-4" /> Create Invoice
          </Button>
        </div>
      </div>

      {/* Stats Overview */}
      <div className="grid gap-4 md:grid-cols-4">
        <Card>
          <CardHeader className="flex flex-row items-center justify-between pb-2 space-y-0">
            <CardTitle className="text-sm font-medium">Total Revenue</CardTitle>
            <DollarSign className="h-4 w-4 text-green-500" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">$42,856.32</div>
            <p className="text-xs text-muted-foreground">+12% from last month</p>
          </CardContent>
        </Card>
        <Card>
          <CardHeader className="flex flex-row items-center justify-between pb-2 space-y-0">
            <CardTitle className="text-sm font-medium">Pending Payments</CardTitle>
            <CreditCard className="h-4 w-4 text-yellow-500" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">$7,543.21</div>
            <p className="text-xs text-muted-foreground">15 invoices pending</p>
          </CardContent>
        </Card>
        <Card>
          <CardHeader className="flex flex-row items-center justify-between pb-2 space-y-0">
            <CardTitle className="text-sm font-medium">Active Customers</CardTitle>
            <Users className="h-4 w-4 text-blue-500" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">128</div>
            <p className="text-xs text-muted-foreground">+8 from last month</p>
          </CardContent>
        </Card>
        <Card>
          <CardHeader className="flex flex-row items-center justify-between pb-2 space-y-0">
            <CardTitle className="text-sm font-medium">Avg. Invoice</CardTitle>
            <FileText className="h-4 w-4 text-purple-500" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">$335.62</div>
            <p className="text-xs text-muted-foreground">+5% from last month</p>
          </CardContent>
        </Card>
      </div>

      <Card>
        <CardHeader className="pb-2">
          <CardTitle>Billing Management</CardTitle>
          <CardDescription>
            Manage invoices, transactions, and pricing plans
          </CardDescription>
        </CardHeader>
        <CardContent>
          <div className="flex flex-col md:flex-row gap-4 mb-6">
            <form onSubmit={handleSearch} className="flex-1 flex gap-2">
              <div className="relative flex-1">
                <Search className="absolute left-2.5 top-2.5 h-4 w-4 text-muted-foreground" />
                <Input
                  type="search"
                  placeholder="Search by invoice ID, customer name..."
                  className="pl-8"
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                />
              </div>
              <Button type="submit">Search</Button>
            </form>
            <div className="flex items-center gap-2">
              <Select 
                defaultValue={period} 
                onValueChange={setPeriod}
              >
                <SelectTrigger className="w-[180px]">
                  <Calendar className="mr-2 h-4 w-4" />
                  <SelectValue placeholder="Select period" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="thisMonth">This Month</SelectItem>
                  <SelectItem value="lastMonth">Last Month</SelectItem>
                  <SelectItem value="last3Months">Last 3 Months</SelectItem>
                  <SelectItem value="thisYear">This Year</SelectItem>
                  <SelectItem value="custom">Custom Range</SelectItem>
                </SelectContent>
              </Select>
              
              {period === "custom" && (
                <DatePickerWithRange 
                  className="w-[300px]"
                  selected={dateRange}
                  onSelect={setDateRange}
                />
              )}
            </div>
          </div>

          <Tabs defaultValue="invoices" className="space-y-4" onValueChange={setActiveTab}>
            <TabsList>
              <TabsTrigger value="invoices">Invoices</TabsTrigger>
              <TabsTrigger value="transactions">Transactions</TabsTrigger>
              <TabsTrigger value="tariffs">Tariff Plans</TabsTrigger>
              <TabsTrigger value="analytics">Analytics</TabsTrigger>
            </TabsList>

            {/* Invoices Tab */}
            <TabsContent value="invoices" className="space-y-4">
              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead>Invoice ID</TableHead>
                    <TableHead>Customer</TableHead>
                    <TableHead>Amount</TableHead>
                    <TableHead>Issue Date</TableHead>
                    <TableHead>Due Date</TableHead>
                    <TableHead>Sessions</TableHead>
                    <TableHead>Status</TableHead>
                    <TableHead>Actions</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {invoices.map((invoice) => (
                    <TableRow key={invoice.id}>
                      <TableCell className="font-medium">{invoice.id}</TableCell>
                      <TableCell>{invoice.customer}</TableCell>
                      <TableCell>{invoice.amount}</TableCell>
                      <TableCell>{invoice.date}</TableCell>
                      <TableCell>{invoice.dueDate}</TableCell>
                      <TableCell>{invoice.sessions}</TableCell>
                      <TableCell>{getStatusBadge(invoice.status)}</TableCell>
                      <TableCell>
                        <DropdownMenu>
                          <DropdownMenuTrigger asChild>
                            <Button variant="ghost" size="sm">
                              <MoreHorizontal className="h-4 w-4" />
                            </Button>
                          </DropdownMenuTrigger>
                          <DropdownMenuContent align="end">
                            <DropdownMenuItem onClick={() => viewInvoiceDetails(invoice.id)}>
                              <Eye className="mr-2 h-4 w-4" /> View Details
                            </DropdownMenuItem>
                            <DropdownMenuItem>
                              <Download className="mr-2 h-4 w-4" /> Download PDF
                            </DropdownMenuItem>
                            <DropdownMenuItem>
                              <RefreshCw className="mr-2 h-4 w-4" /> Mark as Paid
                            </DropdownMenuItem>
                          </DropdownMenuContent>
                        </DropdownMenu>
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </TabsContent>

            {/* Transactions Tab */}
            <TabsContent value="transactions" className="space-y-4">
              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead>Transaction ID</TableHead>
                    <TableHead>Customer</TableHead>
                    <TableHead>Amount</TableHead>
                    <TableHead>Date</TableHead>
                    <TableHead>Payment Method</TableHead>
                    <TableHead>Session ID</TableHead>
                    <TableHead>Status</TableHead>
                    <TableHead>Actions</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {transactions.map((transaction) => (
                    <TableRow key={transaction.id}>
                      <TableCell className="font-medium">{transaction.id}</TableCell>
                      <TableCell>{transaction.customer}</TableCell>
                      <TableCell>{transaction.amount}</TableCell>
                      <TableCell>{transaction.date}</TableCell>
                      <TableCell>{transaction.method}</TableCell>
                      <TableCell>{transaction.sessionId}</TableCell>
                      <TableCell>{getStatusBadge(transaction.status)}</TableCell>
                      <TableCell>
                        <DropdownMenu>
                          <DropdownMenuTrigger asChild>
                            <Button variant="ghost" size="sm">
                              <MoreHorizontal className="h-4 w-4" />
                            </Button>
                          </DropdownMenuTrigger>
                          <DropdownMenuContent align="end">
                            <DropdownMenuItem>
                              <Eye className="mr-2 h-4 w-4" /> View Details
                            </DropdownMenuItem>
                            <DropdownMenuItem>
                              <FileSpreadsheet className="mr-2 h-4 w-4" /> View Receipt
                            </DropdownMenuItem>
                          </DropdownMenuContent>
                        </DropdownMenu>
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </TabsContent>

            {/* Tariff Plans Tab */}
            <TabsContent value="tariffs" className="space-y-4">
              <div className="flex justify-end mb-4">
                <Button variant="outline" className="mr-2">
                  <Download className="mr-2 h-4 w-4" /> Export Plans
                </Button>
                <Button>
                  <Plus className="mr-2 h-4 w-4" /> New Tariff Plan
                </Button>
              </div>
              
              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead>Plan ID</TableHead>
                    <TableHead>Name</TableHead>
                    <TableHead>Energy Rate</TableHead>
                    <TableHead>Time Fee</TableHead>
                    <TableHead>Connection Fee</TableHead>
                    <TableHead>Status</TableHead>
                    <TableHead>Actions</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {tariffPlans.map((plan) => (
                    <TableRow key={plan.id}>
                      <TableCell className="font-medium">{plan.id}</TableCell>
                      <TableCell>{plan.name}</TableCell>
                      <TableCell>{plan.energyRate}</TableCell>
                      <TableCell>{plan.timeFee}</TableCell>
                      <TableCell>{plan.connectionFee}</TableCell>
                      <TableCell>{getStatusBadge(plan.status)}</TableCell>
                      <TableCell>
                        <DropdownMenu>
                          <DropdownMenuTrigger asChild>
                            <Button variant="ghost" size="sm">
                              <MoreHorizontal className="h-4 w-4" />
                            </Button>
                          </DropdownMenuTrigger>
                          <DropdownMenuContent align="end">
                            <DropdownMenuItem>
                              <Eye className="mr-2 h-4 w-4" /> View Details
                            </DropdownMenuItem>
                            <DropdownMenuItem>
                              <ChevronDown className="mr-2 h-4 w-4" /> Duplicate
                            </DropdownMenuItem>
                          </DropdownMenuContent>
                        </DropdownMenu>
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </TabsContent>

            {/* Analytics Tab */}
            <TabsContent value="analytics" className="space-y-4">
              <div className="grid gap-4 grid-cols-1 md:grid-cols-2">
                <Card>
                  <CardHeader>
                    <CardTitle>Revenue Trend</CardTitle>
                    <CardDescription>Monthly revenue over time</CardDescription>
                  </CardHeader>
                  <CardContent className="h-[350px]">
                    <AreaChart />
                  </CardContent>
                </Card>
                
                <Card>
                  <CardHeader>
                    <CardTitle>Payment Methods</CardTitle>
                    <CardDescription>Distribution of payment methods used</CardDescription>
                  </CardHeader>
                  <CardContent className="h-[350px]">
                    <BarChart />
                  </CardContent>
                </Card>
                
                <Card className="md:col-span-2">
                  <CardHeader>
                    <CardTitle>Invoice Status</CardTitle>
                    <CardDescription>Current status of all invoices</CardDescription>
                  </CardHeader>
                  <CardContent className="pt-4">
                    <div className="grid grid-cols-3 gap-4">
                      <div className="flex flex-col items-center p-4 bg-green-50 dark:bg-green-900/20 rounded-lg">
                        <span className="text-xl font-bold text-green-600 dark:text-green-400">75%</span>
                        <span className="text-sm text-muted-foreground">Paid</span>
                      </div>
                      <div className="flex flex-col items-center p-4 bg-yellow-50 dark:bg-yellow-900/20 rounded-lg">
                        <span className="text-xl font-bold text-yellow-600 dark:text-yellow-400">20%</span>
                        <span className="text-sm text-muted-foreground">Pending</span>
                      </div>
                      <div className="flex flex-col items-center p-4 bg-red-50 dark:bg-red-900/20 rounded-lg">
                        <span className="text-xl font-bold text-red-600 dark:text-red-400">5%</span>
                        <span className="text-sm text-muted-foreground">Overdue</span>
                      </div>
                    </div>
                  </CardContent>
                </Card>
              </div>
            </TabsContent>
          </Tabs>
        </CardContent>
        <CardFooter className="flex justify-between">
          <div className="text-sm text-muted-foreground">
            Showing {activeTab === "invoices" ? invoices.length : activeTab === "transactions" ? transactions.length : tariffPlans.length} items
          </div>
          <div className="flex gap-1">
            <Button variant="outline" size="sm">Previous</Button>
            <Button variant="outline" size="sm">Next</Button>
          </div>
        </CardFooter>
      </Card>
    </div>
  );
} 