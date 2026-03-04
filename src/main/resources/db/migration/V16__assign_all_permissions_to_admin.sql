-- Ensure ADMINISTRATEUR has ALL permissions (including SYSTEM ones from V14).
-- Uses ON CONFLICT to skip any already-assigned permissions.

INSERT INTO role_permission (role_id, permission_id)
SELECT r.id, p.id
FROM role r
CROSS JOIN permission p
WHERE r.code = 'ADMINISTRATEUR'
ON CONFLICT ON CONSTRAINT uk_role_permission DO NOTHING;
