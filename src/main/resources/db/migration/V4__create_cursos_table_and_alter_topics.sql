-- Create the courses table
CREATE TABLE courses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    category VARCHAR(255)
);

-- Add the foreign key column to the topics table
ALTER TABLE topics ADD COLUMN course_id BIGINT;

-- Add the foreign key constraint
ALTER TABLE topics
ADD CONSTRAINT fk_topics_course_id
FOREIGN KEY (course_id) REFERENCES courses(id);

-- Optional: Populate with some courses for testing
INSERT INTO courses(name, category) VALUES('Spring Boot 3', 'Programming');
INSERT INTO courses(name, category) VALUES('Java Basics', 'Programming');

-- Remove the old 'course' column (This was done in a previous version of the script you had)
-- Ensure this is what you want if you have existing data.
-- The original 'course' column was removed in the file you provided earlier. We are just renaming `curso_id` to `course_id`.
-- The column `curso_id` was added and then this one replaces it. If you ran the old one, you might need to drop `curso_id`.
-- For simplicity, this script assumes a clean state from V3.
