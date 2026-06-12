'use client';

import React from 'react';
import { motion } from 'framer-motion';
import { Heart, IndianRupee, TrendingUp, TrendingDown, Activity, Shield, Stethoscope, Home, Sparkles } from 'lucide-react';

/* ── Health Pulse Data ── */
const HEALTH_DATA = {
  hba1cHistory: [
    { month: 'Jan', value: 7.8 },
    { month: 'Mar', value: 7.5 },
    { month: 'May', value: 7.2 },
  ],
  insuranceCoverage: [
    { member: 'Rajesh', covered: true, provider: 'Star Health' },
    { member: 'Priya', covered: true, provider: 'Star Health' },
    { member: 'Aarav', covered: true, provider: 'Star Health' },
    { member: 'Ananya', covered: true, provider: 'Star Health' },
    { member: 'Kamla Devi', covered: false, provider: 'None' },
  ],
  upcomingMedical: 'Kamla Devi — Diabetes follow-up (Dr. Mehta, Apollo Clinic)',
};

/* ── Financial Pulse Data ── */
const FINANCE_DATA = {
  loanTotal: 5500000,
  loanOutstanding: 3845000,
  loanPaid: 1655000,
  monthlyEMI: 48500,
  taxSavingsFound: 46800,
  investments: [
    { name: 'PPF', value: 420000 },
    { name: 'ELSS', value: 185000 },
    { name: 'LIC', value: 12500 },
  ],
};

function MiniSparkline({ data, color }: { data: { month: string; value: number }[]; color: string }) {
  const max = Math.max(...data.map(d => d.value));
  const min = Math.min(...data.map(d => d.value)) - 0.5;
  const w = 140, h = 48;
  const points = data.map((d, i) => {
    const x = (i / (data.length - 1)) * w;
    const y = h - ((d.value - min) / (max - min)) * h;
    return `${x},${y}`;
  }).join(' ');

  return (
    <svg width={w} height={h} className="overflow-visible">
      <polyline points={points} fill="none" stroke={color} strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round" />
      {data.map((d, i) => {
        const x = (i / (data.length - 1)) * w;
        const y = h - ((d.value - min) / (max - min)) * h;
        return (
          <g key={i}>
            <circle cx={x} cy={y} r="3" fill={color} />
            <text x={x} y={h + 14} textAnchor="middle" fontSize="9" fill="#9A9A8E">{d.month}</text>
            <text x={x} y={y - 8} textAnchor="middle" fontSize="9" fontWeight="600" fill={color}>{d.value}</text>
          </g>
        );
      })}
    </svg>
  );
}

export function LifeOverview() {
  const paidPct = Math.round((FINANCE_DATA.loanPaid / FINANCE_DATA.loanTotal) * 100);
  const investTotal = FINANCE_DATA.investments.reduce((s, i) => s + i.value, 0);

  return (
    <motion.section
      initial={{ opacity: 0, y: 16 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ delay: 0.15 }}
    >
      <div className="flex items-center gap-2 mb-5">
        <p className="section-label">Life Overview</p>
        <span className="tag tag-success text-[9px]">UIDAI Can't Do This</span>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        {/* ── Health Pulse ── */}
        <div className="card p-6 rounded-2xl space-y-5">
          <div className="flex items-center gap-2.5">
            <div className="w-8 h-8 rounded-lg bg-red-500/10 flex items-center justify-center">
              <Heart className="w-4 h-4 text-red-400" />
            </div>
            <div>
              <p className="text-sm font-semibold text-nidhi-text">Health Pulse</p>
              <p className="text-[11px] text-nidhi-text-muted">ABHA + Medical Records</p>
            </div>
          </div>

          {/* HbA1c Trend */}
          <div>
            <div className="flex items-center justify-between mb-3">
              <p className="text-xs text-nidhi-text-secondary font-medium">Kamla Devi — HbA1c Trend</p>
              <span className="flex items-center gap-1 text-[10px] text-emerald-400 font-semibold">
                <TrendingDown className="w-3 h-3" /> Improving
              </span>
            </div>
            <MiniSparkline data={HEALTH_DATA.hba1cHistory} color="#EF4444" />
          </div>

          {/* Insurance Coverage */}
          <div>
            <p className="text-xs text-nidhi-text-secondary font-medium mb-2">Family Insurance Coverage</p>
            <div className="space-y-1.5">
              {HEALTH_DATA.insuranceCoverage.map(m => (
                <div key={m.member} className="flex items-center justify-between text-xs">
                  <span className="text-nidhi-text">{m.member}</span>
                  <span className={`flex items-center gap-1 font-medium ${m.covered ? 'text-emerald-400' : 'text-red-400'}`}>
                    {m.covered ? <Shield className="w-3 h-3" /> : <Stethoscope className="w-3 h-3" />}
                    {m.covered ? m.provider : '⚠ Not Covered'}
                  </span>
                </div>
              ))}
            </div>
          </div>

          {/* Upcoming */}
          <div className="p-3 rounded-xl bg-red-500/5 border border-red-500/15">
            <p className="text-[10px] text-red-400 font-semibold uppercase tracking-wider mb-1">Upcoming Appointment</p>
            <p className="text-xs text-nidhi-text-secondary">{HEALTH_DATA.upcomingMedical}</p>
          </div>
        </div>

        {/* ── Financial Pulse ── */}
        <div className="card p-6 rounded-2xl space-y-5">
          <div className="flex items-center gap-2.5">
            <div className="w-8 h-8 rounded-lg bg-nidhi-gold/10 flex items-center justify-center">
              <IndianRupee className="w-4 h-4 text-nidhi-gold" />
            </div>
            <div>
              <p className="text-sm font-semibold text-nidhi-text">Financial Pulse</p>
              <p className="text-[11px] text-nidhi-text-muted">Account Aggregator + Documents</p>
            </div>
          </div>

          {/* Home Loan Progress */}
          <div>
            <div className="flex items-center justify-between mb-2">
              <p className="text-xs text-nidhi-text-secondary font-medium">Home Loan Progress</p>
              <span className="text-xs font-bold text-nidhi-gold">{paidPct}% paid</span>
            </div>
            <div className="h-3 rounded-full bg-nidhi-border overflow-hidden">
              <motion.div
                initial={{ width: 0 }}
                animate={{ width: `${paidPct}%` }}
                transition={{ duration: 1.5, ease: 'easeOut' }}
                className="h-full rounded-full bg-gradient-to-r from-nidhi-gold to-yellow-400"
              />
            </div>
            <div className="flex justify-between mt-2 text-[11px] text-nidhi-text-muted">
              <span>Paid: ₹{(FINANCE_DATA.loanPaid / 100000).toFixed(1)}L</span>
              <span>Outstanding: ₹{(FINANCE_DATA.loanOutstanding / 100000).toFixed(1)}L</span>
            </div>
          </div>

          {/* EMI & Tax */}
          <div className="grid grid-cols-2 gap-3">
            <div className="p-3 rounded-xl bg-nidhi-elevated border border-nidhi-border-subtle">
              <p className="text-[10px] text-nidhi-text-muted uppercase tracking-wider mb-1">Monthly EMI</p>
              <p className="text-lg font-bold text-nidhi-text">₹{(FINANCE_DATA.monthlyEMI / 1000).toFixed(1)}K</p>
              <p className="text-[10px] text-nidhi-text-muted">SBI Home Loan</p>
            </div>
            <div className="p-3 rounded-xl bg-emerald-500/5 border border-emerald-500/15">
              <p className="text-[10px] text-emerald-400 uppercase tracking-wider mb-1 font-semibold">AI Tax Savings</p>
              <p className="text-lg font-bold text-emerald-400">₹{(FINANCE_DATA.taxSavingsFound / 1000).toFixed(1)}K</p>
              <p className="text-[10px] text-nidhi-text-muted">Unclaimed 80C</p>
            </div>
          </div>

          {/* Investments */}
          <div>
            <div className="flex items-center justify-between mb-2">
              <p className="text-xs text-nidhi-text-secondary font-medium">Tax-Saving Investments</p>
              <span className="text-xs font-semibold text-nidhi-text">₹{(investTotal / 100000).toFixed(1)}L</span>
            </div>
            {FINANCE_DATA.investments.map(inv => (
              <div key={inv.name} className="flex items-center gap-3 mb-1.5">
                <span className="text-xs text-nidhi-text-muted w-12">{inv.name}</span>
                <div className="flex-1 h-1.5 rounded-full bg-nidhi-border overflow-hidden">
                  <div
                    className="h-full rounded-full bg-nidhi-gold/70"
                    style={{ width: `${(inv.value / investTotal) * 100}%` }}
                  />
                </div>
                <span className="text-xs font-medium text-nidhi-text w-12 text-right">₹{(inv.value / 1000).toFixed(0)}K</span>
              </div>
            ))}
          </div>
        </div>
      </div>

      {/* AI Cross-Domain Insight Strip */}
      <motion.div
        initial={{ opacity: 0, y: 8 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ delay: 0.3 }}
        className="mt-4 p-4 rounded-xl border border-nidhi-gold/15 bg-nidhi-gold/5 flex items-start gap-3"
      >
        <Sparkles className="w-4 h-4 text-nidhi-gold flex-shrink-0 mt-0.5" />
        <div>
          <p className="text-xs font-semibold text-nidhi-gold mb-0.5">AI Cross-Domain Insight</p>
          <p className="text-xs text-nidhi-text-secondary leading-relaxed">
            Your medical expenses for Kamla Devi this year (₹18,200) may qualify for
            <strong className="text-nidhi-text"> Section 80D deduction — saving ₹5,460 in taxes</strong>.
            Combined with your home loan interest under Section 24(b), your total potential tax savings are <strong className="text-nidhi-text">₹1,09,200</strong>.
          </p>
        </div>
      </motion.div>
    </motion.section>
  );
}
