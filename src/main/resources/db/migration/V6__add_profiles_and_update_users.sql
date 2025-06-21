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

-- Make email unique and not null after populating existing records (if any)
-- For a new setup, we can add the constraints directly.
-- If you have existing users, you would first need to update their emails.
-- UPDATE users SET email = CONCAT(username, '@example.com') WHERE email IS NULL;
ALTER TABLE users MODIFY COLUMN email VARCHAR(255) NOT NULL UNIQUE;


-- Optional: Insert default profiles
INSERT INTO profiles (name) VALUES ('ROLE_USER');
INSERT INTO profiles (name) VALUES ('ROLE_ADMIN');