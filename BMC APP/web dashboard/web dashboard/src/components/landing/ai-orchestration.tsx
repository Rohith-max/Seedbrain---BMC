'use client';

import React from 'react';
import { motion } from 'framer-motion';

export function AIOrchestration() {
  return (
    <section className="py-24 overflow-hidden relative">
      {/* Ambient BG */}
      <div className="absolute inset-0 bg-nidhi-black z-0"></div>
      
      <div className="container mx-auto px-4 relative z-10">
        <div className="flex flex-col lg:flex-row items-center gap-16">
          
          <div className="lg:w-1/2">
            <motion.div
              initial={{ opacity: 0, x: -30 }}
              whileInView={{ opacity: 1, x: 0 }}
              viewport={{ once: true }}
              transition={{ duration: 0.6 }}
            >
              <h2 className="text-3xl md:text-5xl font-display font-bold mb-6 tracking-tight">
                Understand your family's <span className="text-gradient">Knowledge Graph.</span>
              </h2>
              <p className="text-nidhi-text-secondary text-lg mb-8 leading-relaxed">
                NIDHI doesn't just store files in folders. It creates a dynamic intelligence web. It knows that Aarav is your son, he has a Class 10 marksheet, which makes him eligible for a scholarship.
              </p>
              
              <ul className="space-y-4">
                {[
                  'Entity Extraction (Names, Dates, Amounts)',
                  'Semantic Relationship Mapping',
                  'Cross-document Eligibility Matching'
                ].map((item, i) => (
                  <li key={i} className="flex items-center gap-3">
                    <div className="w-6 h-6 rounded-full bg-nidhi-gold/20 flex items-center justify-center border border-nidhi-gold/30">
                      <div className="w-2 h-2 rounded-full bg-nidhi-gold"></div>
                    </div>
                    <span className="text-nidhi-text font-medium">{item}</span>
                  </li>
                ))}
              </ul>
            </motion.div>
          </div>

          <div className="lg:w-1/2 relative h-[500px] w-full">
            {/* Visual Graph Representation */}
            <div className="absolute inset-0 flex items-center justify-center">
              
              {/* Central Node */}
              <motion.div 
                animate={{ boxShadow: ['0 0 20px rgba(201,169,110,0.2)', '0 0 60px rgba(201,169,110,0.5)', '0 0 20px rgba(201,169,110,0.2)'] }}
                transition={{ duration: 4, repeat: Infinity }}
                className="absolute w-24 h-24 rounded-full bg-nidhi-card border border-nidhi-gold flex items-center justify-center z-20 shadow-2xl"
              >
                <span className="font-display font-bold text-nidhi-gold">Family</span>
              </motion.div>

              {/* Orbiting Nodes */}
              <motion.div 
                animate={{ rotate: 360 }}
                transition={{ duration: 20, repeat: Infinity, ease: "linear" }}
                className="absolute w-64 h-64 rounded-full border border-dashed border-nidhi-border-subtle"
              >
                <div className="absolute top-0 left-1/2 -translate-x-1/2 -translate-y-1/2 w-16 h-16 rounded-xl glass-strong flex items-center justify-center -rotate-0 text-sm">
                  📄 Docs
                </div>
                <div className="absolute bottom-0 left-1/2 -translate-x-1/2 translate-y-1/2 w-16 h-16 rounded-xl glass-strong flex items-center justify-center rotate-180 text-sm">
                  ⏰ Alerts
                </div>
              </motion.div>

              <motion.div 
                animate={{ rotate: -360 }}
                transition={{ duration: 30, repeat: Infinity, ease: "linear" }}
                className="absolute w-96 h-96 rounded-full border border-dashed border-nidhi-border-subtle"
              >
                <div className="absolute left-0 top-1/2 -translate-y-1/2 -translate-x-1/2 w-16 h-16 rounded-xl glass-strong border-nidhi-success/30 flex items-center justify-center rotate-90 text-sm">
                  🎁 Benefits
                </div>
                <div className="absolute right-0 top-1/2 -translate-y-1/2 translate-x-1/2 w-16 h-16 rounded-xl glass-strong border-nidhi-info/30 flex items-center justify-center -rotate-90 text-sm">
                  👥 Members
                </div>
              </motion.div>

            </div>
          </div>

        </div>
      </div>
    </section>
  );
}
