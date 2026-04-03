-- Create assessments table
CREATE TABLE assessments (
    id UUID PRIMARY KEY,
    course_id UUID NOT NULL,
    title VARCHAR(255) NOT NULL,
    description VARCHAR(1000),
    total_marks INTEGER NOT NULL DEFAULT 20,
    mcq_marks INTEGER NOT NULL DEFAULT 10,
    coding_marks INTEGER NOT NULL DEFAULT 10,
    duration_minutes INTEGER NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'DRAFT',
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

-- Create questions table
CREATE TABLE questions (
    id UUID PRIMARY KEY,
    assessment_id UUID NOT NULL REFERENCES assessments(id) ON DELETE CASCADE,
    type VARCHAR(50) NOT NULL,
    text VARCHAR(2000) NOT NULL,
    code_template VARCHAR(5000),
    test_cases JSONB,
    marks INTEGER NOT NULL,
    order_index INTEGER NOT NULL
);

-- Create question_options table
CREATE TABLE question_options (
    id UUID PRIMARY KEY,
    question_id UUID NOT NULL REFERENCES questions(id) ON DELETE CASCADE,
    text VARCHAR(1000) NOT NULL,
    is_correct BOOLEAN NOT NULL DEFAULT FALSE,
    order_index INTEGER NOT NULL
);

-- Create attempts table with UNIQUE constraint
CREATE TABLE attempts (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    assessment_id UUID NOT NULL,
    started_at TIMESTAMP NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    is_submitted BOOLEAN NOT NULL DEFAULT FALSE,
    submitted_at TIMESTAMP,
    CONSTRAINT unique_user_assessment UNIQUE (user_id, assessment_id)
);

-- Create submissions table
CREATE TABLE submissions (
    id UUID PRIMARY KEY,
    attempt_id UUID NOT NULL UNIQUE,
    user_id UUID NOT NULL,
    assessment_id UUID NOT NULL,
    answers JSONB NOT NULL,
    mcq_score INTEGER NOT NULL DEFAULT 0,
    coding_score INTEGER NOT NULL DEFAULT 0,
    total_score INTEGER NOT NULL DEFAULT 0,
    category VARCHAR(50) NOT NULL,
    submitted_at TIMESTAMP NOT NULL
);

-- Create indexes
CREATE INDEX idx_assessments_course_id ON assessments(course_id);
CREATE INDEX idx_assessments_status ON assessments(status);
CREATE INDEX idx_questions_assessment_id ON questions(assessment_id);
CREATE INDEX idx_question_options_question_id ON question_options(question_id);
CREATE INDEX idx_attempts_user_assessment ON attempts(user_id, assessment_id);
CREATE INDEX idx_submissions_attempt_id ON submissions(attempt_id);
CREATE INDEX idx_submissions_user_assessment ON submissions(user_id, assessment_id);
