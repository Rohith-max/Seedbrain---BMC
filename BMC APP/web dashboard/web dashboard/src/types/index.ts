// ============================================================
// NIDHI (निधि) — Core Type Definitions
// ============================================================

// ---- Users & Auth ----
export interface User {
  id: string;
  email: string;
  name: string;
  phone?: string;
  avatarUrl?: string;
  language: Language;
  role: UserRole;
  familyId: string;
  createdAt: string;
  updatedAt: string;
}

export type UserRole = 'head' | 'member' | 'viewer';
export type Language = 'en' | 'hi' | 'kn' | 'ta' | 'te' | 'ml';

export interface AuthCredentials {
  email: string;
  password: string;
}

export interface RegisterData extends AuthCredentials {
  name: string;
  phone?: string;
}

export interface AuthResponse {
  user: User;
  token: string;
}

// ---- Family ----
export interface FamilyMember {
  id: string;
  userId: string;
  name: string;
  relationship: FamilyRelationship;
  dateOfBirth: string;
  gender: 'male' | 'female' | 'other';
  aadhaarLast4?: string;
  panNumber?: string;
  avatarUrl?: string;
  phone?: string;
  email?: string;
  occupation?: string;
  annualIncome?: number;
  isActive: boolean;
  createdAt: string;
}

export type FamilyRelationship =
  | 'self'
  | 'spouse'
  | 'father'
  | 'mother'
  | 'son'
  | 'daughter'
  | 'brother'
  | 'sister'
  | 'grandfather'
  | 'grandmother'
  | 'uncle'
  | 'aunt'
  | 'other';

// ---- Documents ----
export interface Document {
  id: string;
  userId: string;
  familyMemberId?: string;
  title: string;
  description?: string;
  category: DocumentCategory;
  subcategory?: string;
  fileUrl: string;
  fileType: 'pdf' | 'image' | 'scan';
  fileSize: number;
  thumbnailUrl?: string;
  tags: string[];
  isVerified: boolean;
  expiryDate?: string;
  issueDate?: string;
  issuingAuthority?: string;
  documentNumber?: string;
  confidenceScore: number;
  ocrText?: string;
  aiSummary?: string;
  status: DocumentStatus;
  createdAt: string;
  updatedAt: string;
}

export type DocumentCategory =
  | 'identity'
  | 'financial'
  | 'medical'
  | 'legal'
  | 'education'
  | 'property'
  | 'vehicle'
  | 'utility'
  | 'insurance'
  | 'tax'
  | 'other';

export type DocumentStatus = 'processing' | 'active' | 'expired' | 'archived';

export interface DocumentMetadata {
  id: string;
  documentId: string;
  fieldName: string;
  fieldValue: string;
  confidence: number;
  isEdited: boolean;
}

// ---- Alerts ----
export interface Alert {
  id: string;
  userId: string;
  familyMemberId?: string;
  documentId?: string;
  title: string;
  description: string;
  type: AlertType;
  priority: AlertPriority;
  dueDate: string;
  status: AlertStatus;
  actionUrl?: string;
  actionLabel?: string;
  isRead: boolean;
  createdAt: string;
}

export type AlertType =
  | 'expiry'
  | 'renewal'
  | 'emi'
  | 'tax'
  | 'scholarship'
  | 'insurance'
  | 'warranty'
  | 'government'
  | 'custom';

export type AlertPriority = 'critical' | 'high' | 'medium' | 'low';
export type AlertStatus = 'active' | 'snoozed' | 'dismissed' | 'resolved';

// ---- Benefits ----
export interface Benefit {
  id: string;
  title: string;
  description: string;
  category: BenefitCategory;
  eligibilityCriteria: string[];
  requiredDocuments: string[];
  estimatedValue?: string;
  deadline?: string;
  applicationUrl?: string;
  matchScore: number;
  matchedMembers: string[];
  status: BenefitStatus;
  source: string;
  createdAt: string;
}

export type BenefitCategory =
  | 'government_scheme'
  | 'scholarship'
  | 'subsidy'
  | 'insurance_claim'
  | 'tax_benefit'
  | 'financial';

export type BenefitStatus = 'eligible' | 'applied' | 'claimed' | 'expired' | 'not_eligible';

// ---- AI ----
export interface AIMessage {
  id: string;
  role: 'user' | 'assistant' | 'system';
  content: string;
  timestamp: string;
  sources?: DocumentReference[];
  isStreaming?: boolean;
}

export interface DocumentReference {
  documentId: string;
  documentTitle: string;
  relevanceScore: number;
  excerpt: string;
}

export interface AIRecommendation {
  id: string;
  userId: string;
  title: string;
  description: string;
  type: 'action' | 'benefit' | 'warning' | 'insight';
  priority: AlertPriority;
  relatedDocumentIds: string[];
  relatedMemberIds: string[];
  actionUrl?: string;
  actionLabel?: string;
  isActioned: boolean;
  createdAt: string;
}

// ---- Knowledge Graph ----
export interface KnowledgeNode {
  id: string;
  label: string;
  type: 'person' | 'document' | 'alert' | 'benefit' | 'deadline' | 'claim';
  data?: Record<string, unknown>;
}

export interface KnowledgeEdge {
  id: string;
  source: string;
  target: string;
  label: string;
  type: string;
}

export interface KnowledgeGraph {
  nodes: KnowledgeNode[];
  edges: KnowledgeEdge[];
}

// ---- Activity & Notifications ----
export interface ActivityLog {
  id: string;
  userId: string;
  action: string;
  entityType: string;
  entityId: string;
  details?: string;
  ipAddress?: string;
  createdAt: string;
}

export interface Notification {
  id: string;
  userId: string;
  title: string;
  body: string;
  type: 'alert' | 'benefit' | 'recommendation' | 'system';
  isRead: boolean;
  actionUrl?: string;
  createdAt: string;
}

// ---- Dashboard ----
export interface DashboardStats {
  totalDocuments: number;
  activeAlerts: number;
  benefitsAvailable: number;
  benefitsClaimed: number;
  familyMembers: number;
  documentHealthScore: number;
  missingDocuments: string[];
  recentUploads: Document[];
  upcomingDeadlines: Alert[];
  recommendations: AIRecommendation[];
  intelligenceScore: number;
}

// ---- API ----
export interface ApiResponse<T> {
  success: boolean;
  data?: T;
  error?: string;
  message?: string;
}

export interface PaginatedResponse<T> extends ApiResponse<T[]> {
  total: number;
  page: number;
  limit: number;
}

// ---- Application Drafts ----
export interface ApplicationDraft {
  id: string;
  userId: string;
  title: string;
  type: 'application' | 'complaint' | 'claim' | 'cover_letter' | 'legal_summary';
  content: string;
  relatedDocumentIds: string[];
  status: 'draft' | 'finalized' | 'submitted';
  createdAt: string;
  updatedAt: string;
}
