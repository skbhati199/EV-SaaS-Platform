/** @type {import('next').NextConfig} */
const nextConfig = {
  reactStrictMode: true,
  async rewrites() {
    return [
      // Auth service endpoints
      {
        source: '/api/v1/auth/:path*',
        destination: 'http://192.168.29.133:8081/api/v1/auth/:path*',
      },
      // User endpoints
      {
        source: '/api/v1/users/:path*',
        destination: 'http://192.168.29.133:8081/api/v1/users/:path*',
      },
      // General API endpoints
      {
        source: '/api/:path*',
        destination: 'http://192.168.29.133:8081/api/:path*',
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