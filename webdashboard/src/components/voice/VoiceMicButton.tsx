'use client';

import React, { useEffect, useRef, useState } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { Mic, MicOff, Loader2 } from 'lucide-react';
import { useWhisper } from '@/lib/hooks/useWhisper';

// The 4 supported languages
const LANGS = [
  { key: 'en', label: 'English',  native: 'English' },
  { key: 'hi', label: 'Hindi',    native: 'हिंदी'   },
  { key: 'kn', label: 'Kannada',  native: 'ಕನ್ನಡ'  },
  { key: 'ta', label: 'Tamil',    native: 'தமிழ்'  },
] as const;

type LangKey = (typeof LANGS)[number]['key'];

interface VoiceMicButtonProps {
  /** Called when Whisper finishes — receives transcript text + language key */
  onTranscript: (text: string, detectedLanguage: string) => void;
  /** Whether the chat is currently busy (sending / streaming) */
  disabled?: boolean;
}

export function VoiceMicButton({ onTranscript, disabled = false }: VoiceMicButtonProps) {
  const { state, transcript, detectedLanguage, isSupported, startRecording, stopRecording, error } =
    useWhisper();

  // Whether the language picker popover is open
  const [pickerOpen, setPickerOpen] = useState(false);
  // The language chosen for the current/next recording session
  const [selectedLang, setSelectedLang] = useState<LangKey>('en');

  const containerRef = useRef<HTMLDivElement>(null);

  // Close picker when clicking outside
  useEffect(() => {
    const handleOutside = (e: MouseEvent) => {
      if (containerRef.current && !containerRef.current.contains(e.target as Node)) {
        setPickerOpen(false);
      }
    };
    if (pickerOpen) document.addEventListener('mousedown', handleOutside);
    return () => document.removeEventListener('mousedown', handleOutside);
  }, [pickerOpen]);

  // Forward transcript when Whisper finishes
  useEffect(() => {
    if (transcript) {
      onTranscript(transcript, detectedLanguage);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [transcript]);

  if (!isSupported) return null;

  const isRecording  = state === 'recording';
  const isProcessing = state === 'processing';
  const isError      = state === 'error';
  const isBusy       = isRecording || isProcessing;

  const handleMicClick = () => {
    if (disabled || isProcessing) return;

    if (isRecording) {
      // Stop ongoing recording immediately
      stopRecording();
      setPickerOpen(false);
      return;
    }

    // Open language picker before starting
    setPickerOpen((prev) => !prev);
  };

  const handleLangSelect = (key: LangKey) => {
    setSelectedLang(key);
    setPickerOpen(false);
    startRecording(key);
  };

  const langLabel = LANGS.find((l) => l.key === selectedLang)?.native ?? 'EN';

  return (
    <div ref={containerRef} className="relative flex-shrink-0">

      {/* ── Status badge (Recording / Transcribing) ── */}
      <AnimatePresence>
        {isBusy && (
          <motion.div
            initial={{ opacity: 0, y: 4, scale: 0.85 }}
            animate={{ opacity: 1, y: 0, scale: 1 }}
            exit={{ opacity: 0, y: 4, scale: 0.85 }}
            className="absolute -top-9 left-1/2 -translate-x-1/2 whitespace-nowrap px-2.5 py-1 rounded-full text-[10px] font-medium border border-nidhi-border-subtle bg-nidhi-card text-nidhi-text-secondary flex items-center gap-1.5 pointer-events-none z-10"
          >
            {isRecording ? (
              <>
                <span className="w-1.5 h-1.5 rounded-full bg-red-500 animate-pulse" />
                Recording in {langLabel}…
              </>
            ) : (
              <>
                <Loader2 className="w-2.5 h-2.5 animate-spin" />
                Transcribing…
              </>
            )}
          </motion.div>
        )}
      </AnimatePresence>

      {/* ── Error badge ── */}
      <AnimatePresence>
        {isError && error && (
          <motion.div
            initial={{ opacity: 0, y: 4 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0 }}
            className="absolute -top-9 left-1/2 -translate-x-1/2 whitespace-nowrap px-2 py-0.5 rounded-full text-[10px] font-medium border border-red-500/30 bg-nidhi-card text-red-400 pointer-events-none z-10"
          >
            {error}
          </motion.div>
        )}
      </AnimatePresence>

      {/* ── Language picker popover ── */}
      <AnimatePresence>
        {pickerOpen && (
          <motion.div
            initial={{ opacity: 0, y: 6, scale: 0.92 }}
            animate={{ opacity: 1, y: 0, scale: 1 }}
            exit={{ opacity: 0, y: 6, scale: 0.92 }}
            transition={{ type: 'spring', stiffness: 380, damping: 28 }}
            className="absolute bottom-12 left-1/2 -translate-x-1/2 z-50 min-w-[160px] bg-nidhi-card border border-nidhi-border rounded-2xl shadow-2xl shadow-nidhi-black/60 overflow-hidden p-1.5"
          >
            <p className="text-[9px] uppercase tracking-widest text-nidhi-text-muted px-2 pt-1 pb-1.5 font-semibold">
              Speak in
            </p>
            {LANGS.map((lang) => (
              <button
                key={lang.key}
                id={`lang-picker-${lang.key}`}
                type="button"
                onClick={() => handleLangSelect(lang.key)}
                className={`w-full flex items-center justify-between gap-3 px-3 py-2 rounded-xl text-sm transition-all duration-150 ${
                  selectedLang === lang.key
                    ? 'bg-nidhi-gold/15 text-nidhi-gold'
                    : 'text-nidhi-text-secondary hover:bg-nidhi-elevated hover:text-nidhi-text'
                }`}
              >
                <span className="font-medium">{lang.native}</span>
                <span className="text-[11px] opacity-60">{lang.label}</span>
                {selectedLang === lang.key && (
                  <motion.span
                    layoutId="lang-check"
                    className="w-1.5 h-1.5 rounded-full bg-nidhi-gold flex-shrink-0"
                  />
                )}
              </button>
            ))}
          </motion.div>
        )}
      </AnimatePresence>

      {/* ── Mic button ── */}
      <motion.button
        type="button"
        id="voice-mic-btn"
        onClick={handleMicClick}
        disabled={disabled || isProcessing}
        whileHover={disabled || isProcessing ? {} : { scale: 1.08 }}
        whileTap={disabled || isProcessing ? {} : { scale: 0.93 }}
        className={`
          relative w-8 h-8 rounded-xl flex items-center justify-center transition-all duration-200
          focus:outline-none focus-visible:ring-2 focus-visible:ring-nidhi-gold/50
          ${isRecording
            ? 'bg-red-500/20 border border-red-500/60 text-red-400'
            : pickerOpen
            ? 'bg-nidhi-gold/15 border border-nidhi-gold/50 text-nidhi-gold'
            : isProcessing
            ? 'bg-nidhi-elevated border border-nidhi-border-subtle text-nidhi-text-muted cursor-wait'
            : isError
            ? 'bg-red-500/10 border border-red-500/30 text-red-400'
            : 'bg-nidhi-elevated border border-nidhi-border-subtle text-nidhi-text-secondary hover:border-nidhi-gold/40 hover:text-nidhi-gold'
          }
          disabled:opacity-40 disabled:cursor-not-allowed
        `}
        aria-label={isRecording ? 'Stop recording' : 'Choose language and speak'}
        title={isRecording ? 'Click to stop' : 'Click to choose language and speak'}
      >
        {/* Ripple when recording */}
        {isRecording && (
          <motion.span
            className="absolute inset-0 rounded-xl border-2 border-red-500/60"
            animate={{ scale: [1, 1.5], opacity: [0.7, 0] }}
            transition={{ duration: 1.2, repeat: Infinity, ease: 'easeOut' }}
          />
        )}

        {isProcessing ? (
          <Loader2 className="w-3.5 h-3.5 animate-spin" />
        ) : isRecording ? (
          <MicOff className="w-3.5 h-3.5" />
        ) : (
          <Mic className="w-3.5 h-3.5" />
        )}
      </motion.button>

      {/* Small language indicator dot when not recording */}
      <AnimatePresence>
        {!isBusy && !pickerOpen && (
          <motion.span
            initial={{ opacity: 0, scale: 0 }}
            animate={{ opacity: 1, scale: 1 }}
            exit={{ opacity: 0, scale: 0 }}
            className="absolute -bottom-1 -right-1 px-1 rounded-full bg-nidhi-surface border border-nidhi-border text-[8px] font-bold text-nidhi-text-muted leading-none py-0.5"
            title={`Speaking in: ${LANGS.find(l => l.key === selectedLang)?.label}`}
          >
            {selectedLang.toUpperCase()}
          </motion.span>
        )}
      </AnimatePresence>
    </div>
  );
}
