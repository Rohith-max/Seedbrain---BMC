'use client';

import React from 'react';
import Link from 'next/link';
import { usePathname } from 'next/navigation';
import { APP_NAME, APP_NAME_HINDI, NAV_ITEMS } from '@/lib/constants';
import * as Icons from 'lucide-react';

export function Sidebar() {
  const pathname = usePathname();

  return (
    <aside className="w-64 border-r border-nidhi-border bg-nidhi-black h-screen sticky top-0 flex flex-col hidden md:flex">
      <div className="p-6">
        <Link href="/dashboard" className="inline-block">
          <span className="text-2xl font-display font-bold tracking-wider">
            {APP_NAME} <span className="text-nidhi-gold">{APP_NAME_HINDI}</span>
          </span>
        </Link>
      </div>

      <div className="flex-1 overflow-y-auto py-4 px-3 space-y-1">
        {NAV_ITEMS.map((item) => {
          const Icon = Icons[item.icon as keyof typeof Icons] as React.ElementType;
          // Dashboard home needs exact match, others can be prefix match
          const isActive = item.href === '/dashboard' 
            ? pathname === '/dashboard' 
            : pathname.startsWith(item.href);

          return (
            <Link
              key={item.href}
              href={item.href}
              className={`sidebar-nav-item ${isActive ? 'active' : ''}`}
            >
              {Icon && <Icon className="w-5 h-5" />}
              {item.label}
            </Link>
          );
        })}
      </div>

      <div className="p-4 border-t border-nidhi-border">
        <div className="card-premium p-4 rounded-xl bg-gradient-to-br from-nidhi-gold/10 to-transparent border-nidhi-gold/20">
          <div className="flex items-center gap-2 mb-2">
            <Icons.Sparkles className="w-4 h-4 text-nidhi-gold" />
            <span className="text-sm font-semibold text-nidhi-gold">Nidhi AI Active</span>
          </div>
          <p className="text-xs text-nidhi-text-secondary">
            Monitoring deadlines and analyzing benefits in background.
          </p>
        </div>
      </div>
    </aside>
  );
}
