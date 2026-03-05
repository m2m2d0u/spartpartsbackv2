ALTER TABLE part ADD COLUMN reference VARCHAR(100) DEFAULT NULL;
CREATE INDEX idx_part_reference ON part(reference);
