// Mock RAG (Retrieval-Augmented Generation) Pipeline
// In production, this would use a vector database (like Pinecone or pgvector) and embeddings

import { dataStore } from '@/lib/db/store';
import { Document } from '@/types';

export interface RAGResult {
  documents: Document[];
  relevanceScores: number[];
  contextString: string;
}

export async function retrieveRelevantContext(query: string, limit: number = 3): Promise<RAGResult> {
  // Simulate delay
  await new Promise(resolve => setTimeout(resolve, 600));

  const allDocs = dataStore.getDocuments();
  const lowerQuery = query.toLowerCase();
  
  // Simple keyword matching mock for RAG retrieval
  const matchedDocs = allDocs.filter(doc => 
    doc.title.toLowerCase().includes(lowerQuery) || 
    doc.tags.some(t => t.toLowerCase().includes(lowerQuery)) ||
    doc.category.toLowerCase().includes(lowerQuery)
  ).slice(0, limit);

  // If no match, return generic top documents
  const finalDocs = matchedDocs.length > 0 ? matchedDocs : allDocs.slice(0, limit);

  const contextString = finalDocs.map(d => 
    `Document: ${d.title}\nCategory: ${d.category}\nTags: ${d.tags.join(', ')}\nFile Size: ${d.fileSize}`
  ).join('\n\n');

  return {
    documents: finalDocs,
    relevanceScores: finalDocs.map(() => Math.random() * 0.4 + 0.6), // 0.6 to 1.0
    contextString
  };
}
