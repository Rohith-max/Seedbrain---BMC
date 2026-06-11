/**
 * INDIA-FIRST CONSTANTS
 * All strings, data structures, and configurations are designed specifically
 * for Indian households, government schemes, and workflows.
 */

export const INDIAN_CITIES = [
  { name: 'Bengaluru', state: 'Karnataka', code: 'BLR' },
  { name: 'Mysuru', state: 'Karnataka', code: 'MYS' },
  { name: 'Chennai', state: 'Tamil Nadu', code: 'CHE' },
  { name: 'Hyderabad', state: 'Telangana', code: 'HYD' },
  { name: 'Mumbai', state: 'Maharashtra', code: 'MUM' },
  { name: 'Pune', state: 'Maharashtra', code: 'PUN' },
  { name: 'Delhi', state: 'Delhi', code: 'DEL' },
  { name: 'Kochi', state: 'Kerala', code: 'KOC' },
  { name: 'Kolkata', state: 'West Bengal', code: 'KOL' },
  { name: 'Ahmedabad', state: 'Gujarat', code: 'AHM' },
];

export const INDIAN_STATES = [
  'Andhra Pradesh', 'Arunachal Pradesh', 'Assam', 'Bihar', 'Chhattisgarh',
  'Goa', 'Gujarat', 'Haryana', 'Himachal Pradesh', 'Jharkhand',
  'Karnataka', 'Kerala', 'Madhya Pradesh', 'Maharashtra', 'Manipur',
  'Meghalaya', 'Mizoram', 'Nagaland', 'Odisha', 'Punjab',
  'Rajasthan', 'Sikkim', 'Tamil Nadu', 'Telangana', 'Tripura',
  'Uttar Pradesh', 'Uttarakhand', 'West Bengal', 'Delhi', 'Puducherry'
];

export const INDIAN_DOCUMENT_CATEGORIES = {
  identity: {
    label: 'पहचान पत्र (Identity)',
    documents: [
      { id: 'aadhaar', label: 'Aadhaar Card', icon: 'IdCard' },
      { id: 'pan', label: 'PAN Card', icon: 'Tag' },
      { id: 'voter_id', label: 'Voter ID', icon: 'Vote' },
      { id: 'passport', label: 'Passport', icon: 'Book' },
      { id: 'driving_license', label: 'Driving License', icon: 'Car' },
    ]
  },
  education: {
    label: 'शिक्षा (Education)',
    documents: [
      { id: 'sslc_marks', label: 'SSLC Marks Card', icon: 'ClipboardList' },
      { id: 'puc_certificate', label: 'PUC Certificate', icon: 'FileText' },
      { id: 'degree_certificate', label: 'Degree Certificate', icon: 'GraduationCap' },
      { id: 'engineering_marks', label: 'Engineering Marks Cards', icon: 'BarChart' },
      { id: 'scholarship_docs', label: 'Scholarship Documents', icon: 'Coins' },
    ]
  },
  financial: {
    label: 'वित्तीय (Financial)',
    documents: [
      { id: 'bank_passbook', label: 'Bank Passbook', icon: 'Building' },
      { id: 'fixed_deposit', label: 'Fixed Deposit Certificate', icon: 'Banknote' },
      { id: 'lic_policy', label: 'LIC Policy', icon: 'FileCheck' },
      { id: 'mutual_funds', label: 'Mutual Fund Statements', icon: 'TrendingUp' },
      { id: 'demat_account', label: 'Demat Account Statement', icon: 'Lock' },
      { id: 'income_tax', label: 'Income Tax Documents', icon: 'FileEdit' },
      { id: 'form_16', label: 'Form 16', icon: 'Clipboard' },
      { id: 'form_26as', label: 'Form 26AS', icon: 'Clipboard' },
    ]
  },
  property: {
    label: 'संपत्ति (Property)',
    documents: [
      { id: 'sale_deed', label: 'Sale Deed', icon: 'Home' },
      { id: 'khata', label: 'Khata Document', icon: 'Scroll' },
      { id: 'ec_certificate', label: 'EC Certificate', icon: 'CheckCircle' },
      { id: 'property_tax', label: 'Property Tax Receipt', icon: 'CreditCard' },
      { id: 'encumbrance_cert', label: 'Encumbrance Certificate', icon: 'Search' },
    ]
  },
  healthcare: {
    label: 'स्वास्थ्य (Healthcare)',
    documents: [
      { id: 'ayushman_docs', label: 'Ayushman Bharat Documents', icon: 'Hospital' },
      { id: 'health_insurance', label: 'Health Insurance', icon: 'Shield' },
      { id: 'discharge_summary', label: 'Hospital Discharge Summary', icon: 'FileText' },
      { id: 'medical_records', label: 'Medical Records', icon: 'Files' },
      { id: 'prescriptions', label: 'Prescription Archive', icon: 'Pill' },
    ]
  },
  vehicles: {
    label: 'वाहन (Vehicles)',
    documents: [
      { id: 'rc_book', label: 'RC Book', icon: 'Car' },
      { id: 'vehicle_insurance', label: 'Vehicle Insurance', icon: 'ClipboardList' },
      { id: 'pollution_cert', label: 'Pollution Certificate', icon: 'Settings' },
      { id: 'vehicle_dl', label: 'Driving License', icon: 'IdCard' },
    ]
  },
  government: {
    label: 'सरकारी (Government)',
    documents: [
      { id: 'pension_records', label: 'Pension Records', icon: 'User' },
      { id: 'ration_card', label: 'Ration Card', icon: 'ShoppingCart' },
      { id: 'caste_cert', label: 'Caste Certificate', icon: 'FileBox' },
      { id: 'income_cert', label: 'Income Certificate', icon: 'Briefcase' },
      { id: 'domicile_cert', label: 'Domicile Certificate', icon: 'MapPin' },
      { id: 'e_shram', label: 'E-Shram Card', icon: 'IdCard' },
    ]
  }
};

export const GOVERNMENT_SCHEMES = [
  {
    id: 'pmay',
    name: 'Pradhan Mantri Awas Yojana',
    shortName: 'PMAY',
    description: 'Housing scheme for economically weaker sections',
    targetAudience: ['Low Income', 'Homeless'],
    requiredDocuments: ['Aadhaar', 'Income Certificate', 'Property Documents'],
    benefits: '₹2.67 lakh subsidy on home purchase/construction',
    state: 'National',
    category: 'Housing'
  },
  {
    id: 'ayushman_bharat',
    name: 'Ayushman Bharat - PMJAY',
    shortName: 'PM-JAY',
    description: 'National health insurance scheme',
    targetAudience: ['Low Income', 'Uninsured', 'Senior Citizens'],
    requiredDocuments: ['Aadhaar', 'Ration Card'],
    benefits: '₹5 lakh annual health insurance coverage',
    state: 'National',
    category: 'Healthcare'
  },
  {
    id: 'sukanya_samriddhi',
    name: 'Sukanya Samriddhi Yojana',
    shortName: 'SSY',
    description: 'Savings scheme for girl child',
    targetAudience: ['Parents of Girls', 'Girls (0-10 years)'],
    requiredDocuments: ['Birth Certificate', 'Aadhaar'],
    benefits: '7.6% interest rate, tax-free returns',
    state: 'National',
    category: 'Savings'
  },
  {
    id: 'pm_kisan',
    name: 'PM Kisan Samman Nidhi',
    shortName: 'PM-Kisan',
    description: 'Income support for farmers',
    targetAudience: ['Farmers', 'Rural'],
    requiredDocuments: ['Land Ownership Proof', 'Aadhaar'],
    benefits: '₹6,000 annually in 3 installments',
    state: 'National',
    category: 'Agriculture'
  },
  {
    id: 'national_scholarship',
    name: 'National Scholarship Portal Schemes',
    shortName: 'NSP',
    description: 'Educational scholarships for students',
    targetAudience: ['Students', 'Low Income'],
    requiredDocuments: ['School/College ID', 'Income Certificate'],
    benefits: 'Up to ₹2,50,000 annually',
    state: 'National',
    category: 'Education'
  },
  {
    id: 'senior_citizen_benefits',
    name: 'Senior Citizen Welfare Schemes',
    shortName: 'SC',
    description: 'Benefits and support for senior citizens',
    targetAudience: ['Senior Citizens (60+)'],
    requiredDocuments: ['Age Proof', 'Aadhaar', 'Income Certificate'],
    benefits: 'Pension, healthcare, concessions',
    state: 'National & State-specific',
    category: 'Social Welfare'
  },
  {
    id: 'disability_benefits',
    name: 'Disability Welfare Schemes',
    shortName: 'DWS',
    description: 'Support for persons with disabilities',
    targetAudience: ['Persons with Disabilities'],
    requiredDocuments: ['Disability Certificate', 'Aadhaar'],
    benefits: 'Pension, vocational training, aids & appliances',
    state: 'National & State-specific',
    category: 'Social Welfare'
  }
];

export const INDIAN_FAMILY_RELATIONSHIPS = [
  'Self',
  'Spouse',
  'Father',
  'Mother',
  'Son',
  'Daughter',
  'Grandfather',
  'Grandmother',
  'Grandson',
  'Granddaughter',
  'Brother',
  'Sister',
  'Uncle',
  'Aunt',
  'Cousin',
  'Niece',
  'Nephew',
  'Father-in-law',
  'Mother-in-law',
  'Sibling-in-law',
];

export const SUPPORTED_LANGUAGES = [
  { code: 'en', name: 'English', nativeName: 'English' },
  { code: 'hi', name: 'Hindi', nativeName: 'हिन्दी' },
  { code: 'kn', name: 'Kannada', nativeName: 'ಕನ್ನಡ' },
  { code: 'ta', name: 'Tamil', nativeName: 'தமிழ்' },
  { code: 'te', name: 'Telugu', nativeName: 'తెలుగు' },
  { code: 'ml', name: 'Malayalam', nativeName: 'മലയാളം' },
  { code: 'mr', name: 'Marathi', nativeName: 'मराठी' },
  { code: 'bn', name: 'Bengali', nativeName: 'বাঙ্গালি' },
];

export const INDIAN_FINANCIAL_TRACKING = {
    categories: [
      { id: 'lic_premium', label: 'LIC Premiums', icon: 'Clipboard' },
      { id: 'emi', label: 'EMI Payments', icon: 'CreditCard' },
      { id: 'loan_renewal', label: 'Loan Renewals', icon: 'Building' },
      { id: 'tax_deadline', label: 'Tax Deadlines', icon: 'Calendar' },
      { id: 'sip_investment', label: 'SIP Investments', icon: 'TrendingUp' },
      { id: 'fd_maturity', label: 'FD Maturity', icon: 'Coins' },
      { id: 'credit_card_due', label: 'Credit Card Due', icon: 'CreditCard' },
      { id: 'utility_bills', label: 'Utility Bills', icon: 'Zap' },
      { id: 'insurance_renewal', label: 'Insurance Renewal', icon: 'Shield' },
      { id: 'school_fees', label: 'School Fees', icon: 'GraduationCap' },
    ]
};

export const FINANCIAL_REMINDERS = {
  high_priority: ['Tax Deadline', 'Insurance Renewal', 'LIC Premium'],
  medium_priority: ['EMI Payment', 'Credit Card Due', 'School Fees'],
  low_priority: ['FD Maturity', 'SIP Investment'],
};
