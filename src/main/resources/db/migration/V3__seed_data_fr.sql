-- V3__seed_data_fr.sql
-- Données de démonstration en français pour le système de gestion de pièces détachées
-- Contexte: entreprise sénégalaise de pièces détachées automobiles

-- =============================================
-- Catégories de pièces
-- =============================================
INSERT INTO category (id, name, description) VALUES
    ('a0000000-0000-0000-0000-000000000001', 'Moteur', 'Pièces liées au moteur et à la motorisation'),
    ('a0000000-0000-0000-0000-000000000002', 'Freinage', 'Disques, plaquettes, étriers et flexibles de frein'),
    ('a0000000-0000-0000-0000-000000000003', 'Suspension', 'Amortisseurs, ressorts, rotules et bras de suspension'),
    ('a0000000-0000-0000-0000-000000000004', 'Transmission', 'Embrayages, boîtes de vitesses, cardans et joints homocinétiques'),
    ('a0000000-0000-0000-0000-000000000005', 'Électricité', 'Batteries, alternateurs, démarreurs et câblage'),
    ('a0000000-0000-0000-0000-000000000006', 'Carrosserie', 'Pare-chocs, capots, ailes et rétroviseurs'),
    ('a0000000-0000-0000-0000-000000000007', 'Climatisation', 'Compresseurs, condenseurs, évaporateurs et filtres de climatisation'),
    ('a0000000-0000-0000-0000-000000000008', 'Filtration', 'Filtres à huile, à air, à carburant et d''habitacle'),
    ('a0000000-0000-0000-0000-000000000009', 'Échappement', 'Pots d''échappement, catalyseurs et silencieux'),
    ('a0000000-0000-0000-0000-00000000000a', 'Direction', 'Crémaillères, pompes de direction et biellettes'),
    ('a0000000-0000-0000-0000-00000000000b', 'Éclairage', 'Phares, feux arrière, clignotants et ampoules'),
    ('a0000000-0000-0000-0000-00000000000c', 'Refroidissement', 'Radiateurs, pompes à eau, thermostats et ventilateurs');

-- =============================================
-- Entrepôts
-- =============================================
INSERT INTO warehouse (id, name, code, location, street, city, state, postal_code, country, contact_person, phone, is_active) VALUES
    ('b0000000-0000-0000-0000-000000000001', 'Entrepôt Principal Dakar', 'DKR-01', 'Zone Industrielle de Dakar', 'Rue des Usines, Lot 45', 'Dakar', 'Dakar', '11000', 'Sénégal', 'Abdoulaye Diop', '+221 33 820 10 10', TRUE),
    ('b0000000-0000-0000-0000-000000000002', 'Dépôt Thiès', 'THS-01', 'Quartier Industriel Thiès', 'Boulevard Général de Gaulle', 'Thiès', 'Thiès', '21000', 'Sénégal', 'Moussa Ndiaye', '+221 33 951 20 20', TRUE),
    ('b0000000-0000-0000-0000-000000000003', 'Magasin Médina', 'DKR-02', 'Marché Sandaga', 'Avenue Blaise Diagne, N°12', 'Dakar', 'Dakar', '11000', 'Sénégal', 'Ibrahima Fall', '+221 33 822 30 30', TRUE),
    ('b0000000-0000-0000-0000-000000000004', 'Dépôt Saint-Louis', 'STL-01', 'Zone Artisanale Saint-Louis', 'Rue Abdoul Karim Bourgi', 'Saint-Louis', 'Saint-Louis', '32000', 'Sénégal', 'Ousmane Sy', '+221 33 961 40 40', TRUE);

-- =============================================
-- Fournisseurs
-- =============================================
INSERT INTO supplier (id, name, contact_person, email, phone, street, city, state, postal_code, country, notes) VALUES
    ('c0000000-0000-0000-0000-000000000001', 'Valeo Afrique', 'Jean-Pierre Kouamé', 'jp.kouame@valeo-afrique.com', '+225 27 22 44 55 66', 'Zone Industrielle Vridi', 'Abidjan', 'Lagunes', '01 BP 1234', 'Côte d''Ivoire', 'Fournisseur principal pour pièces de freinage et embrayage'),
    ('c0000000-0000-0000-0000-000000000002', 'Bosch Distribution Sénégal', 'Aminata Sow', 'a.sow@bosch-senegal.com', '+221 33 869 11 22', 'Route de Rufisque, Km 8', 'Dakar', 'Dakar', '11000', 'Sénégal', 'Distributeur officiel Bosch pour l''Afrique de l''Ouest'),
    ('c0000000-0000-0000-0000-000000000003', 'Pièces Auto Casablanca', 'Youssef El Amrani', 'y.elamrani@pac-maroc.ma', '+212 522 30 40 50', 'Zone Franche de Tanger', 'Casablanca', 'Grand Casablanca', '20000', 'Maroc', 'Spécialiste pièces moteur et filtration'),
    ('c0000000-0000-0000-0000-000000000004', 'TotalEnergies Lubrifiants', 'Fatou Bâ', 'f.ba@totalenergies.sn', '+221 33 839 50 50', 'Avenue Cheikh Anta Diop', 'Dakar', 'Dakar', '11000', 'Sénégal', 'Huiles, lubrifiants et produits d''entretien'),
    ('c0000000-0000-0000-0000-000000000005', 'Guangzhou Auto Parts Co.', 'Wei Zhang', 'wei.zhang@gzautoparts.cn', '+86 20 8888 6666', '138 Huanshi Dong Lu', 'Guangzhou', 'Guangdong', '510000', 'Chine', 'Pièces génériques à bas prix — carrosserie et éclairage');

-- =============================================
-- Clients
-- =============================================
INSERT INTO customer (id, name, company, email, phone, street, city, state, postal_code, country, tax_id, notes, portal_access) VALUES
    ('d0000000-0000-0000-0000-000000000001', 'Mamadou Diallo', 'Garage Diallo & Fils', 'mamadou.diallo@gardiallo.sn', '+221 77 123 45 67', 'Rue Moussé Diop, N°8', 'Dakar', 'Dakar', '11000', 'Sénégal', 'SN-2024-00456', 'Client fidèle depuis 2018 — gros volume mensuel', TRUE),
    ('d0000000-0000-0000-0000-000000000002', 'Aïssatou Ndiaye', 'Mécanique Générale Ndiaye', 'aissatou@mecagen.sn', '+221 78 234 56 78', 'Avenue Lamine Guèye, N°22', 'Thiès', 'Thiès', '21000', 'Sénégal', 'SN-2024-00789', 'Spécialiste véhicules utilitaires', FALSE),
    ('d0000000-0000-0000-0000-000000000003', 'Cheikh Bamba Seck', 'Transports Seck SARL', 'c.seck@transport-seck.sn', '+221 76 345 67 89', 'Route de Kaolack, Km 5', 'Kaolack', 'Kaolack', '41000', 'Sénégal', 'SN-2024-01122', 'Flotte de 30 camions — commandes régulières', TRUE),
    ('d0000000-0000-0000-0000-000000000004', 'Oumar Touré', 'Auto Service Touré', 'oumar@autoservice-toure.sn', '+221 77 456 78 90', 'Boulevard du Centenaire, N°3', 'Saint-Louis', 'Saint-Louis', '32000', 'Sénégal', NULL, 'Petit garage indépendant', FALSE),
    ('d0000000-0000-0000-0000-000000000005', 'Fatimata Bâ', 'Location Bâ Auto', 'fatimata@ba-auto.sn', '+221 70 567 89 01', 'Rue Félix Faure, N°15', 'Dakar', 'Dakar', '11000', 'Sénégal', 'SN-2024-01555', 'Société de location — entretien préventif fréquent', TRUE),
    ('d0000000-0000-0000-0000-000000000006', 'Ibrahima Camara', 'Garage Étoile du Sud', 'ibrahima@etoiledusud.sn', '+221 78 678 90 12', 'Avenue Bourguiba, N°40', 'Ziguinchor', 'Ziguinchor', '26000', 'Sénégal', NULL, 'Garage multimarque — Casamance', FALSE),
    ('d0000000-0000-0000-0000-000000000007', 'Awa Diop', 'Rapid''Auto Dakar', 'awa@rapidauto.sn', '+221 77 789 01 23', 'Rond-Point Jet d''Eau', 'Dakar', 'Dakar', '11000', 'Sénégal', 'SN-2024-01890', 'Service rapide — vidange, freins, pneus', TRUE),
    ('d0000000-0000-0000-0000-000000000008', 'Abdoulaye Kane', 'Kane Diesel Mécanique', 'a.kane@kanediesel.sn', '+221 76 890 12 34', 'Zone Industrielle Mbao', 'Dakar', 'Dakar', '11000', 'Sénégal', 'SN-2024-02100', 'Spécialiste moteurs diesel et poids lourds', FALSE);

-- =============================================
-- Utilisateurs
-- =============================================
INSERT INTO "user" (id, name, email, password_hash, role, is_active) VALUES
    ('e0000000-0000-0000-0000-000000000001', 'Abdoulaye Diop', 'a.diop@symmetry.sn', '$2a$10$xJwL5vRfhNkMW0oXzVq2D.9X2YzL7K3cA0VQfZB5nR8gG7hW1dSFO', 'ADMIN', TRUE),
    ('e0000000-0000-0000-0000-000000000002', 'Mariama Sarr', 'm.sarr@symmetry.sn', '$2a$10$xJwL5vRfhNkMW0oXzVq2D.9X2YzL7K3cA0VQfZB5nR8gG7hW1dSFO', 'ADMIN', TRUE),
    ('e0000000-0000-0000-0000-000000000003', 'Moussa Ndiaye', 'm.ndiaye@symmetry.sn', '$2a$10$xJwL5vRfhNkMW0oXzVq2D.9X2YzL7K3cA0VQfZB5nR8gG7hW1dSFO', 'WAREHOUSE_OPERATOR', TRUE),
    ('e0000000-0000-0000-0000-000000000004', 'Ibrahima Fall', 'i.fall@symmetry.sn', '$2a$10$xJwL5vRfhNkMW0oXzVq2D.9X2YzL7K3cA0VQfZB5nR8gG7hW1dSFO', 'WAREHOUSE_OPERATOR', TRUE),
    ('e0000000-0000-0000-0000-000000000005', 'Ousmane Sy', 'o.sy@symmetry.sn', '$2a$10$xJwL5vRfhNkMW0oXzVq2D.9X2YzL7K3cA0VQfZB5nR8gG7hW1dSFO', 'WAREHOUSE_OPERATOR', TRUE);

-- =============================================
-- Taux de taxe
-- =============================================
INSERT INTO tax_rate (id, label, rate, is_default) VALUES
    ('f0000000-0000-0000-0000-000000000001', 'TVA 18%', 18.00, TRUE),
    ('f0000000-0000-0000-0000-000000000002', 'TVA 10%', 10.00, FALSE),
    ('f0000000-0000-0000-0000-000000000003', 'Exonéré', 0.00, FALSE);

-- =============================================
-- Modèle de facture
-- =============================================
INSERT INTO invoice_template (id, name, description, is_default, primary_color, accent_color, font_family, header_layout) VALUES
    ('f1000000-0000-0000-0000-000000000001', 'Modèle Standard', 'Modèle de facture par défaut avec le logo de l''entreprise', TRUE, '#1A1A2E', '#E94560', 'Helvetica', 'LOGO_LEFT'),
    ('f1000000-0000-0000-0000-000000000002', 'Modèle Proforma', 'Modèle épuré pour les devis et factures proforma', FALSE, '#16213E', '#0F3460', 'Arial', 'LOGO_LEFT');

-- =============================================
-- Paramètres de l'entreprise (mise à jour de la ligne existante)
-- =============================================
UPDATE company_settings SET
    company_name = 'Symmetry Pièces Auto',
    street = 'Rue des Usines, Lot 45, Zone Industrielle',
    city = 'Dakar',
    state = 'Dakar',
    postal_code = '11000',
    country = 'Sénégal',
    tax_id = 'SN-TVA-2023-98765',
    ninea = '005678901-2G3',
    rccm = 'SN-DKR-2023-B-12345',
    phone = '+221 33 820 10 10',
    email = 'contact@symmetry-pieces.sn',
    default_tax_rate = 18.00,
    proforma_prefix = 'PRO',
    invoice_prefix = 'FAC',
    deposit_prefix = 'ACO',
    credit_note_prefix = 'AV',
    order_prefix = 'CMD',
    po_prefix = 'BCF',
    transfer_prefix = 'TRF',
    return_prefix = 'RET',
    default_payment_terms = 30,
    default_proforma_validity = 15,
    default_invoice_notes = 'Merci pour votre confiance. Paiement à effectuer sous 30 jours.',
    default_template_id = 'f1000000-0000-0000-0000-000000000001',
    sequential_reset_yearly = TRUE,
    currency_symbol = 'FCFA',
    currency_position = 'AFTER',
    currency_decimals = 0,
    default_warehouse_id = 'b0000000-0000-0000-0000-000000000001',
    portal_warehouse_id = 'b0000000-0000-0000-0000-000000000001',
    portal_enabled = TRUE,
    portal_min_order_amount = 25000,
    portal_shipping_flat_rate = 5000,
    portal_free_shipping_above = 200000,
    portal_terms_text = 'Conditions générales de vente : Les pièces vendues sont garanties contre tout défaut de fabrication pendant 6 mois à compter de la date d''achat. Les retours sont acceptés dans un délai de 15 jours, sous réserve que les pièces soient dans leur état d''origine. Aucun retour n''est accepté pour les pièces électriques et les pièces sur commande.',
    updated_at = NOW();

-- =============================================
-- Affectation utilisateurs aux entrepôts
-- =============================================
INSERT INTO user_warehouse (id, user_id, warehouse_id) VALUES
    ('f2000000-0000-0000-0000-000000000001', 'e0000000-0000-0000-0000-000000000001', 'b0000000-0000-0000-0000-000000000001'),
    ('f2000000-0000-0000-0000-000000000002', 'e0000000-0000-0000-0000-000000000002', 'b0000000-0000-0000-0000-000000000001'),
    ('f2000000-0000-0000-0000-000000000003', 'e0000000-0000-0000-0000-000000000003', 'b0000000-0000-0000-0000-000000000002'),
    ('f2000000-0000-0000-0000-000000000004', 'e0000000-0000-0000-0000-000000000004', 'b0000000-0000-0000-0000-000000000003'),
    ('f2000000-0000-0000-0000-000000000005', 'e0000000-0000-0000-0000-000000000005', 'b0000000-0000-0000-0000-000000000004');

-- Permissions (admins ont tout, opérateurs ont stock + commandes)
INSERT INTO user_warehouse_permission (id, user_warehouse_id, permission) VALUES
    -- Abdoulaye Diop (admin) — toutes les permissions
    ('f3000000-0000-0000-0000-000000000001', 'f2000000-0000-0000-0000-000000000001', 'STOCK_MANAGE'),
    ('f3000000-0000-0000-0000-000000000002', 'f2000000-0000-0000-0000-000000000001', 'ORDER_MANAGE'),
    ('f3000000-0000-0000-0000-000000000003', 'f2000000-0000-0000-0000-000000000001', 'INVOICE_MANAGE'),
    ('f3000000-0000-0000-0000-000000000004', 'f2000000-0000-0000-0000-000000000001', 'PROCUREMENT_MANAGE'),
    ('f3000000-0000-0000-0000-000000000005', 'f2000000-0000-0000-0000-000000000001', 'TRANSFER_MANAGE'),
    ('f3000000-0000-0000-0000-000000000006', 'f2000000-0000-0000-0000-000000000001', 'RETURN_MANAGE'),
    -- Mariama Sarr (admin) — toutes les permissions
    ('f3000000-0000-0000-0000-000000000007', 'f2000000-0000-0000-0000-000000000002', 'STOCK_MANAGE'),
    ('f3000000-0000-0000-0000-000000000008', 'f2000000-0000-0000-0000-000000000002', 'ORDER_MANAGE'),
    ('f3000000-0000-0000-0000-000000000009', 'f2000000-0000-0000-0000-000000000002', 'INVOICE_MANAGE'),
    ('f3000000-0000-0000-0000-00000000000a', 'f2000000-0000-0000-0000-000000000002', 'PROCUREMENT_MANAGE'),
    ('f3000000-0000-0000-0000-00000000000b', 'f2000000-0000-0000-0000-000000000002', 'TRANSFER_MANAGE'),
    ('f3000000-0000-0000-0000-00000000000c', 'f2000000-0000-0000-0000-000000000002', 'RETURN_MANAGE'),
    -- Moussa Ndiaye (opérateur Thiès)
    ('f3000000-0000-0000-0000-00000000000d', 'f2000000-0000-0000-0000-000000000003', 'STOCK_MANAGE'),
    ('f3000000-0000-0000-0000-00000000000e', 'f2000000-0000-0000-0000-000000000003', 'ORDER_MANAGE'),
    ('f3000000-0000-0000-0000-00000000000f', 'f2000000-0000-0000-0000-000000000003', 'TRANSFER_MANAGE'),
    -- Ibrahima Fall (opérateur Médina)
    ('f3000000-0000-0000-0000-000000000010', 'f2000000-0000-0000-0000-000000000004', 'STOCK_MANAGE'),
    ('f3000000-0000-0000-0000-000000000011', 'f2000000-0000-0000-0000-000000000004', 'ORDER_MANAGE'),
    -- Ousmane Sy (opérateur Saint-Louis)
    ('f3000000-0000-0000-0000-000000000012', 'f2000000-0000-0000-0000-000000000005', 'STOCK_MANAGE'),
    ('f3000000-0000-0000-0000-000000000013', 'f2000000-0000-0000-0000-000000000005', 'ORDER_MANAGE'),
    ('f3000000-0000-0000-0000-000000000014', 'f2000000-0000-0000-0000-000000000005', 'TRANSFER_MANAGE');

-- =============================================
-- Pièces détachées (~60 pièces)
-- =============================================

-- Moteur
INSERT INTO part (id, part_number, name, description, short_description, category_id, selling_price, purchase_price, min_stock_level, published) VALUES
    ('10000000-0000-0000-0000-000000000001', 'MOT-001', 'Filtre à huile universel', 'Filtre à huile compatible avec la plupart des véhicules légers. Qualité OEM.', 'Filtre à huile standard', 'a0000000-0000-0000-0000-000000000001', 4500, 2200, 20, TRUE),
    ('10000000-0000-0000-0000-000000000002', 'MOT-002', 'Courroie de distribution Peugeot 307', 'Kit courroie de distribution avec galet tendeur pour Peugeot 307 1.6 HDi', 'Kit distribution Peugeot 307', 'a0000000-0000-0000-0000-000000000001', 45000, 25000, 5, TRUE),
    ('10000000-0000-0000-0000-000000000003', 'MOT-003', 'Pompe à eau Toyota Hilux', 'Pompe à eau pour Toyota Hilux 2.5 D-4D. Référence OE 16100-39465', 'Pompe à eau Hilux', 'a0000000-0000-0000-0000-000000000001', 35000, 18000, 3, TRUE),
    ('10000000-0000-0000-0000-000000000004', 'MOT-004', 'Bougie d''allumage NGK BKR6E', 'Bougie d''allumage NGK standard, résistance intégrée', 'Bougie NGK BKR6E', 'a0000000-0000-0000-0000-000000000001', 3500, 1500, 50, TRUE),
    ('10000000-0000-0000-0000-000000000005', 'MOT-005', 'Joint de culasse Renault Clio', 'Joint de culasse pour Renault Clio III 1.5 dCi 68ch', 'Joint culasse Clio III', 'a0000000-0000-0000-0000-000000000001', 28000, 14000, 3, TRUE);

-- Freinage
INSERT INTO part (id, part_number, name, description, short_description, category_id, selling_price, purchase_price, min_stock_level, published) VALUES
    ('10000000-0000-0000-0000-000000000006', 'FRE-001', 'Plaquettes de frein avant Bosch', 'Jeu de plaquettes de frein avant Bosch. Compatible multi-véhicules.', 'Plaquettes avant Bosch', 'a0000000-0000-0000-0000-000000000002', 18000, 9000, 15, TRUE),
    ('10000000-0000-0000-0000-000000000007', 'FRE-002', 'Disque de frein ventilé 280mm', 'Disque de frein ventilé avant diamètre 280mm. Qualité premium.', 'Disque ventilé 280mm', 'a0000000-0000-0000-0000-000000000002', 22000, 11000, 10, TRUE),
    ('10000000-0000-0000-0000-000000000008', 'FRE-003', 'Étrier de frein avant gauche', 'Étrier de frein avant gauche reconditionné. Véhicules légers.', 'Étrier avant gauche', 'a0000000-0000-0000-0000-000000000002', 55000, 30000, 4, TRUE),
    ('10000000-0000-0000-0000-000000000009', 'FRE-004', 'Flexible de frein avant', 'Flexible de frein avant caoutchouc renforcé. Longueur 450mm.', 'Flexible frein avant 450mm', 'a0000000-0000-0000-0000-000000000002', 8500, 4000, 10, TRUE),
    ('10000000-0000-0000-0000-00000000000a', 'FRE-005', 'Liquide de frein DOT 4 - 1L', 'Liquide de frein synthétique DOT 4. Bidon de 1 litre.', 'Liquide frein DOT4 1L', 'a0000000-0000-0000-0000-000000000002', 5500, 2800, 25, TRUE);

-- Suspension
INSERT INTO part (id, part_number, name, description, short_description, category_id, selling_price, purchase_price, min_stock_level, published) VALUES
    ('10000000-0000-0000-0000-00000000000b', 'SUS-001', 'Amortisseur avant gauche Monroe', 'Amortisseur avant gauche Monroe OESpectrum. Véhicules berline.', 'Amortisseur AV-G Monroe', 'a0000000-0000-0000-0000-000000000003', 42000, 22000, 6, TRUE),
    ('10000000-0000-0000-0000-00000000000c', 'SUS-002', 'Rotule de direction inférieure', 'Rotule de suspension inférieure. Diamètre cône 18mm.', 'Rotule inférieure 18mm', 'a0000000-0000-0000-0000-000000000003', 12000, 5500, 8, TRUE),
    ('10000000-0000-0000-0000-00000000000d', 'SUS-003', 'Ressort hélicoïdal avant', 'Ressort de suspension avant. Charge standard véhicule léger.', 'Ressort avant standard', 'a0000000-0000-0000-0000-000000000003', 25000, 13000, 4, TRUE),
    ('10000000-0000-0000-0000-00000000000e', 'SUS-004', 'Silent bloc bras de suspension', 'Silent bloc de bras de suspension inférieur. Caoutchouc renforcé.', 'Silent bloc bras susp.', 'a0000000-0000-0000-0000-000000000003', 7500, 3500, 12, TRUE),
    ('10000000-0000-0000-0000-00000000000f', 'SUS-005', 'Kit coupelle amortisseur', 'Kit coupelle d''amortisseur avec roulement. Avant gauche/droit.', 'Kit coupelle amortisseur', 'a0000000-0000-0000-0000-000000000003', 15000, 7500, 6, TRUE);

-- Transmission
INSERT INTO part (id, part_number, name, description, short_description, category_id, selling_price, purchase_price, min_stock_level, published) VALUES
    ('10000000-0000-0000-0000-000000000010', 'TRA-001', 'Kit embrayage complet Valeo', 'Kit embrayage 3 pièces Valeo : disque, mécanisme, butée. Peugeot/Citroën.', 'Kit embrayage Valeo', 'a0000000-0000-0000-0000-000000000004', 85000, 45000, 3, TRUE),
    ('10000000-0000-0000-0000-000000000011', 'TRA-002', 'Cardan transmission avant droit', 'Cardan de transmission avant droit. Joint homocinétique inclus.', 'Cardan AV droit', 'a0000000-0000-0000-0000-000000000004', 65000, 35000, 2, TRUE),
    ('10000000-0000-0000-0000-000000000012', 'TRA-003', 'Huile boîte de vitesses 75W80 - 2L', 'Huile transmission manuelle 75W80 GL-4. Bidon de 2 litres.', 'Huile BV 75W80 2L', 'a0000000-0000-0000-0000-000000000004', 12000, 6500, 15, TRUE),
    ('10000000-0000-0000-0000-000000000013', 'TRA-004', 'Câble embrayage Renault', 'Câble de commande d''embrayage pour Renault. Longueur 1050mm.', 'Câble embrayage 1050mm', 'a0000000-0000-0000-0000-000000000004', 9500, 4800, 6, TRUE),
    ('10000000-0000-0000-0000-000000000014', 'TRA-005', 'Soufflet de cardan côté roue', 'Soufflet de cardan côté roue universel. Kit avec colliers et graisse.', 'Soufflet cardan roue', 'a0000000-0000-0000-0000-000000000004', 6000, 2800, 10, TRUE);

-- Électricité
INSERT INTO part (id, part_number, name, description, short_description, category_id, selling_price, purchase_price, min_stock_level, published) VALUES
    ('10000000-0000-0000-0000-000000000015', 'ELE-001', 'Batterie 12V 60Ah Bosch S4', 'Batterie de démarrage Bosch Silver S4 12V 60Ah 540A', 'Batterie Bosch S4 60Ah', 'a0000000-0000-0000-0000-000000000005', 75000, 42000, 8, TRUE),
    ('10000000-0000-0000-0000-000000000016', 'ELE-002', 'Alternateur 14V 90A', 'Alternateur reconditionné 14V 90A. Compatible Peugeot, Citroën.', 'Alternateur 90A', 'a0000000-0000-0000-0000-000000000005', 95000, 50000, 3, TRUE),
    ('10000000-0000-0000-0000-000000000017', 'ELE-003', 'Démarreur Valeo 12V', 'Démarreur Valeo 12V 1.4kW pour moteurs diesel', 'Démarreur Valeo 1.4kW', 'a0000000-0000-0000-0000-000000000005', 110000, 60000, 3, TRUE),
    ('10000000-0000-0000-0000-000000000018', 'ELE-004', 'Capteur ABS avant', 'Capteur de vitesse ABS roue avant. Connecteur 2 broches.', 'Capteur ABS avant', 'a0000000-0000-0000-0000-000000000005', 18000, 8500, 6, TRUE),
    ('10000000-0000-0000-0000-000000000019', 'ELE-005', 'Bobine d''allumage', 'Bobine d''allumage crayon individuelle. Compatible multi-marques.', 'Bobine allumage crayon', 'a0000000-0000-0000-0000-000000000005', 22000, 11000, 8, TRUE);

-- Carrosserie
INSERT INTO part (id, part_number, name, description, short_description, category_id, selling_price, purchase_price, min_stock_level, published) VALUES
    ('10000000-0000-0000-0000-00000000001a', 'CAR-001', 'Rétroviseur extérieur gauche', 'Rétroviseur extérieur gauche électrique. Coque noire apprêtée.', 'Rétro extérieur gauche', 'a0000000-0000-0000-0000-000000000006', 35000, 18000, 4, TRUE),
    ('10000000-0000-0000-0000-00000000001b', 'CAR-002', 'Pare-chocs avant apprêté', 'Pare-chocs avant apprêté universel berline. À peindre.', 'Pare-chocs avant', 'a0000000-0000-0000-0000-000000000006', 65000, 32000, 2, TRUE),
    ('10000000-0000-0000-0000-00000000001c', 'CAR-003', 'Aile avant droite', 'Aile avant droite en tôle. À peindre aux couleurs du véhicule.', 'Aile avant droite', 'a0000000-0000-0000-0000-000000000006', 45000, 22000, 2, TRUE),
    ('10000000-0000-0000-0000-00000000001d', 'CAR-004', 'Capot moteur', 'Capot moteur en acier. Apprêt gris.', 'Capot moteur acier', 'a0000000-0000-0000-0000-000000000006', 85000, 42000, 1, FALSE),
    ('10000000-0000-0000-0000-00000000001e', 'CAR-005', 'Poignée de porte extérieure', 'Poignée de porte extérieure avant droite. Finition noire.', 'Poignée porte AV droite', 'a0000000-0000-0000-0000-000000000006', 12000, 5500, 6, TRUE);

-- Climatisation
INSERT INTO part (id, part_number, name, description, short_description, category_id, selling_price, purchase_price, min_stock_level, published) VALUES
    ('10000000-0000-0000-0000-00000000001f', 'CLI-001', 'Compresseur climatisation', 'Compresseur de climatisation reconditionné. Gaz R134a.', 'Compresseur clim R134a', 'a0000000-0000-0000-0000-000000000007', 145000, 75000, 2, TRUE),
    ('10000000-0000-0000-0000-000000000020', 'CLI-002', 'Condenseur climatisation', 'Condenseur de climatisation aluminium. Dimensions standard.', 'Condenseur clim alu', 'a0000000-0000-0000-0000-000000000007', 65000, 35000, 2, TRUE),
    ('10000000-0000-0000-0000-000000000021', 'CLI-003', 'Filtre déshydrateur clim', 'Filtre déshydrateur pour circuit de climatisation', 'Filtre déshydrateur', 'a0000000-0000-0000-0000-000000000007', 18000, 8500, 5, TRUE),
    ('10000000-0000-0000-0000-000000000022', 'CLI-004', 'Gaz réfrigérant R134a - 900g', 'Recharge de gaz réfrigérant R134a. Bouteille de 900g.', 'Gaz R134a 900g', 'a0000000-0000-0000-0000-000000000007', 12000, 6000, 15, TRUE),
    ('10000000-0000-0000-0000-000000000023', 'CLI-005', 'Ventilateur habitacle', 'Moteur de ventilateur d''habitacle avec turbine', 'Ventilateur habitacle', 'a0000000-0000-0000-0000-000000000007', 28000, 14000, 4, TRUE);

-- Filtration
INSERT INTO part (id, part_number, name, description, short_description, category_id, selling_price, purchase_price, min_stock_level, published) VALUES
    ('10000000-0000-0000-0000-000000000024', 'FIL-001', 'Filtre à air moteur', 'Filtre à air plat pour moteurs essence et diesel', 'Filtre à air standard', 'a0000000-0000-0000-0000-000000000008', 6500, 3000, 25, TRUE),
    ('10000000-0000-0000-0000-000000000025', 'FIL-002', 'Filtre à carburant diesel', 'Filtre à gasoil avec séparateur d''eau intégré', 'Filtre gasoil', 'a0000000-0000-0000-0000-000000000008', 8500, 4200, 20, TRUE),
    ('10000000-0000-0000-0000-000000000026', 'FIL-003', 'Filtre d''habitacle charbon actif', 'Filtre d''habitacle à charbon actif anti-odeurs et anti-pollen', 'Filtre habitacle charbon', 'a0000000-0000-0000-0000-000000000008', 7500, 3500, 15, TRUE),
    ('10000000-0000-0000-0000-000000000027', 'FIL-004', 'Filtre à huile Bosch', 'Filtre à huile Bosch qualité OE. Filetage M20x1.5', 'Filtre huile Bosch', 'a0000000-0000-0000-0000-000000000008', 5000, 2500, 30, TRUE),
    ('10000000-0000-0000-0000-000000000028', 'FIL-005', 'Kit filtration complet', 'Kit de 4 filtres : huile, air, carburant, habitacle', 'Kit 4 filtres complet', 'a0000000-0000-0000-0000-000000000008', 22000, 11000, 10, TRUE);

-- Échappement
INSERT INTO part (id, part_number, name, description, short_description, category_id, selling_price, purchase_price, min_stock_level, published) VALUES
    ('10000000-0000-0000-0000-000000000029', 'ECH-001', 'Pot d''échappement arrière', 'Silencieux arrière en acier inoxydable. Sortie ronde 80mm.', 'Pot échappement arrière', 'a0000000-0000-0000-0000-000000000009', 55000, 28000, 3, TRUE),
    ('10000000-0000-0000-0000-00000000002a', 'ECH-002', 'Catalyseur universel', 'Catalyseur universel aux normes Euro 4. Diamètre 55mm.', 'Catalyseur universel', 'a0000000-0000-0000-0000-000000000009', 120000, 65000, 2, TRUE),
    ('10000000-0000-0000-0000-00000000002b', 'ECH-003', 'Collier échappement 55mm', 'Collier de serrage pour tube d''échappement diamètre 55mm', 'Collier échap. 55mm', 'a0000000-0000-0000-0000-000000000009', 2500, 1000, 20, TRUE),
    ('10000000-0000-0000-0000-00000000002c', 'ECH-004', 'Sonde lambda', 'Sonde lambda 4 fils universelle. Longueur câble 400mm.', 'Sonde lambda 4 fils', 'a0000000-0000-0000-0000-000000000009', 25000, 12000, 5, TRUE);

-- Direction
INSERT INTO part (id, part_number, name, description, short_description, category_id, selling_price, purchase_price, min_stock_level, published) VALUES
    ('10000000-0000-0000-0000-00000000002d', 'DIR-001', 'Crémaillère de direction', 'Crémaillère de direction assistée reconditionnée', 'Crémaillère direction', 'a0000000-0000-0000-0000-00000000000a', 185000, 95000, 1, TRUE),
    ('10000000-0000-0000-0000-00000000002e', 'DIR-002', 'Pompe de direction assistée', 'Pompe de direction assistée hydraulique reconditionnée', 'Pompe DA hydraulique', 'a0000000-0000-0000-0000-00000000000a', 95000, 48000, 2, TRUE),
    ('10000000-0000-0000-0000-00000000002f', 'DIR-003', 'Biellette de direction', 'Biellette de direction avec rotule. Filetage M14.', 'Biellette direction M14', 'a0000000-0000-0000-0000-00000000000a', 14000, 6500, 8, TRUE),
    ('10000000-0000-0000-0000-000000000030', 'DIR-004', 'Huile direction assistée - 1L', 'Huile pour direction assistée ATF Dexron III. 1 litre.', 'Huile DA Dexron III 1L', 'a0000000-0000-0000-0000-00000000000a', 5500, 2800, 15, TRUE);

-- Éclairage
INSERT INTO part (id, part_number, name, description, short_description, category_id, selling_price, purchase_price, min_stock_level, published) VALUES
    ('10000000-0000-0000-0000-000000000031', 'ECL-001', 'Phare avant droit', 'Bloc optique avant droit avec clignotant intégré. H7/H1.', 'Phare avant droit H7', 'a0000000-0000-0000-0000-00000000000b', 48000, 24000, 3, TRUE),
    ('10000000-0000-0000-0000-000000000032', 'ECL-002', 'Feu arrière gauche', 'Feu arrière gauche complet avec porte-ampoules', 'Feu arrière gauche', 'a0000000-0000-0000-0000-00000000000b', 32000, 16000, 3, TRUE),
    ('10000000-0000-0000-0000-000000000033', 'ECL-003', 'Ampoule H7 12V 55W (x2)', 'Lot de 2 ampoules H7 12V 55W. Lumière blanche.', 'Ampoules H7 x2', 'a0000000-0000-0000-0000-00000000000b', 4500, 2000, 30, TRUE),
    ('10000000-0000-0000-0000-000000000034', 'ECL-004', 'Ampoule H4 12V 60/55W (x2)', 'Lot de 2 ampoules H4 12V 60/55W. Double filament.', 'Ampoules H4 x2', 'a0000000-0000-0000-0000-00000000000b', 4000, 1800, 30, TRUE),
    ('10000000-0000-0000-0000-000000000035', 'ECL-005', 'Clignotant latéral LED', 'Répétiteur latéral LED universel. Homologué E4.', 'Clignotant LED latéral', 'a0000000-0000-0000-0000-00000000000b', 3500, 1500, 15, TRUE);

-- Refroidissement
INSERT INTO part (id, part_number, name, description, short_description, category_id, selling_price, purchase_price, min_stock_level, published) VALUES
    ('10000000-0000-0000-0000-000000000036', 'REF-001', 'Radiateur moteur aluminium', 'Radiateur de refroidissement moteur en aluminium. Brasé.', 'Radiateur moteur alu', 'a0000000-0000-0000-0000-00000000000c', 75000, 38000, 2, TRUE),
    ('10000000-0000-0000-0000-000000000037', 'REF-002', 'Thermostat d''eau 89°C', 'Thermostat de calorstat ouverture 89°C. Diamètre 54mm.', 'Thermostat 89°C', 'a0000000-0000-0000-0000-00000000000c', 6500, 3000, 10, TRUE),
    ('10000000-0000-0000-0000-000000000038', 'REF-003', 'Liquide de refroidissement - 5L', 'Liquide de refroidissement antigel -37°C. Bidon de 5 litres. Rose.', 'Antigel -37°C 5L', 'a0000000-0000-0000-0000-00000000000c', 9500, 4800, 15, TRUE),
    ('10000000-0000-0000-0000-000000000039', 'REF-004', 'Ventilateur de radiateur', 'Motoventilateur de radiateur électrique avec hélice', 'Ventilateur radiateur', 'a0000000-0000-0000-0000-00000000000c', 55000, 28000, 2, TRUE),
    ('10000000-0000-0000-0000-00000000003a', 'REF-005', 'Durite de radiateur supérieure', 'Durite de radiateur supérieure en caoutchouc EPDM renforcé', 'Durite radiateur sup.', 'a0000000-0000-0000-0000-00000000000c', 8500, 4000, 8, TRUE);

-- =============================================
-- Stock initial (entrepôt principal Dakar)
-- =============================================
INSERT INTO warehouse_stock (id, warehouse_id, part_id, quantity, min_stock_level) VALUES
    -- Moteur
    (gen_random_uuid(), 'b0000000-0000-0000-0000-000000000001', '10000000-0000-0000-0000-000000000001', 45, 20),
    (gen_random_uuid(), 'b0000000-0000-0000-0000-000000000001', '10000000-0000-0000-0000-000000000002', 8, 5),
    (gen_random_uuid(), 'b0000000-0000-0000-0000-000000000001', '10000000-0000-0000-0000-000000000003', 5, 3),
    (gen_random_uuid(), 'b0000000-0000-0000-0000-000000000001', '10000000-0000-0000-0000-000000000004', 80, 50),
    (gen_random_uuid(), 'b0000000-0000-0000-0000-000000000001', '10000000-0000-0000-0000-000000000005', 4, 3),
    -- Freinage
    (gen_random_uuid(), 'b0000000-0000-0000-0000-000000000001', '10000000-0000-0000-0000-000000000006', 25, 15),
    (gen_random_uuid(), 'b0000000-0000-0000-0000-000000000001', '10000000-0000-0000-0000-000000000007', 15, 10),
    (gen_random_uuid(), 'b0000000-0000-0000-0000-000000000001', '10000000-0000-0000-0000-000000000008', 6, 4),
    (gen_random_uuid(), 'b0000000-0000-0000-0000-000000000001', '10000000-0000-0000-0000-000000000009', 18, 10),
    (gen_random_uuid(), 'b0000000-0000-0000-0000-000000000001', '10000000-0000-0000-0000-00000000000a', 35, 25),
    -- Suspension
    (gen_random_uuid(), 'b0000000-0000-0000-0000-000000000001', '10000000-0000-0000-0000-00000000000b', 8, 6),
    (gen_random_uuid(), 'b0000000-0000-0000-0000-000000000001', '10000000-0000-0000-0000-00000000000c', 12, 8),
    (gen_random_uuid(), 'b0000000-0000-0000-0000-000000000001', '10000000-0000-0000-0000-00000000000d', 6, 4),
    (gen_random_uuid(), 'b0000000-0000-0000-0000-000000000001', '10000000-0000-0000-0000-00000000000e', 20, 12),
    (gen_random_uuid(), 'b0000000-0000-0000-0000-000000000001', '10000000-0000-0000-0000-00000000000f', 10, 6),
    -- Transmission
    (gen_random_uuid(), 'b0000000-0000-0000-0000-000000000001', '10000000-0000-0000-0000-000000000010', 4, 3),
    (gen_random_uuid(), 'b0000000-0000-0000-0000-000000000001', '10000000-0000-0000-0000-000000000011', 3, 2),
    (gen_random_uuid(), 'b0000000-0000-0000-0000-000000000001', '10000000-0000-0000-0000-000000000012', 20, 15),
    (gen_random_uuid(), 'b0000000-0000-0000-0000-000000000001', '10000000-0000-0000-0000-000000000013', 8, 6),
    (gen_random_uuid(), 'b0000000-0000-0000-0000-000000000001', '10000000-0000-0000-0000-000000000014', 15, 10),
    -- Électricité
    (gen_random_uuid(), 'b0000000-0000-0000-0000-000000000001', '10000000-0000-0000-0000-000000000015', 12, 8),
    (gen_random_uuid(), 'b0000000-0000-0000-0000-000000000001', '10000000-0000-0000-0000-000000000016', 4, 3),
    (gen_random_uuid(), 'b0000000-0000-0000-0000-000000000001', '10000000-0000-0000-0000-000000000017', 4, 3),
    (gen_random_uuid(), 'b0000000-0000-0000-0000-000000000001', '10000000-0000-0000-0000-000000000018', 10, 6),
    (gen_random_uuid(), 'b0000000-0000-0000-0000-000000000001', '10000000-0000-0000-0000-000000000019', 12, 8),
    -- Filtration
    (gen_random_uuid(), 'b0000000-0000-0000-0000-000000000001', '10000000-0000-0000-0000-000000000024', 40, 25),
    (gen_random_uuid(), 'b0000000-0000-0000-0000-000000000001', '10000000-0000-0000-0000-000000000025', 30, 20),
    (gen_random_uuid(), 'b0000000-0000-0000-0000-000000000001', '10000000-0000-0000-0000-000000000026', 22, 15),
    (gen_random_uuid(), 'b0000000-0000-0000-0000-000000000001', '10000000-0000-0000-0000-000000000027', 50, 30),
    (gen_random_uuid(), 'b0000000-0000-0000-0000-000000000001', '10000000-0000-0000-0000-000000000028', 14, 10),
    -- Éclairage
    (gen_random_uuid(), 'b0000000-0000-0000-0000-000000000001', '10000000-0000-0000-0000-000000000033', 50, 30),
    (gen_random_uuid(), 'b0000000-0000-0000-0000-000000000001', '10000000-0000-0000-0000-000000000034', 45, 30),
    -- Refroidissement
    (gen_random_uuid(), 'b0000000-0000-0000-0000-000000000001', '10000000-0000-0000-0000-000000000037', 15, 10),
    (gen_random_uuid(), 'b0000000-0000-0000-0000-000000000001', '10000000-0000-0000-0000-000000000038', 22, 15);

-- Stock dépôt Thiès (plus petit)
INSERT INTO warehouse_stock (id, warehouse_id, part_id, quantity, min_stock_level) VALUES
    (gen_random_uuid(), 'b0000000-0000-0000-0000-000000000002', '10000000-0000-0000-0000-000000000001', 15, 10),
    (gen_random_uuid(), 'b0000000-0000-0000-0000-000000000002', '10000000-0000-0000-0000-000000000004', 30, 20),
    (gen_random_uuid(), 'b0000000-0000-0000-0000-000000000002', '10000000-0000-0000-0000-000000000006', 10, 8),
    (gen_random_uuid(), 'b0000000-0000-0000-0000-000000000002', '10000000-0000-0000-0000-000000000007', 6, 4),
    (gen_random_uuid(), 'b0000000-0000-0000-0000-000000000002', '10000000-0000-0000-0000-000000000015', 5, 3),
    (gen_random_uuid(), 'b0000000-0000-0000-0000-000000000002', '10000000-0000-0000-0000-000000000024', 15, 10),
    (gen_random_uuid(), 'b0000000-0000-0000-0000-000000000002', '10000000-0000-0000-0000-000000000025', 12, 8),
    (gen_random_uuid(), 'b0000000-0000-0000-0000-000000000002', '10000000-0000-0000-0000-000000000027', 20, 12),
    (gen_random_uuid(), 'b0000000-0000-0000-0000-000000000002', '10000000-0000-0000-0000-000000000033', 20, 10),
    (gen_random_uuid(), 'b0000000-0000-0000-0000-000000000002', '10000000-0000-0000-0000-000000000038', 8, 5);

-- Stock magasin Médina (pièces courantes uniquement)
INSERT INTO warehouse_stock (id, warehouse_id, part_id, quantity, min_stock_level) VALUES
    (gen_random_uuid(), 'b0000000-0000-0000-0000-000000000003', '10000000-0000-0000-0000-000000000001', 10, 5),
    (gen_random_uuid(), 'b0000000-0000-0000-0000-000000000003', '10000000-0000-0000-0000-000000000004', 25, 15),
    (gen_random_uuid(), 'b0000000-0000-0000-0000-000000000003', '10000000-0000-0000-0000-000000000006', 8, 5),
    (gen_random_uuid(), 'b0000000-0000-0000-0000-000000000003', '10000000-0000-0000-0000-00000000000a', 10, 5),
    (gen_random_uuid(), 'b0000000-0000-0000-0000-000000000003', '10000000-0000-0000-0000-000000000024', 12, 8),
    (gen_random_uuid(), 'b0000000-0000-0000-0000-000000000003', '10000000-0000-0000-0000-000000000027', 15, 10),
    (gen_random_uuid(), 'b0000000-0000-0000-0000-000000000003', '10000000-0000-0000-0000-000000000033', 15, 10),
    (gen_random_uuid(), 'b0000000-0000-0000-0000-000000000003', '10000000-0000-0000-0000-000000000034', 15, 10);

-- =============================================
-- Compteurs de séquence
-- =============================================
INSERT INTO sequence_counter (id, entity_type, "year", last_value) VALUES
    (gen_random_uuid(), 'INVOICE', 2026, 0),
    (gen_random_uuid(), 'PROFORMA', 2026, 0),
    (gen_random_uuid(), 'DEPOSIT', 2026, 0),
    (gen_random_uuid(), 'ORDER', 2026, 0),
    (gen_random_uuid(), 'PURCHASE_ORDER', 2026, 0),
    (gen_random_uuid(), 'TRANSFER', 2026, 0),
    (gen_random_uuid(), 'RETURN', 2026, 0),
    (gen_random_uuid(), 'CREDIT_NOTE', 2026, 0);
