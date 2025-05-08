import axios from 'axios';

const API_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080';

// Type definitions for billing data
interface BillingPlan {
  id?: string;
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

interface Subscription {
  id?: string;
  userId: string;
  organizationId?: string;
  planId: string;
  billingCycle: 'MONTHLY' | 'YEARLY';
  autoRenew: boolean;
  startDate?: string;
  endDate?: string;
  status?: string;
  paymentMethodId?: string;
}

interface PaymentMethod {
  id?: string;
  userId: string;
  type: string;
  provider: string;
  tokenId: string;
  lastFour: string;
  expiryMonth: string;
  expiryYear: string;
  isDefault: boolean;
}

interface Invoice {
  id: string;
  userId: string;
  subscriptionId: string;
  amount: number;
  status: string;
  dueDate: string;
  createdAt: string;
  paidAt?: string;
  items: any[];
}

interface Payment {
  invoiceId: string;
  amount: number;
  paymentMethod: string;
  transactionId: string;
}

interface BillingSettings {
  organizationId: string;
  billingEmail: string;
  taxId?: string;
  billingAddress: {
    street: string;
    city: string;
    state: string;
    zip: string;
    country: string;
  };
  currency: string;
}

// Service implementation
export const billingService = {
  // Authorization header helper
  getAuthHeader(token: string) {
    return {
      headers: {
        Authorization: `Bearer ${token}`
      }
    };
  },

  // Billing Plans
  async createBillingPlan(token: string, plan: BillingPlan): Promise<BillingPlan> {
    try {
      const response = await axios.post(
        `${API_URL}/api/v1/billing/plans`, 
        plan, 
        this.getAuthHeader(token)
      );
      return response.data;
    } catch (error) {
      throw error;
    }
  },

  async getAllBillingPlans(token: string): Promise<BillingPlan[]> {
    try {
      const response = await axios.get(
        `${API_URL}/api/v1/billing/plans`, 
        this.getAuthHeader(token)
      );
      return response.data;
    } catch (error) {
      throw error;
    }
  },

  async getActiveBillingPlans(token: string): Promise<BillingPlan[]> {
    try {
      const response = await axios.get(
        `${API_URL}/api/v1/billing/plans/active`, 
        this.getAuthHeader(token)
      );
      return response.data;
    } catch (error) {
      throw error;
    }
  },

  // Subscriptions
  async createSubscription(token: string, subscription: Subscription): Promise<Subscription> {
    try {
      const response = await axios.post(
        `${API_URL}/api/v1/billing/subscriptions`, 
        subscription, 
        this.getAuthHeader(token)
      );
      return response.data;
    } catch (error) {
      throw error;
    }
  },

  async getUserSubscriptions(token: string, userId: string): Promise<Subscription[]> {
    try {
      const response = await axios.get(
        `${API_URL}/api/v1/billing/subscriptions/user/${userId}`, 
        this.getAuthHeader(token)
      );
      return response.data;
    } catch (error) {
      throw error;
    }
  },

  async cancelSubscription(token: string, subscriptionId: string): Promise<any> {
    try {
      const response = await axios.put(
        `${API_URL}/api/v1/billing/subscriptions/${subscriptionId}/cancel`, 
        {}, 
        this.getAuthHeader(token)
      );
      return response.data;
    } catch (error) {
      throw error;
    }
  },

  // Invoices
  async getUserInvoices(token: string, userId: string): Promise<Invoice[]> {
    try {
      const response = await axios.get(
        `${API_URL}/api/v1/billing/invoices/user/${userId}`, 
        this.getAuthHeader(token)
      );
      return response.data;
    } catch (error) {
      throw error;
    }
  },

  // Payments
  async createPayment(token: string, payment: Payment): Promise<any> {
    try {
      const response = await axios.post(
        `${API_URL}/api/v1/billing/payments`, 
        payment, 
        this.getAuthHeader(token)
      );
      return response.data;
    } catch (error) {
      throw error;
    }
  },

  // Payment Methods
  async addPaymentMethod(token: string, paymentMethod: PaymentMethod): Promise<any> {
    try {
      const response = await axios.post(
        `${API_URL}/api/v1/billing/payment-methods`, 
        paymentMethod, 
        this.getAuthHeader(token)
      );
      return response.data;
    } catch (error) {
      throw error;
    }
  },

  async getUserPaymentMethods(token: string, userId: string): Promise<PaymentMethod[]> {
    try {
      const response = await axios.get(
        `${API_URL}/api/v1/billing/payment-methods/user/${userId}`, 
        this.getAuthHeader(token)
      );
      return response.data;
    } catch (error) {
      throw error;
    }
  },

  // Billing Settings
  async getOrganizationBillingSettings(token: string, organizationId: string): Promise<BillingSettings> {
    try {
      const response = await axios.get(
        `${API_URL}/api/v1/billing/settings/organization/${organizationId}`, 
        this.getAuthHeader(token)
      );
      return response.data;
    } catch (error) {
      throw error;
    }
  },

  async updateBillingSettings(token: string, settingsId: string, settings: BillingSettings): Promise<BillingSettings> {
    try {
      const response = await axios.put(
        `${API_URL}/api/v1/billing/settings/${settingsId}`, 
        settings, 
        this.getAuthHeader(token)
      );
      return response.data;
    } catch (error) {
      throw error;
    }
  },

  // Usage Records
  async recordUsage(token: string, data: { subscriptionId: string, meterType: string, quantity: number }): Promise<any> {
    try {
      const response = await axios.post(
        `${API_URL}/api/v1/billing/usage-records`, 
        data, 
        this.getAuthHeader(token)
      );
      return response.data;
    } catch (error) {
      throw error;
    }
  },

  // Payment Gateway Integration
  async initiatePayment(token: string, data: { 
    amount: number; 
    currency: string; 
    provider: string; 
    description?: string; 
    metadata?: Record<string, any>;
  }): Promise<any> {
    try {
      const response = await axios.post(
        `${API_URL}/api/v1/billing/payments/initiate`, 
        data, 
        this.getAuthHeader(token)
      );
      return response.data;
    } catch (error) {
      throw error;
    }
  },

  async verifyRazorpayPayment(token: string, data: {
    paymentId: string;
    orderId: string;
    signature: string;
  }): Promise<any> {
    try {
      const response = await axios.post(
        `${API_URL}/api/v1/billing/payments/verify/razorpay`, 
        data, 
        this.getAuthHeader(token)
      );
      return response.data;
    } catch (error) {
      throw error;
    }
  }
};

export default billingService;
