-- Delete all stores and warehouses
-- Dependencies were already cleaned in V30

-- Unlink default/portal warehouse from company_settings
UPDATE company_settings SET default_warehouse_id = NULL, portal_warehouse_id = NULL;

-- Unlink default/portal warehouse from stores
UPDATE store SET default_warehouse_id = NULL, portal_warehouse_id = NULL;

-- Delete warehouses then stores (warehouse has FK to store)
DELETE FROM warehouse;
DELETE FROM store;
