/**
 * GOVERNMENT SCHEME INTELLIGENCE ENGINE
 * AI engine to identify eligibility for Indian government schemes
 * based on family documents and profile information.
 */

export interface FamilyMember {
  id: string;
  name: string;
  age: number;
  relation: string;
  documents: string[];
  income?: number;
  occupation?: string;
  state?: string;
}

export interface SchemeEligibility {
  schemeId: string;
  schemeName: string;
  eligible: boolean;
  eligibilityScore: number; // 0-100
  requiredDocuments: string[];
  missingDocuments: string[];
  reason: string;
  proactiveAction?: string;
}

export class GovernmentSchemeIntelligence {
  /**
   * Analyze family profile and documents to find eligible schemes
   */
  static analyzeEligibility(family: FamilyMember[]): SchemeEligibility[] {
    const eligibilities: SchemeEligibility[] = [];

    // PMAY - Housing Scheme
    eligibilities.push(this.checkPMAY(family));

    // Ayushman Bharat - Health Insurance
    eligibilities.push(this.checkAyushmanBharat(family));

    // Sukanya Samriddhi - Girl Child Savings
    eligibilities.push(this.checkSukanySamriddhi(family));

    // PM Kisan - Farmer Income Support
    eligibilities.push(this.checkPMKisan(family));

    // National Scholarship
    eligibilities.push(this.checkNationalScholarship(family));

    // Senior Citizen Benefits
    eligibilities.push(this.checkSeniorCitizenBenefits(family));

    // Disability Benefits
    eligibilities.push(this.checkDisabilityBenefits(family));

    return eligibilities.filter(e => e.eligible).sort((a, b) => b.eligibilityScore - a.eligibilityScore);
  }

  private static checkPMAY(family: FamilyMember[]): SchemeEligibility {
    // Check if family qualifies for housing scheme
    const head = family[0];
    const hasPropertyDocs = family.some(m => m.documents.includes('sale_deed') || m.documents.includes('khata'));
    const lowIncome = family.some(m => m.income && m.income < 1000000); // Less than 10L annually
    const hasIncomeProof = family.some(m => m.documents.includes('income_cert'));

    const eligible = lowIncome || hasIncomeProof;
    const missingDocs = [];
    
    if (!hasIncomeProof) missingDocs.push('Income Certificate');
    if (!hasPropertyDocs) missingDocs.push('Property Documents');

    return {
      schemeId: 'pmay',
      schemeName: 'Pradhan Mantri Awas Yojana (PMAY)',
      eligible: eligible && hasPropertyDocs,
      eligibilityScore: eligible && hasPropertyDocs ? 85 : (eligible ? 45 : 20),
      requiredDocuments: ['Income Certificate', 'Property Documents', 'Aadhaar'],
      missingDocuments: missingDocs,
      reason: eligible 
        ? 'Your family income qualifies for PMAY housing subsidy'
        : 'Income may exceed scheme limits',
      proactiveAction: eligible && !hasPropertyDocs 
        ? 'Upload property documents to complete PMAY application'
        : undefined
    };
  }

  private static checkAyushmanBharat(family: FamilyMember[]): SchemeEligibility {
    // Health insurance scheme - check income and age
    const hasAgeQualified = family.some(m => m.age > 60); // Senior citizens
    const lowIncome = family.some(m => m.income && m.income < 500000);
    const hasRationCard = family.some(m => m.documents.includes('ration_card'));
    const hasAadhaar = family.some(m => m.documents.includes('aadhaar'));

    const eligible = (hasAgeQualified || lowIncome) && (hasRationCard || lowIncome);

    return {
      schemeId: 'ayushman_bharat',
      schemeName: 'Ayushman Bharat - PMJAY',
      eligible: eligible && hasAadhaar,
      eligibilityScore: eligible && hasAadhaar ? 90 : (eligible ? 50 : 10),
      requiredDocuments: ['Aadhaar', 'Ration Card', 'Income Certificate'],
      missingDocuments: [
        !hasAadhaar ? 'Aadhaar' : null,
        !hasRationCard && lowIncome ? 'Ration Card' : null
      ].filter(Boolean) as string[],
      reason: eligible
        ? '₹5 lakh annual health coverage available for your family'
        : 'Income exceeds scheme criteria',
      proactiveAction: eligible && !hasAadhaar
        ? 'Register with Aadhaar to activate Ayushman Bharat coverage'
        : undefined
    };
  }

  private static checkSukanySamriddhi(family: FamilyMember[]): SchemeEligibility {
    // Girl child savings - check for daughters
    const girls = family.filter(m => m.relation === 'Daughter' || m.relation === 'Granddaughter');
    const youngGirls = girls.filter(m => m.age <= 10);
    const hasDocuments = youngGirls.length > 0 && youngGirls.some(g => g.documents.length > 0);

    return {
      schemeId: 'sukanya_samriddhi',
      schemeName: 'Sukanya Samriddhi Yojana (SSY)',
      eligible: youngGirls.length > 0,
      eligibilityScore: youngGirls.length > 0 ? 95 : 0,
      requiredDocuments: ['Birth Certificate', 'Aadhaar', 'Parent ID'],
      missingDocuments: !hasDocuments ? ['Birth Certificate', 'Parent ID Proof'] : [],
      reason: youngGirls.length > 0
        ? `Open SSY account for your ${youngGirls.length} daughter(s) - 7.6% returns, tax-free`
        : 'Sukanya Samriddhi is for girls aged 0-10 years',
      proactiveAction: youngGirls.length > 0
        ? `Register ${youngGirls.map(g => g.name).join(', ')} in SSY for ₹21 lakh+ by age 21`
        : undefined
    };
  }

  private static checkPMKisan(family: FamilyMember[]): SchemeEligibility {
    // Farmer support scheme
    const farmers = family.filter(m => m.occupation === 'Farmer' || m.documents.some(d => d.includes('land')));
    const hasLandProof = farmers.length > 0 && farmers.some(f => f.documents.length > 0);

    return {
      schemeId: 'pm_kisan',
      schemeName: 'PM Kisan Samman Nidhi',
      eligible: farmers.length > 0 && hasLandProof,
      eligibilityScore: farmers.length > 0 ? (hasLandProof ? 85 : 40) : 0,
      requiredDocuments: ['Land Ownership Proof', 'Aadhaar'],
      missingDocuments: farmers.length > 0 && !hasLandProof ? ['Land Ownership Document'] : [],
      reason: farmers.length > 0
        ? '₹6,000 annual income support for your farming family'
        : 'No farmers identified in family',
      proactiveAction: farmers.length > 0 && !hasLandProof
        ? 'Upload land documents to claim ₹6,000 annual PM Kisan benefit'
        : undefined
    };
  }

  private static checkNationalScholarship(family: FamilyMember[]): SchemeEligibility {
    // Student scholarship scheme
    const students = family.filter(m => m.age >= 15 && m.age <= 25 && m.documents.some(d => d.includes('education')));
    const lowIncome = family.some(m => m.income && m.income < 800000);

    return {
      schemeId: 'national_scholarship',
      schemeName: 'National Scholarship Portal (NSP)',
      eligible: students.length > 0 && lowIncome,
      eligibilityScore: students.length > 0 && lowIncome ? 80 : (students.length > 0 ? 30 : 0),
      requiredDocuments: ['School/College ID', 'Income Certificate', 'Bank Details'],
      missingDocuments: students.length > 0 && lowIncome ? ['Income Certificate'] : [],
      reason: students.length > 0 && lowIncome
        ? `${students.length} student(s) may qualify for scholarships up to ₹2.5 lakh`
        : students.length > 0 ? 'Family income may exceed scholarship limits' : 'No eligible students',
      proactiveAction: students.length > 0 && lowIncome
        ? `Register ${students.map(s => s.name).join(', ')} for scholarships by deadline`
        : undefined
    };
  }

  private static checkSeniorCitizenBenefits(family: FamilyMember[]): SchemeEligibility {
    // Senior citizen welfare
    const seniors = family.filter(m => m.age >= 60);
    const hasIncomeProof = seniors.some(m => m.documents.includes('income_cert'));

    return {
      schemeId: 'senior_citizen_benefits',
      schemeName: 'Senior Citizen Welfare Schemes',
      eligible: seniors.length > 0,
      eligibilityScore: seniors.length > 0 ? 88 : 0,
      requiredDocuments: ['Age Proof', 'Aadhaar', 'Income Certificate'],
      missingDocuments: seniors.length > 0 && !hasIncomeProof ? ['Income Certificate'] : [],
      reason: seniors.length > 0
        ? `${seniors.length} senior(s) eligible for pension, healthcare, travel concessions`
        : 'No senior citizens in family',
      proactiveAction: seniors.length > 0
        ? `Enroll ${seniors.map(s => s.name).join(', ')} for senior citizen benefits`
        : undefined
    };
  }

  private static checkDisabilityBenefits(family: FamilyMember[]): SchemeEligibility {
    // Disability support schemes
    const hasDisabilityCert = family.some(m => m.documents.includes('disability_certificate'));
    const disabled = family.filter(m => m.documents.some(d => d.includes('disability')));

    return {
      schemeId: 'disability_benefits',
      schemeName: 'Disability Welfare Schemes',
      eligible: disabled.length > 0 && hasDisabilityCert,
      eligibilityScore: disabled.length > 0 && hasDisabilityCert ? 90 : (disabled.length > 0 ? 50 : 0),
      requiredDocuments: ['Disability Certificate', 'Aadhaar', 'Income Certificate'],
      missingDocuments: disabled.length > 0 && !hasDisabilityCert ? ['Disability Certificate'] : [],
      reason: disabled.length > 0
        ? `${disabled.length} family member(s) eligible for pension, training, aids & appliances`
        : 'No disabilities reported',
      proactiveAction: disabled.length > 0 && !hasDisabilityCert
        ? 'Get disability certificate from CMHO to access welfare benefits'
        : undefined
    };
  }

  /**
   * Generate proactive alerts for missing documents
   */
  static generateMissingDocumentAlerts(family: FamilyMember[], eligibilities: SchemeEligibility[]): string[] {
    const alerts: string[] = [];

    eligibilities.forEach(e => {
      if (e.missingDocuments.length > 0 && e.eligibilityScore > 50) {
        alerts.push(
          `You're close to qualifying for ${e.schemeName}. Missing: ${e.missingDocuments.join(', ')}`
        );
      }
    });

    return alerts;
  }
}
