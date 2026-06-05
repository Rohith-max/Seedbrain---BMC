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

const CATEGORY_COLORS: Record<string, { bg: string; color: string; border: string }> = {
  government_scheme: { bg: 'rgba(34,197,94,0.08)',   color: 'var(--color-nidhi-success)', border: 'rgba(34,197,94,0.2)'   },
  scholarship:       { bg: 'rgba(212,175,55,0.08)',  color: 'var(--color-nidhi-gold)',    border: 'rgba(212,175,55,0.2)'  },
  tax_benefit:       { bg: 'rgba(59,130,246,0.08)',  color: 'var(--color-nidhi-info)',    border: 'var(--color-nidhi-border-subtle)' },
  financial:         { bg: 'rgba(168,85,247,0.08)',  color: '#a78bfa',                   border: 'rgba(168,85,247,0.2)'  },
};

function MatchBar({ score }: { score: number }) {
  return (
    <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
      <div style={{ flex: 1, height: '6px', background: 'var(--color-nidhi-border-subtle)', borderRadius: '99px', overflow: 'hidden' }}>
        <motion.div
          style={{ height: '100%', borderRadius: '99px', background: 'linear-gradient(to right, #D4AF37, #E8D080)' }}
          initial={{ width: 0 }}
          whileInView={{ width: `${score * 100}%` }}
          transition={{ duration: 1, ease: 'easeOut' }}
          viewport={{ once: true }}
        />
      </div>
      <span style={{ fontSize: '12px', fontWeight: 600, color: 'var(--color-nidhi-gold)', width: '36px', textAlign: 'right' }}>
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
    <div style={{ minHeight: '100vh', padding: '48px 40px', maxWidth: '900px', display: 'flex', flexDirection: 'column', gap: '48px' }}>

      {/* Header */}
      <motion.div initial={{ opacity: 0, y: -16 }} animate={{ opacity: 1, y: 0 }} transition={{ duration: 0.5 }}
        style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
          <Sparkles style={{ width: '16px', height: '16px', color: 'var(--color-nidhi-gold)' }} />
          <p className="section-label">AI Discovered</p>
        </div>
        <h1 style={{ fontSize: '40px', fontFamily: 'var(--font-display)', fontWeight: 700, color: 'var(--color-nidhi-text)', lineHeight: 1.1 }}>Benefits</h1>
        <p style={{ fontSize: '16px', color: 'var(--color-nidhi-text-secondary)' }}>Money your family is leaving on the table.</p>
      </motion.div>

      {/* Total value hero */}
      <motion.div initial={{ opacity: 0, scale: 0.97 }} animate={{ opacity: 1, scale: 1 }} transition={{ duration: 0.5, delay: 0.1 }}
        style={{ position: 'relative', overflow: 'hidden', borderRadius: '16px', border: '1px solid rgba(212,175,55,0.2)', background: 'var(--color-nidhi-card)', padding: '32px' }}>
        <div style={{ position: 'absolute', top: '-64px', right: '-64px', width: '224px', height: '224px', borderRadius: '50%', background: 'rgba(212,175,55,0.05)', filter: 'blur(48px)', pointerEvents: 'none' }} />
        <div style={{ position: 'relative', zIndex: 1, display: 'flex', alignItems: 'center', justifyContent: 'space-between', gap: '24px', flexWrap: 'wrap' }}>
          <div style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
            <p style={{ fontSize: '13px', color: 'var(--color-nidhi-text-muted)' }}>Total discoverable value</p>
            <span style={{ fontSize: '48px', fontFamily: 'var(--font-display)', fontWeight: 700, background: 'linear-gradient(135deg,#D4AF37,#E8D080)', WebkitBackgroundClip: 'text', WebkitTextFillColor: 'transparent' }}>{totalValue}</span>
            <p style={{ fontSize: '14px', color: 'var(--color-nidhi-text-secondary)' }}>Across {eligible.length} government schemes, scholarships & tax benefits</p>
          </div>
          <motion.button whileHover={{ scale: 1.03 }} whileTap={{ scale: 0.97 }} className="btn-primary">
            Claim All Benefits <ArrowRight style={{ width: '16px', height: '16px' }} />
          </motion.button>
        </div>
      </motion.div>

      {/* Benefits list */}
      <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
        {eligible.map((benefit, idx) => {
          const colors = CATEGORY_COLORS[benefit.category] ?? CATEGORY_COLORS.financial;
          return (
            <motion.div key={benefit.id}
              initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.4, delay: 0.15 + idx * 0.08 }}
              whileHover={{ y: -3 }}
              className="card-premium"
              style={{ padding: '28px', cursor: 'pointer' }}>

              {/* Top row */}
              <div style={{ display: 'flex', alignItems: 'flex-start', justifyContent: 'space-between', gap: '16px', marginBottom: '16px' }}>
                <div style={{ flex: 1, minWidth: 0 }}>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '8px', marginBottom: '10px', flexWrap: 'wrap' }}>
                    <span style={{ fontSize: '11px', fontWeight: 500, padding: '3px 10px', borderRadius: '99px', background: colors.bg, color: colors.color, border: `1px solid ${colors.border}` }}>
                      {CATEGORY_LABELS[benefit.category] ?? benefit.category}
                    </span>
                    {benefit.matchedMembers.length > 0 && (
                      <span style={{ fontSize: '11px', color: 'var(--color-nidhi-text-muted)' }}>
                        for {benefit.matchedMembers.slice(0, 2).join(', ')}
                      </span>
                    )}
                  </div>
                  <h3 style={{ fontSize: '18px', fontWeight: 600, color: 'var(--color-nidhi-text)', lineHeight: 1.35 }}>{benefit.title}</h3>
                </div>
                <div style={{ flexShrink: 0, textAlign: 'right' }}>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '4px', justifyContent: 'flex-end' }}>
                    <IndianRupee style={{ width: '16px', height: '16px', color: 'var(--color-nidhi-gold)' }} />
                    <span style={{ fontSize: '18px', fontWeight: 700, color: 'var(--color-nidhi-gold)' }}>
                      {(benefit.estimatedValue ?? '').replace('₹', '')}
                    </span>
                  </div>
                  <span style={{ fontSize: '10px', color: 'var(--color-nidhi-text-muted)' }}>estimated value</span>
                </div>
              </div>

              <p style={{ fontSize: '14px', color: 'var(--color-nidhi-text-secondary)', lineHeight: 1.65, marginBottom: '20px' }}>{benefit.description}</p>

              {/* Match score */}
              <div style={{ marginBottom: '20px' }}>
                <p style={{ fontSize: '12px', color: 'var(--color-nidhi-text-muted)', marginBottom: '8px' }}>Match confidence</p>
                <MatchBar score={benefit.matchScore} />
              </div>

              {/* Documents needed */}
              <div style={{ display: 'flex', flexWrap: 'wrap', gap: '8px', marginBottom: '20px' }}>
                {benefit.requiredDocuments.slice(0, 3).map((doc) => (
                  <span key={doc} style={{ fontSize: '11px', padding: '4px 10px', borderRadius: '8px', background: 'var(--color-nidhi-elevated)', color: 'var(--color-nidhi-text-secondary)', border: '1px solid var(--color-nidhi-border-subtle)' }}>
                    {doc}
                  </span>
                ))}
                {benefit.requiredDocuments.length > 3 && (
                  <span style={{ fontSize: '11px', padding: '4px 10px', borderRadius: '8px', background: 'var(--color-nidhi-elevated)', color: 'var(--color-nidhi-text-muted)', border: '1px solid var(--color-nidhi-border-subtle)' }}>
                    +{benefit.requiredDocuments.length - 3} more
                  </span>
                )}
              </div>

              {/* Action */}
              <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                {benefit.deadline && (
                  <span style={{ fontSize: '12px', color: 'var(--color-nidhi-warning)' }}>
                    Deadline: {new Date(benefit.deadline).toLocaleDateString('en-IN', { day: 'numeric', month: 'short', year: 'numeric' })}
                  </span>
                )}
                <motion.button whileHover={{ x: 3 }}
                  style={{ marginLeft: 'auto', display: 'flex', alignItems: 'center', gap: '6px', fontSize: '14px', fontWeight: 600, color: 'var(--color-nidhi-gold)', background: 'none', border: 'none', cursor: 'pointer' }}>
                  Start Application <ChevronRight style={{ width: '16px', height: '16px' }} />
                </motion.button>
              </div>
            </motion.div>
          );
        })}
      </div>

      <div style={{ height: '32px' }} />
    </div>
  );
}
