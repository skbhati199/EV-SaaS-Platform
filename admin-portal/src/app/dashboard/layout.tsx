'use client';

import React from 'react';
import Link from 'next/link';
import { usePathname, useRouter } from 'next/navigation';
import { ThemeToggle } from '@/components/ThemeToggle';
import { useAuth } from '../context/AuthContext';
import { Button } from '@/components/ui/button';
import {
  LayoutDashboard,
  Plug,
  Users,
  CreditCard,
  Share2,
  Zap,
  Bell,
  BarChart2,
  Settings,
  LogOut,
  User,
} from 'lucide-react';

const navLinks = [
  { href: '/dashboard', label: 'Dashboard', icon: LayoutDashboard },
  { href: '/dashboard/stations', label: 'Stations', icon: Plug },
  { href: '/dashboard/users', label: 'Users', icon: Users },
  { href: '/dashboard/billing', label: 'Billing', icon: CreditCard },
  { href: '/dashboard/roaming', label: 'Roaming', icon: Share2 },
  { href: '/dashboard/smart-charging', label: 'Smart Charging', icon: Zap },
  { href: '/dashboard/notifications', label: 'Notifications', icon: Bell },
  { href: '/dashboard/analytics', label: 'Analytics', icon: BarChart2 },
  { href: '/dashboard/settings', label: 'Settings', icon: Settings },
];

export default function DashboardLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  const pathname = usePathname();
  const router = useRouter();
  const { logout } = useAuth();

  const handleLogout = () => {
    logout();
    router.push('/login');
  };

  return (
    <div className="flex min-h-screen bg-gradient-to-br from-white to-green-50 dark:bg-gradient-to-br dark:from-gray-900 dark:to-green-950/40">
      <aside className="w-64 bg-white/90 dark:bg-gray-900/90 backdrop-blur-sm shadow-md flex flex-col border-r border-gray-200 dark:border-gray-800">
        <div className="h-16 flex items-center justify-between px-6 font-bold text-xl border-b border-gray-200 dark:border-gray-800">
          <span>EV SaaS Admin</span>
          <ThemeToggle />
        </div>
        <nav className="flex-1 py-4">
          <ul className="space-y-1">
            {navLinks.map(({ href, label, icon: Icon }) => (
              <li key={href}>
                <Link
                  href={href}
                  className={`flex items-center gap-3 px-6 py-2 rounded-l-full transition-colors ${
                    pathname === href
                      ? 'bg-accent text-white font-semibold'
                      : 'text-gray-700 dark:text-gray-200 hover:bg-accent/20'
                  }`}
                >
                  <Icon className="w-5 h-5" />
                  {label}
                </Link>
              </li>
            ))}
          </ul>
        </nav>
        <div className="p-4 border-t border-gray-200 dark:border-gray-800">
          <Button 
            variant="ghost" 
            className="w-full flex items-center justify-start gap-2 text-red-500 hover:text-red-600 hover:bg-red-100 dark:hover:bg-red-900/20"
            onClick={handleLogout}
          >
            <LogOut className="w-4 h-4" />
            Logout
          </Button>
        </div>
      </aside>
      <div className="flex-1 flex flex-col">
        <header className="h-16 border-b border-gray-200 dark:border-gray-800 bg-white/80 dark:bg-gray-900/80 backdrop-blur-sm flex items-center justify-between px-8">
          <h1 className="text-xl font-semibold">EV SaaS Platform</h1>
          <div className="flex items-center gap-4">
            <Button variant="ghost" size="sm" className="flex items-center gap-2">
              <User className="w-4 h-4" />
              Admin User
            </Button>
          </div>
        </header>
        <main className="flex-1 p-8 overflow-auto">
          {children}
        </main>
      </div>
    </div>
  );
}
