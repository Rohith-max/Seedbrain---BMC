'use client';

import React, { useState, useEffect } from 'react';
import Link from 'next/link';
import { usePathname } from 'next/navigation';
import { APP_NAME, APP_NAME_HINDI, NAV_ITEMS } from '@/lib/constants';
import * as Icons from 'lucide-react';
import { motion } from 'framer-motion';

export function FloatingNavRail() {
  const pathname = usePathname();
  const [isCollapsed, setIsCollapsed] = useState(true);
  const [hoveredIndex, setHoveredIndex] = useState<number | null>(null);

  return (
    <>
      {/* Floating Navigation Rail */}
      <motion.aside
        initial={{ x: -100, opacity: 0 }}
        animate={{ x: 0, opacity: 1 }}
        transition={{ duration: 0.5, ease: 'easeOut' }}
        className="hidden md:flex fixed left-4 top-1/2 -translate-y-1/2 z-40 flex-col items-start"
      >
        {/* Logo Container */}
        <motion.div
          whileHover={{ scale: 1.05 }}
          className="mb-8 p-3 rounded-xl glass-premium cursor-pointer"
        >
          <Link href="/dashboard" className="flex items-center gap-2">
            <div className="w-8 h-8 rounded-lg gradient-gold flex items-center justify-center text-white font-bold text-sm">
              न
            </div>
            <motion.div
              initial={{ opacity: 0, width: 0 }}
              animate={isCollapsed ? { opacity: 0, width: 0 } : { opacity: 1, width: 'auto' }}
              transition={{ duration: 0.3 }}
              className="overflow-hidden whitespace-nowrap"
            >
              <span className="text-xs font-display font-bold text-nidhi-text tracking-wide">
                {APP_NAME}
              </span>
            </motion.div>
          </Link>
        </motion.div>

        {/* Nav Items Container */}
        <motion.nav
          className="flex flex-col gap-2 p-3 glass-strong rounded-2xl"
          onMouseEnter={() => setIsCollapsed(false)}
          onMouseLeave={() => {
            setIsCollapsed(true);
            setHoveredIndex(null);
          }}
        >
          {NAV_ITEMS.map((item, idx) => {
            const Icon = Icons[item.icon as keyof typeof Icons] as React.ElementType;
            const isActive = item.href === '/dashboard'
              ? pathname === '/dashboard'
              : pathname.startsWith(item.href);

            return (
              <motion.div key={item.href} className="relative group">
                <Link
                  href={item.href}
                  onMouseEnter={() => setHoveredIndex(idx)}
                  onMouseLeave={() => setHoveredIndex(null)}
                  className={`relative w-12 h-12 rounded-xl flex items-center justify-center transition-all duration-300 ${
                    isActive
                      ? 'glass-premium'
                      : 'hover:glass-accent'
                  }`}
                >
                  {/* Active Glow Effect */}
                  {isActive && (
                    <motion.div
                      layoutId="active-nav"
                      className="absolute inset-0 rounded-xl glow-gold"
                      initial={false}
                      transition={{ duration: 0.3 }}
                    />
                  )}

                  {/* Icon */}
                  <motion.div
                    animate={isActive ? { scale: 1.1 } : { scale: 1 }}
                    transition={{ duration: 0.2 }}
                    className={`relative z-10 transition-colors ${
                      isActive
                        ? 'text-nidhi-gold'
                        : 'text-nidhi-text-secondary group-hover:text-nidhi-gold'
                    }`}
                  >
                    {Icon && <Icon className="w-5 h-5" />}
                  </motion.div>
                </Link>

                {/* Label Tooltip */}
                <motion.div
                  initial={{ opacity: 0, x: -10 }}
                  animate={
                    hoveredIndex === idx || (!isCollapsed && isActive)
                      ? { opacity: 1, x: 0 }
                      : { opacity: 0, x: -10 }
                  }
                  transition={{ duration: 0.2 }}
                  className="absolute left-16 top-1/2 -translate-y-1/2 ml-2 glass-strong px-3 py-2 rounded-lg whitespace-nowrap text-xs font-medium text-nidhi-text pointer-events-none"
                >
                  {item.label}
                  <div className="absolute -left-1 top-1/2 -translate-y-1/2 w-2 h-2 rounded-full bg-nidhi-gold/40" />
                </motion.div>
              </motion.div>
            );
          })}
        </motion.nav>

        {/* AI Status Badge */}
        <motion.div
          initial={{ opacity: 0, y: 10 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.5, delay: 0.2 }}
          className="mt-auto pt-4 glass-premium p-3 rounded-xl text-center"
        >
          <div className="flex items-center justify-center gap-1.5 mb-1">
            <span className="w-2 h-2 rounded-full bg-nidhi-success animate-pulse-gold" />
            <span className="text-[10px] font-semibold text-nidhi-gold uppercase tracking-wider">
              AI Active
            </span>
          </div>
          <p className="text-[10px] text-nidhi-text-muted max-w-[80px]">
            Monitoring family intelligence
          </p>
        </motion.div>
      </motion.aside>

      {/* Mobile Bottom Navigation */}
      <motion.nav
        initial={{ y: 100 }}
        animate={{ y: 0 }}
        transition={{ duration: 0.5 }}
        className="md:hidden fixed bottom-0 left-0 right-0 z-40 glass-strong rounded-t-3xl border-t border-nidhi-border"
      >
        <div className="flex items-center justify-around h-20 px-4">
          {NAV_ITEMS.slice(0, 5).map((item) => {
            const Icon = Icons[item.icon as keyof typeof Icons] as React.ElementType;
            const isActive = pathname.startsWith(item.href);
            return (
              <Link
                key={item.href}
                href={item.href}
                className={`flex flex-col items-center gap-1 p-3 rounded-lg transition-all ${
                  isActive ? 'text-nidhi-gold' : 'text-nidhi-text-secondary'
                }`}
              >
                {Icon && <Icon className="w-5 h-5" />}
              </Link>
            );
          })}
        </div>
      </motion.nav>
    </>
  );
}
