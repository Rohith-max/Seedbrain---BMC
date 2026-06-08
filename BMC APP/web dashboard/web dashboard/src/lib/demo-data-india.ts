/**
 * DEMO DATA - INDIA FIRST
 * Realistic Indian household data for demo mode.
 * Uses actual Indian names, addresses, document formats.
 */

export const DEMO_INDIAN_FAMILY_PROFILES = [
  {
    id: 'family-bengaluru-001',
    name: 'Sharma Family',
    city: 'Bengaluru',
    state: 'Karnataka',
    familyHead: 'Rajesh Sharma (58)',
    description: 'Joint family with 2 generations',
    members: [
      { name: 'Rajesh Sharma', age: 58, relation: 'Father', occupation: 'IT Professional' },
      { name: 'Anita Sharma', age: 55, relation: 'Mother', occupation: 'Homemaker' },
      { name: 'Arjun Sharma', age: 28, relation: 'Son', occupation: 'Software Engineer' },
      { name: 'Priya Sharma', age: 25, relation: 'Daughter', occupation: 'Student' },
      { name: 'Kavya Sharma', age: 5, relation: 'Granddaughter', occupation: 'Student' },
    ],
    documents: {
      aadhaar: 3,
      pan_card: 3,
      income_certificate: 2,
      property_documents: 1,
      bank_passbook: 3,
      lic_policy: 2,
      health_insurance: 3,
      school_certificates: 1,
    },
    income: 1500000,
    qualifiesFor: ['Sukanya Samriddhi', 'National Scholarship']
  },
  {
    id: 'family-mysuru-001',
    name: 'Krishna Family',
    city: 'Mysuru',
    state: 'Karnataka',
    familyHead: 'H. Krishna (72)',
    description: 'Senior citizen with family',
    members: [
      { name: 'H. Krishna', age: 72, relation: 'Grandfather', occupation: 'Retired Teacher' },
      { name: 'Lakshmi Krishna', age: 68, relation: 'Grandmother', occupation: 'Retired' },
      { name: 'Suresh', age: 45, relation: 'Son', occupation: 'Businessman' },
      { name: 'Roopa', age: 42, relation: 'Daughter-in-law', occupation: 'Accountant' },
      { name: 'Aditya', age: 18, relation: 'Grandson', occupation: 'Engineering Student' },
    ],
    documents: {
      aadhaar: 5,
      pension_certificate: 2,
      senior_citizen_id: 2,
      health_insurance: 3,
      property_documents: 1,
      bank_passbook: 5,
    },
    income: 600000,
    qualifiesFor: ['Senior Citizen Benefits', 'Ayushman Bharat', 'National Scholarship']
  },
  {
    id: 'family-tamil-nadu-001',
    name: 'Srinivasan Family',
    city: 'Chennai',
    state: 'Tamil Nadu',
    familyHead: 'Srinivasan (45)',
    description: 'Farmer family with government benefits',
    members: [
      { name: 'Srinivasan', age: 45, relation: 'Father', occupation: 'Farmer' },
      { name: 'Gayathri', age: 42, relation: 'Mother', occupation: 'Farmer' },
      { name: 'Arjun', age: 16, relation: 'Son', occupation: 'School Student' },
      { name: 'Anjali', age: 14, relation: 'Daughter', occupation: 'School Student' },
      { name: 'Devi', age: 8, relation: 'Daughter', occupation: 'School Student' },
    ],
    documents: {
      aadhaar: 5,
      land_documents: 1,
      income_certificate: 1,
      ration_card: 1,
      school_certificates: 2,
      health_insurance: 2,
    },
    income: 300000,
    qualifiesFor: ['PM Kisan', 'Ayushman Bharat', 'National Scholarship', 'Sukanya Samriddhi']
  },
  {
    id: 'family-maharashtra-001',
    name: 'Deshmukh Family',
    city: 'Pune',
    state: 'Maharashtra',
    familyHead: 'Vikram Deshmukh (38)',
    description: 'Small business family',
    members: [
      { name: 'Vikram Deshmukh', age: 38, relation: 'Father', occupation: 'Business Owner' },
      { name: 'Savitri', age: 35, relation: 'Mother', occupation: 'Homemaker' },
      { name: 'Nikhil', age: 12, relation: 'Son', occupation: 'School Student' },
      { name: 'Divya', age: 9, relation: 'Daughter', occupation: 'School Student' },
    ],
    documents: {
      aadhaar: 4,
      pan_card: 2,
      business_license: 1,
      income_certificate: 1,
      property_documents: 1,
      lic_policy: 2,
      school_certificates: 2,
    },
    income: 800000,
    qualifiesFor: ['Sukanya Samriddhi', 'PMAY']
  },
];

export const DEMO_INDIAN_DOCUMENTS = [
  {
    id: 'doc-aadhaar-001',
    type: 'Aadhaar Card',
    owner: 'Rajesh Sharma',
    ownerID: '1234 5678 9012',
    issueDate: '2010-05-15',
    expiryDate: 'Lifetime',
    status: 'Valid'
  },
  {
    id: 'doc-pan-001',
    type: 'PAN Card',
    owner: 'Rajesh Sharma',
    panNumber: 'AAAPK5055K',
    issueDate: '1995-03-20',
    expiryDate: 'Lifetime',
    status: 'Valid'
  },
  {
    id: 'doc-lic-001',
    type: 'LIC Policy',
    owner: 'Rajesh Sharma',
    policyNumber: 'LIC/B123/45678',
    amount: '₹10,00,000',
    issueDate: '2005-08-10',
    maturityDate: '2030-08-10',
    nextPremiumDue: '2025-06-15',
    premiumAmount: '₹15,000'
  },
  {
    id: 'doc-degree-001',
    type: 'Degree Certificate',
    owner: 'Priya Sharma',
    degree: 'B.Tech Computer Science',
    institution: 'VIT Vellore',
    year: '2024',
    status: 'Pursuing'
  },
];

export const DEMO_FINANCIAL_TRACKING = [
  {
    id: 'tracking-lic-001',
    type: 'LIC Premium',
    owner: 'Rajesh Sharma',
    amount: '₹15,000',
    dueDate: '2025-06-15',
    status: 'Upcoming',
    priority: 'High'
  },
  {
    id: 'tracking-emi-001',
    type: 'Home Loan EMI',
    amount: '₹45,000',
    dueDate: '2025-06-05',
    status: 'Upcoming',
    priority: 'High'
  },
  {
    id: 'tracking-fd-001',
    type: 'FD Maturity',
    amount: '₹5,00,000',
    maturityDate: '2025-08-20',
    interest: '₹75,000',
    status: 'Pending',
    priority: 'Medium'
  },
  {
    id: 'tracking-tax-001',
    type: 'Income Tax Filing',
    dueDate: '2025-07-31',
    documents: ['Form 16', 'Bank Statements'],
    status: 'Not Started',
    priority: 'High'
  },
  {
    id: 'tracking-school-001',
    type: 'School Fees',
    amount: '₹50,000',
    dueDate: '2025-06-30',
    student: 'Kavya Sharma',
    status: 'Upcoming',
    priority: 'High'
  },
];

export const DEMO_GOVERNMENT_ALERTS = [
  {
    id: 'alert-sukanya-001',
    type: 'Scheme Eligibility',
    title: 'Your daughter qualifies for Sukanya Samriddhi Yojana',
    description: 'Kavya (5 years) can benefit from 7.6% returns, tax-free savings scheme',
    action: 'Open account now',
    priority: 'High',
    daysToDeadline: 'No deadline - Open anytime'
  },
  {
    id: 'alert-scholarship-001',
    type: 'Missing Document',
    title: 'Income Certificate needed for scholarship',
    description: 'Priya (B.Tech student) needs income certificate to apply for national scholarship',
    action: 'Get from Tahsildar office',
    priority: 'Medium',
    daysToDeadline: '45 days before application deadline'
  },
  {
    id: 'alert-tax-001',
    type: 'Deadline Reminder',
    title: 'Income Tax Filing Deadline: July 31, 2025',
    description: 'Submit ITR before deadline to avoid penalties',
    action: 'Organize documents',
    priority: 'High',
    daysToDeadline: '57 days remaining'
  },
];

export const DEMO_FAMILY_INSIGHTS = [
  {
    type: 'Financial Health',
    score: 82,
    status: 'Good',
    insights: [
      '✓ LIC premiums up to date',
      '✓ FD investments growing',
      'alert: Home loan EMI at 45% of income',
      '→ Consider consolidating loans'
    ]
  },
  {
    type: 'Document Organization',
    score: 65,
    status: 'Average',
    insights: [
      '✓ All identity documents updated',
      'alert: Missing some education certificates',
      'alert: Property documents not digitized',
      '→ Recommended: Back up all documents'
    ]
  },
  {
    type: 'Benefits Realization',
    score: 72,
    status: 'Good',
    insights: [
      '✓ Accessing Sukanya Samriddhi benefits',
      '✓ Ayushman Bharat coverage active',
      'alert: PM Kisan not applicable (non-farmer)',
      '→ 3 unclaimed schemes identified'
    ]
  },
];

export const DEMO_QUICK_ACTIONS = [
  {
    title: 'Upload School Certificates',
    description: 'For scholarship eligibility',
    category: 'Document',
    icon: 'FileText'
  },
  {
    title: 'Prepare Tax Documents',
    description: 'Due July 31, 2025',
    category: 'Financial',
    icon: 'Clipboard'
  },
  {
    title: 'Register in Sukanya Samriddhi',
    description: 'Open account for Kavya',
    category: 'Benefit',
    icon: 'Coins'
  },
  {
    title: 'Update Property Records',
    description: 'For home loan documentation',
    category: 'Property',
    icon: 'Home'
  },
];

export const DEMO_FAMILY_GRAPH = {
  head: { name: 'Rajesh Sharma', role: 'Family Head' },
  relationships: [
    { from: 'Rajesh Sharma', to: 'Anita Sharma', relation: 'Spouse' },
    { from: 'Rajesh Sharma', to: 'Arjun Sharma', relation: 'Son' },
    { from: 'Rajesh Sharma', to: 'Priya Sharma', relation: 'Daughter' },
    { from: 'Arjun Sharma', to: 'Kavya Sharma', relation: 'Father' },
  ],
  documents: [
    { docType: 'Aadhaar', members: 3 },
    { docType: 'PAN', members: 3 },
    { docType: 'Bank Passbook', members: 3 },
    { docType: 'LIC Policy', members: 2 },
    { docType: 'Health Insurance', members: 3 },
  ]
};
