-- V2__migrate_ids_to_uuid.sql
-- Migrate all entity IDs from BIGSERIAL (Long) to UUID
-- gen_random_uuid() is built into PostgreSQL 13+ (no extension needed)

-- =============================================
-- Drop ALL tables (reverse dependency order)
-- =============================================

DROP TABLE IF EXISTS audit_log CASCADE;
DROP TABLE IF EXISTS refund CASCADE;
DROP TABLE IF EXISTS credit_note CASCADE;
DROP TABLE IF EXISTS return_item CASCADE;
DROP TABLE IF EXISTS "return" CASCADE;
DROP TABLE IF EXISTS payment CASCADE;
DROP TABLE IF EXISTS invoice_item CASCADE;
DROP TABLE IF EXISTS invoice CASCADE;
DROP TABLE IF EXISTS order_item CASCADE;
DROP TABLE IF EXISTS client_order CASCADE;
DROP TABLE IF EXISTS cart_item CASCADE;
DROP TABLE IF EXISTS cart CASCADE;
DROP TABLE IF EXISTS stock_movement CASCADE;
DROP TABLE IF EXISTS stock_transfer_item CASCADE;
DROP TABLE IF EXISTS stock_transfer CASCADE;
DROP TABLE IF EXISTS warehouse_stock CASCADE;
DROP TABLE IF EXISTS purchase_order_item CASCADE;
DROP TABLE IF EXISTS purchase_order CASCADE;
DROP TABLE IF EXISTS company_settings CASCADE;
DROP TABLE IF EXISTS user_warehouse_permission CASCADE;
DROP TABLE IF EXISTS user_warehouse CASCADE;
DROP TABLE IF EXISTS part_image CASCADE;
DROP TABLE IF EXISTS part CASCADE;
DROP TABLE IF EXISTS sequence_counter CASCADE;
DROP TABLE IF EXISTS tax_rate CASCADE;
DROP TABLE IF EXISTS invoice_template CASCADE;
DROP TABLE IF EXISTS "user" CASCADE;
DROP TABLE IF EXISTS customer CASCADE;
DROP TABLE IF EXISTS supplier CASCADE;
DROP TABLE IF EXISTS warehouse CASCADE;
DROP TABLE IF EXISTS category CASCADE;

-- =============================================
-- Independent tables (no FK dependencies)
-- =============================================

CREATE TABLE category (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    image_url VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE warehouse (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(200) NOT NULL,
    code VARCHAR(20) NOT NULL UNIQUE,
    location VARCHAR(200),
    street VARCHAR(300),
    city VARCHAR(100),
    state VARCHAR(100),
    postal_code VARCHAR(20),
    country VARCHAR(100),
    contact_person VARCHAR(200),
    phone VARCHAR(50),
    ninea VARCHAR(50),
    rccm VARCHAR(50),
    tax_id VARCHAR(50),
    notes TEXT,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE supplier (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(200) NOT NULL,
    contact_person VARCHAR(200),
    email VARCHAR(200),
    phone VARCHAR(50),
    street VARCHAR(300),
    city VARCHAR(100),
    state VARCHAR(100),
    postal_code VARCHAR(20),
    country VARCHAR(100),
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE customer (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(200) NOT NULL,
    company VARCHAR(200),
    email VARCHAR(200) UNIQUE,
    phone VARCHAR(50),
    street VARCHAR(300),
    city VARCHAR(100),
    state VARCHAR(100),
    postal_code VARCHAR(20),
    country VARCHAR(100),
    tax_id VARCHAR(50),
    notes TEXT,
    portal_access BOOLEAN NOT NULL DEFAULT FALSE,
    password_hash VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_customer_email ON customer (email);
CREATE INDEX idx_customer_portal ON customer (portal_access);

CREATE TABLE "user" (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(200) NOT NULL,
    email VARCHAR(200) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(30) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE invoice_template (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    is_default BOOLEAN NOT NULL DEFAULT FALSE,
    primary_color VARCHAR(7) NOT NULL DEFAULT '#000000',
    accent_color VARCHAR(7) NOT NULL DEFAULT '#4F46E5',
    font_family VARCHAR(50) NOT NULL DEFAULT 'Helvetica',
    header_layout VARCHAR(20) NOT NULL DEFAULT 'LOGO_LEFT',
    logo_url VARCHAR(500),
    header_image_url VARCHAR(500),
    footer_image_url VARCHAR(500),
    stamp_image_url VARCHAR(500),
    signature_image_url VARCHAR(500),
    watermark_image_url VARCHAR(500),
    show_ninea BOOLEAN NOT NULL DEFAULT TRUE,
    show_rccm BOOLEAN NOT NULL DEFAULT TRUE,
    show_tax_id BOOLEAN NOT NULL DEFAULT TRUE,
    show_warehouse_address BOOLEAN NOT NULL DEFAULT FALSE,
    show_customer_tax_id BOOLEAN NOT NULL DEFAULT TRUE,
    show_payment_terms BOOLEAN NOT NULL DEFAULT TRUE,
    show_discount_column BOOLEAN NOT NULL DEFAULT TRUE,
    default_notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE tax_rate (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    label VARCHAR(50) NOT NULL,
    rate DECIMAL(5,2) NOT NULL,
    is_default BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE sequence_counter (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    entity_type VARCHAR(30) NOT NULL,
    "year" INTEGER NOT NULL,
    last_value BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT uk_sequence UNIQUE (entity_type, "year")
);

-- =============================================
-- Tables with FK to independent tables
-- =============================================

CREATE TABLE part (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    part_number VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    short_description VARCHAR(500),
    category_id UUID REFERENCES category(id),
    selling_price DECIMAL(12,2) NOT NULL,
    purchase_price DECIMAL(12,2) NOT NULL,
    min_stock_level INTEGER NOT NULL DEFAULT 0,
    published BOOLEAN NOT NULL DEFAULT FALSE,
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_part_category ON part (category_id);
CREATE INDEX idx_part_published ON part (published);

CREATE TABLE part_image (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    part_id UUID NOT NULL REFERENCES part(id),
    url VARCHAR(500) NOT NULL,
    sort_order INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE user_warehouse (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES "user"(id),
    warehouse_id UUID NOT NULL REFERENCES warehouse(id),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_user_warehouse UNIQUE (user_id, warehouse_id)
);

CREATE INDEX idx_uw_user ON user_warehouse (user_id);
CREATE INDEX idx_uw_warehouse ON user_warehouse (warehouse_id);

CREATE TABLE user_warehouse_permission (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_warehouse_id UUID NOT NULL REFERENCES user_warehouse(id),
    permission VARCHAR(30) NOT NULL,
    CONSTRAINT uk_uwp UNIQUE (user_warehouse_id, permission)
);

CREATE TABLE company_settings (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    company_name VARCHAR(200),
    logo_url VARCHAR(500),
    street VARCHAR(300),
    city VARCHAR(100),
    state VARCHAR(100),
    postal_code VARCHAR(20),
    country VARCHAR(100),
    tax_id VARCHAR(50),
    ninea VARCHAR(50),
    rccm VARCHAR(50),
    phone VARCHAR(50),
    email VARCHAR(200),
    default_tax_rate DECIMAL(5,2) NOT NULL DEFAULT 0,
    proforma_prefix VARCHAR(10) NOT NULL DEFAULT 'PRO',
    invoice_prefix VARCHAR(10) NOT NULL DEFAULT 'INV',
    deposit_prefix VARCHAR(10) NOT NULL DEFAULT 'DEP',
    credit_note_prefix VARCHAR(10) NOT NULL DEFAULT 'CN',
    order_prefix VARCHAR(10) NOT NULL DEFAULT 'ORD',
    po_prefix VARCHAR(10) NOT NULL DEFAULT 'PO',
    transfer_prefix VARCHAR(10) NOT NULL DEFAULT 'TRF',
    return_prefix VARCHAR(10) NOT NULL DEFAULT 'RET',
    default_payment_terms INTEGER NOT NULL DEFAULT 30,
    default_proforma_validity INTEGER NOT NULL DEFAULT 30,
    default_invoice_notes TEXT,
    default_template_id UUID REFERENCES invoice_template(id),
    sequential_reset_yearly BOOLEAN NOT NULL DEFAULT TRUE,
    currency_symbol VARCHAR(10) NOT NULL DEFAULT '$',
    currency_position VARCHAR(10) NOT NULL DEFAULT 'BEFORE',
    currency_decimals INTEGER NOT NULL DEFAULT 2,
    default_warehouse_id UUID REFERENCES warehouse(id),
    portal_warehouse_id UUID REFERENCES warehouse(id),
    portal_enabled BOOLEAN NOT NULL DEFAULT FALSE,
    portal_min_order_amount DECIMAL(12,2),
    portal_shipping_flat_rate DECIMAL(12,2),
    portal_free_shipping_above DECIMAL(12,2),
    portal_terms_text TEXT,
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Insert default company settings row with generated UUID
INSERT INTO company_settings (id) VALUES (gen_random_uuid());

CREATE TABLE purchase_order (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    po_number VARCHAR(30) NOT NULL UNIQUE,
    supplier_id UUID NOT NULL REFERENCES supplier(id),
    status VARCHAR(30) NOT NULL,
    total_amount DECIMAL(12,2) NOT NULL DEFAULT 0,
    order_date DATE NOT NULL,
    expected_delivery_date DATE,
    destination_warehouse_id UUID REFERENCES warehouse(id),
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_po_supplier ON purchase_order (supplier_id);
CREATE INDEX idx_po_status ON purchase_order (status);

CREATE TABLE purchase_order_item (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    purchase_order_id UUID NOT NULL REFERENCES purchase_order(id),
    part_id UUID NOT NULL REFERENCES part(id),
    quantity INTEGER NOT NULL CHECK (quantity > 0),
    unit_price DECIMAL(12,2) NOT NULL,
    received_quantity INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE warehouse_stock (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    warehouse_id UUID NOT NULL REFERENCES warehouse(id),
    part_id UUID NOT NULL REFERENCES part(id),
    quantity INTEGER NOT NULL DEFAULT 0,
    min_stock_level INTEGER NOT NULL DEFAULT 0,
    CONSTRAINT uk_warehouse_part UNIQUE (warehouse_id, part_id)
);

CREATE INDEX idx_ws_part ON warehouse_stock (part_id);
CREATE INDEX idx_ws_low_stock ON warehouse_stock (quantity, min_stock_level);

CREATE TABLE stock_transfer (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    transfer_number VARCHAR(30) NOT NULL UNIQUE,
    source_warehouse_id UUID NOT NULL REFERENCES warehouse(id),
    destination_warehouse_id UUID NOT NULL REFERENCES warehouse(id),
    status VARCHAR(20) NOT NULL,
    transfer_date DATE NOT NULL,
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE stock_transfer_item (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    stock_transfer_id UUID NOT NULL REFERENCES stock_transfer(id),
    part_id UUID NOT NULL REFERENCES part(id),
    quantity INTEGER NOT NULL CHECK (quantity > 0),
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE stock_movement (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    part_id UUID NOT NULL REFERENCES part(id),
    warehouse_id UUID NOT NULL REFERENCES warehouse(id),
    type VARCHAR(30) NOT NULL,
    quantity_change INTEGER NOT NULL,
    balance_after INTEGER NOT NULL,
    reference_type VARCHAR(30),
    reference_id UUID,
    notes VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_sm_part ON stock_movement (part_id);
CREATE INDEX idx_sm_warehouse ON stock_movement (warehouse_id);
CREATE INDEX idx_sm_reference ON stock_movement (reference_type, reference_id);
CREATE INDEX idx_sm_created ON stock_movement (created_at);

CREATE TABLE cart (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    customer_id UUID NOT NULL UNIQUE REFERENCES customer(id),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE cart_item (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    cart_id UUID NOT NULL REFERENCES cart(id),
    part_id UUID NOT NULL REFERENCES part(id),
    quantity INTEGER NOT NULL CHECK (quantity > 0),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_cart_part UNIQUE (cart_id, part_id)
);

CREATE TABLE client_order (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_number VARCHAR(30) NOT NULL UNIQUE,
    customer_id UUID NOT NULL REFERENCES customer(id),
    status VARCHAR(20) NOT NULL,
    subtotal DECIMAL(12,2) NOT NULL DEFAULT 0,
    tax_amount DECIMAL(12,2) NOT NULL DEFAULT 0,
    discount_amount DECIMAL(12,2) NOT NULL DEFAULT 0,
    shipping_amount DECIMAL(12,2) NOT NULL DEFAULT 0,
    total_amount DECIMAL(12,2) NOT NULL DEFAULT 0,
    shipping_street VARCHAR(300),
    shipping_city VARCHAR(100),
    shipping_state VARCHAR(100),
    shipping_postal VARCHAR(20),
    shipping_country VARCHAR(100),
    notes TEXT,
    tracking_number VARCHAR(100),
    warehouse_id UUID REFERENCES warehouse(id),
    order_date TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_order_customer ON client_order (customer_id);
CREATE INDEX idx_order_status ON client_order (status);
CREATE INDEX idx_order_date ON client_order (order_date);

CREATE TABLE order_item (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id UUID NOT NULL REFERENCES client_order(id),
    part_id UUID NOT NULL REFERENCES part(id),
    quantity INTEGER NOT NULL CHECK (quantity > 0),
    unit_price DECIMAL(12,2) NOT NULL,
    total_price DECIMAL(12,2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE invoice (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    invoice_number VARCHAR(30) NOT NULL UNIQUE,
    invoice_type VARCHAR(20) NOT NULL,
    customer_id UUID NOT NULL REFERENCES customer(id),
    order_id UUID REFERENCES client_order(id),
    proforma_id UUID REFERENCES invoice(id),
    deposit_id UUID REFERENCES invoice(id),
    template_id UUID REFERENCES invoice_template(id),
    status VARCHAR(20) NOT NULL,
    subtotal DECIMAL(12,2) NOT NULL DEFAULT 0,
    tax_amount DECIMAL(12,2) NOT NULL DEFAULT 0,
    discount_amount DECIMAL(12,2) NOT NULL DEFAULT 0,
    deposit_deduction DECIMAL(12,2) NOT NULL DEFAULT 0,
    total_amount DECIMAL(12,2) NOT NULL DEFAULT 0,
    issued_date DATE NOT NULL,
    due_date DATE,
    validity_date DATE,
    paid_date DATE,
    source_warehouse_id UUID REFERENCES warehouse(id),
    issuer_name VARCHAR(200),
    issuer_ninea VARCHAR(50),
    issuer_rccm VARCHAR(50),
    issuer_tax_id VARCHAR(50),
    issuer_address TEXT,
    notes TEXT,
    internal_notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_invoice_customer ON invoice (customer_id);
CREATE INDEX idx_invoice_type ON invoice (invoice_type);
CREATE INDEX idx_invoice_status ON invoice (status);
CREATE INDEX idx_invoice_due_date ON invoice (due_date);
CREATE INDEX idx_invoice_order ON invoice (order_id);
CREATE INDEX idx_invoice_proforma ON invoice (proforma_id);
CREATE INDEX idx_invoice_template ON invoice (template_id);

CREATE TABLE invoice_item (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    invoice_id UUID NOT NULL REFERENCES invoice(id),
    part_id UUID NOT NULL REFERENCES part(id),
    quantity INTEGER NOT NULL CHECK (quantity > 0),
    unit_price DECIMAL(12,2) NOT NULL,
    discount_percent DECIMAL(5,2) NOT NULL DEFAULT 0,
    discount_amount DECIMAL(12,2) NOT NULL DEFAULT 0,
    total_price DECIMAL(12,2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE payment (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    invoice_id UUID NOT NULL REFERENCES invoice(id),
    amount DECIMAL(12,2) NOT NULL,
    payment_method VARCHAR(20) NOT NULL,
    payment_date DATE NOT NULL,
    reference VARCHAR(100),
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_payment_invoice ON payment (invoice_id);
CREATE INDEX idx_payment_date ON payment (payment_date);

CREATE TABLE "return" (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    return_number VARCHAR(30) NOT NULL UNIQUE,
    invoice_id UUID REFERENCES invoice(id),
    order_id UUID REFERENCES client_order(id),
    customer_id UUID NOT NULL REFERENCES customer(id),
    status VARCHAR(20) NOT NULL,
    return_date DATE NOT NULL,
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE return_item (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    return_id UUID NOT NULL REFERENCES "return"(id),
    part_id UUID NOT NULL REFERENCES part(id),
    quantity INTEGER NOT NULL CHECK (quantity > 0),
    reason VARCHAR(30) NOT NULL,
    restock_action VARCHAR(20),
    warehouse_id UUID REFERENCES warehouse(id),
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE credit_note (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    credit_note_number VARCHAR(30) NOT NULL UNIQUE,
    return_id UUID NOT NULL UNIQUE REFERENCES "return"(id),
    total_amount DECIMAL(12,2) NOT NULL,
    issued_date DATE NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE refund (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    return_id UUID NOT NULL REFERENCES "return"(id),
    invoice_id UUID REFERENCES invoice(id),
    amount DECIMAL(12,2) NOT NULL,
    refund_method VARCHAR(20) NOT NULL,
    refund_date DATE NOT NULL,
    reference VARCHAR(100),
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE audit_log (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES "user"(id),
    customer_id UUID REFERENCES customer(id),
    action VARCHAR(20) NOT NULL,
    entity_type VARCHAR(50) NOT NULL,
    entity_id UUID NOT NULL,
    changes JSONB,
    ip_address VARCHAR(45),
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_audit_entity ON audit_log (entity_type, entity_id);
CREATE INDEX idx_audit_user ON audit_log (user_id);
CREATE INDEX idx_audit_created ON audit_log (created_at);
