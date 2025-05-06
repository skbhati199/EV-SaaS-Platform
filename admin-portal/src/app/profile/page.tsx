'use client';

import { useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { useAuth } from '../hooks/useAuth';
import TwoFactorSetup from '../components/auth/TwoFactorSetup';
import ProtectedRoute from '../components/auth/ProtectedRoute';

export default function ProfilePage() {
  const { user, isAuthenticated, logout } = useAuth();
  const router = useRouter();

  const handleLogout = () => {
    logout();
    router.push('/login');
  };

  return (
    <ProtectedRoute>
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
            
            {user ? (
              <div className="grid md:grid-cols-2 gap-6">
                <div>
                  <h2 className="text-xl font-semibold mb-4">Account Information</h2>
                  <div className="space-y-3">
                    <div>
                      <span className="font-medium text-gray-700">Name: </span>
                      <span>{user.firstName} {user.lastName}</span>
                    </div>
                    <div>
                      <span className="font-medium text-gray-700">Email: </span>
                      <span>{user.email}</span>
                    </div>
                    <div>
                      <span className="font-medium text-gray-700">Role: </span>
                      <span className="capitalize">{user.role.toLowerCase()}</span>
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
            ) : (
              <div className="text-center py-4">
                <p>Loading user information...</p>
              </div>
            )}
          </div>
          
          <div className="bg-white rounded-lg shadow-md p-6">
            <h2 className="text-xl font-semibold mb-4">Two-Factor Authentication</h2>
            <TwoFactorSetup />
          </div>
        </div>
      </div>
    </ProtectedRoute>
  );
}
