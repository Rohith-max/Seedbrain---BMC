'use client';

import React, { useEffect, useState, useCallback } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { dataStore } from '@/lib/db/store';
import { getCategoryColor } from '@/lib/utils';
import { CategoryIcon } from '@/components/ui/category-icon';
import {
  Search,
  Upload,
  ShieldCheck,
  FileText,
  X,
  CheckCircle2,
  AlertCircle,
  Clock,
  Fingerprint,
  Banknote,
  ShieldCheck as InsuranceIcon,
  Stethoscope,
  Home,
  GraduationCap,
  Receipt,
  Car,
} from 'lucide-react';
import { formatDate } from '@/lib/utils';

const SECTIONS = [
  { key: 'identity',  label: 'Identity',   Icon: Fingerprint,   desc: 'Aadhaar, PAN, Passport, Voter ID' },
  { key: 'financial', label: 'Financial',  Icon: Banknote,      desc: 'Bank statements, LIC, investments' },
  { key: 'insurance', label: 'Insurance',  Icon: InsuranceIcon, desc: 'Life, health, vehicle insurance' },
  { key: 'medical',   label: 'Medical',    Icon: Stethoscope,   desc: 'Reports, prescriptions, records' },
  { key: 'property',  label: 'Property',   Icon: Home,          desc: 'Deeds, tax receipts, agreements' },
  { key: 'education', label: 'Education',  Icon: GraduationCap, desc: 'Certificates, marksheets, degrees' },
  { key: 'tax',       label: 'Tax',        Icon: Receipt,       desc: 'ITR, Form 16, TDS certificates' },
  { key: 'vehicle',   label: 'Vehicle',    Icon: Car,           desc: 'RC, insurance, pollution cert' },
];

// ─── OCR Upload Simulation ──────────────────────────────────────────────────

type UploadStage =
  | 'idle'
  | 'uploading'
  | 'ocr'
  | 'extracting'
  | 'classifying'
  | 'complete';

const STAGE_LABELS: Record<UploadStage, string> = {
  idle:        '',
  uploading:   'Uploading document...',
  ocr:         'Running OCR extraction...',
  extracting:  'Extracting metadata fields...',
  classifying: 'Classifying document type...',
  complete:    'Document processed successfully',
};

const STAGE_ORDER: UploadStage[] = [
  'uploading',
  'ocr',
  'extracting',
  'classifying',
  'complete',
];

const MOCK_OCR_RESULT = {
  title: 'Aadhaar Card — Ananya Sharma',
  category: 'identity',
  documentNumber: 'XXXX-XXXX-2481',
  issuingAuthority: 'UIDAI',
  issueDate: '2021-03-10',
  confidence: 0.96,
  extractedFields: [
    { label: 'Name', value: 'Ananya Sharma' },
    { label: 'DOB', value: '18 April 2012' },
    { label: 'Gender', value: 'Female' },
    { label: 'Address', value: '42 Maple Heights, Whitefield, Bengaluru 560066' },
  ],
  aiSummary:
    'Aadhaar card for Ananya Sharma, female, DOB 18 April 2012. Residential address in Bengaluru. Card is active and valid for all government purposes.',
};

function UploadModal({ onClose }: { onClose: () => void }) {
  const [stage, setStage] = useState<UploadStage>('idle');
  const [stageIndex, setStageIndex] = useState(-1);
  const [progress, setProgress] = useState(0);
  const [fileName, setFileName] = useState('');
  const [dragging, setDragging] = useState(false);

  const startSimulation = useCallback((name: string) => {
    setFileName(name);
    let idx = 0;
    setStage(STAGE_ORDER[0]);
    setStageIndex(0);
    setProgress(0);

    const advance = () => {
      idx++;
      if (idx < STAGE_ORDER.length) {
        setStage(STAGE_ORDER[idx]);
        setStageIndex(idx);
        setProgress(Math.round((idx / (STAGE_ORDER.length - 1)) * 100));
        const delay = idx === STAGE_ORDER.length - 1 ? 1200 : 900 + Math.random() * 400;
        setTimeout(advance, delay);
      }
    };
    setTimeout(advance, 1100);
  }, []);

  const handleFile = (file: File) => {
    if (stage !== 'idle') return;
    startSimulation(file.name);
  };

  const handleDrop = (e: React.DragEvent) => {
    e.preventDefault();
    setDragging(false);
    const file = e.dataTransfer.files[0];
    if (file) handleFile(file);
  };

  const isComplete = stage === 'complete';

  return (
    <div className="modal-overlay" onClick={(e) => e.target === e.currentTarget && onClose()}>
      <motion.div
        initial={{ opacity: 0, scale: 0.95, y: 16 }}
        animate={{ opacity: 1, scale: 1, y: 0 }}
        exit={{ opacity: 0, scale: 0.95, y: 16 }}
        transition={{ duration: 0.25 }}
        className="modal-box w-full max-w-lg"
      >
        {/* Header */}
        <div className="flex items-center justify-between px-6 py-5 border-b border-nidhi-border-subtle">
          <div>
            <h2 className="text-base font-semibold text-nidhi-text">Upload Document</h2>
            <p className="text-xs text-nidhi-text-muted mt-0.5">AI will extract and classify automatically</p>
          </div>
          <button onClick={onClose} className="text-nidhi-text-muted hover:text-nidhi-text transition-colors p-1 rounded">
            <X className="w-5 h-5" />
          </button>
        </div>

        <div className="p-6 space-y-6">
          {/* Drop Zone */}
          {stage === 'idle' && (
            <div
              onDragOver={(e) => { e.preventDefault(); setDragging(true); }}
              onDragLeave={() => setDragging(false)}
              onDrop={handleDrop}
              className={`upload-zone ${dragging ? 'drag-active' : ''}`}
            >
              <div className="flex flex-col items-center gap-3">
                <div className="w-12 h-12 rounded-xl bg-nidhi-elevated flex items-center justify-center border border-nidhi-border">
                  <Upload className="w-5 h-5 text-nidhi-text-muted" />
                </div>
                <div>
                  <p className="text-sm font-medium text-nidhi-text text-center">Drop your document here</p>
                  <p className="text-xs text-nidhi-text-muted text-center mt-1">PDF, JPG, PNG — up to 20 MB</p>
                </div>
                <label className="btn-ghost cursor-pointer text-sm px-4 py-2">
                  Browse Files
                  <input
                    type="file"
                    className="hidden"
                    accept=".pdf,.jpg,.jpeg,.png"
                    onChange={(e) => { const f = e.target.files?.[0]; if (f) handleFile(f); }}
                  />
                </label>
                {/* Quick test */}
                <button
                  onClick={() => startSimulation('Aadhaar_Ananya.pdf')}
                  className="text-xs text-nidhi-gold hover:text-nidhi-gold-light underline underline-offset-2 transition-colors"
                >
                  Try demo upload
                </button>
              </div>
            </div>
          )}

          {/* Progress */}
          {stage !== 'idle' && (
            <div className="space-y-4">
              {/* File name */}
              <div className="flex items-center gap-3 p-3 rounded-xl bg-nidhi-elevated border border-nidhi-border-subtle">
                <FileText className="w-5 h-5 text-nidhi-gold flex-shrink-0" />
                <span className="text-sm text-nidhi-text truncate flex-1">{fileName}</span>
                {isComplete && <CheckCircle2 className="w-4 h-4 text-nidhi-success flex-shrink-0" />}
              </div>

              {/* Progress bar */}
              <div className="space-y-2">
                <div className="flex items-center justify-between text-xs">
                  <span className={`font-medium ${isComplete ? 'text-nidhi-success' : 'text-nidhi-text-secondary'}`}>
                    {STAGE_LABELS[stage]}
                  </span>
                  <span className="text-nidhi-text-muted tabular-nums">{progress}%</span>
                </div>
                <div className="h-1.5 rounded-full bg-nidhi-border overflow-hidden">
                  <motion.div
                    className={`h-full rounded-full ${isComplete ? 'bg-nidhi-success' : 'bg-gradient-to-r from-nidhi-gold to-nidhi-gold-light'}`}
                    initial={{ width: 0 }}
                    animate={{ width: `${progress}%` }}
                    transition={{ duration: 0.5, ease: 'easeOut' }}
                  />
                </div>
              </div>

              {/* Stage steps */}
              <div className="space-y-2">
                {STAGE_ORDER.slice(0, -1).map((s, i) => {
                  const done = stageIndex > i;
                  const active = stageIndex === i;
                  return (
                    <motion.div
                      key={s}
                      initial={{ opacity: 0, x: -8 }}
                      animate={{ opacity: 1, x: 0 }}
                      transition={{ delay: i * 0.05 }}
                      className="flex items-center gap-3 text-xs"
                    >
                      {done ? (
                        <CheckCircle2 className="w-3.5 h-3.5 text-nidhi-success flex-shrink-0" />
                      ) : active ? (
                        <motion.div
                          className="w-3.5 h-3.5 rounded-full border-2 border-nidhi-gold flex-shrink-0"
                          animate={{ opacity: [1, 0.4, 1] }}
                          transition={{ duration: 0.8, repeat: Infinity }}
                        />
                      ) : (
                        <div className="w-3.5 h-3.5 rounded-full border border-nidhi-border-subtle flex-shrink-0" />
                      )}
                      <span className={done ? 'text-nidhi-text-secondary' : active ? 'text-nidhi-text' : 'text-nidhi-text-muted'}>
                        {STAGE_LABELS[s]}
                      </span>
                    </motion.div>
                  );
                })}
              </div>

              {/* OCR Result */}
              <AnimatePresence>
                {isComplete && (
                  <motion.div
                    initial={{ opacity: 0, y: 12 }}
                    animate={{ opacity: 1, y: 0 }}
                    className="space-y-3 pt-2 border-t border-nidhi-border-subtle"
                  >
                    <div className="flex items-center justify-between">
                      <h3 className="text-sm font-semibold text-nidhi-text">{MOCK_OCR_RESULT.title}</h3>
                      <span className="text-xs text-nidhi-success font-semibold">
                        {Math.round(MOCK_OCR_RESULT.confidence * 100)}% confidence
                      </span>
                    </div>

                    <div className="grid grid-cols-2 gap-2">
                      {MOCK_OCR_RESULT.extractedFields.map((f) => (
                        <div key={f.label} className="p-2.5 rounded-lg bg-nidhi-elevated border border-nidhi-border-subtle">
                          <p className="text-[10px] text-nidhi-text-muted uppercase tracking-wider">{f.label}</p>
                          <p className="text-xs font-medium text-nidhi-text mt-0.5">{f.value}</p>
                        </div>
                      ))}
                    </div>

                    <div className="p-3 rounded-xl bg-nidhi-gold/5 border border-nidhi-gold/15">
                      <p className="text-[11px] text-nidhi-text-secondary leading-relaxed">{MOCK_OCR_RESULT.aiSummary}</p>
                    </div>

                    <button
                      onClick={onClose}
                      className="btn-primary w-full text-sm"
                    >
                      Save to Vault
                      <CheckCircle2 className="w-4 h-4" />
                    </button>
                  </motion.div>
                )}
              </AnimatePresence>
            </div>
          )}
        </div>
      </motion.div>
    </div>
  );
}

// ─── Document Card ───────────────────────────────────────────────────────────

function DocumentCard({ doc, idx }: { doc: any; idx: number }) {
  const isExpiring =
    doc.expiryDate &&
    new Date(doc.expiryDate).getTime() - Date.now() < 90 * 24 * 60 * 60 * 1000;
  const isExpired = doc.expiryDate && new Date(doc.expiryDate) < new Date();

  return (
    <motion.div
      layout
      initial={{ opacity: 0, scale: 0.97 }}
      animate={{ opacity: 1, scale: 1 }}
      exit={{ opacity: 0, scale: 0.97 }}
      transition={{ duration: 0.22, delay: idx * 0.03 }}
      whileHover={{ y: -2 }}
      className="group card-premium p-5 cursor-pointer"
    >
      {/* Category color bar */}
      <div
        className="h-0.5 w-8 rounded-full mb-4"
        style={{ backgroundColor: getCategoryColor(doc.category) }}
      />

      <div className="flex items-start gap-4">
        {/* Category icon */}
        <div
          className="w-10 h-10 rounded-xl flex items-center justify-center flex-shrink-0 transition-transform group-hover:scale-105"
          style={{ backgroundColor: `${getCategoryColor(doc.category)}18` }}
        >
          <CategoryIcon
            category={doc.category}
            className="w-4 h-4"
            style={{ color: getCategoryColor(doc.category) }}
          />
        </div>

        {/* Content */}
        <div className="flex-1 min-w-0">
          <div className="flex items-start justify-between gap-2 mb-1">
            <h3 className="font-medium text-nidhi-text text-sm leading-snug line-clamp-2">
              {doc.title}
            </h3>
            {/* Health indicator */}
            {isExpired ? (
              <AlertCircle className="w-3.5 h-3.5 text-nidhi-danger flex-shrink-0 mt-0.5" />
            ) : isExpiring ? (
              <Clock className="w-3.5 h-3.5 text-nidhi-warning flex-shrink-0 mt-0.5" />
            ) : doc.isVerified ? (
              <ShieldCheck className="w-3.5 h-3.5 text-nidhi-success flex-shrink-0 mt-0.5" />
            ) : null}
          </div>

          {doc.documentNumber && (
            <p className="text-[11px] font-mono text-nidhi-text-muted mb-2 truncate">
              {doc.documentNumber}
            </p>
          )}

          {doc.aiSummary && (
            <p className="text-[11px] text-nidhi-text-muted line-clamp-2 leading-relaxed">
              {doc.aiSummary}
            </p>
          )}
        </div>
      </div>

      {/* Footer */}
      <div className="mt-4 pt-3 border-t border-nidhi-border-subtle flex items-center justify-between gap-3">
        <div className="flex items-center gap-3">
          {doc.issueDate && (
            <span className="text-[10px] text-nidhi-text-muted">
              Issued {formatDate(doc.issueDate)}
            </span>
          )}
        </div>
        {doc.expiryDate && (
          <span className={`text-[10px] font-medium ${isExpired ? 'text-nidhi-danger' : isExpiring ? 'text-nidhi-warning' : 'text-nidhi-text-muted'}`}>
            {isExpired ? 'Expired' : `Expires ${formatDate(doc.expiryDate)}`}
          </span>
        )}
      </div>
    </motion.div>
  );
}

// ─── Main Page ───────────────────────────────────────────────────────────────

export default function VaultPage() {
  const [allDocs, setAllDocs] = useState<any[]>([]);
  const [search, setSearch] = useState('');
  const [active, setActive] = useState<string | null>(null);
  const [showUpload, setShowUpload] = useState(false);

  useEffect(() => {
    setAllDocs(dataStore.getDocuments());
  }, []);

  const filtered = allDocs.filter((d) => {
    const matchSearch =
      !search ||
      d.title.toLowerCase().includes(search.toLowerCase()) ||
      d.tags?.some((t: string) => t.toLowerCase().includes(search.toLowerCase())) ||
      d.aiSummary?.toLowerCase().includes(search.toLowerCase());
    const matchCat = !active || d.category === active;
    return matchSearch && matchCat;
  });

  return (
    <div className="min-h-screen px-8 py-10 max-w-5xl mx-auto space-y-10">

      {/* Header */}
      <motion.div
        initial={{ opacity: 0, y: -12 }}
        animate={{ opacity: 1, y: 0 }}
        className="flex items-end justify-between gap-4"
      >
        <div className="space-y-1">
          <p className="section-label">Sharma Family</p>
          <h1 className="text-4xl font-display font-bold text-nidhi-text">Document Library</h1>
          <p className="text-nidhi-text-secondary">
            {allDocs.length} documents · {allDocs.filter(d => d.isVerified).length} verified
          </p>
        </div>
        <motion.button
          whileHover={{ scale: 1.02 }}
          whileTap={{ scale: 0.98 }}
          onClick={() => setShowUpload(true)}
          className="btn-primary flex-shrink-0"
        >
          <Upload className="w-4 h-4" />
          Upload
        </motion.button>
      </motion.div>

      {/* Search */}
      <motion.div
        initial={{ opacity: 0, y: 8 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ delay: 0.08 }}
        className="relative"
      >
        <Search className="absolute left-4 top-1/2 -translate-y-1/2 w-4 h-4 text-nidhi-text-muted" />
        <input
          type="text"
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          placeholder="Search by name, tag, or content..."
          className="input-premium pl-11"
        />
        {search && (
          <button
            onClick={() => setSearch('')}
            className="absolute right-4 top-1/2 -translate-y-1/2 text-nidhi-text-muted hover:text-nidhi-text transition-colors"
          >
            <X className="w-4 h-4" />
          </button>
        )}
      </motion.div>

      {/* Category filter */}
      <div className="grid grid-cols-2 sm:grid-cols-4 gap-2">
        {SECTIONS.map(({ key, label, Icon, desc }, idx) => {
          const count = allDocs.filter((d) => d.category === key).length;
          const isActive = active === key;
          return (
            <motion.button
              key={key}
              initial={{ opacity: 0, y: 8 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: 0.1 + idx * 0.03 }}
              whileHover={{ y: -1 }}
              onClick={() => setActive(isActive ? null : key)}
              className={`text-left p-4 rounded-xl border transition-all duration-200 ${
                isActive
                  ? 'bg-nidhi-gold/8 border-nidhi-gold/25 shadow-sm'
                  : 'bg-nidhi-card border-nidhi-border-subtle hover:border-nidhi-border'
              }`}
            >
              <Icon
                className={`w-4 h-4 mb-2.5 ${isActive ? 'text-nidhi-gold' : 'text-nidhi-text-muted'}`}
              />
              <p className={`text-sm font-medium ${isActive ? 'text-nidhi-gold' : 'text-nidhi-text'}`}>
                {label}
              </p>
              <p className="text-[11px] text-nidhi-text-muted mt-0.5">{count} docs</p>
            </motion.button>
          );
        })}
      </div>

      {/* Docs count row */}
      <div className="flex items-center justify-between">
        <p className="text-sm text-nidhi-text-muted">
          {active ? SECTIONS.find((s) => s.key === active)?.label : 'All'} &middot; {filtered.length} documents
        </p>
        {active && (
          <button
            onClick={() => setActive(null)}
            className="flex items-center gap-1 text-xs text-nidhi-gold hover:text-nidhi-gold-light transition-colors"
          >
            <X className="w-3 h-3" /> Clear filter
          </button>
        )}
      </div>

      {/* Document grid */}
      <AnimatePresence mode="popLayout">
        {filtered.length > 0 ? (
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            {filtered.map((doc, idx) => (
              <DocumentCard key={doc.id} doc={doc} idx={idx} />
            ))}
          </div>
        ) : (
          <motion.div
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            className="text-center py-20"
          >
            <div className="w-12 h-12 rounded-xl bg-nidhi-elevated border border-nidhi-border-subtle flex items-center justify-center mx-auto mb-4">
              <FileText className="w-5 h-5 text-nidhi-text-muted" />
            </div>
            <p className="text-nidhi-text font-medium mb-1">No documents found</p>
            <p className="text-sm text-nidhi-text-muted">Try a different search or category</p>
          </motion.div>
        )}
      </AnimatePresence>

      <div className="h-8" />

      {/* Upload modal */}
      <AnimatePresence>
        {showUpload && <UploadModal onClose={() => setShowUpload(false)} />}
      </AnimatePresence>
    </div>
  );
}
