# Database Schema — Spart Parts

> **Database**: PostgreSQL
> **ORM**: Spring Data JPA + Hibernate
> **Date**: 2026-03-03

---

## Entity Relationship Diagram

```
┌──────────┐      ┌──────────────┐      ┌───────────────────┐
│ Category │1───N│     Part      │1───N│    PartImage       │
└──────────┘      └──────┬───────┘      └───────────────────┘
                         │
          ┌──────────────┼──────────────────────────┐
          │              │                          │
          │1             │1                         │1
          N              N                          N
  ┌───────────────┐ ┌──────────────┐      ┌────────────────┐
  │WarehouseStock │ │StockMovement │      │   CartItem     │
  └───────┬───────┘ └──────────────┘      └───────┬────────┘
          │N                                      │N
          1                                       1
  ┌───────────────┐                        ┌──────────────┐
  │  Warehouse    │                        │    Cart      │
  └───────┬───────┘                        └──────┬───────┘
          │                                       │1
    ┌─────┼─────────┐                             1
    │1    │1        │                      ┌──────────────┐
    N     N         │                      │  Customer    │
┌────────┐┌────────┐│                      └──────┬───────┘
│Transfer││Transfer││                             │1
│(source)││(dest)  ││                          ┌──┼─────────┐
└───┬────┘└────────┘│                          N  N         N
    │1              │                   ┌──────┐┌───────┐┌─────┐
    N               │                   │Order ││Invoice││Return│
┌──────────────┐    │                   └──┬───┘└──┬────┘└──┬──┘
│TransferItem  │    │                      │1      │1       │1
└──────────────┘    │                      N       N        N
                    │               ┌──────────┐┌───────┐┌──────────┐
                    │               │OrderItem ││InvItem││ReturnItem│
                    │               └──────────┘└───────┘└──────────┘
                    │
  ┌─────────────┐   │          ┌─────────────┐
  │  Supplier   │1──┼────N────│PurchaseOrder │
  └─────────────┘   │          └──────┬───────┘
                    │                 │1
                    │                 N
                    │          ┌──────────────────┐
                    │          │PurchaseOrderItem  │
                    │          └──────────────────┘
                    │
           Invoice──┼──1───N──Payment
                    │
           Return───┼──1───1──CreditNote
                    │
           Return───┼──1───N──Refund
                    │
                    │
                    │   ┌──────┐     ┌───────────────┐     ┌─────────────────────────┐
                    └───│ User │1──N│UserWarehouse  │1──N│UserWarehousePermission  │
                        └──────┘     └───────┬───────┘     └─────────────────────────┘
                                             │N
                                             1
                                      ┌──────────────┐
                                      │  Warehouse   │
                                      └──────────────┘

                        CompanySettings, TaxRate, AuditLog, SequenceCounter
```

---

## Enums

```java
public enum PurchaseOrderStatus {
    DRAFT, SENT, PARTIALLY_RECEIVED, RECEIVED, CANCELLED
}

public enum InvoiceType {
    PROFORMA,   // Preliminary quote/estimate — no stock impact, no payment
    STANDARD,   // Official commercial invoice — stock deduction, payment required
    DEPOSIT     // Partial upfront payment — no stock impact, payment required
}

public enum InvoiceStatus {
    DRAFT, SENT, PAID, PARTIALLY_PAID, OVERDUE, CANCELLED,
    ACCEPTED,   // Proforma only: converted to standard invoice
    EXPIRED     // Proforma only: validity period passed
}

public enum PaymentMethod {
    CASH, BANK_TRANSFER, CHECK, CREDIT_CARD, OTHER
}

public enum OrderStatus {
    PENDING, CONFIRMED, PROCESSING, SHIPPED, DELIVERED, COMPLETED, CANCELLED
}

public enum ReturnStatus {
    REQUESTED, APPROVED, RECEIVED, REFUNDED, CLOSED, REJECTED
}

public enum ReturnReason {
    DEFECTIVE, WRONG_PART, CHANGED_MIND, DAMAGED_IN_TRANSIT, OTHER
}

public enum RestockAction {
    RETURN_TO_STOCK, WRITE_OFF
}

public enum StockTransferStatus {
    PENDING, IN_TRANSIT, COMPLETED, CANCELLED
}

public enum StockMovementType {
    PURCHASE, SALE, ADJUSTMENT, TRANSFER_IN, TRANSFER_OUT,
    RETURN, CLIENT_ORDER, ORDER_CANCELLATION, INVOICE_CANCELLATION, INITIAL
}

public enum RefundMethod {
    ORIGINAL_METHOD, CASH, BANK_TRANSFER, STORE_CREDIT
}

public enum UserRole {
    ADMIN,              // Full access to all warehouses and all features
    WAREHOUSE_OPERATOR  // Access scoped to assigned warehouses only
}

public enum WarehousePermission {
    STOCK_MANAGE,       // View/adjust stock, manage stock movements
    ORDER_MANAGE,       // View/process client orders
    INVOICE_MANAGE,     // Create/edit invoices, record payments
    PROCUREMENT_MANAGE, // Create/receive purchase orders
    TRANSFER_MANAGE,    // Create/manage stock transfers
    RETURN_MANAGE       // Process returns and refunds
}
```

---

## Entities

### 1. Category

| Column      | Type         | Constraints                  |
|-------------|--------------|------------------------------|
| id          | BIGINT       | PK, auto-generated           |
| name        | VARCHAR(100) | NOT NULL, UNIQUE             |
| description | TEXT         | nullable                     |
| image_url   | VARCHAR(500) | nullable (for portal display)|
| created_at  | TIMESTAMP    | NOT NULL, default now        |
| updated_at  | TIMESTAMP    | NOT NULL, auto-updated       |

---

### 2. Part

| Column             | Type           | Constraints                     |
|--------------------|----------------|---------------------------------|
| id                 | BIGINT         | PK, auto-generated              |
| part_number        | VARCHAR(50)    | NOT NULL, UNIQUE                 |
| name               | VARCHAR(200)   | NOT NULL                         |
| description        | TEXT           | nullable (rich text)             |
| short_description  | VARCHAR(500)   | nullable (catalog card text)     |
| category_id        | BIGINT         | FK → Category, nullable          |
| selling_price      | DECIMAL(12,2)  | NOT NULL, >= 0                   |
| purchase_price     | DECIMAL(12,2)  | NOT NULL, >= 0                   |
| min_stock_level    | INTEGER        | NOT NULL, default 0              |
| published          | BOOLEAN        | NOT NULL, default false          |
| notes              | TEXT           | nullable (internal)              |
| created_at         | TIMESTAMP      | NOT NULL, default now            |
| updated_at         | TIMESTAMP      | NOT NULL, auto-updated           |

**Indexes**: `idx_part_category` on (category_id), `idx_part_published` on (published), full-text index on (name, part_number, description)

---

### 3. PartImage

| Column      | Type         | Constraints                  |
|-------------|--------------|------------------------------|
| id          | BIGINT       | PK, auto-generated           |
| part_id     | BIGINT       | FK → Part, NOT NULL          |
| url         | VARCHAR(500) | NOT NULL                     |
| sort_order  | INTEGER      | NOT NULL, default 0          |
| created_at  | TIMESTAMP    | NOT NULL, default now        |

**Constraint**: max 5 images per part (enforced at application level)

---

### 4. Supplier

| Column          | Type         | Constraints              |
|-----------------|--------------|--------------------------|
| id              | BIGINT       | PK, auto-generated       |
| name            | VARCHAR(200) | NOT NULL                 |
| contact_person  | VARCHAR(200) | nullable                 |
| email           | VARCHAR(200) | nullable                 |
| phone           | VARCHAR(50)  | nullable                 |
| street          | VARCHAR(300) | nullable                 |
| city            | VARCHAR(100) | nullable                 |
| state           | VARCHAR(100) | nullable                 |
| postal_code     | VARCHAR(20)  | nullable                 |
| country         | VARCHAR(100) | nullable                 |
| notes           | TEXT         | nullable                 |
| created_at      | TIMESTAMP    | NOT NULL, default now    |
| updated_at      | TIMESTAMP    | NOT NULL, auto-updated   |

---

### 5. PurchaseOrder

| Column                   | Type           | Constraints                          |
|--------------------------|----------------|--------------------------------------|
| id                       | BIGINT         | PK, auto-generated                   |
| po_number                | VARCHAR(30)    | NOT NULL, UNIQUE                     |
| supplier_id              | BIGINT         | FK → Supplier, NOT NULL              |
| status                   | VARCHAR(30)    | NOT NULL (enum PurchaseOrderStatus)  |
| total_amount             | DECIMAL(12,2)  | NOT NULL, default 0                  |
| order_date               | DATE           | NOT NULL                             |
| expected_delivery_date   | DATE           | nullable                             |
| destination_warehouse_id | BIGINT         | FK → Warehouse, nullable             |
| notes                    | TEXT           | nullable                             |
| created_at               | TIMESTAMP      | NOT NULL, default now                |
| updated_at               | TIMESTAMP      | NOT NULL, auto-updated               |

**Indexes**: `idx_po_supplier` on (supplier_id), `idx_po_status` on (status)

---

### 6. PurchaseOrderItem

| Column            | Type          | Constraints                    |
|-------------------|---------------|--------------------------------|
| id                | BIGINT        | PK, auto-generated             |
| purchase_order_id | BIGINT        | FK → PurchaseOrder, NOT NULL   |
| part_id           | BIGINT        | FK → Part, NOT NULL            |
| quantity          | INTEGER       | NOT NULL, > 0                  |
| unit_price        | DECIMAL(12,2) | NOT NULL, >= 0                 |
| received_quantity | INTEGER       | NOT NULL, default 0            |
| created_at        | TIMESTAMP     | NOT NULL, default now          |

---

### 7. Customer

| Column         | Type         | Constraints                   |
|----------------|--------------|-------------------------------|
| id             | BIGINT       | PK, auto-generated            |
| name           | VARCHAR(200) | NOT NULL                      |
| company        | VARCHAR(200) | nullable                      |
| email          | VARCHAR(200) | UNIQUE, nullable              |
| phone          | VARCHAR(50)  | nullable                      |
| street         | VARCHAR(300) | nullable                      |
| city           | VARCHAR(100) | nullable                      |
| state          | VARCHAR(100) | nullable                      |
| postal_code    | VARCHAR(20)  | nullable                      |
| country        | VARCHAR(100) | nullable                      |
| tax_id         | VARCHAR(50)  | nullable                      |
| notes          | TEXT         | nullable (internal)           |
| portal_access  | BOOLEAN      | NOT NULL, default false       |
| password_hash  | VARCHAR(255) | nullable (for portal login)   |
| created_at     | TIMESTAMP    | NOT NULL, default now         |
| updated_at     | TIMESTAMP    | NOT NULL, auto-updated        |

**Indexes**: `idx_customer_email` on (email), `idx_customer_portal` on (portal_access)

---

### 8. Invoice

| Column              | Type          | Constraints                       |
|---------------------|---------------|-----------------------------------|
| id                  | BIGINT        | PK, auto-generated                |
| invoice_number      | VARCHAR(30)   | NOT NULL, UNIQUE                  |
| invoice_type        | VARCHAR(20)   | NOT NULL (enum InvoiceType)       |
| customer_id         | BIGINT        | FK → Customer, NOT NULL           |
| order_id            | BIGINT        | FK → ClientOrder, nullable        |
| proforma_id         | BIGINT        | FK → Invoice (self), nullable     |
| deposit_id          | BIGINT        | FK → Invoice (self), nullable     |
| template_id         | BIGINT        | FK → InvoiceTemplate, nullable    |
| status              | VARCHAR(20)   | NOT NULL (enum InvoiceStatus)     |
| subtotal            | DECIMAL(12,2) | NOT NULL, default 0               |
| tax_amount          | DECIMAL(12,2) | NOT NULL, default 0               |
| discount_amount     | DECIMAL(12,2) | NOT NULL, default 0               |
| deposit_deduction   | DECIMAL(12,2) | NOT NULL, default 0               |
| total_amount        | DECIMAL(12,2) | NOT NULL, default 0               |
| issued_date         | DATE          | NOT NULL                          |
| due_date            | DATE          | nullable (not required for proforma)|
| validity_date       | DATE          | nullable (proforma expiry date)   |
| paid_date           | DATE          | nullable                          |
| source_warehouse_id | BIGINT        | FK → Warehouse, nullable          |
| issuer_name         | VARCHAR(200)  | nullable (snapshot: company or warehouse name) |
| issuer_ninea        | VARCHAR(50)   | nullable (snapshot: NINEA at time of invoice)  |
| issuer_rccm         | VARCHAR(50)   | nullable (snapshot: RCCM at time of invoice)   |
| issuer_tax_id       | VARCHAR(50)   | nullable (snapshot: Tax ID at time of invoice) |
| issuer_address      | TEXT          | nullable (snapshot: full address)  |
| notes               | TEXT          | nullable (printed on invoice)     |
| internal_notes      | TEXT          | nullable (not printed)            |
| created_at          | TIMESTAMP     | NOT NULL, default now             |
| updated_at          | TIMESTAMP     | NOT NULL, auto-updated            |

**Self-references**:
- `proforma_id`: if this standard invoice was converted from a proforma, points to the source proforma
- `deposit_id`: if a deposit was applied, points to the deposit invoice

**Issuer snapshot fields**: when an invoice is finalized (Sent), the issuing entity's name, NINEA, RCCM, Tax ID, and address are copied from the company settings or selected warehouse. This ensures the invoice PDF remains accurate even if company/warehouse info is later updated.

**Indexes**: `idx_invoice_customer` on (customer_id), `idx_invoice_type` on (invoice_type), `idx_invoice_status` on (status), `idx_invoice_due_date` on (due_date), `idx_invoice_order` on (order_id), `idx_invoice_proforma` on (proforma_id), `idx_invoice_template` on (template_id)

---

### 9. InvoiceItem

| Column           | Type          | Constraints                  |
|------------------|---------------|------------------------------|
| id               | BIGINT        | PK, auto-generated           |
| invoice_id       | BIGINT        | FK → Invoice, NOT NULL       |
| part_id          | BIGINT        | FK → Part, NOT NULL          |
| quantity         | INTEGER       | NOT NULL, > 0                |
| unit_price       | DECIMAL(12,2) | NOT NULL, >= 0               |
| discount_percent | DECIMAL(5,2)  | NOT NULL, default 0          |
| discount_amount  | DECIMAL(12,2) | NOT NULL, default 0          |
| total_price      | DECIMAL(12,2) | NOT NULL                     |
| created_at       | TIMESTAMP     | NOT NULL, default now        |

---

### 10. InvoiceTemplate

> Configurable PDF templates that control the visual design of generated invoices.

| Column              | Type         | Constraints                  |
|---------------------|--------------|------------------------------|
| id                  | BIGINT       | PK, auto-generated           |
| name                | VARCHAR(100) | NOT NULL (e.g., "Standard", "Proforma Blue") |
| description         | VARCHAR(500) | nullable                     |
| is_default          | BOOLEAN      | NOT NULL, default false      |
| primary_color       | VARCHAR(7)   | NOT NULL, default '#000000' (hex) |
| accent_color        | VARCHAR(7)   | NOT NULL, default '#4F46E5' (hex) |
| font_family         | VARCHAR(50)  | NOT NULL, default 'Helvetica'|
| header_layout       | VARCHAR(20)  | NOT NULL, default 'LOGO_LEFT' (LOGO_LEFT, LOGO_CENTER, BANNER) |
| logo_url            | VARCHAR(500) | nullable (overrides company logo) |
| header_image_url    | VARCHAR(500) | nullable (full-width banner at top) |
| footer_image_url    | VARCHAR(500) | nullable (full-width banner at bottom) |
| stamp_image_url     | VARCHAR(500) | nullable (stamp/seal image)  |
| signature_image_url | VARCHAR(500) | nullable (digital signature) |
| watermark_image_url | VARCHAR(500) | nullable (background watermark) |
| show_ninea          | BOOLEAN      | NOT NULL, default true       |
| show_rccm           | BOOLEAN      | NOT NULL, default true       |
| show_tax_id         | BOOLEAN      | NOT NULL, default true       |
| show_warehouse_address | BOOLEAN   | NOT NULL, default false      |
| show_customer_tax_id| BOOLEAN      | NOT NULL, default true       |
| show_payment_terms  | BOOLEAN      | NOT NULL, default true       |
| show_discount_column| BOOLEAN      | NOT NULL, default true       |
| default_notes       | TEXT         | nullable (overrides global default notes) |
| created_at          | TIMESTAMP    | NOT NULL, default now        |
| updated_at          | TIMESTAMP    | NOT NULL, auto-updated       |

**Constraint**: at most one template can have `is_default = true` (enforced at app level)

---

### 11. Payment

| Column         | Type          | Constraints                      |
|----------------|---------------|----------------------------------|
| id             | BIGINT        | PK, auto-generated               |
| invoice_id     | BIGINT        | FK → Invoice, NOT NULL           |
| amount         | DECIMAL(12,2) | NOT NULL, > 0                    |
| payment_method | VARCHAR(20)   | NOT NULL (enum PaymentMethod)    |
| payment_date   | DATE          | NOT NULL                         |
| reference      | VARCHAR(100)  | nullable                         |
| notes          | TEXT          | nullable                         |
| created_at     | TIMESTAMP     | NOT NULL, default now            |

**Indexes**: `idx_payment_invoice` on (invoice_id), `idx_payment_date` on (payment_date)

---

### 12. Return

| Column        | Type          | Constraints                      |
|---------------|---------------|----------------------------------|
| id            | BIGINT        | PK, auto-generated               |
| return_number | VARCHAR(30)   | NOT NULL, UNIQUE                 |
| invoice_id    | BIGINT        | FK → Invoice, nullable           |
| order_id      | BIGINT        | FK → ClientOrder, nullable       |
| customer_id   | BIGINT        | FK → Customer, NOT NULL          |
| status        | VARCHAR(20)   | NOT NULL (enum ReturnStatus)     |
| return_date   | DATE          | NOT NULL                         |
| notes         | TEXT          | nullable                         |
| created_at    | TIMESTAMP     | NOT NULL, default now            |
| updated_at    | TIMESTAMP     | NOT NULL, auto-updated           |

**Constraint**: at least one of invoice_id or order_id must be set (app-level check)

---

### 13. ReturnItem

| Column         | Type          | Constraints                      |
|----------------|---------------|----------------------------------|
| id             | BIGINT        | PK, auto-generated               |
| return_id      | BIGINT        | FK → Return, NOT NULL            |
| part_id        | BIGINT        | FK → Part, NOT NULL              |
| quantity       | INTEGER       | NOT NULL, > 0                    |
| reason         | VARCHAR(30)   | NOT NULL (enum ReturnReason)     |
| restock_action | VARCHAR(20)   | nullable (enum RestockAction)    |
| warehouse_id   | BIGINT        | FK → Warehouse, nullable         |
| created_at     | TIMESTAMP     | NOT NULL, default now            |

---

### 14. CreditNote

| Column             | Type          | Constraints                  |
|--------------------|---------------|------------------------------|
| id                 | BIGINT        | PK, auto-generated           |
| credit_note_number | VARCHAR(30)   | NOT NULL, UNIQUE             |
| return_id          | BIGINT        | FK → Return, NOT NULL, UNIQUE|
| total_amount       | DECIMAL(12,2) | NOT NULL                     |
| issued_date        | DATE          | NOT NULL                     |
| created_at         | TIMESTAMP     | NOT NULL, default now        |

---

### 15. Refund

| Column        | Type          | Constraints                      |
|---------------|---------------|----------------------------------|
| id            | BIGINT        | PK, auto-generated               |
| return_id     | BIGINT        | FK → Return, NOT NULL            |
| invoice_id    | BIGINT        | FK → Invoice, nullable           |
| amount        | DECIMAL(12,2) | NOT NULL, > 0                    |
| refund_method | VARCHAR(20)   | NOT NULL (enum RefundMethod)     |
| refund_date   | DATE          | NOT NULL                         |
| reference     | VARCHAR(100)  | nullable                         |
| notes         | TEXT          | nullable                         |
| created_at    | TIMESTAMP     | NOT NULL, default now            |

---

### 16. Warehouse

| Column         | Type         | Constraints              |
|----------------|--------------|--------------------------|
| id             | BIGINT       | PK, auto-generated       |
| name           | VARCHAR(200) | NOT NULL                 |
| code           | VARCHAR(20)  | NOT NULL, UNIQUE         |
| location       | VARCHAR(200) | nullable                 |
| street         | VARCHAR(300) | nullable                 |
| city           | VARCHAR(100) | nullable                 |
| state          | VARCHAR(100) | nullable                 |
| postal_code    | VARCHAR(20)  | nullable                 |
| country        | VARCHAR(100) | nullable                 |
| contact_person | VARCHAR(200) | nullable                 |
| phone          | VARCHAR(50)  | nullable                 |
| ninea          | VARCHAR(50)  | nullable (warehouse-level NINEA, overrides company) |
| rccm           | VARCHAR(50)  | nullable (warehouse-level RCCM, overrides company)  |
| tax_id         | VARCHAR(50)  | nullable (warehouse-level Tax ID, overrides company) |
| notes          | TEXT         | nullable                 |
| is_active      | BOOLEAN      | NOT NULL, default true   |
| created_at     | TIMESTAMP    | NOT NULL, default now    |
| updated_at     | TIMESTAMP    | NOT NULL, auto-updated   |

> When a warehouse is selected as the issuing entity on an invoice, its NINEA/RCCM/Tax ID are used. If a field is null, the company-level value from CompanySettings is used as fallback.

---

### 17. WarehouseStock

| Column        | Type    | Constraints                          |
|---------------|---------|--------------------------------------|
| id            | BIGINT  | PK, auto-generated                   |
| warehouse_id  | BIGINT  | FK → Warehouse, NOT NULL             |
| part_id       | BIGINT  | FK → Part, NOT NULL                  |
| quantity      | INTEGER | NOT NULL, default 0, >= 0            |
| min_stock_level | INTEGER | NOT NULL, default 0               |

**Unique constraint**: `uk_warehouse_part` on (warehouse_id, part_id)
**Indexes**: `idx_ws_part` on (part_id), `idx_ws_low_stock` on (quantity, min_stock_level)

---

### 18. StockTransfer

| Column                   | Type         | Constraints                            |
|--------------------------|--------------|----------------------------------------|
| id                       | BIGINT       | PK, auto-generated                     |
| transfer_number          | VARCHAR(30)  | NOT NULL, UNIQUE                       |
| source_warehouse_id      | BIGINT       | FK → Warehouse, NOT NULL               |
| destination_warehouse_id | BIGINT       | FK → Warehouse, NOT NULL               |
| status                   | VARCHAR(20)  | NOT NULL (enum StockTransferStatus)    |
| transfer_date            | DATE         | NOT NULL                               |
| notes                    | TEXT         | nullable                               |
| created_at               | TIMESTAMP    | NOT NULL, default now                  |
| updated_at               | TIMESTAMP    | NOT NULL, auto-updated                 |

**Constraint**: source_warehouse_id != destination_warehouse_id (app-level)

---

### 19. StockTransferItem

| Column            | Type    | Constraints                       |
|-------------------|---------|-----------------------------------|
| id                | BIGINT  | PK, auto-generated                |
| stock_transfer_id | BIGINT  | FK → StockTransfer, NOT NULL      |
| part_id           | BIGINT  | FK → Part, NOT NULL               |
| quantity          | INTEGER | NOT NULL, > 0                     |
| created_at        | TIMESTAMP | NOT NULL, default now           |

---

### 20. StockMovement

| Column          | Type          | Constraints                          |
|-----------------|---------------|--------------------------------------|
| id              | BIGINT        | PK, auto-generated                   |
| part_id         | BIGINT        | FK → Part, NOT NULL                  |
| warehouse_id    | BIGINT        | FK → Warehouse, NOT NULL             |
| type            | VARCHAR(30)   | NOT NULL (enum StockMovementType)    |
| quantity_change | INTEGER       | NOT NULL (positive or negative)      |
| balance_after   | INTEGER       | NOT NULL                             |
| reference_type  | VARCHAR(30)   | nullable (e.g., "INVOICE", "PO", "ORDER", "TRANSFER", "RETURN") |
| reference_id    | BIGINT        | nullable (ID of the related entity)  |
| notes           | VARCHAR(500)  | nullable                             |
| created_at      | TIMESTAMP     | NOT NULL, default now                |

**Indexes**: `idx_sm_part` on (part_id), `idx_sm_warehouse` on (warehouse_id), `idx_sm_reference` on (reference_type, reference_id), `idx_sm_created` on (created_at)

> This is an append-only audit table. Rows are never updated or deleted.

---

### 21. Cart (Client Portal)

| Column      | Type      | Constraints                        |
|-------------|-----------|------------------------------------|
| id          | BIGINT    | PK, auto-generated                 |
| customer_id | BIGINT    | FK → Customer, NOT NULL, UNIQUE    |
| created_at  | TIMESTAMP | NOT NULL, default now              |
| updated_at  | TIMESTAMP | NOT NULL, auto-updated             |

---

### 22. CartItem

| Column     | Type      | Constraints                  |
|------------|-----------|------------------------------|
| id         | BIGINT    | PK, auto-generated           |
| cart_id    | BIGINT    | FK → Cart, NOT NULL          |
| part_id    | BIGINT    | FK → Part, NOT NULL          |
| quantity   | INTEGER   | NOT NULL, > 0                |
| created_at | TIMESTAMP | NOT NULL, default now        |
| updated_at | TIMESTAMP | NOT NULL, auto-updated       |

**Unique constraint**: `uk_cart_part` on (cart_id, part_id)

---

### 23. ClientOrder

> Named `ClientOrder` (mapped to table `client_order`) to avoid SQL reserved keyword `ORDER`.

| Column           | Type          | Constraints                       |
|------------------|---------------|-----------------------------------|
| id               | BIGINT        | PK, auto-generated                |
| order_number     | VARCHAR(30)   | NOT NULL, UNIQUE                  |
| customer_id      | BIGINT        | FK → Customer, NOT NULL           |
| status           | VARCHAR(20)   | NOT NULL (enum OrderStatus)       |
| subtotal         | DECIMAL(12,2) | NOT NULL, default 0               |
| tax_amount       | DECIMAL(12,2) | NOT NULL, default 0               |
| discount_amount  | DECIMAL(12,2) | NOT NULL, default 0               |
| shipping_amount  | DECIMAL(12,2) | NOT NULL, default 0               |
| total_amount     | DECIMAL(12,2) | NOT NULL, default 0               |
| shipping_street  | VARCHAR(300)  | nullable                          |
| shipping_city    | VARCHAR(100)  | nullable                          |
| shipping_state   | VARCHAR(100)  | nullable                          |
| shipping_postal  | VARCHAR(20)   | nullable                          |
| shipping_country | VARCHAR(100)  | nullable                          |
| notes            | TEXT          | nullable (customer notes)         |
| tracking_number  | VARCHAR(100)  | nullable                          |
| warehouse_id     | BIGINT        | FK → Warehouse, nullable          |
| order_date       | TIMESTAMP     | NOT NULL                          |
| created_at       | TIMESTAMP     | NOT NULL, default now             |
| updated_at       | TIMESTAMP     | NOT NULL, auto-updated            |

**Indexes**: `idx_order_customer` on (customer_id), `idx_order_status` on (status), `idx_order_date` on (order_date)

---

### 24. OrderItem

| Column      | Type          | Constraints                      |
|-------------|---------------|----------------------------------|
| id          | BIGINT        | PK, auto-generated               |
| order_id    | BIGINT        | FK → ClientOrder, NOT NULL       |
| part_id     | BIGINT        | FK → Part, NOT NULL              |
| quantity    | INTEGER       | NOT NULL, > 0                    |
| unit_price  | DECIMAL(12,2) | NOT NULL, >= 0                   |
| total_price | DECIMAL(12,2) | NOT NULL                         |
| created_at  | TIMESTAMP     | NOT NULL, default now            |

---

### 25. User (Back-Office)

| Column        | Type         | Constraints                    |
|---------------|--------------|--------------------------------|
| id            | BIGINT       | PK, auto-generated             |
| name          | VARCHAR(200) | NOT NULL                       |
| email         | VARCHAR(200) | NOT NULL, UNIQUE               |
| password_hash | VARCHAR(255) | NOT NULL                       |
| role          | VARCHAR(30)  | NOT NULL (enum UserRole)       |
| is_active     | BOOLEAN      | NOT NULL, default true         |
| created_at    | TIMESTAMP    | NOT NULL, default now          |
| updated_at    | TIMESTAMP    | NOT NULL, auto-updated         |

**Role behavior**:
- `ADMIN` — full access to all warehouses, all features, settings, and user management. No warehouse assignments needed.
- `WAREHOUSE_OPERATOR` — access scoped to assigned warehouses only. Permissions are granted per warehouse via the `UserWarehouse` table.

---

### 26. UserWarehouse (Junction — User ↔ Warehouse)

> Assigns a user to one or more warehouses with specific permissions.
> Only used for users with role `WAREHOUSE_OPERATOR`. ADMINs have implicit access to everything.

| Column       | Type         | Constraints                           |
|--------------|--------------|---------------------------------------|
| id           | BIGINT       | PK, auto-generated                    |
| user_id      | BIGINT       | FK → User, NOT NULL                   |
| warehouse_id | BIGINT       | FK → Warehouse, NOT NULL              |
| created_at   | TIMESTAMP    | NOT NULL, default now                 |

**Unique constraint**: `uk_user_warehouse` on (user_id, warehouse_id)
**Indexes**: `idx_uw_user` on (user_id), `idx_uw_warehouse` on (warehouse_id)

---

### 27. UserWarehousePermission

> Granular permissions for a user within a specific warehouse.

| Column              | Type        | Constraints                               |
|---------------------|-------------|-------------------------------------------|
| id                  | BIGINT      | PK, auto-generated                        |
| user_warehouse_id   | BIGINT      | FK → UserWarehouse, NOT NULL              |
| permission          | VARCHAR(30) | NOT NULL (enum WarehousePermission)       |

**Unique constraint**: `uk_uwp` on (user_warehouse_id, permission)

**Example**: User "Alice" assigned to Warehouse "WH-01" with permissions [STOCK_MANAGE, ORDER_MANAGE] and Warehouse "WH-02" with permissions [STOCK_MANAGE, INVOICE_MANAGE, PROCUREMENT_MANAGE].

**Access control logic (application layer)**:
```
if user.role == ADMIN:
    → allow everything

if user.role == WAREHOUSE_OPERATOR:
    → check UserWarehouse for (user_id, target_warehouse_id)
    → if found, check UserWarehousePermission for the required permission
    → for non-warehouse-scoped features (e.g., parts catalog, customers, reports):
      allow read access, but write operations on stock/orders are scoped to assigned warehouses
```

---

### 28. CompanySettings

> Single-row table holding all application settings. Always has exactly one row (ID = 1).

| Column                   | Type          | Constraints              |
|--------------------------|---------------|--------------------------|
| id                       | BIGINT        | PK, default 1            |
| company_name             | VARCHAR(200)  | nullable                 |
| logo_url                 | VARCHAR(500)  | nullable                 |
| street                   | VARCHAR(300)  | nullable                 |
| city                     | VARCHAR(100)  | nullable                 |
| state                    | VARCHAR(100)  | nullable                 |
| postal_code              | VARCHAR(20)   | nullable                 |
| country                  | VARCHAR(100)  | nullable                 |
| tax_id                   | VARCHAR(50)   | nullable                 |
| ninea                    | VARCHAR(50)   | nullable (company-level NINEA) |
| rccm                     | VARCHAR(50)   | nullable (company-level RCCM)  |
| phone                    | VARCHAR(50)   | nullable                 |
| email                    | VARCHAR(200)  | nullable                 |
| default_tax_rate         | DECIMAL(5,2)  | NOT NULL, default 0      |
| proforma_prefix          | VARCHAR(10)   | NOT NULL, default 'PRO'  |
| invoice_prefix           | VARCHAR(10)   | NOT NULL, default 'INV'  |
| deposit_prefix           | VARCHAR(10)   | NOT NULL, default 'DEP'  |
| credit_note_prefix       | VARCHAR(10)   | NOT NULL, default 'CN'   |
| order_prefix             | VARCHAR(10)   | NOT NULL, default 'ORD'  |
| po_prefix                | VARCHAR(10)   | NOT NULL, default 'PO'   |
| transfer_prefix          | VARCHAR(10)   | NOT NULL, default 'TRF'  |
| return_prefix            | VARCHAR(10)   | NOT NULL, default 'RET'  |
| default_payment_terms    | INTEGER       | NOT NULL, default 30     |
| default_proforma_validity| INTEGER       | NOT NULL, default 30     |
| default_invoice_notes    | TEXT          | nullable                 |
| default_template_id      | BIGINT        | FK → InvoiceTemplate, nullable |
| sequential_reset_yearly  | BOOLEAN       | NOT NULL, default true   |
| currency_symbol          | VARCHAR(10)   | NOT NULL, default '$'    |
| currency_position        | VARCHAR(10)   | NOT NULL, default 'BEFORE' |
| currency_decimals        | INTEGER       | NOT NULL, default 2      |
| default_warehouse_id     | BIGINT        | FK → Warehouse, nullable |
| portal_warehouse_id      | BIGINT        | FK → Warehouse, nullable |
| portal_enabled           | BOOLEAN       | NOT NULL, default false  |
| portal_min_order_amount  | DECIMAL(12,2) | nullable                 |
| portal_shipping_flat_rate| DECIMAL(12,2) | nullable                 |
| portal_free_shipping_above| DECIMAL(12,2)| nullable                 |
| portal_terms_text        | TEXT          | nullable                 |
| updated_at               | TIMESTAMP     | NOT NULL, auto-updated   |

---

### 29. TaxRate

| Column      | Type         | Constraints              |
|-------------|--------------|--------------------------|
| id          | BIGINT       | PK, auto-generated       |
| label       | VARCHAR(50)  | NOT NULL (e.g., "VAT 20%") |
| rate        | DECIMAL(5,2) | NOT NULL                 |
| is_default  | BOOLEAN      | NOT NULL, default false  |
| created_at  | TIMESTAMP    | NOT NULL, default now    |
| updated_at  | TIMESTAMP    | NOT NULL, auto-updated   |

---

### 30. AuditLog

| Column      | Type         | Constraints              |
|-------------|--------------|--------------------------|
| id          | BIGINT       | PK, auto-generated       |
| user_id     | BIGINT       | FK → User, nullable      |
| customer_id | BIGINT       | FK → Customer, nullable  |
| action      | VARCHAR(20)  | NOT NULL (CREATE, UPDATE, DELETE) |
| entity_type | VARCHAR(50)  | NOT NULL (e.g., "Part", "Invoice") |
| entity_id   | BIGINT       | NOT NULL                 |
| changes     | JSONB        | nullable (before/after)  |
| ip_address  | VARCHAR(45)  | nullable                 |
| created_at  | TIMESTAMP    | NOT NULL, default now    |

**Indexes**: `idx_audit_entity` on (entity_type, entity_id), `idx_audit_user` on (user_id), `idx_audit_created` on (created_at)

> Append-only table. Rows are never updated or deleted.

---

### 31. SequenceCounter

> Tracks sequential numbers for auto-generated document numbers (invoices, POs, orders, etc.).

| Column       | Type         | Constraints                   |
|--------------|--------------|-------------------------------|
| id           | BIGINT       | PK, auto-generated            |
| entity_type  | VARCHAR(30)  | NOT NULL (e.g., "INVOICE", "PO", "ORDER") |
| year         | INTEGER      | NOT NULL                      |
| last_value   | BIGINT       | NOT NULL, default 0           |

**Unique constraint**: `uk_sequence` on (entity_type, year)

> Used to generate sequential numbers like INV-2026-00001. The `last_value` is atomically incremented when creating a new document.

---

## Summary

| Entity             | Table Name           | Key Relationships                                |
|--------------------|----------------------|--------------------------------------------------|
| Category           | `category`           | → Part (1:N)                                     |
| Part               | `part`               | → Category (N:1), → PartImage (1:N)             |
| PartImage          | `part_image`         | → Part (N:1)                                     |
| Supplier           | `supplier`           | → PurchaseOrder (1:N)                            |
| PurchaseOrder      | `purchase_order`     | → Supplier (N:1), → PurchaseOrderItem (1:N), → Warehouse (N:1) |
| PurchaseOrderItem  | `purchase_order_item`| → PurchaseOrder (N:1), → Part (N:1)             |
| Customer           | `customer`           | → Invoice (1:N), → ClientOrder (1:N), → Cart (1:1), → Return (1:N) |
| Invoice            | `invoice`            | → Customer (N:1), → InvoiceItem (1:N), → Payment (1:N), → ClientOrder (N:1), → InvoiceTemplate (N:1), → Invoice self (proforma/deposit) |
| InvoiceItem        | `invoice_item`       | → Invoice (N:1), → Part (N:1)                   |
| InvoiceTemplate    | `invoice_template`   | → Invoice (1:N)                                  |
| Payment            | `payment`            | → Invoice (N:1)                                  |
| Return             | `return`             | → Invoice (N:1), → ClientOrder (N:1), → Customer (N:1), → ReturnItem (1:N), → CreditNote (1:1), → Refund (1:N) |
| ReturnItem         | `return_item`        | → Return (N:1), → Part (N:1), → Warehouse (N:1) |
| CreditNote         | `credit_note`        | → Return (1:1)                                   |
| Refund             | `refund`             | → Return (N:1), → Invoice (N:1)                 |
| Warehouse          | `warehouse`          | → WarehouseStock (1:N), → StockTransfer (1:N)   |
| WarehouseStock     | `warehouse_stock`    | → Warehouse (N:1), → Part (N:1)                 |
| StockTransfer      | `stock_transfer`     | → Warehouse src (N:1), → Warehouse dest (N:1), → StockTransferItem (1:N) |
| StockTransferItem  | `stock_transfer_item`| → StockTransfer (N:1), → Part (N:1)             |
| StockMovement      | `stock_movement`     | → Part (N:1), → Warehouse (N:1)                 |
| Cart               | `cart`               | → Customer (1:1), → CartItem (1:N)              |
| CartItem           | `cart_item`          | → Cart (N:1), → Part (N:1)                      |
| ClientOrder        | `client_order`       | → Customer (N:1), → OrderItem (1:N), → Invoice (1:0..1) |
| OrderItem          | `order_item`         | → ClientOrder (N:1), → Part (N:1)               |
| User               | `user`                       | → UserWarehouse (1:N)                            |
| UserWarehouse      | `user_warehouse`             | → User (N:1), → Warehouse (N:1), → UserWarehousePermission (1:N) |
| UserWarehousePermission | `user_warehouse_permission` | → UserWarehouse (N:1)                       |
| CompanySettings    | `company_settings`           | → Warehouse default (N:1), → Warehouse portal (N:1), → InvoiceTemplate default (N:1) |
| TaxRate            | `tax_rate`                   | (standalone)                                     |
| AuditLog           | `audit_log`                  | → User (N:1), → Customer (N:1)                  |
| SequenceCounter    | `sequence_counter`           | (standalone utility)                             |

**Total: 31 tables**
