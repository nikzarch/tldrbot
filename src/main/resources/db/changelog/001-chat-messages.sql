--liquibase formatted sql

--changeset nikzarch:1
CREATE TABLE chat_messages (
    id BIGSERIAL PRIMARY KEY,
    chat_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    username VARCHAR(255),
    text TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_chat_messages_chat_id
    ON chat_messages (chat_id);

CREATE INDEX idx_chat_messages_chat_created_at
    ON chat_messages (chat_id, created_at DESC);