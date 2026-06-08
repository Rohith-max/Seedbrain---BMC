import React from 'react';
import { Sparkles, ArrowRight, type LucideIcon } from 'lucide-react';
import { motion } from 'framer-motion';

interface EmptyStateProps {
  icon?: LucideIcon;
  title: string;
  description: string;
  primaryAction?: {
    label: string;
    onClick: () => void;
  };
  secondaryAction?: {
    label: string;
    onClick: () => void;
  };
  suggestions?: string[];
}

export function EmptyState({
  icon: Icon,
  title,
  description,
  primaryAction,
  secondaryAction,
  suggestions
}: EmptyStateProps) {
  return (
    <div className="flex flex-col items-center justify-center py-16 px-4 text-center bg-nidhi-surface/50 border border-nidhi-border-subtle rounded-2xl">
      {Icon ? (
        <div className="w-16 h-16 rounded-full bg-nidhi-gold/10 flex items-center justify-center mb-6 border border-nidhi-gold/20">
          <Icon className="w-8 h-8 text-nidhi-gold" />
        </div>
      ) : (
        <div className="w-16 h-16 rounded-full bg-nidhi-gold/10 flex items-center justify-center mb-6 border border-nidhi-gold/20">
          <Sparkles className="w-8 h-8 text-nidhi-gold" />
        </div>
      )}
      
      <h3 className="text-xl font-display font-semibold text-nidhi-text mb-2">{title}</h3>
      <p className="text-sm text-nidhi-text-secondary max-w-sm mb-8 leading-relaxed">
        {description}
      </p>

      <div className="flex flex-col sm:flex-row gap-4 mb-8">
        {primaryAction && (
          <motion.button
            whileHover={{ scale: 1.02 }}
            whileTap={{ scale: 0.98 }}
            onClick={primaryAction.onClick}
            className="btn-primary"
          >
            {primaryAction.label}
          </motion.button>
        )}
        {secondaryAction && (
          <motion.button
            whileHover={{ scale: 1.02 }}
            whileTap={{ scale: 0.98 }}
            onClick={secondaryAction.onClick}
            className="btn-ghost"
          >
            {secondaryAction.label}
          </motion.button>
        )}
      </div>

      {suggestions && suggestions.length > 0 && (
        <div className="w-full max-w-md bg-nidhi-black/50 rounded-xl p-6 border border-nidhi-border">
          <p className="text-xs font-semibold text-nidhi-text-muted uppercase tracking-wider mb-4">Recommended Actions</p>
          <div className="space-y-3">
            {suggestions.map((suggestion, idx) => (
              <button 
                key={idx}
                className="w-full flex items-center justify-between p-3 rounded-lg hover:bg-nidhi-surface transition-colors text-sm text-nidhi-text-secondary hover:text-nidhi-text group"
              >
                <span>{suggestion}</span>
                <ArrowRight className="w-4 h-4 opacity-0 group-hover:opacity-100 group-hover:translate-x-1 transition-all" />
              </button>
            ))}
          </div>
        </div>
      )}
    </div>
  );
}
