'use client';

import React, { useEffect, useState } from 'react';
import { motion } from 'framer-motion';
import { HeartPulse, FileText, Gift, Calendar } from 'lucide-react';

interface MetricCardProps {
  label: string;
  value: string | number;
  unit?: string;
  icon?: React.ReactNode;
  trend?: 'up' | 'down' | 'neutral';
  trendValue?: string;
  variant?: 'gold' | 'accent' | 'success' | 'warning';
  delay?: number;
}

export function MetricCard({
  label,
  value,
  unit,
  icon,
  trend = 'neutral',
  trendValue,
  variant = 'gold',
  delay = 0,
}: MetricCardProps) {
  const [isVisible, setIsVisible] = useState(false);

  useEffect(() => {
    setIsVisible(true);
  }, []);

  const variantStyles = {
    gold: 'border-nidhi-gold/20 hover:border-nidhi-gold/40 hover:shadow-[0_0_60px_rgba(212,175,55,0.1)]',
    accent:
      'border-nidhi-accent/20 hover:border-nidhi-accent/40 hover:shadow-[0_0_60px_rgba(99,102,241,0.1)]',
    success:
      'border-nidhi-success/20 hover:border-nidhi-success/40 hover:shadow-[0_0_60px_rgba(16,185,129,0.1)]',
    warning:
      'border-nidhi-warning/20 hover:border-nidhi-warning/40 hover:shadow-[0_0_60px_rgba(245,158,11,0.1)]',
  };

  const trendStyles = {
    up: 'text-nidhi-success',
    down: 'text-nidhi-danger',
    neutral: 'text-nidhi-text-secondary',
  };

  return (
    <motion.div
      initial={{ opacity: 0, y: 20, scale: 0.95 }}
      animate={isVisible ? { opacity: 1, y: 0, scale: 1 } : {}}
      transition={{ delay, duration: 0.4, ease: 'easeOut' }}
      whileHover={{ y: -4 }}
      className={`card-premium p-6 rounded-xl ${variantStyles[variant]} transition-all duration-300 group`}
    >
      {/* Content */}
      <div className="space-y-4">
        {/* Header */}
        <div className="flex items-start justify-between">
          {icon && (
            <motion.div
              animate={{ rotate: 360 }}
              transition={{ duration: 20, repeat: Infinity, ease: 'linear' }}
              className="p-3 rounded-lg bg-nidhi-surface/60 text-nidhi-gold group-hover:bg-nidhi-gold/10 transition-colors"
            >
              {icon}
            </motion.div>
          )}

          {trend !== 'neutral' && trendValue && (
            <div className={`text-xs font-semibold flex items-center gap-1 ${trendStyles[trend]}`}>
              <span>
                {trend === 'up' ? '↑' : '↓'}
              </span>
              <span>{trendValue}</span>
            </div>
          )}
        </div>

        {/* Metric */}
        <div>
          <motion.div className="text-4xl font-display font-bold text-nidhi-text mb-1">
            <motion.span
              initial={{ opacity: 0 }}
              animate={{ opacity: 1 }}
              transition={{ delay: delay + 0.3 }}
            >
              {value}
            </motion.span>
          </motion.div>
          <p className="text-sm text-nidhi-text-secondary">
            {label}
            {unit && <span className="text-xs ml-1 opacity-75">{unit}</span>}
          </p>
        </div>

        {/* Bottom Accent */}
        <motion.div
          initial={{ scaleX: 0 }}
          animate={isVisible ? { scaleX: 1 } : {}}
          transition={{ delay: delay + 0.4, duration: 0.6 }}
          className="h-1 w-12 bg-gradient-gold rounded-full origin-left"
        />
      </div>
    </motion.div>
  );
}

export function IntelligenceMetricsRow() {
  return (
    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
      <MetricCard
        label="Family Health Score"
        value="92"
        unit="%"
        icon={<HeartPulse className="w-5 h-5" />}
        trend="up"
        trendValue="+8% this week"
        variant="gold"
        delay={0}
      />
      <MetricCard
        label="Documents Protected"
        value="48"
        icon={<FileText className="w-5 h-5" />}
        trend="up"
        trendValue="+3 new"
        variant="accent"
        delay={0.1}
      />
      <MetricCard
        label="Benefits Available"
        value="12"
        icon={<Gift className="w-5 h-5" />}
        trend="up"
        trendValue="₹2.4L"
        variant="success"
        delay={0.2}
      />
      <MetricCard
        label="Upcoming Deadlines"
        value="3"
        icon={<Calendar className="w-5 h-5" />}
        trend="down"
        trendValue="-1 pending"
        variant="warning"
        delay={0.3}
      />
    </div>
  );
}
