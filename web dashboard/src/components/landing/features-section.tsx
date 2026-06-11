'use client';

import React from 'react';
import { motion, Variants } from 'framer-motion';
import { FileText, Bell, Gift, Users, Shield, Cpu } from 'lucide-react';

const features = [
  {
    icon: FileText,
    title: 'Smart Document Vault',
    description: 'Auto-categorizes Aadhaar, PAN, insurance, and medical records using advanced OCR and AI extraction.',
    iconStyle: 'bg-sky-500/10 text-sky-400',
    hoverStyle: 'hover:border-sky-500/25',
  },
  {
    icon: Bell,
    title: 'Proactive Deadlines',
    description: "Never miss an EMI, policy renewal, or tax filing deadline. Nidhi reads the dates for you.",
    iconStyle: 'bg-orange-500/10 text-orange-400',
    hoverStyle: 'hover:border-orange-500/25',
  },
  {
    icon: Gift,
    title: 'Benefit Discovery',
    description: 'AI matches your family profile to government schemes, scholarships, and tax benefits automatically.',
    iconStyle: 'bg-emerald-500/10 text-emerald-400',
    hoverStyle: 'hover:border-emerald-500/25',
  },
  {
    icon: Cpu,
    title: 'Conversational AI',
    description: "Ask \"When does dad's insurance expire?\" and get instant, accurate answers from your vault.",
    iconStyle: 'bg-sky-500/10 text-sky-400',
    hoverStyle: 'hover:border-sky-500/25',
  },
  {
    icon: Users,
    title: 'Family Intelligence',
    description: 'Map relationships and securely manage documents for parents, spouse, children, and dependents.',
    iconStyle: 'bg-orange-500/10 text-orange-400',
    hoverStyle: 'hover:border-orange-500/25',
  },
  {
    icon: Shield,
    title: 'Enterprise Security',
    description: 'End-to-end encryption with local-first processing. Your sensitive household data remains strictly yours.',
    iconStyle: 'bg-emerald-500/10 text-emerald-400',
    hoverStyle: 'hover:border-emerald-500/25',
  },
];

const containerVariants: Variants = {
  hidden: { opacity: 0 },
  show: { opacity: 1, transition: { staggerChildren: 0.08 } },
};

const itemVariants: Variants = {
  hidden: { opacity: 0, y: 20 },
  show: { opacity: 1, y: 0, transition: { type: 'spring', stiffness: 100, damping: 15 } },
};

export function FeaturesSection() {
  return (
    <section className="relative bg-nidhi-deep overflow-hidden">
      {/* Ambient glows */}
      <div className="absolute top-1/4 right-0 w-96 h-96 bg-sky-500/5 rounded-full blur-[150px] pointer-events-none" />
      <div className="absolute bottom-1/4 left-0 w-96 h-96 bg-orange-500/5 rounded-full blur-[150px] pointer-events-none" />

      <div className="section-container py-24 md:py-32">

        {/* Section header */}
        <div className="text-center max-w-2xl mx-auto mb-16 md:mb-20">
          <h2 className="text-4xl md:text-5xl font-display font-bold tracking-tight leading-tight text-white mb-5">
            Everything your household{' '}
            <span className="bg-gradient-to-r from-amber-400 to-yellow-300 bg-clip-text text-transparent">
              needs, automated.
            </span>
          </h2>
          <p className="text-lg text-nidhi-text-secondary leading-relaxed">
            NIDHI actively reads, understands, and acts on your family's documents — not just stores them.
          </p>
        </div>

        {/* Cards */}
        <motion.div
          variants={containerVariants}
          initial="hidden"
          whileInView="show"
          viewport={{ once: true, margin: '-80px' }}
          className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6"
        >
          {features.map((f, idx) => {
            const Icon = f.icon;
            return (
              <motion.article
                key={idx}
                variants={itemVariants}
                className={[
                  'rounded-2xl p-7',
                  'bg-white/[0.03] border border-white/[0.06]',
                  'transition-all duration-300 group',
                  'hover:-translate-y-1',
                  f.hoverStyle,
                ].join(' ')}
              >
                <div className={`w-10 h-10 rounded-xl flex items-center justify-center mb-5 group-hover:scale-110 transition-transform duration-200 ${f.iconStyle}`}>
                  <Icon className="w-5 h-5" aria-hidden="true" />
                </div>
                <h3 className="text-lg font-semibold text-white mb-2.5 leading-snug">
                  {f.title}
                </h3>
                <p className="text-sm text-nidhi-text-secondary leading-relaxed">
                  {f.description}
                </p>
              </motion.article>
            );
          })}
        </motion.div>

      </div>
    </section>
  );
}
