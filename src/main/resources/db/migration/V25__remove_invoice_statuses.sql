-- Remove SENT, EXPIRED, CANCELLED, and ACCEPTED statuses
-- Convert SENT and ACCEPTED to DRAFT (as they are preliminary states)
UPDATE invoice
SET status = 'DRAFT'
WHERE status IN ('SENT', 'ACCEPTED');

-- Convert EXPIRED and CANCELLED to OVERDUE (as they are terminal states)
UPDATE invoice
SET status = 'OVERDUE'
WHERE status IN ('EXPIRED', 'CANCELLED');
