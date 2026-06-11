'use client';

import React, { useState } from 'react';
import { useRouter } from 'next/navigation';
import Link from 'next/link';
import { motion } from 'framer-motion';
import { APP_NAME, APP_NAME_HINDI } from '@/lib/constants';
import { Button } from '@/components/ui/button';
import { useAuthStore } from '@/lib/store/auth-store';
import { Lock, Mail, ArrowRight, User, Phone } from 'lucide-react';

export default function RegisterPage() {
  const [formData, setFormData] = useState({ name: '', email: '', phone: '', password: '' });
  const [error, setError] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const router = useRouter();
  const login = useAuthStore(state => state.login);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsSubmitting(true);
    setError('');

    try {
      const res = await fetch('/api/auth/register', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(formData),
      });

      const data = await res.json();

      if (res.ok && data.success) {
        login(data.data.user, data.data.token);
        router.push('/dashboard');
      } else {
        setError(data.message || 'Registration failed.');
      }
    } catch (err) {
      setError('An error occurred. Please try again.');
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  return (
    <div className="min-h-screen flex items-center justify-center relative overflow-hidden p-4">
      {/* Background Elements */}
      <div className="absolute inset-0 z-0">
        <div className="absolute top-1/4 right-1/4 w-96 h-96 bg-nidhi-gold/5 rounded-full blur-[100px] pointer-events-none"></div>
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
          <p className="text-nidhi-text-secondary">Create your Family Vault</p>
        </div>

        <div className="card-premium p-8 relative overflow-hidden">
          <div className="absolute top-0 left-0 w-full h-[2px] bg-gradient-to-r from-transparent via-nidhi-gold to-transparent opacity-50"></div>

          <form onSubmit={handleSubmit} className="space-y-4">
            {error && (
              <div className="p-3 rounded-lg bg-nidhi-danger/10 border border-nidhi-danger/20 text-nidhi-danger text-sm">
                {error}
              </div>
            )}

            <div>
              <label className="block text-sm font-medium text-nidhi-text-secondary mb-1">Full Name</label>
              <div className="relative">
                <User className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-nidhi-text-muted" />
                <input type="text" name="name" value={formData.name} onChange={handleChange} className="input-premium pl-10" placeholder="Head of Family Name" required />
              </div>
            </div>

            <div>
              <label className="block text-sm font-medium text-nidhi-text-secondary mb-1">Email Address</label>
              <div className="relative">
                <Mail className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-nidhi-text-muted" />
                <input type="email" name="email" value={formData.email} onChange={handleChange} className="input-premium pl-10" placeholder="Enter email" required />
              </div>
            </div>

            <div>
              <label className="block text-sm font-medium text-nidhi-text-secondary mb-1">Phone Number (Optional)</label>
              <div className="relative">
                <Phone className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-nidhi-text-muted" />
                <input type="tel" name="phone" value={formData.phone} onChange={handleChange} className="input-premium pl-10" placeholder="+91 00000 00000" />
              </div>
            </div>

            <div>
              <label className="block text-sm font-medium text-nidhi-text-secondary mb-1">Password</label>
              <div className="relative">
                <Lock className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-nidhi-text-muted" />
                <input type="password" name="password" value={formData.password} onChange={handleChange} className="input-premium pl-10" placeholder="Create secure password" required minLength={8} />
              </div>
            </div>

            <Button type="submit" variant="nidhiGold" className="w-full mt-4" disabled={isSubmitting}>
              {isSubmitting ? 'Creating Vault...' : 'Create Vault'}
              {!isSubmitting && <ArrowRight className="ml-2 w-4 h-4" />}
            </Button>
          </form>

          <div className="mt-6 pt-6 border-t border-nidhi-border-subtle text-center">
            <p className="text-sm text-nidhi-text-secondary">
              Already have an account?{' '}
              <Link href="/login" className="text-nidhi-text font-medium hover:text-nidhi-gold transition-colors">Sign in</Link>
            </p>
          </div>
        </div>
      </motion.div>
    </div>
  );
}
