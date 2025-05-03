Admin Portal Requirements use context7
Tech Stack:
Next.js 14 (App Router)
TypeScript
Core Features:
Authentication & Authorization
Secure login (JWT, OAuth2/Keycloak integration in future)
Role-based access (Admin, Operator, etc.)
Dashboard
Overview of stations, users, billing, and system health
Real-time status monitoring of EVSEs
Station Management
Register new stations
View/edit station details
Monitor station status (online/offline, charging, error)
OCPP command interface (remote start/stop, diagnostics, etc.)
User Management
List/search users
View/edit user profiles
Assign roles/permissions
Billing & Payments
View billing sessions and invoices
Manage payment gateway integration
Generate/download invoices
Tax handling
Roaming Management
Manage OCPI connections/partners
View roaming sessions and CDRs
Smart Charging
Configure load balancing and dynamic pricing rules
Monitor grid interface and V2G readiness
Notifications
View notification logs (email, SMS, push)
Configure notification templates
Analytics & Reports
Usage analytics (energy delivered, sessions, revenue)
Generate and download reports
Settings & Integrations
System configuration (protocols, integrations)
API keys, webhooks, etc.
Security
TLS, security headers, audit logs
Monitoring & Logging
Real-time logs and alerts
System health dashboard
User Website (User Portal) Requirements
Tech Stack:
Next.js 14 (App Router)
TypeScript
Core Features:
Authentication
User registration and login (JWT, OAuth2/Keycloak in future)
Password reset, 2FA (future)
Profile Management
View and edit profile
Manage RFID tokens (future)
Wallet management (future)
Station Finder
Map view of available stations
Filter/search by location, availability, charging type
Charging Session Management
Start/stop charging sessions
View session history and details
Billing & Payments
View billing history and invoices
Manage payment methods
Download invoices
Notifications
Receive and view notifications (charging status, promotions, etc.)
Support
Contact support/helpdesk
FAQ and documentation
Security
Secure user data, TLS, security headers
General Non-Functional Requirements
Responsive and accessible UI
Real-time updates (WebSocket for status, notifications)
Comprehensive error handling and user feedback
Integration with backend microservices via REST/gRPC/WebSocket
Internationalization (future)
Testing: Unit, integration, and E2E tests