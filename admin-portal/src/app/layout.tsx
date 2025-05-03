import './globals.css';
import type { Metadata } from 'next';
import { Inter } from 'next/font/google';
import { AuthProvider } from './context/AuthContext';
import { ThemeProvider } from '@/components/theme-provider';
import { ThemeStyles } from '@/components/theme-styles';

const inter = Inter({ subsets: ['latin'] });

export const metadata: Metadata = {
  title: 'EV SaaS Admin Portal',
  description: 'Admin portal for managing EVSE infrastructure',
};

export default function RootLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <html lang="en" suppressHydrationWarning>
      <body className={inter.className}>
        <ThemeProvider
          attribute="class"
          defaultTheme="system"
          enableSystem
          disableTransitionOnChange
        >
          <ThemeStyles />
          <AuthProvider>
            {children}
          </AuthProvider>
        </ThemeProvider>
      </body>
    </html>
  );
}
