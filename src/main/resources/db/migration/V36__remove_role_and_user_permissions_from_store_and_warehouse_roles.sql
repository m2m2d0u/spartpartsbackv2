-- Remove all USER_* permissions from RESPONSABLE_MAGASIN (Store Manager)
DELETE FROM role_permission
WHERE role_id = (SELECT id FROM role WHERE code = 'RESPONSABLE_MAGASIN')
  AND permission_id IN (SELECT id FROM permission WHERE code LIKE 'USER_%');

-- Remove all ROLE_* permissions from RESPONSABLE_MAGASIN (Store Manager)
DELETE FROM role_permission
WHERE role_id = (SELECT id FROM role WHERE code = 'RESPONSABLE_MAGASIN')
  AND permission_id IN (SELECT id FROM permission WHERE code LIKE 'ROLE_%');

-- Remove all USER_* permissions from RESPONSABLE_ENTREPOT (Warehouse Manager)
DELETE FROM role_permission
WHERE role_id = (SELECT id FROM role WHERE code = 'RESPONSABLE_ENTREPOT')
  AND permission_id IN (SELECT id FROM permission WHERE code LIKE 'USER_%');

-- Remove all ROLE_* permissions from RESPONSABLE_ENTREPOT (Warehouse Manager)
DELETE FROM role_permission
WHERE role_id = (SELECT id FROM role WHERE code = 'RESPONSABLE_ENTREPOT')
  AND permission_id IN (SELECT id FROM permission WHERE code LIKE 'ROLE_%');
