-- Migration to remove default_warehouse_id and portal_warehouse_id from store table
-- These fields are no longer needed as the system will use total stock across all warehouses

ALTER TABLE store DROP COLUMN IF EXISTS default_warehouse_id;
ALTER TABLE store DROP COLUMN IF EXISTS portal_warehouse_id;
