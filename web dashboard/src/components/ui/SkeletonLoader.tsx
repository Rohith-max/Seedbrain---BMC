import React from 'react';

interface SkeletonProps {
  className?: string;
  variant?: 'rectangular' | 'circular' | 'text';
  animation?: 'pulse' | 'wave' | 'none';
}

export function Skeleton({ className = '', variant = 'rectangular', animation = 'pulse' }: SkeletonProps) {
  const baseClass = 'bg-nidhi-surface border border-nidhi-border-subtle';
  
  const variantClasses = {
    rectangular: 'rounded-xl',
    circular: 'rounded-full',
    text: 'rounded-[4px] h-4',
  };

  const animationClasses = {
    pulse: 'animate-pulse',
    wave: 'relative overflow-hidden before:absolute before:inset-0 before:-translate-x-full before:animate-[shimmer_2s_infinite] before:bg-gradient-to-r before:from-transparent before:via-white/5 before:to-transparent',
    none: '',
  };

  return (
    <div
      className={`${baseClass} ${variantClasses[variant]} ${animationClasses[animation]} ${className}`}
    />
  );
}

export function CardSkeleton() {
  return (
    <div className="card-premium p-6">
      <div className="flex items-start gap-4 mb-4">
        <Skeleton variant="circular" className="w-10 h-10 flex-shrink-0" animation="wave" />
        <div className="flex-1 space-y-2">
          <Skeleton variant="text" className="w-3/4" animation="wave" />
          <Skeleton variant="text" className="w-1/2" animation="wave" />
        </div>
      </div>
      <div className="space-y-2">
        <Skeleton variant="text" className="w-full" animation="wave" />
        <Skeleton variant="text" className="w-5/6" animation="wave" />
        <Skeleton variant="text" className="w-4/6" animation="wave" />
      </div>
      <div className="mt-6 flex gap-2">
        <Skeleton className="w-24 h-8 rounded-lg" animation="wave" />
        <Skeleton className="w-24 h-8 rounded-lg" animation="wave" />
      </div>
    </div>
  );
}
