'use client';

import React, { useState } from 'react';
import { useRouter } from 'next/navigation';
import Link from 'next/link';
import { motion } from 'framer-motion';
import { APP_NAME, APP_NAME_HINDI } from '@/lib/constants';
import { Button } from '@/components/ui/button';
import { useAuthStore } from '@/lib/store/auth-store';
import { Lock, Mail, ArrowRight, ShieldCheck } from 'lucide-react';

export default function LoginPage() {
  const [email, setEmail] = useState('rajesh.sharma@email.com');
  const [password, setPassword] = useState('demo-password');
  const [error, setError] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const router = useRouter();
  const login = useAuthStore(state => state.login);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsSubmitting(true);
    setError('');

    try {
      const res = await fetch('/api/auth/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, password }),
      });

      const data = await res.json();

      if (res.ok && data.success) {
        login(data.data.user, data.data.token);
        router.push('/dashboard');
      } else {
        setError(data.message || 'Login failed. Please try again.');
      }
    } catch (err) {
      setError('An error occurred. Please try again.');
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center relative overflow-hidden p-4">
      {/* Background Elements */}
      <div className="absolute inset-0 z-0">
        <div className="absolute top-1/4 left-1/4 w-96 h-96 bg-nidhi-gold/5 rounded-full blur-[100px] pointer-events-none"></div>
        <div className="absolute bottom-1/4 right-1/4 w-[30rem] h-[30rem] bg-nidhi-accent/5 rounded-full blur-[120px] pointer-events-none"></div>
        <div className="gradient-mesh absolute inset-0 opacity-40"></div>
      </div>

      <motion.div 
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.5 }}
        className="w-full max-w-md z-10"
      >
        <div className="text-center mb-8">
          <Link href="/" className="inline-block">
            <h1 className="text-4xl font-display font-bold tracking-wider mb-2">
              {APP_NAME} <span className="text-nidhi-gold">{APP_NAME_HINDI}</span>
            </h1>
          </Link>
          <p className="text-nidhi-text-secondary">Secure Household Login</p>
        </div>

        <div className="card-premium p-8 relative overflow-hidden">
          {/* Subtle Top Border Glow */}
          <div className="absolute top-0 left-0 w-full h-[2px] bg-gradient-to-r from-transparent via-nidhi-gold to-transparent opacity-50"></div>

          <form onSubmit={handleSubmit} className="space-y-5">
            {error && (
              <div className="p-3 rounded-lg bg-nidhi-danger/10 border border-nidhi-danger/20 text-nidhi-danger text-sm flex items-center gap-2">
                <div className="w-1 h-1 rounded-full bg-nidhi-danger"></div>
                {error}
              </div>
            )}

            <div>
              <label className="block text-sm font-medium text-nidhi-text-secondary mb-1.5">
                Email Address
              </label>
              <div className="relative">
                <Mail className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-nidhi-text-muted" />
                <input
                  type="email"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  className="input-premium pl-10"
                  placeholder="Enter your email"
                  required
                />
              </div>
            </div>

            <div>
              <div className="flex justify-between items-center mb-1.5">
                <label className="block text-sm font-medium text-nidhi-text-secondary">
                  Password
                </label>
                <Link href="#" className="text-xs text-nidhi-gold hover:text-nidhi-gold-light transition-colors">
                  Forgot Password?
                </Link>
              </div>
              <div className="relative">
                <Lock className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-nidhi-text-muted" />
                <input
                  type="password"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  className="input-premium pl-10"
                  placeholder="Enter your password"
                  required
                />
              </div>
            </div>

            <Button 
              type="submit" 
              variant="nidhiGold" 
              className="w-full mt-2" 
              disabled={isSubmitting}
            >
              {isSubmitting ? 'Authenticating...' : 'Access Vault'}
              {!isSubmitting && <ArrowRight className="ml-2 w-4 h-4" />}
            </Button>
          </form>

          <div className="mt-6 pt-6 border-t border-nidhi-border-subtle text-center">
            <p className="text-sm text-nidhi-text-secondary">
              Don't have an account?{' '}
              <Link href="/register" className="text-nidhi-text font-medium hover:text-nidhi-gold transition-colors">
                Create a Family Vault
              </Link>
            </p>
          </div>
        </div>

        <div className="mt-8 flex items-center justify-center gap-2 text-xs text-nidhi-text-muted">
          <ShieldCheck className="w-4 h-4 text-nidhi-success" />
          <span>End-to-End Encrypted Session</span>
        </div>
      </motion.div>
    </div>
  );
}
