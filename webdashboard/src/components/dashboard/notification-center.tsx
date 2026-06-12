'use client';

import React, { useState, useEffect, useRef } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import Link from 'next/link';
import { dataStore } from '@/lib/db/store';
import { ProactiveNotification } from '@/types';
import {
  Bell, X, CheckCheck, AlertTriangle, Sparkles, TrendingUp,
  Clock, GraduationCap, CreditCard, Home, FileWarning, Pill,
} from 'lucide-react';

const CATEGORY_CONFIG = {
  urgent:      { color: '#EF4444', bg: 'rgba(239,68,68,0.12)', border: 'rgba(239,68,68,0.25)', label: 'Urgent', Icon: AlertTriangle },
  opportunity: { color: '#10B981', bg: 'rgba(16,185,129,0.12)', border: 'rgba(16,185,129,0.25)', label: 'Opportunity', Icon: Sparkles },
  insight:     { color: '#D4AF37', bg: 'rgba(212,175,55,0.12)', border: 'rgba(212,175,55,0.25)', label: 'Insight', Icon: TrendingUp },
  reminder:    { color: '#6366F1', bg: 'rgba(99,102,241,0.12)', border: 'rgba(99,102,241,0.25)', label: 'Reminder', Icon: Clock },
};

const ICON_MAP: Record<string, React.ElementType> = {
  Pill, GraduationCap, TrendingUp, CreditCard, Home, FileWarning,
  AlertTriangle, Sparkles, Clock, Bell,
};

function timeAgo(dateStr: string): string {
  const diff = Date.now() - new Date(dateStr).getTime();
  const mins = Math.floor(diff / 60000);
  if (mins < 60) return `${mins}m ago`;
  const hrs = Math.floor(mins / 60);
  if (hrs < 24) return `${hrs}h ago`;
  return `${Math.floor(hrs / 24)}d ago`;
}

export function NotificationCenter() {
  const [open, setOpen] = useState(false);
  const [notifications, setNotifications] = useState<ProactiveNotification[]>([]);
  const [unreadCount, setUnreadCount] = useState(0);
  const [showToast, setShowToast] = useState(false);
  const panelRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    const notifs = dataStore.getProactiveNotifications();
    setNotifications(notifs);
    setUnreadCount(dataStore.getUnreadNotificationCount());
    // Show toast for the most recent unread notification after a delay
    const timer = setTimeout(() => {
      if (notifs.some(n => !n.isRead)) setShowToast(true);
    }, 2000);
    return () => clearTimeout(timer);
  }, []);

  useEffect(() => {
    if (showToast) {
      const timer = setTimeout(() => setShowToast(false), 6000);
      return () => clearTimeout(timer);
    }
  }, [showToast]);

  // Close panel on outside click
  useEffect(() => {
    const handler = (e: MouseEvent) => {
      if (panelRef.current && !panelRef.current.contains(e.target as Node)) {
        setOpen(false);
      }
    };
    if (open) document.addEventListener('mousedown', handler);
    return () => document.removeEventListener('mousedown', handler);
  }, [open]);

  const handleMarkAllRead = () => {
    dataStore.markAllNotificationsRead();
    setNotifications(prev => prev.map(n => ({ ...n, isRead: true })));
    setUnreadCount(0);
  };

  const handleMarkRead = (id: string) => {
    dataStore.markNotificationRead(id);
    setNotifications(prev => prev.map(n => n.id === id ? { ...n, isRead: true } : n));
    setUnreadCount(prev => Math.max(0, prev - 1));
  };

  const firstUnread = notifications.find(n => !n.isRead);

  return (
    <>
      {/* Toast popup */}
      <AnimatePresence>
        {showToast && firstUnread && !open && (
          <motion.div
            initial={{ opacity: 0, y: -20, x: 20 }}
            animate={{ opacity: 1, y: 0, x: 0 }}
            exit={{ opacity: 0, y: -20 }}
            className="fixed top-4 right-4 z-[60] max-w-sm"
          >
            <div
              className="p-4 rounded-2xl border shadow-2xl backdrop-blur-xl cursor-pointer"
              style={{
                background: 'rgba(28,28,24,0.95)',
                borderColor: CATEGORY_CONFIG[firstUnread.category].border,
              }}
              onClick={() => { setShowToast(false); setOpen(true); }}
            >
              <div className="flex items-start gap-3">
                <div
                  className="w-8 h-8 rounded-lg flex items-center justify-center flex-shrink-0"
                  style={{ backgroundColor: CATEGORY_CONFIG[firstUnread.category].bg }}
                >
                  {(() => {
                    const CatIcon = CATEGORY_CONFIG[firstUnread.category].Icon;
                    return <CatIcon className="w-4 h-4" style={{ color: CATEGORY_CONFIG[firstUnread.category].color }} />;
                  })()}
                </div>
                <div className="flex-1 min-w-0">
                  <div className="flex items-center gap-2 mb-1">
                    <span
                      className="text-[9px] font-bold uppercase tracking-wider px-1.5 py-0.5 rounded"
                      style={{
                        color: CATEGORY_CONFIG[firstUnread.category].color,
                        backgroundColor: CATEGORY_CONFIG[firstUnread.category].bg,
                      }}
                    >
                      {CATEGORY_CONFIG[firstUnread.category].label}
                    </span>
                    <span className="text-[10px] text-nidhi-text-muted">{timeAgo(firstUnread.createdAt)}</span>
                  </div>
                  <p className="text-sm font-semibold text-nidhi-text leading-snug">{firstUnread.title}</p>
                  <p className="text-xs text-nidhi-text-muted mt-1 line-clamp-2">{firstUnread.body}</p>
                </div>
                <button
                  onClick={(e) => { e.stopPropagation(); setShowToast(false); }}
                  className="text-nidhi-text-muted hover:text-nidhi-text p-0.5"
                >
                  <X className="w-3.5 h-3.5" />
                </button>
              </div>
            </div>
          </motion.div>
        )}
      </AnimatePresence>

      {/* Bell button (integrated in Topbar) */}
      <motion.button
        whileHover={{ scale: 1.05 }}
        whileTap={{ scale: 0.95 }}
        onClick={() => { setOpen(true); setShowToast(false); }}
        className="relative w-10 h-10 rounded-lg bg-nidhi-surface/60 hover:bg-nidhi-card border border-nidhi-border-subtle transition-all flex items-center justify-center group"
      >
        <Bell className="w-5 h-5 text-nidhi-text-secondary group-hover:text-nidhi-gold transition-colors" />
        {unreadCount > 0 && (
          <motion.span
            initial={{ scale: 0 }}
            animate={{ scale: 1 }}
            className="absolute -top-1.5 -right-1.5 w-5 h-5 rounded-full bg-nidhi-danger text-white text-[10px] font-bold flex items-center justify-center border-[1.5px] border-nidhi-black"
          >
            {unreadCount}
          </motion.span>
        )}
      </motion.button>

      {/* Slide-out panel */}
      <AnimatePresence>
        {open && (
          <>
            <motion.div
              initial={{ opacity: 0 }}
              animate={{ opacity: 1 }}
              exit={{ opacity: 0 }}
              className="fixed inset-0 z-50 bg-black/40 backdrop-blur-sm"
              onClick={() => setOpen(false)}
            />
            <motion.div
              ref={panelRef}
              initial={{ x: '100%' }}
              animate={{ x: 0 }}
              exit={{ x: '100%' }}
              transition={{ type: 'spring', stiffness: 350, damping: 35 }}
              className="fixed right-0 top-0 bottom-0 z-50 w-full max-w-md bg-nidhi-card border-l border-nidhi-border overflow-y-auto hide-scrollbar"
            >
              {/* Panel header */}
              <div className="sticky top-0 z-10 px-6 py-5 border-b border-nidhi-border-subtle bg-nidhi-card/95 backdrop-blur-xl">
                <div className="flex items-center justify-between">
                  <div>
                    <h2 className="text-base font-bold text-nidhi-text flex items-center gap-2">
                      <Sparkles className="w-4 h-4 text-nidhi-gold" />
                      AI Intelligence Feed
                    </h2>
                    <p className="text-xs text-nidhi-text-muted mt-0.5">
                      {unreadCount} unread notification{unreadCount !== 1 ? 's' : ''}
                    </p>
                  </div>
                  <div className="flex items-center gap-2">
                    {unreadCount > 0 && (
                      <button
                        onClick={handleMarkAllRead}
                        className="text-xs text-nidhi-gold hover:text-nidhi-gold-light flex items-center gap-1 transition-colors"
                      >
                        <CheckCheck className="w-3.5 h-3.5" /> Mark all read
                      </button>
                    )}
                    <button
                      onClick={() => setOpen(false)}
                      className="text-nidhi-text-muted hover:text-nidhi-text p-1 rounded transition-colors"
                    >
                      <X className="w-5 h-5" />
                    </button>
                  </div>
                </div>
              </div>

              {/* Notification list */}
              <div className="p-4 space-y-3">
                {notifications.map((notif, idx) => {
                  const cfg = CATEGORY_CONFIG[notif.category];
                  const NIcon = ICON_MAP[notif.icon] || Bell;
                  return (
                    <motion.div
                      key={notif.id}
                      initial={{ opacity: 0, y: 8 }}
                      animate={{ opacity: 1, y: 0 }}
                      transition={{ delay: idx * 0.04 }}
                      onClick={() => handleMarkRead(notif.id)}
                      className={`p-4 rounded-xl border cursor-pointer transition-all duration-200 hover:border-nidhi-border ${
                        notif.isRead
                          ? 'bg-nidhi-surface/50 border-nidhi-border-subtle opacity-70'
                          : 'bg-nidhi-card border-nidhi-border-subtle'
                      }`}
                      style={!notif.isRead ? { borderLeftWidth: '3px', borderLeftColor: cfg.color } : {}}
                    >
                      <div className="flex items-start gap-3">
                        <div
                          className="w-9 h-9 rounded-lg flex items-center justify-center flex-shrink-0"
                          style={{ backgroundColor: cfg.bg }}
                        >
                          <NIcon className="w-4 h-4" style={{ color: cfg.color }} />
                        </div>
                        <div className="flex-1 min-w-0">
                          <div className="flex items-center gap-2 mb-1 flex-wrap">
                            <span
                              className="text-[9px] font-bold uppercase tracking-wider px-1.5 py-0.5 rounded"
                              style={{ color: cfg.color, backgroundColor: cfg.bg }}
                            >
                              {cfg.label}
                            </span>
                            <span className="text-[10px] text-nidhi-text-muted">{timeAgo(notif.createdAt)}</span>
                            {!notif.isRead && (
                              <span className="w-1.5 h-1.5 rounded-full bg-nidhi-gold flex-shrink-0" />
                            )}
                          </div>
                          <p className="text-sm font-semibold text-nidhi-text leading-snug">{notif.title}</p>
                          <p className="text-xs text-nidhi-text-muted mt-1 leading-relaxed">{notif.body}</p>
                          {notif.actionLabel && notif.actionHref && (
                            <Link
                              href={notif.actionHref}
                              onClick={() => setOpen(false)}
                              className="inline-flex items-center gap-1 mt-2.5 text-xs font-semibold transition-colors"
                              style={{ color: cfg.color }}
                            >
                              {notif.actionLabel} →
                            </Link>
                          )}
                        </div>
                      </div>
                    </motion.div>
                  );
                })}
              </div>
            </motion.div>
          </>
        )}
      </AnimatePresence>
    </>
  );
}
