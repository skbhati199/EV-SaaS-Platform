/** @type {import('next').NextConfig} */
const nextConfig = {
  reactStrictMode: true,
  // Enable TypeScript error checking in dev mode
  typescript: {
    ignoreBuildErrors: process.env.NODE_ENV === 'production',
    tsconfigPath: 'tsconfig.json',
  },
  
  // Explicitly enable SWC and disable Babel
  swcMinify: true,
  experimental: {
    forceSwcTransforms: true,
  },
  
  // Runtime configuration for handling browser APIs
  serverRuntimeConfig: {
    // Will only be available on the server side
  },
  publicRuntimeConfig: {
    // Will be available on both server and client
    staticFolder: '/static',
  },
  
  images: { unoptimized: true },
  
  // Disable automatic static optimization for paths that need browser APIs
  // This tells Next.js not to statically optimize these pages at build time
  trailingSlash: true,

  // Disable SSR for problematic pages
  // Better error handling
  onDemandEntries: {
    // period (in ms) where the server will keep pages in the buffer
    maxInactiveAge: 25 * 1000,
    // number of pages that should be kept simultaneously without being disposed
    pagesBufferLength: 2,
  },

  // Configure CORS and HTTP proxy settings
  async headers() {
    return [
      {
        // Apply CORS headers to all routes
        source: '/:path*',
        headers: [
          { key: 'Access-Control-Allow-Credentials', value: 'true' },
          { key: 'Access-Control-Allow-Origin', value: '*' },
          { key: 'Access-Control-Allow-Methods', value: 'GET,POST,PUT,DELETE,OPTIONS,PATCH' },
          { key: 'Access-Control-Allow-Headers', value: 'X-Requested-With, X-HTTP-Method-Override, Content-Type, Accept, Authorization, Origin' },
        ],
      },
    ];
  },

  // API route proxy configuration
  async rewrites() {
    return [
      // Auth service endpoints
      {
        source: '/api/v1/auth/:path*',
        destination: 'http://api.nbevc.com/api/v1/auth/:path*',
      },
      // User endpoints
      {
        source: '/api/v1/users/:path*',
        destination: 'http://api.nbevc.com/api/v1/users/:path*',
      },
      // General API endpoints
      {
        source: '/api/:path*',
        destination: `${process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8082'}/api/:path*`,
      },
      // Assets, images, etc.
      {
        source: '/_next/:path*',
        destination: '/_next/:path*',
      }
    ]
  },
}

module.exports = nextConfig 