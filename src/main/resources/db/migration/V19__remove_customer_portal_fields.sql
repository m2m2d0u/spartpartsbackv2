-- =====================================================================
-- V19: Remove customer portal access fields
-- Removes portal_access and password_hash columns from customer table
-- =====================================================================

-- Drop the index on portal_access
DROP INDEX IF EXISTS idx_customer_portal;

-- Drop the portal_access column
ALTER TABLE customer DROP COLUMN IF EXISTS portal_access;

-- Drop the password_hash column
ALTER TABLE customer DROP COLUMN IF EXISTS password_hash;
