-- Migration to convert legacy warehouse permissions to granular permissions
-- This provides fine-grained access control by replacing broad permissions with specific ones

-- Create a temporary function to migrate permissions
CREATE OR REPLACE FUNCTION migrate_legacy_permissions()
RETURNS void AS $$
DECLARE
    uwp_record RECORD;
    uw_id UUID;
BEGIN
    -- Process each user_warehouse_permission with legacy permissions
    FOR uwp_record IN
        SELECT DISTINCT uwp.user_warehouse_id, uwp.permission
        FROM user_warehouse_permission uwp
        WHERE uwp.permission IN ('STOCK_MANAGE', 'ORDER_MANAGE', 'INVOICE_MANAGE',
                                 'PROCUREMENT_MANAGE', 'TRANSFER_MANAGE', 'RETURN_MANAGE')
    LOOP
        uw_id := uwp_record.user_warehouse_id;

        -- STOCK_MANAGE → Granular stock permissions
        IF uwp_record.permission = 'STOCK_MANAGE' THEN
            -- Delete legacy permission
            DELETE FROM user_warehouse_permission
            WHERE user_warehouse_id = uw_id AND permission = 'STOCK_MANAGE';

            -- Insert granular permissions (avoid duplicates)
            INSERT INTO user_warehouse_permission (user_warehouse_id, permission)
            SELECT uw_id, unnest(ARRAY[
                'STOCK_VIEW'::VARCHAR,
                'STOCK_CREATE'::VARCHAR,
                'STOCK_UPDATE'::VARCHAR,
                'STOCK_ADJUST'::VARCHAR,
                'STOCK_DELETE'::VARCHAR,
                'STOCK_EXPORT'::VARCHAR
            ])
            ON CONFLICT DO NOTHING;
        END IF;

        -- ORDER_MANAGE → Granular order permissions
        IF uwp_record.permission = 'ORDER_MANAGE' THEN
            DELETE FROM user_warehouse_permission
            WHERE user_warehouse_id = uw_id AND permission = 'ORDER_MANAGE';

            INSERT INTO user_warehouse_permission (user_warehouse_id, permission)
            SELECT uw_id, unnest(ARRAY[
                'ORDER_VIEW'::VARCHAR,
                'ORDER_CREATE'::VARCHAR,
                'ORDER_UPDATE'::VARCHAR,
                'ORDER_DELETE'::VARCHAR,
                'ORDER_APPROVE'::VARCHAR,
                'ORDER_FULFILL'::VARCHAR,
                'ORDER_EXPORT'::VARCHAR
            ])
            ON CONFLICT DO NOTHING;
        END IF;

        -- INVOICE_MANAGE → Granular invoice permissions
        IF uwp_record.permission = 'INVOICE_MANAGE' THEN
            DELETE FROM user_warehouse_permission
            WHERE user_warehouse_id = uw_id AND permission = 'INVOICE_MANAGE';

            INSERT INTO user_warehouse_permission (user_warehouse_id, permission)
            SELECT uw_id, unnest(ARRAY[
                'INVOICE_VIEW'::VARCHAR,
                'INVOICE_CREATE'::VARCHAR,
                'INVOICE_UPDATE'::VARCHAR,
                'INVOICE_DELETE'::VARCHAR,
                'INVOICE_SEND'::VARCHAR,
                'INVOICE_PRINT'::VARCHAR,
                'INVOICE_PAYMENT'::VARCHAR,
                'INVOICE_EXPORT'::VARCHAR
            ])
            ON CONFLICT DO NOTHING;
        END IF;

        -- PROCUREMENT_MANAGE → Granular procurement permissions
        IF uwp_record.permission = 'PROCUREMENT_MANAGE' THEN
            DELETE FROM user_warehouse_permission
            WHERE user_warehouse_id = uw_id AND permission = 'PROCUREMENT_MANAGE';

            INSERT INTO user_warehouse_permission (user_warehouse_id, permission)
            SELECT uw_id, unnest(ARRAY[
                'PROCUREMENT_VIEW'::VARCHAR,
                'PROCUREMENT_CREATE'::VARCHAR,
                'PROCUREMENT_UPDATE'::VARCHAR,
                'PROCUREMENT_DELETE'::VARCHAR,
                'PROCUREMENT_APPROVE'::VARCHAR,
                'PROCUREMENT_RECEIVE'::VARCHAR,
                'PROCUREMENT_EXPORT'::VARCHAR
            ])
            ON CONFLICT DO NOTHING;
        END IF;

        -- TRANSFER_MANAGE → Granular transfer permissions
        IF uwp_record.permission = 'TRANSFER_MANAGE' THEN
            DELETE FROM user_warehouse_permission
            WHERE user_warehouse_id = uw_id AND permission = 'TRANSFER_MANAGE';

            INSERT INTO user_warehouse_permission (user_warehouse_id, permission)
            SELECT uw_id, unnest(ARRAY[
                'TRANSFER_VIEW'::VARCHAR,
                'TRANSFER_CREATE'::VARCHAR,
                'TRANSFER_UPDATE'::VARCHAR,
                'TRANSFER_DELETE'::VARCHAR,
                'TRANSFER_APPROVE'::VARCHAR,
                'TRANSFER_SEND'::VARCHAR,
                'TRANSFER_RECEIVE'::VARCHAR,
                'TRANSFER_EXPORT'::VARCHAR
            ])
            ON CONFLICT DO NOTHING;
        END IF;

        -- RETURN_MANAGE → Granular return permissions
        IF uwp_record.permission = 'RETURN_MANAGE' THEN
            DELETE FROM user_warehouse_permission
            WHERE user_warehouse_id = uw_id AND permission = 'RETURN_MANAGE';

            INSERT INTO user_warehouse_permission (user_warehouse_id, permission)
            SELECT uw_id, unnest(ARRAY[
                'RETURN_VIEW'::VARCHAR,
                'RETURN_CREATE'::VARCHAR,
                'RETURN_UPDATE'::VARCHAR,
                'RETURN_DELETE'::VARCHAR,
                'RETURN_APPROVE'::VARCHAR,
                'RETURN_REFUND'::VARCHAR,
                'RETURN_RESTOCK'::VARCHAR,
                'RETURN_EXPORT'::VARCHAR
            ])
            ON CONFLICT DO NOTHING;
        END IF;

    END LOOP;

    RAISE NOTICE 'Legacy permissions migrated to granular permissions successfully';
END;
$$ LANGUAGE plpgsql;

-- Execute the migration
SELECT migrate_legacy_permissions();

-- Drop the temporary function
DROP FUNCTION migrate_legacy_permissions();

-- Add comments documenting the migration
COMMENT ON TABLE user_warehouse_permission IS 'User warehouse permissions - migrated from legacy to granular permissions in V10';

-- Log migration statistics
DO $$
DECLARE
    total_permissions INTEGER;
    stock_perms INTEGER;
    order_perms INTEGER;
    invoice_perms INTEGER;
    procurement_perms INTEGER;
    transfer_perms INTEGER;
    return_perms INTEGER;
BEGIN
    SELECT COUNT(*) INTO total_permissions FROM user_warehouse_permission;
    SELECT COUNT(*) INTO stock_perms FROM user_warehouse_permission WHERE permission LIKE 'STOCK_%';
    SELECT COUNT(*) INTO order_perms FROM user_warehouse_permission WHERE permission LIKE 'ORDER_%';
    SELECT COUNT(*) INTO invoice_perms FROM user_warehouse_permission WHERE permission LIKE 'INVOICE_%';
    SELECT COUNT(*) INTO procurement_perms FROM user_warehouse_permission WHERE permission LIKE 'PROCUREMENT_%';
    SELECT COUNT(*) INTO transfer_perms FROM user_warehouse_permission WHERE permission LIKE 'TRANSFER_%';
    SELECT COUNT(*) INTO return_perms FROM user_warehouse_permission WHERE permission LIKE 'RETURN_%';

    RAISE NOTICE '=== Permission Migration Summary ===';
    RAISE NOTICE 'Total permissions: %', total_permissions;
    RAISE NOTICE 'Stock permissions: %', stock_perms;
    RAISE NOTICE 'Order permissions: %', order_perms;
    RAISE NOTICE 'Invoice permissions: %', invoice_perms;
    RAISE NOTICE 'Procurement permissions: %', procurement_perms;
    RAISE NOTICE 'Transfer permissions: %', transfer_perms;
    RAISE NOTICE 'Return permissions: %', return_perms;
    RAISE NOTICE '===================================';
END $$;
