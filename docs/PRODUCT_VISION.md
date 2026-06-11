# Nexora: Product Vision Document

## Executive Summary
Nexora is a production-grade, developer-centric SaaS platform designed to bridge the gap between academic/bootcamp training and professional software engineering readiness. By connecting directly to developers' real-world activity hubs—GitHub, Data Structures & Algorithms (DSA) trackers, and resumes—Nexora constructs an objective **Engineering Readiness Graph**. The platform identifies concrete engineering, algorithmic, and documentation gaps, translating them into a prioritized, actionable queue of high-impact next steps (e.g., *"Add unit tests to your payment microservice," "Solve 3 tree-traversal DSA challenges," "Remove hardcoded API keys"*). Nexora serves as the ultimate professional compass, guiding aspiring engineers step-by-step toward internship and job readiness.

---

## Problem Statement
The path to securing a modern software engineering role is chaotic, overwhelming, and highly unguided. Candidates suffer from four distinct systemic challenges:

1. **The Technical Blindspot**: Aspiring developers build basic, repetitive CRUD applications from tutorials. They struggle to transition to enterprise-grade software development practices (e.g., automated testing, CI/CD pipelines, robust error handling, containerization).
2. **The "LeetCode Grind" Trap**: Candidates solve algorithmic challenges randomly without structure or company-specific alignment. They waste hundreds of hours memorizing answers instead of developing algorithmic intuition and addressing concrete pattern deficiencies.
3. **The Resume-Reality Mismatch**: Resumes boast achievements that their active codebases fail to substantiate. If a resume lists "Unit Testing" but their GitHub repositories contain zero test files, recruiters and hiring managers discard their profiles immediately.
4. **Information Overload**: There is a flood of video courses and bootcamps, but no central, objective system that analyzes a developer's **actual code** to tell them precisely what they need to learn and build *today*.

---

## Target Users
Nexora targets three primary user segments:

*   **Computer Science Undergraduates**: Students with strong theoretical knowledge (computational complexity, OS, databases) but little to no experience building industry-grade systems, writing clean code, or adopting standard Git/CI-CD workflows.
*   **Coding Bootcamp Graduates & Career Switchers**: Individuals with practical stack experience (e.g., MERN) who lack algorithmic problem-solving depth (DSA) and struggle with technical assessments or system design interviews.
*   **Self-Taught Developers**: Independent learners navigating an unstructured path who require objective validation of their coding practices and a clear, sequential curriculum to meet professional hiring standards.

---

## User Personas

### Persona 1: Theory-Heavy Tina (CS Junior)
*   **Profile**: 3rd-year Computer Science student at a state university.
*   **Frustrations**: Has a 3.8 GPA and understands compiler design, but has never written a Jest/PyTest test suite, doesn't know how to deploy an application, and her GitHub is populated with command-line academic assignments. She fails initial resume screens for internships due to a lack of practical, production-like projects.
*   **Goals**: Build a highly polished, production-ready portfolio project; master professional engineering workflows (CI/CD, PR reviews, linting); secure a Software Engineering Internship.

### Persona 2: Bootcamp Billy (Career Switcher)
*   **Profile**: Former hospitality manager who recently graduated from a 24-week full-stack web development bootcamp.
*   **Frustrations**: Built a standard React e-commerce application following a bootcamp template. He has zero familiarity with algorithmic problem-solving. When faced with automated HackerRank or LeetCode screens, he fails instantly. He feels generic and indistinguishable from thousands of other bootcamp graduates.
*   **Goals**: Learn core DSA patterns (Sliding Window, Two Pointers, Trees, Graphs) sequentially; optimize his GitHub to demonstrate authentic technical depth (e.g., database indexing, performance optimization); land his first junior software engineering job.

---

## Product Vision
To build the **"Waze for Software Engineering Careers"**—an intelligent, dynamic co-pilot that maps a developer's current technical capabilities, understands their career goals, and continuously recalculates the most direct, high-impact route to professional readiness through automated code analysis, portfolio optimization, and structured training feedback.

---

## Core Value Proposition
For aspiring software engineers, **Nexora replaces guesswork with data-driven reality.** Unlike generic learning platforms, Nexora inspects your **actual codebase, DSA history, and resume** to generate a personalized action plan, converting your raw activity into verified, industry-ready credentials.

---

## Features In Scope

### 1. GitHub Integration & Portfolio Analysis
*   Connects via GitHub OAuth to analyze public and private repositories.
*   Assesses project quality metrics: directory organization, README clarity, configuration hygiene, and clean commit history.

### 2. Engineering Quality Analyzer (Insight Engine)
*   Performs static code analysis (AST parsing) to detect:
    *   **Testing presence**: Are there unit, integration, or E2E tests?
    *   **Security vulnerabilities**: Are there hardcoded API keys, passwords, or SQL injection vectors?
    *   **Production readiness**: Presence of environment variables, error boundaries, dockerfiles, or CI/CD pipelines.

### 3. DSA Progress Tracking
*   Integrates with user profiles to log DSA practice metrics.
*   Aggregates questions solved, grouping them by complexity (Easy/Medium/Hard) and structural patterns (e.g., Backtracking, BFS/DFS, Heaps).

### 4. Resume Intelligence
*   Accepts PDF uploads of developer resumes.
*   Parses experience, skills, and projects, mapping them against chosen career paths (e.g., "Full-Stack Engineer," "Backend Specialist") to flag missing capabilities or keyword gaps.

### 5. Prioritized Recommendation Engine
*   Synthesizes the portfolio, DSA status, resume, and career goals.
*   Outputs a dynamic queue of **"Top 3 Highest-Impact Actions"** updated in real-time.

---

## Features Out Of Scope
*   **Direct Code Hosting**: Nexora will not compete with GitHub, GitLab, or Bitbucket.
*   **Interactive Coding IDE**: The platform will not provide an in-browser code editor. All coding and refactoring must occur in the developer's local environment.
*   **Direct Job Board / Recruiting Agency**: Nexora focuses exclusively on job-readiness preparation, not candidate placements or employer matching in its early stages.
*   **Original Educational Content Creation**: Nexora will not produce its own video courses or textbooks. It acts as an orchestrator, recommending and linking to high-quality external resources (e.g., MDN, LeetCode, roadmap.sh).

---

## MVP Definition
The Minimum Viable Product (MVP) will focus on validating the core value loop with minimal complexity:

1.  **Authentication**: Secure user sign-up/login via GitHub OAuth.
2.  **Portfolio Review**: Integration with a single user-selected GitHub repository. The backend will parse the repo for basic health checks: presence of a README, presence of a package manager file, and existence of a testing folder/script.
3.  **Manual DSA Logs**: A basic form allowing users to input their LeetCode/HackerRank counts by category.
4.  **Basic Resume Parser**: A simple file uploader where users submit a PDF, extracting text to compare against a static database of junior engineering skills.
5.  **Static recommendation engine**: A rule-based dashboard showcasing 3 critical next actions based on the analysis.

---

## Future Vision
*   **AI Code Review Bot**: An automated PR reviewer that leaves contextual comments directly on the user's GitHub PRs, coaching them on performance optimizations, memory leaks, and design patterns.
*   **Simulated Technical Interviews**: GenAI-powered conversational mock interviews tailored to specific roles and companies, grading responses against real rubric standards.
*   **The "Verified Candidate" Portal**: A hiring portal where recruiters can view candidate dashboards showing verified metrics (e.g., "Verified 85% unit test coverage on portfolio project" or "Verified DFS/BFS competency").

---

## Success Metrics

| Metric Category | Metric Name | Definition / Target |
| :--- | :--- | :--- |
| **Activation** | Connect Rate | % of registered users who successfully connect GitHub and upload their resume within 24 hours of sign-up (Target: >75%). |
| **Engagement** | Recommendation Resolution | Average number of suggested next steps marked "Resolved" and verified by the backend per month per user (Target: >3 items/month). |
| **Growth** | Skill Delta | Average increase in a user's cumulative Engineering Readiness Score over 60 days (Target: >25% improvement). |
| **North Star** | Career Outcome | Self-reported number of users who credit Nexora with helping them secure an internship or full-time offer (Target: 100+ users in Phase 1-3). |

---

## Risks & Assumptions
*   **Assumption: Third-Party API Stability**: We assume GitHub and other developer APIs will maintain affordable and accessible developer portals. Changes to GitHub's rate limits could severely disrupt our data harvesting capabilities.
*   **Risk: High User Drop-off**: Implementing advanced software patterns (like writing unit tests or refactoring database queries) takes time and effort. Users may become frustrated and abandon the platform.
    *   *Mitigation*: We will break down recommendations into bite-sized micro-tasks (e.g., instead of "Test your API," we will suggest: "Write a single test verifying that your `/health` endpoint returns 200 OK").
*   **Risk: AI Hallucinations in Code Analysis**: LLM or heuristic analysis of user repositories might suggest irrelevant refactors or falsely flag security issues, damaging platform credibility.
    *   *Mitigation*: Implement strict schema-validated AST heuristics for code scanning and fallback to LLMs only for open-ended code style checks.
