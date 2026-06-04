import { HeroSection } from '@/components/landing/hero-section';
import { FeaturesSection } from '@/components/landing/features-section';
import { IndiaFeaturesSection } from '@/components/landing/india-features';
import { AIOrchestration } from '@/components/landing/ai-orchestration';
import { StorySection } from '@/components/landing/story-section';
import { TrustSection } from '@/components/landing/trust-section';
import { Footer } from '@/components/landing/footer';

export default function LandingPage() {
  return (
    <div className="min-h-screen flex flex-col">
      <main className="flex-1">
        <HeroSection />
        <IndiaFeaturesSection />
        <FeaturesSection />
        <AIOrchestration />
        <TrustSection />
      </main>
      <Footer />
    </div>
  );
}
