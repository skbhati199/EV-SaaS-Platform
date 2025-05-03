'use client';

import { useState } from 'react';
import { useStripe, useElements, CardElement } from '@stripe/react-stripe-js';
import { processPayment } from './RazorpayProvider';
import { processStripePayment, confirmStripePayment } from './StripeProvider';

interface PaymentFormProps {
  amount: number;
  currency: string;
  description: string;
  metadata?: Record<string, any>;
  onSuccess: (invoice: any) => void;
  onError: (error: any) => void;
}

export default function PaymentForm({
  amount,
  currency,
  description,
  metadata,
  onSuccess,
  onError
}: PaymentFormProps) {
  const [isLoading, setIsLoading] = useState(false);
  const [paymentMethod, setPaymentMethod] = useState<'STRIPE' | 'RAZORPAY'>('STRIPE');
  const [error, setError] = useState<string | null>(null);
  
  // Stripe specific
  const stripe = useStripe();
  const elements = useElements();

  const handleStripeSubmit = async (event: React.FormEvent) => {
    event.preventDefault();

    if (!stripe || !elements) {
      setError('Stripe not loaded');
      return;
    }

    setIsLoading(true);
    setError(null);

    try {
      // Get client secret
      const clientSecret = await processStripePayment(amount, currency, description, metadata);
      
      if (!clientSecret) {
        throw new Error('Failed to initialize Stripe payment');
      }

      // Confirm card payment
      const cardElement = elements.getElement(CardElement);
      if (!cardElement) {
        throw new Error('Card element not found');
      }

      const { error: stripeError, paymentIntent } = await stripe.confirmCardPayment(clientSecret, {
        payment_method: {
          card: cardElement,
          billing_details: {
            name: 'Test User', // You should collect this from the user
          },
        },
      });

      if (stripeError) {
        throw new Error(stripeError.message);
      }

      if (paymentIntent && paymentIntent.status === 'succeeded') {
        // Confirm payment on backend
        const invoice = await confirmStripePayment(paymentIntent.id);
        onSuccess(invoice);
      } else {
        throw new Error('Payment failed');
      }
    } catch (error) {
      console.error('Payment error:', error);
      setError(error instanceof Error ? error.message : 'Payment processing failed');
      onError(error);
    } finally {
      setIsLoading(false);
    }
  };

  const handleRazorpaySubmit = () => {
    setIsLoading(true);
    setError(null);

    processPayment(
      amount,
      currency,
      description,
      metadata,
      (invoice) => {
        setIsLoading(false);
        onSuccess(invoice);
      },
      (error) => {
        console.error('Razorpay error:', error);
        setError(error.message || 'Payment processing failed');
        setIsLoading(false);
        onError(error);
      }
    );
  };

  return (
    <div className="p-6 bg-white rounded-lg shadow-md">
      <div className="mb-4">
        <h2 className="text-xl font-semibold mb-2">Payment Details</h2>
        <p className="text-gray-600">
          Amount: {new Intl.NumberFormat('en-US', { style: 'currency', currency }).format(amount / 100)}
        </p>
        <p className="text-gray-600">Description: {description}</p>
      </div>

      {error && (
        <div className="bg-red-50 border-l-4 border-red-500 p-4 mb-4 text-red-700">
          <p>{error}</p>
        </div>
      )}

      <div className="mb-6">
        <label className="block text-sm font-medium text-gray-700 mb-2">Select Payment Method</label>
        <div className="flex space-x-4">
          <button
            type="button"
            onClick={() => setPaymentMethod('STRIPE')}
            className={`px-4 py-2 rounded ${
              paymentMethod === 'STRIPE'
                ? 'bg-blue-600 text-white'
                : 'bg-gray-200 text-gray-800'
            }`}
          >
            Credit/Debit Card
          </button>
          <button
            type="button"
            onClick={() => setPaymentMethod('RAZORPAY')}
            className={`px-4 py-2 rounded ${
              paymentMethod === 'RAZORPAY'
                ? 'bg-blue-600 text-white'
                : 'bg-gray-200 text-gray-800'
            }`}
          >
            Razorpay
          </button>
        </div>
      </div>

      {paymentMethod === 'STRIPE' ? (
        <form onSubmit={handleStripeSubmit}>
          <div className="mb-4">
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Card Details
            </label>
            <div className="border rounded-md p-3 bg-white">
              <CardElement
                options={{
                  style: {
                    base: {
                      fontSize: '16px',
                      color: '#424770',
                      '::placeholder': {
                        color: '#aab7c4',
                      },
                    },
                    invalid: {
                      color: '#9e2146',
                    },
                  },
                }}
              />
            </div>
          </div>
          <button
            type="submit"
            disabled={!stripe || isLoading}
            className="w-full bg-blue-600 text-white py-2 px-4 rounded-md hover:bg-blue-700 disabled:bg-blue-300"
          >
            {isLoading ? 'Processing...' : 'Pay with Card'}
          </button>
        </form>
      ) : (
        <button
          type="button"
          onClick={handleRazorpaySubmit}
          disabled={isLoading}
          className="w-full bg-blue-600 text-white py-2 px-4 rounded-md hover:bg-blue-700 disabled:bg-blue-300"
        >
          {isLoading ? 'Processing...' : 'Pay with Razorpay'}
        </button>
      )}
    </div>
  );
} 