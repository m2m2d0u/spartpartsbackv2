-- Translate all role display_name and description to French

-- System-level roles (from V12)
UPDATE role SET
    display_name = 'Administrateur',
    description = 'Accès complet au système - gère tous les magasins, entrepôts et utilisateurs'
WHERE code = 'ADMIN';

UPDATE role SET
    display_name = 'Responsable de magasin',
    description = 'Gère les magasins assignés et leurs entrepôts'
WHERE code = 'STORE_MANAGER';

UPDATE role SET
    display_name = 'Opérateur d''entrepôt',
    description = 'Opère dans les entrepôts assignés avec des permissions granulaires'
WHERE code = 'WAREHOUSE_OPERATOR';

-- Warehouse-level roles (from V11)
UPDATE role SET
    display_name = 'Responsable d''entrepôt',
    description = 'Accès complet à toutes les opérations de l''entrepôt'
WHERE code = 'WAREHOUSE_MANAGER';

UPDATE role SET
    display_name = 'Magasinier',
    description = 'Gère l''inventaire et les niveaux de stock'
WHERE code = 'STOCK_KEEPER';

UPDATE role SET
    display_name = 'Gestionnaire de commandes',
    description = 'Traite et exécute les commandes clients'
WHERE code = 'ORDER_PROCESSOR';

UPDATE role SET
    display_name = 'Responsable des achats',
    description = 'Gère les achats et la réception des marchandises'
WHERE code = 'PROCUREMENT_OFFICER';

UPDATE role SET
    display_name = 'Comptable',
    description = 'Gère les factures et les documents financiers'
WHERE code = 'ACCOUNTANT';

UPDATE role SET
    display_name = 'Observateur d''entrepôt',
    description = 'Accès en lecture seule aux données de l''entrepôt'
WHERE code = 'WAREHOUSE_VIEWER';
