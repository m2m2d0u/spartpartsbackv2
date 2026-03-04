-- Rename invoice status from DRAFT to PROFORMA
UPDATE invoice
SET status = 'PROFORMA'
WHERE status = 'DRAFT';
