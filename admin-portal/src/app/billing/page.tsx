'use client';

import { Suspense } from 'react';
import ProtectedRoute from '../components/auth/ProtectedRoute';
import ClientOnly from '@/components/ui/ClientOnly';
import dynamic from 'next/dynamic';

// Dynamically import the billing component with no SSR
const BillingContent = dynamic(() => import('@/app/components/billing/BillingContent'), { 
  ssr: false,
  loading: () => (
    <div className="flex justify-center items-center py-12">
      <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-blue-500"></div>
    </div>
  )
});

export default function BillingPage() {
  return (
    <ProtectedRoute>
      <Suspense fallback={
        <div className="flex justify-center items-center py-12">
          <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-blue-500"></div>
        </div>
      }>
        <ClientOnly>
          <BillingContent />
        </ClientOnly>
      </Suspense>
    </ProtectedRoute>
  );
}
