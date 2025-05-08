'use client';

import { Suspense } from 'react';
import LoginForm from '@/app/components/auth/LoginForm';
import { ThemeToggle } from '@/components/ThemeToggle';
import ClientOnly from '@/components/ui/ClientOnly';

export default function LoginPage() {
  return (
    <div className="flex min-h-screen flex-col items-center justify-center p-6 bg-gradient-to-br from-background to-secondary/20 dark:from-background dark:to-background">
      <div className="absolute top-4 right-4">
        <ThemeToggle />
      </div>
      <div className="w-full max-w-md space-y-6">
        <div className="text-center">
          <h1 className="text-3xl font-bold tracking-tight">EV SaaS Platform</h1>
          <h2 className="mt-2 text-xl font-semibold text-muted-foreground">Admin Portal</h2>
        </div>
        <Suspense fallback={<div>Loading...</div>}>
          <ClientOnly>
            <LoginForm />
          </ClientOnly>
        </Suspense>
      </div>
    </div>
  );
}
