'use client';

import React, { useState } from 'react';
import Link from 'next/link';
import { usePathname, useRouter } from 'next/navigation';
import { motion, AnimatePresence } from 'framer-motion';
import {
  Home,
  Bot,
  Bell,
  Gift,
  Archive,
  Users,
  Network,
  BarChart3,
  Settings,
  LogOut,
  ChevronLeft,
  ChevronRight,
  Globe,
  Shield,
} from 'lucide-react';
import { useAuthStore } from '@/lib/store/auth-store';

const NAV = [
  { href: '/dashboard',                   icon: Home,      label: 'Home' },
  { href: '/dashboard/assistant',         icon: Bot,       label: 'AI Assistant' },
  { href: '/dashboard/alerts',            icon: Bell,      label: 'Alerts',    badge: '3' },
  { href: '/dashboard/benefits',          icon: Gift,      label: 'Benefits',  badge: '12' },
  { href: '/dashboard/vault',             icon: Archive,   label: 'Documents' },
  { href: '/dashboard/family',            icon: Users,     label: 'Family' },
  { href: '/dashboard/knowledge-graph',   icon: Network,   label: 'Knowledge Graph' },
  { href: '/dashboard/analytics',         icon: BarChart3, label: 'Analytics' },
  { href: '/dashboard/government-portals', icon: Globe,    label: 'Gov Portals' },
];

const GOLD_GRADIENT = 'linear-gradient(135deg, #D4AF37, #E8D080)';

export function AppSidebar() {
  const pathname = usePathname();
  const router = useRouter();
  const { user, logout } = useAuthStore();
  const [collapsed, setCollapsed] = useState(false);

  const handleLogout = () => {
    document.cookie = 'nidhi-token=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;';
    logout();
    router.push('/login');
  };

  const isActive = (href: string) =>
    href === '/dashboard' ? pathname === '/dashboard' : pathname.startsWith(href);

  return (
    <motion.aside
      initial={false}
      animate={{ width: collapsed ? 72 : 240 }}
      transition={{ duration: 0.28, ease: [0.4, 0, 0.2, 1] }}
      className="hidden md:flex flex-col h-screen bg-nidhi-surface border-r border-nidhi-border-subtle flex-shrink-0 overflow-visible z-30 relative"
    >
      {/* Brand Row */}
      <div className="px-4 pt-5 pb-4 flex items-center gap-3 min-h-[68px]">
        {/* Logo mark */}
        <div
          className="w-9 h-9 rounded-xl flex items-center justify-center flex-shrink-0 glow-gold-sm"
          style={{ background: GOLD_GRADIENT }}
        >
          <span className="text-nidhi-black font-bold text-base font-display select-none">न</span>
        </div>

        <AnimatePresence initial={false}>
          {!collapsed && (
            <motion.div
              key="brand-text"
              initial={{ opacity: 0, x: -8 }}
              animate={{ opacity: 1, x: 0 }}
              exit={{ opacity: 0, x: -8 }}
              transition={{ duration: 0.18 }}
              className="min-w-0 overflow-hidden"
            >
              <div className="text-[15px] font-display font-bold text-nidhi-text leading-tight tracking-wide">
                NIDHI
              </div>
              <div className="text-[10px] text-nidhi-text-muted tracking-widest uppercase leading-tight">
                निधि · Family OS
              </div>
            </motion.div>
          )}
        </AnimatePresence>
      </div>

      {/* AI Status Pill */}
      <AnimatePresence initial={false}>
        {!collapsed && (
          <motion.div
            key="status-pill"
            initial={{ opacity: 0, height: 0 }}
            animate={{ opacity: 1, height: 'auto' }}
            exit={{ opacity: 0, height: 0 }}
            transition={{ duration: 0.18 }}
            className="mx-3 mb-2"
          >
            <div className="flex items-center gap-2 px-3 py-2 rounded-xl bg-nidhi-card border border-nidhi-border-subtle">
              <span className="relative flex h-2 w-2 flex-shrink-0">
                <span className="animate-ping absolute inline-flex h-full w-full rounded-full bg-nidhi-success opacity-75" />
                <span className="relative inline-flex rounded-full h-2 w-2 bg-nidhi-success" />
              </span>
              <span className="text-[11px] text-nidhi-text-secondary font-medium truncate">
                AI Active — Monitoring
              </span>
            </div>
          </motion.div>
        )}
      </AnimatePresence>

      {/* Navigation */}
      <nav className="flex-1 px-3 space-y-1 overflow-y-auto hide-scrollbar py-2">
        {NAV.map((item) => {
          const active = isActive(item.href);
          const Icon = item.icon;
          return (
            <div key={item.href} title={collapsed ? item.label : undefined}>
              <Link
                href={item.href}
                className={`nav-item w-full ${active ? 'active' : ''}`}
              >
                <div className="relative flex-shrink-0">
                  <Icon
                    className={`w-5 h-5 transition-colors ${
                      active ? 'text-nidhi-gold' : 'text-nidhi-text-muted'
                    }`}
                  />
                  {item.badge && collapsed && (
                    <span className="absolute -top-1 -right-1 w-2 h-2 bg-nidhi-gold rounded-full" />
                  )}
                </div>

                <AnimatePresence initial={false}>
                  {!collapsed && (
                    <motion.div
                      key="label"
                      initial={{ opacity: 0 }}
                      animate={{ opacity: 1 }}
                      exit={{ opacity: 0 }}
                      transition={{ duration: 0.15 }}
                      className="flex items-center gap-2 flex-1 min-w-0 overflow-hidden"
                    >
                      <span className="truncate text-sm">{item.label}</span>
                      {item.badge && (
                        <span className="ml-auto flex-shrink-0 text-[10px] font-bold px-1.5 py-0.5 rounded-md bg-nidhi-gold/15 text-nidhi-gold border border-nidhi-gold/20">
                          {item.badge}
                        </span>
                      )}
                    </motion.div>
                  )}
                </AnimatePresence>
              </Link>
            </div>
          );
        })}
      </nav>

      {/* Bottom section */}
      <div className="px-3 pb-4 space-y-1 border-t border-nidhi-border-subtle pt-3">
        {/* Settings */}
        <div title={collapsed ? 'Settings' : undefined}>
          <Link
            href="/dashboard/settings"
            className={`nav-item w-full ${isActive('/dashboard/settings') ? 'active' : ''}`}
          >
            <Settings
              className={`w-5 h-5 flex-shrink-0 transition-colors ${
                isActive('/dashboard/settings') ? 'text-nidhi-gold' : 'text-nidhi-text-muted'
              }`}
            />
            <AnimatePresence initial={false}>
              {!collapsed && (
                <motion.span
                  key="settings-label"
                  initial={{ opacity: 0 }}
                  animate={{ opacity: 1 }}
                  exit={{ opacity: 0 }}
                  transition={{ duration: 0.15 }}
                  className="text-sm"
                >
                  Settings
                </motion.span>
              )}
            </AnimatePresence>
          </Link>
        </div>

        {/* User Card */}
        <AnimatePresence initial={false} mode="wait">
          {!collapsed ? (
            <motion.div
              key="user-card-expanded"
              initial={{ opacity: 0 }}
              animate={{ opacity: 1 }}
              exit={{ opacity: 0 }}
              transition={{ duration: 0.15 }}
              className="flex items-center gap-3 p-3 rounded-xl bg-nidhi-card border border-nidhi-border-subtle mt-1"
            >
              <div
                className="w-8 h-8 rounded-lg flex items-center justify-center text-nidhi-black font-bold text-sm flex-shrink-0"
                style={{ background: GOLD_GRADIENT }}
              >
                {user?.name?.charAt(0) ?? 'R'}
              </div>
              <div className="flex-1 min-w-0">
                <p className="text-sm font-semibold text-nidhi-text truncate leading-tight">
                  {user?.name?.split(' ')[0] ?? 'Rajesh'}
                </p>
                <div className="flex items-center gap-1 mt-0.5">
                  <Shield className="w-2.5 h-2.5 text-nidhi-gold" />
                  <p className="text-[10px] text-nidhi-text-muted">Family Head</p>
                </div>
              </div>
              <button
                onClick={handleLogout}
                className="text-nidhi-text-muted hover:text-nidhi-danger transition-colors p-1 rounded flex-shrink-0"
                title="Logout"
              >
                <LogOut className="w-3.5 h-3.5" />
              </button>
            </motion.div>
          ) : (
            <motion.button
              key="user-card-collapsed"
              initial={{ opacity: 0 }}
              animate={{ opacity: 1 }}
              exit={{ opacity: 0 }}
              transition={{ duration: 0.15 }}
              onClick={handleLogout}
              title="Logout"
              className="w-full flex justify-center p-3 rounded-xl hover:bg-nidhi-card transition-colors text-nidhi-text-muted hover:text-nidhi-danger"
            >
              <LogOut className="w-5 h-5" />
            </motion.button>
          )}
        </AnimatePresence>
      </div>

      {/* Collapse Toggle — on right edge, correctly positioned */}
      <button
        onClick={() => setCollapsed(!collapsed)}
        className="absolute right-0 translate-x-1/2 top-[80px] w-6 h-6 rounded-full bg-nidhi-elevated border border-nidhi-border flex items-center justify-center text-nidhi-text-muted hover:text-nidhi-gold hover:border-nidhi-gold/30 transition-all z-50 shadow-md"
        title={collapsed ? 'Expand sidebar' : 'Collapse sidebar'}
      >
        {collapsed ? (
          <ChevronRight className="w-3 h-3" />
        ) : (
          <ChevronLeft className="w-3 h-3" />
        )}
      </button>
    </motion.aside>
  );
}
