'use client';

import { Suspense } from 'react';
import ProtectedRoute from '../components/auth/ProtectedRoute';
import ClientOnly from '@/components/ui/ClientOnly';
import dynamic from 'next/dynamic';

// Dynamically import the profile component with no SSR
const ProfileContent = dynamic(() => import('@/app/components/profile/ProfileContent'), { 
  ssr: false,
  loading: () => (
    <div className="flex justify-center items-center py-12">
      <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-blue-500"></div>
    </div>
  )
});

export default function ProfilePage() {
  return (
    <ProtectedRoute>
      <Suspense fallback={
        <div className="flex justify-center items-center py-12">
          <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-blue-500"></div>
        </div>
      }>
        <ClientOnly>
          <ProfileContent />
        </ClientOnly>
      </Suspense>
    </ProtectedRoute>
  );
}
