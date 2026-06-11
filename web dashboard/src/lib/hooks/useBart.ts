'use client';

import { useState, useEffect, useRef, useCallback } from 'react';

export interface BartProgress {
  status: 'progress';
  progress: {
    status: string;
    name: string;
    progress: number;
    loaded?: number;
    total?: number;
  };
}

export function useBart() {
  const [isReady, setIsReady] = useState(false);
  const [isSummarizing, setIsSummarizing] = useState(false);
  const [progress, setProgress] = useState<BartProgress['progress'] | null>(null);
  
  const workerRef = useRef<Worker | null>(null);
  const callbacksRef = useRef<{ [key: string]: { resolve: (val: string) => void, reject: (err: any) => void } }>({});

  useEffect(() => {
    if (typeof window !== 'undefined' && !workerRef.current) {
      // Initialize the worker
      workerRef.current = new Worker(new URL('../workers/bart.worker.ts', import.meta.url), {
        type: 'module'
      });

      workerRef.current.onmessage = (event) => {
        const { status, id, result, error, progress: progressData } = event.data;

        if (status === 'progress') {
          setProgress(progressData);
          if (progressData.status === 'ready') {
            setIsReady(true);
          }
        } else if (status === 'complete') {
          setIsReady(true);
          setProgress(null);
          if (id && callbacksRef.current[id]) {
            callbacksRef.current[id].resolve(result);
            delete callbacksRef.current[id];
          }
        } else if (status === 'error') {
          if (id && callbacksRef.current[id]) {
            callbacksRef.current[id].reject(new Error(error));
            delete callbacksRef.current[id];
          }
        }
      };
    }

    return () => {
      if (workerRef.current) {
        workerRef.current.terminate();
        workerRef.current = null;
      }
    };
  }, []);

  const summarize = useCallback((text: string): Promise<string> => {
    return new Promise((resolve, reject) => {
      if (!workerRef.current) {
        reject(new Error('Worker not initialized'));
        return;
      }

      setIsSummarizing(true);
      const id = Date.now().toString();
      
      // Store callbacks
      callbacksRef.current[id] = { 
        resolve: (val) => {
          setIsSummarizing(false);
          resolve(val);
        }, 
        reject: (err) => {
          setIsSummarizing(false);
          reject(err);
        }
      };

      workerRef.current.postMessage({
        action: 'summarize',
        text,
        id
      });
    });
  }, []);

  return {
    isReady,
    isSummarizing,
    progress,
    summarize
  };
}
