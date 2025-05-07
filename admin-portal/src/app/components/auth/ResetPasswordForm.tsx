'use client';

import { useState } from 'react';
import { useRouter } from 'next/navigation';
import Link from 'next/link';
import authService from '@/app/services/authService';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { AlertCircle, CheckCircle2, Loader2 } from 'lucide-react';

interface ResetPasswordFormProps {
  token: string;
}

export default function ResetPasswordForm({ token }: ResetPasswordFormProps) {
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [isSubmitted, setIsSubmitted] = useState(false);
  const [error, setError] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  
  const router = useRouter();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    
    if (!token) {
      setError('Invalid or missing reset token. Please try again or request a new reset link.');
      return;
    }
    
    if (password !== confirmPassword) {
      setError('Passwords do not match');
      return;
    }
    
    if (password.length < 8) {
      setError('Password must be at least 8 characters long');
      return;
    }
    
    setIsLoading(true);
    
    try {
      await authService.resetPassword(token, password);
      setIsSubmitted(true);
    } catch (err: any) {
      console.error('Password reset error:', err);
      setError(err.response?.data?.message || err.message || 'Failed to reset password. Please try again.');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="bg-card dark:bg-card/5 p-8 rounded-xl shadow-md dark:shadow-lg border border-border w-full max-w-md">
      <h2 className="text-2xl font-bold mb-2 text-foreground">Reset password</h2>
      <p className="text-sm text-muted-foreground mb-6">Enter your new password below</p>
      
      {!token && (
        <div className="bg-destructive/10 dark:bg-destructive/20 border-l-4 border-destructive p-4 text-destructive flex items-start gap-3 mb-4 rounded">
          <AlertCircle className="h-5 w-5 mt-0.5 flex-shrink-0" />
          <p className="text-sm">Invalid or missing reset token. Please request a new password reset link.</p>
        </div>
      )}
      
      {error && (
        <div className="bg-destructive/10 dark:bg-destructive/20 border-l-4 border-destructive p-4 text-destructive flex items-start gap-3 mb-4 rounded">
          <AlertCircle className="h-5 w-5 mt-0.5 flex-shrink-0" />
          <p className="text-sm">{error}</p>
        </div>
      )}
      
      {isSubmitted ? (
        <div className="space-y-4">
          <div className="bg-success/10 dark:bg-success/20 border-l-4 border-success p-4 text-success flex items-start gap-3 rounded">
            <CheckCircle2 className="h-5 w-5 mt-0.5 flex-shrink-0" />
            <p className="text-sm">Your password has been successfully reset.</p>
          </div>
          <p className="text-center text-sm text-muted-foreground">
            You can now sign in with your new password.
          </p>
          <Button className="w-full" asChild>
            <Link href="/login">Go to login</Link>
          </Button>
        </div>
      ) : (
        <form onSubmit={handleSubmit} className="space-y-4">
          <div className="space-y-2">
            <Label htmlFor="password">New password</Label>
            <Input
              id="password"
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              className="bg-background dark:bg-background/50"
              minLength={8}
              required
            />
            <p className="text-xs text-muted-foreground">
              Must be at least 8 characters long
            </p>
          </div>
          
          <div className="space-y-2">
            <Label htmlFor="confirmPassword">Confirm new password</Label>
            <Input
              id="confirmPassword"
              type="password"
              value={confirmPassword}
              onChange={(e) => setConfirmPassword(e.target.value)}
              className="bg-background dark:bg-background/50"
              required
            />
          </div>
          
          <Button
            type="submit"
            disabled={isLoading || !token}
            className="w-full"
          >
            {isLoading ? (
              <>
                <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                Resetting password...
              </>
            ) : (
              'Reset password'
            )}
          </Button>
          
          <div className="text-center text-sm text-muted-foreground">
            <Link href="/login" className="text-primary hover:text-primary/90 dark:text-primary/90 dark:hover:text-primary font-medium">
              Back to login
            </Link>
          </div>
        </form>
      )}
    </div>
  );
} 