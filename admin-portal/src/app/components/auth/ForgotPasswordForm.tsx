'use client';

import { useState } from 'react';
import { useRouter } from 'next/navigation';
import Link from 'next/link';
import authService from '@/app/services/authService';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { AlertCircle, CheckCircle2, Loader2 } from 'lucide-react';

export default function ForgotPasswordForm() {
  const [email, setEmail] = useState('');
  const [isSubmitted, setIsSubmitted] = useState(false);
  const [error, setError] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  
  const router = useRouter();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setIsLoading(true);
    
    try {
      await authService.forgotPassword(email);
      setIsSubmitted(true);
    } catch (err: any) {
      console.error('Forgot password error:', err);
      setError(err.response?.data?.message || err.message || 'Failed to send reset email. Please try again.');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="bg-card dark:bg-card/5 p-8 rounded-xl shadow-md dark:shadow-lg border border-border w-full max-w-md">
      <h2 className="text-2xl font-bold mb-2 text-foreground">Reset password</h2>
      <p className="text-sm text-muted-foreground mb-6">
        Enter your email address and we&apos;ll send you a link to reset your password
      </p>
      
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
            <p className="text-sm">Password reset link has been sent to your email.</p>
          </div>
          <p className="text-center text-sm text-muted-foreground">
            Check your inbox and follow the instructions to reset your password.
          </p>
          <Button className="w-full" asChild>
            <Link href="/login">Return to login</Link>
          </Button>
        </div>
      ) : (
        <form onSubmit={handleSubmit} className="space-y-4">
          <div className="space-y-2">
            <Label htmlFor="email">Email address</Label>
            <Input
              id="email"
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              placeholder="you@example.com"
              className="bg-background dark:bg-background/50"
              required
            />
          </div>
          
          <Button
            type="submit"
            disabled={isLoading}
            className="w-full"
          >
            {isLoading ? (
              <>
                <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                Sending reset link...
              </>
            ) : (
              'Send reset link'
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