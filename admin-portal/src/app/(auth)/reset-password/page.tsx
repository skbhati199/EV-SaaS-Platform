'use client';

import { useSearchParams } from 'next/navigation';
import ResetPasswordForm from '@/app/components/auth/ResetPasswordForm';

export default function ResetPasswordPage() {
  const searchParams = useSearchParams();
  const token = searchParams.get('token');

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-100">
      <ResetPasswordForm token={token || ''} />
    </div>
  );
} 