-- Users table (partitioned for scale)
CREATE TABLE IF NOT EXISTS users (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username    VARCHAR(50) UNIQUE NOT NULL,
    email       VARCHAR(255) UNIQUE NOT NULL,
    password    VARCHAR(255) NOT NULL,
    token_version INT DEFAULT 0,
    enabled     BOOLEAN DEFAULT TRUE,
    created_at  TIMESTAMP DEFAULT NOW()
);

-- Roles (ADMIN, INSTRUCTOR, STUDENT, GUEST)
CREATE TABLE IF NOT EXISTS roles (
    id   SERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL
);

-- RBAC join table
CREATE TABLE IF NOT EXISTS user_roles (
    user_id UUID REFERENCES users(id) ON DELETE CASCADE,
    role_id INT  REFERENCES roles(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);

-- Refresh token store (Redis is primary; DB for audit trail)
CREATE TABLE IF NOT EXISTS refresh_tokens (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id     UUID REFERENCES users(id) ON DELETE CASCADE,
    token_hash  VARCHAR(255) UNIQUE NOT NULL,
    device_id   VARCHAR(100),
    expires_at  TIMESTAMP NOT NULL,
    revoked     BOOLEAN DEFAULT FALSE
);

-- Indexes for 1M scale
CREATE INDEX IF NOT EXISTS idx_users_email    ON users(email);
CREATE INDEX IF NOT EXISTS idx_refresh_userid ON refresh_tokens(user_id);
CREATE INDEX IF NOT EXISTS idx_refresh_token  ON refresh_tokens(token_hash);

-- Seed default roles
INSERT INTO roles (name) VALUES ('ROLE_ADMIN'), ('ROLE_INSTRUCTOR'), ('ROLE_STUDENT'), ('ROLE_GUEST')
ON CONFLICT (name) DO NOTHING;
