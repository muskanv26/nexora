# Nexora: Technical & Product Roadmap

This roadmap defines the sequential, iterative execution strategy to design, build, test, and deploy Nexora. Each phase builds upon the previous, starting from baseline configurations to full production launch.

---

## Phase 1 - Foundation
*   **Goal**: Establish the structural base, setup shared environment infrastructure, and build the scaffolding for the frontend and backend microservices.
*   **Features**:
    *   Initialize frontend boilerplate (React/TypeScript, CSS Variables system).
    *   Initialize backend API framework (Node.js/Express or Python/FastAPI).
    *   Configure PostgreSQL and Redis local containers (Docker Compose).
    *   Design shared database models and initial schemas.
*   **Deliverables**:
    *   Monorepo or multi-repo directory layout setup with Docker integration.
    *   Working local system running on Docker Compose showing backend-frontend connectivity.
    *   Database migrations set up and ran successfully.
*   **Completion Criteria**:
    *   Running `docker compose up` starts frontend, backend, PostgreSQL database, and Redis cache.
    *   Health check endpoint (`GET /api/v1/health`) returns `200 OK` with database connection state verified.

---

## Phase 2 - Authentication
*   **Goal**: Enable secure user management and storage of developer profiles, prioritizing GitHub-based onboarding.
*   **Features**:
    *   GitHub OAuth integration flow (Frontend redirect to Backend callback).
    *   Local email/password option with secure hashing (bcrypt/Argon2).
    *   JWT-based session authentication (Access & Refresh tokens).
    *   User Profile initialization (Staged database records for user details).
*   **Deliverables**:
    *   `/api/v1/auth/github` endpoint orchestrating OAuth callbacks.
    *   User profile table schema defining system roles and preferences.
    *   State management in the frontend handling token storage (HttpOnly Cookies).
*   **Completion Criteria**:
    *   User can log in using GitHub, receive a JWT token, and access a protected route (`GET /api/v1/profile`).
    *   Tokens correctly refresh before expiration without requiring manual user re-authentication.

---

## Phase 3 - GitHub Integration
*   **Goal**: Establish deep system connectivity with the GitHub API to fetch user repositories, commit logs, and metadata.
*   **Features**:
    *   GitHub App/OAuth scopes for reading public and private repositories (`repo` scope).
    *   Repository synchronization worker (polling or webhook-driven).
    *   Metadata scraper for commit velocity, language distributions, and commit message quality.
    *   Redis-based caching to limit GitHub API rate-limit consumption.
*   **Deliverables**:
    *   Interactive repository selection panel in the UI.
    *   Background queue worker (BullMQ/Celery) processing repository listings.
    *   Repository metadata schema stored in the central database.
*   **Completion Criteria**:
    *   User can select up to 3 repositories from a checklist.
    *   System fetches, parses, and saves metadata (languages, line count, commits, PR counts) to PostgreSQL in under 10 seconds.

---

## Phase 4 - Resume Intelligence
*   **Goal**: Extract structural skill profiles and history from user resumes using heuristic and AI analysis.
*   **Features**:
    *   Drag-and-drop file uploader accepting PDF formats (up to 2MB limit).
    *   Backend processing pipeline utilizing a PDF text parser (e.g., pdf-parse).
    *   Keyword extraction engine analyzing experience history and listed skill badges.
    *   Role gap calculator evaluating profiles against target jobs (e.g., Frontend React Dev, Python Backend Dev).
*   **Deliverables**:
    *   Secure AWS S3/MinIO upload storage configuration.
    *   Resume processing service module.
    *   Resume intelligence UI segment showing current parsing results and missing keywords.
*   **Completion Criteria**:
    *   Uploaded resume PDF is successfully stored.
    *   System returns a list of extracted skills (e.g., `["React", "NodeJS", "SQL"]`) and identifies at least 3 matching gaps against a selected role template.

---

## Phase 5 - DSA Tracking
*   **Goal**: Construct the tracking and visualization framework for algorithmic problem-solving progress.
*   **Features**:
    *   DSA Logger Dashboard (Manual entry forms for LeetCode, HackerRank, Codeforces).
    *   Problem categorizations aligned with major interview patterns (e.g., Slidewindow, Backtracking, DFS/BFS).
    *   Progress indicator highlighting problem difficulty ratios (Easy vs. Medium vs. Hard).
    *   Integration with unofficial scraper libraries to automate scraping of LeetCode public profile counts.
*   **Deliverables**:
    *   DSA database schema representing problems solved, categories, and timestamps.
    *   DSA Dashboard UI page featuring interactive data charts.
*   **Completion Criteria**:
    *   User can log a solved problem (e.g., LeetCode #121, Dynamic Programming, Easy) and see their category distribution charts recalculate in real-time.

---

## Phase 6 - Insight Engine
*   **Goal**: Perform static analysis on synchronized GitHub repositories to extract objective code quality insights.
*   **Features**:
    *   Static analysis runner analyzing files for structural signals.
    *   Quality checkers verifying:
        *   Testing configuration (jest.config, package.json scripts containing "test").
        *   Documentation standards (README presence, length, structure).
        *   Security issues (hardcoded API tokens, configuration files not listed in `.gitignore`).
    *   Parsing logic to gauge architectural practices (e.g., layer separation, modularization).
*   **Deliverables**:
    *   Static scanner module running asynchronously via background workers.
    *   Security risk ruleset configuration.
    *   Insight summary view in the user's dashboard detailing warnings and successes.
*   **Completion Criteria**:
    *   Scanning a repository with a hardcoded token flags a high-severity warning.
    *   Repository lacking standard test directories is correctly marked as "0% Test Coverage presence."

---

## Phase 7 - Recommendation Engine
*   **Goal**: Synthesize data points from Phase 3, 4, 5, and 6 to output a single, prioritized next-action queue.
*   **Features**:
    *   Prioritization scoring algorithm weighing:
        *   High-risk security findings (weight: critical).
        *   Missing core skills from target resume profile (weight: high).
        *   Lacking testing suite in active projects (weight: medium).
        *   Stale DSA practice in target categories (weight: medium).
    *   Personalized action description generator.
    *   Interactive roadmap dashboard displaying the "Top 3 Actionable Recommendations."
*   **Deliverables**:
    *   Core recommendation algorithm module (`RecommendationService`).
    *   Interactive roadmap frontend component featuring status tracking (Todo, Doing, Verified).
*   **Completion Criteria**:
    *   Engine automatically updates the user's actionable roadmap whenever a new resume is processed or a repository is rescanned.
    *   Engine correctly prioritizes a "Security vulnerability" or a "Missing resume keyword" over simple formatting checks.

---

## Phase 8 - Production Deployment
*   **Goal**: Transition Nexora from a local development workspace to a secure, resilient, production cloud platform.
*   **Features**:
    *   Infrastructure provisioning (VPC, ECS/Kubernetes, RDS PostgreSQL, Managed Redis).
    *   CI/CD deployment pipelines (GitHub Actions building Docker containers and deploying to cloud targets).
    *   Production-grade security configurations (SSL certificates, CORS restrictions, API rate-limiting via Nginx or Cloudflare).
    *   Telemetry setup: Logging (Winston/Pino), Monitoring (Sentry for errors, Prometheus/Grafana for server metrics).
*   **Deliverables**:
    *   Terraform or AWS CloudFormation configuration scripts.
    *   GitHub Actions workflow files (`.github/workflows/deploy.yml`).
    *   SSL secured public URL routing directly to the application.
*   **Completion Criteria**:
    *   New commits merged to the `main` branch trigger automated tests, build Docker images, and deploy to staging/production without manual intervention.
    *   Live platform scores >90 on Lighthouse performance, security, and SEO audits.
