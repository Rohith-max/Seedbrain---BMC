import React from 'react';
import { AppSidebar } from '@/components/dashboard/app-sidebar';

export default function DashboardLayout({ children }: { children: React.ReactNode }) {
  return (
    <div className="flex h-screen bg-nidhi-black overflow-hidden">
      {/* Sidebar */}
      <AppSidebar />

      {/* Main content area */}
      <main className="flex-1 overflow-y-auto min-w-0">
        {children}
      </main>
    </div>
  );
}
