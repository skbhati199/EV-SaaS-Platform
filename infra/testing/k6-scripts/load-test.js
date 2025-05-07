import http from 'k6/http';
import { sleep, check } from 'k6';
import { Counter, Rate, Trend } from 'k6/metrics';

// Custom metrics
const serviceErrors = new Counter('service_errors');
const successRate = new Rate('success_rate');
const requestDuration = new Trend('request_duration');

// Test configuration
export const options = {
  vus: __ENV.VUS || 10,
  duration: __ENV.DURATION || '30s',
  thresholds: {
    'http_req_duration': ['p(95)<500'], // 95% of requests should be below 500ms
    'success_rate': ['rate>0.95'],       // 95% success rate
  },
};

// Base URL from environment
const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';
console.log(`Using base URL: ${BASE_URL}`);

// List of endpoints to test
const endpoints = [
  { name: 'Auth Health', path: '/auth/health', method: 'GET' },
  { name: 'User Health', path: '/api/users/health', method: 'GET' },
  { name: 'Station Health', path: '/api/stations/health', method: 'GET' },
  { name: 'Billing Health', path: '/api/billing/health', method: 'GET' },
  { name: 'Notification Health', path: '/api/notifications/health', method: 'GET' },
  { name: 'Roaming Health', path: '/api/roaming/health', method: 'GET' },
  { name: 'Smart Charging Health', path: '/api/smart-charging/health', method: 'GET' },
];

// Helper function for getting headers
function getHeaders() {
  return {
    'Content-Type': 'application/json',
  };
}

// Main test function
export default function() {
  // Select a random endpoint
  const endpoint = endpoints[Math.floor(Math.random() * endpoints.length)];
  const url = `${BASE_URL}${endpoint.path}`;
  const params = { headers: getHeaders() };
  
  console.log(`Testing: ${endpoint.name} - ${url}`);

  // Make request and measure timing
  const startTime = new Date().getTime();
  let response;
  
  if (endpoint.method === 'GET') {
    response = http.get(url, params);
  } else if (endpoint.method === 'POST') {
    response = http.post(url, JSON.stringify(endpoint.body || {}), params);
  }
  
  const endTime = new Date().getTime();
  const duration = endTime - startTime;
  
  // Record request duration
  requestDuration.add(duration);
  
  // Check if response was successful
  const isSuccess = check(response, {
    'status is 200': (r) => r.status === 200,
    'response time < 500ms': (r) => r.timings.duration < 500,
  });
  
  // Record success rate and errors
  successRate.add(isSuccess);
  
  if (!isSuccess) {
    serviceErrors.add(1, { endpoint: endpoint.name });
    console.error(`Error with ${endpoint.name}: Status ${response.status}`);
  }
  
  // Prevent sending too many requests at once
  sleep(1);
}

// Function to run after completion
export function handleSummary(data) {
  console.log('Load test completed');
  console.log('Summary:');
  console.log(`- VUs: ${options.vus}`);
  console.log(`- Duration: ${options.duration}`);
  console.log(`- Success Rate: ${data.metrics.success_rate.values.rate}`);
  console.log(`- Avg Request Duration: ${data.metrics.http_req_duration.values.avg}ms`);
  
  return {
    'stdout': JSON.stringify(data),
  };
} 