'use client';

import React from 'react';
import { Shield, Lock, ServerOff, CheckCircle2 } from 'lucide-react';
import { motion } from 'framer-motion';

export function TrustSection() {
  return (
    <section className="py-24 bg-nidhi-deep">
      <div className="section-container">
        
        <div className="text-center max-w-2xl mx-auto mb-16">
          <Shield className="w-12 h-12 text-nidhi-success mx-auto mb-6" aria-hidden="true" />
          <h2 className="text-3xl md:text-4xl font-display font-bold mb-4 tracking-tight">
            Uncompromising Security
          </h2>
          <p className="text-nidhi-text-secondary text-lg">
            Your household data is highly sensitive. We built NIDHI with enterprise-grade security protocols so you can have total peace of mind.
          </p>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-8 max-w-5xl mx-auto">
          
          <motion.div 
            whileHover={{ y: -5 }}
            className="p-8 rounded-2xl glass border border-nidhi-border flex flex-col items-center text-center"
          >
            <Lock className="w-10 h-10 text-nidhi-gold mb-4" aria-hidden="true" />
            <h3 className="text-xl font-semibold mb-2">End-to-End Encryption</h3>
            <p className="text-sm text-nidhi-text-secondary">
              All documents and metadata are encrypted before being stored. Only you hold the decryption keys.
            </p>
          </motion.div>

          <motion.div 
            whileHover={{ y: -5 }}
            className="p-8 rounded-2xl glass border border-nidhi-border flex flex-col items-center text-center"
          >
            <ServerOff className="w-10 h-10 text-nidhi-gold mb-4" aria-hidden="true" />
            <h3 className="text-xl font-semibold mb-2">Local-First AI</h3>
            <p className="text-sm text-nidhi-text-secondary">
              Core OCR and intelligence processing can run directly on your device, ensuring data never leaves your control.
            </p>
          </motion.div>

          <motion.div 
            whileHover={{ y: -5 }}
            className="p-8 rounded-2xl glass border border-nidhi-border flex flex-col items-center text-center"
          >
            <CheckCircle2 className="w-10 h-10 text-nidhi-gold mb-4" aria-hidden="true" />
            <h3 className="text-xl font-semibold mb-2">Audit Logs</h3>
            <p className="text-sm text-nidhi-text-secondary">
              Every access, download, or AI query is logged. Complete transparency on who accessed what in your family vault.
            </p>
          </motion.div>

        </div>
      </div>
    </section>
  );
}
