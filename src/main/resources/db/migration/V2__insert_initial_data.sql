-- Insert default profiles/roles
INSERT INTO profiles (name) VALUES ('ROLE_USER');
INSERT INTO profiles (name) VALUES ('ROLE_ADMIN');

-- Insert some sample courses
INSERT INTO courses(name, category) VALUES('Spring Boot 3', 'Backend');
INSERT INTO courses(name, category) VALUES('Java & OOP', 'Programming');
INSERT INTO courses(name, category) VALUES('Testing with JUnit 5', 'QA');
