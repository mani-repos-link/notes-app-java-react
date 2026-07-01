CREATE TABLE IF NOT EXISTS notes (
    id         BIGINT                   AUTO_INCREMENT PRIMARY KEY,
    title      VARCHAR(255)             NOT NULL,
    content    CLOB                     NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

