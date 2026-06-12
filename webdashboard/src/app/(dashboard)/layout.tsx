import React from 'react';
import { AppSidebar } from '@/components/dashboard/app-sidebar';
import { Topbar } from '@/components/dashboard/topbar';

export default function DashboardLayout({ children }: { children: React.ReactNode }) {
  return (
    <div className="flex h-screen bg-nidhi-black overflow-hidden">
      {/* Sidebar */}
      <AppSidebar />

      {/* Right side: topbar + scrollable content */}
      <div className="flex flex-col flex-1 min-w-0 overflow-hidden">
        <Topbar />
        <main className="flex-1 overflow-y-auto min-w-0">
          {children}
        </main>
      </div>
    </div>
  );
}

