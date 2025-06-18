CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

-- Optional: Insert a default user for testing
INSERT INTO users (username, password) VALUES ('testuser', '$2a$10$MY.wTJp5V6wUTxG.7YA79ekjE47k.uorpZ8XQ9V6qAKenecV..lEq');
