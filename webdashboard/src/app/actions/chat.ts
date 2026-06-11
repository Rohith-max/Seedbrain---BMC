'use server';

export async function analyzeIntent(input: string): Promise<number> {
  const apiKey = process.env.GROQ_API_KEY;
  if (!apiKey) {
    console.warn('GROQ_API_KEY not set — using fallback intent.');
    return 7; // fallback
  }

  const systemPrompt = `You are an intent classifier for a smart family document assistant. 
Match the user's input to the most appropriate category ID below based on the semantic meaning:

0: Insurance & Policy Expiry (e.g., life insurance, health insurance, vehicle insurance, LIC, renewals)
1: Education & Scholarships (e.g., student marks, scholarships, KV, school)
2: Medical & Health Records (e.g., diabetes, hospital reports, doctor, health condition)
3: Home Loan & EMI (e.g., mortgage, SBI loan, monthly payments, outstanding amount)
4: Tax & Deductions (e.g., ITR, Section 80C, income tax, deductions, refunds)
5: Property details & taxes (e.g., apartment details, BBMP tax, property deed)
6: Government Benefits & Schemes (e.g., PMAY, Ayushman Bharat, general eligible schemes)
7: Unrelated/Fallback (If the query is too general or doesn't fit the above)

Output ONLY the single numeric ID (0-7) that best matches the user's intent. Do not output any other text, explanation, or punctuation.`;

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
          { role: 'user', content: input },
        ],
        temperature: 0.1,
        max_tokens: 10,
      }),
    });

    if (!res.ok) {
      console.error('Groq LLM intent error:', await res.text());
      return 7;
    }

    const data = await res.json();
    const answer = data.choices?.[0]?.message?.content?.trim();
    
    const id = parseInt(answer, 10);
    if (!isNaN(id) && id >= 0 && id <= 6) {
      return id;
    }
    return 7;
  } catch (err) {
    console.error('analyzeIntent error:', err);
    return 7;
  }
}

export async function translateText(text: string, targetLanguage: string): Promise<string> {
  const apiKey = process.env.GROQ_API_KEY;
  if (!apiKey || targetLanguage === 'english') return text;

  const systemPrompt = `You are an expert translator. Translate the following text into ${targetLanguage}. Maintain the original tone, markdown formatting, and bullet points. Output ONLY the translated text without any conversational filler or intro.`;

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
          { role: 'user', content: text },
        ],
        temperature: 0.2,
      }),
    });

    if (!res.ok) {
      console.error('Groq translation error:', await res.text());
      return text;
    }

    const data = await res.json();
    return data.choices?.[0]?.message?.content?.trim() || text;
  } catch (err) {
    console.error('translateText error:', err);
    return text;
  }
}
