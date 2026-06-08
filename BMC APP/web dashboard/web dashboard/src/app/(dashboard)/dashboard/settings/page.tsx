'use client';

import React from 'react';
import { Settings, Shield, Bell, Moon, Download, Key } from 'lucide-react';
import { useAuthStore } from '@/lib/store/auth-store';
import { Button } from '@/components/ui/button';

export default function SettingsPage() {
  const { user } = useAuthStore();

  return (
    <div className="space-y-6 max-w-4xl mx-auto">
      
      <div>
        <h1 className="text-3xl font-display font-bold text-nidhi-text mb-1 flex items-center gap-2">
          Settings <Settings className="w-6 h-6 text-nidhi-text-muted" />
        </h1>
        <p className="text-nidhi-text-secondary">Manage your vault preferences and security.</p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-4 gap-6">
        
        {/* Sidebar Tabs */}
        <div className="md:col-span-1 space-y-1">
          <button className="w-full text-left px-4 py-2.5 rounded-lg bg-nidhi-gold/10 text-nidhi-gold font-medium text-sm">Account Profile</button>
          <button className="w-full text-left px-4 py-2.5 rounded-lg text-nidhi-text-secondary hover:bg-nidhi-surface text-sm transition-colors">Security & Privacy</button>
          <button className="w-full text-left px-4 py-2.5 rounded-lg text-nidhi-text-secondary hover:bg-nidhi-surface text-sm transition-colors">Notifications</button>
          <button className="w-full text-left px-4 py-2.5 rounded-lg text-nidhi-text-secondary hover:bg-nidhi-surface text-sm transition-colors">Preferences</button>
        </div>

        {/* Content Area */}
        <div className="md:col-span-3 space-y-6">
          
          <div className="card-premium p-6">
            <h3 className="text-lg font-semibold text-nidhi-text mb-6">Profile Information</h3>
            <div className="space-y-4">
              <div className="flex items-center gap-6">
                <div className="w-20 h-20 rounded-full bg-nidhi-surface border border-nidhi-border flex items-center justify-center text-2xl font-display text-nidhi-gold">
                  {user?.name?.charAt(0) || 'U'}
                </div>
                <Button variant="outline" size="sm" className="bg-nidhi-card border-nidhi-border text-nidhi-text">Change Avatar</Button>
              </div>
              
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 pt-4">
                <div>
                  <label className="block text-sm text-nidhi-text-muted mb-1.5">Full Name</label>
                  <input type="text" className="input-premium" defaultValue={user?.name} />
                </div>
                <div>
                  <label className="block text-sm text-nidhi-text-muted mb-1.5">Email Address</label>
                  <input type="email" className="input-premium" defaultValue={user?.email} disabled />
                </div>
                <div>
                  <label className="block text-sm text-nidhi-text-muted mb-1.5">Phone Number</label>
                  <input type="tel" className="input-premium" defaultValue={user?.phone || ''} />
                </div>
                <div>
                  <label className="block text-sm text-nidhi-text-muted mb-1.5">Language Preference</label>
                  <select className="input-premium appearance-none bg-nidhi-surface" defaultValue="en">
                    <option value="en">English</option>
                    <option value="hi">Hindi (हिन्दी)</option>
                  </select>
                </div>
              </div>
              <div className="pt-4 flex justify-end">
                <Button variant="nidhiGold">Save Changes</Button>
              </div>
            </div>
          </div>

          <div className="card-premium p-6">
            <h3 className="text-lg font-semibold text-nidhi-text mb-4">Security</h3>
            <div className="space-y-4">
              <div className="flex items-center justify-between p-4 rounded-lg border border-nidhi-border bg-nidhi-surface/50">
                <div className="flex items-center gap-3">
                  <div className="p-2 rounded bg-nidhi-gold/10 text-nidhi-gold"><Key className="w-5 h-5"/></div>
                  <div>
                    <div className="font-medium text-nidhi-text text-sm">Two-Factor Authentication</div>
                    <div className="text-xs text-nidhi-text-muted">Add an extra layer of security</div>
                  </div>
                </div>
                <Button variant="outline" size="sm" className="bg-nidhi-card">Enable</Button>
              </div>
              <div className="flex items-center justify-between p-4 rounded-lg border border-nidhi-border bg-nidhi-surface/50">
                <div className="flex items-center gap-3">
                  <div className="p-2 rounded bg-nidhi-success/10 text-nidhi-success"><Shield className="w-5 h-5"/></div>
                  <div>
                    <div className="font-medium text-nidhi-text text-sm">Vault Encryption Key</div>
                    <div className="text-xs text-nidhi-text-muted">Managed securely by Nidhi</div>
                  </div>
                </div>
                <Button variant="outline" size="sm" className="bg-nidhi-card">View Key</Button>
              </div>
            </div>
          </div>

          <div className="card-premium p-6">
            <h3 className="text-lg font-semibold text-nidhi-danger mb-4">Data Management</h3>
            <div className="p-4 rounded-lg border border-nidhi-danger/20 bg-nidhi-danger/5">
              <div className="flex justify-between items-center mb-2">
                <div className="font-medium text-nidhi-text text-sm">Export All Data</div>
                <Button variant="outline" size="sm" className="border-nidhi-border text-nidhi-text bg-transparent hover:bg-nidhi-surface">
                  <Download className="w-4 h-4 mr-2"/> Export
                </Button>
              </div>
              <p className="text-xs text-nidhi-text-muted">Download all your documents and metadata in a ZIP archive.</p>
            </div>
          </div>

        </div>
      </div>
    </div>
  );
}
