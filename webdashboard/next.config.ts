import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  /* config options here */
  webpack: (config, { isServer }) => {
    // Ignore node-specific modules when bundling for the browser
    // This is required for @xenova/transformers
    if (!isServer) {
      config.resolve.fallback = {
        ...config.resolve.fallback,
        fs: false,
        path: false,
        crypto: false,
      };
    }
    
    // Fix for "Cannot read properties of undefined (reading 'subarray')" in modern next.js
    config.resolve.alias = {
      ...config.resolve.alias,
      "onnxruntime-node$": false,
    };
    
    return config;
  },
};

export default nextConfig;
