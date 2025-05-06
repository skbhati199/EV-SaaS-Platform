'use client';

import { useState } from 'react';
import { useAuth } from '../../hooks/useAuth';

const TwoFactorSetup = () => {
  const [verificationCode, setVerificationCode] = useState('');
  const [setupComplete, setSetupComplete] = useState(false);
  const { setup2FA, enable2FA, twoFactorSetup, isLoading, error, clearError } = useAuth();

  const handleSetup = async () => {
    clearError();
    try {
      await setup2FA();
    } catch (err) {
      console.error('2FA setup error:', err);
    }
  };

  const handleVerify = async (e: React.FormEvent) => {
    e.preventDefault();
    clearError();
    
    if (!twoFactorSetup?.secret) {
      alert('Please set up 2FA first');
      return;
    }
    
    try {
      const result = await enable2FA(twoFactorSetup.secret, verificationCode);
      if (result) {
        setSetupComplete(true);
      }
    } catch (err) {
      console.error('2FA verification error:', err);
    }
  };

  return (
    <div className="max-w-md mx-auto mt-10 p-6 bg-white rounded-lg shadow-md">
      <h2 className="text-2xl font-bold mb-6 text-center">Two-Factor Authentication</h2>
      
      {error && (
        <div className="mb-4 p-3 bg-red-100 text-red-700 rounded">
          {error}
        </div>
      )}
      
      {setupComplete ? (
        <div className="text-center p-4 bg-green-100 text-green-700 rounded mb-4">
          <p className="font-bold">2FA Setup Complete!</p>
          <p>Your account is now protected with two-factor authentication.</p>
        </div>
      ) : (
        <>
          {!twoFactorSetup ? (
            <div className="text-center">
              <p className="mb-4">Enhance your account security by enabling two-factor authentication.</p>
              <button
                onClick={handleSetup}
                className="bg-blue-500 text-white py-2 px-4 rounded hover:bg-blue-600 focus:outline-none focus:ring-2 focus:ring-blue-500 disabled:opacity-50"
                disabled={isLoading}
              >
                {isLoading ? 'Processing...' : 'Set Up 2FA'}
              </button>
            </div>
          ) : (
            <div>
              <div className="mb-6">
                <p className="mb-2">1. Scan this QR code with your authenticator app:</p>
                <div className="flex justify-center mb-4">
                  <img 
                    src={twoFactorSetup.qrCodeImage} 
                    alt="QR Code for 2FA" 
                    className="border border-gray-300 p-2 rounded"
                  />
                </div>
                
                <p className="mb-2">2. Or manually enter this code in your app:</p>
                <div className="bg-gray-100 p-3 rounded text-center font-mono mb-4">
                  {twoFactorSetup.secret}
                </div>
                
                <p className="mb-4">3. Enter the verification code from your app:</p>
                
                <form onSubmit={handleVerify}>
                  <div className="mb-4">
                    <input
                      type="text"
                      value={verificationCode}
                      onChange={(e) => setVerificationCode(e.target.value)}
                      className="w-full px-3 py-2 border border-gray-300 rounded focus:outline-none focus:ring-2 focus:ring-blue-500"
                      placeholder="Enter 6-digit code"
                      required
                    />
                  </div>
                  
                  <button
                    type="submit"
                    className="w-full bg-blue-500 text-white py-2 px-4 rounded hover:bg-blue-600 focus:outline-none focus:ring-2 focus:ring-blue-500 disabled:opacity-50"
                    disabled={isLoading}
                  >
                    {isLoading ? 'Verifying...' : 'Verify & Enable 2FA'}
                  </button>
                </form>
              </div>
            </div>
          )}
        </>
      )}
    </div>
  );
};

export default TwoFactorSetup;
