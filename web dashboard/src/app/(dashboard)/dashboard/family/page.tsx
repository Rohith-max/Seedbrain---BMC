'use client';

import React, { useEffect, useState } from 'react';
import { motion } from 'framer-motion';
import { dataStore } from '@/lib/db/store';
import { FamilyMember } from '@/types';
import { UserPlus, Shield, User, UserCheck, Baby, ChevronRight, FileText, Phone, Calendar, Briefcase } from 'lucide-react';

const RELATIONSHIP_ICONS: Record<string, React.ElementType> = {
  self: User, spouse: UserCheck, son: Baby, daughter: Baby,
  father: User, mother: User, grandfather: User, grandmother: User, other: User,
};
const RELATIONSHIP_LABELS: Record<string, string> = {
  self: 'Family Head', spouse: 'Spouse', son: 'Son', daughter: 'Daughter',
  father: 'Father', mother: 'Mother', grandfather: 'Grandfather', grandmother: 'Grandmother', other: 'Member',
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
  useEffect(() => { setMembers(dataStore.getFamilyMembers()); }, []);

  const head = members.find(m => m.relationship === 'self');
  const others = members.filter(m => m.relationship !== 'self');

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
        <motion.button whileHover={{ scale: 1.03 }} whileTap={{ scale: 0.97 }} className="btn-primary" style={{ flexShrink: 0 }}>
          <UserPlus style={{ width: '16px', height: '16px' }} /> Add Member
        </motion.button>
      </motion.div>

      {/* Family head card */}
      {head && (
        <motion.div initial={{ opacity: 0, y: 12 }} animate={{ opacity: 1, y: 0 }} transition={{ delay: 0.05 }}
          style={{ position: 'relative', overflow: 'hidden', borderRadius: '16px', border: '1px solid rgba(212,175,55,0.2)', background: 'var(--color-nidhi-card)', padding: '32px' }}>
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
                {head.panNumber && <div><p style={{ fontSize: '10px', textTransform: 'uppercase', letterSpacing: '0.08em', color: 'var(--color-nidhi-text-muted)', marginBottom: '4px' }}>PAN</p><p style={{ fontFamily: 'monospace', fontWeight: 600, color: 'var(--color-nidhi-text)', fontSize: '14px' }}>{head.panNumber}</p></div>}
                {head.aadhaarLast4 && <div><p style={{ fontSize: '10px', textTransform: 'uppercase', letterSpacing: '0.08em', color: 'var(--color-nidhi-text-muted)', marginBottom: '4px' }}>Aadhaar</p><p style={{ fontFamily: 'monospace', fontWeight: 600, color: 'var(--color-nidhi-text)', fontSize: '14px' }}>XXXX XXXX {head.aadhaarLast4}</p></div>}
                {head.phone && <div><p style={{ fontSize: '10px', textTransform: 'uppercase', letterSpacing: '0.08em', color: 'var(--color-nidhi-text-muted)', marginBottom: '4px' }}>Phone</p><p style={{ fontWeight: 600, color: 'var(--color-nidhi-text)', fontSize: '14px' }}>{head.phone}</p></div>}
                {head.annualIncome && <div><p style={{ fontSize: '10px', textTransform: 'uppercase', letterSpacing: '0.08em', color: 'var(--color-nidhi-text-muted)', marginBottom: '4px' }}>Annual Income</p><p style={{ fontWeight: 600, color: 'var(--color-nidhi-text)', fontSize: '14px' }}>₹{(head.annualIncome / 100000).toFixed(1)}L</p></div>}
              </div>
            </div>
          </div>
        </motion.div>
      )}

      {/* Members grid */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(2, 1fr)', gap: '16px' }}>
        {others.map((member, idx) => {
          const RelIcon = RELATIONSHIP_ICONS[member.relationship] ?? User;
          const age = member.dateOfBirth ? getAge(member.dateOfBirth) : null;
          const docs = dataStore.getDocuments().filter(d => d.familyMemberId === member.id);
          const label = RELATIONSHIP_LABELS[member.relationship] ?? 'Member';
          return (
            <motion.div key={member.id}
              initial={{ opacity: 0, y: 16 }} animate={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.35, delay: 0.1 + idx * 0.06 }}
              whileHover={{ y: -2 }}
              className="card-premium group"
              style={{ padding: '24px', cursor: 'pointer' }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: '16px', marginBottom: '20px' }}>
                <div style={{ width: '48px', height: '48px', borderRadius: '12px', background: 'var(--color-nidhi-elevated)', border: '1px solid var(--color-nidhi-border-subtle)', display: 'flex', alignItems: 'center', justifyContent: 'center', flexShrink: 0 }}>
                  <RelIcon style={{ width: '20px', height: '20px', color: 'var(--color-nidhi-text-muted)' }} />
                </div>
                <div style={{ flex: 1, minWidth: 0 }}>
                  <h3 style={{ fontSize: '16px', fontWeight: 600, color: 'var(--color-nidhi-text)', lineHeight: 1.3 }}>{member.name}</h3>
                  <p style={{ fontSize: '14px', color: 'var(--color-nidhi-text-muted)', marginTop: '2px' }}>{label}{age ? ` · ${age} yrs` : ''}</p>
                </div>
              </div>
              <div style={{ display: 'flex', flexDirection: 'column', gap: '10px', marginBottom: '20px' }}>
                {member.occupation && <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}><Briefcase style={{ width: '14px', height: '14px', color: 'var(--color-nidhi-text-muted)', flexShrink: 0 }} /><span style={{ fontSize: '14px', color: 'var(--color-nidhi-text-secondary)' }}>{member.occupation}</span></div>}
                {member.dateOfBirth && <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}><Calendar style={{ width: '14px', height: '14px', color: 'var(--color-nidhi-text-muted)', flexShrink: 0 }} /><span style={{ fontSize: '14px', color: 'var(--color-nidhi-text-secondary)' }}>{new Date(member.dateOfBirth).toLocaleDateString('en-IN', { day: 'numeric', month: 'long', year: 'numeric' })}</span></div>}
                {member.aadhaarLast4 && <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}><Shield style={{ width: '14px', height: '14px', color: 'var(--color-nidhi-text-muted)', flexShrink: 0 }} /><span style={{ fontSize: '14px', fontFamily: 'monospace', color: 'var(--color-nidhi-text-secondary)' }}>XXXX XXXX {member.aadhaarLast4}</span></div>}
                {member.phone && <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}><Phone style={{ width: '14px', height: '14px', color: 'var(--color-nidhi-text-muted)', flexShrink: 0 }} /><span style={{ fontSize: '14px', color: 'var(--color-nidhi-text-secondary)' }}>{member.phone}</span></div>}
              </div>
              <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', paddingTop: '16px', borderTop: '1px solid var(--color-nidhi-border-subtle)' }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: '6px', fontSize: '12px', color: 'var(--color-nidhi-text-muted)' }}>
                  <FileText style={{ width: '12px', height: '12px' }} />
                  <span>{docs.length} document{docs.length !== 1 ? 's' : ''}</span>
                </div>
                <span style={{ fontSize: '12px', fontWeight: 500, color: 'var(--color-nidhi-gold)', display: 'flex', alignItems: 'center', gap: '4px' }}>
                  View Profile <ChevronRight style={{ width: '12px', height: '12px' }} />
                </span>
              </div>
            </motion.div>
          );
        })}
      </div>
      <div style={{ height: '32px' }} />
    </div>
  );
}
