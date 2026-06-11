'use client';

import React from 'react';
import { motion } from 'framer-motion';
import { AlertCircle, CheckCircle, TrendingUp, Zap, Lightbulb } from 'lucide-react';

export function SchemeEligibility() {
  const eligibleSchemes = [
    {
      id: 'sukanya',
      name: 'Sukanya Samriddhi Yojana',
      status: 'Eligible',
      score: 95,
      description: 'Girl child savings - 7.6% interest, tax-free',
      benefit: '₹21+ lakh by age 21',
      action: 'Open Account',
      color: 'from-pink-500 to-rose-500'
    },
    {
      id: 'ayushman',
      name: 'Ayushman Bharat',
      status: 'Eligible',
      score: 90,
      description: 'Family health insurance coverage',
      benefit: '₹5 lakh annual coverage',
      action: 'Register Now',
      color: 'from-green-500 to-emerald-500'
    },
    {
      id: 'scholarship',
      name: 'National Scholarship',
      status: 'Near Eligible',
      score: 72,
      description: 'Educational scholarships for students',
      benefit: 'Up to ₹2.5 lakh annually',
      action: 'Upload Documents',
      color: 'from-blue-500 to-cyan-500'
    }
  ];

  const missingDocuments = [
    { scheme: 'PM Kisan', doc: 'Land Ownership Document' },
    { scheme: 'PMAY', doc: 'Property Tax Receipt' },
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
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-start justify-between">
        <div>
          <h1 className="text-3xl font-bold text-white mb-2">Government Schemes</h1>
          <p className="text-nidhi-text-secondary">Unlock benefits your family qualifies for</p>
        </div>
        <div className="bg-gradient-to-br from-nidhi-gold/20 to-nidhi-accent/20 px-4 py-2 rounded-lg border border-nidhi-gold/30">
          <p className="text-sm text-nidhi-text-muted">Benefits Found</p>
          <p className="text-2xl font-bold text-nidhi-gold">{eligibleSchemes.length}</p>
        </div>
      </div>

      {/* Eligible Schemes */}
      <motion.div
        variants={containerVariants}
        initial="hidden"
        animate="visible"
        className="space-y-4"
      >
        {eligibleSchemes.map((scheme) => (
          <motion.div
            key={scheme.id}
            variants={itemVariants}
            className="bg-nidhi-card border border-nidhi-border hover:border-nidhi-gold/50 rounded-xl p-6 transition-all hover:shadow-lg hover:shadow-nidhi-gold/10"
          >
            <div className="flex items-start justify-between mb-4">
              <div className="flex items-start gap-4 flex-1">
                <div className={`bg-gradient-to-br ${scheme.color} p-3 rounded-lg`}>
                  <TrendingUp className="w-6 h-6 text-white" />
                </div>
                <div className="flex-1">
                  <h3 className="text-lg font-semibold text-white mb-1">{scheme.name}</h3>
                  <p className="text-sm text-nidhi-text-secondary">{scheme.description}</p>
                </div>
              </div>
              <div className="text-right">
                <div className="inline-flex items-center gap-2 bg-nidhi-success/10 px-3 py-1 rounded-full">
                  <CheckCircle className="w-4 h-4 text-nidhi-success" />
                  <span className="text-sm font-medium text-nidhi-success">{scheme.status}</span>
                </div>
              </div>
            </div>

            <div className="space-y-3">
              {/* Eligibility Score */}
              <div>
                <div className="flex justify-between items-center mb-2">
                  <span className="text-sm text-nidhi-text-muted">Eligibility Score</span>
                  <span className="font-semibold text-white">{scheme.score}%</span>
                </div>
                <div className="w-full bg-nidhi-border rounded-full h-2">
                  <div
                    className={`bg-gradient-to-r ${scheme.color} h-2 rounded-full transition-all`}
                    style={{ width: `${scheme.score}%` }}
                  />
                </div>
              </div>

              {/* Benefit */}
              <div className="flex items-center justify-between pt-2">
                <div className="flex items-center gap-2">
                  <Zap className="w-4 h-4 text-nidhi-gold" />
                  <span className="text-sm font-medium text-nidhi-gold">{scheme.benefit}</span>
                </div>
                <button className="bg-nidhi-gold/20 hover:bg-nidhi-gold/30 text-nidhi-gold px-4 py-2 rounded-lg text-sm font-medium transition-colors">
                  {scheme.action}
                </button>
              </div>
            </div>
          </motion.div>
        ))}
      </motion.div>

      {/* Missing Documents Alert */}
      {missingDocuments.length > 0 && (
        <motion.div
          variants={itemVariants}
          className="bg-gradient-to-r from-amber-500/10 to-orange-500/10 border border-amber-500/30 rounded-xl p-6"
        >
          <div className="flex gap-4">
            <AlertCircle className="w-6 h-6 text-amber-500 flex-shrink-0 mt-1" />
            <div className="flex-1">
              <h3 className="font-semibold text-white mb-3">Unlock More Benefits</h3>
              <div className="space-y-2">
                {missingDocuments.map((item, idx) => (
                  <div key={idx} className="flex items-center justify-between bg-nidhi-card/50 p-3 rounded-lg">
                    <div>
                      <p className="text-sm text-white font-medium">{item.scheme}</p>
                      <p className="text-xs text-nidhi-text-muted">Missing: {item.doc}</p>
                    </div>
                    <button className="text-amber-500 hover:text-amber-400 text-sm font-medium">
                      Upload →
                    </button>
                  </div>
                ))}
              </div>
            </div>
          </div>
        </motion.div>
      )}

      {/* Info Card */}
      <div className="bg-nidhi-accent/10 border border-nidhi-accent/30 rounded-xl p-6">
        <p className="text-sm text-nidhi-text-secondary">
          <Lightbulb className="w-4 h-4 inline mr-1 text-nidhi-gold" /> <span className="font-medium text-white">Pro Tip:</span> The AI regularly scans for new government schemes matching your family profile. Keep documents updated to automatically qualify for new benefits as they're announced.
        </p>
      </div>
    </div>
  );
}
