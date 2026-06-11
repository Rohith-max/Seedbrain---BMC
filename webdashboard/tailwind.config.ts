import type { Config } from "tailwindcss";

const config: Config = {
  content: [
    "./src/pages/**/*.{js,ts,jsx,tsx,mdx}",
    "./src/components/**/*.{js,ts,jsx,tsx,mdx}",
    "./src/app/**/*.{js,ts,jsx,tsx,mdx}",
  ],
  theme: {
    extend: {
      colors: {
        "nidhi-black":         "#080808",
        "nidhi-deep":          "#0C0C0C",
        "nidhi-surface":       "#111111",
        "nidhi-card":          "#161616",
        "nidhi-elevated":      "#1C1C1C",
        "nidhi-border":        "#282828",
        "nidhi-border-subtle": "#1E1E1E",
        "nidhi-text":          "#F5F1E8",
        "nidhi-text-secondary":"#9A9A8E",
        "nidhi-text-muted":    "#5A5A52",
        "nidhi-gold":          "#D4AF37",
        "nidhi-gold-light":    "#E8D080",
        "nidhi-gold-dim":      "#8B7322",
        "nidhi-saffron":       "#FF8038",
        "nidhi-green-accent":  "#10B981",
        "nidhi-blue-accent":   "#38BDF8",
        "nidhi-success":       "#22C55E",
        "nidhi-warning":       "#F59E0B",
        "nidhi-danger":        "#EF4444",
        "nidhi-info":          "#3B82F6",
      },
      fontFamily: {
        sans:    ["Inter", "-apple-system", "BlinkMacSystemFont", "Segoe UI", "sans-serif"],
        display: ["Geist", "Inter", "sans-serif"],
      },
    },
  },
  plugins: [],
};

export default config;
