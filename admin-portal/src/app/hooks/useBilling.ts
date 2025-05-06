'use client';

import { useState } from 'react';
import billingService from '../services/billingService';

export const useBilling = () => {
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // Billing Plans
  const getAllBillingPlans = async () => {
    setIsLoading(true);
    setError(null);
    try {
      const plans = await billingService.getAllBillingPlans();
      setIsLoading(false);
      return plans;
    } catch (err: any) {
      setError(err.message);
      setIsLoading(false);
      throw err;
    }
  };

  const getActiveBillingPlans = async () => {
    setIsLoading(true);
    setError(null);
    try {
      const plans = await billingService.getActiveBillingPlans();
      setIsLoading(false);
      return plans;
    } catch (err: any) {
      setError(err.message);
      setIsLoading(false);
      throw err;
    }
  };

  const createBillingPlan = async (planData: any) => {
    setIsLoading(true);
    setError(null);
    try {
      const plan = await billingService.createBillingPlan(planData);
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
      const subscriptions = await billingService.getUserSubscriptions(userId);
      setIsLoading(false);
      return subscriptions;
    } catch (err: any) {
      setError(err.message);
      setIsLoading(false);
      throw err;
    }
  };

  const createSubscription = async (subscriptionData: any) => {
    setIsLoading(true);
    setError(null);
    try {
      const subscription = await billingService.createSubscription(subscriptionData);
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
      const result = await billingService.cancelSubscription(subscriptionId);
      setIsLoading(false);
      return result;
    } catch (err: any) {
      setError(err.message);
      setIsLoading(false);
      throw err;
    }
  };

  // Invoices
  const getUserInvoices = async (userId: string) => {
    setIsLoading(true);
    setError(null);
    try {
      const invoices = await billingService.getUserInvoices(userId);
      setIsLoading(false);
      return invoices;
    } catch (err: any) {
      setError(err.message);
      setIsLoading(false);
      throw err;
    }
  };

  // Payment Methods
  const getUserPaymentMethods = async (userId: string) => {
    setIsLoading(true);
    setError(null);
    try {
      const paymentMethods = await billingService.getUserPaymentMethods(userId);
      setIsLoading(false);
      return paymentMethods;
    } catch (err: any) {
      setError(err.message);
      setIsLoading(false);
      throw err;
    }
  };

  const addPaymentMethod = async (paymentMethodData: any) => {
    setIsLoading(true);
    setError(null);
    try {
      const paymentMethod = await billingService.addPaymentMethod(paymentMethodData);
      setIsLoading(false);
      return paymentMethod;
    } catch (err: any) {
      setError(err.message);
      setIsLoading(false);
      throw err;
    }
  };

  // Billing Settings
  const getOrganizationBillingSettings = async (organizationId: string) => {
    setIsLoading(true);
    setError(null);
    try {
      const settings = await billingService.getOrganizationBillingSettings(organizationId);
      setIsLoading(false);
      return settings;
    } catch (err: any) {
      setError(err.message);
      setIsLoading(false);
      throw err;
    }
  };

  const updateBillingSettings = async (settingsId: string, settingsData: any) => {
    setIsLoading(true);
    setError(null);
    try {
      const settings = await billingService.updateBillingSettings(settingsId, settingsData);
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
