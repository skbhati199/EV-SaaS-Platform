'use client';

import { useState, useEffect } from 'react';
import Image from 'next/image';
import { useAuth } from '../../hooks/useAuth';

export default function TwoFactorSetup() {
  const [isLoading, setIsLoading] = useState(false);
  const [qrCode, setQrCode] = useState('');
  const [secret, setSecret] = useState('');
  const [verificationCode, setVerificationCode] = useState('');
  const [is2FAEnabled, setIs2FAEnabled] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  const { user, setup2FA, enable2FA, disable2FA } = useAuth();

  useEffect(() => {
    if (user) {
      setIs2FAEnabled(user.twoFactorEnabled || false);
    }
  }, [user]);

  const handleSetup = async () => {
    setIsLoading(true);
    setError('');
    setSuccess('');
    
    try {
      const result = await setup2FA();
      setQrCode(result.qrCodeImage);
      setSecret(result.secret);
    } catch (err: any) {
      setError(err.message || 'Failed to set up two-factor authentication');
    } finally {
      setIsLoading(false);
    }
  };

  const handleEnable = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsLoading(true);
    setError('');
    setSuccess('');
    
    try {
      await enable2FA(secret, verificationCode);
      setIs2FAEnabled(true);
      setQrCode('');
      setSecret('');
      setVerificationCode('');
      setSuccess('Two-factor authentication has been enabled successfully');
    } catch (err: any) {
      setError(err.message || 'Failed to enable two-factor authentication');
    } finally {
      setIsLoading(false);
    }
  };

  const handleDisable = async () => {
    setIsLoading(true);
    setError('');
    setSuccess('');
    
    try {
      await disable2FA();
      setIs2FAEnabled(false);
      setSuccess('Two-factor authentication has been disabled');
    } catch (err: any) {
      setError(err.message || 'Failed to disable two-factor authentication');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div>
      {error && (
        <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mb-4">
          {error}
        </div>
      )}
      
      {success && (
        <div className="bg-green-100 border border-green-400 text-green-700 px-4 py-3 rounded mb-4">
          {success}
        </div>
      )}

      {is2FAEnabled ? (
        <div>
          <p className="mb-4">Two-factor authentication is currently <span className="font-bold text-green-600">enabled</span> for your account.</p>
          <button
            onClick={handleDisable}
            className="bg-red-500 text-white py-2 px-4 rounded hover:bg-red-600 focus:outline-none focus:ring-2 focus:ring-red-500"
            disabled={isLoading}
          >
            {isLoading ? 'Disabling...' : 'Disable Two-Factor Authentication'}
          </button>
        </div>
      ) : qrCode ? (
        <div>
          <p className="mb-4">Scan this QR code with your authenticator app:</p>
          <div className="mb-4">
            <Image
              src={qrCode}
              alt="QR Code for two-factor authentication"
              width={200}
              height={200}
            />
          </div>
          <p className="mb-4">Or enter this code manually: <span className="font-mono bg-gray-100 p-1 rounded">{secret}</span></p>
          
          <form onSubmit={handleEnable} className="mt-6">
            <div className="mb-4">
              <label className="block text-gray-700 text-sm font-bold mb-2" htmlFor="verificationCode">
                Verification Code
              </label>
              <input
                id="verificationCode"
                type="text"
                value={verificationCode}
                onChange={(e) => setVerificationCode(e.target.value)}
                className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
                placeholder="Enter 6-digit code"
                required
              />
            </div>
            
            <button
              type="submit"
              className="bg-blue-500 text-white py-2 px-4 rounded hover:bg-blue-600 focus:outline-none focus:ring-2 focus:ring-blue-500"
              disabled={isLoading}
            >
              {isLoading ? 'Verifying...' : 'Verify and Enable'}
            </button>
          </form>
        </div>
      ) : (
        <div>
          <p className="mb-4">Two-factor authentication adds an extra layer of security to your account by requiring a verification code from your mobile device.</p>
          <button
            onClick={handleSetup}
            className="bg-blue-500 text-white py-2 px-4 rounded hover:bg-blue-600 focus:outline-none focus:ring-2 focus:ring-blue-500"
            disabled={isLoading}
          >
            {isLoading ? 'Setting up...' : 'Set Up Two-Factor Authentication'}
          </button>
        </div>
      )}
    </div>
  );
}
