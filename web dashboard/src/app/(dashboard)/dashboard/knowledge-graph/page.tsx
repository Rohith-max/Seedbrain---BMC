'use client';

import React from 'react';
import { motion } from 'framer-motion';
import { KnowledgeGraphLayout } from '@/components/dashboard/knowledge-graph-layout';
import { Network } from 'lucide-react';

export default function KnowledgeGraphPage() {
  return (
    <div className="px-8 py-10 h-screen flex flex-col max-w-full">

      {/* Header */}
      <motion.div
        initial={{ opacity: 0, y: -12 }}
        animate={{ opacity: 1, y: 0 }}
        className="flex-shrink-0 mb-8"
      >
        <p className="section-label mb-1">Sharma Family</p>
        <div className="flex items-center gap-3">
          <h1 className="text-4xl font-display font-bold text-nidhi-text">Family Graph</h1>
          <Network className="w-6 h-6 text-nidhi-text-muted flex-shrink-0" />
        </div>
        <p className="text-nidhi-text-secondary mt-1">
          Intelligence map — documents, relationships, benefits, and risks at a glance.
        </p>
      </motion.div>

      {/* Graph layout */}
      <motion.div
        initial={{ opacity: 0, y: 16 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ delay: 0.08 }}
        className="flex-1 min-h-0"
      >
        <KnowledgeGraphLayout />
      </motion.div>
    </div>
  );
}
