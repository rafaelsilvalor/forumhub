-- Create the profiles table for user roles
CREATE TABLE profiles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

-- Create the join table for the many-to-many relationship
CREATE TABLE user_profiles (
    user_id BIGINT NOT NULL,
    profile_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, profile_id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (profile_id) REFERENCES profiles(id)
);

-- Add new 'name' and 'email' columns to the users table
ALTER TABLE users ADD COLUMN name VARCHAR(255);
ALTER TABLE users ADD COLUMN email VARCHAR(255);

-- *** FIX IS HERE ***
-- Update existing rows to have a default value before adding constraints.
-- This prevents the "Invalid use of NULL value" error.
UPDATE users SET email = CONCAT(username, '@forum.com') WHERE email IS NULL;
UPDATE users SET name = username WHERE name IS NULL;

-- Now, it is safe to apply the NOT NULL constraint
ALTER TABLE users MODIFY COLUMN email VARCHAR(255) NOT NULL UNIQUE;


-- Optional: Insert default profiles
INSERT INTO profiles (name) VALUES ('ROLE_USER');
INSERT INTO profiles (name) VALUES ('ROLE_ADMIN');
