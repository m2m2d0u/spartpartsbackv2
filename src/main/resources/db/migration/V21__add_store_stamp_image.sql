-- =====================================================================
-- V21: Add stamp_image_url field to store
-- Adds stamp image URL field to store for invoice customization
-- =====================================================================

-- Add stamp_image_url column
ALTER TABLE store ADD COLUMN IF NOT EXISTS stamp_image_url VARCHAR(500);
