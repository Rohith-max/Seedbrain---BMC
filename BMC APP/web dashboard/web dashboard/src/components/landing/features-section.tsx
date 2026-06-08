'use client';

import React from 'react';
import { motion, Variants } from 'framer-motion';
import { FileText, Bell, Gift, Users, Shield, Cpu } from 'lucide-react';

const features = [
  {
    icon: <FileText className="w-6 h-6 text-[#38BDF8]" />,
    title: 'Smart Document Vault',
    description: 'Auto-categorizes Aadhaar, PAN, insurance, and medical records using advanced OCR.',
  },
  {
    icon: <Bell className="w-6 h-6 text-[#F87171]" />,
    title: 'Proactive Deadlines',
    description: 'Never miss an EMI, policy renewal, or tax filing deadline. Nidhi reads the dates for you.',
  },
  {
    icon: <Gift className="w-6 h-6 text-[#4ADE80]" />,
    title: 'Benefit Discovery',
    description: 'AI matches your family profile to government schemes, scholarships, and tax benefits.',
  },
  {
    icon: <Cpu className="w-6 h-6 text-[#A78BFA]" />,
    title: 'Conversational AI',
    description: 'Ask "When does dad\'s insurance expire?" and get instant answers from your vault.',
  },
  {
    icon: <Users className="w-6 h-6 text-[#FBBF24]" />,
    title: 'Family Intelligence',
    description: 'Map relationships and securely manage documents for parents, spouse, and children.',
  },
  {
    icon: <Shield className="w-6 h-6 text-[#C9A96E]" />,
    title: 'Enterprise Security',
    description: 'End-to-end encryption ensures your sensitive household data remains strictly yours.',
  },
];

const container: Variants = {
  hidden: { opacity: 0 },
  show: {
    opacity: 1,
    transition: {
      staggerChildren: 0.1
    }
  }
};

const item: Variants = {
  hidden: { opacity: 0, y: 20 },
  show: { opacity: 1, y: 0, transition: { type: 'spring', stiffness: 300, damping: 24 } }
};

export function FeaturesSection() {
  return (
    <section className="py-24 relative z-10 bg-nidhi-deep">
      <div className="container mx-auto px-4 md:px-6">
        <div className="text-center max-w-3xl mx-auto mb-16">
          <h2 className="text-3xl md:text-5xl font-display font-bold mb-6 tracking-tight">
            Everything your household needs, <span className="text-nidhi-gold-light">automated.</span>
          </h2>
          <p className="text-nidhi-text-secondary text-lg">
            NIDHI isn't just storage. It actively reads, understands, and acts on your family's documents to make your life easier.
          </p>
        </div>

        <motion.div 
          variants={container}
          initial="hidden"
          whileInView="show"
          viewport={{ once: true, margin: "-100px" }}
          className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6"
        >
          {features.map((feature, idx) => (
            <motion.div key={idx} variants={item} className="card-premium p-8 group">
              <div className="w-12 h-12 rounded-xl bg-nidhi-surface border border-nidhi-border flex items-center justify-center mb-6 group-hover:scale-110 transition-transform" aria-hidden="true">
                {feature.icon}
              </div>
              <h3 className="text-xl font-semibold mb-3 text-nidhi-text">{feature.title}</h3>
              <p className="text-nidhi-text-secondary leading-relaxed">
                {feature.description}
              </p>
            </motion.div>
          ))}
        </motion.div>
      </div>
    </section>
  );
}
