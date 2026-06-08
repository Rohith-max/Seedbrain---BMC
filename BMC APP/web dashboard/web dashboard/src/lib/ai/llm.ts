// Mock LLM Interface
// In production, this would use OpenAI, Anthropic, or an open-source model like Llama

import { retrieveRelevantContext } from './rag';

export interface LLMResponse {
  answer: string;
  sources: { title: string; type: string }[];
}

export async function askAI(question: string): Promise<LLMResponse> {
  // Retrieve context
  const ragResult = await retrieveRelevantContext(question);
  
  // Simulate LLM processing delay
  await new Promise(resolve => setTimeout(resolve, 1200));

  const lowerQ = question.toLowerCase();
  
  // Simple heuristic responses based on query
  if (lowerQ.includes('insurance') || lowerQ.includes('expire')) {
    return {
      answer: "Based on your documents, your family floater health insurance with Star Health expires on August 31, 2026. Your LIC Term plan expires on January 14, 2048.",
      sources: [
        { title: 'Star Health Policy', type: 'insurance' },
        { title: 'LIC Term Plan', type: 'insurance' }
      ]
    };
  }
  
  if (lowerQ.includes('diabetes') || lowerQ.includes('health')) {
    return {
      answer: "Kamla Devi's latest medical report from May 15, 2025 shows an HbA1c of 7.2%. The doctor recommended a follow-up in 3 months.",
      sources: [
        { title: 'Medical Report - Apollo', type: 'medical' }
      ]
    };
  }

  // Fallback generic response using RAG context
  return {
    answer: `I analyzed your documents to answer your question. Based on the context found in your vault, I can confirm we have records related to this. Let me know if you need specific details from: ${ragResult.documents.map(d => d.title).join(', ')}.`,
    sources: ragResult.documents.map(d => ({ title: d.title, type: d.category }))
  };
}
