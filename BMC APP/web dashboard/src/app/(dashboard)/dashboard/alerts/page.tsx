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
    <div className="min-h-screen px-8 py-10 max-w-3xl mx-auto space-y-10">

      {/* Header */}
      <motion.div
        initial={{ opacity: 0, y: -16 }}
        animate={{ opacity: 1, y: 0 }}
        className="space-y-1"
      >
        <p className="section-label">Sharma Family</p>
        <h1 className="text-4xl font-display font-bold text-nidhi-text">Alerts</h1>
        <p className="text-nidhi-text-secondary">
          {active.length} active &middot; {resolved.length} resolved
        </p>
      </motion.div>

      {/* Active alerts */}
      <div className="space-y-3">
        {active.map((alert, idx) => {
          const cfg = PRIORITY_CONFIG[alert.priority as keyof typeof PRIORITY_CONFIG] ?? PRIORITY_CONFIG.medium;
          const Icon = cfg.icon;
          const rel = formatRelativeDate(alert.dueDate);
          const isUrgent = alert.priority === 'critical' || alert.priority === 'high';

          return (
            <motion.div
              key={alert.id}
              initial={{ opacity: 0, y: 16 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.4, delay: idx * 0.06 }}
              whileHover={{ y: -2 }}
              className={`card-premium p-6 group ${isUrgent ? 'border-nidhi-border-subtle hover:border-nidhi-warning/30' : ''}`}
            >
              <div className="flex items-start gap-4">
                {/* Icon dot */}
                <div className={`mt-1 w-2 h-2 rounded-full flex-shrink-0 ${cfg.dot}`} />

                {/* Content */}
                <div className="flex-1 min-w-0">
                  <div className="flex items-start justify-between gap-3 mb-1">
                    <h3 className="text-base font-semibold text-nidhi-text leading-tight">{alert.title}</h3>
                    <span className={`flex-shrink-0 text-[10px] font-bold uppercase tracking-wider px-2 py-0.5 rounded-md border ${
                      alert.priority === 'critical' ? 'bg-nidhi-danger/10 text-nidhi-danger border-nidhi-danger/20' :
                      alert.priority === 'high'     ? 'bg-nidhi-warning/10 text-nidhi-warning border-nidhi-warning/20' :
                      'bg-nidhi-border-subtle text-nidhi-text-muted border-nidhi-border-subtle'
                    }`}>
                      {cfg.label}
                    </span>
                  </div>
                  <p className="text-sm text-nidhi-text-secondary leading-relaxed mb-4">{alert.description}</p>

                  <div className="flex items-center justify-between gap-3">
                    <div className="flex items-center gap-1.5 text-xs text-nidhi-text-muted">
                      <Calendar className="w-3.5 h-3.5" />
                      <span>Due: {new Date(alert.dueDate).toLocaleDateString('en-IN', { day: 'numeric', month: 'short', year: 'numeric' })}</span>
                      <span className={`font-semibold ${isUrgent ? cfg.text : ''}`}>({rel})</span>
                    </div>

                    <motion.button
                      whileHover={{ x: 2 }}
                      className="text-xs font-semibold text-nidhi-gold hover:text-nidhi-gold-light transition-colors"
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
          <h2 className="text-sm font-semibold text-nidhi-text-muted uppercase tracking-widest mb-4 flex items-center gap-2">
            <CheckCircle2 className="w-4 h-4 text-nidhi-success" />
            Resolved ({resolved.length})
          </h2>
          <div className="space-y-2">
            {resolved.map((alert, idx) => (
              <motion.div
                key={alert.id}
                initial={{ opacity: 0 }}
                animate={{ opacity: 0.5 }}
                transition={{ delay: idx * 0.05 }}
                className="flex items-center gap-3 px-5 py-4 rounded-xl border border-nidhi-border-subtle bg-nidhi-card/50"
              >
                <CheckCircle2 className="w-4 h-4 text-nidhi-success flex-shrink-0" />
                <p className="text-sm text-nidhi-text-secondary line-through">{alert.title}</p>
              </motion.div>
            ))}
          </div>
        </div>
      )}

      <div className="h-8" />
    </div>
  );
}
