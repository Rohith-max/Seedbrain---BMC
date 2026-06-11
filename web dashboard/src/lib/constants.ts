// ============================================================
// NIDHI — Application Constants
// ============================================================

export const APP_NAME = 'NIDHI';
export const APP_NAME_HINDI = 'निधि';
export const APP_TAGLINE = "India's Household Intelligence System";
export const APP_DESCRIPTION = 'AI-powered household operating system for Indian families. Understand documents, organize family intelligence, detect deadlines, find missed benefits.';

export const DOCUMENT_CATEGORIES = [
  { value: 'identity', label: 'Identity', icon: 'Fingerprint', description: 'Aadhaar, PAN, Passport, Voter ID' },
  { value: 'financial', label: 'Financial', icon: 'Banknote', description: 'Bank statements, FD receipts, Investments' },
  { value: 'medical', label: 'Medical', icon: 'Stethoscope', description: 'Health records, Prescriptions, Reports' },
  { value: 'legal', label: 'Legal', icon: 'Scale', description: 'Agreements, Contracts, Court orders' },
  { value: 'education', label: 'Education', icon: 'GraduationCap', description: 'Certificates, Marksheets, Degrees' },
  { value: 'property', label: 'Property', icon: 'Home', description: 'Deeds, Agreements, Tax receipts' },
  { value: 'vehicle', label: 'Vehicle', icon: 'Car', description: 'RC, Insurance, Pollution certificates' },
  { value: 'utility', label: 'Utility', icon: 'Zap', description: 'Electricity, Water, Gas, Phone bills' },
  { value: 'insurance', label: 'Insurance', icon: 'ShieldCheck', description: 'Life, Health, Vehicle, Property insurance' },
  { value: 'tax', label: 'Tax', icon: 'Receipt', description: 'ITR, Form 16, TDS certificates' },
  { value: 'other', label: 'Other', icon: 'FileText', description: 'Miscellaneous documents' },
] as const;

export const ALERT_TYPES = [
  { value: 'expiry', label: 'Document Expiry', icon: 'Clock' },
  { value: 'renewal', label: 'Renewal Due', icon: 'RefreshCw' },
  { value: 'emi', label: 'EMI Payment', icon: 'CreditCard' },
  { value: 'tax', label: 'Tax Filing', icon: 'Receipt' },
  { value: 'scholarship', label: 'Scholarship', icon: 'GraduationCap' },
  { value: 'insurance', label: 'Insurance', icon: 'ShieldCheck' },
  { value: 'warranty', label: 'Warranty', icon: 'Package' },
  { value: 'government', label: 'Government', icon: 'Landmark' },
  { value: 'custom', label: 'Custom', icon: 'Pin' },
] as const;

export const LANGUAGES = [
  { code: 'en', label: 'English', nativeLabel: 'English' },
  { code: 'hi', label: 'Hindi', nativeLabel: 'हिन्दी' },
  { code: 'kn', label: 'Kannada', nativeLabel: 'ಕನ್ನಡ' },
  { code: 'ta', label: 'Tamil', nativeLabel: 'தமிழ்' },
  { code: 'te', label: 'Telugu', nativeLabel: 'తెలుగు' },
  { code: 'ml', label: 'Malayalam', nativeLabel: 'മലയാളം' },
] as const;

export const FAMILY_RELATIONSHIPS = [
  { value: 'self', label: 'Self' },
  { value: 'spouse', label: 'Spouse' },
  { value: 'father', label: 'Father' },
  { value: 'mother', label: 'Mother' },
  { value: 'son', label: 'Son' },
  { value: 'daughter', label: 'Daughter' },
  { value: 'brother', label: 'Brother' },
  { value: 'sister', label: 'Sister' },
  { value: 'grandfather', label: 'Grandfather' },
  { value: 'grandmother', label: 'Grandmother' },
  { value: 'uncle', label: 'Uncle' },
  { value: 'aunt', label: 'Aunt' },
  { value: 'other', label: 'Other' },
] as const;

export const SUGGESTED_AI_PROMPTS = [
  "When does dad's insurance expire?",
  "What documents are missing for passport renewal?",
  "Which scholarships can my child apply for?",
  "Show all medical records for grandma.",
  "Generate a summary of our home loan documents.",
  "What government schemes am I eligible for?",
  "List all documents expiring this month.",
  "Help me file an insurance claim.",
  "What tax deductions can I claim this year?",
  "Draft a complaint letter for electricity overcharge.",
];

export const NAV_ITEMS = [
  { href: '/dashboard', label: 'Dashboard', icon: 'LayoutDashboard' },
  { href: '/dashboard/vault', label: 'Document Vault', icon: 'Archive' },
  { href: '/dashboard/assistant', label: 'AI Assistant', icon: 'Bot' },
  { href: '/dashboard/knowledge-graph', label: 'Knowledge Graph', icon: 'Network' },
  { href: '/dashboard/alerts', label: 'Alerts', icon: 'Bell' },
  { href: '/dashboard/benefits', label: 'Benefits', icon: 'Gift' },
  { href: '/dashboard/family', label: 'Family', icon: 'Users' },
  { href: '/dashboard/analytics', label: 'Analytics', icon: 'BarChart3' },
  { href: '/dashboard/government-portals', label: 'Gov Portals', icon: 'Globe' },
  { href: '/dashboard/settings', label: 'Settings', icon: 'Settings' },
] as const;
