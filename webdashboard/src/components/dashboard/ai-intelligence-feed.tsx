'use client';

import React, { useState } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import {
  AlertCircle,
  TrendingUp,
  Calendar,
  AlertTriangle,
  CheckCircle2,
  Sparkles,
  ChevronRight,
  Clock,
} from 'lucide-react';

interface IntelligenceItem {
  id: string;
  type: 'deadline' | 'scheme' | 'alert' | 'opportunity' | 'action';
  priority: 'critical' | 'high' | 'medium' | 'low';
  title: string;
  description: string;
  meta: string;
  action?: string;
  icon?: React.ReactNode;
  color?: string;
}

const INTELLIGENCE_ITEMS: IntelligenceItem[] = [
  {
    id: '1',
    type: 'deadline',
    priority: 'critical',
    title: 'LIC Policy Expires in 18 Days',
    description: 'Your LIC Term Plan (Policy #2847509) renewal premium is due',
    meta: 'Action required by June 22, 2026',
    action: 'Review & Pay',
    icon: <AlertTriangle className="w-5 h-5" />,
    color: '#EF4444',
  },
  {
    id: '2',
    type: 'scheme',
    priority: 'high',
    title: 'You Qualify for Karnataka Gruha Jyothi',
    description: 'Electricity subsidy benefit available. Eligible for ₹4,800/month savings',
    meta: 'BPL card required • Processing time: 15 days',
    action: 'Claim Now',
    icon: <TrendingUp className="w-5 h-5" />,
    color: '#10B981',
  },
  {
    id: '3',
    type: 'alert',
    priority: 'high',
    title: 'Missing Nominee on SBI Account',
    description: 'Your SBI savings account (2497****) has no nominee. Add one today.',
    meta: 'Important for legal protection',
    action: 'Complete Now',
    icon: <AlertCircle className="w-5 h-5" />,
    color: '#F59E0B',
  },
  {
    id: '4',
    type: 'deadline',
    priority: 'medium',
    title: 'Property Tax Due Next Month',
    description: 'Annual property tax for your residence is due on July 5, 2026',
    meta: 'Amount: ₹8,500 • Includes municipal charges',
    action: 'Schedule Payment',
    icon: <Calendar className="w-5 h-5" />,
    color: '#06B6D4',
  },
  {
    id: '5',
    type: 'opportunity',
    priority: 'medium',
    title: 'Aarav Eligible for PMAY Scheme',
    description: 'Your son qualifies for PM Awas Yojana (first-time homebuyer benefit)',
    meta: '₹2,67,000 subsidy available • Starts from age 21',
    action: 'Learn More',
    icon: <CheckCircle2 className="w-5 h-5" />,
    color: '#D4AF37',
  },
];

const priorityStyles = {
  critical: 'bg-nidhi-danger/10 border-nidhi-danger/30 hover:border-nidhi-danger/50',
  high: 'bg-nidhi-warning/10 border-nidhi-warning/30 hover:border-nidhi-warning/50',
  medium: 'bg-nidhi-accent/10 border-nidhi-accent/30 hover:border-nidhi-accent/50',
  low: 'bg-nidhi-info/10 border-nidhi-info/30 hover:border-nidhi-info/50',
};

export function AIIntelligenceFeed() {
  const [expandedId, setExpandedId] = useState<string | null>(null);

  return (
    <div className="space-y-4">
      {/* Header */}
      <div className="flex items-center gap-2 mb-6">
        <Sparkles className="w-5 h-5 text-nidhi-gold animate-pulse-gold" />
        <h3 className="text-lg font-display font-semibold text-nidhi-text">
          NIDHI Intelligence Feed
        </h3>
        <span className="ml-auto text-xs font-semibold text-nidhi-text-secondary bg-nidhi-surface/60 px-2.5 py-1 rounded-full">
          {INTELLIGENCE_ITEMS.length} insights
        </span>
      </div>

      {/* Intelligence Items */}
      <div className="space-y-3">
        <AnimatePresence>
          {INTELLIGENCE_ITEMS.map((item, idx) => (
            <motion.div
              key={item.id}
              initial={{ opacity: 0, x: -20 }}
              animate={{ opacity: 1, x: 0 }}
              transition={{ delay: idx * 0.1 }}
              whileHover={{ x: 4 }}
              className={`card-premium border rounded-xl p-4 cursor-pointer transition-all ${priorityStyles[item.priority]}`}
              onClick={() => setExpandedId(expandedId === item.id ? null : item.id)}
            >
              {/* Item Header */}
              <div className="flex items-start gap-4">
                {/* Icon */}
                <motion.div
                  className="mt-1 flex-shrink-0 w-10 h-10 rounded-lg flex items-center justify-center"
                  style={{ background: `${item.color}20` }}
                >
                  <motion.div
                    animate={{ rotate: [0, 5, -5, 0] }}
                    transition={{ repeat: Infinity, duration: 4 }}
                    style={{ color: item.color }}
                  >
                    {item.icon}
                  </motion.div>
                </motion.div>

                {/* Content */}
                <div className="flex-1 min-w-0">
                  <div className="flex items-start justify-between gap-2 mb-1">
                    <h4 className="text-sm font-semibold text-nidhi-text leading-tight">
                      {item.title}
                    </h4>
                    <motion.div
                      animate={{ rotate: expandedId === item.id ? 90 : 0 }}
                      className="flex-shrink-0 mt-0.5"
                    >
                      <ChevronRight className="w-4 h-4 text-nidhi-text-secondary" />
                    </motion.div>
                  </div>
                  <p className="text-xs text-nidhi-text-secondary">
                    {item.description}
                  </p>
                </div>
              </div>

              {/* Expanded Details */}
              <AnimatePresence>
                {expandedId === item.id && (
                  <motion.div
                    initial={{ opacity: 0, height: 0 }}
                    animate={{ opacity: 1, height: 'auto' }}
                    exit={{ opacity: 0, height: 0 }}
                    transition={{ duration: 0.3 }}
                    className="mt-4 pt-4 border-t border-nidhi-border/40 space-y-3"
                  >
                    {/* Meta Info */}
                    <div className="flex items-center gap-2 text-xs text-nidhi-text-secondary">
                      <Clock className="w-3 h-3" />
                      <span>{item.meta}</span>
                    </div>

                    {/* Action Button */}
                    {item.action && (
                      <motion.button
                        whileHover={{ scale: 1.02 }}
                        whileTap={{ scale: 0.98 }}
                        className="w-full py-2.5 px-3 rounded-lg bg-gradient-gold text-nidhi-black font-semibold text-sm transition-all hover:shadow-lg flex items-center justify-center gap-2 group"
                      >
                        {item.action}
                        <ChevronRight className="w-4 h-4 group-hover:translate-x-0.5 transition-transform" />
                      </motion.button>
                    )}
                  </motion.div>
                )}
              </AnimatePresence>

              {/* Priority Indicator Line */}
              <motion.div
                layoutId={`priority-${item.id}`}
                className="absolute bottom-0 left-0 h-1 rounded-b-xl bg-gradient-gold"
                style={{
                  background: item.color,
                  opacity: expandedId === item.id ? 1 : 0.5,
                }}
                initial={false}
                transition={{ duration: 0.3 }}
              />
            </motion.div>
          ))}
        </AnimatePresence>
      </div>

      {/* View All Button */}
      <motion.button
        whileHover={{ scale: 1.02 }}
        whileTap={{ scale: 0.98 }}
        className="w-full py-3 px-4 rounded-xl glass-strong hover:glass-premium transition-all text-sm font-semibold text-nidhi-gold flex items-center justify-center gap-2 group mt-4"
      >
        View All Intelligence
        <ChevronRight className="w-4 h-4 group-hover:translate-x-0.5 transition-transform" />
      </motion.button>
    </div>
  );
}
