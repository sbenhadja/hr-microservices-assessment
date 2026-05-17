
CREATE SCHEMA IF NOT EXISTS hr_leave;

CREATE TABLE IF NOT EXISTS hr_leave.leave (
    id UUID PRIMARY KEY,
    employee_id UUID NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    reason TEXT,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW()
);

-- Optional: index for employee lookups
CREATE INDEX IF NOT EXISTS idx_leave_employee_id ON hr_leave.leave (employee_id);

