-- Migrate user.role from VARCHAR enum to FK reference to role table
-- Creates system-level roles (ADMIN, STORE_MANAGER, WAREHOUSE_OPERATOR) and links users to them

-- 1. Insert system-level roles
INSERT INTO role (code, display_name, description, is_system) VALUES
('ADMIN', 'Administrator', 'Full system access - manages all stores, warehouses, and users', true),
('STORE_MANAGER', 'Store Manager', 'Manages assigned stores and their warehouses', true),
('WAREHOUSE_OPERATOR', 'Warehouse Operator', 'Operates within assigned warehouses with granular permissions', true);

-- 2. Assign permissions to system roles
-- ADMIN gets all permissions
INSERT INTO role_permission (role_id, permission_id)
SELECT r.id, p.id
FROM role r
CROSS JOIN permission p
WHERE r.code = 'ADMIN';

-- STORE_MANAGER gets all permissions
INSERT INTO role_permission (role_id, permission_id)
SELECT r.id, p.id
FROM role r
CROSS JOIN permission p
WHERE r.code = 'STORE_MANAGER';

-- WAREHOUSE_OPERATOR gets READ-level permissions only
INSERT INTO role_permission (role_id, permission_id)
SELECT r.id, p.id
FROM role r
CROSS JOIN permission p
WHERE r.code = 'WAREHOUSE_OPERATOR'
  AND p.level = 'READ';

-- 3. Add role_id column to user table
ALTER TABLE "user" ADD COLUMN role_id UUID;

-- 4. Migrate existing data: map old role VARCHAR to new role entity
UPDATE "user" u
SET role_id = r.id
FROM role r
WHERE r.code = u.role;

-- 5. Verify no NULLs remain
DO $$
DECLARE
    null_count INTEGER;
BEGIN
    SELECT COUNT(*) INTO null_count FROM "user" WHERE role_id IS NULL;
    IF null_count > 0 THEN
        RAISE EXCEPTION 'Migration failed: % users have NULL role_id', null_count;
    END IF;
END $$;

-- 6. Set NOT NULL constraint and FK
ALTER TABLE "user" ALTER COLUMN role_id SET NOT NULL;
ALTER TABLE "user" ADD CONSTRAINT fk_user_role FOREIGN KEY (role_id) REFERENCES role(id);
CREATE INDEX idx_user_role_id ON "user"(role_id);

-- 7. Drop old role VARCHAR column
ALTER TABLE "user" DROP COLUMN role;
