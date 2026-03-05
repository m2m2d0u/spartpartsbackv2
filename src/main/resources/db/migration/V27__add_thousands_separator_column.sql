ALTER TABLE company_settings ADD COLUMN thousands_separator VARCHAR(5) NOT NULL DEFAULT ' ';
ALTER TABLE store ADD COLUMN thousands_separator VARCHAR(5);
