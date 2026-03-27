-- =============================================================
-- HRMS Migration Script — Run in order
-- =============================================================

-- 1. Activity Log table
CREATE TABLE IF NOT EXISTS activity_log (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    action_type VARCHAR(50)  NOT NULL,
    description TEXT         NOT NULL,
    emp_id      BIGINT       NULL,           -- nullable; HR actions have no emp_id
    created_at  TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (emp_id) REFERENCES employee(emp_id) ON DELETE SET NULL
);

-- 2. Onboarding task template table
CREATE TABLE IF NOT EXISTS onboarding_task (
    task_id     BIGINT AUTO_INCREMENT PRIMARY KEY,
    title       VARCHAR(255) NOT NULL,
    description TEXT,
    is_bonus    BOOLEAN      DEFAULT FALSE
);

-- 3. Employee task assignment table
CREATE TABLE IF NOT EXISTS employee_task (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    emp_id          BIGINT       NOT NULL,
    task_id         BIGINT       NOT NULL,
    status          VARCHAR(20)  NOT NULL DEFAULT 'PENDING',   -- PENDING | COMPLETED
    proof_file_path VARCHAR(500) NULL,
    assigned_date   TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (emp_id)  REFERENCES employee(emp_id)  ON DELETE CASCADE,
    FOREIGN KEY (task_id) REFERENCES onboarding_task(task_id) ON DELETE CASCADE
);

-- =============================================================
-- Verify tables were created
-- =============================================================
SHOW TABLES LIKE 'activity_log';
SHOW TABLES LIKE 'onboarding_task';
SHOW TABLES LIKE 'employee_task';

-- =============================================================
-- ATTENDANCE SYSTEM — Check-In / Check-Out Migration
-- Run this AFTER the base schema is already applied.
-- =============================================================

-- 1. Add check_out_time column if it doesn't exist
ALTER TABLE attendance
    ADD COLUMN IF NOT EXISTS check_out_time DATETIME NULL DEFAULT NULL
        COMMENT 'Timestamp when employee checked out. NULL means still active.';

-- 2. Add role column to activity_log if missing (needed by ActivityLog entity)
ALTER TABLE activity_log
    ADD COLUMN IF NOT EXISTS role VARCHAR(20) NULL DEFAULT NULL
        COMMENT 'HR or EMPLOYEE role context for the log entry';

-- 3. Update AttendanceStatus ENUM to include ACTIVE and OFFLINE
--    (MySQL stores ENUM as string with EnumType.STRING, so no ALTER needed —
--     existing PRESENT/ABSENT rows are safe; new rows will use ACTIVE/OFFLINE)

-- 4. Helpful indexes for attendance queries
CREATE INDEX IF NOT EXISTS idx_attendance_emp_date
    ON attendance (emp_id, date);

CREATE INDEX IF NOT EXISTS idx_attendance_date_status
    ON attendance (date, status);

CREATE INDEX IF NOT EXISTS idx_attendance_checkin_checkout
    ON attendance (date, check_in_time, check_out_time);

-- 5. Verify structure
DESCRIBE attendance;

-- =============================================================
-- PROFILE SYSTEM MIGRATION
-- Adds profile_completed and resume_path to employee table
-- Ensures email lives only in users table
-- =============================================================

-- 1. Add profile_completed to employee if not exists
ALTER TABLE employee
    ADD COLUMN IF NOT EXISTS profile_completed BOOLEAN NOT NULL DEFAULT FALSE
        COMMENT 'True when all required profile fields are filled';

-- 2. Add resume_path to employee if not exists
ALTER TABLE employee
    ADD COLUMN IF NOT EXISTS resume_path VARCHAR(500) NULL DEFAULT NULL
        COMMENT 'Path to uploaded resume PDF file';

-- 3. Ensure email column exists in users table
ALTER TABLE users
    ADD COLUMN IF NOT EXISTS email VARCHAR(100) UNIQUE NULL DEFAULT NULL
        COMMENT 'User email address — primary identifier for login';

-- 4. Backfill email from username if email is null (for existing rows where username=email)
UPDATE users SET email = username WHERE email IS NULL AND username LIKE '%@%';


-- =============================================================
-- NAME FIELDS MIGRATION
-- Ensures first_name is nullable (employees created by HR approval
-- will have null first_name until they fill in their profile)
-- =============================================================

-- Allow first_name to be NULL (previously NOT NULL may block HR-approved users)
ALTER TABLE employee
    MODIFY COLUMN first_name VARCHAR(50) NULL DEFAULT NULL
        COMMENT 'Employee first name — editable from profile page';

-- Ensure middle_name and last_name columns exist and are nullable
ALTER TABLE employee
    MODIFY COLUMN middle_name VARCHAR(50) NULL DEFAULT NULL
        COMMENT 'Employee middle name — optional, editable from profile page';

ALTER TABLE employee
    MODIFY COLUMN last_name VARCHAR(50) NULL DEFAULT NULL
        COMMENT 'Employee last name — editable from profile page';
