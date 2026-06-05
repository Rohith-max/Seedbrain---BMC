'use client';

import { useState, useEffect, useCallback, useRef } from 'react';

// Extended window interface to include speech recognition types
declare global {
  interface Window {
    SpeechRecognition: any;
    webkitSpeechRecognition: any;
  }
}

export type VoiceState = 'idle' | 'listening' | 'thinking' | 'speaking' | 'error';

export interface UseVoiceOptions {
  onTranscriptChange?: (text: string, isFinal: boolean) => void;
  onStateChange?: (state: VoiceState) => void;
  defaultLang?: string;
}

export function useVoice({ onTranscriptChange, onStateChange, defaultLang = 'en-IN' }: UseVoiceOptions = {}) {
  const [state, setStateInternal] = useState<VoiceState>('idle');
  const [transcript, setTranscript] = useState('');
  const [isSupported, setIsSupported] = useState(false);
  
  const recognitionRef = useRef<any>(null);
  const synthesisRef = useRef<SpeechSynthesis | null>(null);
  
  const setState = useCallback((newState: VoiceState) => {
    setStateInternal(newState);
    onStateChange?.(newState);
  }, [onStateChange]);

  // Initialize Web Speech APIs
  useEffect(() => {
    if (typeof window !== 'undefined') {
      const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition;
      
      if (SpeechRecognition && window.speechSynthesis) {
        setIsSupported(true);
        synthesisRef.current = window.speechSynthesis;
        
        recognitionRef.current = new SpeechRecognition();
        recognitionRef.current.continuous = true;
        recognitionRef.current.interimResults = true;
        recognitionRef.current.lang = defaultLang;
        
        recognitionRef.current.onstart = () => {
          setState('listening');
        };
        
        recognitionRef.current.onresult = (event: any) => {
          let currentTranscript = '';
          let isFinal = false;
          
          for (let i = event.resultIndex; i < event.results.length; ++i) {
            currentTranscript += event.results[i][0].transcript;
            if (event.results[i].isFinal) {
              isFinal = true;
            }
          }
          
          setTranscript(currentTranscript);
          onTranscriptChange?.(currentTranscript, isFinal);
          
          // If it's final, we automatically stop listening and move to thinking
          if (isFinal) {
            recognitionRef.current?.stop();
            setState('thinking');
          }
        };
        
        recognitionRef.current.onerror = (event: any) => {
          console.error('Speech recognition error', event.error);
          setState('error');
          setTimeout(() => setState('idle'), 3000);
        };
        
        recognitionRef.current.onend = () => {
          // If we were listening but haven't transitioned to thinking (e.g. user stopped manually)
          setStateInternal(prev => prev === 'listening' ? 'idle' : prev);
        };
      }
    }
    
    return () => {
      if (recognitionRef.current) {
        recognitionRef.current.stop();
      }
      if (synthesisRef.current) {
        synthesisRef.current.cancel();
      }
    };
  }, [defaultLang, setState, onTranscriptChange]);

  const startListening = useCallback((lang: string = defaultLang) => {
    if (!isSupported || !recognitionRef.current) return;
    
    try {
      // Stop synthesis if currently speaking
      if (synthesisRef.current && synthesisRef.current.speaking) {
        synthesisRef.current.cancel();
      }
      
      recognitionRef.current.lang = lang;
      setTranscript('');
      recognitionRef.current.start();
    } catch (error) {
      console.error('Failed to start listening:', error);
      // Sometimes it throws if already started
      recognitionRef.current.stop();
      setTimeout(() => {
        try { recognitionRef.current.start(); } catch (e) {}
      }, 100);
    }
  }, [isSupported, defaultLang]);

  const stopListening = useCallback(() => {
    if (recognitionRef.current) {
      recognitionRef.current.stop();
      setState('idle');
    }
  }, [setState]);

  const speak = useCallback((text: string, lang: string = defaultLang) => {
    if (!isSupported || !synthesisRef.current) return;
    
    // Cancel any ongoing speech
    synthesisRef.current.cancel();
    
    setState('speaking');
    
    const utterance = new SpeechSynthesisUtterance(text);
    utterance.lang = lang;
    
    // Try to find a good native voice or fallback
    const voices = synthesisRef.current.getVoices();
    const voice = voices.find(v => v.lang === lang) || voices.find(v => v.lang.startsWith(lang.split('-')[0]));
    if (voice) {
      utterance.voice = voice;
    }
    
    // Adjust properties for a more premium, natural feel
    utterance.rate = 1.0;
    utterance.pitch = 1.0;
    
    utterance.onend = () => {
      setState('idle');
    };
    
    utterance.onerror = () => {
      setState('idle');
    };
    
    synthesisRef.current.speak(utterance);
  }, [isSupported, defaultLang, setState]);

  const stopSpeaking = useCallback(() => {
    if (synthesisRef.current) {
      synthesisRef.current.cancel();
      setState('idle');
    }
  }, [setState]);

  // Manually set thinking state (useful for when waiting for AI API response)
  const setThinking = useCallback(() => {
    setState('thinking');
  }, [setState]);

  return {
    state,
    transcript,
    isSupported,
    startListening,
    stopListening,
    speak,
    stopSpeaking,
    setThinking
  };
}
