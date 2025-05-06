'use client';

import { useState, useEffect } from 'react';
import { useAuth } from '../hooks/useAuth';
import { useBilling } from '../hooks/useBilling';
import ProtectedRoute from '../components/auth/ProtectedRoute';

interface BillingPlan {
  id: string;
  name: string;
  description: string;
  priceMonthly: number;
  priceYearly: number;
  features: string;
  isActive: boolean;
  currency: string;
  energyRate: number;
  timeRate: number;
}

interface Invoice {
  id: string;
  userId: string;
  subscriptionId: string;
  amount: number;
  status: string;
  dueDate: string;
  paidDate: string | null;
  currency: string;
  invoiceNumber: string;
  createdAt: string;
}

export default function BillingPage() {
  const { user } = useAuth();
  const { 
    isLoading, 
    error, 
    getActiveBillingPlans, 
    getUserInvoices 
  } = useBilling();
  
  const [plans, setPlans] = useState<BillingPlan[]>([]);
  const [invoices, setInvoices] = useState<Invoice[]>([]);
  const [activeTab, setActiveTab] = useState('plans');

  useEffect(() => {
    const fetchBillingData = async () => {
      try {
        const plansData = await getActiveBillingPlans();
        setPlans(plansData);
        
        if (user?.id) {
          const invoicesData = await getUserInvoices(user.id);
          setInvoices(invoicesData);
        }
      } catch (err) {
        console.error('Error fetching billing data:', err);
      }
    };
    
    fetchBillingData();
  }, [user, getActiveBillingPlans, getUserInvoices]);

  const formatCurrency = (amount: number, currency: string) => {
    return new Intl.NumberFormat('en-US', { style: 'currency', currency }).format(amount);
  };

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString();
  };

  return (
    <ProtectedRoute>
      <div className="container mx-auto px-4 py-8">
        <h1 className="text-2xl font-bold mb-6">Billing & Payments</h1>
        
        {error && (
          <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mb-4">
            {error}
          </div>
        )}
        
        <div className="mb-6">
          <div className="border-b border-gray-200">
            <nav className="-mb-px flex">
              <button
                onClick={() => setActiveTab('plans')}
                className={`py-4 px-6 font-medium text-sm ${
                  activeTab === 'plans'
                    ? 'border-b-2 border-blue-500 text-blue-600'
                    : 'text-gray-500 hover:text-gray-700 hover:border-gray-300'
                }`}
              >
                Billing Plans
              </button>
              <button
                onClick={() => setActiveTab('invoices')}
                className={`py-4 px-6 font-medium text-sm ${
                  activeTab === 'invoices'
                    ? 'border-b-2 border-blue-500 text-blue-600'
                    : 'text-gray-500 hover:text-gray-700 hover:border-gray-300'
                }`}
              >
                Invoices
              </button>
            </nav>
          </div>
        </div>
        
        {isLoading ? (
          <div className="flex justify-center items-center py-12">
            <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-blue-500"></div>
          </div>
        ) : activeTab === 'plans' ? (
          <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-6">
            {plans.map((plan) => (
              <div key={plan.id} className="bg-white rounded-lg shadow-md p-6">
                <h3 className="text-xl font-semibold mb-2">{plan.name}</h3>
                <p className="text-gray-600 mb-4">{plan.description}</p>
                <div className="mb-4">
                  <div className="text-2xl font-bold">
                    {formatCurrency(plan.priceMonthly, plan.currency)}<span className="text-sm font-normal text-gray-500">/month</span>
                  </div>
                  <div className="text-gray-600">
                    {formatCurrency(plan.priceYearly, plan.currency)}<span className="text-sm text-gray-500">/year</span>
                  </div>
                </div>
                <div className="mb-4">
                  <div className="text-sm text-gray-600">
                    <span className="font-medium">Energy Rate:</span> {formatCurrency(plan.energyRate, plan.currency)}/kWh
                  </div>
                  <div className="text-sm text-gray-600">
                    <span className="font-medium">Time Rate:</span> {formatCurrency(plan.timeRate, plan.currency)}/hour
                  </div>
                </div>
                <div className="mb-4">
                  <h4 className="font-medium mb-2">Features:</h4>
                  <p className="text-gray-600 text-sm">{plan.features}</p>
                </div>
                <button className="w-full bg-blue-500 text-white py-2 px-4 rounded hover:bg-blue-600 focus:outline-none focus:ring-2 focus:ring-blue-500">
                  Subscribe
                </button>
              </div>
            ))}
            
            {plans.length === 0 && (
              <div className="col-span-full text-center py-12 text-gray-500">
                No billing plans available.
              </div>
            )}
          </div>
        ) : (
          <div className="bg-white rounded-lg shadow-md overflow-hidden">
            <table className="min-w-full divide-y divide-gray-200">
              <thead className="bg-gray-50">
                <tr>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Invoice #
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Date
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Amount
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Status
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Actions
                  </th>
                </tr>
              </thead>
              <tbody className="bg-white divide-y divide-gray-200">
                {invoices.map((invoice) => (
                  <tr key={invoice.id}>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <div className="text-sm font-medium text-gray-900">{invoice.invoiceNumber}</div>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <div className="text-sm text-gray-500">{formatDate(invoice.createdAt)}</div>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <div className="text-sm text-gray-900">{formatCurrency(invoice.amount, invoice.currency)}</div>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <span className={`px-2 inline-flex text-xs leading-5 font-semibold rounded-full ${
                        invoice.status === 'PAID' 
                          ? 'bg-green-100 text-green-800' 
                          : invoice.status === 'PENDING' 
                            ? 'bg-yellow-100 text-yellow-800' 
                            : 'bg-red-100 text-red-800'
                      }`}>
                        {invoice.status}
                      </span>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm font-medium">
                      <button className="text-blue-600 hover:text-blue-900 mr-4">View</button>
                      <button className="text-blue-600 hover:text-blue-900">Download</button>
                    </td>
                  </tr>
                ))}
                
                {invoices.length === 0 && (
                  <tr>
                    <td colSpan={5} className="px-6 py-12 text-center text-gray-500">
                      No invoices available.
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </ProtectedRoute>
  );
}
