'use client';

import React, { useEffect } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { Mic, X, Loader2, Volume2, Globe } from 'lucide-react';
import { useVoice, VoiceState } from '@/lib/hooks/useVoice';

interface VoiceAssistantProps {
  isOpen: boolean;
  onClose: () => void;
  onCommand?: (text: string) => void;
}

export function VoiceAssistant({ isOpen, onClose, onCommand }: VoiceAssistantProps) {
  const { state, transcript, isSupported, startListening, stopListening, speak, stopSpeaking, setThinking } = useVoice({
    onTranscriptChange: (text, isFinal) => {
      if (isFinal && onCommand) {
        // Here we simulate the AI processing the command
        setTimeout(() => {
          onCommand(text);
          // Simulate AI responding
          speak(`I have received your command: ${text}. Processing now.`);
        }, 1000);
      }
    }
  });

  // Start listening automatically when opened
  useEffect(() => {
    if (isOpen && state === 'idle') {
      startListening();
    }
    if (!isOpen) {
      stopListening();
      stopSpeaking();
    }
  }, [isOpen, startListening, stopListening, stopSpeaking, state]);

  if (!isOpen) return null;

  return (
    <AnimatePresence>
      <motion.div
        initial={{ opacity: 0 }}
        animate={{ opacity: 1 }}
        exit={{ opacity: 0 }}
        className="fixed inset-0 z-50 flex items-end justify-center sm:items-center p-4 bg-nidhi-black/80 backdrop-blur-xl"
      >
        <motion.div
          initial={{ y: 100, opacity: 0, scale: 0.95 }}
          animate={{ y: 0, opacity: 1, scale: 1 }}
          exit={{ y: 100, opacity: 0, scale: 0.95 }}
          className="w-full max-w-md bg-nidhi-card border border-nidhi-border-subtle rounded-3xl p-8 relative overflow-hidden shadow-[0_0_100px_rgba(212,175,55,0.15)]"
        >
          {/* Close button */}
          <button 
            onClick={onClose}
            className="absolute top-6 right-6 p-2 rounded-full bg-nidhi-surface hover:bg-nidhi-elevated transition-colors"
          >
            <X className="w-5 h-5 text-nidhi-text-secondary" />
          </button>

          {/* Lang selector (visual only for demo) */}
          <div className="absolute top-6 left-6 flex items-center gap-2 px-3 py-1.5 rounded-full bg-nidhi-surface border border-nidhi-border text-xs text-nidhi-text-secondary font-medium">
            <Globe className="w-3.5 h-3.5" />
            English
          </div>

          <div className="flex flex-col items-center mt-12 mb-8">
            {/* Visualizer Circle */}
            <div className="relative w-32 h-32 flex items-center justify-center mb-8">
              {/* Background ambient glow */}
              <motion.div
                animate={{
                  scale: state === 'listening' ? [1, 1.2, 1] : state === 'speaking' ? [1, 1.5, 1] : 1,
                  opacity: state === 'idle' ? 0.2 : 0.8
                }}
                transition={{ duration: state === 'speaking' ? 1.5 : 2, repeat: Infinity }}
                className={`absolute inset-0 rounded-full blur-2xl ${
                  state === 'speaking' ? 'bg-nidhi-gold/40' :
                  state === 'listening' ? 'bg-blue-500/30' :
                  'bg-nidhi-surface'
                }`}
              />

              {/* Core button */}
              <motion.button
                onClick={() => state === 'listening' ? stopListening() : startListening()}
                whileHover={{ scale: 1.05 }}
                whileTap={{ scale: 0.95 }}
                className={`relative z-10 w-20 h-20 rounded-full flex items-center justify-center border-2 transition-colors ${
                  state === 'listening' ? 'border-blue-500 bg-blue-500/20 text-blue-400' :
                  state === 'speaking' ? 'border-nidhi-gold bg-nidhi-gold/20 text-nidhi-gold' :
                  state === 'thinking' ? 'border-purple-500 bg-purple-500/20 text-purple-400' :
                  'border-nidhi-border bg-nidhi-surface text-nidhi-text'
                }`}
              >
                {state === 'thinking' ? (
                  <Loader2 className="w-8 h-8 animate-spin" />
                ) : state === 'speaking' ? (
                  <Volume2 className="w-8 h-8" />
                ) : (
                  <Mic className="w-8 h-8" />
                )}
              </motion.button>
            </div>

            {/* Status text */}
            <h3 className="text-xl font-display font-medium text-nidhi-text mb-2">
              {state === 'listening' ? 'Listening...' :
               state === 'thinking' ? 'Thinking...' :
               state === 'speaking' ? 'Speaking...' :
               'Tap to speak'}
            </h3>
            
            {/* Transcript */}
            <p className="text-center text-nidhi-text-secondary min-h-[48px] max-w-[280px]">
              {transcript || (state === 'listening' ? 'Go ahead, I am listening.' : 'Say "Show my LIC policies"')}
            </p>
          </div>

          {/* Quick suggestions */}
          {state === 'idle' && (
            <div className="space-y-3 mt-8">
              <p className="text-xs font-semibold text-nidhi-text-muted uppercase tracking-wider text-center mb-4">Try saying</p>
              {['"Find Aarav\'s certificates"', '"When does my insurance expire?"', '"Do I qualify for any schemes?"'].map((cmd, i) => (
                <button 
                  key={i}
                  onClick={() => {
                    setThinking();
                    setTimeout(() => {
                      if(onCommand) onCommand(cmd.replace(/"/g, ''));
                      speak(`Let me check that for you.`);
                    }, 1500);
                  }}
                  className="w-full p-4 rounded-2xl bg-nidhi-surface border border-nidhi-border hover:border-nidhi-gold/30 hover:bg-nidhi-elevated transition-all text-sm text-nidhi-text-secondary text-left flex items-center justify-between group"
                >
                  <span>{cmd}</span>
                  <div className="w-6 h-6 rounded-full bg-nidhi-black flex items-center justify-center opacity-0 group-hover:opacity-100 transition-opacity">
                    <Mic className="w-3 h-3 text-nidhi-gold" />
                  </div>
                </button>
              ))}
            </div>
          )}
        </motion.div>
      </motion.div>
    </AnimatePresence>
  );
}
