'use client';

import Link from 'next/link';
import { usePathname } from 'next/navigation';

export default function DashboardLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  const pathname = usePathname();

  const navigation = [
    { name: 'Dashboard', href: '/dashboard', current: pathname === '/dashboard' },
    { name: 'Stations', href: '/dashboard/stations', current: pathname.startsWith('/dashboard/stations') },
    { name: 'Users', href: '/dashboard/users', current: pathname.startsWith('/dashboard/users') },
    { name: 'Reports', href: '/dashboard/reports', current: pathname.startsWith('/dashboard/reports') },
    { name: 'Settings', href: '/dashboard/settings', current: pathname.startsWith('/dashboard/settings') },
  ];

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Header */}
      <header className="bg-white shadow">
        <div className="mx-auto max-w-7xl px-4 py-6 sm:px-6 lg:px-8 flex justify-between items-center">
          <h1 className="text-3xl font-bold tracking-tight text-gray-900">EV SaaS Platform</h1>
          <div className="flex items-center space-x-4">
            <span className="text-gray-700">Admin User</span>
            <Link href="/login" className="text-sm text-red-600 hover:text-red-800">Logout</Link>
          </div>
        </div>
      </header>

      <div className="flex">
        {/* Sidebar */}
        <div className="w-64 bg-white shadow-sm h-[calc(100vh-4rem)] fixed">
          <nav className="mt-5 px-2">
            <div className="space-y-1">
              {navigation.map((item) => (
                <Link
                  key={item.name}
                  href={item.href}
                  className={`${
                    item.current
                      ? 'bg-gray-100 text-gray-900'
                      : 'text-gray-600 hover:bg-gray-50 hover:text-gray-900'
                  } group flex items-center px-2 py-2 text-base font-medium rounded-md`}
                >
                  {item.name}
                </Link>
              ))}
            </div>
          </nav>
        </div>

        {/* Main content */}
        <div className="flex-1 pl-64">
          {children}
        </div>
      </div>
    </div>
  );
}
