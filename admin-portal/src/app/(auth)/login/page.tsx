'use client';

import React, { useState } from 'react';
import Link from 'next/link';
import { useRouter } from 'next/navigation';
import { authService } from '@/app/services';
import { useAuthStore } from '@/app/store/authStore';
import { RoleType } from '@/app/services/authService';
import { ThemeToggle } from '@/components/ThemeToggle';

const mockLogin = async (email: string, password: string) => {
  // Mock authentication logic
  return email === 'admin@example.com' && password === 'password123';
};

export default function LoginPage() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const router = useRouter();
  const { login } = useAuthStore();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsLoading(true);
    setError('');

    try {
      const success = await mockLogin(email, password);
      if (success) {
        // Set a simple auth flag in localStorage (for demo)
        localStorage.setItem('isAuthenticated', 'true');
        router.push('/dashboard');
      } else {
        setError('Invalid credentials');
      }
    } catch (err) {
      console.error('Login error:', err);
      setError(err instanceof Error ? err.message : 'Invalid email or password');
    } finally {
      setIsLoading(false);
    }
  };

  const redirectBasedOnRole = (role: RoleType) => {
    switch (role) {
      case 'ADMIN':
        router.push('/dashboard');
        break;
      case 'OPERATOR':
        router.push('/dashboard/stations');
        break;
      case 'BILLING_ADMIN':
        router.push('/dashboard/billing');
        break;
      case 'SUPPORT':
        router.push('/dashboard/support');
        break;
      default:
        router.push('/dashboard');
    }
  };

  return (
    <div className="flex min-h-screen flex-col items-center justify-center p-6 bg-gray-50 dark:bg-gray-900">
      <div className="absolute top-4 right-4">
        <ThemeToggle />
      </div>
      <div className="w-full max-w-md space-y-8 bg-white dark:bg-gray-800 p-8 rounded-xl shadow-md">
        <div className="text-center">
          <h1 className="text-3xl font-bold tracking-tight text-gray-900 dark:text-gray-100">EV SaaS Platform</h1>
          <h2 className="mt-2 text-xl font-semibold text-gray-700 dark:text-gray-300">Admin Portal</h2>
          <p className="mt-2 text-sm text-gray-600 dark:text-gray-400">Sign in to your account</p>
        </div>

        {error && (
          <div className="bg-red-50 dark:bg-red-900/30 border-l-4 border-red-500 p-4 text-red-700 dark:text-red-400">
            <p>{error}</p>
          </div>
        )}

        <form className="mt-8 space-y-6" onSubmit={handleSubmit}>
          <div className="space-y-4 rounded-md shadow-sm">
            <div>
              <label htmlFor="email" className="block text-sm font-medium text-gray-700 dark:text-gray-300">
                Email address
              </label>
              <input
                id="email"
                name="email"
                type="email"
                autoComplete="email"
                required
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                className="mt-1 block w-full rounded-md border border-gray-300 dark:border-gray-600 px-3 py-2 shadow-sm focus:border-blue-500 focus:outline-none focus:ring-blue-500 bg-white dark:bg-gray-700 dark:text-white"
                placeholder="you@example.com"
              />
            </div>

            <div>
              <label htmlFor="password" className="block text-sm font-medium text-gray-700 dark:text-gray-300">
                Password
              </label>
              <input
                id="password"
                name="password"
                type="password"
                autoComplete="current-password"
                required
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                className="mt-1 block w-full rounded-md border border-gray-300 dark:border-gray-600 px-3 py-2 shadow-sm focus:border-blue-500 focus:outline-none focus:ring-blue-500 bg-white dark:bg-gray-700 dark:text-white"
                placeholder="••••••••"
              />
            </div>
          </div>

          <div className="flex items-center justify-between">
            <div className="flex items-center">
              <input
                id="remember-me"
                name="remember-me"
                type="checkbox"
                className="h-4 w-4 rounded border-gray-300 text-blue-600 focus:ring-blue-500 dark:border-gray-600 dark:bg-gray-700"
              />
              <label htmlFor="remember-me" className="ml-2 block text-sm text-gray-900 dark:text-gray-300">
                Remember me
              </label>
            </div>

            <div className="text-sm">
              <Link href="/forgot-password" className="font-medium text-blue-600 hover:text-blue-500 dark:text-blue-400 dark:hover:text-blue-300">
                Forgot your password?
              </Link>
            </div>
          </div>

          <div>
            <button
              type="submit"
              disabled={isLoading}
              className="group relative flex w-full justify-center rounded-md bg-accent px-3 py-2 text-sm font-semibold text-white hover:bg-accent/90 focus:outline-none focus:ring-2 focus:ring-accent focus:ring-offset-2 disabled:bg-accent/70 dark:bg-accent dark:hover:bg-accent/80"
            >
              {isLoading ? 'Signing in...' : 'Sign in'}
            </button>
          </div>
        </form>

        <div className="mt-6 text-center text-sm">
          <p className="text-gray-600 dark:text-gray-400">
            Don&apos;t have an account?{' '}
            <Link href="/register" className="font-medium text-blue-600 hover:text-blue-500 dark:text-blue-400 dark:hover:text-blue-300">
              Register now
            </Link>
          </p>
        </div>
      </div>
    </div>
  );
}
