# GitLab CI Templates

## Standard Pipeline Structure

**File location**: `.gitlab-ci.yml`

GitLab CI uses **stages** (sequential) with **jobs** (parallel within a stage).

---

## Full Template (Node.js)

```yaml
image: node:20-alpine

stages:
  - lint
  - test
  - build
  - deploy

variables:
  DOCKER_IMAGE: $CI_REGISTRY_IMAGE:$CI_COMMIT_SHA
  DOCKER_DRIVER: overlay2

cache:
  key: ${CI_COMMIT_REF_SLUG}
  paths:
    - node_modules/

# ── Lint ──────────────────────────────────────────────────────────
lint:
  stage: lint
  script:
    - npm ci
    - npm run lint
  only:
    - merge_requests
    - main
    - develop

# ── Test ──────────────────────────────────────────────────────────
test:
  stage: test
  script:
    - npm ci
    - npm run test:ci
  coverage: '/Coverage: \d+\.\d+%/'
  artifacts:
    reports:
      coverage_report:
        coverage_format: cobertura
        path: coverage/cobertura-coverage.xml
    expire_in: 1 week
  only:
    - merge_requests
    - main
    - develop

# ── Build Docker image ────────────────────────────────────────────
build:
  stage: build
  image: docker:24
  services:
    - docker:24-dind
  before_script:
    - docker login -u $CI_REGISTRY_USER -p $CI_REGISTRY_PASSWORD $CI_REGISTRY
  script:
    - docker build -t $DOCKER_IMAGE .
    - docker push $DOCKER_IMAGE
  only:
    - main
    - develop

# ── Deploy to staging ─────────────────────────────────────────────
deploy:staging:
  stage: deploy
  image: alpine:latest
  before_script:
    - apk add --no-cache openssh-client
    - eval $(ssh-agent -s)
    - echo "$STAGING_SSH_KEY" | tr -d '\r' | ssh-add -
    - mkdir -p ~/.ssh && chmod 700 ~/.ssh
  script:
    - ssh -o StrictHostKeyChecking=no $STAGING_USER@$STAGING_HOST "
        docker pull $DOCKER_IMAGE &&
        docker-compose -f docker-compose.prod.yml up -d
      "
  environment:
    name: staging
    url: https://staging.example.com
  only:
    - develop

# ── Deploy to production (manual gate) ───────────────────────────
deploy:production:
  stage: deploy
  image: alpine:latest
  before_script:
    - apk add --no-cache openssh-client
    - eval $(ssh-agent -s)
    - echo "$PROD_SSH_KEY" | tr -d '\r' | ssh-add -
    - mkdir -p ~/.ssh && chmod 700 ~/.ssh
  script:
    - ssh -o StrictHostKeyChecking=no $PROD_USER@$PROD_HOST "
        docker pull $DOCKER_IMAGE &&
        docker-compose -f docker-compose.prod.yml up -d
      "
  environment:
    name: production
    url: https://example.com
  when: manual                  # Requires manual approval
  only:
    - main
```

---

## Spring Boot (Maven) GitLab CI

```yaml
image: eclipse-temurin:21-jdk-alpine

stages:
  - test
  - build
  - deploy

variables:
  MAVEN_OPTS: "-Dmaven.repo.local=$CI_PROJECT_DIR/.m2/repository"
  DOCKER_IMAGE: $CI_REGISTRY_IMAGE:$CI_COMMIT_SHA

cache:
  key: ${CI_COMMIT_REF_SLUG}
  paths:
    - .m2/repository

test:
  stage: test
  script:
    - mvn verify
  artifacts:
    reports:
      junit:
        - target/surefire-reports/TEST-*.xml
    expire_in: 1 week

build:
  stage: build
  image: docker:24
  services:
    - docker:24-dind
  before_script:
    - docker login -u $CI_REGISTRY_USER -p $CI_REGISTRY_PASSWORD $CI_REGISTRY
  script:
    - docker build -t $DOCKER_IMAGE .
    - docker push $DOCKER_IMAGE
  only:
    - main
    - develop

deploy:staging:
  stage: deploy
  image: alpine:latest
  before_script:
    - apk add --no-cache openssh-client
    - eval $(ssh-agent -s)
    - echo "$STAGING_SSH_KEY" | tr -d '\r' | ssh-add -
    - mkdir -p ~/.ssh && chmod 700 ~/.ssh
  script:
    - ssh -o StrictHostKeyChecking=no $STAGING_USER@$STAGING_HOST "
        docker pull $DOCKER_IMAGE &&
        docker-compose -f docker-compose.prod.yml up -d
      "
  environment:
    name: staging
  only:
    - develop

deploy:production:
  stage: deploy
  image: alpine:latest
  before_script:
    - apk add --no-cache openssh-client
    - eval $(ssh-agent -s)
    - echo "$PROD_SSH_KEY" | tr -d '\r' | ssh-add -
    - mkdir -p ~/.ssh && chmod 700 ~/.ssh
  script:
    - ssh -o StrictHostKeyChecking=no $PROD_USER@$PROD_HOST "
        docker pull $DOCKER_IMAGE &&
        docker-compose -f docker-compose.prod.yml up -d
      "
  environment:
    name: production
  when: manual
  only:
    - main
```

---

## Key GitLab CI Concepts

### Variables (where to set secrets)
- Go to **Settings → CI/CD → Variables**
- Mark secrets as `Masked` (hidden in logs) and `Protected` (only on protected branches)
- Common variables to add: `PROD_SSH_KEY`, `STAGING_SSH_KEY`, `PROD_HOST`, `STAGING_HOST`

### GitLab Container Registry
GitLab provides a built-in container registry — no DockerHub needed.
- `$CI_REGISTRY` → registry URL (auto-populated)
- `$CI_REGISTRY_USER` / `$CI_REGISTRY_PASSWORD` → auto-populated per job
- `$CI_REGISTRY_IMAGE` → image path for your project

### Environments
Declare environments in jobs to enable:
- Deployment history tracking
- Rollback buttons in GitLab UI
- Manual approval gates (`when: manual`)

### Include & Extends (DRY pipelines)
```yaml
# Reuse job config
.base-deploy: &base-deploy
  image: alpine:latest
  before_script:
    - apk add --no-cache openssh-client

deploy:staging:
  <<: *base-deploy
  script: ...

deploy:production:
  <<: *base-deploy
  script: ...
```
