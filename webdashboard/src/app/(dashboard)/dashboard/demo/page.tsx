'use client';

import React, { useState } from 'react';
import { motion } from 'framer-motion';
import { SchemeEligibility } from '@/components/dashboard/scheme-eligibility';
import { ArrowRight, MapPin, Users, FileText, AlertCircle } from 'lucide-react';
import { DEMO_INDIAN_FAMILY_PROFILES, DEMO_FINANCIAL_TRACKING, DEMO_GOVERNMENT_ALERTS } from '@/lib/demo-data-india';

export default function DemoDashboard() {
  const [selectedFamily, setSelectedFamily] = useState(0);
  const family = DEMO_INDIAN_FAMILY_PROFILES[selectedFamily];

  const containerVariants = {
    hidden: { opacity: 0 },
    visible: {
      opacity: 1,
      transition: {
        staggerChildren: 0.1,
      },
    },
  };

  const itemVariants = {
    hidden: { opacity: 0, y: 10 },
    visible: { opacity: 1, y: 0 },
  };

  return (
    <div className="space-y-8">
      {/* Demo Header */}
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        className="bg-gradient-to-r from-purple-600/20 to-pink-600/20 border border-purple-500/30 rounded-xl p-6 mb-8"
      >
        <div className="flex items-start gap-4">
          <div className="bg-purple-500/20 p-3 rounded-lg">
            <AlertCircle className="w-6 h-6 text-purple-400" />
          </div>
          <div>
            <h2 className="text-lg font-semibold text-white mb-1">Demo Mode</h2>
            <p className="text-sm text-nidhi-text-secondary">
              Exploring NIDHI with realistic Indian household data. Switch between families below to see how the system adapts.
            </p>
          </div>
        </div>
      </motion.div>

      {/* Family Selector */}
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ delay: 0.1 }}
      >
        <h3 className="text-lg font-semibold text-white mb-4">Select Demo Family</h3>
        <div className="grid md:grid-cols-2 gap-4">
          {DEMO_INDIAN_FAMILY_PROFILES.map((f, idx) => (
            <button
              key={f.id}
              onClick={() => setSelectedFamily(idx)}
              className={`p-4 rounded-xl border transition-all text-left ${
                selectedFamily === idx
                  ? 'border-nidhi-gold bg-nidhi-gold/10'
                  : 'border-nidhi-border bg-nidhi-card hover:border-nidhi-gold/50'
              }`}
            >
              <div className="flex items-start justify-between mb-2">
                <div>
                  <h4 className="font-semibold text-white">{f.name}</h4>
                  <p className="text-sm text-nidhi-text-secondary">{f.familyHead}</p>
                </div>
                {selectedFamily === idx && <span className="text-nidhi-gold">✓</span>}
              </div>
              <div className="flex gap-4 text-xs text-nidhi-text-muted mt-2">
                <div className="flex items-center gap-1">
                  <MapPin className="w-3 h-3" />
                  <span>{f.city}</span>
                </div>
                <div className="flex items-center gap-1">
                  <Users className="w-3 h-3" />
                  <span>{f.members.length} members</span>
                </div>
              </div>
            </button>
          ))}
        </div>
      </motion.div>

      {/* Family Overview */}
      <motion.div
        variants={containerVariants}
        initial="hidden"
        animate="visible"
        className="grid md:grid-cols-2 gap-6"
      >
        <motion.div variants={itemVariants} className="bg-nidhi-card border border-nidhi-border rounded-xl p-6">
          <h3 className="text-lg font-semibold text-white mb-4 flex items-center gap-2">
            <Users className="w-5 h-5 text-nidhi-gold" />
            Family Details
          </h3>
          <div className="space-y-3">
            <div>
              <p className="text-xs text-nidhi-text-muted uppercase mb-1">Location</p>
              <p className="text-white font-medium">{family.city}, {family.state}</p>
            </div>
            <div>
              <p className="text-xs text-nidhi-text-muted uppercase mb-1">Family Members</p>
              <div className="space-y-1">
                {family.members.map((m, i) => (
                  <p key={i} className="text-sm text-nidhi-text-secondary">
                    {m.name} • <span className="text-nidhi-text-muted">{m.relation}</span>
                  </p>
                ))}
              </div>
            </div>
            <div className="pt-2 border-t border-nidhi-border">
              <p className="text-xs text-nidhi-text-muted uppercase mb-1">Annual Income</p>
              <p className="text-lg font-semibold text-nidhi-gold">₹{(family.income / 100000).toFixed(1)}L</p>
            </div>
          </div>
        </motion.div>

        <motion.div variants={itemVariants} className="bg-nidhi-card border border-nidhi-border rounded-xl p-6">
          <h3 className="text-lg font-semibold text-white mb-4 flex items-center gap-2">
            <FileText className="w-5 h-5 text-nidhi-accent" />
            Documents on File
          </h3>
          <div className="space-y-2">
            {Object.entries(family.documents).map(([doc, count]) => (
              <div key={doc} className="flex items-center justify-between p-2 bg-nidhi-black/50 rounded">
                <span className="text-sm text-nidhi-text-secondary capitalize">
                  {doc.replace(/_/g, ' ')}
                </span>
                <span className="bg-nidhi-gold/20 text-nidhi-gold text-xs font-semibold px-2 py-1 rounded">
                  {count}
                </span>
              </div>
            ))}
          </div>
        </motion.div>
      </motion.div>

      {/* Financial Tracking */}
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ delay: 0.2 }}
        className="bg-nidhi-card border border-nidhi-border rounded-xl p-6"
      >
        <h3 className="text-lg font-semibold text-white mb-4">Financial Deadlines</h3>
        <div className="space-y-3">
          {DEMO_FINANCIAL_TRACKING.slice(0, 4).map((tracking) => (
            <div key={tracking.id} className="flex items-start justify-between p-3 bg-nidhi-black/50 rounded-lg hover:bg-nidhi-black/70 transition">
              <div className="flex-1">
                <p className="font-medium text-white">{tracking.type}</p>
                <p className="text-xs text-nidhi-text-muted mt-1">Due: {tracking.dueDate || tracking.maturityDate}</p>
              </div>
              <div className="text-right">
                <p className="font-semibold text-nidhi-gold">{tracking.amount}</p>
                <span className={`text-xs px-2 py-1 rounded mt-1 inline-block ${
                  tracking.priority === 'High' ? 'bg-red-500/20 text-red-300' : 'bg-amber-500/20 text-amber-300'
                }`}>
                  {tracking.priority}
                </span>
              </div>
            </div>
          ))}
        </div>
      </motion.div>

      {/* Government Alerts */}
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ delay: 0.3 }}
        className="bg-nidhi-card border border-nidhi-border rounded-xl p-6"
      >
        <h3 className="text-lg font-semibold text-white mb-4">Government Scheme Alerts</h3>
        <div className="space-y-3">
          {DEMO_GOVERNMENT_ALERTS.map((alert) => (
            <div key={alert.id} className="p-4 bg-gradient-to-r from-nidhi-gold/10 to-nidhi-accent/10 border border-nidhi-gold/20 rounded-lg">
              <div className="flex items-start justify-between mb-2">
                <h4 className="font-semibold text-white">{alert.title}</h4>
                <span className={`text-xs px-2 py-1 rounded ${
                  alert.priority === 'High' ? 'bg-red-500/20 text-red-300' : 'bg-amber-500/20 text-amber-300'
                }`}>
                  {alert.priority}
                </span>
              </div>
              <p className="text-sm text-nidhi-text-secondary mb-3">{alert.description}</p>
              <div className="flex items-center justify-between">
                <span className="text-xs text-nidhi-text-muted">{alert.daysToDeadline}</span>
                <button className="text-nidhi-gold hover:text-nidhi-gold/70 text-sm font-medium flex items-center gap-1">
                  {alert.action} <ArrowRight className="w-3 h-3" />
                </button>
              </div>
            </div>
          ))}
        </div>
      </motion.div>

      {/* Scheme Eligibility Section */}
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ delay: 0.4 }}
      >
        <SchemeEligibility />
      </motion.div>

      {/* Demo Note */}
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ delay: 0.5 }}
        className="bg-nidhi-accent/10 border border-nidhi-accent/30 rounded-xl p-6 text-center"
      >
        <p className="text-nidhi-text-secondary">
          This is demo data showing realistic Indian household scenarios.{' '}
          <span className="text-nidhi-gold font-medium">Sign up</span> to start organizing your family documents, accessing real government schemes, and discovering benefits you qualify for.
        </p>
      </motion.div>
    </div>
  );
}
