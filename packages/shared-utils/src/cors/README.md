# CORS Configuration for EV SaaS Platform

This package provides a standardized Cross-Origin Resource Sharing (CORS) configuration that can be used across all microservices in the EV SaaS Platform.

## What is CORS?

Cross-Origin Resource Sharing (CORS) is a security feature implemented by browsers that restricts web pages from making requests to a different domain than the one that served the original page. This prevents potentially malicious websites from making unauthorized requests to your APIs.

## Implementation Details

The CORS configuration in this package:

1. Allows requests from specified origins (localhost development environments and *.nbevc.com domains)
2. Allows common HTTP methods (GET, POST, PUT, DELETE, PATCH, OPTIONS)
3. Allows necessary headers for authentication and content negotiation
4. Exposes response headers that clients may need to access
5. Supports credentials (cookies, authorization headers)
6. Sets an appropriate max-age for preflight caching

## How to Use in Your Microservice

### Step 1: Include the Shared Package

Make sure your microservice has access to the shared-utils package, either by:

- Adding it as a dependency in your pom.xml (Maven)
- Adding it in your build.gradle (Gradle)

### Step 2: Enable Component Scanning

Add the `com.ev.shared.cors` package to your component scan configuration in your main application class:

```java
@SpringBootApplication
@ComponentScan(basePackages = {
    "com.ev.yourservice",
    "com.ev.shared.cors"
})
public class YourServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(YourServiceApplication.class, args);
    }
}
```

### Step 3: Add CORS Configuration Properties

Add these properties to your `application.properties` or `application.yml` file:

```properties
# CORS Configuration
app.cors.allowed-origins=http://localhost:3000,http://localhost:3001,https://*.nbevc.com,https://*.ev-platform.nbevc.com
app.cors.allowed-methods=GET,POST,PUT,DELETE,OPTIONS,PATCH
app.cors.allowed-headers=Authorization,Content-Type,X-Requested-With,Accept,Origin,Access-Control-Request-Method,Access-Control-Request-Headers
app.cors.exposed-headers=Authorization,Content-Type,Access-Control-Allow-Origin,Access-Control-Allow-Credentials
app.cors.max-age=3600
```

### Step 4: Remove Any Existing CORS Configuration

To avoid conflicts, remove any existing CORS configuration classes from your microservice.

## Security Considerations

While this configuration allows for development with localhost origins, in production environments, you should consider:

1. Restricting `allowed-origins` to only your production domains
2. Using environment-specific configuration for different deployment environments
3. Periodically reviewing and updating the allowed origins list

## Troubleshooting CORS Issues

If you encounter CORS errors in your browser console:

1. Check the error message for specific details about which origin, method, or headers are being blocked
2. Verify that the requesting domain is included in the `app.cors.allowed-origins` property
3. Ensure the HTTP method being used is in the `app.cors.allowed-methods` list
4. Check that any custom headers used by your application are in the `app.cors.allowed-headers` list
5. For credentials issues, make sure `withCredentials: true` is set in your frontend API calls

## Additional Resources

- [MDN Web Docs: CORS](https://developer.mozilla.org/en-US/docs/Web/HTTP/CORS)
- [Spring Framework CORS Support Documentation](https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#mvc-cors) 