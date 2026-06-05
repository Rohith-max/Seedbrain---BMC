'use client';

import React, { useState, useRef, useEffect, useCallback } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { Send, FileText, ShieldCheck, Sparkles, ChevronRight } from 'lucide-react';

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
        `Kamla Devi's latest medical report (Apollo Diagnostics, 15 May 2025) shows:\n\n**Blood Sugar Panel**\n- HbA1c: **7.2%** — slightly above the ideal target of 7.0%\n- Fasting blood sugar: **145 mg/dL** (normal below 130)\n- Post-prandial: **210 mg/dL** (target below 180)\n\nDr. Mehta's recommendation: continue current medication, schedule a follow-up in 3 months. **That follow-up is due in mid-August 2025.**\n\n**What I suggest:**\n1. Book an appointment at Apollo Clinic, Whitefield (you can call 1860-500-1066)\n2. Maintain a fasting blood sugar log before the appointment\n3. Kamla Devi may be eligible for Ayushman Bharat (PM-JAY) coverage — I've identified this as a benefit worth exploring (₹5 lakh cover, no premium for seniors)\n\nShall I draft an appointment reminder or check PMJAY eligibility details?`,
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
        `Here's a complete picture of your SBI Home Loan.\n\n**Loan Summary** (Account SBI-HL-2018-234567)\n- Original loan: ₹55,00,000 (September 2018)\n- Current outstanding: **₹38,45,000**\n- Monthly EMI: **₹48,500** at 8.5% interest\n- Tenure: 20 years (approx. 13 years remaining)\n- Principal repaid so far: ₹16,55,000\n\n**Next EMI:** Due 5 July 2025 — ensure your SBI account (...2234) has sufficient balance.\n\n**Tax Benefits you can claim:**\n- Section 24(b): Interest paid ~₹3.25 lakh in FY 2024-25 — deductible up to ₹2 lakh for self-occupied property\n- Section 80C: Principal repayment ~₹1.22 lakh counted towards the ₹1.5 lakh limit\n\nI estimate this reduces your tax liability by approximately **₹62,500** for FY 2024-25. Want me to generate a tax deduction summary?`,
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
        `Based on your uploaded documents, here's your tax position for AY 2025-26.\n\n**Gross Total Income:** ~₹18,50,000 (Rajesh Sharma)\n\n**Available Deductions:**\n- Section 80C: LIC premium ₹12,500 + home loan principal ₹1,22,000 + ELSS (if any) = **up to ₹1,50,000**\n- Section 24(b): Home loan interest ~₹3,25,000 — capped at **₹2,00,000** for self-occupied\n- Section 80D: Star Health premium ₹28,500 — deductible up to ₹25,000 (self+family), additional ₹50,000 for senior citizen parent (Kamla Devi)\n\n**Estimated Tax Saving: ~₹46,800**\n\n**ITR Deadline:** 31 July 2025 — that's **57 days away**. I'd recommend starting next week.\n\nDocuments ready in vault: PAN, Aadhaar, Home Loan Statement, LIC Policy, Health Insurance.\nMissing: Form 16 from employer, Priya's PAN (for joint filing check).\n\nShall I create a filing checklist?`,
      sources: [
        { title: 'ITR Acknowledgment AY 2024-25', category: 'tax' },
        { title: 'SBI Home Loan Statement', category: 'financial' },
        { title: 'LIC Term Plan', category: 'insurance' },
        { title: 'Star Health Policy', category: 'insurance' },
      ],
    },
  },
  {
    keywords: ['property', 'house', 'apartment', 'whitefield', 'maple heights', 'deed', 'bbmp'],
    response: {
      thinkingMs: 700,
      content:
        `Here are the details for your residential property.\n\n**42 Maple Heights, Whitefield, Bengaluru**\n- Type: 3BHK apartment, 1450 sq ft\n- Purchase price: ₹85,00,000 (September 2018)\n- Registration: Sub-Registrar Office, Bengaluru (BLR-SR-2018-45678)\n- Mortgage: SBI Home Loan ₹55 lakh, ₹38.45 lakh outstanding\n\n**BBMP Property Tax**\nLast payment status: I don't see a recent tax receipt in the vault. BBMP property tax for a 1450 sq ft flat in Whitefield is typically around ₹8,000–12,000/year. The payment window is usually April–June.\n\n**Action items:**\n1. Verify BBMP payment status at bbmptax.karnataka.gov.in\n2. Upload tax receipt to vault for complete records\n3. Consider registering for BBMP online payment for auto-reminders`,
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
        `I've analysed all family documents and identified 5 benefits worth ₹8.9L+ total.\n\n**High confidence (apply now):**\n\n1. **National Merit Scholarship** — Aarav Sharma\n   Value: ₹12,000/year. Apply by 30 September 2025. 92% match.\n\n2. **Section 80C + 80D Tax Deduction** — Rajesh Sharma\n   Estimated saving: ₹46,800. File before 31 July 2025. 88% match.\n\n**Medium confidence (verify eligibility):**\n\n3. **PM Awas Yojana — Urban (PMAY)** — Rajesh Sharma\n   Interest subsidy up to ₹2,67,000. Based on income bracket and first-time ownership status. 75% match.\n\n4. **Ayushman Bharat PM-JAY** — Kamla Devi Sharma\n   ₹5 lakh health cover. Kamla Devi is 75 years old and has a pre-existing condition. 68% match.\n\n5. **Sukanya Samriddhi Yojana** — Ananya Sharma\n   Long-term savings: ₹65L+ at maturity. 55% match (check age eligibility).\n\nWant me to create an application checklist for any of these?`,
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

// ─── Streaming Component ──────────────────────────────────────────────────────

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

// ─── Main Page ───────────────────────────────────────────────────────────────

export default function AssistantPage() {
  const [messages, setMessages] = useState<Message[]>([
    {
      id: '0',
      role: 'assistant',
      content:
        "Namaste. I'm NIDHI — your family's intelligence assistant.\n\nI've analysed all 12 documents in the Sharma family vault. Ask me anything about insurance, loans, taxes, medical records, scholarships, or government schemes.",
    },
  ]);
  const [input, setInput] = useState('');
  const [thinking, setThinking] = useState(false);
  const [streamingId, setStreamingId] = useState<string | null>(null);
  const endRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    endRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages, thinking]);

  const send = useCallback(
    (text: string) => {
      if (!text.trim() || thinking || streamingId) return;

      const userId = `u-${Date.now()}`;
      setMessages((prev) => [...prev, { id: userId, role: 'user', content: text }]);
      setInput('');
      setThinking(true);

      const def = getResponse(text);
      const delay = def.thinkingMs ?? 900;

      setTimeout(() => {
        setThinking(false);
        const aiId = `a-${Date.now()}`;
        setMessages((prev) => [
          ...prev,
          {
            id: aiId,
            role: 'assistant',
            content: def.content,
            sources: def.sources,
            streaming: true,
          },
        ]);
        setStreamingId(aiId);
      }, delay);
    },
    [thinking, streamingId]
  );

  const handleStreamDone = useCallback(
    (id: string) => {
      setStreamingId(null);
      setMessages((prev) =>
        prev.map((m) => (m.id === id ? { ...m, streaming: false } : m))
      );
    },
    []
  );

  const showSuggestions = messages.length === 1 && !thinking;

  return (
    <div className="flex flex-col h-screen">

      {/* Header */}
      <div className="px-6 md:px-8 py-5 border-b border-nidhi-border-subtle bg-nidhi-black/70 backdrop-blur-xl flex-shrink-0">
        <div className="max-w-3xl mx-auto flex items-center gap-3">
          <div className="w-9 h-9 rounded-xl gradient-gold flex items-center justify-center flex-shrink-0">
            <Sparkles className="w-4 h-4 text-nidhi-black" />
          </div>
          <div className="flex-1">
            <h1 className="text-sm font-semibold text-nidhi-text">NIDHI AI</h1>
            <div className="flex items-center gap-1.5">
              <span className="w-1.5 h-1.5 rounded-full bg-nidhi-success animate-pulse" />
              <p className="text-[11px] text-nidhi-text-muted">Active · 12 documents loaded</p>
            </div>
          </div>
        </div>
      </div>

      {/* Messages */}
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
                          onDone={() => handleStreamDone(msg.id)}
                        />
                      ) : (
                        /* Bold markdown rendering */
                        msg.content.split(/(\*\*[^*]+\*\*)/).map((part, i) =>
                          part.startsWith('**') && part.endsWith('**') ? (
                            <strong key={i} className="font-semibold text-nidhi-text">
                              {part.slice(2, -2)}
                            </strong>
                          ) : (
                            <span key={i}>{part}</span>
                          )
                        )
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

          <div ref={endRef} />
        </div>
      </div>

      {/* Suggested prompts — only on first load */}
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

      {/* Input */}
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
              type="text"
              value={input}
              onChange={(e) => setInput(e.target.value)}
              placeholder="Ask anything about your family..."
              className="flex-1 bg-transparent text-sm text-nidhi-text outline-none placeholder:text-nidhi-text-muted"
              disabled={thinking || !!streamingId}
            />
            <motion.button
              type="submit"
              disabled={!input.trim() || thinking || !!streamingId}
              whileHover={{ scale: 1.05 }}
              whileTap={{ scale: 0.95 }}
              className="w-8 h-8 rounded-xl gradient-gold flex items-center justify-center text-nidhi-black disabled:opacity-35 disabled:cursor-not-allowed flex-shrink-0"
            >
              <Send className="w-3.5 h-3.5" />
            </motion.button>
          </div>
          <div className="flex items-center justify-center gap-1.5 mt-2 text-[11px] text-nidhi-text-muted">
            <ShieldCheck className="w-3 h-3 text-nidhi-success" />
            End-to-end encrypted · Data never used for training
          </div>
        </form>
      </div>
    </div>
  );
}
