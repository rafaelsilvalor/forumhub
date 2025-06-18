-- Add the user_id column to the topics table
ALTER TABLE topics
ADD COLUMN user_id BIGINT;

-- Optional: If you have existing data in the 'topics' table, you would update
-- the 'user_id' for existing topics here before making it NOT NULL.
-- For example, setting all existing topics to a default user (e.g., user with ID 1).
-- UPDATE topics SET user_id = 1 WHERE user_id IS NULL;

-- Add a foreign key constraint
ALTER TABLE topics
ADD CONSTRAINT fk_topics_user_id
FOREIGN KEY (user_id) REFERENCES users(id);

-- Make the user_id column NOT NULL (after ensuring all rows have a value if there was existing data)
ALTER TABLE topics
MODIFY COLUMN user_id BIGINT NOT NULL;

-- Remove the old 'author' column if it was just a string and no longer needed
-- ALTER TABLE topics
-- DROP COLUMN author;
