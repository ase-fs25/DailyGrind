# DailyGrind

> **DailyGrind** combines the spontaneity of BeReal with the professionalism of LinkedIn, matching users once per day into one-on-one “Piece of Knowledge” (PoK) exchanges. By limiting interactions to a single, bite-sized learning reflection each day, we foster high-quality, focused connections and mutual accountability.
>
> Built with **React**, **Spring Boot** and **AWS (LocalStack)**, DailyGrind showcases cloud-native microservices, —complete with single‐table DynamoDB data modeling, asynchronouse SNS/SQS messaging, event-driven web push notifications and infrastructure-as-code via Terraform, underpinned by a fully automated CI/CD pipeline, all developed as part of the Advanced Software Engineering course (FS 2025).

---

## Contents

1. [Quick start](#quick-start)
2. [Architecture at a glance](#architecture-at-a-glance)
3. [Testing](#testing)
4. [Continuous Integration / Deployment](#continuous-integration--deployment)
5. [Monitoring](#monitoring)
6.[Project Organization](#project-organization)
    - [Team Members](#team-members-and-their-main-focuses)
    - [Process & Methodology](#process--methodology)
    - [Sprint Cadence](#sprint-cadence)
    - [Timeline / Roadmap](#timeline--roadmap)
7[Submission Artifacts](#submission-artifacts)
8[License](#license)

---

## Quick start

### Prerequisites

Ensure that the following are installed on your machine:

- **Docker**: A platform for developing, shipping, and running applications in containers.
- **Docker Compose**: A tool for defining and running multi-container Docker applications.
- **LocalStack Pro License**: Required for emulating certain AWS services. Obtain your API key via localstack.cloud and set it in your environment as LOCALSTACK_AUTH_TOKEN.

### Run in production

```bash
# clone & enter
$ git clone https://github.com/ase-fs25/DailyGrind.git
$ cd DailyGrind

# inorder for terraform to set env variables you have to define a client secret
# in /terraform create the terraform.tfvars file and choose a secret (see terraform.tfvars.example as example)
$ echo 'cognito_client_secret = "super-secret-dev-value"' > terraform/terraform.tfvars

# in the runtime config: set the profile to prod
$ docker compose up --build -d

# open the app
$ open http://localhost:4566/dailygrind/index.html
```

```bash

# use one of the profiles:
# dev: spins up the localstack container without the microservices and the frontend
# deploay: redeploy the S3 bucket for frontend deployment (might be needed if older instances of previous S3 Bucktes get cached)
# prod: run everything through localstack
````

---

### For Development

#### Microservices

#### Frontend

#### Postman

### Troubleshooting

---

## Architecture at a glance

<figure style="text-align: center;">
  <img src="./img/architecture.jpeg" alt="Architecture overview" width="600"/>
  <figcaption><em>Figure 1: High-level microservices & AWS diagram.</em></figcaption>
</figure>

For more detailed documentations please revice the following READMEs which comprehensively list the services functionality as well as the motivation of the tech-stack chosen:

- [*Infrastructure documentation*](terraform/README.md)

- [*User Service documentation*](microservices/user-service/README.md)

- [*Post Service documentation*](microservices/post-service/README.md)

- [*Push Notification Service documentation*](microservices/push-notification-service/README.md)

- [*Frontend documentation*](frontend/README.md)

Additionally, you can find a JavaDoc for each microservice at `microservices/*/docs/index.html` as well as the swagger doc at `http://localhost:MS-PORT/swagger-ui/index.html`.

---

## Testing

#### Unit Tests

- Focus on isolated component testing with mocked dependencies
- Cover core functionality like service implementations and utility classes
- Use JUnit 5 with nested test classes for organized test suites
- Mockito for mocking dependencies in service and controller unit tests

#### Integration Tests

- Verify cross-component functionality using real service implementations
- TestContainers with LocalStack to simulate AWS services:
- Spring MockMVC for API endpoint testing with full request/response validation
- Custom test configurations (like `AwsTestCredentialsConfig`) provide consistent test environments
- Authentication testing with Spring Security's `@WithMockUser` annotation

#### Test Implementation Patterns

- Extensive use of test fixtures and builders for test data creation
- Nested test classes organize tests by functional area (e.g., `UserIntegrationTest` contains nested classes for details and search functionalities)
- REST endpoint testing with JSON path validation for complete response verification
- Comprehensive test coverage for edge cases (e.g., users with/without jobs, education, friends)

### Test Execution

- **Local Development**: Tests can be run through IDE or Maven commands. Make sure Docker is running, since the integration tests use TestContainers to simulate AWS services.
- **Maven Profile**: Run integration tests for the microservices with:
  ```bash
  mvn clean test -P integration-tests

---

## Continuous Integration / Deployment

We used a **trunk-based development model** with short-lived branches:

```
main            ← production
│
└─ develop      ← integration branch, auto‑deploys to staging
   ├─ feature/ | one branch per Issue (lifetime ≤ 7 days)
   ├─ release/ | one branch per Sprint
   └─ fix/ | emergency patch branched from develop
```

- All branches followed naming conventions and protection rules.
- **Squash-and-merge** was enforced to keep history linear.

---

## Monitoring

The `dev` and `prod` profiles include **Prometheus** and **Grafana** for service observability.

### Prometheus

Prometheus scrapes metrics from each microservice via their `/actuator/prometheus` endpoints at 5-second intervals.

- URL: [http://localhost:9090](http://localhost:9090)
- Config: `./prometheus/prometheus.yml`

### Grafana

Grafana provides interactive dashboards for visualizing Prometheus metrics.

- URL: [http://localhost:4000](http://localhost:4000)
- Login: `admin` / `admin`
- Data is persisted in the `grafana-storage` Docker volume

Prometheus is pre-configured as a data source. Dashboards can be imported or created using PromQL queries.

---

### Pull Request Workflow

1. **Draft PR** opened when work begins; always linked to its GitHub Issue.
2. PR runs checks:

- Lint, unit & integration tests
- Docker build
  - SonarQube static code analysis:
    <div align="left">
    <a href="https://sonarcloud.io/summary/new_code?id=ase-fs25_DailyGrind">
    <img src="https://sonarcloud.io/api/project_badges/quality_gate?project=ase-fs25_DailyGrind" alt="Quality Gate Status">
    </a>
    </div>

3. At least **one peer review** required (cross-team if possible).

4. Upon approval → **squash-merge**, auto-close Issue, move to *Done* in project board.

---

### Github Actions

Our project uses GitHub Actions for comprehensive CI/CD automation with three main workflows:

#### Microservices CI

- **Workflow:** (`microservices.yml`)
- Automatically discovers all microservices in the project
- Builds and tests each microservice with Maven
- Runs both unit and integration tests using the `integration-tests` profile
- Docker must be running as tests use TestContainers

#### Frontend CI

- **Workflow:** (`frontend.yml`)
- Builds and validates the React frontend
- Performs static code analysis:
  - ESLint for code linting
  - Prettier for code formatting
  - TypeScript type checking
- Runs security audits on dependencies
- Builds the production frontend bundle

#### Code Quality Analysis

- **Workflow:** (`codeql.yml`)
- Performs advanced security scanning using GitHub CodeQL
- Separate analysis for Java microservices and TypeScript/JavaScript frontend
- Identifies potential security vulnerabilities and code quality issues
- Results published to GitHub Security tab

All workflows run automatically on:

- Pushes to `main`, `develop` and `release/*`, and `hotfix/*` branches
- Pull requests targeting these branches

You can find the complete workflow configurations in the `.github/workflows` directory of our repository.

---

## Project Organization

This section explains how the team planned, tracked, and delivered the DailyGrind application, focusing on process, timeline, collaboration practices,
and final deliverables.

### Team Members and Their Main Focuses

<div style="white-space: pre; font-family: monospace;">
Jonas Gebel       Lead Frontend Developer
Leonard Wagner    Lead Backend Developer & DevOps
Mete Polat        DevOps Engineer
Tim Vorburger     Full-Stack Developer
Toni Krstic       Full-Stack Developer
</div>

### Process & Methodology

| Aspect                 | Choice                                                                           | Motivation                                                                                                        |
|------------------------|----------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------------|
| **Framework**          | Scrum (adapted): 4 sprints, weekly meetings, review & retro                      | Lightweight process that fit the semester timeline. Weekly inspect-and-adapt loops improved delivery consistency. |
| **Backlog Tool**       | [GitHub Projects](https://github.com/orgs/ase-fs25/projects/1/views/1)           | Tight integration with issues, PRs, milestones — one source of truth for planning and tracking.                   |
| **Task Tracking**      | GitHub Issues tracked during weekly meetings                                     | Ensures traceability, structured progress, and clarity on ownership.                                              |
| **Definition of Done** | Code + tests pass locally, CI pipeline green, PR approved, documentation updated | Maintains shared quality expectations across services and contributors.                                           |

---

### Sprint Cadence

| Sprint | Dates (2025)    | Theme                                         |
|--------|-----------------|-----------------------------------------------|
| 1      | Jan 29 – Mar 17 | Project setup, CI, core user flows            |
| 2      | Mar 18 – Mar 31 | CRUD for users, first batch of functionality  |
| 3      | Apr 01 – Apr 28 | Second batch of functionality, CRUD for posts |
| 4      | Apr 29 – May 12 | Final batch of functionality, testing, polish |

*All sprint tasks were tracked and visualized in the roadmap below.*

---

### Timeline / Roadmap

> The roadmap was exported from GitHub Projects on May 12, 2025.

<figure align="center">
  <img src="./img/Github_Project_Roadmap.jpeg" alt="Roadmap" width="600"/>
  <figcaption>Figure 2: Github Project Roadmap.</figcaption>
</figure>

Key observations:

- Parallel swim-lanes show each microservice progressing independently, yet synchronized by sprint.
- White dots = completed tasks; hollow = in progress.
- Burndown trends helped track velocity.

### Submission Artifacts

| Item                 | Location                                                       |
|----------------------|----------------------------------------------------------------|
| Source code snapshot | `releases/DailyGrind_submission.zip` (tag `v1.0.0-submission`) |
| Project board export | `Github_Project_Roadmap.jpeg`                                  |
| Project Reflection   | `Reflection.pdf`                                               |

---

### License

MIT © 2025 DailyGrind Team – University of Zurich
