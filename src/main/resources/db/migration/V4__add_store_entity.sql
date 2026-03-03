-- Create store table
CREATE TABLE store (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(200) NOT NULL,
    code VARCHAR(20) NOT NULL UNIQUE,
    street VARCHAR(300),
    city VARCHAR(100),
    state VARCHAR(100),
    postal_code VARCHAR(20),
    country VARCHAR(100),
    phone VARCHAR(50),
    email VARCHAR(200),
    logo_url VARCHAR(500),
    ninea VARCHAR(50),
    rccm VARCHAR(50),
    tax_id VARCHAR(50),
    proforma_prefix VARCHAR(10),
    invoice_prefix VARCHAR(10),
    deposit_prefix VARCHAR(10),
    credit_note_prefix VARCHAR(10),
    order_prefix VARCHAR(10),
    default_payment_terms INTEGER,
    default_proforma_validity INTEGER,
    default_template_id UUID REFERENCES invoice_template(id),
    default_invoice_notes TEXT,
    default_warehouse_id UUID REFERENCES warehouse(id),
    portal_warehouse_id UUID REFERENCES warehouse(id),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Add store_id to warehouse (nullable first, then backfill, then NOT NULL)
ALTER TABLE warehouse ADD COLUMN store_id UUID REFERENCES store(id);

-- Insert a default store, backfill existing warehouses
INSERT INTO store (id, name, code, city, country)
  VALUES ('b1000000-0000-0000-0000-000000000001', 'Magasin Principal Dakar', 'MAG-DKR', 'Dakar', 'Sénégal');

UPDATE warehouse SET store_id = 'b1000000-0000-0000-0000-000000000001';
ALTER TABLE warehouse ALTER COLUMN store_id SET NOT NULL;
CREATE INDEX idx_warehouse_store ON warehouse (store_id);

-- Remove ninea/rccm/tax_id from warehouse (moved to store)
ALTER TABLE warehouse DROP COLUMN IF EXISTS ninea;
ALTER TABLE warehouse DROP COLUMN IF EXISTS rccm;
ALTER TABLE warehouse DROP COLUMN IF EXISTS tax_id;
