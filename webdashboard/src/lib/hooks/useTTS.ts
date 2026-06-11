'use client';

import { useState, useCallback, useRef, useEffect } from 'react';

export type TTSState = 'idle' | 'speaking';

/** Supported languages and their BCP-47 locale codes for SpeechSynthesis */
export const SUPPORTED_LANGS = {
  english: { label: 'English', bcp47: 'en-IN', flag: '🇬🇧' },
  hindi:   { label: 'हिंदी',   bcp47: 'hi-IN', flag: '🇮🇳' },
  kannada: { label: 'ಕನ್ನಡ',  bcp47: 'kn-IN', flag: '🇮🇳' },
  tamil:   { label: 'தமிழ்',  bcp47: 'ta-IN', flag: '🇮🇳' },
} as const;

export type SupportedLang = keyof typeof SUPPORTED_LANGS;

export function useTTS() {
  const [ttsState, setTtsState] = useState<TTSState>('idle');
  const [isSupported, setIsSupported] = useState(false);
  const synthRef = useRef<SpeechSynthesis | null>(null);

  useEffect(() => {
    if (typeof window !== 'undefined' && window.speechSynthesis) {
      synthRef.current = window.speechSynthesis;
      setIsSupported(true);
      // Pre-load voices (some browsers need this trigger)
      window.speechSynthesis.getVoices();
    }
  }, []);

  const speak = useCallback((text: string, lang: SupportedLang = 'english') => {
    const synth = synthRef.current;
    if (!synth) return;

    // Cancel any ongoing speech first
    synth.cancel();

    // Strip markdown bold markers for cleaner speech
    const cleanText = text
      .replace(/\*\*([^*]+)\*\*/g, '$1')
      .replace(/\n\n/g, '. ')
      .replace(/\n/g, ', ')
      .trim();

    const bcp47 = SUPPORTED_LANGS[lang].bcp47;
    const utterance = new SpeechSynthesisUtterance(cleanText);
    utterance.lang = bcp47;
    utterance.rate = 1.0;
    utterance.pitch = 1.05;
    utterance.volume = 1.0;

    // Try to find the best matching voice
    const voices = synth.getVoices();
    const exactMatch = voices.find(v => v.lang === bcp47);
    const partialMatch = voices.find(v => v.lang.startsWith(bcp47.split('-')[0]));
    if (exactMatch) utterance.voice = exactMatch;
    else if (partialMatch) utterance.voice = partialMatch;

    utterance.onstart = () => setTtsState('speaking');
    utterance.onend = () => setTtsState('idle');
    utterance.onerror = () => setTtsState('idle');

    setTtsState('speaking');
    synth.speak(utterance);
  }, []);

  const stopSpeaking = useCallback(() => {
    synthRef.current?.cancel();
    setTtsState('idle');
  }, []);

  return { ttsState, isSupported, speak, stopSpeaking };
}
