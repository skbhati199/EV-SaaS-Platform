API Documentation
GET http://localhost:8080/v3/api-docs - OpenAPI specification for the API Gateway
GET http://localhost:8080/swagger-ui.html - Swagger UI for exploring all APIs
GET http://localhost:8080/api-docs/services - List of all available microservices with API documentation
Auth Service
POST http://localhost:8080/api/auth/login - Authenticate and get access token
POST http://localhost:8080/api/auth/register - Register a new user
POST http://localhost:8080/api/auth/refresh - Refresh access token
User Service
GET http://localhost:8080/api/users - Get all users (paginated)
GET http://localhost:8080/api/users/{id} - Get user by ID
PUT http://localhost:8080/api/users/{id} - Update user
POST http://localhost:8080/api/users - Create a new user
Station Service
GET http://localhost:8080/api/stations - Get all stations (filtered)
GET http://localhost:8080/api/stations/{id} - Get station by ID
POST http://localhost:8080/api/stations - Create a new station
PUT http://localhost:8080/api/stations/{id} - Update a station
GET http://localhost:8080/api/stations/{id}/connectors - Get station connectors
Billing Service
GET http://localhost:8080/api/billing/invoices - Get all invoices
GET http://localhost:8080/api/billing/transactions - Get all billing transactions
Roaming Service
GET http://localhost:8080/api/roaming/locations - Get all roaming locations
GET http://localhost:8080/api/roaming/tokens - Get all roaming tokens
GET http://localhost:8080/ocpi/** - External OCPI endpoints
Smart Charging Service
GET http://localhost:8080/api/smart-charging/sessions - Get all smart charging sessions
GET http://localhost:8080/api/smart-charging/profiles - Get all smart charging profiles
Notification Service
GET http://localhost:8080/api/notifications/preferences - Get notification preferences
GET http://localhost:8080/api/notifications/history - Get notification history
How to Use Postman OpenAPI Spec
To use the OpenAPI specification with Postman:
Import the OpenAPI spec:
Open Postman
Click the "Import" button
Select "Link" or "Raw text" or "File"
Enter http://localhost:8080/v3/api-docs or paste the JSON/YAML content
Click "Import"
Generate a collection:
Postman will automatically generate a collection from the OpenAPI spec
All endpoints will be organized by service/tag
Request bodies, parameters, and response schemas will be pre-configured
Set up environment variables:
Create a new environment in Postman (top-right corner)
Add variables like baseUrl (e.g., http://localhost:8080)
Add authToken variable to store your JWT after login
Authentication:
After calling the login endpoint, use the script tab to automatically extract and set the JWT token:
Apply to openapi-serv...
;
For other requests, in the Authorization tab, select "Bearer Token" and use {{authToken}} as the token
Making requests:
Expand the imported collection to see all endpoints
Fill in required parameters (highlighted with )
Use the examples provided in the spec if available
Click "Send" to make the request
Working with multiple environments:
Create different environments for dev, staging, production
Switch environments as needed using the environment selector