// Mock Benefits Discovery Engine
// In production, this uses a rules engine against a database of government schemes

import { dataStore } from '@/lib/db/store';
import { Benefit } from '@/types';
import { generateId } from '@/lib/utils';

export async function runBenefitsEngine(): Promise<Benefit[]> {
  // Simulate heavy computation
  await new Promise(resolve => setTimeout(resolve, 2000));
  
  const family = dataStore.getFamilyMembers();
  const docs = dataStore.getDocuments();
  
  const discovered: Benefit[] = [];
  
  // Rule 1: Student with good grades -> Scholarship
  const hasStudentDocs = docs.some(d => d.category === 'education');
  if (hasStudentDocs) {
    discovered.push({
      id: `ben-${generateId()}`,
      title: 'National Scholarship for Meritorious Students',
      description: 'Based on Aarav\'s Class 10 results and household income brackets, you are eligible for the central scholarship scheme.',
      eligibilityCriteria: ['Student in Class 11/12', 'Above 80% marks', 'Income < 8 LPA'],
      matchScore: 0.95,
      status: 'eligible',
      source: 'Central Government',
      deadline: '2026-09-30T00:00:00Z',
      estimatedValue: '₹12,000/year',
      requiredDocuments: ['Aadhaar', 'Class 10 Marksheet', 'Income Certificate'],
      category: 'scholarship',
      matchedMembers: ['Aarav'],
      createdAt: new Date().toISOString()
    });
  }

  // Rule 2: Senior citizen -> Pension/Health
  const hasSeniors = family.some(f => {
    const age = new Date().getFullYear() - new Date(f.dateOfBirth).getFullYear();
    return age > 60;
  });

  if (hasSeniors) {
    discovered.push({
      id: `ben-${generateId()}`,
      title: 'Senior Citizen Health Scheme (PMJAY)',
      description: 'Kamla Devi is eligible for enhanced health coverage under the Ayushman Bharat PM-JAY scheme for senior citizens.',
      eligibilityCriteria: ['Age > 60 years', 'Valid Aadhaar'],
      matchScore: 0.88,
      status: 'eligible',
      source: 'Ministry of Health',
      estimatedValue: '₹5,00,000 cover',
      requiredDocuments: ['Aadhaar', 'Age Proof'],
      category: 'government_scheme',
      matchedMembers: ['Kamla'],
      createdAt: new Date().toISOString()
    });
  }

  return discovered;
}
