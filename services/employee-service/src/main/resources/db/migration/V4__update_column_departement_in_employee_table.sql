INSERT INTO hr_employee.department (id, name)
SELECT gen_random_uuid(), departement
FROM hr_employee.employee
WHERE departement IS NOT NULL;

ALTER TABLE hr_employee.employee
DROP COLUMN departement;

ALTER TABLE hr_employee.employee
ADD COLUMN department_id UUID;

ALTER TABLE hr_employee.employee
ADD CONSTRAINT fk_employee_department
FOREIGN KEY (department_id)
REFERENCES hr_employee.department(id);