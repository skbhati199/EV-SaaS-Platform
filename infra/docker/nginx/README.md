# Nginx Gateway for EV SaaS Platform

This directory contains the configuration for using Nginx as an API Gateway for the EV SaaS Platform microservices architecture, replacing the previous Spring Cloud Gateway implementation.

## Advantages of Using Nginx

1. **Lightweight and Efficient**: Nginx is a lightweight, high-performance web server that uses fewer resources than a Java-based gateway.
2. **Simplified Configuration**: The routing rules are defined in the `nginx.conf` file using a declarative syntax.
3. **Improved Performance**: Nginx is optimized for handling high-traffic loads and has a smaller memory footprint.
4. **Reliable**: Nginx is a battle-tested solution used by millions of websites worldwide.

## Configuration Overview

The Nginx configuration (`nginx.conf`) includes:

- **Upstream Definitions**: Each microservice is defined as an upstream to enable load balancing.
- **CORS Settings**: Cross-Origin Resource Sharing headers are set globally.
- **Route Mappings**: Each route from the previous API Gateway has been mapped to the appropriate microservice.
- **Health Check Endpoint**: A simple `/health` endpoint for container health checks.
- **Error Handling**: Service unavailability is handled with custom error responses.

## Differences from Spring Cloud Gateway

While Nginx handles most of the basic routing and load balancing functionality previously provided by Spring Cloud Gateway, some features need to be implemented differently:

1. **Service Discovery**: Without Eureka integration, services are referenced by their container names instead of being discovered dynamically.
2. **Circuit Breaking**: Nginx does not have built-in circuit breaking, but it does provide timeouts and error handling.
3. **JWT Validation**: Authentication needs to be handled by each microservice or by adding a specialized auth module to Nginx.

## Maintenance

When adding new microservices or routes:

1. Add the service to the `upstream` section in `nginx.conf`
2. Create a new `location` block for the service's endpoints
3. Update the Docker Compose file to include the new service in the dependencies for Nginx

## Monitoring

Nginx access and error logs are available in the `/var/log/nginx` directory, which is mounted as a volume in the Docker Compose configuration. 