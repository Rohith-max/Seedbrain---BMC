import { NextRequest, NextResponse } from 'next/server';

export const runtime = 'nodejs';
export const maxDuration = 30;

export async function POST(req: NextRequest) {
  try {
    const apiKey = process.env.GROQ_API_KEY;
    if (!apiKey) {
      return NextResponse.json(
        { error: 'GROQ_API_KEY is not configured in .env.local' },
        { status: 500 }
      );
    }

    // Receive the raw multipart form from the client
    const formData = await req.formData();
    const audioFile = formData.get('audio') as File | null;
    // Optional BCP-47 language code sent by the client (e.g. 'hi', 'kn', 'ta', 'en')
    const languageHint = (formData.get('language') as string | null) ?? null;

    if (!audioFile) {
      return NextResponse.json({ error: 'No audio file provided' }, { status: 400 });
    }

    // Forward to Groq Whisper (OpenAI-compatible endpoint)
    const whisperForm = new FormData();
    whisperForm.append('file', audioFile, audioFile.name || 'audio.webm');
    whisperForm.append('model', 'whisper-large-v3-turbo');
    whisperForm.append('response_format', 'verbose_json');
    // Constrain transcription to the user-selected language (no auto-detect)
    if (languageHint) {
      whisperForm.append('language', languageHint);
    }

    const whisperRes = await fetch('https://api.groq.com/openai/v1/audio/transcriptions', {
      method: 'POST',
      headers: {
        Authorization: `Bearer ${apiKey}`,
      },
      body: whisperForm,
    });

    if (!whisperRes.ok) {
      const errText = await whisperRes.text();
      console.error('Groq Whisper API error:', errText);
      return NextResponse.json(
        { error: `Groq Whisper API error: ${whisperRes.status}` },
        { status: whisperRes.status }
      );
    }

    const data = await whisperRes.json();

    // data.text  = transcript
    // data.language = detected language code (e.g. "hindi", "english", "tamil")
    return NextResponse.json({
      transcript: data.text ?? '',
      language: data.language ?? 'english',
    });
  } catch (err: unknown) {
    console.error('Whisper route error:', err);
    return NextResponse.json({ error: 'Internal server error' }, { status: 500 });
  }
}
