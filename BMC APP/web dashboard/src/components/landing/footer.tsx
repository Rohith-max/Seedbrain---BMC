import React from 'react';
import Link from 'next/link';
import { APP_NAME, APP_NAME_HINDI } from '@/lib/constants';
import { Heart } from 'lucide-react';

export function Footer() {
  return (
    <footer className="bg-nidhi-black border-t border-nidhi-border pt-16 pb-8">
      <div className="container mx-auto px-4 md:px-6">
        <div className="grid grid-cols-1 md:grid-cols-4 gap-12 mb-12">
          
          <div className="col-span-1 md:col-span-2">
            <Link href="/" className="inline-block mb-4">
              <span className="text-2xl font-display font-bold tracking-wider">
                {APP_NAME} <span className="text-nidhi-gold">{APP_NAME_HINDI}</span>
              </span>
            </Link>
            <p className="text-nidhi-text-secondary text-sm max-w-sm mb-6">
              India's first AI-powered household operating system. Manage documents, track deadlines, and secure your family's future intelligently.
            </p>
            <div className="flex gap-4">
              {/* Social placeholders */}
              <div className="w-8 h-8 rounded-full bg-nidhi-surface border border-nidhi-border"></div>
              <div className="w-8 h-8 rounded-full bg-nidhi-surface border border-nidhi-border"></div>
              <div className="w-8 h-8 rounded-full bg-nidhi-surface border border-nidhi-border"></div>
            </div>
          </div>

          <div>
            <h4 className="font-semibold mb-4 text-nidhi-text">Product</h4>
            <ul className="space-y-3 text-sm text-nidhi-text-secondary">
              <li><Link href="#" className="hover:text-nidhi-gold transition-colors">Document Vault</Link></li>
              <li><Link href="#" className="hover:text-nidhi-gold transition-colors">AI Assistant</Link></li>
              <li><Link href="#" className="hover:text-nidhi-gold transition-colors">Smart Deadlines</Link></li>
              <li><Link href="#" className="hover:text-nidhi-gold transition-colors">Benefit Discovery</Link></li>
              <li><Link href="#" className="hover:text-nidhi-gold transition-colors">Security</Link></li>
            </ul>
          </div>

          <div>
            <h4 className="font-semibold mb-4 text-nidhi-text">Company</h4>
            <ul className="space-y-3 text-sm text-nidhi-text-secondary">
              <li><Link href="#" className="hover:text-nidhi-gold transition-colors">About Us</Link></li>
              <li><Link href="#" className="hover:text-nidhi-gold transition-colors">Careers</Link></li>
              <li><Link href="#" className="hover:text-nidhi-gold transition-colors">Privacy Policy</Link></li>
              <li><Link href="#" className="hover:text-nidhi-gold transition-colors">Terms of Service</Link></li>
              <li><Link href="#" className="hover:text-nidhi-gold transition-colors">Contact</Link></li>
            </ul>
          </div>
        </div>

        <div className="pt-8 border-t border-nidhi-border-subtle text-center text-sm text-nidhi-text-muted flex flex-col md:flex-row justify-between items-center">
          <p>© {new Date().getFullYear()} {APP_NAME} Technologies. All rights reserved.</p>
          <p className="mt-2 md:mt-0 flex items-center justify-center">Made with <Heart className="w-4 h-4 mx-1 text-red-500 fill-current" /> in India</p>
        </div>
      </div>
    </footer>
  );
}
