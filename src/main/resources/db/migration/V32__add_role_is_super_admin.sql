-- Add is_super_admin flag to role table
ALTER TABLE role ADD COLUMN is_super_admin BOOLEAN NOT NULL DEFAULT false;

-- Set ADMINISTRATEUR as super admin
UPDATE role SET is_super_admin = true WHERE code = 'ADMINISTRATEUR';
