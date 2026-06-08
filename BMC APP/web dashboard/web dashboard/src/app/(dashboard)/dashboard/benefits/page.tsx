'use client';

import React, { useEffect, useState } from 'react';
import { motion } from 'framer-motion';
import { dataStore } from '@/lib/db/store';
import { Benefit } from '@/types';
import { ArrowRight, ChevronRight, IndianRupee, Sparkles } from 'lucide-react';

const CATEGORY_LABELS: Record<string, string> = {
  government_scheme: 'Government Scheme',
  scholarship:       'Scholarship',
  tax_benefit:       'Tax Benefit',
  financial:         'Financial',
};

const CATEGORY_COLORS: Record<string, { bg: string; text: string; border: string }> = {
  government_scheme: { bg: 'bg-nidhi-success/8',  text: 'text-nidhi-success', border: 'border-nidhi-success/20' },
  scholarship:       { bg: 'bg-nidhi-gold/8',     text: 'text-nidhi-gold',    border: 'border-nidhi-gold/20'    },
  tax_benefit:       { bg: 'bg-nidhi-info/8',     text: 'text-nidhi-info',    border: 'border-nidhi-border-subtle'    },
  financial:         { bg: 'bg-purple-500/8',     text: 'text-purple-400',    border: 'border-purple-500/20'    },
};

function MatchBar({ score }: { score: number }) {
  return (
    <div className="flex items-center gap-3">
      <div className="flex-1 h-1.5 bg-nidhi-border-subtle rounded-full overflow-hidden">
        <motion.div
          className="h-full rounded-full bg-gradient-to-r from-nidhi-gold to-nidhi-gold-light"
          initial={{ width: 0 }}
          whileInView={{ width: `${score * 100}%` }}
          transition={{ duration: 1, ease: 'easeOut' }}
          viewport={{ once: true }}
        />
      </div>
      <span className="text-xs font-semibold text-nidhi-gold w-9 text-right">
        {Math.round(score * 100)}%
      </span>
    </div>
  );
}

export default function BenefitsPage() {
  const [benefits, setBenefits] = useState<Benefit[]>([]);

  useEffect(() => {
    setBenefits(dataStore.getBenefits());
  }, []);

  const eligible = benefits.filter(b => b.status === 'eligible');

  const totalValue = '₹8.9L+';

  return (
    <div className="min-h-screen px-8 py-10 max-w-4xl mx-auto space-y-12">

      {/* Header */}
      <motion.div
        initial={{ opacity: 0, y: -16 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.5 }}
        className="space-y-1"
      >
        <div className="flex items-center gap-2 mb-2">
          <Sparkles className="w-4 h-4 text-nidhi-gold" />
          <p className="section-label">AI Discovered</p>
        </div>
        <h1 className="text-4xl font-display font-bold text-nidhi-text">Benefits</h1>
        <p className="text-nidhi-text-secondary">Money your family is leaving on the table.</p>
      </motion.div>

      {/* Total value hero */}
      <motion.div
        initial={{ opacity: 0, scale: 0.97 }}
        animate={{ opacity: 1, scale: 1 }}
        transition={{ duration: 0.5, delay: 0.1 }}
        className="relative overflow-hidden rounded-2xl border border-nidhi-gold/20 bg-nidhi-card p-8"
      >
        <div className="absolute -top-16 -right-16 w-56 h-56 rounded-full bg-nidhi-gold/5 blur-3xl pointer-events-none" />
        <div className="relative z-10 flex flex-col md:flex-row md:items-center gap-6 justify-between">
          <div>
            <p className="text-nidhi-text-muted text-sm mb-1">Total discoverable value</p>
            <div className="flex items-end gap-2">
              <span className="text-5xl font-display font-bold text-gold">{totalValue}</span>
            </div>
            <p className="text-sm text-nidhi-text-secondary mt-2">
              Across {eligible.length} government schemes, scholarships & tax benefits
            </p>
          </div>
          <motion.button
            whileHover={{ scale: 1.03 }}
            whileTap={{ scale: 0.97 }}
            className="btn-primary self-start md:self-auto"
          >
            Claim All Benefits
            <ArrowRight className="w-4 h-4" />
          </motion.button>
        </div>
      </motion.div>

      {/* Benefits list */}
      <div className="space-y-4">
        {eligible.map((benefit, idx) => {
          const colors = CATEGORY_COLORS[benefit.category] ?? CATEGORY_COLORS.financial;
          return (
            <motion.div
              key={benefit.id}
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.4, delay: 0.15 + idx * 0.08 }}
              whileHover={{ y: -3 }}
              className="group card-premium p-7 cursor-pointer"
            >
              {/* Top row */}
              <div className="flex items-start justify-between gap-4 mb-4">
                <div className="flex-1 min-w-0">
                  <div className="flex items-center gap-2 mb-2 flex-wrap">
                    <span className={`badge text-[11px] ${colors.bg} ${colors.text} border ${colors.border}`}>
                      {CATEGORY_LABELS[benefit.category] ?? benefit.category}
                    </span>
                    {benefit.matchedMembers.length > 0 && (
                      <span className="text-[11px] text-nidhi-text-muted">
                        for {benefit.matchedMembers.slice(0, 2).join(', ')}
                      </span>
                    )}
                  </div>
                  <h3 className="text-xl font-semibold text-nidhi-text leading-tight">{benefit.title}</h3>
                </div>
                {/* Value */}
                <div className="flex-shrink-0 text-right">
                  <div className="flex items-center gap-1 justify-end">
                    <IndianRupee className="w-4 h-4 text-nidhi-gold" />
                    <span className="text-lg font-bold text-nidhi-gold">
                      {(benefit.estimatedValue ?? '').replace('₹', '')}
                    </span>
                  </div>
                  <span className="text-[10px] text-nidhi-text-muted">estimated value</span>
                </div>
              </div>

              <p className="text-sm text-nidhi-text-secondary leading-relaxed mb-5">{benefit.description}</p>

              {/* Match score */}
              <div className="mb-5">
                <div className="flex items-center justify-between mb-2">
                  <span className="text-xs text-nidhi-text-muted">Match confidence</span>
                </div>
                <MatchBar score={benefit.matchScore} />
              </div>

              {/* Documents needed */}
              <div className="flex flex-wrap gap-2 mb-5">
                {benefit.requiredDocuments.slice(0, 3).map((doc) => (
                  <span key={doc} className="text-[11px] px-2.5 py-1 rounded-lg bg-nidhi-elevated text-nidhi-text-secondary border border-nidhi-border-subtle">
                    {doc}
                  </span>
                ))}
                {benefit.requiredDocuments.length > 3 && (
                  <span className="text-[11px] px-2.5 py-1 rounded-lg bg-nidhi-elevated text-nidhi-text-muted border border-nidhi-border-subtle">
                    +{benefit.requiredDocuments.length - 3} more
                  </span>
                )}
              </div>

              {/* Action */}
              <div className="flex items-center justify-between">
                {benefit.deadline && (
                  <span className="text-xs text-nidhi-warning">
                    Deadline: {new Date(benefit.deadline).toLocaleDateString('en-IN', { day: 'numeric', month: 'short', year: 'numeric' })}
                  </span>
                )}
                <motion.button
                  whileHover={{ x: 3 }}
                  className="ml-auto flex items-center gap-1.5 text-sm font-semibold text-nidhi-gold group-hover:text-nidhi-gold-light transition-colors"
                >
                  Start Application
                  <ChevronRight className="w-4 h-4 group-hover:translate-x-0.5 transition-transform" />
                </motion.button>
              </div>
            </motion.div>
          );
        })}
      </div>

      <div className="h-8" />
    </div>
  );
}
