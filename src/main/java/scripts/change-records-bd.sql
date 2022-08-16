-- liquibase formatted sql

-- changeset avasilieva:1
CREATE TABLE notification_task (
id BIGSERIAL PRIMARY KEY,
chat_id BIGINT,
notification_text TEXT,
date_time TIMESTAMP
)