// Mock Document Classifier
// In production, this would use a lightweight LLM or a custom trained classifier model

export interface ClassificationResult {
  category: 'identity' | 'financial' | 'property' | 'medical' | 'education' | 'insurance' | 'utility' | 'other';
  confidence: number;
  tags: string[];
}

export async function classifyDocument(text: string): Promise<ClassificationResult> {
  // Simulate delay
  await new Promise(resolve => setTimeout(resolve, 800));

  const lowerText = text.toLowerCase();
  
  if (lowerText.includes('policy') || lowerText.includes('premium')) {
    return { category: 'insurance', confidence: 0.95, tags: ['policy', 'health'] };
  }
  
  if (lowerText.includes('aadhaar') || lowerText.includes('pan')) {
    return { category: 'identity', confidence: 0.99, tags: ['kyc', 'government'] };
  }

  if (lowerText.includes('hospital') || lowerText.includes('doctor') || lowerText.includes('report')) {
    return { category: 'medical', confidence: 0.9, tags: ['health', 'report'] };
  }

  return { category: 'other', confidence: 0.5, tags: ['uncategorized'] };
}
