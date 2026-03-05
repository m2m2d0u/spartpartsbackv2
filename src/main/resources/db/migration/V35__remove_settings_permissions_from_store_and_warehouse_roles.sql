-- Remove SETTINGS_VIEW and SETTINGS_UPDATE from RESPONSABLE_MAGASIN (Store Manager)
DELETE FROM role_permission
WHERE role_id = (SELECT id FROM role WHERE code = 'RESPONSABLE_MAGASIN')
  AND permission_id IN (SELECT id FROM permission WHERE code IN ('SETTINGS_VIEW', 'SETTINGS_UPDATE'));

-- Remove SETTINGS_VIEW and SETTINGS_UPDATE from RESPONSABLE_ENTREPOT (Warehouse Manager)
DELETE FROM role_permission
WHERE role_id = (SELECT id FROM role WHERE code = 'RESPONSABLE_ENTREPOT')
  AND permission_id IN (SELECT id FROM permission WHERE code IN ('SETTINGS_VIEW', 'SETTINGS_UPDATE'));
