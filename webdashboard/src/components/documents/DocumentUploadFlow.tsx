'use client';

import React, { useState, useEffect } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { FileUp, Search, Cpu, CheckCircle, Database, AlertTriangle, ShieldCheck, X, FileText } from 'lucide-react';
import { useBart } from '@/lib/hooks/useBart';

interface DocumentUploadFlowProps {
  onClose: () => void;
  onComplete: () => void;
}

const steps = [
  { id: 'preprocess', label: 'Image Preprocessing', icon: Search, duration: 1500 },
  { id: 'enhance', label: 'Resolution Enhancement', icon: Search, duration: 1200 },
  { id: 'ocr', label: 'OCR Extraction', icon: Cpu, duration: 2000 },
  { id: 'detect', label: 'Field Detection', icon: Search, duration: 1000 },
  { id: 'classify', label: 'Document Classification', icon: Database, duration: 1500 },
  { id: 'verify', label: 'Data Verification', icon: ShieldCheck, duration: 1800 },
  { id: 'analyze', label: 'BART AI Summarization', icon: FileText, duration: 0 }, // Duration 0 means it waits for actual completion
  { id: 'link', label: 'Knowledge Graph Linking', icon: CheckCircle, duration: 1500 },
];

export function DocumentUploadFlow({ onClose, onComplete }: DocumentUploadFlowProps) {
  const [currentStepIndex, setCurrentStepIndex] = useState(-1);
  const [isUploading, setIsUploading] = useState(false);
  const [progress, setProgress] = useState(0);
  const [summary, setSummary] = useState('');
  const { summarize, isReady, progress: bartProgress } = useBart();

  const startSimulation = () => {
    setIsUploading(true);
    setCurrentStepIndex(0);
  };

  // Simulation loop
  useEffect(() => {
    if (!isUploading || currentStepIndex < 0) return;

    if (currentStepIndex >= steps.length) {
      // Completed all steps
      setTimeout(() => {
        onComplete();
      }, 1000);
      return;
    }

    const currentStepId = steps[currentStepIndex].id;
    const currentStepDuration = steps[currentStepIndex].duration;
    
    if (currentStepId === 'analyze') {
      if (!isReady && bartProgress) {
        // Model is still loading
        setProgress(bartProgress.progress);
        return;
      }
      
      const runSummarization = async () => {
        try {
          const fakeOcrText = "Life Insurance Corporation of India. Policy Number: 2847509. Name: Rajesh Sharma. Plan Name: Jeevan Anand. Date of Commencement: 22/06/2016. Sum Assured: Rs 50,00,000. Premium Amount: Rs 15,200. Mode of Payment: Yearly. Date of Maturity: 22/06/2036. Nominee Name: Priya Sharma (Wife). This policy covers natural death, accidental death, and provides a maturity benefit. Ensure timely payment of premiums to keep the policy in force.";
          setProgress(50);
          const result = await summarize(fakeOcrText);
          setSummary(result);
          setProgress(100);
          setTimeout(() => {
            setCurrentStepIndex(prev => prev + 1);
            setProgress(0);
          }, 500);
        } catch (e) {
          console.error('BART Error', e);
          setSummary('Error generating summary');
          setCurrentStepIndex(prev => prev + 1);
        }
      };
      
      runSummarization();
      return;
    }

    // Animate progress bar for the current step
    let startTime = Date.now();
    const interval = setInterval(() => {
      const elapsed = Date.now() - startTime;
      const stepProgress = Math.min((elapsed / currentStepDuration) * 100, 100);
      setProgress(stepProgress);
      
      if (stepProgress >= 100) {
        clearInterval(interval);
        setTimeout(() => {
          setCurrentStepIndex(prev => prev + 1);
          setProgress(0);
        }, 300);
      }
    }, 50);

    return () => clearInterval(interval);
  }, [currentStepIndex, isUploading, onComplete]);

  return (
    <div className="fixed inset-0 z-50 bg-nidhi-black/80 backdrop-blur-md flex items-center justify-center p-4">
      <motion.div
        initial={{ opacity: 0, scale: 0.95, y: 20 }}
        animate={{ opacity: 1, scale: 1, y: 0 }}
        className="w-full max-w-xl bg-nidhi-card border border-nidhi-border rounded-3xl overflow-hidden shadow-2xl relative"
      >
        <div className="absolute top-4 right-4 z-10">
          <button onClick={onClose} className="p-2 rounded-full hover:bg-nidhi-surface text-nidhi-text-secondary transition-colors">
            <X className="w-5 h-5" />
          </button>
        </div>

        <div className="p-8">
          <h2 className="text-2xl font-display font-bold text-nidhi-text mb-2">Upload Document</h2>
          <p className="text-nidhi-text-secondary text-sm mb-8">
            NIDHI AI will automatically classify, extract, and link document data to your family graph.
          </p>

          {!isUploading ? (
            <div 
              onClick={startSimulation}
              className="border-2 border-dashed border-nidhi-border hover:border-nidhi-gold/50 rounded-2xl p-12 flex flex-col items-center justify-center cursor-pointer transition-all bg-nidhi-surface/30 hover:bg-nidhi-surface/80 group"
            >
              <div className="w-16 h-16 rounded-full bg-nidhi-elevated flex items-center justify-center mb-4 group-hover:scale-110 transition-transform">
                <FileUp className="w-8 h-8 text-nidhi-gold" />
              </div>
              <h3 className="font-semibold text-nidhi-text mb-1">Click or drag file to upload</h3>
              <p className="text-xs text-nidhi-text-muted text-center max-w-xs">
                Supports Aadhaar, PAN, LIC Policies, Insurance, and standard PDFs/JPEGs up to 10MB
              </p>
            </div>
          ) : (
            <div className="space-y-6">
              {/* Fake document preview card */}
              <div className="bg-nidhi-surface rounded-xl p-4 flex gap-4 items-center border border-nidhi-border-subtle">
                <div className="w-12 h-16 bg-nidhi-elevated rounded flex items-center justify-center border border-nidhi-border">
                  <FileUp className="w-6 h-6 text-nidhi-text-muted" />
                </div>
                <div className="flex-1">
                  <h4 className="text-sm font-medium text-nidhi-text">document_scan_new.pdf</h4>
                  <p className="text-xs text-nidhi-text-muted mt-1">2.4 MB • Analyzing with NIDHI Vision...</p>
                </div>
              </div>

              {/* Progress Steps */}
              <div className="bg-nidhi-surface/50 border border-nidhi-border-subtle rounded-xl p-6">
                <div className="space-y-4">
                  {steps.map((step, index) => {
                    const isCompleted = index < currentStepIndex;
                    const isActive = index === currentStepIndex;
                    const isUpcoming = index > currentStepIndex;
                    const StepIcon = step.icon;

                    return (
                      <div key={step.id} className={`flex items-start gap-4 transition-opacity duration-300 ${isUpcoming ? 'opacity-30' : 'opacity-100'}`}>
                        <div className="mt-0.5">
                          {isCompleted ? (
                            <CheckCircle className="w-5 h-5 text-nidhi-success" />
                          ) : isActive ? (
                            <div className="relative w-5 h-5 flex items-center justify-center">
                              <div className="absolute inset-0 rounded-full border-2 border-nidhi-gold/20"></div>
                              <div className="absolute inset-0 rounded-full border-2 border-nidhi-gold border-t-transparent animate-spin"></div>
                            </div>
                          ) : (
                            <div className="w-5 h-5 rounded-full border-2 border-nidhi-border flex items-center justify-center">
                              <div className="w-1.5 h-1.5 rounded-full bg-nidhi-border"></div>
                            </div>
                          )}
                        </div>
                        <div className="flex-1">
                          <h4 className={`text-sm font-medium ${isActive ? 'text-nidhi-gold' : isCompleted ? 'text-nidhi-text' : 'text-nidhi-text-muted'}`}>
                            {step.label}
                          </h4>
                          
                          <AnimatePresence>
                            {isActive && (
                              <motion.div
                                initial={{ height: 0, opacity: 0 }}
                                animate={{ height: 'auto', opacity: 1 }}
                                exit={{ height: 0, opacity: 0 }}
                                className="overflow-hidden mt-2"
                              >
                                <div className="w-full bg-nidhi-border rounded-full h-1.5 mb-1 overflow-hidden">
                                  <div 
                                    className="bg-nidhi-gold h-full rounded-full transition-all duration-75"
                                    style={{ width: `${progress}%` }}
                                  />
                                </div>
                                {step.id === 'classify' && progress > 50 && (
                                  <p className="text-xs text-nidhi-success mt-2 flex items-center gap-1">
                                    <CheckCircle className="w-3 h-3" /> Identified as LIC Policy Document
                                  </p>
                                )}
                                {step.id === 'verify' && progress > 50 && (
                                  <p className="text-xs text-nidhi-success mt-2 flex items-center gap-1">
                                    <ShieldCheck className="w-3 h-3" /> Verified against Rajesh Sharma's profile
                                  </p>
                                )}
                                {step.id === 'analyze' && summary && (
                                  <div className="mt-3 p-3 bg-nidhi-black/50 border border-nidhi-gold/20 rounded-lg">
                                    <p className="text-xs font-semibold text-nidhi-gold mb-1">BART AI Summary:</p>
                                    <p className="text-xs text-nidhi-text-secondary leading-relaxed">{summary}</p>
                                  </div>
                                )}
                                {step.id === 'analyze' && !isReady && bartProgress && (
                                  <p className="text-[10px] text-nidhi-text-muted mt-2">
                                    Loading BART model ({Math.round(bartProgress.progress)}%)...
                                  </p>
                                )}
                              </motion.div>
                            )}
                          </AnimatePresence>
                        </div>
                      </div>
                    );
                  })}
                </div>
              </div>

              {currentStepIndex >= steps.length && (
                <motion.div 
                  initial={{ opacity: 0, y: 10 }}
                  animate={{ opacity: 1, y: 0 }}
                  className="bg-nidhi-success/10 border border-nidhi-success/20 rounded-xl p-4 flex gap-3 items-center"
                >
                  <CheckCircle className="w-5 h-5 text-nidhi-success flex-shrink-0" />
                  <div>
                    <h4 className="text-sm font-semibold text-nidhi-success">Document Processed Successfully</h4>
                    <p className="text-xs text-nidhi-success/80 mt-1">1 upcoming deadline added. 2 missing fields autofilled.</p>
                  </div>
                </motion.div>
              )}
            </div>
          )}
        </div>
      </motion.div>
    </div>
  );
}
