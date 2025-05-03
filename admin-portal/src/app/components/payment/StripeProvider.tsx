'use client';

import { useState, useEffect } from 'react';
import { loadStripe } from '@stripe/stripe-js';
import { Elements } from '@stripe/react-stripe-js';
import { billingService } from '@/app/services';

// Initialize Stripe with the publishable key
// Replace with your actual key or fetch from environment
const stripePromise = loadStripe(process.env.NEXT_PUBLIC_STRIPE_PUBLISHABLE_KEY || '');

interface StripeProviderProps {
  children: React.ReactNode;
}

export default function StripeProvider({ children }: StripeProviderProps) {
  const [clientSecret, setClientSecret] = useState<string | null>(null);

  // If needed, initialize with a payment intent
  // This is often not needed for initial load, but can be useful for specific flows
  useEffect(() => {
    // You could optionally fetch a setup intent or payment intent here
    // For now we'll just use the stripePromise
  }, []);

  return (
    <Elements stripe={stripePromise} options={clientSecret ? { clientSecret } : undefined}>
      {children}
    </Elements>
  );
}

// Helper functions for Stripe payments
export const processStripePayment = async (
  amount: number,
  currency: string,
  description: string,
  metadata?: Record<string, any>,
  onSuccess?: (invoice: any) => void,
  onError?: (error: any) => void
) => {
  try {
    // Initiate payment with Stripe as the provider
    const paymentResponse = await billingService.initiatePayment({
      amount,
      currency,
      provider: 'STRIPE',
      description,
      metadata
    });
    
    if (!paymentResponse.clientSecret) {
      throw new Error('Missing client secret from payment initiation response');
    }
    
    // Return the client secret for the component to handle
    return paymentResponse.clientSecret;
  } catch (error) {
    onError && onError(error);
    return null;
  }
};

export const confirmStripePayment = async (
  paymentIntentId: string,
  onSuccess?: (invoice: any) => void,
  onError?: (error: any) => void
) => {
  try {
    const invoice = await billingService.confirmStripePayment(paymentIntentId);
    onSuccess && onSuccess(invoice);
    return invoice;
  } catch (error) {
    onError && onError(error);
    return null;
  }
}; 