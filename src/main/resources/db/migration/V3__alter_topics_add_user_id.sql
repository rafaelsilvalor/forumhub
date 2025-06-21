-- Add the user_id column to the topics table
ALTER TABLE topics
ADD COLUMN user_id BIGINT;

-- Add a foreign key constraint
ALTER TABLE topics
ADD CONSTRAINT fk_topics_user_id
FOREIGN KEY (user_id) REFERENCES users(id);

-- Make the user_id column NOT NULL (after ensuring all rows have a value if there was existing data)
ALTER TABLE topics
MODIFY COLUMN user_id BIGINT NOT NULL;

-- This is the corrected line that will now execute:
ALTER TABLE topics
DROP COLUMN author;
