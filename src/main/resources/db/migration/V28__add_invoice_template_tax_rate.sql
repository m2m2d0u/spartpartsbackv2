ALTER TABLE invoice_template ADD COLUMN tax_rate_id UUID DEFAULT NULL;
ALTER TABLE invoice_template ADD CONSTRAINT fk_invoice_template_tax_rate FOREIGN KEY (tax_rate_id) REFERENCES tax_rate(id);
