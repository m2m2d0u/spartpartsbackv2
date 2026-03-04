-- V13 updated display_name/description but the code column rename was added after Flyway
-- had already applied the original V13. This migration applies the code renames.

-- System-level roles
UPDATE role SET code = 'ADMINISTRATEUR'      WHERE code = 'ADMIN';
UPDATE role SET code = 'RESPONSABLE_MAGASIN' WHERE code = 'STORE_MANAGER';
UPDATE role SET code = 'OPERATEUR_ENTREPOT'  WHERE code = 'WAREHOUSE_OPERATOR';

-- Warehouse-level roles
UPDATE role SET code = 'RESPONSABLE_ENTREPOT'    WHERE code = 'WAREHOUSE_MANAGER';
UPDATE role SET code = 'MAGASINIER'              WHERE code = 'STOCK_KEEPER';
UPDATE role SET code = 'GESTIONNAIRE_COMMANDES'  WHERE code = 'ORDER_PROCESSOR';
UPDATE role SET code = 'RESPONSABLE_ACHATS'      WHERE code = 'PROCUREMENT_OFFICER';
UPDATE role SET code = 'COMPTABLE'               WHERE code = 'ACCOUNTANT';
UPDATE role SET code = 'OBSERVATEUR_ENTREPOT'    WHERE code = 'WAREHOUSE_VIEWER';

-- Now assign ALL permissions to ADMINISTRATEUR
INSERT INTO role_permission (role_id, permission_id)
SELECT r.id, p.id
FROM role r
CROSS JOIN permission p
WHERE r.code = 'ADMINISTRATEUR'
ON CONFLICT ON CONSTRAINT uk_role_permission DO NOTHING;

-- Assign selected SYSTEM permissions to RESPONSABLE_MAGASIN
INSERT INTO role_permission (role_id, permission_id)
SELECT r.id, p.id
FROM role r
CROSS JOIN permission p
WHERE r.code = 'RESPONSABLE_MAGASIN'
  AND p.code IN (
    'USER_VIEW', 'USER_CREATE', 'USER_UPDATE', 'USER_DELETE',
    'STORE_UPDATE',
    'WAREHOUSE_CREATE', 'WAREHOUSE_UPDATE',
    'ROLE_VIEW', 'ROLE_CREATE', 'ROLE_UPDATE',
    'PERMISSION_VIEW'
  )
ON CONFLICT ON CONSTRAINT uk_role_permission DO NOTHING;
