-- Create schema
CREATE SCHEMA IF NOT EXISTS attus;

-- Create tasks table
CREATE TABLE IF NOT EXISTS attus.tasks (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description VARCHAR(1000),
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    priority VARCHAR(50) NOT NULL DEFAULT 'MEDIUM',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP,
    CONSTRAINT check_status CHECK (status IN ('PENDING', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED')),
    CONSTRAINT check_priority CHECK (priority IN ('LOW', 'MEDIUM', 'HIGH', 'CRITICAL'))
);

-- Create indexes for better query performance
CREATE INDEX idx_tasks_status ON attus.tasks(status);
CREATE INDEX idx_tasks_priority ON attus.tasks(priority);
CREATE INDEX idx_tasks_created_at ON attus.tasks(created_at DESC);

-- Seed some sample data (optional)
INSERT INTO attus.tasks (title, description, status, priority) VALUES
('Implement task management API', 'Create REST endpoints for task CRUD operations', 'COMPLETED', 'CRITICAL'),
('Design frontend UI', 'Design the user interface for task management', 'COMPLETED', 'HIGH'),
('Write unit tests', 'Write comprehensive unit tests for services', 'IN_PROGRESS', 'HIGH'),
('Setup CI/CD pipeline', 'Configure GitHub Actions for automated testing and deployment', 'PENDING', 'MEDIUM'),
('Database optimization', 'Optimize queries and add appropriate indexes', 'PENDING', 'LOW');
