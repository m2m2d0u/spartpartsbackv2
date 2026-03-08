-- Migration to rename part_image.url to part_image.reference
-- Store only the object reference (path) instead of the full URL

ALTER TABLE part_image RENAME COLUMN url TO reference;

-- Add comment for documentation
COMMENT ON COLUMN part_image.reference IS 'MinIO object reference (e.g., parts/images/uuid.jpg) instead of full URL';
