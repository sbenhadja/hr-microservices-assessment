CREATE SCHEMA IF NOT EXISTS hr_leave;
CREATE TABLE IF NOT EXISTS hr_leave.processed_event (
    event_id UUID PRIMARY KEY,
    processed_at TIMESTAMP NOT NULL DEFAULT NOW()
);

