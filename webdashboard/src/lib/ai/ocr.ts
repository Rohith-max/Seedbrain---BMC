// Mock OCR Engine
// In production, this would use Tesseract.js, AWS Textract, or Google Cloud Vision

export interface OCRResult {
  text: string;
  confidence: number;
  language: string;
  pages: number;
  extractedEntities: Record<string, string>;
}

export async function performOCR(fileBuffer: Buffer | ArrayBuffer, mimeType: string): Promise<OCRResult> {
  // Simulate network delay
  await new Promise(resolve => setTimeout(resolve, 1500));

  // Mock extraction logic based on file type
  return {
    text: "This is a mock OCR extraction of the document. It contains dates like 12-05-2025 and names like Rajesh Sharma.",
    confidence: 0.92,
    language: 'eng',
    pages: 1,
    extractedEntities: {
      "Name": "Rajesh Sharma",
      "Date": "2025-05-12",
      "DocumentID": "DOC-998877"
    }
  };
}
