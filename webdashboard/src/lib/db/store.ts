// ============================================================
// NIDHI — Demo Data Store (In-Memory Database)
// Replaces SQLite for zero-dependency local development
// ============================================================

import { User, FamilyMember, Document, Alert, Benefit, AIRecommendation, KnowledgeNode, KnowledgeEdge, ActivityLog, Notification, ApplicationDraft, DocumentCategory, AlertType, AlertPriority, BenefitCategory } from '@/types';

// ---- Sharma Family Demo Data ----

const DEMO_USER: User = {
  id: 'user-001',
  email: 'rajesh.sharma@email.com',
  name: 'Rajesh Sharma',
  phone: '+91 98765 43210',
  language: 'en',
  role: 'head',
  familyId: 'family-001',
  createdAt: '2024-01-15T10:00:00Z',
  updatedAt: '2025-06-01T10:00:00Z',
};

const DEMO_FAMILY_MEMBERS: FamilyMember[] = [
  {
    id: 'fm-001', userId: 'user-001', name: 'Rajesh Sharma',
    relationship: 'self', dateOfBirth: '1978-03-15', gender: 'male',
    aadhaarLast4: '4523', panNumber: 'ABCPS1234F',
    phone: '+91 98765 43210', email: 'rajesh.sharma@email.com',
    occupation: 'Software Engineer', annualIncome: 1800000,
    isActive: true, createdAt: '2024-01-15T10:00:00Z',
  },
  {
    id: 'fm-002', userId: 'user-001', name: 'Priya Sharma',
    relationship: 'spouse', dateOfBirth: '1982-07-22', gender: 'female',
    aadhaarLast4: '7891', panNumber: 'DEFPS5678G',
    phone: '+91 98765 43211', email: 'priya.sharma@email.com',
    occupation: 'Teacher', annualIncome: 600000,
    isActive: true, createdAt: '2024-01-15T10:00:00Z',
  },
  {
    id: 'fm-003', userId: 'user-001', name: 'Aarav Sharma',
    relationship: 'son', dateOfBirth: '2008-11-05', gender: 'male',
    occupation: 'Student', isActive: true, createdAt: '2024-01-15T10:00:00Z',
  },
  {
    id: 'fm-004', userId: 'user-001', name: 'Ananya Sharma',
    relationship: 'daughter', dateOfBirth: '2012-04-18', gender: 'female',
    occupation: 'Student', isActive: true, createdAt: '2024-01-15T10:00:00Z',
  },
  {
    id: 'fm-005', userId: 'user-001', name: 'Kamla Devi Sharma',
    relationship: 'mother', dateOfBirth: '1950-01-10', gender: 'female',
    aadhaarLast4: '2345',
    occupation: 'Retired', isActive: true, createdAt: '2024-01-15T10:00:00Z',
  },
];

const DEMO_DOCUMENTS: Document[] = [
  {
    id: 'doc-001', userId: 'user-001', familyMemberId: 'fm-001',
    title: 'Aadhaar Card - Rajesh Sharma', description: 'Government issued Aadhaar identification card',
    category: 'identity', fileUrl: '/demo/aadhaar-rajesh.pdf', fileType: 'pdf', fileSize: 245000,
    tags: ['aadhaar', 'identity', 'government'], isVerified: true,
    issueDate: '2015-06-20', issuingAuthority: 'UIDAI',
    documentNumber: 'XXXX-XXXX-4523', confidenceScore: 0.97,
    ocrText: 'Rajesh Sharma, DOB: 15/03/1978, Address: 42 Maple Heights, Bengaluru 560001',
    aiSummary: 'Aadhaar card for Rajesh Sharma, male, DOB 15 March 1978. Residential address in Bengaluru. Card is active and valid.',
    status: 'active', createdAt: '2024-02-01T10:00:00Z', updatedAt: '2024-02-01T10:00:00Z',
  },
  {
    id: 'doc-002', userId: 'user-001', familyMemberId: 'fm-001',
    title: 'PAN Card - Rajesh Sharma', description: 'Permanent Account Number card',
    category: 'tax', fileUrl: '/demo/pan-rajesh.pdf', fileType: 'pdf', fileSize: 189000,
    tags: ['pan', 'tax', 'identity'], isVerified: true,
    issueDate: '2010-03-12', issuingAuthority: 'Income Tax Department',
    documentNumber: 'ABCPS1234F', confidenceScore: 0.95,
    ocrText: 'INCOME TAX DEPARTMENT, GOVT OF INDIA. PERMANENT ACCOUNT NUMBER CARD. RAJESH SHARMA. ABCPS1234F',
    aiSummary: 'PAN Card for Rajesh Sharma. PAN number ABCPS1234F issued by Income Tax Department. Required for all financial transactions above ₹50,000.',
    status: 'active', createdAt: '2024-02-01T10:30:00Z', updatedAt: '2024-02-01T10:30:00Z',
  },
  {
    id: 'doc-003', userId: 'user-001', familyMemberId: 'fm-001',
    title: 'Health Insurance Policy - Family Floater', description: 'Star Health Family Floater Policy',
    category: 'insurance', fileUrl: '/demo/insurance-family.pdf', fileType: 'pdf', fileSize: 520000,
    tags: ['insurance', 'health', 'family', 'star-health'], isVerified: true,
    issueDate: '2024-04-01', expiryDate: '2026-08-31',
    issuingAuthority: 'Star Health Insurance', documentNumber: 'SH-FAM-2024-78945',
    confidenceScore: 0.92,
    ocrText: 'Star Health and Allied Insurance. Family Health Optima. Sum Insured: ₹10,00,000. Premium: ₹28,500/year. Members: Rajesh, Priya, Aarav, Ananya',
    aiSummary: 'Family floater health insurance by Star Health. Covers Rajesh, Priya, Aarav, and Ananya Sharma with ₹10 lakh sum insured. Annual premium ₹28,500. Policy valid until August 2026.',
    status: 'active', createdAt: '2024-04-05T14:00:00Z', updatedAt: '2024-04-05T14:00:00Z',
  },
  {
    id: 'doc-004', userId: 'user-001', familyMemberId: 'fm-005',
    title: 'Medical Report - Kamla Devi (Diabetes)', description: 'HbA1c and Blood Sugar Report',
    category: 'medical', fileUrl: '/demo/medical-kamla.pdf', fileType: 'pdf', fileSize: 312000,
    tags: ['medical', 'diabetes', 'blood-test', 'kamla-devi'], isVerified: true,
    issueDate: '2025-05-15', issuingAuthority: 'Apollo Diagnostics',
    confidenceScore: 0.89,
    ocrText: 'Patient: Kamla Devi Sharma, Age: 75. HbA1c: 7.2%, Fasting Blood Sugar: 145 mg/dL, PP Blood Sugar: 210 mg/dL. Recommendation: Continue medication, follow-up in 3 months.',
    aiSummary: 'Diabetes monitoring report for Kamla Devi Sharma (75 yrs). HbA1c at 7.2% (slightly above target). Fasting sugar 145, PP sugar 210. Doctor recommends continuing medication with 3-month follow-up.',
    status: 'active', createdAt: '2025-05-16T09:00:00Z', updatedAt: '2025-05-16T09:00:00Z',
  },
  {
    id: 'doc-005', userId: 'user-001', familyMemberId: 'fm-001',
    title: 'Property Deed - 42 Maple Heights', description: 'Sale deed for residential apartment',
    category: 'property', fileUrl: '/demo/property-deed.pdf', fileType: 'pdf', fileSize: 1240000,
    tags: ['property', 'apartment', 'sale-deed', 'bengaluru'], isVerified: true,
    issueDate: '2018-09-20', issuingAuthority: 'Sub-Registrar Office, Bengaluru',
    documentNumber: 'BLR-SR-2018-45678', confidenceScore: 0.88,
    ocrText: 'Sale Deed. Property: Flat No. 42, Maple Heights, 3BHK, 1450 sq ft, Whitefield, Bengaluru. Seller: XYZ Builders. Buyer: Rajesh Sharma. Consideration: ₹85,00,000.',
    aiSummary: 'Sale deed for 3BHK apartment at 42 Maple Heights, Whitefield, Bengaluru. Area 1450 sq ft purchased for ₹85 lakh from XYZ Builders in September 2018. Registered at Sub-Registrar Office.',
    status: 'active', createdAt: '2024-02-10T11:00:00Z', updatedAt: '2024-02-10T11:00:00Z',
  },
  {
    id: 'doc-006', userId: 'user-001', familyMemberId: 'fm-003',
    title: 'School Report Card - Aarav (Class 10)', description: 'CBSE Class 10 Board Exam Results',
    category: 'education', fileUrl: '/demo/report-aarav.pdf', fileType: 'pdf', fileSize: 195000,
    tags: ['education', 'report-card', 'cbse', 'class-10'], isVerified: true,
    issueDate: '2025-05-20', issuingAuthority: 'CBSE',
    documentNumber: 'CBSE-2025-78945612', confidenceScore: 0.94,
    ocrText: 'CBSE Class X Board Examination 2025. Student: Aarav Sharma. Roll No: 12345678. Mathematics: 92, Science: 88, English: 85, Hindi: 78, Social Science: 90. Overall: 86.6%',
    aiSummary: 'CBSE Class 10 Board exam results for Aarav Sharma. Overall score 86.6% with highest in Mathematics (92) and Social Science (90). Strong performance qualifying for science stream.',
    status: 'active', createdAt: '2025-05-21T10:00:00Z', updatedAt: '2025-05-21T10:00:00Z',
  },
  {
    id: 'doc-007', userId: 'user-001', familyMemberId: 'fm-001',
    title: 'Vehicle RC - Honda City', description: 'Registration certificate for Honda City sedan',
    category: 'vehicle', fileUrl: '/demo/rc-car.pdf', fileType: 'pdf', fileSize: 167000,
    tags: ['vehicle', 'car', 'rc', 'honda-city'], isVerified: true,
    issueDate: '2022-01-15', expiryDate: '2037-01-14',
    issuingAuthority: 'RTO Bengaluru', documentNumber: 'KA-05-MN-4567',
    confidenceScore: 0.93,
    ocrText: 'Registration Certificate. Vehicle: Honda City V CVT Petrol. Reg No: KA-05-MN-4567. Owner: Rajesh Sharma. Engine: L15B7. Chassis: MAKE2022XYZ. Reg Valid: 14/01/2037.',
    aiSummary: 'Vehicle registration for Honda City V CVT Petrol, registration KA-05-MN-4567 in Rajesh Sharma\'s name. Registered at RTO Bengaluru, valid until January 2037.',
    status: 'active', createdAt: '2024-02-15T10:00:00Z', updatedAt: '2024-02-15T10:00:00Z',
  },
  {
    id: 'doc-008', userId: 'user-001', familyMemberId: 'fm-001',
    title: 'Home Loan Statement - SBI', description: 'SBI Home Loan EMI Statement FY 2024-25',
    category: 'financial', fileUrl: '/demo/home-loan.pdf', fileType: 'pdf', fileSize: 380000,
    tags: ['loan', 'home-loan', 'sbi', 'emi'], isVerified: true,
    issueDate: '2025-04-01', issuingAuthority: 'State Bank of India',
    documentNumber: 'SBI-HL-2018-234567', confidenceScore: 0.91,
    ocrText: 'SBI Home Loan Statement. Account: SBI-HL-2018-234567. Borrower: Rajesh Sharma. Loan Amount: ₹55,00,000. Outstanding: ₹38,45,000. EMI: ₹48,500/month. Interest Rate: 8.5%. Tenure: 20 years.',
    aiSummary: 'SBI home loan for ₹55 lakh with ₹38.45 lakh outstanding. Monthly EMI ₹48,500 at 8.5% interest. 20-year tenure started 2018. Principal repaid: ₹16.55 lakh. Interest component deductible under Section 24(b).',
    status: 'active', createdAt: '2025-04-02T10:00:00Z', updatedAt: '2025-04-02T10:00:00Z',
  },
  {
    id: 'doc-009', userId: 'user-001', familyMemberId: 'fm-001',
    title: 'ITR Acknowledgment - AY 2024-25', description: 'Income Tax Return filing acknowledgment',
    category: 'tax', fileUrl: '/demo/itr-2024.pdf', fileType: 'pdf', fileSize: 95000,
    tags: ['tax', 'itr', 'income-tax', 'ay-2024-25'], isVerified: true,
    issueDate: '2024-07-28', issuingAuthority: 'Income Tax Department',
    documentNumber: 'ACK-2024-ITR3-12345678', confidenceScore: 0.96,
    ocrText: 'Income Tax Return Acknowledgment. AY 2024-25. PAN: ABCPS1234F. Name: Rajesh Sharma. Total Income: ₹18,50,000. Tax Paid: ₹2,85,000. Refund Due: ₹15,200.',
    aiSummary: 'ITR filed for AY 2024-25 under new tax regime. Total income ₹18.5 lakh, tax paid ₹2.85 lakh with ₹15,200 refund due. Filed on time before July 31 deadline.',
    status: 'active', createdAt: '2024-07-29T10:00:00Z', updatedAt: '2024-07-29T10:00:00Z',
  },
  {
    id: 'doc-010', userId: 'user-001', familyMemberId: 'fm-002',
    title: 'Aadhaar Card - Priya Sharma', description: 'Government issued Aadhaar identification card',
    category: 'identity', fileUrl: '/demo/aadhaar-priya.pdf', fileType: 'pdf', fileSize: 238000,
    tags: ['aadhaar', 'identity', 'government'], isVerified: true,
    issueDate: '2016-02-15', issuingAuthority: 'UIDAI',
    documentNumber: 'XXXX-XXXX-7891', confidenceScore: 0.96,
    ocrText: 'Priya Sharma, DOB: 22/07/1982, Address: 42 Maple Heights, Bengaluru 560001',
    aiSummary: 'Aadhaar card for Priya Sharma, female, DOB 22 July 1982. Same residential address as family head.',
    status: 'active', createdAt: '2024-02-01T11:00:00Z', updatedAt: '2024-02-01T11:00:00Z',
  },
  {
    id: 'doc-011', userId: 'user-001', familyMemberId: 'fm-001',
    title: 'Electricity Bill - June 2025', description: 'BESCOM electricity bill',
    category: 'utility', fileUrl: '/demo/electricity-bill.pdf', fileType: 'pdf', fileSize: 120000,
    tags: ['utility', 'electricity', 'bescom', 'bill'], isVerified: true,
    issueDate: '2025-06-01', expiryDate: '2025-06-15',
    issuingAuthority: 'BESCOM', documentNumber: 'BESCOM-2025-456789',
    confidenceScore: 0.94,
    ocrText: 'BESCOM Electricity Bill. Consumer: Rajesh Sharma. Account: 456789. Units: 342. Amount: ₹2,450. Due Date: 15/06/2025.',
    aiSummary: 'BESCOM electricity bill for June 2025. 342 units consumed, amount ₹2,450. Due date 15 June 2025.',
    status: 'active', createdAt: '2025-06-01T10:00:00Z', updatedAt: '2025-06-01T10:00:00Z',
  },
  {
    id: 'doc-012', userId: 'user-001', familyMemberId: 'fm-001',
    title: 'Term Life Insurance - LIC', description: 'LIC Term Plan Policy Document',
    category: 'insurance', fileUrl: '/demo/lic-term.pdf', fileType: 'pdf', fileSize: 670000,
    tags: ['insurance', 'life', 'lic', 'term-plan'], isVerified: true,
    issueDate: '2020-01-15', expiryDate: '2048-01-14',
    issuingAuthority: 'Life Insurance Corporation', documentNumber: 'LIC-TERM-2020-12345',
    confidenceScore: 0.90,
    ocrText: 'LIC Tech Term Plan. Policy No: LIC-TERM-2020-12345. Life Assured: Rajesh Sharma. Sum Assured: ₹1,00,00,000. Premium: ₹12,500/year. Term: 28 years.',
    aiSummary: 'LIC term life insurance for ₹1 crore. Annual premium ₹12,500 with 28-year term. Nominee: Priya Sharma. Premium deductible under Section 80C.',
    status: 'active', createdAt: '2024-02-20T10:00:00Z', updatedAt: '2024-02-20T10:00:00Z',
  },
];

const today = new Date();
const futureDate = (days: number) => {
  const d = new Date(today);
  d.setDate(d.getDate() + days);
  return d.toISOString();
};

const DEMO_ALERTS: Alert[] = [
  {
    id: 'alert-001', userId: 'user-001', familyMemberId: 'fm-001', documentId: 'doc-003',
    title: 'Health Insurance Renewal Due', description: 'Star Health Family Floater policy expires on August 31, 2026. Renew before expiry to avoid gap in coverage.',
    type: 'renewal', priority: 'high', dueDate: '2026-08-31T00:00:00Z',
    status: 'active', isRead: false, actionLabel: 'View Policy',
    createdAt: '2025-06-01T10:00:00Z',
  },
  {
    id: 'alert-002', userId: 'user-001', familyMemberId: 'fm-001',
    title: 'ITR Filing Deadline - AY 2025-26', description: 'Income Tax Return for AY 2025-26 must be filed before July 31, 2025. Gather Form 16, bank statements, and investment proofs.',
    type: 'tax', priority: 'critical', dueDate: '2025-07-31T00:00:00Z',
    status: 'active', isRead: false, actionLabel: 'Prepare Documents',
    createdAt: '2025-06-01T10:00:00Z',
  },
  {
    id: 'alert-003', userId: 'user-001', familyMemberId: 'fm-001', documentId: 'doc-008',
    title: 'Home Loan EMI Due', description: 'SBI Home Loan EMI of ₹48,500 due on July 5. Ensure sufficient balance in linked account.',
    type: 'emi', priority: 'high', dueDate: futureDate(12),
    status: 'active', isRead: false, actionLabel: 'Check Balance',
    createdAt: '2025-06-01T10:00:00Z',
  },
  {
    id: 'alert-004', userId: 'user-001', familyMemberId: 'fm-005',
    title: 'Diabetes Follow-up Due', description: 'Kamla Devi\'s 3-month diabetes follow-up is due. Schedule appointment with Dr. Mehta at Apollo Clinic.',
    type: 'custom', priority: 'medium', dueDate: futureDate(25),
    status: 'active', isRead: false, actionLabel: 'Book Appointment',
    createdAt: '2025-06-01T10:00:00Z',
  },
  {
    id: 'alert-005', userId: 'user-001', familyMemberId: 'fm-003',
    title: 'KVPY Scholarship Application Opens', description: 'KVPY scholarship applications open in August. Aarav with 86.6% in Class 10 may be eligible. Deadline typically in September.',
    type: 'scholarship', priority: 'medium', dueDate: futureDate(60),
    status: 'active', isRead: false, actionLabel: 'Check Eligibility',
    createdAt: '2025-06-01T10:00:00Z',
  },
  {
    id: 'alert-006', userId: 'user-001', familyMemberId: 'fm-001', documentId: 'doc-011',
    title: 'Electricity Bill Payment Due', description: 'BESCOM bill of ₹2,450 due on June 15. Pay before due date to avoid late fee.',
    type: 'custom', priority: 'high', dueDate: '2025-06-15T00:00:00Z',
    status: 'active', isRead: true, actionLabel: 'Pay Now',
    createdAt: '2025-06-01T10:00:00Z',
  },
  {
    id: 'alert-007', userId: 'user-001', familyMemberId: 'fm-001',
    title: 'Vehicle Insurance Renewal', description: 'Honda City comprehensive insurance expires on December 31, 2025. Compare quotes early for best rates.',
    type: 'insurance', priority: 'low', dueDate: '2025-12-31T00:00:00Z',
    status: 'active', isRead: false, actionLabel: 'Compare Quotes',
    createdAt: '2025-06-01T10:00:00Z',
  },
  {
    id: 'alert-008', userId: 'user-001', familyMemberId: 'fm-001',
    title: 'LIC Premium Due', description: 'Annual LIC term insurance premium of ₹12,500 due on January 15, 2026. Claim under Section 80C.',
    type: 'renewal', priority: 'low', dueDate: '2026-01-15T00:00:00Z',
    status: 'active', isRead: false, actionLabel: 'Pay Premium',
    createdAt: '2025-06-01T10:00:00Z',
  },
];

const DEMO_BENEFITS: Benefit[] = [
  {
    id: 'benefit-001', title: 'PM Awas Yojana - Urban',
    description: 'Interest subsidy of up to ₹2.67 lakh on home loans for EWS/LIG/MIG categories under PMAY(U). You may qualify based on your income bracket.',
    category: 'government_scheme', eligibilityCriteria: ['Annual household income ≤ ₹18 lakh', 'First-time home buyer', 'Property purchased after June 2015'],
    requiredDocuments: ['Aadhaar Card', 'PAN Card', 'Income Certificate', 'Property Documents', 'Home Loan Statement'],
    estimatedValue: '₹2,67,000 interest subsidy', deadline: '2025-12-31',
    applicationUrl: 'https://pmaymis.gov.in', matchScore: 0.75,
    matchedMembers: ['fm-001'], status: 'eligible', source: 'Ministry of Housing',
    createdAt: '2025-06-01T10:00:00Z',
  },
  {
    id: 'benefit-002', title: 'National Scholarship for Meritorious Students',
    description: 'CBSE merit scholarship for students scoring above 85% in Class 10 Board exams. Aarav\'s 86.6% score qualifies him.',
    category: 'scholarship', eligibilityCriteria: ['Class 10 score above 85%', 'Family income below ₹25 lakh', 'Indian citizen'],
    requiredDocuments: ['Class 10 Marksheet', 'Aadhaar Card', 'Income Certificate', 'Bank Account Details'],
    estimatedValue: '₹12,000/year for 2 years', deadline: '2025-09-30',
    matchScore: 0.92, matchedMembers: ['fm-003'], status: 'eligible',
    source: 'CBSE / Ministry of Education', createdAt: '2025-06-01T10:00:00Z',
  },
  {
    id: 'benefit-003', title: 'Senior Citizen Health Insurance - PMJAY',
    description: 'Ayushman Bharat Pradhan Mantri Jan Arogya Yojana provides health cover of ₹5 lakh per family for secondary and tertiary care. Kamla Devi may be eligible as a senior citizen.',
    category: 'government_scheme', eligibilityCriteria: ['Age 60+ years', 'Family income criteria met', 'Not covered under ESIC/CGHS'],
    requiredDocuments: ['Aadhaar Card', 'Age Proof', 'Income Certificate', 'Ration Card'],
    estimatedValue: '₹5,00,000 health cover', matchScore: 0.68,
    matchedMembers: ['fm-005'], status: 'eligible',
    source: 'National Health Authority', createdAt: '2025-06-01T10:00:00Z',
  },
  {
    id: 'benefit-004', title: 'Section 80C Tax Deduction - Unclaimed',
    description: 'You can claim up to ₹1.5 lakh deduction under Section 80C for LIC premiums, PPF, ELSS, and home loan principal repayment. AI analysis shows potential unclaimed deductions.',
    category: 'tax_benefit', eligibilityCriteria: ['Indian taxpayer', 'Investments in eligible instruments'],
    requiredDocuments: ['LIC Premium Receipt', 'PPF Passbook', 'Home Loan Statement', 'ELSS Statement'],
    estimatedValue: '₹46,800 tax saving', matchScore: 0.88,
    matchedMembers: ['fm-001'], status: 'eligible',
    source: 'Income Tax Department', createdAt: '2025-06-01T10:00:00Z',
  },
  {
    id: 'benefit-005', title: 'Sukanya Samriddhi Yojana',
    description: 'Government savings scheme for girl children with 8.2% interest rate and tax benefits. Ananya (age 12) is eligible for enrollment.',
    category: 'financial', eligibilityCriteria: ['Girl child below 10 years at account opening', 'Indian citizen', 'Maximum 2 accounts per family'],
    requiredDocuments: ['Birth Certificate', 'Parent Aadhaar', 'Parent PAN', 'Address Proof'],
    estimatedValue: '₹65 lakh+ at maturity (₹1.5L/yr deposit)', matchScore: 0.55,
    matchedMembers: ['fm-004'], status: 'eligible',
    source: 'Ministry of Finance', createdAt: '2025-06-01T10:00:00Z',
  },
];

const DEMO_RECOMMENDATIONS: AIRecommendation[] = [
  {
    id: 'rec-001', userId: 'user-001',
    title: 'File ITR Before July 31 Deadline',
    description: 'Tax filing deadline approaching. You have all Form 16 and investment documents uploaded. Start preparing your return now to avoid last-minute rush.',
    type: 'action', priority: 'critical',
    relatedDocumentIds: ['doc-002', 'doc-009'], relatedMemberIds: ['fm-001'],
    actionLabel: 'Start ITR Preparation', isActioned: false,
    createdAt: '2025-06-01T10:00:00Z',
  },
  {
    id: 'rec-002', userId: 'user-001',
    title: 'Apply for Aarav\'s Merit Scholarship',
    description: 'Aarav scored 86.6% in CBSE Class 10 — he qualifies for the National Merit Scholarship worth ₹12,000/year. Application deadline is September 30.',
    type: 'benefit', priority: 'high',
    relatedDocumentIds: ['doc-006'], relatedMemberIds: ['fm-003'],
    actionLabel: 'Check Scholarship Details', isActioned: false,
    createdAt: '2025-06-01T10:00:00Z',
  },
  {
    id: 'rec-003', userId: 'user-001',
    title: 'Missing: Priya\'s PAN Card Upload',
    description: 'Priya Sharma\'s PAN card is referenced in tax documents but not uploaded to the vault. Upload it for complete family documentation.',
    type: 'warning', priority: 'medium',
    relatedDocumentIds: [], relatedMemberIds: ['fm-002'],
    actionLabel: 'Upload Document', isActioned: false,
    createdAt: '2025-06-01T10:00:00Z',
  },
  {
    id: 'rec-004', userId: 'user-001',
    title: 'Schedule Kamla Devi\'s Diabetes Follow-up',
    description: 'Last HbA1c was 7.2% — slightly above target. 3-month follow-up is due. Book an appointment at Apollo Clinic.',
    type: 'action', priority: 'medium',
    relatedDocumentIds: ['doc-004'], relatedMemberIds: ['fm-005'],
    actionLabel: 'Book Appointment', isActioned: false,
    createdAt: '2025-06-01T10:00:00Z',
  },
  {
    id: 'rec-005', userId: 'user-001',
    title: 'Potential Tax Savings of ₹46,800',
    description: 'AI analysis of your documents shows unclaimed Section 80C deductions from LIC premiums and home loan principal. Review and claim these in your next ITR.',
    type: 'insight', priority: 'high',
    relatedDocumentIds: ['doc-008', 'doc-012'], relatedMemberIds: ['fm-001'],
    actionLabel: 'View Tax Analysis', isActioned: false,
    createdAt: '2025-06-01T10:00:00Z',
  },
];

const DEMO_KNOWLEDGE_NODES: KnowledgeNode[] = [
  { id: 'fm-001', label: 'Rajesh Sharma', type: 'person' },
  { id: 'fm-002', label: 'Priya Sharma', type: 'person' },
  { id: 'fm-003', label: 'Aarav Sharma', type: 'person' },
  { id: 'fm-004', label: 'Ananya Sharma', type: 'person' },
  { id: 'fm-005', label: 'Kamla Devi', type: 'person' },
  { id: 'doc-001', label: 'Aadhaar - Rajesh', type: 'document' },
  { id: 'doc-002', label: 'PAN - Rajesh', type: 'document' },
  { id: 'doc-003', label: 'Health Insurance', type: 'document' },
  { id: 'doc-004', label: 'Medical Report - Kamla', type: 'document' },
  { id: 'doc-005', label: 'Property Deed', type: 'document' },
  { id: 'doc-006', label: 'Report Card - Aarav', type: 'document' },
  { id: 'doc-008', label: 'Home Loan - SBI', type: 'document' },
  { id: 'doc-012', label: 'LIC Term Plan', type: 'document' },
  { id: 'alert-002', label: 'ITR Deadline', type: 'deadline' },
  { id: 'alert-003', label: 'EMI Due', type: 'deadline' },
  { id: 'benefit-002', label: 'Merit Scholarship', type: 'benefit' },
  { id: 'benefit-004', label: 'Tax Deduction 80C', type: 'benefit' },
];

const DEMO_KNOWLEDGE_EDGES: KnowledgeEdge[] = [
  { id: 'e1', source: 'fm-001', target: 'fm-002', label: 'married to', type: 'family' },
  { id: 'e2', source: 'fm-001', target: 'fm-003', label: 'father of', type: 'family' },
  { id: 'e3', source: 'fm-001', target: 'fm-004', label: 'father of', type: 'family' },
  { id: 'e4', source: 'fm-005', target: 'fm-001', label: 'mother of', type: 'family' },
  { id: 'e5', source: 'fm-001', target: 'doc-001', label: 'owns', type: 'ownership' },
  { id: 'e6', source: 'fm-001', target: 'doc-002', label: 'owns', type: 'ownership' },
  { id: 'e7', source: 'fm-001', target: 'doc-003', label: 'policyholder', type: 'ownership' },
  { id: 'e8', source: 'fm-002', target: 'doc-003', label: 'covered by', type: 'coverage' },
  { id: 'e9', source: 'fm-003', target: 'doc-003', label: 'covered by', type: 'coverage' },
  { id: 'e10', source: 'fm-004', target: 'doc-003', label: 'covered by', type: 'coverage' },
  { id: 'e11', source: 'fm-005', target: 'doc-004', label: 'patient', type: 'ownership' },
  { id: 'e12', source: 'fm-001', target: 'doc-005', label: 'owner', type: 'ownership' },
  { id: 'e13', source: 'fm-003', target: 'doc-006', label: 'student', type: 'ownership' },
  { id: 'e14', source: 'fm-001', target: 'doc-008', label: 'borrower', type: 'ownership' },
  { id: 'e15', source: 'doc-008', target: 'alert-003', label: 'triggers', type: 'alert' },
  { id: 'e16', source: 'fm-001', target: 'alert-002', label: 'must file', type: 'deadline' },
  { id: 'e17', source: 'doc-006', target: 'benefit-002', label: 'qualifies for', type: 'eligibility' },
  { id: 'e18', source: 'fm-003', target: 'benefit-002', label: 'eligible', type: 'eligibility' },
  { id: 'e19', source: 'doc-008', target: 'benefit-004', label: 'enables', type: 'eligibility' },
  { id: 'e20', source: 'doc-012', target: 'benefit-004', label: 'enables', type: 'eligibility' },
  { id: 'e21', source: 'fm-001', target: 'doc-012', label: 'insured', type: 'ownership' },
  { id: 'e22', source: 'doc-005', target: 'doc-008', label: 'secured by', type: 'relation' },
];

const DEMO_ACTIVITY_LOGS: ActivityLog[] = [
  { id: 'log-001', userId: 'user-001', action: 'Uploaded document', entityType: 'document', entityId: 'doc-006', details: 'Uploaded Aarav\'s Class 10 report card', createdAt: '2025-05-21T10:00:00Z' },
  { id: 'log-002', userId: 'user-001', action: 'AI analyzed document', entityType: 'document', entityId: 'doc-004', details: 'OCR extracted medical report for Kamla Devi', createdAt: '2025-05-16T09:30:00Z' },
  { id: 'log-003', userId: 'user-001', action: 'Benefit discovered', entityType: 'benefit', entityId: 'benefit-002', details: 'AI identified merit scholarship eligibility for Aarav', createdAt: '2025-05-22T14:00:00Z' },
  { id: 'log-004', userId: 'user-001', action: 'Alert created', entityType: 'alert', entityId: 'alert-002', details: 'ITR filing deadline alert set for July 31', createdAt: '2025-06-01T10:00:00Z' },
  { id: 'log-005', userId: 'user-001', action: 'Queried AI assistant', entityType: 'ai', entityId: 'query-001', details: 'Asked about tax deductions available', createdAt: '2025-06-02T15:00:00Z' },
];

// ============================================================
// Data Store Class
// ============================================================

class DataStore {
  private user: User = DEMO_USER;
  private familyMembers: FamilyMember[] = DEMO_FAMILY_MEMBERS;
  private documents: Document[] = DEMO_DOCUMENTS;
  private alerts: Alert[] = DEMO_ALERTS;
  private benefits: Benefit[] = DEMO_BENEFITS;
  private recommendations: AIRecommendation[] = DEMO_RECOMMENDATIONS;
  private knowledgeNodes: KnowledgeNode[] = DEMO_KNOWLEDGE_NODES;
  private knowledgeEdges: KnowledgeEdge[] = DEMO_KNOWLEDGE_EDGES;
  private activityLogs: ActivityLog[] = DEMO_ACTIVITY_LOGS;
  private notifications: Notification[] = [];

  // ---- User ----
  getUser(): User { return this.user; }
  updateUser(data: Partial<User>): User {
    this.user = { ...this.user, ...data, updatedAt: new Date().toISOString() };
    return this.user;
  }

  // ---- Family Members ----
  getFamilyMembers(): FamilyMember[] { return this.familyMembers; }
  getFamilyMember(id: string): FamilyMember | undefined { return this.familyMembers.find(m => m.id === id); }
  addFamilyMember(member: FamilyMember): FamilyMember {
    this.familyMembers.push(member);
    return member;
  }

  // ---- Documents ----
  getDocuments(): Document[] { return this.documents; }
  getDocument(id: string): Document | undefined { return this.documents.find(d => d.id === id); }
  getDocumentsByCategory(category: DocumentCategory): Document[] { return this.documents.filter(d => d.category === category); }
  getDocumentsByMember(memberId: string): Document[] { return this.documents.filter(d => d.familyMemberId === memberId); }
  addDocument(doc: Document): Document {
    this.documents.push(doc);
    return doc;
  }
  searchDocuments(query: string): Document[] {
    const q = query.toLowerCase();
    return this.documents.filter(d =>
      d.title.toLowerCase().includes(q) ||
      d.ocrText?.toLowerCase().includes(q) ||
      d.aiSummary?.toLowerCase().includes(q) ||
      d.tags.some(t => t.toLowerCase().includes(q))
    );
  }

  // ---- Alerts ----
  getAlerts(): Alert[] { return this.alerts; }
  getActiveAlerts(): Alert[] { return this.alerts.filter(a => a.status === 'active'); }
  getAlertsByType(type: AlertType): Alert[] { return this.alerts.filter(a => a.type === type); }
  addAlert(alert: Alert): Alert {
    this.alerts.push(alert);
    return alert;
  }
  updateAlert(id: string, data: Partial<Alert>): Alert | undefined {
    const idx = this.alerts.findIndex(a => a.id === id);
    if (idx === -1) return undefined;
    this.alerts[idx] = { ...this.alerts[idx], ...data };
    return this.alerts[idx];
  }

  // ---- Benefits ----
  getBenefits(): Benefit[] { return this.benefits; }
  getEligibleBenefits(): Benefit[] { return this.benefits.filter(b => b.status === 'eligible'); }

  // ---- Recommendations ----
  getRecommendations(): AIRecommendation[] { return this.recommendations; }
  getActiveRecommendations(): AIRecommendation[] { return this.recommendations.filter(r => !r.isActioned); }

  // ---- Knowledge Graph ----
  getKnowledgeGraph(): { nodes: KnowledgeNode[]; edges: KnowledgeEdge[] } {
    return { nodes: this.knowledgeNodes, edges: this.knowledgeEdges };
  }

  // ---- Activity Logs ----
  getActivityLogs(): ActivityLog[] { return this.activityLogs; }
  addActivityLog(log: ActivityLog): void { this.activityLogs.unshift(log); }

  // ---- Dashboard Stats ----
  getDashboardStats() {
    const activeAlerts = this.getActiveAlerts();
    const eligibleBenefits = this.getEligibleBenefits();
    const docs = this.getDocuments();

    const essentialDocTypes = ['identity', 'financial', 'insurance', 'tax', 'medical'];
    const missingPerMember = this.familyMembers.map(m => {
      const memberDocs = docs.filter(d => d.familyMemberId === m.id);
      const memberCategories = new Set(memberDocs.map(d => d.category));
      return essentialDocTypes.filter(t => !memberCategories.has(t as DocumentCategory))
        .map(t => `${m.name}: ${t}`);
    }).flat();

    const healthScore = Math.min(100, Math.round(
      (docs.length / (this.familyMembers.length * 5)) * 60 +
      (1 - activeAlerts.filter(a => a.priority === 'critical').length / Math.max(activeAlerts.length, 1)) * 20 +
      (eligibleBenefits.length > 0 ? 20 : 10)
    ));

    return {
      totalDocuments: docs.length,
      activeAlerts: activeAlerts.length,
      benefitsAvailable: eligibleBenefits.length,
      benefitsClaimed: this.benefits.filter(b => b.status === 'claimed').length,
      familyMembers: this.familyMembers.length,
      documentHealthScore: healthScore,
      missingDocuments: missingPerMember.slice(0, 5),
      recentUploads: docs.slice(-5).reverse(),
      upcomingDeadlines: activeAlerts.sort((a, b) => new Date(a.dueDate).getTime() - new Date(b.dueDate).getTime()).slice(0, 5),
      recommendations: this.getActiveRecommendations().slice(0, 5),
      intelligenceScore: Math.min(100, healthScore + Math.round(eligibleBenefits.length * 3)),
    };
  }
}

// Singleton
export const dataStore = new DataStore();
