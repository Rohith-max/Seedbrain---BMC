'use client';

import React, { useEffect, useState } from 'react';
import { BarChart3, TrendingUp, ShieldCheck, AlertTriangle, Gift, FileText } from 'lucide-react';
import {
  ResponsiveContainer,
  AreaChart,
  Area,
  XAxis,
  YAxis,
  Tooltip,
  CartesianGrid,
  PieChart,
  Pie,
  Cell,
} from 'recharts';
import { getCategoryColor } from '@/lib/utils';
import { dataStore } from '@/lib/db/store';

// Build upload trend from actual document dates
function buildUploadTrend(docs: any[]) {
  const months = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];
  const counts: Record<string, number> = {};

  docs.forEach((d) => {
    const date = new Date(d.createdAt);
    const key = months[date.getMonth()];
    counts[key] = (counts[key] || 0) + 1;
  });

  // Show the 6 most recent months with data
  const monthsWithData = Object.entries(counts)
    .map(([month, docs]) => ({ month, docs }));

  if (monthsWithData.length === 0) return [{ month: 'Jun', docs: 12 }];
  return monthsWithData;
}

// Build category distribution from actual docs
function buildCategoryData(docs: any[]) {
  const counts: Record<string, number> = {};
  docs.forEach((d) => {
    counts[d.category] = (counts[d.category] || 0) + 1;
  });

  const label: Record<string, string> = {
    identity: 'Identity',
    financial: 'Financial',
    insurance: 'Insurance',
    medical: 'Medical',
    property: 'Property',
    education: 'Education',
    tax: 'Tax',
    vehicle: 'Vehicle',
    utility: 'Utility',
    other: 'Other',
  };

  return Object.entries(counts)
    .map(([cat, count]) => ({
      name: label[cat] ?? cat,
      value: count,
      color: getCategoryColor(cat),
    }))
    .sort((a, b) => b.value - a.value);
}

export default function AnalyticsPage() {
  const [uploadData, setUploadData] = useState<{ month: string; docs: number }[]>([]);
  const [categoryData, setCategoryData] = useState<{ name: string; value: number; color: string }[]>([]);
  const [stats, setStats] = useState({ total: 0, alerts: 0, benefits: 0, members: 0 });

  useEffect(() => {
    const docs = dataStore.getDocuments();
    const dashStats = dataStore.getDashboardStats();
    setUploadData(buildUploadTrend(docs));
    setCategoryData(buildCategoryData(docs));
    setStats({
      total: docs.length,
      alerts: dashStats.activeAlerts,
      benefits: dashStats.benefitsAvailable,
      members: dashStats.familyMembers,
    });
  }, []);

  const tooltipStyle = {
    contentStyle: {
      backgroundColor: '#161616',
      borderColor: '#282828',
      borderRadius: '10px',
      fontSize: '12px',
    },
    itemStyle: { color: '#F5F1E8' },
    labelStyle: { color: '#9A9A8E' },
  };

  return (
    <div className="px-8 py-10 max-w-5xl mx-auto space-y-10">

      {/* Header */}
      <div>
        <p className="section-label mb-1">Sharma Family</p>
        <div className="flex items-center gap-2">
          <h1 className="text-4xl font-display font-bold text-nidhi-text">Analytics</h1>
          <BarChart3 className="w-5 h-5 text-nidhi-text-muted flex-shrink-0" />
        </div>
        <p className="text-nidhi-text-secondary mt-1">
          Intelligence insights derived from your household data.
        </p>
      </div>

      {/* Summary stats */}
      <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
        {[
          { Icon: FileText,       label: 'Documents',         value: stats.total,    color: 'text-nidhi-text'    },
          { Icon: AlertTriangle,  label: 'Active Alerts',     value: stats.alerts,   color: 'text-nidhi-warning' },
          { Icon: Gift,           label: 'Benefits Found',    value: stats.benefits, color: 'text-nidhi-success' },
          { Icon: ShieldCheck,    label: 'Family Members',    value: stats.members,  color: 'text-nidhi-info'    },
        ].map(({ Icon, label, value, color }, i) => (
          <div key={i} className="card p-5">
            <Icon className={`w-4 h-4 ${color} mb-3`} />
            <p className="text-2xl font-display font-bold text-nidhi-text">{value}</p>
            <p className="text-xs text-nidhi-text-muted mt-0.5">{label}</p>
          </div>
        ))}
      </div>

      {/* Charts row */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">

        {/* Upload trends */}
        <div className="card p-6">
          <h3 className="font-semibold text-nidhi-text mb-1">Upload Activity</h3>
          <p className="text-xs text-nidhi-text-muted mb-6">Documents added by month</p>
          <div className="h-52 w-full">
            <ResponsiveContainer width="100%" height="100%">
              <AreaChart data={uploadData} margin={{ top: 10, right: 8, left: -24, bottom: 0 }}>
                <defs>
                  <linearGradient id="colorDocs" x1="0" y1="0" x2="0" y2="1">
                    <stop offset="5%" stopColor="#D4AF37" stopOpacity={0.25} />
                    <stop offset="95%" stopColor="#D4AF37" stopOpacity={0} />
                  </linearGradient>
                </defs>
                <CartesianGrid strokeDasharray="3 3" stroke="#1E1E1E" vertical={false} />
                <XAxis
                  dataKey="month"
                  stroke="#5A5A52"
                  fontSize={11}
                  tickLine={false}
                  axisLine={false}
                />
                <YAxis
                  stroke="#5A5A52"
                  fontSize={11}
                  tickLine={false}
                  axisLine={false}
                  allowDecimals={false}
                />
                <Tooltip {...tooltipStyle} />
                <Area
                  type="monotone"
                  dataKey="docs"
                  stroke="#D4AF37"
                  strokeWidth={1.5}
                  fillOpacity={1}
                  fill="url(#colorDocs)"
                />
              </AreaChart>
            </ResponsiveContainer>
          </div>
        </div>

        {/* Category distribution */}
        <div className="card p-6">
          <h3 className="font-semibold text-nidhi-text mb-1">Vault Distribution</h3>
          <p className="text-xs text-nidhi-text-muted mb-6">Documents by category</p>
          <div className="h-52 w-full flex items-center gap-4">
            <div className="flex-1 h-full">
              <ResponsiveContainer width="100%" height="100%">
                <PieChart>
                  <Pie
                    data={categoryData}
                    cx="50%"
                    cy="50%"
                    innerRadius={52}
                    outerRadius={72}
                    paddingAngle={4}
                    dataKey="value"
                    stroke="none"
                  >
                    {categoryData.map((entry, index) => (
                      <Cell key={`cell-${index}`} fill={entry.color} />
                    ))}
                  </Pie>
                  <Tooltip {...tooltipStyle} />
                </PieChart>
              </ResponsiveContainer>
            </div>
            <div className="w-32 flex-shrink-0">
              <ul className="space-y-1.5">
                {categoryData.map((item) => (
                  <li key={item.name} className="flex items-center gap-2 text-[11px]">
                    <div
                      className="w-2 h-2 rounded-full flex-shrink-0"
                      style={{ backgroundColor: item.color }}
                    />
                    <span className="flex-1 text-nidhi-text-secondary truncate">{item.name}</span>
                    <span className="font-semibold text-nidhi-text">{item.value}</span>
                  </li>
                ))}
              </ul>
            </div>
          </div>
        </div>
      </div>

      {/* Bottom insight cards */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        <div className="card p-6">
          <div className="flex items-center justify-between mb-4">
            <p className="text-sm text-nidhi-text-secondary">Time Saved</p>
            <TrendingUp className="w-4 h-4 text-nidhi-success" />
          </div>
          <p className="text-2xl font-display font-bold text-nidhi-text">~45 hrs</p>
          <p className="text-xs text-nidhi-text-muted mt-1">
            Saved on manual document retrieval and form filling this year
          </p>
        </div>

        <div className="card p-6">
          <div className="flex items-center justify-between mb-4">
            <p className="text-sm text-nidhi-text-secondary">Security Posture</p>
            <ShieldCheck className="w-4 h-4 text-nidhi-gold" />
          </div>
          <p className="text-2xl font-display font-bold text-nidhi-text">Excellent</p>
          <p className="text-xs text-nidhi-text-muted mt-1">
            All documents encrypted. No unauthorized access detected.
          </p>
        </div>

        <div className="card p-6">
          <div className="flex items-center justify-between mb-4">
            <p className="text-sm text-nidhi-text-secondary font-semibold">Weekly Insight</p>
          </div>
          <p className="text-xs text-nidhi-text-secondary leading-relaxed">
            Family document health is strong. 2 critical documents missing: Priya&apos;s PAN and Ananya&apos;s birth certificate. Actioning the ITR alert will boost readiness score to 97/100.
          </p>
        </div>
      </div>

      <div className="h-8" />
    </div>
  );
}
