-- V1__initial_schema.sql
-- Create initial tables for roadmaps, milestones, and tasks using the existing JPA models

-- 1. Create roadmaps table
CREATE TABLE roadmaps (
    id UUID NOT NULL,
    title VARCHAR(150) NOT NULL,
    description TEXT,
    difficulty_level VARCHAR(50) NOT NULL,
    estimated_weeks INTEGER NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_roadmaps PRIMARY KEY (id)
);

-- 2. Create milestones table
CREATE TABLE milestones (
    id UUID NOT NULL,
    title VARCHAR(150) NOT NULL,
    description TEXT,
    sequence_order INTEGER NOT NULL,
    roadmap_id UUID NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_milestones PRIMARY KEY (id),
    CONSTRAINT fk_milestones_roadmap FOREIGN KEY (roadmap_id) REFERENCES roadmaps (id)
);

-- Create index on roadmap_id as specified in Milestone entity
CREATE INDEX idx_milestones_roadmap_id ON milestones (roadmap_id);

-- 3. Create tasks table
CREATE TABLE tasks (
    id UUID NOT NULL,
    title VARCHAR(150) NOT NULL,
    description TEXT,
    status VARCHAR(50) NOT NULL,
    estimated_hours INTEGER NOT NULL,
    milestone_id UUID NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_tasks PRIMARY KEY (id),
    CONSTRAINT fk_tasks_milestone FOREIGN KEY (milestone_id) REFERENCES milestones (id)
);

-- Create index on milestone_id as specified in Task entity
CREATE INDEX idx_tasks_milestone_id ON tasks (milestone_id);
