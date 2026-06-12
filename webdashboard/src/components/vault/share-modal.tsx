'use client';

import React, { useState, useEffect } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { dataStore } from '@/lib/db/store';
import { SharedLink } from '@/types';
import {
  X, Shield, Clock, Copy, CheckCircle2, Eye, Download, Printer,
  Link2, QrCode, Timer, AlertTriangle, Lock, Unlock,
} from 'lucide-react';

const DURATION_OPTIONS = [
  { label: '1 Hour', value: 1 * 60 * 60 * 1000 },
  { label: '2 Hours', value: 2 * 60 * 60 * 1000 },
  { label: '24 Hours', value: 24 * 60 * 60 * 1000 },
  { label: '7 Days', value: 7 * 24 * 60 * 60 * 1000 },
];

const PERM_OPTIONS: { label: string; value: SharedLink['permission']; Icon: React.ElementType; desc: string }[] = [
  { label: 'View Only', value: 'view', Icon: Eye, desc: 'Recipient can only view the document' },
  { label: 'Download', value: 'download', Icon: Download, desc: 'Recipient can download a copy' },
  { label: 'Print', value: 'print', Icon: Printer, desc: 'Recipient can view, download, and print' },
];

export function ShareModal({ documentId, documentTitle, onClose }: {
  documentId: string;
  documentTitle: string;
  onClose: () => void;
}) {
  const [recipientName, setRecipientName] = useState('');
  const [recipientEmail, setRecipientEmail] = useState('');
  const [permission, setPermission] = useState<SharedLink['permission']>('view');
  const [duration, setDuration] = useState(DURATION_OPTIONS[1].value);
  const [redactSensitive, setRedactSensitive] = useState(true);
  const [vaultLockdown, setVaultLockdown] = useState(true);
  const [generated, setGenerated] = useState(false);
  const [copied, setCopied] = useState(false);
  const [linkId, setLinkId] = useState('');

  const handleGenerate = () => {
    if (!recipientName.trim()) return;
    const id = `sl-${Date.now()}`;
    const link: SharedLink = {
      id,
      documentId,
      documentTitle,
      recipientName: recipientName.trim(),
      recipientEmail: recipientEmail.trim() || undefined,
      permission,
      expiresAt: new Date(Date.now() + duration).toISOString(),
      createdAt: new Date().toISOString(),
      isRevoked: false,
      redactSensitive,
      accessCount: 0,
    };
    dataStore.createSharedLink(link);
    setLinkId(id);
    setGenerated(true);
  };

  const fakeUrl = `https://nidhi.app/share/${linkId}`;

  const handleCopy = () => {
    navigator.clipboard?.writeText(fakeUrl);
    setCopied(true);
    setTimeout(() => setCopied(false), 2000);
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
          <div className="flex items-center gap-3">
            <div className="w-9 h-9 rounded-lg bg-nidhi-gold/10 flex items-center justify-center">
              <Shield className="w-4 h-4 text-nidhi-gold" />
            </div>
            <div>
              <h2 className="text-base font-semibold text-nidhi-text">Secure Share</h2>
              <p className="text-xs text-nidhi-text-muted mt-0.5 line-clamp-1">{documentTitle}</p>
            </div>
          </div>
          <button onClick={onClose} className="text-nidhi-text-muted hover:text-nidhi-text transition-colors p-1 rounded">
            <X className="w-5 h-5" />
          </button>
        </div>

        <div className="p-6 space-y-5">
          {!generated ? (
            <>
              {/* Recipient */}
              <div className="space-y-3">
                <label className="block text-xs font-medium text-nidhi-text-secondary">Recipient Name *</label>
                <input
                  value={recipientName}
                  onChange={e => setRecipientName(e.target.value)}
                  placeholder="e.g. Dr. Mehta, CA Suresh"
                  className="input-premium w-full"
                />
                <label className="block text-xs font-medium text-nidhi-text-secondary">Recipient Email</label>
                <input
                  type="email"
                  value={recipientEmail}
                  onChange={e => setRecipientEmail(e.target.value)}
                  placeholder="optional@email.com"
                  className="input-premium w-full"
                />
              </div>

              {/* Duration */}
              <div>
                <label className="block text-xs font-medium text-nidhi-text-secondary mb-2">
                  <Clock className="w-3 h-3 inline mr-1" />
                  Self-Destruct Timer
                </label>
                <div className="grid grid-cols-4 gap-2">
                  {DURATION_OPTIONS.map(opt => (
                    <button
                      key={opt.value}
                      onClick={() => setDuration(opt.value)}
                      className={`text-xs py-2.5 px-3 rounded-lg border transition-all font-medium ${
                        duration === opt.value
                          ? 'bg-nidhi-gold/10 border-nidhi-gold/30 text-nidhi-gold'
                          : 'bg-nidhi-surface border-nidhi-border-subtle text-nidhi-text-secondary hover:border-nidhi-border'
                      }`}
                    >
                      {opt.label}
                    </button>
                  ))}
                </div>
              </div>

              {/* Permission */}
              <div>
                <label className="block text-xs font-medium text-nidhi-text-secondary mb-2">Access Level</label>
                <div className="space-y-2">
                  {PERM_OPTIONS.map(opt => (
                    <button
                      key={opt.value}
                      onClick={() => setPermission(opt.value)}
                      className={`w-full flex items-center gap-3 p-3 rounded-xl border transition-all text-left ${
                        permission === opt.value
                          ? 'bg-nidhi-gold/8 border-nidhi-gold/25'
                          : 'bg-nidhi-surface border-nidhi-border-subtle hover:border-nidhi-border'
                      }`}
                    >
                      <opt.Icon className={`w-4 h-4 ${permission === opt.value ? 'text-nidhi-gold' : 'text-nidhi-text-muted'}`} />
                      <div>
                        <p className={`text-sm font-medium ${permission === opt.value ? 'text-nidhi-gold' : 'text-nidhi-text'}`}>{opt.label}</p>
                        <p className="text-[11px] text-nidhi-text-muted">{opt.desc}</p>
                      </div>
                    </button>
                  ))}
                </div>
              </div>

              {/* Security toggles */}
              <div className="space-y-3 p-4 rounded-xl border border-nidhi-border-subtle bg-nidhi-surface/50">
                <p className="text-xs font-semibold text-nidhi-text-muted uppercase tracking-wider">Security Options</p>

                {/* Redact toggle */}
                <div className="flex items-center justify-between">
                  <div className="flex items-center gap-2">
                    <AlertTriangle className="w-3.5 h-3.5 text-nidhi-warning" />
                    <div>
                      <p className="text-xs font-medium text-nidhi-text">Redact Sensitive Fields</p>
                      <p className="text-[10px] text-nidhi-text-muted">Mask Aadhaar, PAN numbers</p>
                    </div>
                  </div>
                  <button
                    onClick={() => setRedactSensitive(v => !v)}
                    className={`w-10 h-5 rounded-full transition-all relative ${redactSensitive ? 'bg-nidhi-gold' : 'bg-nidhi-border'}`}
                  >
                    <motion.div
                      animate={{ x: redactSensitive ? 20 : 2 }}
                      className="w-4 h-4 rounded-full bg-white absolute top-0.5"
                    />
                  </button>
                </div>

                {/* Vault lockdown toggle */}
                <div className="flex items-center justify-between">
                  <div className="flex items-center gap-2">
                    {vaultLockdown ? <Lock className="w-3.5 h-3.5 text-nidhi-gold" /> : <Unlock className="w-3.5 h-3.5 text-nidhi-text-muted" />}
                    <div>
                      <p className="text-xs font-medium text-nidhi-text">Vault Lockdown Mode</p>
                      <p className="text-[10px] text-nidhi-text-muted">Encrypted, auto-revoke on expiry</p>
                    </div>
                  </div>
                  <button
                    onClick={() => setVaultLockdown(v => !v)}
                    className={`w-10 h-5 rounded-full transition-all relative ${vaultLockdown ? 'bg-nidhi-gold' : 'bg-nidhi-border'}`}
                  >
                    <motion.div
                      animate={{ x: vaultLockdown ? 20 : 2 }}
                      className="w-4 h-4 rounded-full bg-white absolute top-0.5"
                    />
                  </button>
                </div>
              </div>

              {/* Generate button */}
              <button
                onClick={handleGenerate}
                disabled={!recipientName.trim()}
                className="btn-primary w-full text-sm disabled:opacity-40 disabled:cursor-not-allowed"
              >
                <Link2 className="w-4 h-4" />
                Generate Secure Link
              </button>
            </>
          ) : (
            /* ── Generated Link View ── */
            <motion.div
              initial={{ opacity: 0, y: 12 }}
              animate={{ opacity: 1, y: 0 }}
              className="space-y-5"
            >
              <div className="flex flex-col items-center gap-3 py-4">
                <motion.div
                  initial={{ scale: 0 }}
                  animate={{ scale: 1 }}
                  transition={{ type: 'spring', stiffness: 300 }}
                  className="w-14 h-14 rounded-2xl bg-nidhi-gold/10 flex items-center justify-center"
                >
                  <Shield className="w-7 h-7 text-nidhi-gold" />
                </motion.div>
                <p className="text-base font-bold text-nidhi-text">Secure Link Generated</p>
                <p className="text-xs text-nidhi-text-muted text-center">
                  This link will self-destruct after {DURATION_OPTIONS.find(d => d.value === duration)?.label?.toLowerCase()}.
                  {redactSensitive && ' Sensitive fields are redacted.'}
                </p>
              </div>

              {/* Link preview */}
              <div className="p-3 rounded-xl bg-nidhi-elevated border border-nidhi-border-subtle flex items-center gap-3">
                <Link2 className="w-4 h-4 text-nidhi-gold flex-shrink-0" />
                <span className="text-xs font-mono text-nidhi-text truncate flex-1">{fakeUrl}</span>
                <button onClick={handleCopy} className="flex-shrink-0">
                  {copied
                    ? <CheckCircle2 className="w-4 h-4 text-nidhi-success" />
                    : <Copy className="w-4 h-4 text-nidhi-text-muted hover:text-nidhi-gold transition-colors" />
                  }
                </button>
              </div>

              {/* QR Code placeholder */}
              <div className="flex justify-center">
                <div className="w-28 h-28 rounded-xl bg-white p-2 flex items-center justify-center">
                  <QrCode className="w-20 h-20 text-nidhi-black" />
                </div>
              </div>

              {/* Summary */}
              <div className="grid grid-cols-3 gap-3">
                <div className="p-3 rounded-xl bg-nidhi-surface border border-nidhi-border-subtle text-center">
                  <p className="text-[10px] text-nidhi-text-muted uppercase mb-1">Recipient</p>
                  <p className="text-xs font-semibold text-nidhi-text truncate">{recipientName}</p>
                </div>
                <div className="p-3 rounded-xl bg-nidhi-surface border border-nidhi-border-subtle text-center">
                  <p className="text-[10px] text-nidhi-text-muted uppercase mb-1">Access</p>
                  <p className="text-xs font-semibold text-nidhi-text capitalize">{permission}</p>
                </div>
                <div className="p-3 rounded-xl bg-nidhi-surface border border-nidhi-border-subtle text-center">
                  <p className="text-[10px] text-nidhi-text-muted uppercase mb-1">Expires</p>
                  <p className="text-xs font-semibold text-nidhi-gold">{DURATION_OPTIONS.find(d => d.value === duration)?.label}</p>
                </div>
              </div>

              <button onClick={onClose} className="btn-primary w-full text-sm">
                <CheckCircle2 className="w-4 h-4" /> Done
              </button>
            </motion.div>
          )}
        </div>
      </motion.div>
    </div>
  );
}

/* ── Active Shares List ── */
export function ActiveSharesList() {
  const [shares, setShares] = useState<SharedLink[]>([]);
  const [, setTick] = useState(0);

  useEffect(() => {
    setShares(dataStore.getActiveSharedLinks());
    const interval = setInterval(() => {
      setShares(dataStore.getActiveSharedLinks());
      setTick(t => t + 1);
    }, 1000);
    return () => clearInterval(interval);
  }, []);

  if (shares.length === 0) return null;

  return (
    <motion.div
      initial={{ opacity: 0, y: 12 }}
      animate={{ opacity: 1, y: 0 }}
      className="space-y-3"
    >
      <div className="flex items-center gap-2">
        <div className="w-6 h-6 rounded-md bg-nidhi-gold/10 flex items-center justify-center">
          <Lock className="w-3 h-3 text-nidhi-gold" />
        </div>
        <p className="text-sm font-semibold text-nidhi-text">Active Secure Shares</p>
        <span className="text-[10px] text-nidhi-text-muted">({shares.length})</span>
      </div>

      <div className="space-y-2">
        {shares.map(share => {
          const remaining = new Date(share.expiresAt).getTime() - Date.now();
          const hrs = Math.floor(remaining / 3600000);
          const mins = Math.floor((remaining % 3600000) / 60000);
          const secs = Math.floor((remaining % 60000) / 1000);
          const timeStr = hrs > 24
            ? `${Math.floor(hrs / 24)}d ${hrs % 24}h`
            : hrs > 0
            ? `${hrs}h ${mins}m`
            : `${mins}m ${secs}s`;
          const isUrgent = remaining < 3600000;

          return (
            <div
              key={share.id}
              className="card p-4 rounded-xl flex items-center gap-4"
            >
              <div className={`w-9 h-9 rounded-lg flex items-center justify-center flex-shrink-0 ${
                isUrgent ? 'bg-red-500/10' : 'bg-nidhi-gold/10'
              }`}>
                <Timer className={`w-4 h-4 ${isUrgent ? 'text-red-400' : 'text-nidhi-gold'}`} />
              </div>
              <div className="flex-1 min-w-0">
                <p className="text-sm font-medium text-nidhi-text truncate">{share.documentTitle}</p>
                <p className="text-[11px] text-nidhi-text-muted">
                  Shared with <strong>{share.recipientName}</strong> · {share.permission} · {share.accessCount} views
                </p>
              </div>
              <div className="text-right flex-shrink-0">
                <p className={`text-xs font-bold tabular-nums ${isUrgent ? 'text-red-400' : 'text-nidhi-gold'}`}>{timeStr}</p>
                <p className="text-[10px] text-nidhi-text-muted">remaining</p>
              </div>
              <button
                onClick={() => {
                  dataStore.revokeSharedLink(share.id);
                  setShares(dataStore.getActiveSharedLinks());
                }}
                className="text-xs text-red-400 hover:text-red-300 font-semibold transition-colors flex-shrink-0"
              >
                Revoke
              </button>
            </div>
          );
        })}
      </div>
    </motion.div>
  );
}
