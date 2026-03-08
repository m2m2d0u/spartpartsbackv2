-- Migration to add is_main column to part_image table
-- Allows marking one image as the primary/main image for a part

ALTER TABLE part_image ADD COLUMN is_main BOOLEAN NOT NULL DEFAULT false;

-- Create index for faster queries on main images
CREATE INDEX idx_part_image_is_main ON part_image(part_id, is_main) WHERE is_main = true;

-- Add comment for documentation
COMMENT ON COLUMN part_image.is_main IS 'Indicates if this is the main/primary image for the part';
