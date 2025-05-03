import { ApiService } from './api';
import { PaginatedResponse } from './userService';

export type PaymentProvider = 'RAZORPAY' | 'STRIPE';

export interface PaymentMethod {
  id: string;
  type: 'CARD' | 'UPI' | 'BANK_ACCOUNT' | 'WALLET';
  provider: PaymentProvider;
  details: {
    last4?: string;
    brand?: string;
    expiryMonth?: string;
    expiryYear?: string;
    holderName?: string;
    upiId?: string;
    walletType?: string;
  };
  isDefault: boolean;
  createdAt: string;
}

export interface Invoice {
  id: string;
  userId: string;
  amount: number;
  tax: number;
  total: number;
  currency: string;
  status: 'DRAFT' | 'PENDING' | 'PAID' | 'CANCELLED' | 'FAILED';
  dueDate: string;
  items: InvoiceItem[];
  paymentId?: string;
  paymentProvider?: PaymentProvider;
  createdAt: string;
  paidAt?: string;
}

export interface InvoiceItem {
  id: string;
  description: string;
  quantity: number;
  unitPrice: number;
  amount: number;
  type: 'CHARGE_SESSION' | 'SUBSCRIPTION' | 'PENALTY' | 'OTHER';
  metadata?: Record<string, any>;
}

export interface Subscription {
  id: string;
  userId: string;
  planId: string;
  status: 'ACTIVE' | 'CANCELED' | 'PENDING' | 'PAST_DUE';
  startDate: string;
  endDate: string;
  renewalDate?: string;
  amount: number;
  currency: string;
  interval: 'MONTHLY' | 'YEARLY';
  paymentMethodId: string;
  createdAt: string;
  updatedAt: string;
}

export interface SubscriptionPlan {
  id: string;
  name: string;
  description: string;
  features: string[];
  price: number;
  currency: string;
  interval: 'MONTHLY' | 'YEARLY';
  isActive: boolean;
}

export interface PaymentInitiateRequest {
  amount: number;
  currency: string;
  invoiceId?: string;
  provider: PaymentProvider;
  description?: string;
  metadata?: Record<string, any>;
}

export interface PaymentInitiateResponse {
  paymentId: string;
  orderId?: string; // For Razorpay
  clientSecret?: string; // For Stripe
  amount: number;
  currency: string;
  provider: PaymentProvider;
  apiKey?: string; // Public key for the provider
}

export interface RazorpayVerificationRequest {
  paymentId: string;
  orderId: string;
  signature: string;
}

export interface RazorpayInitiateRecurringRequest {
  customerId: string;
  planId: string;
  totalCount: number;
  currency: string;
  amount: number;
  description?: string;
}

export interface StripeSetupIntentResponse {
  clientSecret: string;
  setupIntentId: string;
}

class BillingService extends ApiService {
  constructor() {
    super();
    // Use billing-service as base URL
    this.api.defaults.baseURL = `${this.api.defaults.baseURL}/api/billing`;
  }

  // Payment Methods
  async getPaymentMethods(): Promise<PaymentMethod[]> {
    return this.get<PaymentMethod[]>('/payment-methods');
  }

  async addStripePaymentMethod(paymentMethodId: string): Promise<PaymentMethod> {
    return this.post<PaymentMethod>('/payment-methods/stripe', { paymentMethodId });
  }

  async addRazorpayPaymentMethod(paymentMethodId: string): Promise<PaymentMethod> {
    return this.post<PaymentMethod>('/payment-methods/razorpay', { paymentMethodId });
  }

  async deletePaymentMethod(id: string): Promise<void> {
    return this.delete(`/payment-methods/${id}`);
  }

  async setDefaultPaymentMethod(id: string): Promise<PaymentMethod> {
    return this.post<PaymentMethod>(`/payment-methods/${id}/default`);
  }

  // Invoices
  async getInvoices(page = 0, size = 10): Promise<PaginatedResponse<Invoice>> {
    return this.get<PaginatedResponse<Invoice>>('/invoices', {
      params: { page, size }
    });
  }

  async getInvoice(id: string): Promise<Invoice> {
    return this.get<Invoice>(`/invoices/${id}`);
  }

  async getInvoicePdf(id: string): Promise<Blob> {
    return this.get<Blob>(`/invoices/${id}/pdf`, {
      responseType: 'blob'
    });
  }

  // Payments
  async initiatePayment(paymentRequest: PaymentInitiateRequest): Promise<PaymentInitiateResponse> {
    return this.post<PaymentInitiateResponse>('/payments/initiate', paymentRequest);
  }

  async verifyRazorpayPayment(request: RazorpayVerificationRequest): Promise<Invoice> {
    return this.post<Invoice>('/payments/razorpay/verify', request);
  }

  async confirmStripePayment(paymentIntentId: string): Promise<Invoice> {
    return this.post<Invoice>('/payments/stripe/confirm', {
      paymentIntentId
    });
  }
  
  // Stripe-specific methods
  async createStripeSetupIntent(): Promise<StripeSetupIntentResponse> {
    return this.post<StripeSetupIntentResponse>('/payments/stripe/setup-intent');
  }
  
  // Razorpay-specific methods
  async initiateRazorpayRecurring(request: RazorpayInitiateRecurringRequest): Promise<PaymentInitiateResponse> {
    return this.post<PaymentInitiateResponse>('/payments/razorpay/recurring', request);
  }
  
  async createRazorpayCustomer(name: string, email: string, contact: string): Promise<{ customerId: string }> {
    return this.post<{ customerId: string }>('/payments/razorpay/customer', {
      name,
      email,
      contact
    });
  }

  // Subscriptions
  async getSubscriptionPlans(): Promise<SubscriptionPlan[]> {
    return this.get<SubscriptionPlan[]>('/subscription-plans');
  }

  async getCurrentSubscription(): Promise<Subscription | null> {
    return this.get<Subscription | null>('/subscriptions/current');
  }

  async subscribeToNewPlan(planId: string, paymentMethodId: string): Promise<Subscription> {
    return this.post<Subscription>('/subscriptions', {
      planId,
      paymentMethodId
    });
  }

  async cancelSubscription(subscriptionId: string): Promise<Subscription> {
    return this.post<Subscription>(`/subscriptions/${subscriptionId}/cancel`);
  }

  async changePlan(subscriptionId: string, newPlanId: string): Promise<Subscription> {
    return this.post<Subscription>(`/subscriptions/${subscriptionId}/change-plan`, {
      newPlanId
    });
  }
  
  // Analytics & Reporting
  async getBillingStats(startDate: string, endDate: string): Promise<{
    totalRevenue: number;
    newSubscriptions: number;
    activeSubscriptions: number;
    averageInvoiceAmount: number;
  }> {
    return this.get('/analytics/stats', {
      params: { startDate, endDate }
    });
  }
  
  async getRevenueByPeriod(period: 'day' | 'week' | 'month', startDate: string, endDate: string): Promise<{
    periods: string[];
    revenue: number[];
  }> {
    return this.get('/analytics/revenue', {
      params: { period, startDate, endDate }
    });
  }
}

export default new BillingService(); 