-- Enable uuid generation
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- Assessments
CREATE TABLE IF NOT EXISTS assessments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    course_id UUID NOT NULL,
    title VARCHAR(255) NOT NULL,
    description VARCHAR(1000),
    total_marks INTEGER NOT NULL DEFAULT 20,
    mcq_marks INTEGER NOT NULL DEFAULT 10,
    coding_marks INTEGER NOT NULL DEFAULT 10,
    duration_minutes INTEGER NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    CONSTRAINT chk_assessment_status CHECK (status IN ('DRAFT', 'PUBLISHED', 'ARCHIVED'))
);

-- Questions
CREATE TABLE IF NOT EXISTS questions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    assessment_id UUID NOT NULL REFERENCES assessments(id) ON DELETE CASCADE,
    type VARCHAR(20) NOT NULL,
    text VARCHAR(2000) NOT NULL,
    code_template VARCHAR(5000),
    test_cases JSONB,
    marks INTEGER NOT NULL,
    order_index INTEGER NOT NULL,
    CONSTRAINT chk_question_type CHECK (type IN ('MCQ', 'CODING'))
);
CREATE INDEX IF NOT EXISTS idx_questions_assessment ON questions(assessment_id);

-- Question Options
CREATE TABLE IF NOT EXISTS question_options (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    question_id UUID NOT NULL REFERENCES questions(id) ON DELETE CASCADE,
    text VARCHAR(1000) NOT NULL,
    is_correct BOOLEAN NOT NULL DEFAULT FALSE,
    order_index INTEGER NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_options_question ON question_options(question_id);

-- Attempts
CREATE TABLE IF NOT EXISTS attempts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    assessment_id UUID NOT NULL REFERENCES assessments(id) ON DELETE CASCADE,
    started_at TIMESTAMP NOT NULL DEFAULT NOW(),
    expires_at TIMESTAMP NOT NULL,
    is_submitted BOOLEAN NOT NULL DEFAULT FALSE,
    submitted_at TIMESTAMP,
    CONSTRAINT uq_attempt_user_assessment UNIQUE (user_id, assessment_id)
);
CREATE INDEX IF NOT EXISTS idx_attempts_user ON attempts(user_id);
CREATE INDEX IF NOT EXISTS idx_attempts_assessment ON attempts(assessment_id);

-- Submissions
CREATE TABLE IF NOT EXISTS submissions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    attempt_id UUID NOT NULL UNIQUE REFERENCES attempts(id) ON DELETE CASCADE,
    user_id UUID NOT NULL,
    assessment_id UUID NOT NULL REFERENCES assessments(id) ON DELETE CASCADE,
    answers JSONB NOT NULL,
    mcq_score INTEGER NOT NULL DEFAULT 0,
    coding_score INTEGER NOT NULL DEFAULT 0,
    total_score INTEGER NOT NULL DEFAULT 0,
    category VARCHAR(20) NOT NULL,
    submitted_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_submission_category CHECK (category IN ('BEGINNER', 'BASIC', 'ADVANCED', 'EXPERT'))
);
CREATE INDEX IF NOT EXISTS idx_submissions_user ON submissions(user_id);
CREATE INDEX IF NOT EXISTS idx_submissions_assessment ON submissions(assessment_id);
