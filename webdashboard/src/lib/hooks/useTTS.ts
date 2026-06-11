import { useState, useCallback, useRef } from 'react';

export type TTSState = 'idle' | 'speaking';

/** Helper to play audio chunks sequentially via our Next.js API proxy */
const playCloudTTS = (text: string, langPrefix: string, onStart: () => void, onEnd: () => void, audioRef: React.MutableRefObject<HTMLAudioElement | null>) => {
  // Split text by punctuation or into chunks of max 150 chars
  const rawChunks = text.match(/[^.!?,\n]+[.!?,\n]+/g) || [text];
  const chunks: string[] = [];
  rawChunks.forEach(c => {
    if (c.length < 150) {
      chunks.push(c);
    } else {
      const words = c.split(' ');
      let current = '';
      words.forEach(w => {
        if (current.length + w.length > 150) {
          chunks.push(current);
          current = w + ' ';
        } else {
          current += w + ' ';
        }
      });
      if (current) chunks.push(current);
    }
  });

  let currentIndex = 0;
  const audio = new Audio();
  audioRef.current = audio;
  
  onStart();

  const playNext = () => {
    if (currentIndex >= chunks.length) {
      onEnd();
      return;
    }
    const chunk = chunks[currentIndex].trim();
    if (!chunk) {
      currentIndex++;
      playNext();
      return;
    }
    
    // Call our server proxy instead of Google directly to bypass CORS / blockers
    const url = `/api/tts?lang=${langPrefix}&text=${encodeURIComponent(chunk)}`;
    audio.src = url;
    audio.play().catch(e => {
      console.error('Cloud TTS playback failed', e);
      // Skip chunk if it fails to play
      currentIndex++;
      playNext();
    });
  };

  audio.onended = () => {
    currentIndex++;
    playNext();
  };

  audio.onerror = () => {
    currentIndex++;
    playNext();
  };

  playNext();
};

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
  const audioRef = useRef<HTMLAudioElement | null>(null);

  const speak = useCallback((text: string, lang: SupportedLang = 'english') => {
    // Cancel any ongoing speech first
    if (audioRef.current) {
      audioRef.current.pause();
      audioRef.current = null;
    }

    // Strip markdown bold markers for cleaner speech
    const cleanText = text
      .replace(/\*\*([^*]+)\*\*/g, '$1')
      .replace(/\n\n/g, '. ')
      .replace(/\n/g, ', ')
      .trim();

    const bcp47 = SUPPORTED_LANGS[lang].bcp47;
    const langPrefix = bcp47.split('-')[0];

    // Always use Cloud TTS for a guaranteed high-quality, correct-language female voice
    playCloudTTS(
      cleanText, 
      langPrefix, 
      () => setTtsState('speaking'), 
      () => setTtsState('idle'), 
      audioRef
    );
  }, []);

  const stopSpeaking = useCallback(() => {
    if (audioRef.current) {
      audioRef.current.pause();
      audioRef.current = null;
    }
    setTtsState('idle');
  }, []);

  return { ttsState, isSupported: true, speak, stopSpeaking };
}
