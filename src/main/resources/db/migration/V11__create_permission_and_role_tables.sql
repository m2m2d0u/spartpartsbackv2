-- Migration to create permission and role management tables
-- This enables dynamic role and permission management without code changes

-- Create permission table
CREATE TABLE permission (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code VARCHAR(100) NOT NULL UNIQUE,
    display_name VARCHAR(255) NOT NULL,
    description TEXT,
    category VARCHAR(50) NOT NULL,
    level VARCHAR(50) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_permission_category ON permission(category);
CREATE INDEX idx_permission_level ON permission(level);
CREATE INDEX idx_permission_is_active ON permission(is_active);

-- Create role table
CREATE TABLE role (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code VARCHAR(100) NOT NULL UNIQUE,
    display_name VARCHAR(255) NOT NULL,
    description TEXT,
    is_system BOOLEAN NOT NULL DEFAULT false,
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_role_is_system ON role(is_system);
CREATE INDEX idx_role_is_active ON role(is_active);

-- Create role_permission join table
CREATE TABLE role_permission (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    role_id UUID NOT NULL REFERENCES role(id) ON DELETE CASCADE,
    permission_id UUID NOT NULL REFERENCES permission(id) ON DELETE CASCADE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_role_permission UNIQUE (role_id, permission_id)
);

CREATE INDEX idx_role_permission_role_id ON role_permission(role_id);
CREATE INDEX idx_role_permission_permission_id ON role_permission(permission_id);

-- Create user_warehouse_role table (assigns roles to user-warehouse combinations)
CREATE TABLE user_warehouse_role (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_warehouse_id UUID NOT NULL REFERENCES user_warehouse(id) ON DELETE CASCADE,
    role_id UUID NOT NULL REFERENCES role(id) ON DELETE CASCADE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_user_warehouse_role UNIQUE (user_warehouse_id, role_id)
);

CREATE INDEX idx_user_warehouse_role_uw_id ON user_warehouse_role(user_warehouse_id);
CREATE INDEX idx_user_warehouse_role_role_id ON user_warehouse_role(role_id);

-- Insert all permissions from WarehousePermission enum
INSERT INTO permission (code, display_name, description, category, level) VALUES
-- Stock Management Permissions
('STOCK_VIEW', 'View Stock', 'View warehouse stock levels and details', 'STOCK', 'READ'),
('STOCK_CREATE', 'Add Stock', 'Add new stock items to the warehouse', 'STOCK', 'WRITE'),
('STOCK_UPDATE', 'Update Stock', 'Update stock information (min levels, locations)', 'STOCK', 'WRITE'),
('STOCK_ADJUST', 'Adjust Stock', 'Adjust stock quantities (increase/decrease)', 'STOCK', 'WRITE'),
('STOCK_DELETE', 'Delete Stock', 'Remove stock items from the warehouse', 'STOCK', 'DELETE'),
('STOCK_EXPORT', 'Export Stock', 'Export stock data to Excel/CSV', 'STOCK', 'READ'),

-- Order Management Permissions
('ORDER_VIEW', 'View Orders', 'View warehouse orders and order details', 'ORDER', 'READ'),
('ORDER_CREATE', 'Create Orders', 'Create new orders for the warehouse', 'ORDER', 'WRITE'),
('ORDER_UPDATE', 'Update Orders', 'Update order information and status', 'ORDER', 'WRITE'),
('ORDER_DELETE', 'Delete Orders', 'Cancel or delete orders', 'ORDER', 'DELETE'),
('ORDER_APPROVE', 'Approve Orders', 'Approve or reject orders', 'ORDER', 'APPROVE'),
('ORDER_FULFILL', 'Fulfill Orders', 'Mark orders as fulfilled and process shipments', 'ORDER', 'WRITE'),
('ORDER_EXPORT', 'Export Orders', 'Export order data to Excel/CSV', 'ORDER', 'READ'),

-- Invoice Management Permissions
('INVOICE_VIEW', 'View Invoices', 'View warehouse invoices and invoice details', 'INVOICE', 'READ'),
('INVOICE_CREATE', 'Create Invoices', 'Create new invoices', 'INVOICE', 'WRITE'),
('INVOICE_UPDATE', 'Update Invoices', 'Update invoice information', 'INVOICE', 'WRITE'),
('INVOICE_DELETE', 'Delete Invoices', 'Delete or void invoices', 'INVOICE', 'DELETE'),
('INVOICE_SEND', 'Send Invoices', 'Send invoices to customers via email', 'INVOICE', 'WRITE'),
('INVOICE_PRINT', 'Print Invoices', 'Print or download invoice PDFs', 'INVOICE', 'READ'),
('INVOICE_PAYMENT', 'Record Payments', 'Record and manage invoice payments', 'INVOICE', 'WRITE'),
('INVOICE_EXPORT', 'Export Invoices', 'Export invoice data to Excel/CSV', 'INVOICE', 'READ'),

-- Procurement Permissions
('PROCUREMENT_VIEW', 'View Procurement', 'View purchase orders and procurement details', 'PROCUREMENT', 'READ'),
('PROCUREMENT_CREATE', 'Create Purchase Orders', 'Create new purchase orders', 'PROCUREMENT', 'WRITE'),
('PROCUREMENT_UPDATE', 'Update Purchase Orders', 'Update purchase order information', 'PROCUREMENT', 'WRITE'),
('PROCUREMENT_DELETE', 'Delete Purchase Orders', 'Delete or cancel purchase orders', 'PROCUREMENT', 'DELETE'),
('PROCUREMENT_APPROVE', 'Approve Purchase Orders', 'Approve or reject purchase orders', 'PROCUREMENT', 'APPROVE'),
('PROCUREMENT_RECEIVE', 'Receive Goods', 'Receive and process incoming shipments', 'PROCUREMENT', 'WRITE'),
('PROCUREMENT_EXPORT', 'Export Procurement', 'Export procurement data to Excel/CSV', 'PROCUREMENT', 'READ'),

-- Transfer Management Permissions
('TRANSFER_VIEW', 'View Transfers', 'View stock transfers between warehouses', 'TRANSFER', 'READ'),
('TRANSFER_CREATE', 'Create Transfers', 'Create new stock transfers', 'TRANSFER', 'WRITE'),
('TRANSFER_UPDATE', 'Update Transfers', 'Update transfer information', 'TRANSFER', 'WRITE'),
('TRANSFER_DELETE', 'Delete Transfers', 'Cancel or delete transfers', 'TRANSFER', 'DELETE'),
('TRANSFER_APPROVE', 'Approve Transfers', 'Approve or reject transfer requests', 'TRANSFER', 'APPROVE'),
('TRANSFER_SEND', 'Send Transfers', 'Ship transfers from the warehouse', 'TRANSFER', 'WRITE'),
('TRANSFER_RECEIVE', 'Receive Transfers', 'Receive incoming transfers', 'TRANSFER', 'WRITE'),
('TRANSFER_EXPORT', 'Export Transfers', 'Export transfer data to Excel/CSV', 'TRANSFER', 'READ'),

-- Return Management Permissions
('RETURN_VIEW', 'View Returns', 'View product returns and return details', 'RETURN', 'READ'),
('RETURN_CREATE', 'Create Returns', 'Process new product returns', 'RETURN', 'WRITE'),
('RETURN_UPDATE', 'Update Returns', 'Update return information', 'RETURN', 'WRITE'),
('RETURN_DELETE', 'Delete Returns', 'Cancel or delete returns', 'RETURN', 'DELETE'),
('RETURN_APPROVE', 'Approve Returns', 'Approve or reject return requests', 'RETURN', 'APPROVE'),
('RETURN_REFUND', 'Process Refunds', 'Process refunds for returns', 'RETURN', 'WRITE'),
('RETURN_RESTOCK', 'Restock Returns', 'Add returned items back to stock', 'RETURN', 'WRITE'),
('RETURN_EXPORT', 'Export Returns', 'Export return data to Excel/CSV', 'RETURN', 'READ'),

-- Report Permissions
('REPORT_VIEW', 'View Reports', 'View warehouse reports and analytics', 'REPORT', 'READ'),
('REPORT_EXPORT', 'Export Reports', 'Export reports to Excel/PDF', 'REPORT', 'READ'),
('REPORT_SALES', 'Sales Reports', 'View and export sales reports', 'REPORT', 'READ'),
('REPORT_INVENTORY', 'Inventory Reports', 'View and export inventory reports', 'REPORT', 'READ'),
('REPORT_FINANCIAL', 'Financial Reports', 'View and export financial reports', 'REPORT', 'READ'),

-- Customer Management Permissions
('CUSTOMER_VIEW', 'View Customers', 'View customer information', 'CUSTOMER', 'READ'),
('CUSTOMER_CREATE', 'Create Customers', 'Add new customers', 'CUSTOMER', 'WRITE'),
('CUSTOMER_UPDATE', 'Update Customers', 'Update customer information', 'CUSTOMER', 'WRITE'),
('CUSTOMER_DELETE', 'Delete Customers', 'Delete or deactivate customers', 'CUSTOMER', 'DELETE'),
('CUSTOMER_EXPORT', 'Export Customers', 'Export customer data to Excel/CSV', 'CUSTOMER', 'READ'),

-- Parts Management Permissions
('PART_VIEW', 'View Parts', 'View parts catalog and part details', 'PART', 'READ'),
('PART_CREATE', 'Create Parts', 'Add new parts to the catalog', 'PART', 'WRITE'),
('PART_UPDATE', 'Update Parts', 'Update part information and pricing', 'PART', 'WRITE'),
('PART_DELETE', 'Delete Parts', 'Delete or deactivate parts', 'PART', 'DELETE'),
('PART_EXPORT', 'Export Parts', 'Export parts catalog to Excel/CSV', 'PART', 'READ'),
('PART_IMPORT', 'Import Parts', 'Import parts from Excel/CSV', 'PART', 'WRITE'),
('PART_PRICING', 'Manage Pricing', 'Update part prices and discounts', 'PART', 'WRITE'),

-- Warehouse Settings Permissions
('SETTINGS_VIEW', 'View Settings', 'View warehouse settings and configuration', 'SETTINGS', 'READ'),
('SETTINGS_UPDATE', 'Update Settings', 'Update warehouse settings and configuration', 'SETTINGS', 'WRITE');

-- Create system roles with predefined permission sets
INSERT INTO role (code, display_name, description, is_system) VALUES
('WAREHOUSE_MANAGER', 'Warehouse Manager', 'Full access to all warehouse operations', true),
('STOCK_KEEPER', 'Stock Keeper', 'Manage inventory and stock levels', true),
('ORDER_PROCESSOR', 'Order Processor', 'Process and fulfill customer orders', true),
('PROCUREMENT_OFFICER', 'Procurement Officer', 'Manage purchasing and receiving', true),
('ACCOUNTANT', 'Accountant', 'Manage invoices and financial records', true),
('WAREHOUSE_VIEWER', 'Warehouse Viewer', 'Read-only access to warehouse data', true);

-- Assign permissions to WAREHOUSE_MANAGER role (all permissions)
INSERT INTO role_permission (role_id, permission_id)
SELECT r.id, p.id
FROM role r
CROSS JOIN permission p
WHERE r.code = 'WAREHOUSE_MANAGER';

-- Assign permissions to STOCK_KEEPER role (stock management)
INSERT INTO role_permission (role_id, permission_id)
SELECT r.id, p.id
FROM role r
CROSS JOIN permission p
WHERE r.code = 'STOCK_KEEPER'
  AND p.category IN ('STOCK', 'PART')
  AND p.code NOT IN ('STOCK_DELETE', 'PART_DELETE');

-- Assign permissions to ORDER_PROCESSOR role (orders and invoices)
INSERT INTO role_permission (role_id, permission_id)
SELECT r.id, p.id
FROM role r
CROSS JOIN permission p
WHERE r.code = 'ORDER_PROCESSOR'
  AND p.category IN ('ORDER', 'INVOICE', 'CUSTOMER')
  AND p.level IN ('READ', 'WRITE');

-- Assign permissions to PROCUREMENT_OFFICER role (procurement and transfers)
INSERT INTO role_permission (role_id, permission_id)
SELECT r.id, p.id
FROM role r
CROSS JOIN permission p
WHERE r.code = 'PROCUREMENT_OFFICER'
  AND p.category IN ('PROCUREMENT', 'TRANSFER', 'STOCK')
  AND p.code NOT IN ('STOCK_DELETE');

-- Assign permissions to ACCOUNTANT role (invoices and reports)
INSERT INTO role_permission (role_id, permission_id)
SELECT r.id, p.id
FROM role r
CROSS JOIN permission p
WHERE r.code = 'ACCOUNTANT'
  AND (p.category IN ('INVOICE', 'REPORT', 'CUSTOMER') OR p.code LIKE '%_VIEW' OR p.code LIKE '%_EXPORT');

-- Assign permissions to WAREHOUSE_VIEWER role (all read permissions)
INSERT INTO role_permission (role_id, permission_id)
SELECT r.id, p.id
FROM role r
CROSS JOIN permission p
WHERE r.code = 'WAREHOUSE_VIEWER'
  AND p.level = 'READ';

-- Add comments
COMMENT ON TABLE permission IS 'Warehouse permissions - granular actions users can perform';
COMMENT ON TABLE role IS 'Roles - collections of permissions for easier management';
COMMENT ON TABLE role_permission IS 'Role-Permission mapping - defines which permissions each role has';
COMMENT ON TABLE user_warehouse_role IS 'User-Warehouse-Role mapping - assigns roles to users for specific warehouses';

-- Migration summary
DO $$
DECLARE
    total_permissions INTEGER;
    total_roles INTEGER;
    manager_perms INTEGER;
    stock_perms INTEGER;
    order_perms INTEGER;
BEGIN
    SELECT COUNT(*) INTO total_permissions FROM permission;
    SELECT COUNT(*) INTO total_roles FROM role;
    SELECT COUNT(*) INTO manager_perms FROM role_permission rp JOIN role r ON rp.role_id = r.id WHERE r.code = 'WAREHOUSE_MANAGER';
    SELECT COUNT(*) INTO stock_perms FROM role_permission rp JOIN role r ON rp.role_id = r.id WHERE r.code = 'STOCK_KEEPER';
    SELECT COUNT(*) INTO order_perms FROM role_permission rp JOIN role r ON rp.role_id = r.id WHERE r.code = 'ORDER_PROCESSOR';

    RAISE NOTICE '=== Permission & Role Tables Created ===';
    RAISE NOTICE 'Total permissions: %', total_permissions;
    RAISE NOTICE 'Total roles: %', total_roles;
    RAISE NOTICE 'WAREHOUSE_MANAGER permissions: %', manager_perms;
    RAISE NOTICE 'STOCK_KEEPER permissions: %', stock_perms;
    RAISE NOTICE 'ORDER_PROCESSOR permissions: %', order_perms;
    RAISE NOTICE '========================================';
END $$;
