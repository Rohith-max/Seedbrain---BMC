'use client';

import React, { useEffect, useState } from 'react';
import { motion } from 'framer-motion';
import { dataStore } from '@/lib/db/store';
import { Alert } from '@/types';
import { formatRelativeDate, getPriorityColor } from '@/lib/utils';
import { Bell, AlertTriangle, Clock, CheckCircle2, Calendar } from 'lucide-react';

const PRIORITY_CONFIG = {
  critical: { icon: AlertTriangle, label: 'Critical',   dot: 'bg-nidhi-danger',  text: 'text-nidhi-danger'  },
  high:     { icon: Clock,         label: 'High',        dot: 'bg-nidhi-warning', text: 'text-nidhi-warning' },
  medium:   { icon: Bell,          label: 'Medium',      dot: 'bg-nidhi-info',    text: 'text-nidhi-info'    },
  low:      { icon: Bell,          label: 'Low',         dot: 'bg-nidhi-success', text: 'text-nidhi-success' },
};

export default function AlertsPage() {
  const [alerts, setAlerts] = useState<Alert[]>([]);

  useEffect(() => {
    const sorted = dataStore.getAlerts().sort((a, b) => {
      if (a.status === 'resolved' && b.status !== 'resolved') return 1;
      if (a.status !== 'resolved' && b.status === 'resolved') return -1;
      const priorityOrder = { critical: 0, high: 1, medium: 2, low: 3 };
      return (priorityOrder[a.priority as keyof typeof priorityOrder] ?? 2) -
             (priorityOrder[b.priority as keyof typeof priorityOrder] ?? 2);
    });
    setAlerts(sorted);
  }, []);

  const active   = alerts.filter(a => a.status === 'active');
  const resolved = alerts.filter(a => a.status === 'resolved');

  return (
    <div style={{ minHeight: '100vh', padding: '48px 40px', maxWidth: '780px', display: 'flex', flexDirection: 'column', gap: '48px' }}>

      {/* Header */}
      <motion.div
        initial={{ opacity: 0, y: -16 }}
        animate={{ opacity: 1, y: 0 }}
        style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}
      >
        <p className="section-label">Sharma Family</p>
        <h1 style={{ fontSize: '40px', fontFamily: 'var(--font-display)', fontWeight: 700, color: 'var(--color-nidhi-text)', lineHeight: 1.1 }}>
          Alerts
        </h1>
        <p style={{ fontSize: '16px', color: 'var(--color-nidhi-text-secondary)', marginTop: '4px' }}>
          {active.length} active &middot; {resolved.length} resolved
        </p>
      </motion.div>

      {/* Active alerts */}
      <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
        {active.map((alert, idx) => {
          const cfg = PRIORITY_CONFIG[alert.priority as keyof typeof PRIORITY_CONFIG] ?? PRIORITY_CONFIG.medium;
          const rel = formatRelativeDate(alert.dueDate);
          const isUrgent = alert.priority === 'critical' || alert.priority === 'high';

          return (
            <motion.div
              key={alert.id}
              initial={{ opacity: 0, y: 16 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.4, delay: idx * 0.06 }}
              whileHover={{ y: -2 }}
              className={`card-premium group ${isUrgent ? 'border-nidhi-border-subtle hover:border-nidhi-warning/30' : ''}`}
              style={{ padding: '24px' }}
            >
              <div style={{ display: 'flex', alignItems: 'flex-start', gap: '16px' }}>
                {/* Priority dot */}
                <div
                  className={cfg.dot}
                  style={{ marginTop: '6px', width: '8px', height: '8px', borderRadius: '50%', flexShrink: 0 }}
                />

                {/* Content */}
                <div style={{ flex: 1, minWidth: 0 }}>
                  {/* Title row */}
                  <div style={{ display: 'flex', alignItems: 'flex-start', justifyContent: 'space-between', gap: '12px', marginBottom: '10px' }}>
                    <h3 style={{ fontSize: '16px', fontWeight: 600, color: 'var(--color-nidhi-text)', lineHeight: 1.35 }}>
                      {alert.title}
                    </h3>
                    <span
                      style={{
                        flexShrink: 0,
                        fontSize: '10px',
                        fontWeight: 700,
                        textTransform: 'uppercase',
                        letterSpacing: '0.08em',
                        padding: '3px 8px',
                        borderRadius: '6px',
                        border: '1px solid',
                        ...(alert.priority === 'critical'
                          ? { background: 'rgba(239,68,68,0.1)', color: 'var(--color-nidhi-danger)', borderColor: 'rgba(239,68,68,0.2)' }
                          : alert.priority === 'high'
                          ? { background: 'rgba(245,158,11,0.1)', color: 'var(--color-nidhi-warning)', borderColor: 'rgba(245,158,11,0.2)' }
                          : { background: 'var(--color-nidhi-elevated)', color: 'var(--color-nidhi-text-muted)', borderColor: 'var(--color-nidhi-border-subtle)' })
                      }}
                    >
                      {cfg.label}
                    </span>
                  </div>

                  {/* Description */}
                  <p style={{ fontSize: '14px', color: 'var(--color-nidhi-text-secondary)', lineHeight: 1.65, marginBottom: '20px' }}>
                    {alert.description}
                  </p>

                  {/* Footer row */}
                  <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', gap: '12px' }}>
                    <div style={{ display: 'flex', alignItems: 'center', gap: '6px', fontSize: '12px', color: 'var(--color-nidhi-text-muted)' }}>
                      <Calendar style={{ width: '14px', height: '14px' }} />
                      <span>
                        Due: {new Date(alert.dueDate).toLocaleDateString('en-IN', { day: 'numeric', month: 'short', year: 'numeric' })}
                      </span>
                      <span
                        style={{
                          fontWeight: 600,
                          color: isUrgent
                            ? (alert.priority === 'critical' ? 'var(--color-nidhi-danger)' : 'var(--color-nidhi-warning)')
                            : 'var(--color-nidhi-text-muted)'
                        }}
                      >
                        ({rel})
                      </span>
                    </div>

                    <motion.button
                      whileHover={{ x: 2 }}
                      style={{ fontSize: '13px', fontWeight: 600, color: 'var(--color-nidhi-gold)', background: 'none', border: 'none', cursor: 'pointer', whiteSpace: 'nowrap' }}
                    >
                      {alert.actionLabel || 'Take Action'} →
                    </motion.button>
                  </div>
                </div>
              </div>
            </motion.div>
          );
        })}
      </div>

      {/* Resolved */}
      {resolved.length > 0 && (
        <div>
          <div style={{ display: 'flex', alignItems: 'center', gap: '8px', marginBottom: '16px' }}>
            <CheckCircle2 style={{ width: '16px', height: '16px', color: 'var(--color-nidhi-success)' }} />
            <span style={{ fontSize: '12px', fontWeight: 600, color: 'var(--color-nidhi-text-muted)', textTransform: 'uppercase', letterSpacing: '0.1em' }}>
              Resolved ({resolved.length})
            </span>
          </div>
          <div style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
            {resolved.map((alert, idx) => (
              <motion.div
                key={alert.id}
                initial={{ opacity: 0 }}
                animate={{ opacity: 0.5 }}
                transition={{ delay: idx * 0.05 }}
                style={{ display: 'flex', alignItems: 'center', gap: '12px', padding: '16px 20px', borderRadius: '12px', border: '1px solid var(--color-nidhi-border-subtle)', background: 'var(--color-nidhi-card)' }}
              >
                <CheckCircle2 style={{ width: '16px', height: '16px', color: 'var(--color-nidhi-success)', flexShrink: 0 }} />
                <p style={{ fontSize: '14px', color: 'var(--color-nidhi-text-secondary)', textDecoration: 'line-through' }}>{alert.title}</p>
              </motion.div>
            ))}
          </div>
        </div>
      )}

      <div style={{ height: '32px' }} />
    </div>
  );
}
