// LLM Interface — powered by Groq (llama-3.3-70b-versatile)
// Uses Groq's OpenAI-compatible chat completions endpoint for ultra-fast inference.

import { retrieveRelevantContext } from './rag';

export interface LLMResponse {
  answer: string;
  sources: { title: string; type: string }[];
}

export async function askAI(question: string): Promise<LLMResponse> {
  // Retrieve relevant document context via RAG
  const ragResult = await retrieveRelevantContext(question);

  const apiKey = process.env.GROQ_LLM_API_KEY;

  // If no API key is set, fall back to a descriptive mock response
  if (!apiKey) {
    console.warn('GROQ_LLM_API_KEY not set — using fallback mock response.');
    return {
      answer: `I found relevant documents in your vault. To get AI-powered answers, please add your GROQ_LLM_API_KEY to .env.local. Relevant documents: ${ragResult.documents.map(d => d.title).join(', ')}.`,
      sources: ragResult.documents.map(d => ({ title: d.title, type: d.category })),
    };
  }

  const systemPrompt = `You are Nidhi, a smart, empathetic AI assistant helping Indian families manage their important documents — Aadhaar, PAN, insurance policies, medical records, property papers, and more.

Answer the user's question clearly and concisely based on the document context provided. If a document doesn't contain the exact information, say so politely. Always respond in the same language the user used (Hindi or English). Keep answers brief and actionable.

Document Context:
${ragResult.contextString}`;

  try {
    const res = await fetch('https://api.groq.com/openai/v1/chat/completions', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${apiKey}`,
      },
      body: JSON.stringify({
        model: 'llama-3.3-70b-versatile',
        messages: [
          { role: 'system', content: systemPrompt },
          { role: 'user', content: question },
        ],
        temperature: 0.4,
        max_tokens: 512,
      }),
    });

    if (!res.ok) {
      const errText = await res.text();
      console.error('Groq LLM error:', errText);
      throw new Error(`Groq API error: ${res.status}`);
    }

    const data = await res.json();
    const answer = data.choices?.[0]?.message?.content ?? 'Sorry, I could not generate a response.';

    return {
      answer,
      sources: ragResult.documents.map(d => ({ title: d.title, type: d.category })),
    };
  } catch (err) {
    console.error('askAI error:', err);
    return {
      answer: 'I encountered an error while fetching your answer. Please check your GROQ_LLM_API_KEY and try again.',
      sources: [],
    };
  }
}
