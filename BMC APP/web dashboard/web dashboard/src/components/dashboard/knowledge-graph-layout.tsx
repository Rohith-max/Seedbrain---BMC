'use client';

import React, { useState } from 'react';
import { motion } from 'framer-motion';
import {
  Shield,
  TrendingUp,
  Gift,
  Clock,
  AlertTriangle,
  FileText,
  Users,
  ChevronRight,
  Fingerprint,
  Stethoscope,
  Home,
  Banknote,
  ShieldCheck,
  GraduationCap,
  Receipt,
  CheckCircle2,
  Info,
} from 'lucide-react';

// ─── Data ──────────────────────────────────────────────────────────────────

const FAMILY_INSIGHTS = [
  {
    id: 'fi-1',
    icon: Shield,
    label: 'Family Readiness Score',
    value: '94/100',
    sub: 'Excellent — 2 documents missing',
    color: 'text-nidhi-success',
    bg: 'bg-nidhi-success/10',
  },
  {
    id: 'fi-2',
    icon: Gift,
    label: 'Benefits Available',
    value: '₹8.9L+',
    sub: '5 schemes identified by AI',
    color: 'text-nidhi-gold',
    bg: 'bg-nidhi-gold/10',
  },
  {
    id: 'fi-3',
    icon: AlertTriangle,
    label: 'Upcoming Risks',
    value: '3 critical',
    sub: 'ITR deadline, EMI, insurance',
    color: 'text-nidhi-danger',
    bg: 'bg-nidhi-danger/10',
  },
  {
    id: 'fi-4',
    icon: TrendingUp,
    label: 'Potential Tax Savings',
    value: '₹46,800',
    sub: 'Section 80C + 24(b) unclaimed',
    color: 'text-nidhi-info',
    bg: 'bg-nidhi-info/10',
  },
  {
    id: 'fi-5',
    icon: FileText,
    label: 'Documents in Vault',
    value: '12',
    sub: 'Across 5 family members',
    color: 'text-nidhi-text-secondary',
    bg: 'bg-nidhi-elevated',
  },
  {
    id: 'fi-6',
    icon: Users,
    label: 'Family Members',
    value: '5',
    sub: 'Rajesh, Priya, Aarav, Ananya, Kamla Devi',
    color: 'text-purple-400',
    bg: 'bg-purple-500/10',
  },
];

interface PersonNode {
  id: string;
  name: string;
  role: string;
  docs: number;
  color: string;
  cx: number;
  cy: number;
}

const PERSON_NODES: PersonNode[] = [
  { id: 'fm-001', name: 'Rajesh',  role: 'Head', docs: 7, color: '#D4AF37', cx: 200, cy: 195 },
  { id: 'fm-002', name: 'Priya',   role: 'Spouse', docs: 1, color: '#10B981', cx: 108, cy: 130 },
  { id: 'fm-003', name: 'Aarav',   role: 'Son', docs: 1, color: '#3B82F6', cx: 295, cy: 130 },
  { id: 'fm-004', name: 'Ananya',  role: 'Daughter', docs: 0, color: '#A78BFA', cx: 295, cy: 265 },
  { id: 'fm-005', name: 'Kamla',   role: 'Mother', docs: 1, color: '#F87171', cx: 108, cy: 265 },
];

const GRAPH_EDGES = [
  { from: 'fm-001', to: 'fm-002' },
  { from: 'fm-001', to: 'fm-003' },
  { from: 'fm-001', to: 'fm-004' },
  { from: 'fm-001', to: 'fm-005' },
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
      'Upload Priya\'s PAN card to vault',
      'Add as nominee on SBI savings account',
    ],
  },
  'fm-003': {
    docTypes: [
      { Icon: GraduationCap, label: 'CBSE Report Card', status: 'ok' },
    ],
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
    docTypes: [
      { Icon: Stethoscope, label: 'Medical Report', status: 'ok' },
    ],
    recommendations: [
      'Schedule diabetes follow-up (due Aug 2025)',
      'Check Ayushman Bharat PM-JAY eligibility',
      'Senior citizen health deduction under 80D',
    ],
  },
};

// ─── Component ───────────────────────────────────────────────────────────────

export function KnowledgeGraphLayout() {
  const [selected, setSelected] = useState<PersonNode | null>(null);

  const details = selected ? MEMBER_DETAILS[selected.id] : null;

  return (
    <div className="grid grid-cols-1 lg:grid-cols-[260px_1fr_280px] gap-6 h-full">

      {/* LEFT — Family Intelligence */}
      <motion.div
        initial={{ opacity: 0, x: -16 }}
        animate={{ opacity: 1, x: 0 }}
        transition={{ duration: 0.35 }}
        className="space-y-3 overflow-y-auto hide-scrollbar"
      >
        <h2 className="text-xs font-semibold uppercase tracking-wider text-nidhi-text-muted pt-1">
          Family Intelligence
        </h2>
        {FAMILY_INSIGHTS.map(({ id, icon: Icon, label, value, sub, color, bg }) => (
          <div key={id} className="card p-4 rounded-xl flex items-center gap-3">
            <div className={`w-9 h-9 rounded-lg ${bg} flex items-center justify-center flex-shrink-0`}>
              <Icon className={`w-4 h-4 ${color}`} />
            </div>
            <div className="flex-1 min-w-0">
              <p className="text-xs text-nidhi-text-muted leading-tight">{label}</p>
              <p className={`text-base font-bold ${color} leading-tight mt-0.5`}>{value}</p>
              <p className="text-[10px] text-nidhi-text-muted mt-0.5 leading-snug">{sub}</p>
            </div>
          </div>
        ))}
      </motion.div>

      {/* CENTER — Graph */}
      <motion.div
        initial={{ opacity: 0, scale: 0.97 }}
        animate={{ opacity: 1, scale: 1 }}
        transition={{ duration: 0.35, delay: 0.1 }}
        className="card rounded-2xl overflow-hidden relative flex items-center justify-center"
        style={{ minHeight: 360 }}
      >
        <svg
          viewBox="0 0 400 400"
          className="w-full h-full max-h-[480px]"
          style={{ filter: 'drop-shadow(0 0 12px rgba(212,175,55,0.08))' }}
        >
          <defs>
            <linearGradient id="edge-grad" x1="0%" y1="0%" x2="100%" y2="100%">
              <stop offset="0%" stopColor="rgba(212,175,55,0.25)" />
              <stop offset="100%" stopColor="rgba(212,175,55,0.08)" />
            </linearGradient>
          </defs>

          {/* Edges */}
          {GRAPH_EDGES.map((edge, i) => {
            const from = PERSON_NODES.find((n) => n.id === edge.from)!;
            const to = PERSON_NODES.find((n) => n.id === edge.to)!;
            return (
              <line
                key={i}
                x1={from.cx}
                y1={from.cy}
                x2={to.cx}
                y2={to.cy}
                stroke="url(#edge-grad)"
                strokeWidth="1.5"
              />
            );
          })}

          {/* Nodes */}
          {PERSON_NODES.map((node) => {
            const isSelected = selected?.id === node.id;
            return (
              <motion.g
                key={node.id}
                initial={{ opacity: 0, scale: 0.6 }}
                animate={{ opacity: 1, scale: 1 }}
                whileHover={{ scale: 1.12 }}
                transition={{ type: 'spring', stiffness: 300, damping: 20 }}
                onClick={() => setSelected(isSelected ? null : node)}
                style={{ cursor: 'pointer' }}
              >
                {/* Glow ring when selected */}
                {isSelected && (
                  <motion.circle
                    cx={node.cx}
                    cy={node.cy}
                    r={32}
                    fill="none"
                    stroke={node.color}
                    strokeWidth={2}
                    opacity={0.35}
                    animate={{ r: [28, 33, 28] }}
                    transition={{ duration: 1.8, repeat: Infinity }}
                  />
                )}

                {/* Node circle */}
                <circle
                  cx={node.cx}
                  cy={node.cy}
                  r={22}
                  fill={`${node.color}20`}
                  stroke={node.color}
                  strokeWidth={isSelected ? 2.5 : 1.5}
                />

                {/* Name */}
                <text
                  x={node.cx}
                  y={node.cy - 1}
                  textAnchor="middle"
                  dominantBaseline="middle"
                  fontSize="9.5"
                  fontWeight="600"
                  fill={node.color}
                  fontFamily="Outfit, sans-serif"
                >
                  {node.name}
                </text>

                {/* Doc count badge */}
                {node.docs > 0 && (
                  <>
                    <circle cx={node.cx + 16} cy={node.cy - 16} r={8} fill={node.color} opacity={0.9} />
                    <text
                      x={node.cx + 16}
                      y={node.cy - 16}
                      textAnchor="middle"
                      dominantBaseline="middle"
                      fontSize="8"
                      fontWeight="700"
                      fill="#0C0C0C"
                    >
                      {node.docs}
                    </text>
                  </>
                )}

                {/* Role label below */}
                <text
                  x={node.cx}
                  y={node.cy + 32}
                  textAnchor="middle"
                  dominantBaseline="middle"
                  fontSize="8"
                  fill="rgba(154,154,142,0.7)"
                >
                  {node.role}
                </text>
              </motion.g>
            );
          })}
        </svg>

        {!selected && (
          <div className="absolute bottom-4 left-0 right-0 flex justify-center pointer-events-none">
            <p className="text-[11px] text-nidhi-text-muted">Select a family member for details</p>
          </div>
        )}
      </motion.div>

      {/* RIGHT — Detail Panel */}
      <motion.div
        initial={{ opacity: 0, x: 16 }}
        animate={{ opacity: 1, x: 0 }}
        transition={{ duration: 0.35, delay: 0.15 }}
        className="space-y-4 overflow-y-auto hide-scrollbar"
      >
        {selected && details ? (
          <>
            {/* Member header */}
            <div className="flex items-center gap-3">
              <div
                className="w-10 h-10 rounded-xl flex items-center justify-center text-sm font-bold"
                style={{ backgroundColor: `${selected.color}20`, color: selected.color }}
              >
                {selected.name.charAt(0)}
              </div>
              <div>
                <h3 className="font-semibold text-nidhi-text">{selected.name} Sharma</h3>
                <p className="text-xs text-nidhi-text-muted">{selected.role} · {selected.docs} documents</p>
              </div>
            </div>

            {/* Document status */}
            {details.docTypes.length > 0 && (
              <div className="card p-4 rounded-xl space-y-3">
                <p className="text-xs font-semibold uppercase tracking-wider text-nidhi-text-muted">Documents</p>
                <div className="space-y-2">
                  {details.docTypes.map(({ Icon, label, status }, i) => (
                    <div key={i} className="flex items-center gap-2.5 text-xs">
                      <Icon className="w-3.5 h-3.5 text-nidhi-text-muted flex-shrink-0" />
                      <span className="flex-1 text-nidhi-text-secondary">{label}</span>
                      {status === 'ok' && <CheckCircle2 className="w-3.5 h-3.5 text-nidhi-success" />}
                      {status === 'expiring' && <Clock className="w-3.5 h-3.5 text-nidhi-warning" />}
                      {status === 'missing' && <AlertTriangle className="w-3.5 h-3.5 text-nidhi-danger" />}
                    </div>
                  ))}
                </div>
              </div>
            )}

            {/* Recommendations */}
            <div className="card p-4 rounded-xl space-y-3">
              <p className="text-xs font-semibold uppercase tracking-wider text-nidhi-text-muted">Recommendations</p>
              <div className="space-y-2">
                {details.recommendations.map((rec, i) => (
                  <motion.div
                    key={i}
                    initial={{ opacity: 0, x: -8 }}
                    animate={{ opacity: 1, x: 0 }}
                    transition={{ delay: i * 0.05 }}
                    className="flex items-start gap-2 text-xs text-nidhi-text-secondary cursor-pointer group"
                  >
                    <ChevronRight className="w-3 h-3 text-nidhi-gold flex-shrink-0 mt-0.5 group-hover:translate-x-0.5 transition-transform" />
                    <span className="group-hover:text-nidhi-text transition-colors">{rec}</span>
                  </motion.div>
                ))}
              </div>
            </div>
          </>
        ) : (
          <div className="flex flex-col items-center justify-center h-48 text-center gap-3 opacity-60">
            <Info className="w-8 h-8 text-nidhi-text-muted" />
            <p className="text-sm text-nidhi-text-muted">Select a node to view details</p>
          </div>
        )}
      </motion.div>
    </div>
  );
}
