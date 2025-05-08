'use client';

import { useState } from 'react';
import { useRouter } from 'next/navigation';
import { useAuth } from '@/app/hooks/useAuth';
import Link from 'next/link';
import { Loader2, AlertCircle } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Checkbox } from '@/components/ui/checkbox';
import { Label } from '@/components/ui/label';

export default function LoginForm() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [twoFactorCode, setTwoFactorCode] = useState('');
  const [showTwoFactor, setShowTwoFactor] = useState(false);
  const [error, setError] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [rememberMe, setRememberMe] = useState(false);
  
  const { login, verify2FA } = useAuth();
  const router = useRouter();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setIsLoading(true);
    
    try {
      const result = await login(email, password);
      
      if (result.requiresTwoFactor) {
        setShowTwoFactor(true);
      } else {
        router.push('/dashboard');
      }
    } catch (err: any) {
      console.error('Login error:', err);
      setError(err.response?.data?.message || err.message || 'Failed to login. Please check your credentials.');
    } finally {
      setIsLoading(false);
    }
  };

  const handleTwoFactorSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setIsLoading(true);
    
    try {
      await verify2FA(twoFactorCode);
      router.push('/dashboard');
    } catch (err: any) {
      console.error('2FA verification error:', err);
      setError(err.response?.data?.message || err.message || 'Invalid verification code. Please try again.');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="bg-card dark:bg-card/5 p-8 rounded-xl shadow-md dark:shadow-lg border border-border w-full max-w-md">
      <h2 className="text-2xl font-bold mb-2 text-foreground">Sign in</h2>
      <p className="text-sm text-muted-foreground mb-6">Enter your credentials to access your account</p>
      
      {error && (
        <div className="bg-destructive/10 dark:bg-destructive/20 border-l-4 border-destructive p-4 text-destructive flex items-start gap-3 mb-4 rounded">
          <AlertCircle className="h-5 w-5 mt-0.5 flex-shrink-0" />
          <p className="text-sm">{error}</p>
        </div>
      )}
      
      {!showTwoFactor ? (
        <form onSubmit={handleSubmit} className="space-y-4">
          <div className="space-y-2">
            <Label htmlFor="email">Email</Label>
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
          
          <div className="space-y-2">
            <div className="flex items-center justify-between">
              <Label htmlFor="password">Password</Label>
              <Link href="/forgot-password" className="text-xs text-primary hover:text-primary/90 dark:text-primary/90 dark:hover:text-primary">
                Forgot password?
              </Link>
            </div>
            <Input
              id="password"
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              className="bg-background dark:bg-background/50"
              required
            />
          </div>
          
          <div className="flex items-center space-x-2">
            <Checkbox 
              id="remember" 
              checked={rememberMe}
              onCheckedChange={(checked) => setRememberMe(checked as boolean)}
            />
            <Label htmlFor="remember" className="text-sm font-normal cursor-pointer">Remember me</Label>
          </div>
          
          <Button
            type="submit"
            disabled={isLoading}
            className="w-full"
          >
            {isLoading ? (
              <>
                <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                Signing in...
              </>
            ) : (
              'Sign in'
            )}
          </Button>
          
          <div className="text-center text-sm text-muted-foreground">
            <span>Don&apos;t have an account? </span>
            <Link href="/register" className="text-primary hover:text-primary/90 dark:text-primary/90 dark:hover:text-primary font-medium">
              Create account
            </Link>
          </div>
        </form>
      ) : (
        <form onSubmit={handleTwoFactorSubmit} className="space-y-4">
          <div className="space-y-2">
            <Label htmlFor="twoFactorCode">Two-Factor Authentication Code</Label>
            <Input
              id="twoFactorCode"
              type="text"
              value={twoFactorCode}
              onChange={(e) => setTwoFactorCode(e.target.value)}
              className="bg-background dark:bg-background/50"
              placeholder="Enter 6-digit code"
              required
            />
            <p className="text-xs text-muted-foreground">
              Enter the verification code from your authenticator app
            </p>
          </div>
          
          <Button
            type="submit"
            disabled={isLoading}
            className="w-full"
          >
            {isLoading ? (
              <>
                <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                Verifying...
              </>
            ) : (
              'Verify'
            )}
          </Button>
        </form>
      )}
    </div>
  );
}
