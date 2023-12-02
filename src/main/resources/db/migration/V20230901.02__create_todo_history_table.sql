
CREATE TABLE todo_history (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    query_type VARCHAR(255) NOT NULL,
    logged_at DATETIME NOT NULL,
    todo_id BIGINT NOT NULL,
    title VARCHAR(255) NULL,
    completed BOOLEAN NULL
);
