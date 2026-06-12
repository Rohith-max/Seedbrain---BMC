'use client';

import React, { useState } from 'react';
import { useAuthStore } from '@/lib/store/auth-store';
import { useRouter } from 'next/navigation';
import { Bell, LogOut, Settings, ChevronDown } from 'lucide-react';
import { GlobalSearch } from './global-search';
import { NotificationCenter } from './notification-center';
import { motion, AnimatePresence } from 'framer-motion';

export function Topbar() {
  const { user, logout } = useAuthStore();
  const router = useRouter();
  const [isProfileOpen, setIsProfileOpen] = useState(false);

  const handleLogout = () => {
    document.cookie = "nidhi-token=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;";
    logout();
    router.push('/login');
  };

  return (
    <header className="h-16 border-b border-nidhi-border/50 bg-nidhi-black/95 flex items-center justify-between px-6 flex-shrink-0 z-30">
      
      {/* Search & Global Intelligence */}
      <div className="flex-1 max-w-xl">
        <GlobalSearch />
      </div>

      {/* Right Section */}
      <div className="flex items-center gap-3 md:gap-4">
        {/* Notification Bell (Live Feed) */}
        <NotificationCenter />

        {/* Divider */}
        <div className="hidden md:block h-6 w-px bg-nidhi-border/30" />

        {/* Profile Menu */}
        <div className="relative">
          <motion.button
            whileHover={{ scale: 1.02 }}
            onClick={() => setIsProfileOpen(!isProfileOpen)}
            className="flex items-center gap-2 px-3 h-10 rounded-lg bg-nidhi-surface/60 hover:bg-nidhi-card border border-nidhi-border-subtle transition-all group"
          >
            {/* Avatar */}
            <div className="w-8 h-8 rounded-lg bg-gradient-to-br from-nidhi-gold to-nidhi-gold-dim flex items-center justify-center text-nidhi-black font-bold text-sm flex-shrink-0">
              {user?.name?.charAt(0) || 'U'}
            </div>

            {/* Name (desktop only) */}
            <div className="hidden md:flex flex-col items-start">
              <span className="text-xs font-semibold text-nidhi-text leading-tight">
                {user?.name?.split(' ')[0] || 'User'}
              </span>
              <span className="text-[10px] text-nidhi-text-secondary">
                {user?.role || 'Head'}
              </span>
            </div>

            {/* Chevron */}
            <motion.div
              animate={{ rotate: isProfileOpen ? 180 : 0 }}
              transition={{ duration: 0.2 }}
              className="hidden md:block"
            >
              <ChevronDown className="w-3.5 h-3.5 text-nidhi-text-secondary" />
            </motion.div>
          </motion.button>

          {/* Dropdown Menu */}
          <AnimatePresence>
            {isProfileOpen && (
              <motion.div
                initial={{ opacity: 0, y: -10, scale: 0.95 }}
                animate={{ opacity: 1, y: 0, scale: 1 }}
                exit={{ opacity: 0, y: -10, scale: 0.95 }}
                transition={{ duration: 0.2 }}
                className="absolute right-0 mt-2 w-48 rounded-xl bg-nidhi-surface border border-nidhi-gold/20 overflow-hidden shadow-2xl z-50"
              >
                {/* User Info */}
                <div className="px-4 py-3 border-b border-nidhi-border/30 bg-nidhi-surface/40">
                  <p className="text-xs font-semibold text-nidhi-gold mb-1">Signed in as</p>
                  <p className="text-sm font-medium text-nidhi-text truncate">
                    {user?.name || 'User'}
                  </p>
                </div>

                {/* Menu Items */}
                <div className="p-2 space-y-1">
                  <motion.button
                    whileHover={{ x: 4 }}
                    onClick={() => router.push('/dashboard/settings')}
                    className="w-full flex items-center gap-3 px-3 py-2 rounded-lg text-sm text-nidhi-text hover:bg-nidhi-card transition-colors"
                  >
                    <Settings className="w-4 h-4 text-nidhi-gold" />
                    Settings
                  </motion.button>

                  <motion.button
                    whileHover={{ x: 4 }}
                    onClick={handleLogout}
                    className="w-full flex items-center gap-3 px-3 py-2 rounded-lg text-sm text-nidhi-danger hover:bg-nidhi-danger/10 transition-colors"
                  >
                    <LogOut className="w-4 h-4" />
                    Sign Out
                  </motion.button>
                </div>
              </motion.div>
            )}
          </AnimatePresence>
        </div>
      </div>
    </header>
  );
}
