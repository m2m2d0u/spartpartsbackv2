-- Rename invoice status from PROFORMA to DRAFT
UPDATE invoice
SET status = 'DRAFT'
WHERE status = 'PROFORMA';
