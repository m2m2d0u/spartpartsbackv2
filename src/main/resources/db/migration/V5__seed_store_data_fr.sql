-- Assign ninea/rccm/tax_id from company settings to the default store
UPDATE store
SET ninea = (SELECT ninea FROM company_settings LIMIT 1),
    rccm  = (SELECT rccm FROM company_settings LIMIT 1),
    tax_id = (SELECT tax_id FROM company_settings LIMIT 1)
WHERE id = 'b1000000-0000-0000-0000-000000000001';
