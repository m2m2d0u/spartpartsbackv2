-- Add system-level permissions for managing users, stores, warehouses, roles, and permissions.
-- These permissions are in the SYSTEM category and control access to admin endpoints.

INSERT INTO permission (code, display_name, description, category, level) VALUES
-- User Management
('USER_VIEW', 'Voir les utilisateurs', 'Consulter la liste et les détails des utilisateurs', 'SYSTEM', 'READ'),
('USER_CREATE', 'Créer des utilisateurs', 'Créer de nouveaux comptes utilisateur', 'SYSTEM', 'WRITE'),
('USER_UPDATE', 'Modifier les utilisateurs', 'Modifier les informations des utilisateurs', 'SYSTEM', 'WRITE'),
('USER_DELETE', 'Supprimer les utilisateurs', 'Désactiver ou supprimer des comptes utilisateur', 'SYSTEM', 'DELETE'),

-- Store Management
('STORE_CREATE', 'Créer des magasins', 'Créer de nouveaux magasins', 'SYSTEM', 'WRITE'),
('STORE_UPDATE', 'Modifier les magasins', 'Modifier les informations des magasins', 'SYSTEM', 'WRITE'),
('STORE_DELETE', 'Supprimer les magasins', 'Désactiver ou supprimer des magasins', 'SYSTEM', 'DELETE'),

-- Warehouse Management
('WAREHOUSE_CREATE', 'Créer des entrepôts', 'Créer de nouveaux entrepôts', 'SYSTEM', 'WRITE'),
('WAREHOUSE_UPDATE', 'Modifier les entrepôts', 'Modifier les informations des entrepôts', 'SYSTEM', 'WRITE'),
('WAREHOUSE_DELETE', 'Supprimer les entrepôts', 'Désactiver ou supprimer des entrepôts', 'SYSTEM', 'DELETE'),

-- Role Management
('ROLE_VIEW', 'Voir les rôles', 'Consulter la liste et les détails des rôles', 'SYSTEM', 'READ'),
('ROLE_CREATE', 'Créer des rôles', 'Créer de nouveaux rôles', 'SYSTEM', 'WRITE'),
('ROLE_UPDATE', 'Modifier les rôles', 'Modifier les rôles et leurs permissions', 'SYSTEM', 'WRITE'),
('ROLE_DELETE', 'Supprimer les rôles', 'Supprimer des rôles', 'SYSTEM', 'DELETE'),

-- Permission Management
('PERMISSION_VIEW', 'Voir les permissions', 'Consulter la liste et les détails des permissions', 'SYSTEM', 'READ');

-- Assign all 15 SYSTEM permissions to ADMINISTRATEUR
INSERT INTO role_permission (role_id, permission_id)
SELECT r.id, p.id
FROM role r
CROSS JOIN permission p
WHERE r.code = 'ADMINISTRATEUR'
  AND p.category = 'SYSTEM';

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
  );

-- OPERATEUR_ENTREPOT gets no SYSTEM permissions (no system management access)
