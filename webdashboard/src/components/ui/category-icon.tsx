import React from 'react';
import {
  Fingerprint,
  Banknote,
  Stethoscope,
  Scale,
  GraduationCap,
  Home,
  Car,
  Zap,
  ShieldCheck,
  Receipt,
  FileText,
} from 'lucide-react';

const ICON_MAP: Record<string, React.ElementType> = {
  identity: Fingerprint,
  financial: Banknote,
  medical: Stethoscope,
  legal: Scale,
  education: GraduationCap,
  property: Home,
  vehicle: Car,
  utility: Zap,
  insurance: ShieldCheck,
  tax: Receipt,
  other: FileText,
};

interface CategoryIconProps {
  category: string;
  className?: string;
  style?: React.CSSProperties;
}

export function CategoryIcon({ category, className = 'w-4 h-4', style }: CategoryIconProps) {
  const Icon = ICON_MAP[category] ?? FileText;
  return <Icon className={className} style={style} />;
}
