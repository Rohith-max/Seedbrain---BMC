'use client';

import React, { useState, useRef, useEffect } from 'react';
import {
  Search,
  Mic,
  Sparkles,
  FileText,
  Users,
  Gift,
  AlertCircle,
  TrendingUp,
  X,
} from 'lucide-react';
import { motion, AnimatePresence } from 'framer-motion';

const SEARCH_SUGGESTIONS = [
  { icon: FileText, label: 'Find all LIC policies', category: 'Search' },
  { icon: AlertCircle, label: 'Show pending EMIs', category: 'Search' },
  { icon: Gift, label: 'Eligible schemes', category: 'Benefits' },
  { icon: Users, label: 'Family members', category: 'People' },
  { icon: TrendingUp, label: 'Financial overview', category: 'Analytics' },
];

export function GlobalSearch() {
  const [isOpen, setIsOpen] = useState(false);
  const [query, setQuery] = useState('');
  const [isMicActive, setIsMicActive] = useState(false);
  const inputRef = useRef<HTMLInputElement>(null);

  useEffect(() => {
    const handleKeyDown = (e: KeyboardEvent) => {
      if ((e.metaKey || e.ctrlKey) && e.key === 'k') {
        e.preventDefault();
        setIsOpen(!isOpen);
        setTimeout(() => inputRef.current?.focus(), 100);
      }
      if (e.key === 'Escape') setIsOpen(false);
    };

    window.addEventListener('keydown', handleKeyDown);
    return () => window.removeEventListener('keydown', handleKeyDown);
  }, [isOpen]);

  return (
    <>
      {/* Search Trigger Button */}
      <motion.button
        whileHover={{ scale: 1.02 }}
        whileTap={{ scale: 0.98 }}
        onClick={() => {
          setIsOpen(true);
          setTimeout(() => inputRef.current?.focus(), 100);
        }}
        className="hidden md:flex items-center gap-3 px-4 h-11 rounded-xl glass-strong hover:glass-premium transition-all border border-nidhi-border"
      >
        <Search className="w-4 h-4 text-nidhi-text-secondary" />
        <span className="text-sm text-nidhi-text-secondary">
          Search intelligence...
        </span>
        <kbd className="ml-auto text-xs text-nidhi-text-muted bg-nidhi-elevated/60 px-2 py-1 rounded border border-nidhi-border">
          ⌘K
        </kbd>
      </motion.button>

      {/* Mobile Search Button */}
      <button
        onClick={() => setIsOpen(true)}
        className="md:hidden p-2 rounded-lg hover:bg-nidhi-surface transition-colors"
      >
        <Search className="w-5 h-5 text-nidhi-text-secondary" />
      </button>

      {/* Search Modal */}
      <AnimatePresence>
        {isOpen && (
          <>
            {/* Backdrop */}
            <motion.div
              initial={{ opacity: 0 }}
              animate={{ opacity: 1 }}
              exit={{ opacity: 0 }}
              onClick={() => setIsOpen(false)}
              className="fixed inset-0 z-40 bg-black/20 backdrop-blur-sm"
            />

            {/* Search Panel */}
            <motion.div
              initial={{ opacity: 0, scale: 0.95, y: -20 }}
              animate={{ opacity: 1, scale: 1, y: 0 }}
              exit={{ opacity: 0, scale: 0.95, y: -20 }}
              transition={{ duration: 0.2 }}
              className="fixed top-20 left-1/2 -translate-x-1/2 w-full max-w-2xl z-50"
            >
              <div className="glass-premium rounded-2xl overflow-hidden shadow-2xl border border-nidhi-gold/20">
                {/* Search Header */}
                <div className="p-6 border-b border-nidhi-border/40 flex items-center gap-3">
                  <Search className="w-5 h-5 text-nidhi-gold" />
                  <input
                    ref={inputRef}
                    type="text"
                    value={query}
                    onChange={(e) => setQuery(e.target.value)}
                    placeholder="Search documents, benefits, people, alerts..."
                    className="flex-1 bg-transparent text-lg text-nidhi-text outline-none placeholder:text-nidhi-text-muted"
                  />
                  <motion.button
                    whileHover={{ rotate: 90 }}
                    whileTap={{ scale: 0.9 }}
                    onClick={() => {
                      setQuery('');
                      setIsOpen(false);
                    }}
                    className="text-nidhi-text-secondary hover:text-nidhi-text"
                  >
                    <X className="w-5 h-5" />
                  </motion.button>
                </div>

                {/* AI Suggestions or Results */}
                <div className="max-h-96 overflow-y-auto">
                  {query === '' ? (
                    <>
                      {/* AI Suggestions */}
                      <div className="p-6">
                        <div className="flex items-center gap-2 mb-4">
                          <Sparkles className="w-4 h-4 text-nidhi-gold" />
                          <span className="text-xs font-semibold text-nidhi-text-secondary uppercase tracking-wider">
                            AI Suggestions
                          </span>
                        </div>

                        <div className="space-y-2">
                          {SEARCH_SUGGESTIONS.map((suggestion, idx) => {
                            const Icon = suggestion.icon;
                            return (
                              <motion.button
                                key={idx}
                                initial={{ opacity: 0, x: -10 }}
                                animate={{ opacity: 1, x: 0 }}
                                transition={{ delay: idx * 0.05 }}
                                whileHover={{ x: 4, scale: 1.01 }}
                                onClick={() => setQuery(suggestion.label)}
                                className="w-full flex items-center gap-3 p-3 rounded-lg hover:bg-nidhi-surface/60 transition-colors text-left"
                              >
                                <Icon className="w-4 h-4 text-nidhi-gold flex-shrink-0" />
                                <div className="flex-1 min-w-0">
                                  <div className="text-sm text-nidhi-text truncate">
                                    {suggestion.label}
                                  </div>
                                  <div className="text-xs text-nidhi-text-muted">
                                    {suggestion.category}
                                  </div>
                                </div>
                                <TrendingUp className="w-3 h-3 text-nidhi-text-muted flex-shrink-0" />
                              </motion.button>
                            );
                          })}
                        </div>
                      </div>
                    </>
                  ) : (
                    <>
                      {/* Search Results Placeholder */}
                      <div className="p-6 text-center">
                        <motion.div
                          initial={{ opacity: 0, scale: 0.8 }}
                          animate={{ opacity: 1, scale: 1 }}
                          className="mb-4"
                        >
                          <Sparkles className="w-8 h-8 text-nidhi-gold/40 mx-auto" />
                        </motion.div>
                        <p className="text-sm text-nidhi-text-muted">
                          Searching for "{query}"...
                        </p>
                      </div>
                    </>
                  )}
                </div>

                {/* Footer */}
                <div className="p-4 border-t border-nidhi-border/40 flex items-center justify-between text-xs text-nidhi-text-muted">
                  <div className="flex items-center gap-2">
                    <Mic className="w-3 h-3" />
                    <span>Voice search available</span>
                  </div>
                  <span className="opacity-50">Press ESC to close</span>
                </div>
              </div>
            </motion.div>
          </>
        )}
      </AnimatePresence>
    </>
  );
}
