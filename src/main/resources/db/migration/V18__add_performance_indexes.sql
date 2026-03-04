-- =====================================================================
-- V18: Performance indexes
-- Adds missing indexes identified from controller/service query analysis
-- =====================================================================

-- ===========================================
-- 1. Trigram indexes for name search (ILIKE)
-- ===========================================
CREATE EXTENSION IF NOT EXISTS pg_trgm;

CREATE INDEX idx_user_name_trgm ON "user" USING gin (name gin_trgm_ops);
CREATE INDEX idx_store_name_trgm ON store USING gin (name gin_trgm_ops);
CREATE INDEX idx_warehouse_name_trgm ON warehouse USING gin (name gin_trgm_ops);
CREATE INDEX idx_part_name_trgm ON part USING gin (name gin_trgm_ops);

-- ===========================================
-- 2. Filter columns used in paginated queries
-- ===========================================
CREATE INDEX idx_user_is_active ON "user" (is_active);
CREATE INDEX idx_store_is_active ON store (is_active);
CREATE INDEX idx_warehouse_is_active ON warehouse (is_active);
CREATE INDEX idx_stock_transfer_status ON stock_transfer (status);
CREATE INDEX idx_stock_movement_type ON stock_movement (type);
CREATE INDEX idx_return_status ON "return" (status);
CREATE INDEX idx_audit_log_action ON audit_log (action);

-- ===========================================
-- 3. FK indexes on item/detail tables
--    (PostgreSQL does NOT auto-create FK indexes)
-- ===========================================

-- return table FKs
CREATE INDEX idx_return_customer_id ON "return" (customer_id);
CREATE INDEX idx_return_invoice_id ON "return" (invoice_id);
CREATE INDEX idx_return_order_id ON "return" (order_id);

-- part_image
CREATE INDEX idx_part_image_part_id ON part_image (part_id);

-- order_item
CREATE INDEX idx_order_item_order_id ON order_item (order_id);
CREATE INDEX idx_order_item_part_id ON order_item (part_id);

-- purchase_order_item
CREATE INDEX idx_po_item_po_id ON purchase_order_item (purchase_order_id);
CREATE INDEX idx_po_item_part_id ON purchase_order_item (part_id);

-- invoice_item
CREATE INDEX idx_invoice_item_invoice_id ON invoice_item (invoice_id);
CREATE INDEX idx_invoice_item_part_id ON invoice_item (part_id);

-- stock_transfer_item
CREATE INDEX idx_transfer_item_transfer_id ON stock_transfer_item (stock_transfer_id);
CREATE INDEX idx_transfer_item_part_id ON stock_transfer_item (part_id);

-- return_item
CREATE INDEX idx_return_item_return_id ON return_item (return_id);
CREATE INDEX idx_return_item_part_id ON return_item (part_id);
CREATE INDEX idx_return_item_warehouse_id ON return_item (warehouse_id);

-- refund
CREATE INDEX idx_refund_return_id ON refund (return_id);
CREATE INDEX idx_refund_invoice_id ON refund (invoice_id);

-- ===========================================
-- 4. Composite indexes for multi-filter queries
-- ===========================================
CREATE INDEX idx_client_order_customer_status ON client_order (customer_id, status);
CREATE INDEX idx_po_supplier_status ON purchase_order (supplier_id, status);
CREATE INDEX idx_invoice_customer_status ON invoice (customer_id, status);
CREATE INDEX idx_sm_warehouse_part ON stock_movement (warehouse_id, part_id);
