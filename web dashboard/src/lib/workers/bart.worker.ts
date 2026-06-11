import { pipeline, env } from '@xenova/transformers';

// Skip local model checks since we are running in browser
env.allowLocalModels = false;

class PipelineSingleton {
  static task: any = 'summarization';
  // Use a distilled BART model which is optimized for browser usage
  static model = 'Xenova/distilbart-cnn-6-6';
  static instance: any = null;

  static async getInstance(progress_callback: (info: any) => void) {
    if (this.instance === null) {
      this.instance = await pipeline(this.task, this.model, { progress_callback });
    }
    return this.instance;
  }
}

self.addEventListener('message', async (event) => {
  const { text, action, id } = event.data;

  if (action === 'summarize') {
    try {
      // Get the pipeline, initializing it and sending progress updates if necessary
      const summarizer = await PipelineSingleton.getInstance((x: any) => {
        self.postMessage({ id, status: 'progress', progress: x });
      });

      // Run the model on the input text
      const output = await summarizer(text, {
        max_new_tokens: 150,
        temperature: 0.7,
        repetition_penalty: 2.0,
      });

      self.postMessage({
        id,
        status: 'complete',
        result: output[0].summary_text
      });
    } catch (error: any) {
      self.postMessage({ id, status: 'error', error: error.message });
    }
  }
});
