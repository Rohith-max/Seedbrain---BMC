'use client';

import React from 'react';
import { motion, Variants } from 'framer-motion';
import {
  FileText,
  IndianRupee,
  Users,
  Brain,
  Globe,
  BarChart3,
  Bell,
  Shield,
} from 'lucide-react';

const features = [
  {
    icon: FileText,
    title: 'Document Intelligence',
    description: 'Natively understands Aadhaar, PAN, RC Book, Khata, LIC policies, Form 16 — every document your family has.',
    badges: ['Aadhaar', 'PAN', 'Voter ID', 'Passbook'],
    badgeStyle: 'bg-sky-500/10 text-sky-400 border-sky-500/15',
    iconStyle: 'bg-sky-500/10 text-sky-400',
    hoverStyle: 'hover:border-sky-500/25',
  },
  {
    icon: Brain,
    title: 'Government Scheme AI',
    description: 'AI engine identifies schemes your family qualifies for — PM Kisan, Ayushman Bharat, Sukanya Samriddhi.',
    badges: ['PMAY', 'Scholarships', 'Senior Benefits'],
    badgeStyle: 'bg-orange-500/10 text-orange-400 border-orange-500/15',
    iconStyle: 'bg-orange-500/10 text-orange-400',
    hoverStyle: 'hover:border-orange-500/25',
  },
  {
    icon: IndianRupee,
    title: 'Financial Tracking',
    description: 'Track LIC premiums, EMIs, tax deadlines, SIP investments, FD maturity and school fees.',
    badges: ['LIC', 'EMI', 'SIP', 'Tax'],
    badgeStyle: 'bg-emerald-500/10 text-emerald-400 border-emerald-500/15',
    iconStyle: 'bg-emerald-500/10 text-emerald-400',
    hoverStyle: 'hover:border-emerald-500/25',
  },
  {
    icon: Users,
    title: 'Joint Family Support',
    description: 'Manage documents for parents, grandparents, children, and dependents all in one unified place.',
    badges: ['Family Head', 'Nominees', 'Dependents'],
    badgeStyle: 'bg-orange-500/10 text-orange-400 border-orange-500/15',
    iconStyle: 'bg-orange-500/10 text-orange-400',
    hoverStyle: 'hover:border-orange-500/25',
  },
  {
    icon: Globe,
    title: '8 Indian Languages',
    description: 'Hindi, Kannada, Tamil, Telugu, Malayalam, Marathi, Bengali — speak naturally in your language.',
    badges: ['हिंदी', 'ಕನ್ನಡ', 'தமிழ்', 'తెలుగు'],
    badgeStyle: 'bg-sky-500/10 text-sky-400 border-sky-500/15',
    iconStyle: 'bg-sky-500/10 text-sky-400',
    hoverStyle: 'hover:border-sky-500/25',
  },
  {
    icon: Bell,
    title: 'Proactive Alerts',
    description: 'Never miss a benefit. Reminders for deadlines, expiring documents, and new eligible schemes.',
    badges: ['Benefit Alerts', 'Reminders'],
    badgeStyle: 'bg-orange-500/10 text-orange-400 border-orange-500/15',
    iconStyle: 'bg-orange-500/10 text-orange-400',
    hoverStyle: 'hover:border-orange-500/25',
  },
  {
    icon: BarChart3,
    title: 'Financial Health Score',
    description: 'Insights on savings, insurance coverage gaps, and optimisation recommendations for family finances.',
    badges: ['EMI Analysis', 'Coverage Gaps'],
    badgeStyle: 'bg-emerald-500/10 text-emerald-400 border-emerald-500/15',
    iconStyle: 'bg-emerald-500/10 text-emerald-400',
    hoverStyle: 'hover:border-emerald-500/25',
  },
  {
    icon: Shield,
    title: 'Bank-Grade Security',
    description: 'End-to-end encryption with local-first architecture. Your documents never leave your device without consent.',
    badges: ['AES-256', 'Zero-Knowledge'],
    badgeStyle: 'bg-sky-500/10 text-sky-400 border-sky-500/15',
    iconStyle: 'bg-sky-500/10 text-sky-400',
    hoverStyle: 'hover:border-sky-500/25',
  },
];

const checklist = [
  { title: 'Document Intelligence', desc: 'Native classification for Aadhaar, PAN, RC, Khata, LIC policies.' },
  { title: 'Government Scheme AI', desc: 'Identifies 50+ central & state welfare benefits automatically.' },
  { title: '8 Indian Languages', desc: 'Conversational AI in Hindi, Tamil, Kannada, Marathi, and more.' },
  { title: 'Location Intelligence', desc: 'Geo-specific benefit detection by district and state criteria.' },
  { title: 'Indian Financial Workflows', desc: 'Post office deposits, EMIs, LIC cycles — all supported natively.' },
];

const stats = [
  { value: '30+', label: 'Document Types' },
  { value: '50+', label: 'Welfare Schemes' },
  { value: '8', label: 'Languages' },
];

const containerVariants: Variants = {
  hidden: { opacity: 0 },
  visible: { opacity: 1, transition: { staggerChildren: 0.07, delayChildren: 0.1 } },
};

const itemVariants: Variants = {
  hidden: { opacity: 0, y: 20 },
  visible: { opacity: 1, y: 0, transition: { type: 'spring', stiffness: 100, damping: 15 } },
};

export function IndiaFeaturesSection() {
  return (
    <section className="relative bg-nidhi-black overflow-hidden">
      {/* Ambient glows */}
      <div className="absolute top-0 left-1/3 w-96 h-96 bg-orange-500/5 rounded-full blur-[140px] pointer-events-none" />
      <div className="absolute bottom-0 right-1/3 w-96 h-96 bg-emerald-500/5 rounded-full blur-[140px] pointer-events-none" />

      <div className="section-container py-24 md:py-32">

        {/* ── Section header ─────────────────────────────── */}
        <motion.div
          initial={{ opacity: 0, y: 16 }}
          whileInView={{ opacity: 1, y: 0 }}
          viewport={{ once: true }}
          transition={{ duration: 0.5 }}
          className="text-center mb-16 md:mb-20"
        >
          <h2 className="text-4xl md:text-5xl font-display font-bold text-white tracking-tight leading-tight mb-5">
            Built for Indian Households
          </h2>
          <p className="text-lg text-nidhi-text-secondary max-w-2xl mx-auto leading-relaxed">
            Not a global app with Indian features. NIDHI is designed from the ground up for the unique workflows of Indian families.
          </p>
        </motion.div>

        {/* ── Feature cards grid ─────────────────────────── */}
        <motion.div
          variants={containerVariants}
          initial="hidden"
          whileInView="visible"
          viewport={{ once: true, margin: '-40px' }}
          className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-5"
        >
          {features.map((f, idx) => {
            const Icon = f.icon;
            return (
              <motion.article
                key={idx}
                variants={itemVariants}
                className={[
                  'flex flex-col rounded-2xl p-6',
                  'bg-white/[0.03] border border-white/[0.06]',
                  'transition-all duration-300 group',
                  'hover:-translate-y-1',
                  f.hoverStyle,
                ].join(' ')}
              >
                {/* Icon */}
                <div className={`w-10 h-10 rounded-xl flex items-center justify-center mb-5 group-hover:scale-110 transition-transform duration-200 ${f.iconStyle}`}>
                  <Icon className="w-5 h-5" />
                </div>

                {/* Title */}
                <h3 className="text-base font-semibold text-white mb-2 leading-snug">
                  {f.title}
                </h3>

                {/* Description */}
                <p className="text-sm text-nidhi-text-secondary leading-relaxed mb-5 flex-1">
                  {f.description}
                </p>

                {/* Badges */}
                <div className="flex flex-wrap gap-1.5 mt-auto">
                  {f.badges.map((b, i) => (
                    <span key={i} className={`text-[11px] font-medium px-2 py-0.5 rounded-full border ${f.badgeStyle}`}>
                      {b}
                    </span>
                  ))}
                </div>
              </motion.article>
            );
          })}
        </motion.div>

        {/* ── Why India First ────────────────────────────── */}
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          whileInView={{ opacity: 1, y: 0 }}
          viewport={{ once: true, margin: '-40px' }}
          transition={{ duration: 0.5 }}
          className="mt-24 md:mt-32 grid lg:grid-cols-2 gap-12 lg:gap-16 items-start"
        >
          {/* Left — headline + stats */}
          <div>
            <h3 className="text-3xl md:text-4xl font-display font-bold text-white tracking-tight leading-tight mb-5">
              Why{' '}
              <span className="bg-gradient-to-r from-orange-400 to-amber-400 bg-clip-text text-transparent">
                India First?
              </span>
            </h3>
            <p className="text-base md:text-lg text-nidhi-text-secondary leading-relaxed mb-10 max-w-prose">
              Indian families navigate a distinct, multi-generational web of financial and administrative tasks.
              Joint households, government scheme deadlines, and documents in many languages —
              NIDHI is custom-built for these workflows, not adapted from a generic global product.
            </p>

            {/* Stats row */}
            <div className="flex gap-8 pt-8 border-t border-white/[0.06]">
              {stats.map((s, i) => (
                <div key={i}>
                  <div className="text-3xl font-display font-bold bg-gradient-to-r from-orange-400 to-amber-400 bg-clip-text text-transparent">
                    {s.value}
                  </div>
                  <div className="text-sm text-nidhi-text-secondary mt-1">{s.label}</div>
                </div>
              ))}
            </div>
          </div>

          {/* Right — checklist card */}
          <div className="rounded-2xl border border-white/[0.06] bg-white/[0.03] p-7 md:p-8 flex flex-col gap-6">
            {checklist.map((c, i) => (
              <div key={i} className="flex gap-4 items-start">
                <div className="mt-0.5 w-5 h-5 rounded-full bg-emerald-500/15 border border-emerald-500/25 flex items-center justify-center flex-shrink-0">
                  <svg className="w-3 h-3 text-emerald-400" fill="none" viewBox="0 0 12 12">
                    <path d="M2 6l3 3 5-5" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round" />
                  </svg>
                </div>
                <div>
                  <p className="text-sm font-semibold text-white leading-snug mb-0.5">{c.title}</p>
                  <p className="text-sm text-nidhi-text-secondary leading-relaxed">{c.desc}</p>
                </div>
              </div>
            ))}
          </div>
        </motion.div>

      </div>
    </section>
  );
}
