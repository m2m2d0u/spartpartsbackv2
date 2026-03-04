-- =====================================================================
-- V20: Add design field to invoice_template
-- Adds design enum field to support 5 different invoice layouts
-- =====================================================================

-- Add design column with default value CLASSIC
ALTER TABLE invoice_template ADD COLUMN IF NOT EXISTS design VARCHAR(20) NOT NULL DEFAULT 'CLASSIC';

-- Create index for faster lookups by design
CREATE INDEX IF NOT EXISTS idx_invoice_template_design ON invoice_template(design);

-- Update existing templates to use CLASSIC design
UPDATE invoice_template SET design = 'CLASSIC' WHERE design IS NULL OR design = '';
