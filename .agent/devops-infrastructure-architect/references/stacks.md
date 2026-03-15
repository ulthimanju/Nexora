# Stack-Specific Dockerfile Patterns

## Node.js / Express

```dockerfile
# ── Build ────────────────────────────────────────────────────────
FROM node:20-alpine AS builder
WORKDIR /app
COPY package*.json ./
RUN npm ci --only=production

# ── Runtime ──────────────────────────────────────────────────────
FROM node:20-alpine AS runner
WORKDIR /app

RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

COPY --from=builder /app/node_modules ./node_modules
COPY . .

EXPOSE 3000
HEALTHCHECK --interval=30s --timeout=5s --retries=3 \
  CMD wget -qO- http://localhost:3000/health || exit 1

ENTRYPOINT ["node", "src/index.js"]
```

**Notes:**
- `npm ci` (not `npm install`) for reproducible installs
- `--only=production` to skip devDependencies in image
- Use `wget` instead of `curl` in alpine (lighter)

---

## Next.js

```dockerfile
# ── Dependencies ─────────────────────────────────────────────────
FROM node:20-alpine AS deps
WORKDIR /app
COPY package*.json ./
RUN npm ci

# ── Build ────────────────────────────────────────────────────────
FROM node:20-alpine AS builder
WORKDIR /app
COPY --from=deps /app/node_modules ./node_modules
COPY . .
ENV NEXT_TELEMETRY_DISABLED 1
RUN npm run build

# ── Runtime ──────────────────────────────────────────────────────
FROM node:20-alpine AS runner
WORKDIR /app

ENV NODE_ENV production
ENV NEXT_TELEMETRY_DISABLED 1

RUN addgroup -S appgroup && adduser -S appuser -G appgroup

COPY --from=builder /app/public ./public
COPY --from=builder --chown=appuser:appgroup /app/.next/standalone ./
COPY --from=builder --chown=appuser:appgroup /app/.next/static ./.next/static

USER appuser
EXPOSE 3000
HEALTHCHECK --interval=30s --timeout=5s --retries=3 \
  CMD wget -qO- http://localhost:3000/api/health || exit 1

CMD ["node", "server.js"]
```

**Required `next.config.js`:**
```js
module.exports = {
  output: 'standalone',
}
```

---

## Spring Boot (Maven)

```dockerfile
# ── Build ────────────────────────────────────────────────────────
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN apk add --no-cache maven && mvn package -DskipTests

# ── Runtime ──────────────────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine AS runner
WORKDIR /app

RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=10s --retries=3 \
  CMD wget -qO- http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]
```

**Performance tip:** For faster Docker builds with Maven, use a Maven cache layer:
```dockerfile
# Cache deps separately before copying source
COPY pom.xml .
RUN mvn dependency:go-offline -q
COPY src ./src
RUN mvn package -DskipTests
```

---

## Python / FastAPI

```dockerfile
# ── Build ────────────────────────────────────────────────────────
FROM python:3.12-alpine AS builder
WORKDIR /app
RUN pip install --upgrade pip
COPY requirements.txt .
RUN pip install --prefix=/install -r requirements.txt

# ── Runtime ──────────────────────────────────────────────────────
FROM python:3.12-alpine AS runner
WORKDIR /app

RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

COPY --from=builder /install /usr/local
COPY . .

EXPOSE 8000
HEALTHCHECK --interval=30s --timeout=5s --retries=3 \
  CMD wget -qO- http://localhost:8000/health || exit 1

CMD ["uvicorn", "app.main:app", "--host", "0.0.0.0", "--port", "8000"]
```

---

## Nginx (Static / Reverse Proxy)

### As a reverse proxy in front of an app:

```dockerfile
FROM nginx:1.25-alpine
COPY nginx.conf /etc/nginx/nginx.conf
EXPOSE 80 443
HEALTHCHECK --interval=30s --timeout=5s --retries=3 \
  CMD nginx -t || exit 1
```

**`nginx.conf` template:**
```nginx
events {}

http {
    upstream app {
        server app:3000;
    }

    server {
        listen 80;
        server_name _;

        location / {
            proxy_pass http://app;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        }
    }
}
```

---

## Redis

No custom Dockerfile needed — use official image:

```yaml
redis:
  image: redis:7-alpine
  command: redis-server --appendonly yes --requirepass ${REDIS_PASSWORD}
  volumes:
    - redis-data:/data
  healthcheck:
    test: ["CMD", "redis-cli", "-a", "${REDIS_PASSWORD}", "ping"]
    interval: 10s
    retries: 5
```

---

## MongoDB

```yaml
mongo:
  image: mongo:7-jammy
  environment:
    MONGO_INITDB_ROOT_USERNAME: ${MONGO_USER}
    MONGO_INITDB_ROOT_PASSWORD: ${MONGO_PASSWORD}
    MONGO_INITDB_DATABASE: ${MONGO_DB}
  volumes:
    - mongo-data:/data/db
  healthcheck:
    test: ["CMD", "mongosh", "--eval", "db.adminCommand('ping')"]
    interval: 10s
    retries: 5
```

---

## Full Stack Compose Examples

### Node.js + PostgreSQL + Redis

```yaml
version: "3.9"

services:
  app:
    build:
      context: .
      target: runner
    ports:
      - "${APP_PORT:-3000}:3000"
    env_file: .env
    depends_on:
      db:
        condition: service_healthy
      cache:
        condition: service_healthy
    restart: unless-stopped
    networks:
      - app-net

  db:
    image: postgres:16-alpine
    environment:
      POSTGRES_DB: ${DB_NAME}
      POSTGRES_USER: ${DB_USER}
      POSTGRES_PASSWORD: ${DB_PASSWORD}
    volumes:
      - db-data:/var/lib/postgresql/data
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U ${DB_USER}"]
      interval: 10s
      retries: 5
    networks:
      - app-net

  cache:
    image: redis:7-alpine
    command: redis-server --requirepass ${REDIS_PASSWORD}
    volumes:
      - redis-data:/data
    healthcheck:
      test: ["CMD", "redis-cli", "-a", "${REDIS_PASSWORD}", "ping"]
      interval: 10s
      retries: 5
    networks:
      - app-net

volumes:
  db-data:
  redis-data:

networks:
  app-net:
    driver: bridge
```

### Spring Boot + MongoDB + Redis

```yaml
version: "3.9"

services:
  app:
    build:
      context: .
      target: runner
    ports:
      - "${APP_PORT:-8080}:8080"
    env_file: .env
    depends_on:
      mongo:
        condition: service_healthy
      cache:
        condition: service_healthy
    restart: unless-stopped
    networks:
      - app-net

  mongo:
    image: mongo:7-jammy
    environment:
      MONGO_INITDB_ROOT_USERNAME: ${MONGO_USER}
      MONGO_INITDB_ROOT_PASSWORD: ${MONGO_PASSWORD}
      MONGO_INITDB_DATABASE: ${MONGO_DB}
    volumes:
      - mongo-data:/data/db
    healthcheck:
      test: ["CMD", "mongosh", "--eval", "db.adminCommand('ping')"]
      interval: 10s
      retries: 5
    networks:
      - app-net

  cache:
    image: redis:7-alpine
    command: redis-server --requirepass ${REDIS_PASSWORD}
    volumes:
      - redis-data:/data
    healthcheck:
      test: ["CMD", "redis-cli", "-a", "${REDIS_PASSWORD}", "ping"]
      interval: 10s
      retries: 5
    networks:
      - app-net

volumes:
  mongo-data:
  redis-data:

networks:
  app-net:
    driver: bridge
```
