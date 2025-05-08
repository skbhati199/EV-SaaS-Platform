'use client';

import { Suspense } from 'react';
import { useSearchParams } from 'next/navigation';
import ResetPasswordForm from '@/app/components/auth/ResetPasswordForm';
import { ThemeToggle } from '@/components/ThemeToggle';
import ClientOnly from '@/components/ui/ClientOnly';

function ResetPasswordContent() {
  const searchParams = useSearchParams();
  const token = searchParams.get('token');

  return (
    <ResetPasswordForm token={token || ''} />
  );
}

export default function ResetPasswordPage() {
  return (
    <div className="flex min-h-screen flex-col items-center justify-center p-6 bg-gradient-to-br from-background to-secondary/20 dark:from-background dark:to-background">
      <div className="absolute top-4 right-4">
        <ThemeToggle />
      </div>
      <div className="w-full max-w-md space-y-6">
        <div className="text-center">
          <h1 className="text-3xl font-bold tracking-tight">EV SaaS Platform</h1>
          <h2 className="mt-2 text-xl font-semibold text-muted-foreground">Reset Password</h2>
        </div>
        <Suspense fallback={<div>Loading...</div>}>
          <ClientOnly>
            <ResetPasswordContent />
          </ClientOnly>
        </Suspense>
      </div>
    </div>
  );
} 