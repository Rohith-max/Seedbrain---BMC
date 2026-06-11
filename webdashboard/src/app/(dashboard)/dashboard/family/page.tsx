'use client';

import React, { useEffect, useState } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { dataStore } from '@/lib/db/store';
import { FamilyMember, FamilyRelationship } from '@/types';
import {
  UserPlus, Shield, User, UserCheck, Baby, ChevronRight,
  FileText, Phone, Calendar, Briefcase, BarChart2, ChevronDown,
  X, CheckCircle2, AlertCircle, Clock, Mail, CreditCard,
  Banknote, ArrowLeft, ShieldCheck, AlertTriangle,
} from 'lucide-react';
import {
  BarChart, Bar, XAxis, YAxis, Tooltip, Cell, ResponsiveContainer,
  RadarChart, Radar, PolarGrid, PolarAngleAxis, PieChart, Pie,
} from 'recharts';
import { getCategoryColor } from '@/lib/utils';
import { CategoryIcon } from '@/components/ui/category-icon';
import { formatDate } from '@/lib/utils';

// ─── Helpers ──────────────────────────────────────────────────────────────────

const RELATIONSHIP_ICONS: Record<string, React.ElementType> = {
  self: User, spouse: UserCheck, son: Baby, daughter: Baby,
  father: User, mother: User, grandfather: User, grandmother: User,
  brother: User, sister: User, uncle: User, aunt: User, other: User,
};
const RELATIONSHIP_LABELS: Record<string, string> = {
  self: 'Family Head', spouse: 'Spouse', son: 'Son', daughter: 'Daughter',
  father: 'Father', mother: 'Mother', grandfather: 'Grandfather',
  grandmother: 'Grandmother', brother: 'Brother', sister: 'Sister',
  uncle: 'Uncle', aunt: 'Aunt', other: 'Member',
};

const ALL_RELATIONSHIPS: FamilyRelationship[] = [
  'spouse','son','daughter','father','mother',
  'grandfather','grandmother','brother','sister','uncle','aunt','other',
];

const MEMBER_COLORS: Record<string, string> = {
  'fm-001': '#D4AF37', 'fm-002': '#10B981',
  'fm-003': '#3B82F6', 'fm-004': '#A78BFA', 'fm-005': '#F87171',
};
const FALLBACK_COLORS = ['#60A5FA','#34D399','#F472B6','#FB923C','#A78BFA','#FBBF24'];

function getColor(id: string, idx: number) {
  return MEMBER_COLORS[id] ?? FALLBACK_COLORS[idx % FALLBACK_COLORS.length];
}

function getAge(dob: string): number {
  const birth = new Date(dob);
  const today = new Date();
  let age = today.getFullYear() - birth.getFullYear();
  const m = today.getMonth() - birth.getMonth();
  if (m < 0 || (m === 0 && today.getDate() < birth.getDate())) age--;
  return age;
}

// ─── Tooltip ──────────────────────────────────────────────────────────────────

const ChartTooltip = ({ active, payload, label }: any) => {
  if (!active || !payload?.length) return null;
  return (
    <div className="bg-nidhi-card border border-nidhi-border-subtle rounded-xl px-3 py-2 text-xs shadow-xl">
      {label && <p className="text-nidhi-text-muted mb-1">{label}</p>}
      {payload.map((p: any, i: number) => (
        <p key={i} style={{ color: p.fill || p.color }} className="font-semibold">
          {p.name ? `${p.name}: ` : ''}
          {typeof p.value === 'number' && p.value > 9999
            ? `₹${(p.value / 100000).toFixed(1)}L`
            : p.value}
        </p>
      ))}
    </div>
  );
};

// ─── Add Member Modal ─────────────────────────────────────────────────────────

function AddMemberModal({ onClose, onAdd }: { onClose: () => void; onAdd: (m: FamilyMember) => void }) {
  const [form, setForm] = useState({
    name: '', relationship: 'spouse' as FamilyRelationship,
    dateOfBirth: '', gender: 'male' as 'male' | 'female' | 'other',
    phone: '', email: '', occupation: '', annualIncome: '',
    aadhaarLast4: '', panNumber: '',
  });
  const [errors, setErrors] = useState<Record<string, string>>({});
  const [saving, setSaving] = useState(false);

  const validate = () => {
    const e: Record<string, string> = {};
    if (!form.name.trim()) e.name = 'Name is required';
    if (!form.dateOfBirth) e.dateOfBirth = 'Date of birth is required';
    if (form.aadhaarLast4 && !/^\d{4}$/.test(form.aadhaarLast4)) e.aadhaarLast4 = 'Enter last 4 digits only';
    if (form.panNumber && !/^[A-Z]{5}[0-9]{4}[A-Z]$/.test(form.panNumber)) e.panNumber = 'Invalid PAN format (e.g. ABCDE1234F)';
    if (form.phone && !/^\+?[\d\s-]{10,}$/.test(form.phone)) e.phone = 'Invalid phone number';
    return e;
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    const errs = validate();
    if (Object.keys(errs).length) { setErrors(errs); return; }
    setSaving(true);
    setTimeout(() => {
      const newMember: FamilyMember = {
        id: `fm-${Date.now()}`,
        userId: 'user-001',
        name: form.name.trim(),
        relationship: form.relationship,
        dateOfBirth: form.dateOfBirth,
        gender: form.gender,
        phone: form.phone || undefined,
        email: form.email || undefined,
        occupation: form.occupation || undefined,
        annualIncome: form.annualIncome ? parseInt(form.annualIncome) * 100000 : undefined,
        aadhaarLast4: form.aadhaarLast4 || undefined,
        panNumber: form.panNumber || undefined,
        isActive: true,
        createdAt: new Date().toISOString(),
      };
      dataStore.addFamilyMember(newMember);
      onAdd(newMember);
      setSaving(false);
    }, 800);
  };

  const set = (key: string, value: string) => {
    setForm(f => ({ ...f, [key]: value }));
    setErrors(e => { const n = { ...e }; delete n[key]; return n; });
  };

  return (
    <div className="modal-overlay hide-scrollbar" onClick={e => e.target === e.currentTarget && onClose()}>
      <motion.div
        initial={{ opacity: 0, scale: 0.95, y: 16 }}
        animate={{ opacity: 1, scale: 1, y: 0 }}
        exit={{ opacity: 0, scale: 0.95, y: 16 }}
        transition={{ duration: 0.25 }}
        className="modal-box w-full max-w-lg max-h-[90vh] overflow-y-auto hide-scrollbar"
      >
        {/* Header */}
        <div className="flex items-center justify-between px-6 py-5 border-b border-nidhi-border-subtle sticky top-0 bg-nidhi-card z-10">
          <div>
            <h2 className="text-base font-semibold text-nidhi-text">Add Family Member</h2>
            <p className="text-xs text-nidhi-text-muted mt-0.5">Fill in the details to add a new member</p>
          </div>
          <button onClick={onClose} className="text-nidhi-text-muted hover:text-nidhi-text transition-colors p-1 rounded">
            <X className="w-5 h-5" />
          </button>
        </div>

        <form onSubmit={handleSubmit} className="p-6 space-y-5">
          {/* Name + Relationship */}
          <div className="grid grid-cols-2 gap-4">
            <div className="col-span-2">
              <label className="block text-xs font-medium text-nidhi-text-secondary mb-1.5">Full Name *</label>
              <input value={form.name} onChange={e => set('name', e.target.value)}
                placeholder="e.g. Sunita Sharma"
                className={`input-premium w-full ${errors.name ? 'border-red-500/60' : ''}`} />
              {errors.name && <p className="text-xs text-red-400 mt-1">{errors.name}</p>}
            </div>
            <div>
              <label className="block text-xs font-medium text-nidhi-text-secondary mb-1.5">Relationship *</label>
              <select value={form.relationship} onChange={e => set('relationship', e.target.value)}
                className="input-premium w-full">
                {ALL_RELATIONSHIPS.map(r => (
                  <option key={r} value={r}>{RELATIONSHIP_LABELS[r] ?? r}</option>
                ))}
              </select>
            </div>
            <div>
              <label className="block text-xs font-medium text-nidhi-text-secondary mb-1.5">Gender *</label>
              <select value={form.gender} onChange={e => set('gender', e.target.value)} className="input-premium w-full">
                <option value="male">Male</option>
                <option value="female">Female</option>
                <option value="other">Other</option>
              </select>
            </div>
          </div>

          {/* DOB + Occupation */}
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-xs font-medium text-nidhi-text-secondary mb-1.5">Date of Birth *</label>
              <input type="date" value={form.dateOfBirth} onChange={e => set('dateOfBirth', e.target.value)}
                max={new Date().toISOString().split('T')[0]}
                className={`input-premium w-full ${errors.dateOfBirth ? 'border-red-500/60' : ''}`} />
              {errors.dateOfBirth && <p className="text-xs text-red-400 mt-1">{errors.dateOfBirth}</p>}
            </div>
            <div>
              <label className="block text-xs font-medium text-nidhi-text-secondary mb-1.5">Occupation</label>
              <input value={form.occupation} onChange={e => set('occupation', e.target.value)}
                placeholder="e.g. Engineer, Student"
                className="input-premium w-full" />
            </div>
          </div>

          {/* Contact */}
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-xs font-medium text-nidhi-text-secondary mb-1.5">Phone</label>
              <input value={form.phone} onChange={e => set('phone', e.target.value)}
                placeholder="+91 98765 43210"
                className={`input-premium w-full ${errors.phone ? 'border-red-500/60' : ''}`} />
              {errors.phone && <p className="text-xs text-red-400 mt-1">{errors.phone}</p>}
            </div>
            <div>
              <label className="block text-xs font-medium text-nidhi-text-secondary mb-1.5">Email</label>
              <input type="email" value={form.email} onChange={e => set('email', e.target.value)}
                placeholder="name@email.com"
                className="input-premium w-full" />
            </div>
          </div>

          {/* Income */}
          <div>
            <label className="block text-xs font-medium text-nidhi-text-secondary mb-1.5">Annual Income (₹ Lakh)</label>
            <input type="number" min="0" step="0.1" value={form.annualIncome}
              onChange={e => set('annualIncome', e.target.value)}
              placeholder="e.g. 6 for ₹6 Lakh"
              className="input-premium w-full" />
          </div>

          {/* IDs */}
          <div className="p-4 rounded-xl border border-nidhi-border-subtle bg-nidhi-surface/50 space-y-4">
            <p className="text-xs font-semibold text-nidhi-text-muted uppercase tracking-wider">Identity Documents (optional)</p>
            <div className="grid grid-cols-2 gap-4">
              <div>
                <label className="block text-xs font-medium text-nidhi-text-secondary mb-1.5">Aadhaar Last 4</label>
                <input maxLength={4} value={form.aadhaarLast4} onChange={e => set('aadhaarLast4', e.target.value.replace(/\D/g, ''))}
                  placeholder="e.g. 4523"
                  className={`input-premium w-full font-mono ${errors.aadhaarLast4 ? 'border-red-500/60' : ''}`} />
                {errors.aadhaarLast4 && <p className="text-xs text-red-400 mt-1">{errors.aadhaarLast4}</p>}
              </div>
              <div>
                <label className="block text-xs font-medium text-nidhi-text-secondary mb-1.5">PAN Number</label>
                <input maxLength={10} value={form.panNumber} onChange={e => set('panNumber', e.target.value.toUpperCase())}
                  placeholder="e.g. ABCDE1234F"
                  className={`input-premium w-full font-mono tracking-wider ${errors.panNumber ? 'border-red-500/60' : ''}`} />
                {errors.panNumber && <p className="text-xs text-red-400 mt-1">{errors.panNumber}</p>}
              </div>
            </div>
          </div>

          {/* Submit */}
          <div className="flex gap-3 pt-2">
            <button type="button" onClick={onClose} className="btn-ghost flex-1">Cancel</button>
            <button type="submit" disabled={saving} className="btn-primary flex-1">
              {saving ? (
                <motion.span animate={{ opacity: [1, 0.5, 1] }} transition={{ duration: 0.8, repeat: Infinity }}>
                  Saving…
                </motion.span>
              ) : (
                <><UserPlus className="w-4 h-4" /> Add Member</>
              )}
            </button>
          </div>
        </form>
      </motion.div>
    </div>
  );
}

// ─── Member Profile Modal ─────────────────────────────────────────────────────

function MemberProfileModal({ member, idx, onClose }: {
  member: FamilyMember; idx: number; onClose: () => void;
}) {
  const color = getColor(member.id, idx);
  const docs = dataStore.getDocuments().filter(d => d.familyMemberId === member.id);
  const alerts = dataStore.getActiveAlerts().filter(a => a.familyMemberId === member.id);
  const benefits = dataStore.getBenefits().filter(b => b.matchedMembers.includes(member.id));
  const age = member.dateOfBirth ? getAge(member.dateOfBirth) : null;
  const label = RELATIONSHIP_LABELS[member.relationship] ?? 'Member';

  const priorityColor: Record<string, string> = {
    critical: 'text-red-400 bg-red-500/10 border-red-500/20',
    high: 'text-orange-400 bg-orange-500/10 border-orange-500/20',
    medium: 'text-yellow-400 bg-yellow-500/10 border-yellow-500/20',
    low: 'text-blue-400 bg-blue-500/10 border-blue-500/20',
  };

  return (
    <div className="modal-overlay" onClick={e => e.target === e.currentTarget && onClose()}>
      <motion.div
        initial={{ opacity: 0, x: 40 }}
        animate={{ opacity: 1, x: 0 }}
        exit={{ opacity: 0, x: 40 }}
        transition={{ type: 'spring', stiffness: 320, damping: 30 }}
        className="modal-box w-full max-w-2xl max-h-[92vh] overflow-y-auto hide-scrollbar"
      >
        {/* Hero header */}
        <div className="relative overflow-hidden px-6 pt-6 pb-5 border-b border-nidhi-border-subtle"
          style={{ background: `linear-gradient(135deg, ${color}08, transparent)` }}>
          <div className="absolute top-0 right-0 w-48 h-48 rounded-full blur-3xl pointer-events-none"
            style={{ backgroundColor: `${color}08` }} />
          <div className="flex items-start gap-4 relative z-10">
            <div className="w-16 h-16 rounded-2xl flex items-center justify-center text-2xl font-bold flex-shrink-0"
              style={{ background: `linear-gradient(135deg, ${color}30, ${color}15)`, color, border: `1.5px solid ${color}30` }}>
              {member.name.charAt(0)}
            </div>
            <div className="flex-1 min-w-0">
              <div className="flex items-center gap-2 flex-wrap mb-1">
                <h2 className="text-xl font-bold text-nidhi-text">{member.name}</h2>
                <span className="text-[10px] font-bold uppercase tracking-wider px-2 py-0.5 rounded-full border"
                  style={{ color, borderColor: `${color}30`, backgroundColor: `${color}12` }}>
                  {label}
                </span>
              </div>
              <p className="text-sm text-nidhi-text-secondary">
                {member.occupation ?? 'No occupation set'}
                {age !== null && ` · ${age} years old`}
              </p>
              <div className="flex items-center gap-4 mt-3 flex-wrap">
                <span className="text-xs text-nidhi-text-muted flex items-center gap-1">
                  <FileText className="w-3 h-3" /> {docs.length} documents
                </span>
                <span className="text-xs text-nidhi-text-muted flex items-center gap-1">
                  <AlertCircle className="w-3 h-3" /> {alerts.length} alerts
                </span>
                <span className="text-xs text-nidhi-text-muted flex items-center gap-1">
                  <Shield className="w-3 h-3" /> {benefits.length} benefits
                </span>
              </div>
            </div>
            <button onClick={onClose} className="text-nidhi-text-muted hover:text-nidhi-text transition-colors p-1 rounded flex-shrink-0">
              <X className="w-5 h-5" />
            </button>
          </div>
        </div>

        <div className="p-6 space-y-6">
          {/* Personal Details */}
          <div className="card p-5 rounded-2xl space-y-4">
            <p className="text-xs font-semibold uppercase tracking-widest text-nidhi-text-muted">Personal Details</p>
            <div className="grid grid-cols-2 gap-x-8 gap-y-3">
              {member.dateOfBirth && (
                <div className="flex items-center gap-2.5">
                  <Calendar className="w-4 h-4 text-nidhi-text-muted flex-shrink-0" />
                  <div>
                    <p className="text-[10px] text-nidhi-text-muted">Date of Birth</p>
                    <p className="text-sm font-medium text-nidhi-text">
                      {new Date(member.dateOfBirth).toLocaleDateString('en-IN', { day: 'numeric', month: 'long', year: 'numeric' })}
                    </p>
                  </div>
                </div>
              )}
              <div className="flex items-center gap-2.5">
                <User className="w-4 h-4 text-nidhi-text-muted flex-shrink-0" />
                <div>
                  <p className="text-[10px] text-nidhi-text-muted">Gender</p>
                  <p className="text-sm font-medium text-nidhi-text capitalize">{member.gender}</p>
                </div>
              </div>
              {member.phone && (
                <div className="flex items-center gap-2.5">
                  <Phone className="w-4 h-4 text-nidhi-text-muted flex-shrink-0" />
                  <div>
                    <p className="text-[10px] text-nidhi-text-muted">Phone</p>
                    <p className="text-sm font-medium text-nidhi-text">{member.phone}</p>
                  </div>
                </div>
              )}
              {member.email && (
                <div className="flex items-center gap-2.5">
                  <Mail className="w-4 h-4 text-nidhi-text-muted flex-shrink-0" />
                  <div>
                    <p className="text-[10px] text-nidhi-text-muted">Email</p>
                    <p className="text-sm font-medium text-nidhi-text truncate">{member.email}</p>
                  </div>
                </div>
              )}
              {member.occupation && (
                <div className="flex items-center gap-2.5">
                  <Briefcase className="w-4 h-4 text-nidhi-text-muted flex-shrink-0" />
                  <div>
                    <p className="text-[10px] text-nidhi-text-muted">Occupation</p>
                    <p className="text-sm font-medium text-nidhi-text">{member.occupation}</p>
                  </div>
                </div>
              )}
              {member.annualIncome && (
                <div className="flex items-center gap-2.5">
                  <Banknote className="w-4 h-4 text-nidhi-text-muted flex-shrink-0" />
                  <div>
                    <p className="text-[10px] text-nidhi-text-muted">Annual Income</p>
                    <p className="text-sm font-medium text-nidhi-text">₹{(member.annualIncome / 100000).toFixed(1)}L</p>
                  </div>
                </div>
              )}
            </div>

            {/* IDs */}
            {(member.aadhaarLast4 || member.panNumber) && (
              <div className="pt-3 border-t border-nidhi-border-subtle">
                <p className="text-[10px] font-semibold uppercase tracking-widest text-nidhi-text-muted mb-3">Identity</p>
                <div className="flex gap-4 flex-wrap">
                  {member.aadhaarLast4 && (
                    <div className="flex items-center gap-2 bg-nidhi-surface px-3 py-2 rounded-lg border border-nidhi-border-subtle">
                      <Shield className="w-3.5 h-3.5 text-nidhi-text-muted" />
                      <div>
                        <p className="text-[9px] text-nidhi-text-muted">Aadhaar</p>
                        <p className="font-mono text-xs text-nidhi-text">XXXX XXXX {member.aadhaarLast4}</p>
                      </div>
                    </div>
                  )}
                  {member.panNumber && (
                    <div className="flex items-center gap-2 bg-nidhi-surface px-3 py-2 rounded-lg border border-nidhi-border-subtle">
                      <CreditCard className="w-3.5 h-3.5 text-nidhi-text-muted" />
                      <div>
                        <p className="text-[9px] text-nidhi-text-muted">PAN</p>
                        <p className="font-mono text-xs text-nidhi-text tracking-widest">{member.panNumber}</p>
                      </div>
                    </div>
                  )}
                </div>
              </div>
            )}
          </div>

          {/* Documents */}
          <div className="space-y-3">
            <p className="text-xs font-semibold uppercase tracking-widest text-nidhi-text-muted">
              Documents ({docs.length})
            </p>
            {docs.length === 0 ? (
              <div className="card p-6 rounded-2xl flex flex-col items-center gap-2 opacity-60">
                <FileText className="w-8 h-8 text-nidhi-text-muted" />
                <p className="text-sm text-nidhi-text-muted">No documents in vault</p>
              </div>
            ) : (
              <div className="grid grid-cols-1 gap-3">
                {docs.map(doc => {
                  const isExpiring = doc.expiryDate && new Date(doc.expiryDate).getTime() - Date.now() < 90 * 24 * 60 * 60 * 1000;
                  const isExpired = doc.expiryDate && new Date(doc.expiryDate) < new Date();
                  return (
                    <div key={doc.id} className="card p-4 rounded-xl flex items-center gap-3">
                      <div className="w-9 h-9 rounded-lg flex items-center justify-center flex-shrink-0"
                        style={{ backgroundColor: `${getCategoryColor(doc.category)}18` }}>
                        <CategoryIcon category={doc.category} className="w-4 h-4"
                          style={{ color: getCategoryColor(doc.category) }} />
                      </div>
                      <div className="flex-1 min-w-0">
                        <p className="text-sm font-medium text-nidhi-text truncate">{doc.title}</p>
                        <p className="text-[11px] text-nidhi-text-muted">
                          {doc.issueDate ? `Issued ${formatDate(doc.issueDate)}` : doc.category}
                          {doc.expiryDate && ` · ${isExpired ? 'Expired' : `Expires ${formatDate(doc.expiryDate)}`}`}
                        </p>
                      </div>
                      {isExpired
                        ? <AlertCircle className="w-4 h-4 text-nidhi-danger flex-shrink-0" />
                        : isExpiring
                        ? <Clock className="w-4 h-4 text-nidhi-warning flex-shrink-0" />
                        : doc.isVerified
                        ? <ShieldCheck className="w-4 h-4 text-nidhi-success flex-shrink-0" />
                        : null}
                    </div>
                  );
                })}
              </div>
            )}
          </div>

          {/* Alerts */}
          {alerts.length > 0 && (
            <div className="space-y-3">
              <p className="text-xs font-semibold uppercase tracking-widest text-nidhi-text-muted">
                Active Alerts ({alerts.length})
              </p>
              <div className="space-y-2">
                {alerts.map(alert => (
                  <div key={alert.id} className="card p-4 rounded-xl flex items-start gap-3">
                    <div className={`mt-0.5 px-2 py-0.5 rounded-md text-[10px] font-bold uppercase border flex-shrink-0 ${priorityColor[alert.priority]}`}>
                      {alert.priority}
                    </div>
                    <div className="flex-1 min-w-0">
                      <p className="text-sm font-medium text-nidhi-text">{alert.title}</p>
                      <p className="text-[11px] text-nidhi-text-muted mt-0.5 leading-relaxed line-clamp-2">{alert.description}</p>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          )}

          {/* Benefits */}
          {benefits.length > 0 && (
            <div className="space-y-3">
              <p className="text-xs font-semibold uppercase tracking-widest text-nidhi-text-muted">
                Benefits / Schemes ({benefits.length})
              </p>
              <div className="space-y-2">
                {benefits.map(b => (
                  <div key={b.id} className="card p-4 rounded-xl space-y-2">
                    <div className="flex items-start justify-between gap-2">
                      <p className="text-sm font-medium text-nidhi-text">{b.title}</p>
                      <span className="text-xs font-bold text-nidhi-gold flex-shrink-0">{b.estimatedValue}</span>
                    </div>
                    <p className="text-[11px] text-nidhi-text-muted leading-relaxed">{b.description}</p>
                    <div className="flex items-center gap-2">
                      <div className="flex-1 h-1 bg-nidhi-border rounded-full overflow-hidden">
                        <div className="h-full bg-nidhi-gold rounded-full" style={{ width: `${b.matchScore * 100}%` }} />
                      </div>
                      <span className="text-[10px] text-nidhi-text-muted">{Math.round(b.matchScore * 100)}% match</span>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          )}
        </div>
      </motion.div>
    </div>
  );
}

// ─── Family Analytics Section ─────────────────────────────────────────────────

function FamilyAnalytics({ members }: { members: FamilyMember[] }) {
  const [open, setOpen] = useState(true);
  const docs = dataStore.getDocuments();

  const ageData = members.filter(m => m.dateOfBirth).map((m, i) => ({
    name: m.name.split(' ')[0], age: getAge(m.dateOfBirth!), fill: getColor(m.id, i),
  }));

  const docCountData = members.map((m, i) => ({
    name: m.name.split(' ')[0],
    docs: docs.filter(d => d.familyMemberId === m.id).length,
    fill: getColor(m.id, i),
  }));

  const incomeData = members.filter(m => m.annualIncome && m.annualIncome > 0).map((m, i) => ({
    name: m.name.split(' ')[0], value: m.annualIncome!, fill: getColor(m.id, i),
  }));

  const CATS = ['identity', 'financial', 'insurance', 'medical', 'education'];
  const radarData = CATS.map(cat => {
    const entry: Record<string, any> = { category: cat.charAt(0).toUpperCase() + cat.slice(1) };
    members.forEach(m => {
      entry[m.name.split(' ')[0]] = docs.filter(d => d.familyMemberId === m.id && d.category === cat).length;
    });
    return entry;
  });

  return (
    <div className="space-y-4">
      <button onClick={() => setOpen(v => !v)} className="w-full flex items-center justify-between group">
        <div className="flex items-center gap-2.5">
          <div className="w-7 h-7 rounded-lg bg-nidhi-gold/10 border border-nidhi-gold/20 flex items-center justify-center">
            <BarChart2 className="w-3.5 h-3.5 text-nidhi-gold" />
          </div>
          <div className="text-left">
            <p className="text-sm font-semibold text-nidhi-text">Family Analytics</p>
            <p className="text-[11px] text-nidhi-text-muted">Age, documents, and income breakdown</p>
          </div>
        </div>
        <ChevronDown className={`w-4 h-4 text-nidhi-text-muted transition-transform duration-300 ${open ? 'rotate-180' : ''}`} />
      </button>

      <AnimatePresence initial={false}>
        {open && (
          <motion.div initial={{ height: 0, opacity: 0 }} animate={{ height: 'auto', opacity: 1 }}
            exit={{ height: 0, opacity: 0 }} transition={{ duration: 0.3, ease: 'easeInOut' }}
            className="overflow-hidden">
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4 pt-1">

              <div className="card p-5 rounded-2xl space-y-3">
                <div><p className="text-sm font-semibold text-nidhi-text">Age Distribution</p>
                  <p className="text-[11px] text-nidhi-text-muted">Years across {members.length} members</p></div>
                <ResponsiveContainer width="100%" height={240}>
                  <BarChart data={ageData} barSize={38}>
                    <XAxis dataKey="name" tick={{ fontSize: 11, fill: '#9A9A8E' }} axisLine={false} tickLine={false} />
                    <YAxis hide domain={[0, 85]} />
                    <Tooltip content={<ChartTooltip />} cursor={{ fill: 'rgba(255,255,255,0.03)' }} />
                    <Bar dataKey="age" radius={[6, 6, 0, 0]}>
                      {ageData.map((d, i) => <Cell key={i} fill={d.fill} fillOpacity={0.85} />)}
                    </Bar>
                  </BarChart>
                </ResponsiveContainer>
              </div>

              <div className="card p-5 rounded-2xl space-y-3">
                <div><p className="text-sm font-semibold text-nidhi-text">Documents per Member</p>
                  <p className="text-[11px] text-nidhi-text-muted">{docs.length} total across vault</p></div>
                <ResponsiveContainer width="100%" height={240}>
                  <BarChart data={docCountData} layout="vertical" barSize={24}>
                    <XAxis type="number" hide />
                    <YAxis type="category" dataKey="name" tick={{ fontSize: 11, fill: '#9A9A8E' }} width={52} axisLine={false} tickLine={false} />
                    <Tooltip content={<ChartTooltip />} cursor={{ fill: 'rgba(255,255,255,0.03)' }} />
                    <Bar dataKey="docs" radius={[0, 6, 6, 0]}>
                      {docCountData.map((d, i) => <Cell key={i} fill={d.fill} fillOpacity={0.85} />)}
                    </Bar>
                  </BarChart>
                </ResponsiveContainer>
              </div>

              <div className="card p-5 rounded-2xl space-y-3">
                <div><p className="text-sm font-semibold text-nidhi-text">Household Income Split</p>
                  <p className="text-[11px] text-nidhi-text-muted">Combined ₹{(incomeData.reduce((s, d) => s + d.value, 0) / 100000).toFixed(1)}L annual</p></div>
                <div className="flex items-center gap-4">
                  <ResponsiveContainer width="50%" height={200}>
                    <PieChart>
                      <Pie data={incomeData} dataKey="value" nameKey="name"
                        cx="50%" cy="50%" innerRadius={45} outerRadius={70} strokeWidth={0}>
                        {incomeData.map((d, i) => <Cell key={i} fill={d.fill} fillOpacity={0.85} />)}
                      </Pie>
                      <Tooltip content={<ChartTooltip />} />
                    </PieChart>
                  </ResponsiveContainer>
                  <div className="space-y-2 flex-1">
                    {incomeData.map((d, i) => (
                      <div key={i} className="flex items-center gap-2 text-xs">
                        <div className="w-2.5 h-2.5 rounded-full flex-shrink-0" style={{ backgroundColor: d.fill }} />
                        <span className="text-nidhi-text-secondary flex-1">{d.name}</span>
                        <span className="font-semibold text-nidhi-text">₹{(d.value / 100000).toFixed(1)}L</span>
                      </div>
                    ))}
                    <div className="pt-1 border-t border-nidhi-border-subtle">
                      <div className="flex justify-between text-xs">
                        <span className="text-nidhi-text-muted">Total</span>
                        <span className="font-bold text-nidhi-text">₹{(incomeData.reduce((s, d) => s + d.value, 0) / 100000).toFixed(1)}L</span>
                      </div>
                    </div>
                  </div>
                </div>
              </div>

              <div className="card p-5 rounded-2xl space-y-3">
                <div><p className="text-sm font-semibold text-nidhi-text">Document Coverage Radar</p>
                  <p className="text-[11px] text-nidhi-text-muted">Docs per category per member</p></div>
                <ResponsiveContainer width="100%" height={240}>
                  <RadarChart data={radarData}>
                    <PolarGrid stroke="rgba(255,255,255,0.06)" />
                    <PolarAngleAxis dataKey="category" tick={{ fontSize: 11, fill: '#9A9A8E' }} />
                    {members.map((m, i) => {
                      const c = getColor(m.id, i);
                      return (
                        <Radar key={m.id} name={m.name.split(' ')[0]} dataKey={m.name.split(' ')[0]}
                          stroke={c} fill={c} fillOpacity={0.12} strokeWidth={2} />
                      );
                    })}
                    <Tooltip content={<ChartTooltip />} />
                  </RadarChart>
                </ResponsiveContainer>
              </div>
            </div>
          </motion.div>
        )}
      </AnimatePresence>
    </div>
  );
}

// ─── Main Page ───────────────────────────────────────────────────────────────

export default function FamilyPage() {
  const [members, setMembers] = useState<FamilyMember[]>([]);
  const [showAdd, setShowAdd]    = useState(false);
  const [profile, setProfile]   = useState<{ member: FamilyMember; idx: number } | null>(null);

  useEffect(() => { setMembers(dataStore.getFamilyMembers()); }, []);

  const head   = members.find(m => m.relationship === 'self');
  const others = members.filter(m => m.relationship !== 'self');

  const handleAdd = (m: FamilyMember) => {
    setMembers(dataStore.getFamilyMembers());
    setShowAdd(false);
  };

  return (
    <div style={{ minHeight: '100vh', padding: '48px 40px', maxWidth: '900px', display: 'flex', flexDirection: 'column', gap: '40px' }}>

      {/* Header */}
      <motion.div initial={{ opacity: 0, y: -12 }} animate={{ opacity: 1, y: 0 }}
        style={{ display: 'flex', alignItems: 'flex-end', justifyContent: 'space-between', gap: '16px' }}>
        <div style={{ display: 'flex', flexDirection: 'column', gap: '6px' }}>
          <p className="section-label">Sharma Household</p>
          <h1 style={{ fontSize: '40px', fontFamily: 'var(--font-display)', fontWeight: 700, color: 'var(--color-nidhi-text)', lineHeight: 1.1 }}>Family</h1>
          <p style={{ fontSize: '16px', color: 'var(--color-nidhi-text-secondary)' }}>{members.length} members · Bengaluru, Karnataka</p>
        </div>
        <motion.button whileHover={{ scale: 1.03 }} whileTap={{ scale: 0.97 }}
          onClick={() => setShowAdd(true)}
          className="btn-primary" style={{ flexShrink: 0 }}>
          <UserPlus style={{ width: '16px', height: '16px' }} /> Add Member
        </motion.button>
      </motion.div>

      {/* Family head card */}
      {head && (
        <motion.div initial={{ opacity: 0, y: 12 }} animate={{ opacity: 1, y: 0 }} transition={{ delay: 0.05 }}
          onClick={() => setProfile({ member: head, idx: 0 })}
          style={{ position: 'relative', overflow: 'hidden', borderRadius: '16px', border: '1px solid rgba(212,175,55,0.2)', background: 'var(--color-nidhi-card)', padding: '32px', cursor: 'pointer' }}>
          <div style={{ position: 'absolute', top: '-80px', right: '-80px', width: '192px', height: '192px', borderRadius: '50%', background: 'rgba(212,175,55,0.05)', filter: 'blur(48px)', pointerEvents: 'none' }} />
          <div style={{ position: 'relative', zIndex: 1, display: 'flex', alignItems: 'flex-start', gap: '24px' }}>
            <div style={{ width: '64px', height: '64px', borderRadius: '16px', background: 'linear-gradient(135deg,#D4AF37,#E8D080)', display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: '24px', fontWeight: 700, color: '#0C0C0C', flexShrink: 0, fontFamily: 'var(--font-display)' }}>
              {head.name.charAt(0)}
            </div>
            <div style={{ flex: 1, minWidth: 0 }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: '8px', marginBottom: '6px', flexWrap: 'wrap' }}>
                <h2 style={{ fontSize: '20px', fontFamily: 'var(--font-display)', fontWeight: 700, color: 'var(--color-nidhi-text)' }}>{head.name}</h2>
                <span style={{ display: 'flex', alignItems: 'center', gap: '4px', fontSize: '10px', fontWeight: 700, textTransform: 'uppercase', letterSpacing: '0.08em', padding: '3px 8px', borderRadius: '6px', background: 'rgba(212,175,55,0.12)', color: 'var(--color-nidhi-gold)', border: '1px solid rgba(212,175,55,0.2)' }}>
                  <Shield style={{ width: '10px', height: '10px' }} /> Head of Family
                </span>
              </div>
              <p style={{ fontSize: '14px', color: 'var(--color-nidhi-text-secondary)', marginBottom: '20px' }}>
                {head.occupation} · {head.dateOfBirth ? `${getAge(head.dateOfBirth)} years` : ''}
              </p>
              <div style={{ display: 'grid', gridTemplateColumns: 'repeat(4, 1fr)', gap: '16px' }}>
                {head.panNumber    && <div><p style={{ fontSize: '10px', textTransform: 'uppercase', letterSpacing: '0.08em', color: 'var(--color-nidhi-text-muted)', marginBottom: '4px' }}>PAN</p><p style={{ fontFamily: 'monospace', fontWeight: 600, color: 'var(--color-nidhi-text)', fontSize: '14px' }}>{head.panNumber}</p></div>}
                {head.aadhaarLast4 && <div><p style={{ fontSize: '10px', textTransform: 'uppercase', letterSpacing: '0.08em', color: 'var(--color-nidhi-text-muted)', marginBottom: '4px' }}>Aadhaar</p><p style={{ fontFamily: 'monospace', fontWeight: 600, color: 'var(--color-nidhi-text)', fontSize: '14px' }}>XXXX XXXX {head.aadhaarLast4}</p></div>}
                {head.phone        && <div><p style={{ fontSize: '10px', textTransform: 'uppercase', letterSpacing: '0.08em', color: 'var(--color-nidhi-text-muted)', marginBottom: '4px' }}>Phone</p><p style={{ fontWeight: 600, color: 'var(--color-nidhi-text)', fontSize: '14px' }}>{head.phone}</p></div>}
                {head.annualIncome && <div><p style={{ fontSize: '10px', textTransform: 'uppercase', letterSpacing: '0.08em', color: 'var(--color-nidhi-text-muted)', marginBottom: '4px' }}>Annual Income</p><p style={{ fontWeight: 600, color: 'var(--color-nidhi-text)', fontSize: '14px' }}>₹{(head.annualIncome / 100000).toFixed(1)}L</p></div>}
              </div>
            </div>
            <span style={{ fontSize: '12px', fontWeight: 500, color: 'var(--color-nidhi-gold)', display: 'flex', alignItems: 'center', gap: '4px', flexShrink: 0 }}>
              View Profile <ChevronRight style={{ width: '12px', height: '12px' }} />
            </span>
          </div>
        </motion.div>
      )}

      {/* Members grid */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(2, 1fr)', gap: '16px' }}>
        {others.map((member, idx) => {
          const RelIcon = RELATIONSHIP_ICONS[member.relationship] ?? User;
          const age     = member.dateOfBirth ? getAge(member.dateOfBirth) : null;
          const docs    = dataStore.getDocuments().filter(d => d.familyMemberId === member.id);
          const label   = RELATIONSHIP_LABELS[member.relationship] ?? 'Member';
          const color   = getColor(member.id, idx + 1);
          return (
            <motion.div key={member.id}
              initial={{ opacity: 0, y: 16 }} animate={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.35, delay: 0.1 + idx * 0.06 }}
              whileHover={{ y: -2 }}
              className="card-premium group"
              style={{ padding: '24px', cursor: 'pointer' }}
              onClick={() => setProfile({ member, idx: idx + 1 })}
            >
              <div style={{ display: 'flex', alignItems: 'center', gap: '16px', marginBottom: '20px' }}>
                <div style={{ width: '48px', height: '48px', borderRadius: '12px', background: `${color}18`, border: `1px solid ${color}30`, display: 'flex', alignItems: 'center', justifyContent: 'center', flexShrink: 0 }}>
                  <RelIcon style={{ width: '20px', height: '20px', color }} />
                </div>
                <div style={{ flex: 1, minWidth: 0 }}>
                  <h3 style={{ fontSize: '16px', fontWeight: 600, color: 'var(--color-nidhi-text)', lineHeight: 1.3 }}>{member.name}</h3>
                  <p style={{ fontSize: '14px', color: 'var(--color-nidhi-text-muted)', marginTop: '2px' }}>{label}{age ? ` · ${age} yrs` : ''}</p>
                </div>
              </div>
              <div style={{ display: 'flex', flexDirection: 'column', gap: '10px', marginBottom: '20px' }}>
                {member.occupation   && <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}><Briefcase style={{ width: '14px', height: '14px', color: 'var(--color-nidhi-text-muted)', flexShrink: 0 }} /><span style={{ fontSize: '14px', color: 'var(--color-nidhi-text-secondary)' }}>{member.occupation}</span></div>}
                {member.dateOfBirth  && <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}><Calendar style={{ width: '14px', height: '14px', color: 'var(--color-nidhi-text-muted)', flexShrink: 0 }} /><span style={{ fontSize: '14px', color: 'var(--color-nidhi-text-secondary)' }}>{new Date(member.dateOfBirth).toLocaleDateString('en-IN', { day: 'numeric', month: 'long', year: 'numeric' })}</span></div>}
                {member.aadhaarLast4 && <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}><Shield style={{ width: '14px', height: '14px', color: 'var(--color-nidhi-text-muted)', flexShrink: 0 }} /><span style={{ fontSize: '14px', fontFamily: 'monospace', color: 'var(--color-nidhi-text-secondary)' }}>XXXX XXXX {member.aadhaarLast4}</span></div>}
                {member.phone        && <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}><Phone style={{ width: '14px', height: '14px', color: 'var(--color-nidhi-text-muted)', flexShrink: 0 }} /><span style={{ fontSize: '14px', color: 'var(--color-nidhi-text-secondary)' }}>{member.phone}</span></div>}
              </div>
              <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', paddingTop: '16px', borderTop: '1px solid var(--color-nidhi-border-subtle)' }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: '6px', fontSize: '12px', color: 'var(--color-nidhi-text-muted)' }}>
                  <FileText style={{ width: '12px', height: '12px' }} />
                  <span>{docs.length} document{docs.length !== 1 ? 's' : ''}</span>
                </div>
                <span style={{ fontSize: '12px', fontWeight: 500, color, display: 'flex', alignItems: 'center', gap: '4px' }}>
                  View Profile <ChevronRight style={{ width: '12px', height: '12px' }} />
                </span>
              </div>
            </motion.div>
          );
        })}
      </div>

      {/* Analytics */}
      <motion.div initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }} transition={{ delay: 0.35 }}>
        <FamilyAnalytics members={members} />
      </motion.div>

      <div style={{ height: '32px' }} />

      {/* Modals */}
      <AnimatePresence>
        {showAdd && <AddMemberModal onClose={() => setShowAdd(false)} onAdd={handleAdd} />}
      </AnimatePresence>
      <AnimatePresence>
        {profile && (
          <MemberProfileModal
            member={profile.member}
            idx={profile.idx}
            onClose={() => setProfile(null)}
          />
        )}
      </AnimatePresence>
    </div>
  );
}
