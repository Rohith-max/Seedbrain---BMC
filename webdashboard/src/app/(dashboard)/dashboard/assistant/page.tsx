'use client';

import React, { useState, useRef, useEffect, useCallback } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import {
  Send, FileText, ShieldCheck, Sparkles, ChevronRight,
  Volume2, VolumeX, Mic,
} from 'lucide-react';
import { VoiceMicButton } from '@/components/voice/VoiceMicButton';
import { useTTS, SUPPORTED_LANGS, SupportedLang } from '@/lib/hooks/useTTS';

// ─── Types ───────────────────────────────────────────────────────────────────

interface Source {
  title: string;
  category: string;
}

interface Message {
  id: string;
  role: 'user' | 'assistant';
  content: string;
  sources?: Source[];
  streaming?: boolean;
  thinkingMs?: number;
  lang?: SupportedLang;
}

// ─── Response Library ─────────────────────────────────────────────────────────

interface ResponseDef {
  content: string;
  sources?: Source[];
  thinkingMs?: number;
}

const RESPONSES: { keywords: string[]; response: ResponseDef }[] = [
  {
    keywords: ['insurance', 'expire', 'expiry', 'lic', 'policy', 'health policy'],
    response: {
      thinkingMs: 1100,
      content:
        `I found 3 insurance policies in the vault for your family.\n\n**Star Health Family Floater** (Policy SH-FAM-2024-78945)\nCovers Rajesh, Priya, Aarav, and Ananya Sharma. Sum insured ₹10 lakh. Annual premium ₹28,500. This policy expires on **31 August 2026** — I'll send a renewal reminder in July 2026.\n\n**LIC Tech Term Plan** (LIC-TERM-2020-12345)\nLife assured: Rajesh Sharma. Sum assured ₹1 crore. Premium ₹12,500/year. Policy runs until **14 January 2048**. Next premium due 15 January 2026. Eligible for Section 80C deduction.\n\n**Honda City Vehicle Insurance**\nRegistration KA-05-MN-4567. Expires **31 December 2025**. I recommend comparing quotes at least 30 days before — you can potentially save 15–20% by switching providers.`,
      sources: [
        { title: 'Star Health Family Policy', category: 'insurance' },
        { title: 'LIC Term Plan 2020', category: 'insurance' },
        { title: 'Vehicle RC — Honda City', category: 'vehicle' },
      ],
    },
  },
  {
    keywords: ['scholarship', 'aarav', 'education', 'cbse', 'merit', 'kvpy'],
    response: {
      thinkingMs: 900,
      content:
        `Based on Aarav's academic records, he qualifies for two scholarships.\n\n**National Merit-cum-Means Scholarship (CBSE)**\nAarav scored **86.6% in CBSE Class 10 (2025)** — Mathematics 92, Social Science 90, Science 88. This exceeds the 85% eligibility threshold. Scholarship value: ₹12,000/year for 2 years.\nApplication window: **August 1 – September 30, 2025**. I've added this to your alerts.\n\n**KVPY (Kishore Vaigyanik Protsahan Yojana)**\nAarav's strong performance in Science and Mathematics makes him eligible to appear for KVPY. Fellowship value ranges from ₹5,000–8,000/month plus contingency grants.\nApplication typically opens in July–August.\n\nRequired documents for both: Class 10 Marksheet, Aadhaar, income certificate, bank account details — all available in the vault except the income certificate.`,
      sources: [
        { title: 'CBSE Class 10 Report Card', category: 'education' },
        { title: 'Aadhaar — Aarav Sharma', category: 'identity' },
      ],
    },
  },
  {
    keywords: ['diabetes', 'kamla', 'medical', 'hba1c', 'sugar', 'blood', 'apollo'],
    response: {
      thinkingMs: 800,
      content:
        `Kamla Devi's latest medical report (Apollo Diagnostics, 15 May 2025) shows:\n\n**Blood Sugar Panel**\n- HbA1c: **7.2%** — slightly above the ideal target of 7.0%\n- Fasting blood sugar: **145 mg/dL** (normal below 130)\n- Post-prandial: **210 mg/dL** (target below 180)\n\nDr. Mehta's recommendation: continue current medication, schedule a follow-up in 3 months. **That follow-up is due in mid-August 2025.**\n\nShall I draft an appointment reminder or check PMJAY eligibility details?`,
      sources: [
        { title: 'Apollo Diagnostics Report — May 2025', category: 'medical' },
      ],
    },
  },
  {
    keywords: ['loan', 'emi', 'sbi', 'home loan', 'mortgage', 'outstanding'],
    response: {
      thinkingMs: 950,
      content:
        `Here's a complete picture of your SBI Home Loan.\n\n**Loan Summary** (Account SBI-HL-2018-234567)\n- Original loan: ₹55,00,000 (September 2018)\n- Current outstanding: **₹38,45,000**\n- Monthly EMI: **₹48,500** at 8.5% interest\n- Tenure: 20 years (approx. 13 years remaining)\n\n**Next EMI:** Due 5 July 2025 — ensure your SBI account (...2234) has sufficient balance.\n\n**Tax Benefits:**\n- Section 24(b): Interest paid ~₹3.25 lakh — deductible up to ₹2 lakh\n- Section 80C: Principal repayment ~₹1.22 lakh counted towards the ₹1.5 lakh limit`,
      sources: [
        { title: 'SBI Home Loan Statement FY 2024-25', category: 'financial' },
        { title: 'Property Deed — 42 Maple Heights', category: 'property' },
      ],
    },
  },
  {
    keywords: ['tax', 'itr', 'section 80', 'deduction', 'refund', 'income tax'],
    response: {
      thinkingMs: 1200,
      content:
        `Based on your uploaded documents, here's your tax position for AY 2025-26.\n\n**Gross Total Income:** ~₹18,50,000 (Rajesh Sharma)\n\n**Available Deductions:**\n- Section 80C: LIC + home loan principal = **up to ₹1,50,000**\n- Section 24(b): Home loan interest — capped at **₹2,00,000**\n- Section 80D: Star Health premium — **₹25,000** (self+family) + ₹50,000 for Kamla Devi\n\n**Estimated Tax Saving: ~₹46,800**\n\n**ITR Deadline:** 31 July 2025. I recommend starting next week.\n\nMissing: Form 16 from employer, Priya's PAN (for joint filing check).`,
      sources: [
        { title: 'ITR Acknowledgment AY 2024-25', category: 'tax' },
        { title: 'SBI Home Loan Statement', category: 'financial' },
        { title: 'Star Health Policy', category: 'insurance' },
      ],
    },
  },
  {
    keywords: ['property', 'house', 'apartment', 'whitefield', 'maple heights', 'deed', 'bbmp'],
    response: {
      thinkingMs: 700,
      content:
        `Here are the details for your residential property.\n\n**42 Maple Heights, Whitefield, Bengaluru**\n- Type: 3BHK apartment, 1450 sq ft\n- Purchase price: ₹85,00,000 (September 2018)\n- Mortgage: SBI Home Loan ₹55 lakh, ₹38.45 lakh outstanding\n\n**BBMP Property Tax**\nI don't see a recent tax receipt in the vault. BBMP property tax for a 1450 sq ft flat in Whitefield is typically around ₹8,000–12,000/year.\n\n**Action items:**\n1. Verify BBMP payment status at bbmptax.karnataka.gov.in\n2. Upload tax receipt to vault for complete records`,
      sources: [
        { title: 'Property Sale Deed 2018', category: 'property' },
        { title: 'SBI Home Loan Statement', category: 'financial' },
      ],
    },
  },
  {
    keywords: ['benefits', 'scheme', 'government', 'eligible', 'ayushman', 'pmay'],
    response: {
      thinkingMs: 1400,
      content:
        `I've analysed all family documents and identified 5 benefits worth ₹8.9L+ total.\n\n**High confidence (apply now):**\n\n1. **National Merit Scholarship** — Aarav Sharma\n   Value: ₹12,000/year. Apply by 30 September 2025.\n\n2. **Section 80C + 80D Tax Deduction** — Rajesh Sharma\n   Estimated saving: ₹46,800. File before 31 July 2025.\n\n**Medium confidence (verify eligibility):**\n\n3. **PM Awas Yojana — Urban (PMAY)** — Interest subsidy up to ₹2,67,000.\n\n4. **Ayushman Bharat PM-JAY** — Kamla Devi Sharma\n   ₹5 lakh health cover. 68% match.\n\n5. **Sukanya Samriddhi Yojana** — Ananya Sharma\n   Long-term savings: ₹65L+ at maturity.`,
      sources: [
        { title: 'CBSE Report Card — Aarav', category: 'education' },
        { title: 'Medical Report — Kamla Devi', category: 'medical' },
        { title: 'Property Deed', category: 'property' },
      ],
    },
  },
];

const FALLBACK: ResponseDef = {
  thinkingMs: 600,
  content:
    `I've scanned all 12 documents in the vault. Here's what I can help you with:\n\n- Insurance policies and expiry dates\n- Loan details and EMI schedules\n- Tax deductions and ITR filing\n- Scholarship eligibility for Aarav\n- Medical records and follow-ups for Kamla Devi\n- Government scheme eligibility\n- Property and utility documents\n\nTry asking: "When does my health insurance expire?" or "What tax deductions can I claim?"`,
};

const SUGGESTED: string[] = [
  'When does my LIC policy expire?',
  'Is Aarav eligible for any scholarship?',
  "Show Kamla Devi's medical summary",
  'What are my home loan details?',
  'Which government benefits am I eligible for?',
];

function getResponse(q: string): ResponseDef {
  const lower = q.toLowerCase();
  for (const { keywords, response } of RESPONSES) {
    if (keywords.some((k) => lower.includes(k))) return response;
  }
  return FALLBACK;
}

// ─── Streaming Text Component ─────────────────────────────────────────────────

function StreamingText({ text, onDone }: { text: string; onDone: () => void }) {
  const [displayed, setDisplayed] = useState('');
  const idx = useRef(0);

  useEffect(() => {
    idx.current = 0;
    const interval = setInterval(() => {
      if (idx.current < text.length) {
        setDisplayed(text.slice(0, idx.current + 1));
        idx.current++;
      } else {
        clearInterval(interval);
        onDone();
      }
    }, 9);
    return () => clearInterval(interval);
  }, [text, onDone]);

  return (
    <span className="whitespace-pre-wrap">
      {displayed}
      <span className="inline-block w-0.5 h-4 bg-nidhi-gold ml-0.5 animate-pulse align-text-bottom" />
    </span>
  );
}

// ─── Render markdown bold ─────────────────────────────────────────────────────

function RichText({ text }: { text: string }) {
  return (
    <>
      {text.split(/(\*\*[^*]+\*\*)/).map((part, i) =>
        part.startsWith('**') && part.endsWith('**') ? (
          <strong key={i} className="font-semibold text-nidhi-text">
            {part.slice(2, -2)}
          </strong>
        ) : (
          <span key={i}>{part}</span>
        )
      )}
    </>
  );
}

// ─── Language Selector ────────────────────────────────────────────────────────

const LANG_KEYS = Object.keys(SUPPORTED_LANGS) as SupportedLang[];

function LangSelector({
  active,
  onChange,
}: {
  active: SupportedLang;
  onChange: (l: SupportedLang) => void;
}) {
  return (
    <div className="flex items-center gap-1 p-1 rounded-xl bg-nidhi-surface border border-nidhi-border-subtle">
      {LANG_KEYS.map((lang) => {
        const isActive = lang === active;
        return (
          <button
            key={lang}
            id={`lang-btn-${lang}`}
            onClick={() => onChange(lang)}
            title={SUPPORTED_LANGS[lang].label}
            className={`px-2.5 py-1 rounded-lg text-[11px] font-medium transition-all duration-200 ${
              isActive
                ? 'bg-nidhi-gold text-nidhi-black shadow-sm'
                : 'text-nidhi-text-muted hover:text-nidhi-text'
            }`}
          >
            {SUPPORTED_LANGS[lang].label}
          </button>
        );
      })}
    </div>
  );
}

// ─── Main Page ────────────────────────────────────────────────────────────────

export default function AssistantPage() {
  const [messages, setMessages] = useState<Message[]>([
    {
      id: '0',
      role: 'assistant',
      content:
        "Namaste. I'm NIDHI — your family's intelligence assistant.\n\nI've analysed all 12 documents in the Sharma family vault. Ask me anything about insurance, loans, taxes, medical records, scholarships, or government schemes.\n\nYou can speak to me — I'll reply in voice too.",
    },
  ]);
  const [input, setInput] = useState('');
  const [thinking, setThinking] = useState(false);
  const [streamingId, setStreamingId] = useState<string | null>(null);
  const [activeLang, setActiveLang] = useState<SupportedLang>('english');
  // Whether the user prefers voice-output mode (toggled by mic icon in header)
  const [voiceMode, setVoiceMode] = useState(true);

  const endRef = useRef<HTMLDivElement>(null);
  const { ttsState, speak, stopSpeaking } = useTTS();

  useEffect(() => {
    endRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages, thinking]);

  /** Send a message, get response, optionally speak it */
  const send = useCallback(
    async (text: string) => {
      if (!text.trim() || thinking || streamingId) return;

      const userId = `u-${Date.now()}`;
      setMessages((prev) => [...prev, { id: userId, role: 'user', content: text, lang: activeLang }]);
      setInput('');
      setThinking(true);

      try {
        const res = await fetch('/api/chat', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ question: text }),
        });
        
        const data = await res.json();
        
        setThinking(false);
        const aiId = `a-${Date.now()}`;
        setMessages((prev) => [
          ...prev,
          {
            id: aiId,
            role: 'assistant',
            content: data.answer || "I'm sorry, I encountered an error.",
            sources: data.sources || [],
            streaming: true,
            lang: activeLang,
          },
        ]);
        setStreamingId(aiId);
      } catch (err) {
        setThinking(false);
        const aiId = `a-${Date.now()}`;
        setMessages((prev) => [
          ...prev,
          {
            id: aiId,
            role: 'assistant',
            content: "Sorry, I couldn't reach the server. Please try again.",
            streaming: true,
            lang: activeLang,
          },
        ]);
        setStreamingId(aiId);
      }
    },
    [thinking, streamingId, activeLang]
  );

  /** Called when StreamingText finishes rendering — speak the response */
  const handleStreamDone = useCallback(
    (id: string, content: string) => {
      setStreamingId(null);
      setMessages((prev) =>
        prev.map((m) => (m.id === id ? { ...m, streaming: false } : m))
      );
      // Speak the response if voice mode is on
      if (voiceMode) {
        speak(content, activeLang);
      }
    },
    [voiceMode, activeLang, speak]
  );

  /** Called by VoiceMicButton — transcript is in the user's selected language, auto-send it */
  const handleVoiceTranscript = useCallback(
    (text: string) => {
      if (text.trim()) send(text);
    },
    [send]
  );

  const handleLangChange = useCallback((lang: SupportedLang) => {
    setActiveLang(lang);
    stopSpeaking();
  }, [stopSpeaking]);

  const showSuggestions = messages.length === 1 && !thinking;
  const isSpeaking = ttsState === 'speaking';

  return (
    <div className="flex flex-col h-screen">

      {/* ── Header ── */}
      <div className="px-4 md:px-8 py-4 border-b border-nidhi-border-subtle bg-nidhi-black/70 backdrop-blur-xl flex-shrink-0">
        <div className="max-w-3xl mx-auto flex items-center gap-3 flex-wrap">
          <div className="w-9 h-9 rounded-xl gradient-gold flex items-center justify-center flex-shrink-0">
            <Sparkles className="w-4 h-4 text-nidhi-black" />
          </div>
          <div className="flex-1 min-w-0">
            <h1 className="text-sm font-semibold text-nidhi-text">NIDHI AI</h1>
            <div className="flex items-center gap-1.5">
              <span className="w-1.5 h-1.5 rounded-full bg-nidhi-success animate-pulse" />
              <p className="text-[11px] text-nidhi-text-muted">Active · 12 documents loaded</p>
            </div>
          </div>

          {/* Voice mode toggle */}
          <button
            id="voice-mode-toggle"
            onClick={() => {
              if (isSpeaking) stopSpeaking();
              setVoiceMode(v => !v);
            }}
            title={voiceMode ? 'Voice output ON — click to mute' : 'Voice output OFF — click to enable'}
            className={`flex items-center gap-1.5 px-3 py-1.5 rounded-xl text-[11px] font-medium border transition-all duration-200 ${
              voiceMode
                ? 'bg-nidhi-gold/15 border-nidhi-gold/40 text-nidhi-gold'
                : 'bg-nidhi-surface border-nidhi-border text-nidhi-text-muted hover:border-nidhi-gold/25'
            }`}
          >
            {voiceMode ? (
              <>{isSpeaking ? <Volume2 className="w-3.5 h-3.5 animate-pulse" /> : <Volume2 className="w-3.5 h-3.5" />} Voice On</>
            ) : (
              <><VolumeX className="w-3.5 h-3.5" /> Voice Off</>
            )}
          </button>

          {/* Language selector */}
          <LangSelector active={activeLang} onChange={handleLangChange} />
        </div>
      </div>

      {/* ── Messages ── */}
      <div className="flex-1 overflow-y-auto chat-scroll px-4 md:px-8 py-6">
        <div className="max-w-3xl mx-auto space-y-6">
          <AnimatePresence initial={false}>
            {messages.map((msg) => (
              <motion.div
                key={msg.id}
                initial={{ opacity: 0, y: 12 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ duration: 0.28 }}
                className={`flex gap-3 ${msg.role === 'user' ? 'justify-end' : 'justify-start'}`}
              >
                {/* Assistant avatar */}
                {msg.role === 'assistant' && (
                  <div className="w-8 h-8 rounded-xl gradient-gold flex items-center justify-center flex-shrink-0 mt-0.5">
                    <Sparkles className="w-3.5 h-3.5 text-nidhi-black" />
                  </div>
                )}

                <div
                  className={`max-w-[82%] flex flex-col gap-2 ${
                    msg.role === 'user' ? 'items-end' : 'items-start'
                  }`}
                >
                  <div
                    className={`px-5 py-4 rounded-2xl ${
                      msg.role === 'user' ? 'chat-bubble-user' : 'chat-bubble-assistant'
                    }`}
                  >
                    <p className="text-[14px] leading-relaxed text-nidhi-text whitespace-pre-wrap">
                      {msg.streaming && msg.id === streamingId ? (
                        <StreamingText
                          text={msg.content}
                          onDone={() => handleStreamDone(msg.id, msg.content)}
                        />
                      ) : (
                        <RichText text={msg.content} />
                      )}
                    </p>

                    {/* Sources */}
                    {!msg.streaming && msg.sources && msg.sources.length > 0 && (
                      <div className="mt-4 pt-3 border-t border-nidhi-border-subtle">
                        <p className="text-[10px] uppercase tracking-wider text-nidhi-text-muted mb-2">
                          Sources
                        </p>
                        <div className="flex flex-wrap gap-2">
                          {msg.sources.map((s, i) => (
                            <div
                              key={i}
                              className="flex items-center gap-1.5 px-2.5 py-1.5 rounded-lg bg-nidhi-elevated border border-nidhi-border-subtle text-[11px] text-nidhi-text-secondary"
                            >
                              <FileText className="w-3 h-3 text-nidhi-gold flex-shrink-0" />
                              {s.title}
                            </div>
                          ))}
                        </div>
                      </div>
                    )}

                    {/* Re-speak button for assistant messages */}
                    {msg.role === 'assistant' && !msg.streaming && voiceMode && (
                      <button
                        onClick={() => speak(msg.content, msg.lang ?? activeLang)}
                        title="Replay voice"
                        className="mt-3 flex items-center gap-1 text-[10px] text-nidhi-text-muted hover:text-nidhi-gold transition-colors"
                      >
                        <Volume2 className="w-3 h-3" />
                        Replay
                      </button>
                    )}
                  </div>
                </div>

                {/* User avatar */}
                {msg.role === 'user' && (
                  <div className="w-8 h-8 rounded-xl bg-nidhi-elevated border border-nidhi-border-subtle flex items-center justify-center flex-shrink-0 mt-0.5 text-sm font-semibold text-nidhi-text-secondary">
                    R
                  </div>
                )}
              </motion.div>
            ))}
          </AnimatePresence>

          {/* Thinking indicator */}
          <AnimatePresence>
            {thinking && (
              <motion.div
                initial={{ opacity: 0, y: 8 }}
                animate={{ opacity: 1, y: 0 }}
                exit={{ opacity: 0 }}
                className="flex gap-3"
              >
                <div className="w-8 h-8 rounded-xl gradient-gold flex items-center justify-center flex-shrink-0 mt-0.5">
                  <Sparkles className="w-3.5 h-3.5 text-nidhi-black" />
                </div>
                <div className="chat-bubble-assistant px-5 py-4 rounded-2xl flex items-center gap-1.5">
                  {[0, 1, 2].map((i) => (
                    <motion.div
                      key={i}
                      className="w-1.5 h-1.5 rounded-full bg-nidhi-text-muted"
                      animate={{ opacity: [0.3, 1, 0.3], y: [0, -3, 0] }}
                      transition={{ duration: 0.7, repeat: Infinity, delay: i * 0.15 }}
                    />
                  ))}
                </div>
              </motion.div>
            )}
          </AnimatePresence>

          {/* Speaking indicator */}
          <AnimatePresence>
            {isSpeaking && (
              <motion.div
                initial={{ opacity: 0, y: 8 }}
                animate={{ opacity: 1, y: 0 }}
                exit={{ opacity: 0 }}
                className="flex items-center gap-2 text-[11px] text-nidhi-gold"
              >
                <Volume2 className="w-3.5 h-3.5 animate-pulse" />
                <span>NIDHI is speaking…</span>
                <button
                  onClick={stopSpeaking}
                  className="underline hover:no-underline text-nidhi-text-muted"
                >
                  Stop
                </button>
              </motion.div>
            )}
          </AnimatePresence>

          <div ref={endRef} />
        </div>
      </div>

      {/* ── Suggestions ── */}
      <AnimatePresence>
        {showSuggestions && (
          <motion.div
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
            className="px-4 md:px-8 pb-3 flex-shrink-0"
          >
            <div className="max-w-3xl mx-auto flex gap-2 overflow-x-auto hide-scrollbar pb-1">
              {SUGGESTED.map((p, i) => (
                <button
                  key={i}
                  onClick={() => send(p)}
                  className="flex-shrink-0 flex items-center gap-1.5 text-xs px-3.5 py-2 rounded-xl bg-nidhi-card border border-nidhi-border-subtle text-nidhi-text-secondary hover:text-nidhi-gold hover:border-nidhi-gold/25 transition-all whitespace-nowrap"
                >
                  {p}
                  <ChevronRight className="w-3 h-3 opacity-50" />
                </button>
              ))}
            </div>
          </motion.div>
        )}
      </AnimatePresence>

      {/* ── Input ── */}
      <div className="px-4 md:px-8 pb-8 pt-3 flex-shrink-0 border-t border-nidhi-border-subtle bg-nidhi-black/70 backdrop-blur-xl">
        <form
          onSubmit={(e) => {
            e.preventDefault();
            send(input);
          }}
          className="max-w-3xl mx-auto"
        >
          <div className="flex items-center gap-3 bg-nidhi-card border border-nidhi-border-subtle rounded-2xl px-4 py-3 focus-within:border-nidhi-gold/35 transition-all">
            <input
              id="assistant-text-input"
              type="text"
              value={input}
              onChange={(e) => setInput(e.target.value)}
              placeholder={`Ask in ${SUPPORTED_LANGS[activeLang].label}…`}
              className="flex-1 bg-transparent text-sm text-nidhi-text outline-none placeholder:text-nidhi-text-muted"
              disabled={thinking || !!streamingId}
            />

            {/* Voice mic — click to pick language, then record */}
            <VoiceMicButton
              onTranscript={handleVoiceTranscript}
              disabled={thinking || !!streamingId || isSpeaking}
            />

            <motion.button
              id="assistant-send-btn"
              type="submit"
              disabled={!input.trim() || thinking || !!streamingId}
              whileHover={{ scale: 1.05 }}
              whileTap={{ scale: 0.95 }}
              className="w-8 h-8 rounded-xl gradient-gold flex items-center justify-center text-nidhi-black disabled:opacity-35 disabled:cursor-not-allowed flex-shrink-0"
            >
              <Send className="w-3.5 h-3.5" />
            </motion.button>
          </div>

          <div className="flex items-center justify-center gap-2 mt-2 text-[11px] text-nidhi-text-muted">
            <ShieldCheck className="w-3 h-3 text-nidhi-success" />
            End-to-end encrypted · Data never used for training
            {voiceMode && (
              <>
                <span className="opacity-40">·</span>
                <Mic className="w-3 h-3" />
                Voice responses enabled
              </>
            )}
          </div>
        </form>
      </div>
    </div>
  );
}
