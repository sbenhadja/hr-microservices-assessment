CREATE SCHEMA IF NOT EXISTS hr_employee;
CREATE TABLE hr_employee.employee (
    id UUID PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    departement VARCHAR(255),
    status VARCHAR(50) NOT NULL
--     created_at TIMESTAMP NOT NULL,
);
