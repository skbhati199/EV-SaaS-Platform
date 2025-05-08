'use client';

import { useState } from 'react';
import axios from 'axios';
import safeLocalStorage from '../services/localStorage';
import billingService from '../services/billingService';

const API_URL = process.env.NEXT_PUBLIC_API_URL || '/api';

// Define interfaces to match the exact structure expected by components
export interface BillingPlan {
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

export interface Invoice {
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

export const useBilling = () => {
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const getAccessToken = (): string | null => {
    return safeLocalStorage.getItem('accessToken');
  };

  // Billing Plans
  const getAllBillingPlans = async () => {
    setIsLoading(true);
    setError(null);
    try {
      const token = getAccessToken();
      if (!token) {
        throw new Error('Authentication required');
      }
      const plans = await billingService.getAllBillingPlans(token);
      setIsLoading(false);
      return plans;
    } catch (err: any) {
      setError(err.message);
      setIsLoading(false);
      throw err;
    }
  };

  const getActiveBillingPlans = async (): Promise<BillingPlan[]> => {
    try {
      setIsLoading(true);
      setError(null);
      
      const token = getAccessToken();
      if (!token) {
        throw new Error('Authentication required');
      }
      
      const response = await axios.get(`${API_URL}/v1/billing/plans/active`, {
        headers: {
          Authorization: `Bearer ${token}`
        }
      });
      
      return response.data || [];
    } catch (err) {
      console.error('Error getting billing plans:', err);
      setError(err instanceof Error ? err.message : 'Failed to fetch billing plans');
      return [];
    } finally {
      setIsLoading(false);
    }
  };

  const createBillingPlan = async (planData: BillingPlan) => {
    setIsLoading(true);
    setError(null);
    try {
      const token = getAccessToken();
      if (!token) {
        throw new Error('Authentication required');
      }
      const plan = await billingService.createBillingPlan(token, planData);
      setIsLoading(false);
      return plan;
    } catch (err: any) {
      setError(err.message);
      setIsLoading(false);
      throw err;
    }
  };

  // Subscriptions
  const getUserSubscriptions = async (userId: string) => {
    setIsLoading(true);
    setError(null);
    try {
      const token = getAccessToken();
      if (!token) {
        throw new Error('Authentication required');
      }
      const subscriptions = await billingService.getUserSubscriptions(token, userId);
      setIsLoading(false);
      return subscriptions;
    } catch (err: any) {
      setError(err.message);
      setIsLoading(false);
      throw err;
    }
  };

  // Define interfaces for the subscription data
  interface Subscription {
    id?: string;
    userId: string;
    planId: string;
    startDate: string;
    endDate?: string;
    status: string;
    paymentMethodId?: string;
    autoRenew: boolean;
    billingCycle: "MONTHLY" | "YEARLY";
  }

  const createSubscription = async (subscriptionData: Subscription) => {
    setIsLoading(true);
    setError(null);
    try {
      const token = getAccessToken();
      if (!token) {
        throw new Error('Authentication required');
      }
      const subscription = await billingService.createSubscription(token, subscriptionData);
      setIsLoading(false);
      return subscription;
    } catch (err: any) {
      setError(err.message);
      setIsLoading(false);
      throw err;
    }
  };

  const cancelSubscription = async (subscriptionId: string) => {
    setIsLoading(true);
    setError(null);
    try {
      const token = getAccessToken();
      if (!token) {
        throw new Error('Authentication required');
      }
      const result = await billingService.cancelSubscription(token, subscriptionId);
      setIsLoading(false);
      return result;
    } catch (err: any) {
      setError(err.message);
      setIsLoading(false);
      throw err;
    }
  };

  // Invoices
  const getUserInvoices = async (userId: string): Promise<Invoice[]> => {
    try {
      setIsLoading(true);
      setError(null);
      
      const token = getAccessToken();
      if (!token) {
        throw new Error('Authentication required');
      }
      
      const response = await axios.get(`${API_URL}/v1/billing/invoices/user/${userId}`, {
        headers: {
          Authorization: `Bearer ${token}`
        }
      });
      
      return response.data || [];
    } catch (err) {
      console.error('Error getting user invoices:', err);
      setError(err instanceof Error ? err.message : 'Failed to fetch invoices');
      return [];
    } finally {
      setIsLoading(false);
    }
  };

  // Payment Methods
  // Define interface for payment method
  interface PaymentMethod {
    id?: string;
    userId: string;
    type: string;
    provider: string;
    tokenId: string;
    lastFour: string;
    expiryMonth: string;
    expiryYear: string;
    cardNumber?: string;
    expiryDate?: string;
    cardholderName?: string;
    isDefault: boolean;
  }

  const getUserPaymentMethods = async (userId: string) => {
    setIsLoading(true);
    setError(null);
    try {
      const token = getAccessToken();
      if (!token) {
        throw new Error('Authentication required');
      }
      const paymentMethods = await billingService.getUserPaymentMethods(token, userId);
      setIsLoading(false);
      return paymentMethods;
    } catch (err: any) {
      setError(err.message);
      setIsLoading(false);
      throw err;
    }
  };

  const addPaymentMethod = async (paymentMethodData: PaymentMethod) => {
    setIsLoading(true);
    setError(null);
    try {
      const token = getAccessToken();
      if (!token) {
        throw new Error('Authentication required');
      }
      const paymentMethod = await billingService.addPaymentMethod(token, paymentMethodData);
      setIsLoading(false);
      return paymentMethod;
    } catch (err: any) {
      setError(err.message);
      setIsLoading(false);
      throw err;
    }
  };

  // Billing Settings
  // Define interface for billing settings
  interface BillingSettings {
    id?: string;
    organizationId: string;
    billingEmail: string;
    billingAddress: { 
      street: string; 
      city: string; 
      state: string; 
      zip: string; 
      country: string; 
    };
    taxId?: string;
    paymentTerms?: string;
    currency: string;
  }

  const getOrganizationBillingSettings = async (organizationId: string) => {
    setIsLoading(true);
    setError(null);
    try {
      const token = getAccessToken();
      if (!token) {
        throw new Error('Authentication required');
      }
      const settings = await billingService.getOrganizationBillingSettings(token, organizationId);
      setIsLoading(false);
      return settings;
    } catch (err: any) {
      setError(err.message);
      setIsLoading(false);
      throw err;
    }
  };

  const updateBillingSettings = async (settingsId: string, settingsData: BillingSettings) => {
    setIsLoading(true);
    setError(null);
    try {
      const token = getAccessToken();
      if (!token) {
        throw new Error('Authentication required');
      }
      const settings = await billingService.updateBillingSettings(token, settingsId, settingsData);
      setIsLoading(false);
      return settings;
    } catch (err: any) {
      setError(err.message);
      setIsLoading(false);
      throw err;
    }
  };

  return {
    isLoading,
    error,
    getAllBillingPlans,
    getActiveBillingPlans,
    createBillingPlan,
    getUserSubscriptions,
    createSubscription,
    cancelSubscription,
    getUserInvoices,
    getUserPaymentMethods,
    addPaymentMethod,
    getOrganizationBillingSettings,
    updateBillingSettings
  };
};
