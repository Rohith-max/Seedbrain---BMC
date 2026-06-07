'use client';

import React from 'react';
import { motion } from 'framer-motion';
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

export function IndiaFeaturesSection() {
  const features = [
    {
      icon: FileText,
      title: 'Indian Document Intelligence',
      description: 'Natively understands Aadhaar, PAN, RC Book, Khata, LIC policies, Form 16 - every document your family has.',
      examples: 'Aadhaar • PAN • Voter ID • Bank Passbook',
      color: 'text-blue-400'
    },
    {
      icon: Brain,
      title: 'Government Scheme AI',
      description: 'AI engine identifies schemes your family qualifies for. From PM Kisan to Ayushman Bharat to Sukanya Samriddhi.',
      examples: 'PMAY • Scholarships • Senior Benefits',
      color: 'text-purple-400'
    },
    {
      icon: IndianRupee,
      title: 'Financial Tracking (भारतीय)',
      description: 'Track LIC premiums, EMIs, tax deadlines, SIP investments, FD maturity, school fees - all Indian workflows.',
      examples: 'LIC • EMI • Tax • SIP • School Fees',
      color: 'text-green-400'
    },
    {
      icon: Users,
      title: 'Joint Family Management',
      description: 'Built for Indian families. Manage documents for parents, grandparents, children, dependents in one place.',
      examples: 'Family Head • Nominatees • Dependents',
      color: 'text-pink-400'
    },
    {
      icon: Globe,
      title: '8 Indian Languages',
      description: 'Hindi, Kannada, Tamil, Telugu, Malayalam, Marathi, Bengali - speak naturally, get responses in your language.',
      examples: 'हिंदी • ಕನ್ನಡ • தமிழ் • తెలుగు',
      color: 'text-yellow-400'
    },
    {
      icon: Bell,
      title: 'Proactive Alerts',
      description: 'Never miss a benefit. Reminders for missed deadlines, expiring documents, new schemes your family qualifies for.',
      examples: 'Benefit Alerts • Deadline Reminders',
      color: 'text-orange-400'
    },
    {
      icon: BarChart3,
      title: 'Family Financial Health',
      description: 'Insights on savings, investments, insurance coverage. Recommendations to optimize family finances.',
      examples: 'EMI Analysis • Coverage Gap Detection',
      color: 'text-cyan-400'
    },
    {
      icon: Shield,
      title: 'Bank-Grade Security',
      description: 'End-to-end encryption. Local-first architecture. Your family documents never leave your device without consent.',
      examples: 'AES-256 • Zero-Knowledge • NIST Compliant',
      color: 'text-red-400'
    },
  ];

  const containerVariants = {
    hidden: { opacity: 0 },
    visible: {
      opacity: 1,
      transition: {
        staggerChildren: 0.1,
        delayChildren: 0.2,
      },
    },
  };

  const itemVariants = {
    hidden: { opacity: 0, y: 20 },
    visible: { opacity: 1, y: 0 },
  };

  return (
    <section className="py-20 px-4 md:px-8 bg-nidhi-black relative">
      {/* Decorative elements */}
      <div className="absolute top-0 left-1/4 w-96 h-96 bg-nidhi-gold/5 rounded-full blur-[150px] -z-10" />
      <div className="absolute bottom-0 right-1/4 w-96 h-96 bg-nidhi-accent/5 rounded-full blur-[150px] -z-10" />

      <div className="max-w-7xl mx-auto">
        {/* Section Header */}
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          whileInView={{ opacity: 1, y: 0 }}
          viewport={{ once: true }}
          className="text-center mb-16"
        >
          <h2 className="text-4xl md:text-5xl font-bold text-white mb-4">
            Built for Indian Households
          </h2>
          <p className="text-xl text-nidhi-text-secondary max-w-2xl mx-auto">
            Not a global app with Indian features. NIDHI is designed from the ground up for Indian families.
          </p>
        </motion.div>

        {/* Features Grid */}
        <motion.div
          variants={containerVariants}
          initial="hidden"
          whileInView="visible"
          viewport={{ once: true }}
          className="grid md:grid-cols-2 lg:grid-cols-4 gap-6"
        >
          {features.map((feature, idx) => {
            const Icon = feature.icon;
            return (
              <motion.div
                key={idx}
                variants={itemVariants}
                className="bg-nidhi-card/50 border border-nidhi-border hover:border-nidhi-gold/30 rounded-xl p-6 hover:bg-nidhi-card transition-all group"
              >
                <div className="mb-4">
                  <Icon className={`w-8 h-8 ${feature.color} group-hover:scale-110 transition-transform`} />
                </div>
                <h3 className="text-lg font-semibold text-white mb-2">{feature.title}</h3>
                <p className="text-sm text-nidhi-text-secondary mb-4">{feature.description}</p>
                <div className="flex flex-wrap gap-2">
                  {feature.examples.split(' • ').map((example, i) => (
                    <span key={i} className="text-xs bg-nidhi-gold/10 text-nidhi-gold px-2 py-1 rounded">
                      {example}
                    </span>
                  ))}
                </div>
              </motion.div>
            );
          })}
        </motion.div>

        {/* Why India First */}
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          whileInView={{ opacity: 1, y: 0 }}
          viewport={{ once: true }}
          className="mt-16 bg-gradient-to-r from-nidhi-gold/10 to-nidhi-accent/10 border border-nidhi-gold/20 rounded-xl p-8"
        >
          <h3 className="text-2xl font-bold text-white mb-4">Why India First?</h3>
          <div className="grid md:grid-cols-2 gap-8">
            <div>
              <p className="text-nidhi-text-secondary mb-4">
                Indian families face unique challenges. Joint households. Government scheme deadlines. Documents in multiple formats. Regional languages. We built NIDHI specifically for these workflows, not as an afterthought.
              </p>
              <ul className="space-y-2">
                <li className="flex gap-2 text-sm">
                  <span className="text-nidhi-gold">✓</span>
                  <span className="text-nidhi-text-secondary">Understands 30+ Indian document types</span>
                </li>
                <li className="flex gap-2 text-sm">
                  <span className="text-nidhi-gold">✓</span>
                  <span className="text-nidhi-text-secondary">AI identifies 50+ government schemes</span>
                </li>
                <li className="flex gap-2 text-sm">
                  <span className="text-nidhi-gold">✓</span>
                  <span className="text-nidhi-text-secondary">Supports 8 Indian regional languages</span>
                </li>
              </ul>
            </div>
            <div>
              <p className="text-nidhi-text-secondary mb-4">
                We're not just localizing. We're architecting. From government office locations to tax calendar to family structure - everything is designed for India first.
              </p>
              <ul className="space-y-2">
                <li className="flex gap-2 text-sm">
                  <span className="text-nidhi-gold">✓</span>
                  <span className="text-nidhi-text-secondary">Store location intelligence for schemes</span>
                </li>
                <li className="flex gap-2 text-sm">
                  <span className="text-nidhi-gold">✓</span>
                  <span className="text-nidhi-text-secondary">Indian financial workflow optimization</span>
                </li>
                <li className="flex gap-2 text-sm">
                  <span className="text-nidhi-gold">✓</span>
                  <span className="text-nidhi-text-secondary">Demo data with real Indian names/addresses</span>
                </li>
              </ul>
            </div>
          </div>
        </motion.div>
      </div>
    </section>
  );
}
