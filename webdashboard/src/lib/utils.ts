import { clsx, type ClassValue } from "clsx";
import { twMerge } from "tailwind-merge";

export function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs));
}

export function formatDate(date: string | Date): string {
  return new Date(date).toLocaleDateString('en-IN', {
    day: 'numeric',
    month: 'short',
    year: 'numeric'
  });
}

export function formatRelativeDate(date: string | Date): string {
  const now = new Date();
  const target = new Date(date);
  const diffMs = target.getTime() - now.getTime();
  const diffDays = Math.ceil(diffMs / (1000 * 60 * 60 * 24));

  if (diffDays < 0) return `${Math.abs(diffDays)} days ago`;
  if (diffDays === 0) return 'Today';
  if (diffDays === 1) return 'Tomorrow';
  if (diffDays <= 7) return `${diffDays} days`;
  if (diffDays <= 30) return `${Math.ceil(diffDays / 7)} weeks`;
  if (diffDays <= 365) return `${Math.ceil(diffDays / 30)} months`;
  return `${Math.ceil(diffDays / 365)} years`;
}

export function formatFileSize(bytes: number): string {
  if (bytes === 0) return '0 Bytes';
  const k = 1024;
  const sizes = ['Bytes', 'KB', 'MB', 'GB'];
  const i = Math.floor(Math.log(bytes) / Math.log(k));
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
}

export function generateId(): string {
  return crypto.randomUUID?.() ?? Math.random().toString(36).substring(2, 15);
}

export function getCategoryColor(category: string): string {
  const colors: Record<string, string> = {
    identity: '#C9A96E',
    financial: '#4ADE80',
    medical: '#F87171',
    legal: '#818CF8',
    education: '#38BDF8',
    property: '#FB923C',
    vehicle: '#A78BFA',
    utility: '#34D399',
    insurance: '#FBBF24',
    tax: '#F472B6',
    other: '#94A3B8',
  };
  return colors[category] || colors.other;
}

// Returns a Lucide icon name (string) — no emojis
export function getCategoryIconName(category: string): string {
  const icons: Record<string, string> = {
    identity: 'Fingerprint',
    financial: 'Banknote',
    medical: 'Stethoscope',
    legal: 'Scale',
    education: 'GraduationCap',
    property: 'Home',
    vehicle: 'Car',
    utility: 'Zap',
    insurance: 'ShieldCheck',
    tax: 'Receipt',
    other: 'FileText',
  };
  return icons[category] || icons.other;
}

// Legacy alias kept for backward compat — returns lucide icon name not emoji
export function getCategoryIcon(category: string): string {
  return getCategoryIconName(category);
}

export function getPriorityColor(priority: string): string {
  const colors: Record<string, string> = {
    critical: '#EF4444',
    high: '#F97316',
    medium: '#EAB308',
    low: '#22C55E',
  };
  return colors[priority] || colors.medium;
}

export function truncate(str: string, length: number): string {
  if (str.length <= length) return str;
  return str.substring(0, length) + '...';
}
