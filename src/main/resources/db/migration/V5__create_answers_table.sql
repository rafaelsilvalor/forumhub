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