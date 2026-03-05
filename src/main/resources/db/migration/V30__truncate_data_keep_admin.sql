-- Truncate all business data, keeping only:
-- 1. The first admin user (ADMINISTRATEUR role)
-- 2. System roles (is_system = true)
-- 3. All permissions and role_permission mappings for system roles
-- 4. Company settings (singleton row)
-- 5. Sequence counters are reset

-- =============================================
-- Step 1: Delete child/dependent tables first
-- =============================================

-- Financial children
DELETE FROM refund;
DELETE FROM credit_note;
DELETE FROM return_item;
DELETE FROM "return";
DELETE FROM payment;
DELETE FROM invoice_item;
DELETE FROM invoice;

-- Order children
DELETE FROM order_item;
DELETE FROM client_order;

-- Cart
DELETE FROM cart_item;
DELETE FROM cart;

-- Purchase order children
DELETE FROM purchase_order_item;
DELETE FROM purchase_order;

-- Stock & transfers
DELETE FROM stock_movement;
DELETE FROM stock_transfer_item;
DELETE FROM stock_transfer;
DELETE FROM warehouse_stock;

-- Parts
DELETE FROM part_tag;
DELETE FROM part_image;
DELETE FROM part;

-- Audit
DELETE FROM audit_log;

-- =============================================
-- Step 2: Clean up user-warehouse-role mappings
-- =============================================
DELETE FROM user_warehouse_permission;
DELETE FROM user_warehouse_role;
DELETE FROM user_warehouse;
DELETE FROM user_store;

-- =============================================
-- Step 3: Remove all users and create a new admin
-- =============================================
DELETE FROM "user";

INSERT INTO "user" (id, name, email, password_hash, role_id, is_active, created_at, updated_at)
VALUES (
    gen_random_uuid(),
    'Administrateur',
    'admin@spareparts.com',
    '$2a$10$MrYMJX3xlt6/OChyaywhGOc7HGUWruz0sU31.xczhgTMvZaZ6GdNq', -- admin123
    (SELECT id FROM role WHERE code = 'ADMINISTRATEUR'),
    true,
    NOW(),
    NOW()
);

-- =============================================
-- Step 4: Keep only the 9 required roles
-- =============================================
DELETE FROM role_permission WHERE role_id IN (
    SELECT id FROM role WHERE code NOT IN (
        'ADMINISTRATEUR',
        'RESPONSABLE_MAGASIN',
        'RESPONSABLE_ENTREPOT'
    )
);
DELETE FROM role WHERE code NOT IN (
    'ADMINISTRATEUR',
    'RESPONSABLE_MAGASIN',
    'RESPONSABLE_ENTREPOT'
);

-- =============================================
-- Step 5: Clean up reference data
-- =============================================
DELETE FROM customer;
DELETE FROM supplier;
DELETE FROM category;
DELETE FROM car_model;
DELETE FROM car_brand;
DELETE FROM tag;

-- =============================================
-- Step 6: Clean up templates and tax rates
-- =============================================

-- Unlink template from company_settings and stores before deleting
UPDATE company_settings SET default_template_id = NULL;
UPDATE store SET default_template_id = NULL;

-- =============================================
-- Step 7: Reset sequence counters
-- =============================================
DELETE FROM sequence_counter;

-- =============================================
-- Verification
-- =============================================
DO $$
DECLARE
    user_count INTEGER;
    role_count INTEGER;
    perm_count INTEGER;
BEGIN
    SELECT COUNT(*) INTO user_count FROM "user";
    SELECT COUNT(*) INTO role_count FROM role;
    SELECT COUNT(*) INTO perm_count FROM permission;

    RAISE NOTICE '=== Truncation Complete ===';
    RAISE NOTICE 'Remaining users: %', user_count;
    RAISE NOTICE 'Remaining roles: %', role_count;
    RAISE NOTICE 'Remaining permissions: %', perm_count;
    RAISE NOTICE '===========================';
END $$;
