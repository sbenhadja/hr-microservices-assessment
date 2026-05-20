CREATE SCHEMA IF NOT EXISTS hr_employee;
CREATE TABLE IF NOT EXISTS hr_employee.department (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);
