'use client';

import { useState } from 'react';
import RazorpayProvider from '@/app/components/payment/RazorpayProvider';
import StripeProvider from '@/app/components/payment/StripeProvider';
import PaymentForm from '@/app/components/payment/PaymentForm';
import { useRouter } from 'next/navigation';

export default function PaymentPage() {
  const router = useRouter();
  const [paymentSuccess, setPaymentSuccess] = useState(false);
  const [paymentError, setPaymentError] = useState<string | null>(null);
  const [invoice, setInvoice] = useState<any>(null);

  const handlePaymentSuccess = (invoiceData: any) => {
    setInvoice(invoiceData);
    setPaymentSuccess(true);
    setPaymentError(null);
    
    // Redirect to invoice page after 2 seconds
    setTimeout(() => {
      router.push(`/dashboard/billing/invoices/${invoiceData.id}`);
    }, 2000);
  };

  const handlePaymentError = (error: any) => {
    setPaymentError(error.message || 'Payment failed. Please try again.');
    setPaymentSuccess(false);
  };

  return (
    <div className="container mx-auto px-4 py-8">
      <h1 className="text-2xl font-bold mb-6">Make a Payment</h1>

      {paymentSuccess ? (
        <div className="bg-green-50 border-l-4 border-green-500 p-4 mb-4">
          <p className="text-green-700">Payment successful! Redirecting to invoice page...</p>
        </div>
      ) : null}

      {paymentError ? (
        <div className="bg-red-50 border-l-4 border-red-500 p-4 mb-4">
          <p className="text-red-700">{paymentError}</p>
        </div>
      ) : null}

      <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
        <div className="md:col-span-2">
          <RazorpayProvider>
            <StripeProvider>
              <PaymentForm
                amount={50000} // $500.00 (in cents)
                currency="USD"
                description="EV SaaS Platform Subscription"
                metadata={{ subscriptionType: 'premium' }}
                onSuccess={handlePaymentSuccess}
                onError={handlePaymentError}
              />
            </StripeProvider>
          </RazorpayProvider>
        </div>

        <div className="bg-gray-50 p-6 rounded-lg shadow-sm">
          <h2 className="text-xl font-semibold mb-4">Payment Summary</h2>
          <div className="space-y-3">
            <div className="flex justify-between">
              <span>Subscription Plan:</span>
              <span>Premium Plan</span>
            </div>
            <div className="flex justify-between">
              <span>Period:</span>
              <span>Monthly</span>
            </div>
            <div className="flex justify-between">
              <span>Subtotal:</span>
              <span>$480.00</span>
            </div>
            <div className="flex justify-between">
              <span>Tax (4.17%):</span>
              <span>$20.00</span>
            </div>
            <div className="border-t border-gray-300 my-2 pt-2 flex justify-between font-semibold">
              <span>Total:</span>
              <span>$500.00</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
} 