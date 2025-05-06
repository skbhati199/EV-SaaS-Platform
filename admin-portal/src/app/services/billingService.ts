import axios from 'axios';

const API_BASE_URL = 'http://localhost:8085/api/v1/billing';

class BillingService {
  constructor(private baseUrl: string = API_BASE_URL) {}

  private getAuthHeader() {
    const accessToken = typeof window !== 'undefined' ? localStorage.getItem('accessToken') : null;
    return accessToken ? { Authorization: `Bearer ${accessToken}` } : {};
  }

  // Billing Plans
  async getAllBillingPlans() {
    try {
      const response = await axios.get(`${this.baseUrl}/plans`, {
        headers: this.getAuthHeader()
      });
      return response.data;
    } catch (error: any) {
      throw new Error(error.response?.data?.message || 'Failed to fetch billing plans');
    }
  }

  async getActiveBillingPlans() {
    try {
      const response = await axios.get(`${this.baseUrl}/plans/active`, {
        headers: this.getAuthHeader()
      });
      return response.data;
    } catch (error: any) {
      throw new Error(error.response?.data?.message || 'Failed to fetch active billing plans');
    }
  }

  async createBillingPlan(planData: any) {
    try {
      const response = await axios.post(`${this.baseUrl}/plans`, planData, {
        headers: this.getAuthHeader()
      });
      return response.data;
    } catch (error: any) {
      throw new Error(error.response?.data?.message || 'Failed to create billing plan');
    }
  }

  // Subscriptions
  async getUserSubscriptions(userId: string) {
    try {
      const response = await axios.get(`${this.baseUrl}/subscriptions/user/${userId}`, {
        headers: this.getAuthHeader()
      });
      return response.data;
    } catch (error: any) {
      throw new Error(error.response?.data?.message || 'Failed to fetch user subscriptions');
    }
  }

  async createSubscription(subscriptionData: any) {
    try {
      const response = await axios.post(`${this.baseUrl}/subscriptions`, subscriptionData, {
        headers: this.getAuthHeader()
      });
      return response.data;
    } catch (error: any) {
      throw new Error(error.response?.data?.message || 'Failed to create subscription');
    }
  }

  async cancelSubscription(subscriptionId: string) {
    try {
      const response = await axios.put(`${this.baseUrl}/subscriptions/${subscriptionId}/cancel`, null, {
        headers: this.getAuthHeader()
      });
      return response.data;
    } catch (error: any) {
      throw new Error(error.response?.data?.message || 'Failed to cancel subscription');
    }
  }

  // Invoices
  async getUserInvoices(userId: string) {
    try {
      const response = await axios.get(`${this.baseUrl}/invoices/user/${userId}`, {
        headers: this.getAuthHeader()
      });
      return response.data;
    } catch (error: any) {
      throw new Error(error.response?.data?.message || 'Failed to fetch user invoices');
    }
  }

  // Payment Methods
  async getUserPaymentMethods(userId: string) {
    try {
      const response = await axios.get(`${this.baseUrl}/payment-methods/user/${userId}`, {
        headers: this.getAuthHeader()
      });
      return response.data;
    } catch (error: any) {
      throw new Error(error.response?.data?.message || 'Failed to fetch payment methods');
    }
  }

  async addPaymentMethod(paymentMethodData: any) {
    try {
      const response = await axios.post(`${this.baseUrl}/payment-methods`, paymentMethodData, {
        headers: this.getAuthHeader()
      });
      return response.data;
    } catch (error: any) {
      throw new Error(error.response?.data?.message || 'Failed to add payment method');
    }
  }

  // Billing Settings
  async getOrganizationBillingSettings(organizationId: string) {
    try {
      const response = await axios.get(`${this.baseUrl}/settings/organization/${organizationId}`, {
        headers: this.getAuthHeader()
      });
      return response.data;
    } catch (error: any) {
      throw new Error(error.response?.data?.message || 'Failed to fetch billing settings');
    }
  }

  async updateBillingSettings(settingsId: string, settingsData: any) {
    try {
      const response = await axios.put(`${this.baseUrl}/settings/${settingsId}`, settingsData, {
        headers: this.getAuthHeader()
      });
      return response.data;
    } catch (error: any) {
      throw new Error(error.response?.data?.message || 'Failed to update billing settings');
    }
  }
}

const billingService = new BillingService();
export default billingService;
