-- Replace seed password hashes with valid BCrypt hash of "password123"
-- This ensures seed users can authenticate via the new JWT login endpoint
UPDATE "user"
SET password_hash = '$2a$10$872BKq/e9z2gQtG/mSVdh.EnvdBkSpkRWa7MjZDGn0LJ.bFP.EqBy';
