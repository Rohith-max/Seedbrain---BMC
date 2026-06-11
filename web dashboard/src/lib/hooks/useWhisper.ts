'use client';

import { useState, useCallback, useRef, useEffect } from 'react';

export type WhisperState = 'idle' | 'recording' | 'processing' | 'error';

export interface UseWhisperReturn {
  state: WhisperState;
  transcript: string;
  detectedLanguage: string;
  isSupported: boolean;
  startRecording: (languageCode?: string) => Promise<void>;
  stopRecording: () => void;
  error: string | null;
}

/**
 * useWhisper — records audio via MediaRecorder, sends to /api/whisper,
 * returns the transcript and OpenAI-detected language code.
 *
 * The detected language is the raw string Whisper returns, e.g.
 * "hindi", "english", "tamil", "telugu", "kannada", etc.
 */
export function useWhisper(): UseWhisperReturn {
  const [state, setState] = useState<WhisperState>('idle');
  const [transcript, setTranscript] = useState('');
  const [detectedLanguage, setDetectedLanguage] = useState('english');
  const [error, setError] = useState<string | null>(null);
  // Always start false on both server and client — set in useEffect to avoid hydration mismatch
  const [isSupported, setIsSupported] = useState(false);

  const mediaRecorderRef = useRef<MediaRecorder | null>(null);
  const chunksRef = useRef<Blob[]>([]);
  const streamRef = useRef<MediaStream | null>(null);

  // Detect support on the client only (after mount) to avoid SSR hydration mismatch
  useEffect(() => {
    setIsSupported(!!navigator.mediaDevices?.getUserMedia);
  }, []);

  const startRecording = useCallback(async (languageCode?: string) => {
    setError(null);
    setTranscript('');

    try {
      const stream = await navigator.mediaDevices.getUserMedia({ audio: true });
      streamRef.current = stream;

      // Prefer webm/opus for broad browser support; fall back gracefully
      const mimeType = MediaRecorder.isTypeSupported('audio/webm;codecs=opus')
        ? 'audio/webm;codecs=opus'
        : MediaRecorder.isTypeSupported('audio/webm')
        ? 'audio/webm'
        : 'audio/mp4';

      const recorder = new MediaRecorder(stream, { mimeType });
      mediaRecorderRef.current = recorder;
      chunksRef.current = [];

      recorder.ondataavailable = (e) => {
        if (e.data.size > 0) chunksRef.current.push(e.data);
      };

      recorder.onstop = async () => {
        // Stop all tracks to release the microphone
        streamRef.current?.getTracks().forEach((t) => t.stop());

        const blob = new Blob(chunksRef.current, { type: mimeType });

        if (blob.size < 500) {
          // Too small — probably silence
          setState('idle');
          return;
        }

        setState('processing');

        try {
          const ext = mimeType.includes('mp4') ? 'mp4' : 'webm';
          const formData = new FormData();
          formData.append('audio', blob, `recording.${ext}`);
          // Send selected language so Whisper skips auto-detection
          if (languageCode) {
            formData.append('language', languageCode);
          }

          const res = await fetch('/api/whisper', {
            method: 'POST',
            body: formData,
          });

          if (!res.ok) {
            const json = await res.json().catch(() => ({}));
            throw new Error(json.error || `HTTP ${res.status}`);
          }

          const { transcript: text, language } = await res.json();
          setTranscript(text || '');
          setDetectedLanguage(language || 'english');
          setState('idle');
        } catch (err: unknown) {
          const msg = err instanceof Error ? err.message : 'Transcription failed';
          setError(msg);
          setState('error');
          setTimeout(() => setState('idle'), 4000);
        }
      };

      recorder.start();
      setState('recording');
    } catch (err: unknown) {
      const msg =
        err instanceof Error && err.name === 'NotAllowedError'
          ? 'Microphone permission denied'
          : 'Could not access microphone';
      setError(msg);
      setState('error');
      setIsSupported(false);
      setTimeout(() => setState('idle'), 4000);
    }
  }, []);

  const stopRecording = useCallback(() => {
    if (mediaRecorderRef.current && mediaRecorderRef.current.state !== 'inactive') {
      mediaRecorderRef.current.stop();
    }
  }, []);

  return {
    state,
    transcript,
    detectedLanguage,
    isSupported,
    startRecording,
    stopRecording,
    error,
  };
}
