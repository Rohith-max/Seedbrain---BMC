'use client';

import React, { useState, useEffect } from 'react';
import { motion } from 'framer-motion';
import { Button } from '@/components/ui/button';
import { ArrowRight, ShieldCheck, Sparkles } from 'lucide-react';
import Link from 'next/link';
import { APP_NAME_HINDI } from '@/lib/constants';

export function HeroSection() {
  const [particles, setParticles] = useState<Array<{left: string, delay: string, duration: string}>>([]);

  useEffect(() => {
    setParticles([...Array(20)].map(() => ({
      left: `${Math.random() * 100}%`,
      delay: `${Math.random() * 5}s`,
      duration: `${5 + Math.random() * 10}s`
    })));
  }, []);
  return (
    <div className="relative min-h-screen flex items-center justify-center overflow-hidden pt-20">
      {/* Background Elements */}
      <div className="absolute inset-0 z-0" aria-hidden="true">
        <div className="absolute top-1/4 left-1/4 w-72 md:w-96 h-72 md:h-96 bg-nidhi-gold/10 rounded-full blur-[120px] mix-blend-screen animate-pulse-gold"></div>
        <div className="absolute bottom-1/4 right-1/4 w-72 md:w-[30rem] h-72 md:h-[30rem] bg-nidhi-accent/10 rounded-full blur-[150px] mix-blend-screen"></div>
        <div className="gradient-mesh absolute inset-0 opacity-50"></div>
      </div>

      <div className="section-container relative z-10 flex flex-col items-center text-center">
        {/* Badge */}
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.5 }}
          className="badge badge-gold mb-8 inline-flex"
        >
          <Sparkles className="w-3 h-3 mr-1" />
          <span>Intelligent Household OS</span>
        </motion.div>

        {/* Heading */}
        <motion.h1
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.5, delay: 0.1 }}
          className="text-5xl md:text-7xl lg:text-8xl font-display font-bold tracking-tight mb-4"
        >
          Your Digital <br />
          <span className="text-gradient">Operating System</span>
          <br />
          <span className="text-2xl md:text-4xl text-nidhi-text-secondary font-light">for Indian Families</span>
        </motion.h1>

        {/* Subtitle */}
        <motion.p
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.5, delay: 0.2 }}
          className="text-lg md:text-xl text-nidhi-text-secondary max-w-3xl mb-10"
        >
          Organize Aadhaar to documents. Track LIC premiums to tax deadlines. Discover government schemes your family qualifies for. All in your language, designed for Indian households.
        </motion.p>

        {/* CTA Buttons */}
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.5, delay: 0.3 }}
          className="flex flex-col sm:flex-row gap-4 w-full sm:w-auto"
        >
          <Link href="/login" className="w-full sm:w-auto focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-nidhi-gold focus-visible:ring-offset-2 focus-visible:ring-offset-nidhi-black rounded-xl">
            <Button variant="nidhiGold" size="lg" className="w-full sm:w-auto text-lg group min-h-[48px]">
              Get Started Free
              <ArrowRight className="ml-2 w-5 h-5 group-hover:translate-x-1 transition-transform" />
            </Button>
          </Link>
          <Button variant="nidhiGlass" size="lg" className="w-full sm:w-auto text-lg min-h-[48px] focus-visible:ring-nidhi-gold">
            Watch Demo
          </Button>
        </motion.div>

        {/* Trust Indicators */}
        <motion.div
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          transition={{ duration: 1, delay: 0.8 }}
          className="mt-16 flex items-center justify-center gap-6 text-sm text-nidhi-text-muted"
        >
          <div className="flex items-center gap-2">
            <ShieldCheck className="w-4 h-4 text-nidhi-success" />
            <span>Bank-grade Encryption</span>
          </div>
          <div className="w-1 h-1 rounded-full bg-nidhi-border"></div>
          <div className="flex items-center gap-2">
            <ShieldCheck className="w-4 h-4 text-nidhi-success" />
            <span>Local-first Architecture</span>
          </div>
        </motion.div>
      </div>

      {/* Floating Particles (Rendered via CSS in globals.css) */}
      <div className="particles-container" aria-hidden="true">
        {particles.map((p, i) => (
          <div
            key={i}
            className="particle"
            style={{
              left: p.left,
              animationDelay: p.delay,
              animationDuration: p.duration
            }}
          />
        ))}
      </div>
    </div>
  );
}
