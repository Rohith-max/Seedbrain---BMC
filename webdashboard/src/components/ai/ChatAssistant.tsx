'use client';

import React, { useState, useEffect, useRef } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { Send, Bot, User, Loader2, Sparkles, AlertCircle, Search, FileText } from 'lucide-react';
import { useBart } from '@/lib/hooks/useBart';

interface Message {
  id: string;
  role: 'user' | 'assistant';
  content: string;
  isStreaming?: boolean;
  reasoning?: string[];
  sources?: string[];
  suggestions?: string[];
}

export function ChatAssistant() {
  const [messages, setMessages] = useState<Message[]>([
    {
      id: '1',
      role: 'assistant',
      content: 'Hello Rajesh! I have analyzed your family profile. How can I assist you today?',
      suggestions: ['When does my LIC expire?', 'Do I qualify for any schemes?', 'Find Aarav\'s certificates']
    }
  ]);
  const [input, setInput] = useState('');
  const [isTyping, setIsTyping] = useState(false);
  const { summarize, isReady, progress: bartProgress } = useBart();
  const endOfMessagesRef = useRef<HTMLDivElement>(null);

  const scrollToBottom = () => {
    endOfMessagesRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  useEffect(() => {
    scrollToBottom();
  }, [messages, isTyping]);

  const handleSend = async (text: string = input) => {
    if (!text.trim()) return;

    const userMessage: Message = { id: Date.now().toString(), role: 'user', content: text };
    setMessages(prev => [...prev, userMessage]);
    setInput('');
    setIsTyping(true);

    // Check if the user is asking to summarize something long
    if (text.toLowerCase().includes('summarize') && isReady) {
      const textToSummarize = text.replace(/summarize/i, '').trim() || "The Reserve Bank of India (RBI) has announced new guidelines for digital lending platforms to ensure greater transparency and customer protection. Lenders must now provide a Key Fact Statement (KFS) to borrowers before the execution of the contract. Any fees payable to the lending service provider must be paid directly by the regulated entity, not the borrower.";
      
      try {
        const summary = await summarize(textToSummarize);
        
        setIsTyping(false);
        setMessages(prev => [...prev, {
          id: (Date.now() + 1).toString(),
          role: 'assistant',
          content: `Here is the BART AI Summary:\n\n${summary}`,
          reasoning: ['Running BART summarization model locally', 'Extracting key facts'],
        }]);
        return;
      } catch (e) {
        console.error(e);
      }
    }

    // Simulate AI response for standard queries
    setTimeout(() => {
      setIsTyping(false);
      const aiResponseId = (Date.now() + 1).toString();
      
      let newResponse: Message;
      
      if (text.toLowerCase().includes('lic expire')) {
        newResponse = {
          id: aiResponseId,
          role: 'assistant',
          content: 'Your LIC Term Plan policy (LIC/B123/45678) expires in **24 days**.\n\nPremium Due: 30 June 2026\nNominee: Priya Sharma\nRecommended Action: Renew before 20 June.',
          isStreaming: true,
          reasoning: ['Searching LIC document registry', 'Extracting expiry date', 'Calculating days remaining'],
          sources: ['LIC Policy (doc-lic-001)'],
          suggestions: ['Pay premium now', 'Remind me in 10 days']
        };
      } else {
        newResponse = {
          id: aiResponseId,
          role: 'assistant',
          content: 'I have checked your family knowledge graph. Let me know if you need specific details about your documents, deadlines, or scheme eligibility.',
          isStreaming: true,
          reasoning: ['Processing query', 'Querying family knowledge graph'],
          suggestions: ['Show financial summary', 'Check missing documents']
        };
      }
      
      setMessages(prev => [...prev, newResponse]);
      
      // Simulate streaming completion
      setTimeout(() => {
        setMessages(prev => prev.map(m => m.id === aiResponseId ? { ...m, isStreaming: false } : m));
      }, 1500);

    }, 2000);
  };

  return (
    <div className="flex flex-col h-[600px] max-h-screen bg-nidhi-card border border-nidhi-border-subtle rounded-3xl overflow-hidden shadow-2xl">
      {/* Header */}
      <div className="p-4 border-b border-nidhi-border-subtle bg-nidhi-surface/50 backdrop-blur flex items-center justify-between">
        <div className="flex items-center gap-3">
          <div className="w-10 h-10 rounded-full bg-nidhi-gold/20 flex items-center justify-center border border-nidhi-gold/30">
            <Bot className="w-5 h-5 text-nidhi-gold" />
          </div>
          <div>
            <h2 className="text-lg font-display font-semibold text-nidhi-text flex items-center gap-2">
              NIDHI AI <span className="flex w-2 h-2 rounded-full bg-nidhi-success"></span>
            </h2>
            <p className="text-xs text-nidhi-text-secondary">Connected to your family graph</p>
          </div>
        </div>
        {!isReady && bartProgress && (
          <div className="flex items-center gap-2 text-xs text-nidhi-text-muted">
            <Loader2 className="w-3 h-3 animate-spin" />
            Loading BART ({Math.round(bartProgress.progress)}%)
          </div>
        )}
      </div>

      {/* Chat Area */}
      <div className="flex-1 overflow-y-auto p-4 space-y-6">
        <AnimatePresence>
          {messages.map((msg) => (
            <motion.div
              key={msg.id}
              initial={{ opacity: 0, y: 10 }}
              animate={{ opacity: 1, y: 0 }}
              className={`flex flex-col ${msg.role === 'user' ? 'items-end' : 'items-start'}`}
            >
              <div className={`flex items-end gap-2 max-w-[85%] ${msg.role === 'user' ? 'flex-row-reverse' : 'flex-row'}`}>
                <div className={`w-8 h-8 rounded-full flex items-center justify-center flex-shrink-0 ${msg.role === 'user' ? 'bg-nidhi-surface border border-nidhi-border' : 'bg-nidhi-gold/20 border border-nidhi-gold/30'}`}>
                  {msg.role === 'user' ? <User className="w-4 h-4 text-nidhi-text-secondary" /> : <Sparkles className="w-4 h-4 text-nidhi-gold" />}
                </div>

                <div className="space-y-2">
                  {/* Reasoning Block */}
                  {msg.reasoning && (
                    <div className="bg-nidhi-surface/80 border border-nidhi-border rounded-lg p-3 text-xs text-nidhi-text-muted space-y-1">
                      <div className="flex items-center gap-1.5 font-medium mb-2">
                        <Search className="w-3.5 h-3.5" />
                        NIDHI is thinking...
                      </div>
                      {msg.reasoning.map((r, i) => (
                        <div key={i} className="flex items-center gap-2">
                          <CheckCircle2 className="w-3 h-3 text-nidhi-success/70" />
                          <span>{r}</span>
                        </div>
                      ))}
                    </div>
                  )}

                  {/* Main Bubble */}
                  <div className={`p-4 text-sm ${msg.role === 'user' ? 'bg-nidhi-gold/10 border border-nidhi-gold/20 text-nidhi-text rounded-[20px_20px_4px_20px]' : 'bg-nidhi-surface border border-nidhi-border-subtle text-nidhi-text rounded-[20px_20px_20px_4px]'}`}>
                    <div className="whitespace-pre-wrap leading-relaxed">
                      {msg.content}
                      {msg.isStreaming && <span className="inline-block w-1.5 h-4 ml-1 bg-nidhi-gold animate-pulse align-middle" />}
                    </div>
                  </div>

                  {/* Sources */}
                  {msg.sources && !msg.isStreaming && (
                    <div className="flex flex-wrap gap-2 mt-2">
                      {msg.sources.map((source, i) => (
                        <span key={i} className="inline-flex items-center gap-1.5 px-2.5 py-1 rounded bg-nidhi-surface border border-nidhi-border text-[11px] text-nidhi-text-secondary font-medium">
                          <FileText className="w-3 h-3" />
                          {source}
                        </span>
                      ))}
                    </div>
                  )}

                  {/* Suggestions */}
                  {msg.suggestions && !msg.isStreaming && (
                    <div className="flex flex-wrap gap-2 mt-3">
                      {msg.suggestions.map((suggestion, i) => (
                        <button
                          key={i}
                          onClick={() => handleSend(suggestion)}
                          className="px-3 py-1.5 rounded-full border border-nidhi-gold/30 bg-nidhi-gold/5 hover:bg-nidhi-gold/10 text-nidhi-gold text-xs font-medium transition-colors"
                        >
                          {suggestion}
                        </button>
                      ))}
                    </div>
                  )}
                </div>
              </div>
            </motion.div>
          ))}
          
          {isTyping && (
            <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }} className="flex items-end gap-2 max-w-[80%]">
               <div className="w-8 h-8 rounded-full bg-nidhi-gold/20 flex items-center justify-center border border-nidhi-gold/30">
                  <Bot className="w-4 h-4 text-nidhi-gold" />
                </div>
                <div className="bg-nidhi-surface border border-nidhi-border-subtle rounded-[20px_20px_20px_4px] p-4 flex gap-1 items-center h-[52px]">
                  <motion.div animate={{ y: [0, -4, 0] }} transition={{ repeat: Infinity, duration: 0.8, delay: 0 }} className="w-1.5 h-1.5 bg-nidhi-text-muted rounded-full" />
                  <motion.div animate={{ y: [0, -4, 0] }} transition={{ repeat: Infinity, duration: 0.8, delay: 0.2 }} className="w-1.5 h-1.5 bg-nidhi-text-muted rounded-full" />
                  <motion.div animate={{ y: [0, -4, 0] }} transition={{ repeat: Infinity, duration: 0.8, delay: 0.4 }} className="w-1.5 h-1.5 bg-nidhi-text-muted rounded-full" />
                </div>
            </motion.div>
          )}
        </AnimatePresence>
        <div ref={endOfMessagesRef} />
      </div>

      {/* Input Area */}
      <div className="p-4 bg-nidhi-surface/80 border-t border-nidhi-border-subtle">
        <div className="relative flex items-center">
          <input
            type="text"
            value={input}
            onChange={(e) => setInput(e.target.value)}
            onKeyDown={(e) => e.key === 'Enter' && handleSend()}
            placeholder="Ask about documents, schemes, or deadlines..."
            className="w-full bg-nidhi-black border border-nidhi-border rounded-xl py-3 pl-4 pr-12 text-sm text-nidhi-text placeholder:text-nidhi-text-muted focus:outline-none focus:border-nidhi-gold/50 focus:ring-1 focus:ring-nidhi-gold/50 transition-all"
          />
          <button
            onClick={() => handleSend()}
            disabled={!input.trim() || isTyping}
            className="absolute right-2 p-2 bg-nidhi-gold text-nidhi-black rounded-lg disabled:opacity-50 disabled:cursor-not-allowed hover:bg-nidhi-gold-light transition-colors"
          >
            <Send className="w-4 h-4" />
          </button>
        </div>
      </div>
    </div>
  );
}

// Temporary icon to avoid import errors since we used it in reasoning block
function CheckCircle2(props: any) {
  return <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" {...props}><path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/><polyline points="22 4 12 14.01 9 11.01"/></svg>
}
