'use client';

import { useEffect, useState, useCallback } from 'react';
import Script from 'next/script';
import { billingService } from '@/app/services';

declare global {
  interface Window {
    Razorpay: any;
  }
}

interface RazorpayProviderProps {
  children: React.ReactNode;
}

interface RazorpayPaymentOptions {
  amount: number;
  currency: string;
  orderId: string;
  description?: string;
  name?: string;
  email?: string;
  contact?: string;
  notes?: Record<string, string>;
  apiKey: string;
}

export const initializeRazorpayPayment = async (
  options: RazorpayPaymentOptions,
  onSuccess: (response: { razorpay_payment_id: string; razorpay_order_id: string; razorpay_signature: string }) => void,
  onFailure: (error: any) => void
) => {
  if (!window.Razorpay) {
    throw new Error('Razorpay SDK not loaded');
  }

  const razorpay = new window.Razorpay({
    key: options.apiKey,
    amount: options.amount,
    currency: options.currency,
    order_id: options.orderId,
    name: options.name || 'EV SaaS Platform',
    description: options.description || 'Payment for EV SaaS services',
    prefill: {
      email: options.email,
      contact: options.contact
    },
    notes: options.notes || {},
    theme: {
      color: '#3399cc'
    },
    handler: function (response: any) {
      onSuccess({
        razorpay_payment_id: response.razorpay_payment_id,
        razorpay_order_id: response.razorpay_order_id,
        razorpay_signature: response.razorpay_signature
      });
    }
  });

  razorpay.on('payment.failed', function (response: any) {
    onFailure(response.error);
  });

  razorpay.open();
};

export const processPayment = async (
  amount: number,
  currency: string,
  description: string,
  metadata?: Record<string, any>,
  onSuccess?: (invoice: any) => void,
  onError?: (error: any) => void
) => {
  try {
    // Initiate payment with Razorpay as the provider
    const paymentResponse = await billingService.initiatePayment({
      amount,
      currency,
      provider: 'RAZORPAY',
      description,
      metadata
    });
    
    if (!paymentResponse.orderId || !paymentResponse.apiKey) {
      throw new Error('Missing order ID or API key from payment initiation response');
    }
    
    // Initialize Razorpay payment
    initializeRazorpayPayment(
      {
        amount: paymentResponse.amount,
        currency: paymentResponse.currency,
        orderId: paymentResponse.orderId,
        description,
        apiKey: paymentResponse.apiKey
      },
      async (response) => {
        // Verify payment
        try {
          const invoice = await billingService.verifyRazorpayPayment({
            paymentId: response.razorpay_payment_id,
            orderId: response.razorpay_order_id,
            signature: response.razorpay_signature
          });
          onSuccess && onSuccess(invoice);
        } catch (verifyError) {
          onError && onError(verifyError);
        }
      },
      (error) => {
        onError && onError(error);
      }
    );
  } catch (error) {
    onError && onError(error);
  }
};

export default function RazorpayProvider({ children }: RazorpayProviderProps) {
  const [isRazorpayLoaded, setIsRazorpayLoaded] = useState(false);

  const handleRazorpayLoad = useCallback(() => {
    setIsRazorpayLoaded(true);
  }, []);

  return (
    <>
      <Script
        src="https://checkout.razorpay.com/v1/checkout.js"
        strategy="lazyOnload"
        onLoad={handleRazorpayLoad}
      />
      {children}
    </>
  );
} 