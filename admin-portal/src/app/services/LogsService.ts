/**
 * Interface for a log entry
 */
export interface LogEntry {
  timestamp: string;
  message: string;
  service: string;
  level: string;
}

/**
 * LogsService provides access to system logs from Loki
 * Note: Currently uses mock data; in production it should use a backend API
 * that proxies requests to Loki for security reasons.
 */
export class LogsService {
  /**
   * Fetch logs from Loki
   * @param service Service to filter by (or 'all' for all services)
   * @param searchQuery Text to search for in log messages
   * @param limit Maximum number of logs to return
   * @param startTime Start time for logs (ISO string)
   * @param endTime End time for logs (ISO string)
   */
  static async fetchLogs(
    service: string = 'all',
    searchQuery: string = '',
    limit: number = 100,
    startTime: string = new Date(Date.now() - 24 * 60 * 60 * 1000).toISOString(), // 24h ago
    endTime: string = new Date().toISOString()
  ): Promise<LogEntry[]> {
    // In production, this would make an API call to your backend
    // which would proxy the request to Loki
    
    // Example API URL (to be implemented on backend):
    // const url = `/api/logs?service=${service}&query=${searchQuery}&limit=${limit}&start=${startTime}&end=${endTime}`;

    // For now, we'll just return mock data
    return this.getMockLogs(limit, service, searchQuery);
  }

  /**
   * Generate mock logs for development purposes
   * @param count Number of logs to generate
   * @param serviceFilter Service to filter by
   * @param searchText Text to search for in logs
   */
  private static getMockLogs(count: number, serviceFilter: string, searchText: string): Promise<LogEntry[]> {
    return new Promise((resolve) => {
      setTimeout(() => {
        const services = ['auth-service', 'user-service', 'station-service', 'billing-service', 'api-gateway', 'smart-charging'];
        const levels = ['info', 'warn', 'error', 'debug'];
        
        const messages = [
          'Application started successfully',
          'User logged in successfully',
          'Failed to connect to database',
          'Charging session started for user',
          'Payment processed successfully',
          'API request received from client',
          'Charging station went offline',
          'Error processing transaction',
          'Scheduled task completed',
          'Cache invalidated',
          'Connection to Kafka established',
          'Message published to topic',
          'Message consumed from topic',
          'User registration completed',
          'Invalid credentials provided',
          'Rate limit exceeded for API',
          'Database query executed in 120ms',
          'CPU usage at 85%',
          'Memory usage at 60%',
          'Disk space running low',
          'Authentication token expired',
          'Session timeout for user'
        ];
        
        const result: LogEntry[] = [];
        const now = new Date();
        
        for (let i = 0; i < count * 2; i++) { // Generate more logs than needed to allow for filtering
          const timestamp = new Date(now.getTime() - Math.random() * 86400000).toISOString();
          const service = services[Math.floor(Math.random() * services.length)];
          const level = levels[Math.floor(Math.random() * levels.length)];
          const message = messages[Math.floor(Math.random() * messages.length)];
          
          // Apply filters
          if (serviceFilter !== 'all' && service !== serviceFilter) continue;
          if (searchText && !message.toLowerCase().includes(searchText.toLowerCase())) continue;
          
          result.push({
            timestamp,
            service,
            level,
            message
          });
          
          if (result.length >= count) break;
        }
        
        resolve(result.sort((a, b) => new Date(b.timestamp).getTime() - new Date(a.timestamp).getTime()));
      }, 300);
    });
  }
}

export default LogsService; 