CREATE SCHEMA IF NOT EXISTS hr_employee;
ALTER TABLE hr_employee.employee 
ADD COLUMN IF NOT EXISTS recrutement_date DATE;