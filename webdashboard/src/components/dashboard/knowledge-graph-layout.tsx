'use client';

import React, { useState } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import {
  Shield, TrendingUp, Gift, Clock, AlertTriangle, FileText, Users,
  ChevronRight, Fingerprint, Stethoscope, Home, Banknote,
  ShieldCheck, GraduationCap, Receipt, CheckCircle2, Info, BarChart2,
} from 'lucide-react';
import {
  RadialBarChart, RadialBar, ResponsiveContainer,
  BarChart, Bar, XAxis, YAxis, Tooltip, Cell,
  PieChart, Pie,
} from 'recharts';

// ─── Data ──────────────────────────────────────────────────────────────────

const FAMILY_INSIGHTS = [
  { id: 'fi-1', icon: Shield,        label: 'Family Readiness',  value: '94', unit: '/100', color: '#10B981', bg: 'bg-emerald-500/10', arc: 94  },
  { id: 'fi-2', icon: Gift,          label: 'Benefits Available', value: '₹8.9L+', unit: '',    color: '#D4AF37', bg: 'bg-nidhi-gold/10',   arc: 89  },
  { id: 'fi-3', icon: AlertTriangle, label: 'Upcoming Risks',    value: '3',    unit: ' critical', color: '#F87171', bg: 'bg-red-500/10',     arc: 30  },
  { id: 'fi-4', icon: TrendingUp,    label: 'Tax Savings',       value: '₹46.8K', unit: '',   color: '#60A5FA', bg: 'bg-blue-500/10',     arc: 72  },
  { id: 'fi-5', icon: FileText,      label: 'Vault Documents',   value: '12',   unit: ' docs',     color: '#A78BFA', bg: 'bg-purple-500/10', arc: 60  },
  { id: 'fi-6', icon: Users,         label: 'Family Members',    value: '5',    unit: ' members',  color: '#F472B6', bg: 'bg-pink-500/10',   arc: 100 },
];

interface PersonNode {
  id: string; name: string; role: string; docs: number;
  color: string; cx: number; cy: number;
}

const PERSON_NODES: PersonNode[] = [
  { id: 'fm-001', name: 'Rajesh',  role: 'Head',     docs: 7, color: '#D4AF37', cx: 200, cy: 195 },
  { id: 'fm-002', name: 'Priya',   role: 'Spouse',   docs: 1, color: '#10B981', cx: 108, cy: 130 },
  { id: 'fm-003', name: 'Aarav',   role: 'Son',      docs: 1, color: '#3B82F6', cx: 295, cy: 130 },
  { id: 'fm-004', name: 'Ananya',  role: 'Daughter', docs: 0, color: '#A78BFA', cx: 295, cy: 265 },
  { id: 'fm-005', name: 'Kamla',   role: 'Mother',   docs: 1, color: '#F87171', cx: 108, cy: 265 },
];

const GRAPH_EDGES = [
  { from: 'fm-001', to: 'fm-002' }, { from: 'fm-001', to: 'fm-003' },
  { from: 'fm-001', to: 'fm-004' }, { from: 'fm-001', to: 'fm-005' },
];

const MEMBER_DETAILS: Record<string, {
  docTypes: { Icon: React.ElementType; label: string; status: 'ok' | 'missing' | 'expiring' }[];
  recommendations: string[];
}> = {
  'fm-001': {
    docTypes: [
      { Icon: Fingerprint,  label: 'Aadhaar',            status: 'ok'       },
      { Icon: Receipt,      label: 'PAN Card',            status: 'ok'       },
      { Icon: ShieldCheck,  label: 'Health Insurance',    status: 'expiring' },
      { Icon: Banknote,     label: 'Home Loan Statement', status: 'ok'       },
      { Icon: Home,         label: 'Property Deed',       status: 'ok'       },
      { Icon: ShieldCheck,  label: 'LIC Term Plan',       status: 'ok'       },
    ],
    recommendations: [
      'File ITR before 31 July 2025',
      'Claim Section 80C deduction (₹1.5L)',
      'Renew vehicle insurance by Dec 2025',
    ],
  },
  'fm-002': {
    docTypes: [
      { Icon: Fingerprint, label: 'Aadhaar', status: 'ok'      },
      { Icon: Receipt,     label: 'PAN Card', status: 'missing' },
    ],
    recommendations: [
      "Upload Priya's PAN card to vault",
      'Add as nominee on SBI savings account',
    ],
  },
  'fm-003': {
    docTypes: [{ Icon: GraduationCap, label: 'CBSE Report Card', status: 'ok' }],
    recommendations: [
      'Apply for CBSE Merit Scholarship by Sept 30',
      'Check KVPY eligibility (opens July 2025)',
    ],
  },
  'fm-004': {
    docTypes: [],
    recommendations: [
      'Open Sukanya Samriddhi account (SSY)',
      'Add birth certificate to vault',
    ],
  },
  'fm-005': {
    docTypes: [{ Icon: Stethoscope, label: 'Medical Report', status: 'ok' }],
    recommendations: [
      'Schedule diabetes follow-up (due Aug 2025)',
      'Check Ayushman Bharat PM-JAY eligibility',
      'Senior citizen health deduction under 80D',
    ],
  },
};

// ─── Chart Data ────────────────────────────────────────────────────────────

const DOC_CATEGORY_DATA = [
  { name: 'Identity',  value: 2, fill: '#D4AF37' },
  { name: 'Tax',       value: 2, fill: '#10B981' },
  { name: 'Insurance', value: 2, fill: '#3B82F6' },
  { name: 'Medical',   value: 1, fill: '#F87171' },
  { name: 'Property',  value: 1, fill: '#A78BFA' },
  { name: 'Education', value: 1, fill: '#F472B6' },
  { name: 'Vehicle',   value: 1, fill: '#FB923C' },
  { name: 'Financial', value: 1, fill: '#34D399' },
  { name: 'Utility',   value: 1, fill: '#94A3B8' },
];

const ALERT_DATA = [
  { label: 'Critical', count: 1, fill: '#F87171' },
  { label: 'High',     count: 3, fill: '#FB923C' },
  { label: 'Medium',   count: 2, fill: '#FBBF24' },
  { label: 'Low',      count: 2, fill: '#60A5FA' },
];

const BENEFIT_DATA = [
  { name: 'PMAY Subsidy',     value: 267000, fill: '#D4AF37' },
  { name: 'Merit Scholarship',value: 24000,  fill: '#3B82F6' },
  { name: 'PMJAY Health',     value: 500000, fill: '#10B981' },
  { name: 'Tax Saving 80C',   value: 46800,  fill: '#A78BFA' },
  { name: 'SSY Maturity',     value: 6500000,fill: '#F472B6' },
];

// ─── Radial Arc SVG (mini) ─────────────────────────────────────────────────

function RadialArc({ pct, color, size = 48 }: { pct: number; color: string; size?: number }) {
  const r = size / 2 - 5;
  const circ = 2 * Math.PI * r;
  const dash = (pct / 100) * circ;
  return (
    <svg width={size} height={size} className="flex-shrink-0">
      <circle cx={size / 2} cy={size / 2} r={r} fill="none" stroke="rgba(255,255,255,0.06)" strokeWidth={4} />
      <circle
        cx={size / 2} cy={size / 2} r={r} fill="none"
        stroke={color} strokeWidth={4} strokeLinecap="round"
        strokeDasharray={`${dash} ${circ}`}
        strokeDashoffset={circ / 4}
        style={{ transition: 'stroke-dasharray 0.8s ease' }}
      />
    </svg>
  );
}

// ─── Custom Tooltip ────────────────────────────────────────────────────────

const ChartTooltip = ({ active, payload, label }: any) => {
  if (!active || !payload?.length) return null;
  return (
    <div className="bg-nidhi-card border border-nidhi-border-subtle rounded-xl px-3 py-2 text-xs shadow-xl">
      {label && <p className="text-nidhi-text-muted mb-1">{label}</p>}
      {payload.map((p: any, i: number) => (
        <p key={i} style={{ color: p.fill || p.color }} className="font-semibold">
          {p.name ? `${p.name}: ` : ''}{typeof p.value === 'number' && p.value > 9999
            ? `₹${(p.value / 100000).toFixed(1)}L`
            : p.value}
        </p>
      ))}
    </div>
  );
};

// ─── Insight Tabs ──────────────────────────────────────────────────────────

type InsightTab = 'documents' | 'alerts' | 'benefits';

function InsightCharts() {
  const [tab, setTab] = useState<InsightTab>('documents');

  return (
    <div className="card rounded-2xl p-6 space-y-5">
      {/* Tab bar */}
      <div className="flex items-center gap-1 p-1 bg-nidhi-surface rounded-xl w-fit">
        {(['documents', 'alerts', 'benefits'] as InsightTab[]).map((t) => (
          <button
            key={t}
            onClick={() => setTab(t)}
            className={`px-4 py-2 rounded-lg text-xs font-semibold capitalize transition-all duration-200 ${
              tab === t
                ? 'bg-nidhi-gold text-nidhi-black shadow-sm'
                : 'text-nidhi-text-muted hover:text-nidhi-text'
            }`}
          >
            {t}
          </button>
        ))}
      </div>

      <AnimatePresence mode="wait">
        {tab === 'documents' && (
          <motion.div key="docs" initial={{ opacity: 0, y: 6 }} animate={{ opacity: 1, y: 0 }} exit={{ opacity: 0 }}>
            <p className="text-xs uppercase tracking-widest text-nidhi-text-muted mb-4">12 docs across 9 categories</p>
            <ResponsiveContainer width="100%" height={360}>
              <BarChart data={DOC_CATEGORY_DATA} barSize={22} layout="vertical">
                <XAxis type="number" hide />
                <YAxis type="category" dataKey="name" tick={{ fontSize: 12, fill: '#9A9A8E' }} width={68} axisLine={false} tickLine={false} />
                <Tooltip content={<ChartTooltip />} cursor={{ fill: 'rgba(255,255,255,0.03)' }} />
                <Bar dataKey="value" radius={[0, 6, 6, 0]}>
                  {DOC_CATEGORY_DATA.map((d, i) => <Cell key={i} fill={d.fill} fillOpacity={0.85} />)}
                </Bar>
              </BarChart>
            </ResponsiveContainer>
          </motion.div>
        )}

        {tab === 'alerts' && (
          <motion.div key="alerts" initial={{ opacity: 0, y: 6 }} animate={{ opacity: 1, y: 0 }} exit={{ opacity: 0 }}>
            <p className="text-xs uppercase tracking-widest text-nidhi-text-muted mb-4">8 active alerts by priority</p>
            <ResponsiveContainer width="100%" height={360}>
              <BarChart data={ALERT_DATA} barSize={64}>
                <XAxis dataKey="label" tick={{ fontSize: 13, fill: '#9A9A8E' }} axisLine={false} tickLine={false} />
                <YAxis hide />
                <Tooltip content={<ChartTooltip />} cursor={{ fill: 'rgba(255,255,255,0.03)' }} />
                <Bar dataKey="count" radius={[6, 6, 0, 0]}>
                  {ALERT_DATA.map((d, i) => <Cell key={i} fill={d.fill} fillOpacity={0.9} />)}
                </Bar>
              </BarChart>
            </ResponsiveContainer>
          </motion.div>
        )}

        {tab === 'benefits' && (
          <motion.div key="benefits" initial={{ opacity: 0, y: 6 }} animate={{ opacity: 1, y: 0 }} exit={{ opacity: 0 }}>
            <p className="text-xs uppercase tracking-widest text-nidhi-text-muted mb-4">Total identified value: ₹73.4L+</p>
            <ResponsiveContainer width="100%" height={360}>
              <BarChart data={BENEFIT_DATA} layout="vertical" barSize={26}>
                <XAxis type="number" hide />
                <YAxis type="category" dataKey="name" tick={{ fontSize: 11, fill: '#9A9A8E' }} width={120} axisLine={false} tickLine={false} />
                <Tooltip content={<ChartTooltip />} cursor={{ fill: 'rgba(255,255,255,0.03)' }} />
                <Bar dataKey="value" radius={[0, 6, 6, 0]}>
                  {BENEFIT_DATA.map((d, i) => <Cell key={i} fill={d.fill} fillOpacity={0.85} />)}
                </Bar>
              </BarChart>
            </ResponsiveContainer>
          </motion.div>
        )}
      </AnimatePresence>
    </div>
  );
}

// ─── Component ───────────────────────────────────────────────────────────────

export function KnowledgeGraphLayout() {
  const [selected, setSelected] = useState<PersonNode | null>(null);
  const details = selected ? MEMBER_DETAILS[selected.id] : null;

  return (
    <div className="grid grid-cols-1 lg:grid-cols-[240px_1fr_280px] gap-5">

      {/* LEFT — Family Intelligence (enhanced stat cards) */}
      <motion.div
        initial={{ opacity: 0, x: -16 }} animate={{ opacity: 1, x: 0 }}
        transition={{ duration: 0.35 }}
        className="space-y-2.5 overflow-y-auto hide-scrollbar"
      >
        <h2 className="text-[10px] font-semibold uppercase tracking-widest text-nidhi-text-muted pt-1 pb-0.5">
          Family Intelligence
        </h2>
        {FAMILY_INSIGHTS.map(({ id, icon: Icon, label, value, unit, color, bg, arc }) => (
          <motion.div
            key={id}
            whileHover={{ x: 2 }}
            className="card p-3.5 rounded-xl flex items-center gap-3 cursor-default"
          >
            {/* Mini radial arc */}
            <div className="relative flex-shrink-0">
              <RadialArc pct={arc} color={color} size={44} />
              <div className="absolute inset-0 flex items-center justify-center">
                <Icon style={{ color }} className="w-3.5 h-3.5" />
              </div>
            </div>
            <div className="flex-1 min-w-0">
              <p className="text-[10px] text-nidhi-text-muted leading-tight truncate">{label}</p>
              <p className="text-sm font-bold leading-tight mt-0.5" style={{ color }}>
                {value}<span className="text-[10px] font-normal opacity-70">{unit}</span>
              </p>
            </div>
          </motion.div>
        ))}
      </motion.div>

      {/* CENTER — Graph + Insight Charts */}
      <div className="flex flex-col gap-5">
        {/* Graph */}
        <motion.div
          initial={{ opacity: 0, scale: 0.97 }} animate={{ opacity: 1, scale: 1 }}
          transition={{ duration: 0.35, delay: 0.1 }}
          className="card rounded-2xl overflow-hidden relative flex items-center justify-center"
          style={{ height: 380 }}
        >
          <svg
            viewBox="0 0 400 400"
            className="w-full h-full max-h-[360px]"
            style={{ filter: 'drop-shadow(0 0 12px rgba(212,175,55,0.08))' }}
          >
            <defs>
              <linearGradient id="edge-grad" x1="0%" y1="0%" x2="100%" y2="100%">
                <stop offset="0%"   stopColor="rgba(212,175,55,0.3)" />
                <stop offset="100%" stopColor="rgba(212,175,55,0.06)" />
              </linearGradient>
              <filter id="glow">
                <feGaussianBlur stdDeviation="3" result="blur" />
                <feComposite in="SourceGraphic" in2="blur" operator="over" />
              </filter>
            </defs>

            {/* Edges */}
            {GRAPH_EDGES.map((edge, i) => {
              const from = PERSON_NODES.find(n => n.id === edge.from)!;
              const to   = PERSON_NODES.find(n => n.id === edge.to)!;
              return (
                <line key={i} x1={from.cx} y1={from.cy} x2={to.cx} y2={to.cy}
                  stroke="url(#edge-grad)" strokeWidth="1.5" strokeDasharray="4 3" />
              );
            })}

            {/* Nodes */}
            {PERSON_NODES.map((node) => {
              const isSel = selected?.id === node.id;
              return (
                <motion.g key={node.id}
                  initial={{ opacity: 0, scale: 0.6 }} animate={{ opacity: 1, scale: 1 }}
                  whileHover={{ scale: 1.12 }}
                  transition={{ type: 'spring', stiffness: 300, damping: 20 }}
                  onClick={() => setSelected(isSel ? null : node)}
                  style={{ cursor: 'pointer' }}
                >
                  {/* Pulse ring on select */}
                  {isSel && (
                    <motion.circle cx={node.cx} cy={node.cy} r={34} fill="none"
                      stroke={node.color} strokeWidth={1.5} opacity={0.3}
                      animate={{ r: [28, 36, 28] }} transition={{ duration: 2, repeat: Infinity }} />
                  )}
                  {/* Outer ring */}
                  <circle cx={node.cx} cy={node.cy} r={26}
                    fill={`${node.color}10`} stroke={node.color}
                    strokeWidth={isSel ? 2.5 : 1.5} />
                  {/* Name */}
                  <text x={node.cx} y={node.cy - 1} textAnchor="middle"
                    dominantBaseline="middle" fontSize="9.5" fontWeight="600"
                    fill={node.color} fontFamily="Outfit, sans-serif">
                    {node.name}
                  </text>
                  {/* Doc badge */}
                  {node.docs > 0 && (
                    <>
                      <circle cx={node.cx + 18} cy={node.cy - 18} r={9} fill={node.color} opacity={0.9} />
                      <text x={node.cx + 18} y={node.cy - 18} textAnchor="middle"
                        dominantBaseline="middle" fontSize="8" fontWeight="700" fill="#0C0C0C">
                        {node.docs}
                      </text>
                    </>
                  )}
                  {/* Role */}
                  <text x={node.cx} y={node.cy + 36} textAnchor="middle"
                    dominantBaseline="middle" fontSize="8" fill="rgba(154,154,142,0.7)">
                    {node.role}
                  </text>
                </motion.g>
              );
            })}
          </svg>

          {!selected && (
            <div className="absolute bottom-3 left-0 right-0 flex justify-center pointer-events-none">
              <p className="text-[10px] text-nidhi-text-muted bg-nidhi-surface/80 px-3 py-1 rounded-full border border-nidhi-border-subtle">
                Click a node to view details
              </p>
            </div>
          )}
        </motion.div>

        {/* Chart Tabs */}
        <motion.div
          initial={{ opacity: 0, y: 12 }} animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.2 }}
        >
          <InsightCharts />
        </motion.div>
      </div>

      {/* RIGHT — Detail Panel */}
      <motion.div
        initial={{ opacity: 0, x: 16 }} animate={{ opacity: 1, x: 0 }}
        transition={{ duration: 0.35, delay: 0.15 }}
        className="space-y-4 overflow-y-auto hide-scrollbar"
      >
        {selected && details ? (
          <>
            {/* Member header */}
            <div className="flex items-center gap-3">
              <div className="w-11 h-11 rounded-xl flex items-center justify-center text-base font-bold"
                style={{ backgroundColor: `${selected.color}20`, color: selected.color }}>
                {selected.name.charAt(0)}
              </div>
              <div>
                <h3 className="font-semibold text-nidhi-text">{selected.name} Sharma</h3>
                <p className="text-xs text-nidhi-text-muted">{selected.role} · {selected.docs} docs</p>
              </div>
            </div>

            {/* Document status */}
            {details.docTypes.length > 0 && (
              <div className="card p-4 rounded-xl space-y-3">
                <p className="text-[10px] font-semibold uppercase tracking-widest text-nidhi-text-muted">Documents</p>
                <div className="space-y-2">
                  {details.docTypes.map(({ Icon, label, status }, i) => (
                    <div key={i} className="flex items-center gap-2.5 text-xs">
                      <Icon className="w-3.5 h-3.5 text-nidhi-text-muted flex-shrink-0" />
                      <span className="flex-1 text-nidhi-text-secondary">{label}</span>
                      {status === 'ok'      && <CheckCircle2    className="w-3.5 h-3.5 text-nidhi-success"  />}
                      {status === 'expiring'&& <Clock           className="w-3.5 h-3.5 text-nidhi-warning"  />}
                      {status === 'missing' && <AlertTriangle   className="w-3.5 h-3.5 text-nidhi-danger"   />}
                    </div>
                  ))}
                </div>
              </div>
            )}

            {/* Recommendations */}
            <div className="card p-4 rounded-xl space-y-3">
              <p className="text-[10px] font-semibold uppercase tracking-widest text-nidhi-text-muted">Recommendations</p>
              <div className="space-y-2">
                {details.recommendations.map((rec, i) => (
                  <motion.div key={i}
                    initial={{ opacity: 0, x: -8 }} animate={{ opacity: 1, x: 0 }}
                    transition={{ delay: i * 0.05 }}
                    className="flex items-start gap-2 text-xs text-nidhi-text-secondary cursor-pointer group"
                  >
                    <ChevronRight className="w-3 h-3 text-nidhi-gold flex-shrink-0 mt-0.5 group-hover:translate-x-0.5 transition-transform" />
                    <span className="group-hover:text-nidhi-text transition-colors">{rec}</span>
                  </motion.div>
                ))}
              </div>
            </div>

            {/* Mini doc coverage bar */}
            <div className="card p-4 rounded-xl space-y-2">
              <p className="text-[10px] font-semibold uppercase tracking-widest text-nidhi-text-muted">Coverage</p>
              {['Identity','Financial','Insurance','Medical','Education'].map((cat, i) => {
                const filled = details.docTypes.some(d =>
                  d.label.toLowerCase().includes(cat.toLowerCase()) && d.status !== 'missing'
                );
                return (
                  <div key={i} className="flex items-center gap-2 text-[10px]">
                    <span className="w-16 text-nidhi-text-muted truncate">{cat}</span>
                    <div className="flex-1 h-1.5 rounded-full bg-nidhi-border overflow-hidden">
                      <div className={`h-full rounded-full transition-all ${filled ? 'bg-nidhi-success' : 'bg-nidhi-danger/40'}`}
                        style={{ width: filled ? '100%' : '15%' }} />
                    </div>
                    {filled
                      ? <CheckCircle2 className="w-3 h-3 text-nidhi-success flex-shrink-0" />
                      : <AlertTriangle className="w-3 h-3 text-nidhi-danger/60 flex-shrink-0" />}
                  </div>
                );
              })}
            </div>
          </>
        ) : (
          <div className="flex flex-col items-center justify-center h-48 text-center gap-3 opacity-50">
            <BarChart2 className="w-8 h-8 text-nidhi-text-muted" />
            <p className="text-sm text-nidhi-text-muted">Select a node to view<br />member details</p>
          </div>
        )}
      </motion.div>
    </div>
  );
}
