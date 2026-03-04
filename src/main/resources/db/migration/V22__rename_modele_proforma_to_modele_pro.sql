-- Rename template from "Modèle Proforma" to "Modèle Pro"
UPDATE invoice_template
SET name = 'Modèle Pro'
WHERE name = 'Modèle Proforma';
