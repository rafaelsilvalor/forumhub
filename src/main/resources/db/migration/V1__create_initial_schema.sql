-- Create the table for user roles
CREATE TABLE profiles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

-- Create the main users table
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    username VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

-- Create the join table for the many-to-many relationship between users and profiles
CREATE TABLE user_profiles (
    user_id BIGINT NOT NULL,
    profile_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, profile_id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (profile_id) REFERENCES profiles(id)
);

-- Create the courses table
CREATE TABLE courses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    category VARCHAR(255)
);

-- Create the topics table with all foreign keys correctly defined
CREATE TABLE topics (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(100) NOT NULL UNIQUE,
    message VARCHAR(500) NOT NULL UNIQUE,
    creation_date DATETIME NOT NULL,
    status BOOLEAN NOT NULL DEFAULT TRUE,
    author_id BIGINT NOT NULL,
    course_id BIGINT NOT NULL,
    CONSTRAINT fk_topics_author_id FOREIGN KEY (author_id) REFERENCES users(id),
    CONSTRAINT fk_topics_course_id FOREIGN KEY (course_id) REFERENCES courses(id)
);

-- Create the answers table
CREATE TABLE answers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    message TEXT NOT NULL,
    topic_id BIGINT NOT NULL,
    creation_date DATETIME NOT NULL,
    author_id BIGINT NOT NULL,
    solution BOOLEAN DEFAULT FALSE,
    CONSTRAINT fk_answers_topic_id FOREIGN KEY (topic_id) REFERENCES topics(id),
    CONSTRAINT fk_answers_author_id FOREIGN KEY (author_id) REFERENCES users(id)
);
