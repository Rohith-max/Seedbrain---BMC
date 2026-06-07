'use client';

import React, { useEffect, useState } from 'react';
import { motion } from 'framer-motion';
import Link from 'next/link';
import { useAuthStore } from '@/lib/store/auth-store';
import {
  FileText,
  Gift,
  AlertCircle,
  IndianRupee,
  ChevronRight,
  Bot,
  Upload,
  Users,
} from 'lucide-react';

/* ── Animated number counter ── */
function useCounter(target: number, duration = 1200) {
  const [count, setCount] = useState(0);
  useEffect(() => {
    let startTs = 0;
    const step = (ts: number) => {
      if (!startTs) startTs = ts;
      const p = Math.min((ts - startTs) / duration, 1);
      const eased = 1 - Math.pow(1 - p, 3);
      setCount(Math.floor(eased * target));
      if (p < 1) requestAnimationFrame(step);
      else setCount(target);
    };
    requestAnimationFrame(step);
  }, [target, duration]);
  return count;
}

/* ── Hero stat counters ── */
function HeroStat({
  label,
  value,
  prefix = '',
  suffix = '',
  color = 'text-nidhi-text',
  icon: Icon,
}: {
  label: string;
  value: number;
  prefix?: string;
  suffix?: string;
  color?: string;
  icon: React.ElementType;
}) {
  const count = useCounter(value, 1400);
  return (
    <div className="flex flex-col gap-1 min-w-0">
      <div className="flex items-center gap-1.5">
        <Icon className={`w-4 h-4 flex-shrink-0 ${color}`} />
        <span className={`text-3xl font-display font-bold leading-none ${color}`}>
          {prefix}{count}{suffix}
        </span>
      </div>
      <span className="text-xs text-nidhi-text-muted leading-tight">{label}</span>
    </div>
  );
}

/* ── Intelligence feed data ── */
type FeedType = 'opportunity' | 'deadline' | 'action' | 'reminder';

const FEED: {
  id: string;
  type: FeedType;
  title: string;
  meta: string;
  href: string;
}[] = [
  {
    id: 'f1',
    type: 'opportunity',
    title: 'Aarav is eligible for a scholarship worth Rs 50,000',
    meta: 'CBSE Merit Scholarship — Apply by September 30',
    href: '/dashboard/benefits',
  },
  {
    id: 'f2',
    type: 'deadline',
    title: 'LIC policy renewal due in 18 days',
    meta: 'Policy #2847509 — Rs 12,500 annual premium',
    href: '/dashboard/alerts',
  },
  {
    id: 'f3',
    type: 'reminder',
    title: 'Property tax due next month',
    meta: 'BBMP — Rs 8,500 due July 5, 2026',
    href: '/dashboard/alerts',
  },
  {
    id: 'f4',
    type: 'opportunity',
    title: 'Senior citizen healthcare benefits available for Kamla Devi',
    meta: 'Ayushman Bharat — Rs 5 lakh coverage',
    href: '/dashboard/benefits',
  },
  {
    id: 'f5',
    type: 'action',
    title: 'Missing nominee in SBI savings account',
    meta: 'Account 2497**** — Add nominee for protection',
    href: '/dashboard/alerts',
  },
];

const TYPE_CFG: Record<FeedType, { border: string; tag: string; tagColor: string }> = {
  opportunity: {
    border: 'border-l-nidhi-success',
    tag: 'Opportunity',
    tagColor: 'tag-success',
  },
  deadline: {
    border: 'border-l-nidhi-warning',
    tag: 'Deadline',
    tagColor: 'tag-warning',
  },
  reminder: {
    border: 'border-l-nidhi-info',
    tag: 'Reminder',
    tagColor: 'tag',
  },
  action: {
    border: 'border-l-nidhi-danger',
    tag: 'Action',
    tagColor: 'tag-danger',
  },
};

/* ── Quick action cards ── */
const QUICK_ACTIONS = [
  {
    href: '/dashboard/assistant',
    icon: Bot,
    title: 'Ask AI Assistant',
    desc: 'Get instant answers from your documents',
    iconBg: 'bg-nidhi-gold/10 text-nidhi-gold',
  },
  {
    href: '/dashboard/benefits',
    icon: Gift,
    title: 'Claim Benefits',
    desc: 'Rs 3.7L in government schemes awaiting',
    iconBg: 'bg-nidhi-success/10 text-nidhi-success',
  },
  {
    href: '/dashboard/vault',
    icon: Upload,
    title: 'Upload Document',
    desc: 'Add Aadhaar, LIC, property records',
    iconBg: 'bg-nidhi-info/10 text-nidhi-info',
  },
  {
    href: '/dashboard/family',
    icon: Users,
    title: 'View Family',
    desc: 'Rajesh, Priya, Aarav, Ananya, Kamla Devi',
    iconBg: 'bg-purple-500/10 text-purple-400',
  },
];

/* ── Page ── */
export default function HomePage() {
  const { user } = useAuthStore();

  const hour = new Date().getHours();
  const greeting =
    hour < 12 ? 'Good morning' : hour < 17 ? 'Good afternoon' : 'Good evening';
  const firstName = user?.name?.split(' ')[0] ?? 'Rajesh';

  const docCount  = useCounter(128, 1200);
  const benCount  = useCounter(21, 1000);
  const riskCount = useCounter(3, 800);

  return (
    <div className="page-container space-y-14">

      {/* ── HERO GREETING ────────────────────────────── */}
      <section>
        <motion.div
          initial={{ opacity: 0, y: -16 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.45 }}
        >
          <p className="section-label mb-3">{greeting}</p>
          <h1
            style={{ fontSize: '52px', fontFamily: 'var(--font-display)', fontWeight: 700, lineHeight: 1.1, color: 'var(--color-nidhi-text)', letterSpacing: '-0.5px' }}
          >
            {firstName}.
          </h1>
          <p className="page-subtitle mt-2">Your family&apos;s intelligence report for today.</p>

          <div className="flex items-center gap-2 mt-4">
            <span className="relative flex h-2.5 w-2.5">
              <span className="animate-ping absolute inline-flex h-full w-full rounded-full bg-nidhi-success opacity-70" />
              <span className="relative inline-flex rounded-full h-2.5 w-2.5 bg-nidhi-success" />
            </span>
            <span className="text-sm text-nidhi-text-secondary">All systems active</span>
          </div>
        </motion.div>
      </section>

      {/* ── FAMILY HEALTH BANNER ─────────────────────── */}
      <section>
        <motion.div
          initial={{ opacity: 0, y: 16 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.45, delay: 0.1 }}
          className="card-hero p-8"
        >
          <div className="grid grid-cols-2 md:grid-cols-[auto_1px_1fr_1fr_1fr_1fr] gap-6 md:gap-8 items-center">
            {/* Score circle */}
            <div className="flex flex-col items-start">
              <p className="section-label mb-3">Family Health</p>
              <div className="score-ring">
                <svg width="88" height="88" viewBox="0 0 88 88">
                  <circle cx="44" cy="44" r="36" fill="none" stroke="var(--color-nidhi-border)" strokeWidth="6" />
                  <circle
                    cx="44"
                    cy="44"
                    r="36"
                    fill="none"
                    stroke="#D4AF37"
                    strokeWidth="6"
                    strokeLinecap="round"
                    strokeDasharray={`${2 * Math.PI * 36}`}
                    strokeDashoffset={`${2 * Math.PI * 36 * (1 - 0.94)}`}
                    transform="rotate(-90 44 44)"
                    style={{ transition: 'stroke-dashoffset 1.5s cubic-bezier(0.4,0,0.2,1)' }}
                  />
                  <text x="44" y="44" textAnchor="middle" dominantBaseline="middle" fontSize="18" fontWeight="700" fill="#D4AF37" fontFamily="Outfit, sans-serif">
                    94
                  </text>
                  <text x="44" y="59" textAnchor="middle" dominantBaseline="middle" fontSize="9" fill="#5A5A52">
                    SCORE
                  </text>
                </svg>
              </div>
              <span className="tag tag-success mt-2">Excellent</span>
            </div>

            {/* Divider */}
            <div className="hidden md:block h-16 bg-nidhi-border-subtle self-center" style={{ width: '1px' }} />

            {/* Stats */}
            <div>
              <HeroStat icon={FileText} label="Documents" value={128} color="text-nidhi-text" />
            </div>
            <div>
              <HeroStat icon={Gift} label="Benefits Available" value={21} color="text-nidhi-success" />
            </div>
            <div>
              <HeroStat icon={AlertCircle} label="Active Risks" value={3} color="text-nidhi-danger" />
            </div>
            <div>
              <div className="flex flex-col gap-1">
                <div className="flex items-center gap-1">
                  <IndianRupee className="w-4 h-4 text-nidhi-gold flex-shrink-0" />
                  <span className="text-3xl font-display font-bold text-nidhi-gold leading-none">3.7L</span>
                </div>
                <span className="text-xs text-nidhi-text-muted">Potential Savings</span>
              </div>
            </div>
          </div>
        </motion.div>
      </section>

      {/* ── INTELLIGENCE FEED ────────────────────────── */}
      <section>
        <motion.div
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          transition={{ delay: 0.2 }}
          className="flex items-center justify-between mb-6"
        >
          <div>
            <p className="section-label mb-1">Today&apos;s Briefing</p>
          </div>
          <Link
            href="/dashboard/assistant"
            className="flex items-center gap-1 text-xs text-nidhi-text-muted hover:text-nidhi-gold transition-colors"
          >
            Ask NIDHI
            <ChevronRight className="w-3 h-3" />
          </Link>
        </motion.div>

        <div className="space-y-2">
          {FEED.map((item, idx) => {
            const cfg = TYPE_CFG[item.type];
            return (
              <motion.div
                key={item.id}
                initial={{ opacity: 0, x: -12 }}
                animate={{ opacity: 1, x: 0 }}
                transition={{ duration: 0.35, delay: 0.25 + idx * 0.07 }}
              >
                <Link href={item.href} className="block group">
                  <div
                    className={`flex items-center gap-4 p-5 card card-hover border-l-[3px] ${cfg.border}`}
                    style={{
                      borderLeftColor:
                        item.type === 'opportunity' ? 'var(--color-nidhi-success)'
                        : item.type === 'deadline'  ? 'var(--color-nidhi-warning)'
                        : item.type === 'action'    ? 'var(--color-nidhi-danger)'
                        : 'var(--color-nidhi-info)',
                    }}
                  >
                    <div className="flex-1 min-w-0">
                      <div className="flex items-center gap-2 mb-1">
                        <span className={`tag ${cfg.tagColor}`}>{cfg.tag}</span>
                      </div>
                      <p className="text-[15px] font-semibold text-nidhi-text leading-snug">
                        {item.title}
                      </p>
                      <p className="text-xs text-nidhi-text-muted mt-1">{item.meta}</p>
                    </div>
                    <ChevronRight className="w-4 h-4 text-nidhi-text-muted group-hover:text-nidhi-gold flex-shrink-0 transition-colors" />
                  </div>
                </Link>
              </motion.div>
            );
          })}
        </div>
      </section>

      {/* ── QUICK ACTIONS ────────────────────────────── */}
      <section>
        <motion.div
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          transition={{ delay: 0.4 }}
          className="mb-6"
        >
          <p className="section-label">Quick Actions</p>
        </motion.div>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          {QUICK_ACTIONS.map((action, idx) => {
            const Icon = action.icon;
            return (
              <motion.div
                key={action.href}
                initial={{ opacity: 0, y: 12 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ duration: 0.35, delay: 0.45 + idx * 0.07 }}
              >
                <Link href={action.href} className="block group">
                  <div className="card card-hover flex items-center gap-4 p-5">
                    <div
                      className={`w-11 h-11 rounded-xl flex items-center justify-center flex-shrink-0 ${action.iconBg}`}
                    >
                      <Icon className="w-5 h-5" />
                    </div>
                    <div className="flex-1 min-w-0">
                      <p className="font-semibold text-nidhi-text text-[15px] leading-tight">
                        {action.title}
                      </p>
                      <p className="text-xs text-nidhi-text-muted mt-0.5">{action.desc}</p>
                    </div>
                    <ChevronRight className="w-4 h-4 text-nidhi-text-muted group-hover:text-nidhi-gold flex-shrink-0 transition-colors" />
                  </div>
                </Link>
              </motion.div>
            );
          })}
        </div>
      </section>

      <div className="h-8" />
    </div>
  );
}
