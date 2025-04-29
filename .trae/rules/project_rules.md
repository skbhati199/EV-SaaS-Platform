# PROJECT RULES – EV SaaS Platform

## GENERAL GUIDELINES
- All code must be peer-reviewed before merging to the main branch.
- Use task manager mcp server for all task tracking.
- Follow Semantic Commit Messages (feat:, fix:, chore:, refactor:, etc.).
- All new features must include automated tests.
- Environment variables should be documented and stored securely (e.g., `.env.example`).

## BACKEND RULES (Java Microservices)
- Use Spring Boot 3 with Java 21.
- Each service must be stateless and follow REST or gRPC conventions.
- Use PostgreSQL for transactional data and TimescaleDB for time-series data.
- Apply DTO and Entity separation in all layers.
- APIs must follow versioning (e.g., `/api/v1/...`).
- Handle all errors via a centralized exception handler.
- Use Flyway for DB migrations with proper versioning.
- Secrets and keys must never be hard-coded.

## FRONTEND RULES (Next.js 14)
- Use App Router (`src/app`) instead of Pages.
- Write all components using TypeScript and functional patterns.
- Use Tailwind CSS for styling, keep UI consistent with design system.
- Pages should be server-side rendered unless performance requires otherwise.
- Implement client-side validation and display loading/error states clearly.
- Keep components small, reusable, and tested.
- Use Zustand or Redux for state management (avoid prop drilling).

## INFRASTRUCTURE
- Dockerize every microservice.
- Use Kubernetes for deployment (Helm or Kustomize preferred).
- All logs must be centralized using ELK or Loki/Grafana stack.
- Use GitHub Actions for CI/CD, including build, test, and deploy steps.
- Monitor all services for uptime and performance (Prometheus preferred).

## SECURITY
- All APIs must be protected by OAuth2 or API keys.
- User passwords must be hashed using BCrypt or Argon2.
- OCPP and OCPI endpoints must use secure protocols (TLS).
- Validate all user inputs to prevent injection attacks.
- Rate limiting must be implemented on public APIs.

## TESTING
- Unit tests are mandatory for all modules.
- Use integration tests for service-to-service communication.
- Frontend components should be tested with Jest and React Testing Library.
- Maintain a minimum of 80% code coverage.
- All pull requests must pass CI before merging.

## VERSIONING & DEPLOYMENT
- Use semantic versioning (vX.Y.Z) for all releases.
- All deployments should be version-controlled and rollback-capable.
- Use blue/green or rolling deployments for production environments.
- Maintain changelogs for each release.

## DOCUMENTATION
- Document all APIs using Swagger/OpenAPI (backend) or typedoc (frontend).
- README.md must be updated when a major change is introduced.
- Use TODO.md and CHANGELOG.md actively.
- DB schema must be documented and ERD kept updated.

## COMMUNICATION
- Daily stand-ups and weekly sprint reviews are mandatory (if team is distributed).
- Use Slack, Teams, or Discord for all internal communication.
- Avoid assumptions—document and communicate changes clearly.

