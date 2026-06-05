'use client';

import React, { useEffect, useState } from 'react';
import { motion } from 'framer-motion';
import { dataStore } from '@/lib/db/store';
import { FamilyMember } from '@/types';
import {
  UserPlus,
  Shield,
  User,
  Users,
  Baby,
  UserCheck,
  ChevronRight,
  FileText,
  Phone,
  Calendar,
  Briefcase,
} from 'lucide-react';

const RELATIONSHIP_ICONS: Record<string, React.ElementType> = {
  self:        User,
  spouse:      UserCheck,
  son:         Baby,
  daughter:    Baby,
  father:      User,
  mother:      User,
  grandfather: User,
  grandmother: User,
  other:       User,
};

const RELATIONSHIP_LABELS: Record<string, string> = {
  self:        'Family Head',
  spouse:      'Spouse',
  son:         'Son',
  daughter:    'Daughter',
  father:      'Father',
  mother:      'Mother',
  grandfather: 'Grandfather',
  grandmother: 'Grandmother',
  other:       'Member',
};

function getAge(dob: string): number {
  const birth = new Date(dob);
  const today = new Date();
  let age = today.getFullYear() - birth.getFullYear();
  const m = today.getMonth() - birth.getMonth();
  if (m < 0 || (m === 0 && today.getDate() < birth.getDate())) age--;
  return age;
}

export default function FamilyPage() {
  const [members, setMembers] = useState<FamilyMember[]>([]);

  useEffect(() => {
    setMembers(dataStore.getFamilyMembers());
  }, []);

  const head = members.find((m) => m.relationship === 'self');
  const others = members.filter((m) => m.relationship !== 'self');

  return (
    <div className="min-h-screen px-8 py-10 max-w-4xl mx-auto space-y-10">

      {/* Header */}
      <motion.div
        initial={{ opacity: 0, y: -12 }}
        animate={{ opacity: 1, y: 0 }}
        className="flex items-end justify-between gap-4"
      >
        <div className="space-y-1">
          <p className="section-label">Sharma Household</p>
          <h1 className="text-4xl font-display font-bold text-nidhi-text">Family</h1>
          <p className="text-nidhi-text-secondary">
            {members.length} members · Bengaluru, Karnataka
          </p>
        </div>
        <motion.button
          whileHover={{ scale: 1.03 }}
          whileTap={{ scale: 0.97 }}
          className="btn-primary flex-shrink-0"
        >
          <UserPlus className="w-4 h-4" />
          Add Member
        </motion.button>
      </motion.div>

      {/* Family head — prominent card */}
      {head && (
        <motion.div
          initial={{ opacity: 0, y: 12 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.05 }}
          className="relative overflow-hidden rounded-2xl border border-nidhi-gold/20 bg-nidhi-card p-8"
        >
          <div className="absolute -top-20 -right-20 w-48 h-48 rounded-full bg-nidhi-gold/5 blur-3xl pointer-events-none" />
          <div className="relative z-10 flex items-start gap-6">
            {/* Avatar */}
            <div className="w-16 h-16 rounded-2xl gradient-gold flex items-center justify-center text-2xl font-bold text-nidhi-black font-display flex-shrink-0">
              {head.name.charAt(0)}
            </div>

            {/* Info */}
            <div className="flex-1 min-w-0">
              <div className="flex items-center gap-2 mb-1 flex-wrap">
                <h2 className="text-xl font-display font-bold text-nidhi-text">{head.name}</h2>
                <span className="flex items-center gap-1 text-[10px] font-bold uppercase tracking-wider px-2 py-0.5 rounded-md bg-nidhi-gold/12 text-nidhi-gold border border-nidhi-gold/20">
                  <Shield className="w-2.5 h-2.5" />
                  Head of Family
                </span>
              </div>

              <p className="text-sm text-nidhi-text-secondary mb-5">
                {head.occupation} &middot; {head.dateOfBirth ? `${getAge(head.dateOfBirth)} years` : ''}
              </p>

              <div className="grid grid-cols-2 sm:grid-cols-4 gap-4 text-sm">
                {head.panNumber && (
                  <div>
                    <p className="text-[10px] uppercase tracking-wider text-nidhi-text-muted mb-0.5">PAN</p>
                    <p className="font-mono font-semibold text-nidhi-text">{head.panNumber}</p>
                  </div>
                )}
                {head.aadhaarLast4 && (
                  <div>
                    <p className="text-[10px] uppercase tracking-wider text-nidhi-text-muted mb-0.5">Aadhaar</p>
                    <p className="font-mono font-semibold text-nidhi-text">XXXX XXXX {head.aadhaarLast4}</p>
                  </div>
                )}
                {head.phone && (
                  <div>
                    <p className="text-[10px] uppercase tracking-wider text-nidhi-text-muted mb-0.5">Phone</p>
                    <p className="font-semibold text-nidhi-text">{head.phone}</p>
                  </div>
                )}
                {head.annualIncome && (
                  <div>
                    <p className="text-[10px] uppercase tracking-wider text-nidhi-text-muted mb-0.5">Annual Income</p>
                    <p className="font-semibold text-nidhi-text">
                      ₹{(head.annualIncome / 100000).toFixed(1)}L
                    </p>
                  </div>
                )}
              </div>
            </div>
          </div>
        </motion.div>
      )}

      {/* Other members grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        {others.map((member, idx) => {
          const RelIcon = RELATIONSHIP_ICONS[member.relationship] ?? User;
          const age = member.dateOfBirth ? getAge(member.dateOfBirth) : null;
          const docs = dataStore.getDocuments().filter((d) => d.familyMemberId === member.id);
          const label = RELATIONSHIP_LABELS[member.relationship] ?? 'Member';

          return (
            <motion.div
              key={member.id}
              initial={{ opacity: 0, y: 16 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.35, delay: 0.1 + idx * 0.06 }}
              whileHover={{ y: -2 }}
              className="group card-premium p-6 cursor-pointer"
            >
              {/* Top row */}
              <div className="flex items-center gap-4 mb-5">
                <div className="w-12 h-12 rounded-xl bg-nidhi-elevated border border-nidhi-border-subtle flex items-center justify-center flex-shrink-0 group-hover:border-nidhi-border transition-colors">
                  <RelIcon className="w-5 h-5 text-nidhi-text-muted group-hover:text-nidhi-gold transition-colors" />
                </div>
                <div className="flex-1 min-w-0">
                  <h3 className="text-base font-semibold text-nidhi-text leading-tight">{member.name}</h3>
                  <p className="text-sm text-nidhi-text-muted">
                    {label}{age ? ` · ${age} yrs` : ''}
                  </p>
                </div>
              </div>

              {/* Detail rows */}
              <div className="space-y-2 mb-5">
                {member.occupation && (
                  <div className="flex items-center gap-2.5 text-sm">
                    <Briefcase className="w-3.5 h-3.5 text-nidhi-text-muted flex-shrink-0" />
                    <span className="text-nidhi-text-secondary">{member.occupation}</span>
                  </div>
                )}
                {member.dateOfBirth && (
                  <div className="flex items-center gap-2.5 text-sm">
                    <Calendar className="w-3.5 h-3.5 text-nidhi-text-muted flex-shrink-0" />
                    <span className="text-nidhi-text-secondary">
                      {new Date(member.dateOfBirth).toLocaleDateString('en-IN', {
                        day: 'numeric',
                        month: 'long',
                        year: 'numeric',
                      })}
                    </span>
                  </div>
                )}
                {member.aadhaarLast4 && (
                  <div className="flex items-center gap-2.5 text-sm">
                    <Shield className="w-3.5 h-3.5 text-nidhi-text-muted flex-shrink-0" />
                    <span className="font-mono text-nidhi-text-secondary">XXXX XXXX {member.aadhaarLast4}</span>
                  </div>
                )}
                {member.phone && (
                  <div className="flex items-center gap-2.5 text-sm">
                    <Phone className="w-3.5 h-3.5 text-nidhi-text-muted flex-shrink-0" />
                    <span className="text-nidhi-text-secondary">{member.phone}</span>
                  </div>
                )}
              </div>

              {/* Footer */}
              <div className="flex items-center justify-between pt-4 border-t border-nidhi-border-subtle">
                <div className="flex items-center gap-1.5 text-xs text-nidhi-text-muted">
                  <FileText className="w-3 h-3" />
                  <span>{docs.length} document{docs.length !== 1 ? 's' : ''}</span>
                </div>
                <span className="text-xs font-medium text-nidhi-gold group-hover:text-nidhi-gold-light transition-colors flex items-center gap-1">
                  View Profile <ChevronRight className="w-3 h-3" />
                </span>
              </div>
            </motion.div>
          );
        })}
      </div>

      <div className="h-8" />
    </div>
  );
}
