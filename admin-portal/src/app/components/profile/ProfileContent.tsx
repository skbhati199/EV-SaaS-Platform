'use client';

import { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import { useAuth } from '@/app/hooks/useAuth';
import TwoFactorSetup from '@/app/components/auth/TwoFactorSetup';
import { Suspense } from 'react';

export default function ProfileContent() {
  const { user, isAuthenticated, logout } = useAuth();
  const router = useRouter();
  const [isMounted, setIsMounted] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    try {
      setIsMounted(true);
    } catch (err) {
      console.error('Error in ProfileContent:', err);
      setError('Failed to load profile information');
    }
  }, []);

  const handleLogout = () => {
    try {
      logout();
      router.push('/login');
    } catch (err) {
      console.error('Error during logout:', err);
      setError('Failed to log out. Please try again.');
    }
  };

  if (!isMounted) {
    return <div className="text-center py-4">Loading...</div>;
  }

  if (error) {
    return (
      <div className="text-center py-8">
        <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mb-4">
          {error}
        </div>
        <button 
          onClick={() => window.location.reload()}
          className="bg-blue-500 text-white py-2 px-4 rounded hover:bg-blue-600 focus:outline-none"
        >
          Reload Page
        </button>
      </div>
    );
  }

  // Ensure user is defined
  if (!user) {
    return (
      <div className="text-center py-8">
        <p>No user information available. Please log in again.</p>
        <button 
          onClick={() => router.push('/login')}
          className="mt-4 bg-blue-500 text-white py-2 px-4 rounded hover:bg-blue-600 focus:outline-none"
        >
          Go to Login
        </button>
      </div>
    );
  }

  return (
    <div className="container mx-auto px-4 py-8">
      <div className="max-w-4xl mx-auto">
        <div className="bg-white rounded-lg shadow-md p-6 mb-6">
          <div className="flex justify-between items-center mb-6">
            <h1 className="text-2xl font-bold">User Profile</h1>
            <button
              onClick={handleLogout}
              className="bg-red-500 text-white py-2 px-4 rounded hover:bg-red-600 focus:outline-none focus:ring-2 focus:ring-red-500"
            >
              Logout
            </button>
          </div>
          
          <div className="grid md:grid-cols-2 gap-6">
            <div>
              <h2 className="text-xl font-semibold mb-4">Account Information</h2>
              <div className="space-y-3">
                <div>
                  <span className="font-medium text-gray-700">Name: </span>
                  <span>{user.firstName || ''} {user.lastName || ''}</span>
                </div>
                <div>
                  <span className="font-medium text-gray-700">Email: </span>
                  <span>{user.email || ''}</span>
                </div>
                <div>
                  <span className="font-medium text-gray-700">Role: </span>
                  <span className="capitalize">{(user.role || '').toLowerCase()}</span>
                </div>
              </div>
            </div>
            
            <div>
              <h2 className="text-xl font-semibold mb-4">Security Settings</h2>
              <div className="mb-4">
                <button
                  onClick={() => router.push('/profile/change-password')}
                  className="bg-blue-500 text-white py-2 px-4 rounded hover:bg-blue-600 focus:outline-none focus:ring-2 focus:ring-blue-500"
                >
                  Change Password
                </button>
              </div>
            </div>
          </div>
        </div>
        
        <div className="bg-white rounded-lg shadow-md p-6">
          <h2 className="text-xl font-semibold mb-4">Two-Factor Authentication</h2>
          <Suspense fallback={<div>Loading 2FA setup...</div>}>
            <TwoFactorSetup />
          </Suspense>
        </div>
      </div>
    </div>
  );
} 