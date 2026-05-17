CREATE SCHEMA IF NOT EXISTS hr_leave;
CREATE TABLE IF NOT EXISTS hr_leave.employee_projection (
    employee_id UUID PRIMARY KEY,
    status VARCHAR(30) NOT NULL
);
