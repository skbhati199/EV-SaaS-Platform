/** @type {import('next').NextConfig} */
const nextConfig = {
  reactStrictMode: true,
  async rewrites() {
    return [
      // API requests - proxy to backend
      {
        source: '/api/:path*',
        destination: 'http://localhost:8080/api/:path*',
      },
      // Explicitly handle versioned API paths
      {
        source: '/api/v1/:path*',
        destination: 'http://localhost:8080/api/v1/:path*',
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