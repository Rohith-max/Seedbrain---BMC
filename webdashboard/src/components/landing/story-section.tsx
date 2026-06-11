'use client';

import React from 'react';
import { motion } from 'framer-motion';

export function StorySection() {
  return (
    <section className="py-24 bg-nidhi-card border-y border-nidhi-border-subtle relative overflow-hidden">
      {/* Background Glow */}
      <div className="absolute top-0 right-0 w-96 h-96 bg-nidhi-gold/5 rounded-full blur-[100px] pointer-events-none"></div>

      <div className="container mx-auto px-4 md:px-6">
        <div className="flex flex-col md:flex-row items-center gap-12">
          
          <div className="md:w-1/2">
            <motion.div 
              initial={{ opacity: 0, scale: 0.9 }}
              whileInView={{ opacity: 1, scale: 1 }}
              viewport={{ once: true }}
              className="relative rounded-2xl overflow-hidden glass-strong p-2 border border-nidhi-border shadow-2xl"
            >
              <img 
                src="https://images.unsplash.com/photo-1600880292203-757bb62b4baf?q=80&w=2070&auto=format&fit=crop" 
                alt="Indian Family" 
                className="rounded-xl w-full h-[400px] object-cover opacity-80"
              />
              <div className="absolute inset-0 bg-gradient-to-t from-nidhi-deep to-transparent opacity-60"></div>
              
              {/* Floating UI Elements over image */}
              <motion.div 
                animate={{ y: [0, -10, 0] }}
                transition={{ duration: 4, repeat: Infinity, ease: "easeInOut" }}
                className="absolute bottom-6 left-6 card-premium p-4 max-w-[200px]"
              >
                <div className="text-xs text-nidhi-text-secondary mb-1">Upcoming Deadline</div>
                <div className="font-semibold text-nidhi-text text-sm">LIC Premium Due</div>
                <div className="text-nidhi-warning text-xs font-medium mt-1">In 5 Days</div>
              </motion.div>
            </motion.div>
          </div>

          <div className="md:w-1/2">
            <motion.div
              initial={{ opacity: 0, y: 30 }}
              whileInView={{ opacity: 1, y: 0 }}
              viewport={{ once: true }}
              transition={{ duration: 0.6 }}
            >
              <div className="text-nidhi-gold font-medium tracking-wider text-sm uppercase mb-3">The Problem</div>
              <h2 className="text-3xl md:text-4xl font-display font-bold mb-6">
                From household chaos to <br/> <span className="text-gradient">calm intelligence.</span>
              </h2>
              <div className="space-y-6 text-nidhi-text-secondary leading-relaxed">
                <p>
                  Every Indian household manages a massive invisible enterprise. Between Aadhaar cards, insurance policies, school fee receipts, property taxes, and health records — critical information gets lost in physical folders or scattered WhatsApp chats.
                </p>
                <p>
                  When an emergency strikes, or a deadline approaches, the frantic search begins. Valuable benefits are missed simply because no one connected the dots.
                </p>
                <p className="text-nidhi-text font-medium border-l-2 border-nidhi-gold pl-4 py-1">
                  NIDHI replaces this chaos with a proactive, intelligent system that works silently in the background, securing your family's future.
                </p>
              </div>
            </motion.div>
          </div>

        </div>
      </div>
    </section>
  );
}
