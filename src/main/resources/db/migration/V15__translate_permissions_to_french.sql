-- Translate all permission display_name and description to French

-- Stock Management
UPDATE permission SET display_name = 'Voir le stock', description = 'Consulter les niveaux et détails du stock de l''entrepôt' WHERE code = 'STOCK_VIEW';
UPDATE permission SET display_name = 'Ajouter du stock', description = 'Ajouter de nouveaux articles au stock de l''entrepôt' WHERE code = 'STOCK_CREATE';
UPDATE permission SET display_name = 'Modifier le stock', description = 'Modifier les informations du stock (niveaux min, emplacements)' WHERE code = 'STOCK_UPDATE';
UPDATE permission SET display_name = 'Ajuster le stock', description = 'Ajuster les quantités de stock (augmentation/diminution)' WHERE code = 'STOCK_ADJUST';
UPDATE permission SET display_name = 'Supprimer le stock', description = 'Retirer des articles du stock de l''entrepôt' WHERE code = 'STOCK_DELETE';
UPDATE permission SET display_name = 'Exporter le stock', description = 'Exporter les données de stock en Excel/CSV' WHERE code = 'STOCK_EXPORT';

-- Order Management
UPDATE permission SET display_name = 'Voir les commandes', description = 'Consulter les commandes et leurs détails' WHERE code = 'ORDER_VIEW';
UPDATE permission SET display_name = 'Créer des commandes', description = 'Créer de nouvelles commandes pour l''entrepôt' WHERE code = 'ORDER_CREATE';
UPDATE permission SET display_name = 'Modifier les commandes', description = 'Modifier les informations et le statut des commandes' WHERE code = 'ORDER_UPDATE';
UPDATE permission SET display_name = 'Supprimer les commandes', description = 'Annuler ou supprimer des commandes' WHERE code = 'ORDER_DELETE';
UPDATE permission SET display_name = 'Approuver les commandes', description = 'Approuver ou rejeter des commandes' WHERE code = 'ORDER_APPROVE';
UPDATE permission SET display_name = 'Exécuter les commandes', description = 'Marquer les commandes comme exécutées et traiter les expéditions' WHERE code = 'ORDER_FULFILL';
UPDATE permission SET display_name = 'Exporter les commandes', description = 'Exporter les données de commandes en Excel/CSV' WHERE code = 'ORDER_EXPORT';

-- Invoice Management
UPDATE permission SET display_name = 'Voir les factures', description = 'Consulter les factures et leurs détails' WHERE code = 'INVOICE_VIEW';
UPDATE permission SET display_name = 'Créer des factures', description = 'Créer de nouvelles factures' WHERE code = 'INVOICE_CREATE';
UPDATE permission SET display_name = 'Modifier les factures', description = 'Modifier les informations des factures' WHERE code = 'INVOICE_UPDATE';
UPDATE permission SET display_name = 'Supprimer les factures', description = 'Supprimer ou annuler des factures' WHERE code = 'INVOICE_DELETE';
UPDATE permission SET display_name = 'Envoyer les factures', description = 'Envoyer les factures aux clients par e-mail' WHERE code = 'INVOICE_SEND';
UPDATE permission SET display_name = 'Imprimer les factures', description = 'Imprimer ou télécharger les factures en PDF' WHERE code = 'INVOICE_PRINT';
UPDATE permission SET display_name = 'Enregistrer les paiements', description = 'Enregistrer et gérer les paiements des factures' WHERE code = 'INVOICE_PAYMENT';
UPDATE permission SET display_name = 'Exporter les factures', description = 'Exporter les données de factures en Excel/CSV' WHERE code = 'INVOICE_EXPORT';

-- Procurement
UPDATE permission SET display_name = 'Voir les achats', description = 'Consulter les bons de commande et les détails des achats' WHERE code = 'PROCUREMENT_VIEW';
UPDATE permission SET display_name = 'Créer des bons de commande', description = 'Créer de nouveaux bons de commande' WHERE code = 'PROCUREMENT_CREATE';
UPDATE permission SET display_name = 'Modifier les bons de commande', description = 'Modifier les informations des bons de commande' WHERE code = 'PROCUREMENT_UPDATE';
UPDATE permission SET display_name = 'Supprimer les bons de commande', description = 'Supprimer ou annuler des bons de commande' WHERE code = 'PROCUREMENT_DELETE';
UPDATE permission SET display_name = 'Approuver les bons de commande', description = 'Approuver ou rejeter des bons de commande' WHERE code = 'PROCUREMENT_APPROVE';
UPDATE permission SET display_name = 'Réceptionner les marchandises', description = 'Réceptionner et traiter les livraisons entrantes' WHERE code = 'PROCUREMENT_RECEIVE';
UPDATE permission SET display_name = 'Exporter les achats', description = 'Exporter les données d''achats en Excel/CSV' WHERE code = 'PROCUREMENT_EXPORT';

-- Transfer Management
UPDATE permission SET display_name = 'Voir les transferts', description = 'Consulter les transferts de stock entre entrepôts' WHERE code = 'TRANSFER_VIEW';
UPDATE permission SET display_name = 'Créer des transferts', description = 'Créer de nouveaux transferts de stock' WHERE code = 'TRANSFER_CREATE';
UPDATE permission SET display_name = 'Modifier les transferts', description = 'Modifier les informations des transferts' WHERE code = 'TRANSFER_UPDATE';
UPDATE permission SET display_name = 'Supprimer les transferts', description = 'Annuler ou supprimer des transferts' WHERE code = 'TRANSFER_DELETE';
UPDATE permission SET display_name = 'Approuver les transferts', description = 'Approuver ou rejeter des demandes de transfert' WHERE code = 'TRANSFER_APPROVE';
UPDATE permission SET display_name = 'Expédier les transferts', description = 'Expédier les transferts depuis l''entrepôt' WHERE code = 'TRANSFER_SEND';
UPDATE permission SET display_name = 'Réceptionner les transferts', description = 'Réceptionner les transferts entrants' WHERE code = 'TRANSFER_RECEIVE';
UPDATE permission SET display_name = 'Exporter les transferts', description = 'Exporter les données de transferts en Excel/CSV' WHERE code = 'TRANSFER_EXPORT';

-- Return Management
UPDATE permission SET display_name = 'Voir les retours', description = 'Consulter les retours de produits et leurs détails' WHERE code = 'RETURN_VIEW';
UPDATE permission SET display_name = 'Créer des retours', description = 'Traiter de nouveaux retours de produits' WHERE code = 'RETURN_CREATE';
UPDATE permission SET display_name = 'Modifier les retours', description = 'Modifier les informations des retours' WHERE code = 'RETURN_UPDATE';
UPDATE permission SET display_name = 'Supprimer les retours', description = 'Annuler ou supprimer des retours' WHERE code = 'RETURN_DELETE';
UPDATE permission SET display_name = 'Approuver les retours', description = 'Approuver ou rejeter des demandes de retour' WHERE code = 'RETURN_APPROVE';
UPDATE permission SET display_name = 'Traiter les remboursements', description = 'Traiter les remboursements pour les retours' WHERE code = 'RETURN_REFUND';
UPDATE permission SET display_name = 'Remettre en stock', description = 'Remettre les articles retournés en stock' WHERE code = 'RETURN_RESTOCK';
UPDATE permission SET display_name = 'Exporter les retours', description = 'Exporter les données de retours en Excel/CSV' WHERE code = 'RETURN_EXPORT';

-- Reports
UPDATE permission SET display_name = 'Voir les rapports', description = 'Consulter les rapports et analyses de l''entrepôt' WHERE code = 'REPORT_VIEW';
UPDATE permission SET display_name = 'Exporter les rapports', description = 'Exporter les rapports en Excel/PDF' WHERE code = 'REPORT_EXPORT';
UPDATE permission SET display_name = 'Rapports de ventes', description = 'Consulter et exporter les rapports de ventes' WHERE code = 'REPORT_SALES';
UPDATE permission SET display_name = 'Rapports d''inventaire', description = 'Consulter et exporter les rapports d''inventaire' WHERE code = 'REPORT_INVENTORY';
UPDATE permission SET display_name = 'Rapports financiers', description = 'Consulter et exporter les rapports financiers' WHERE code = 'REPORT_FINANCIAL';

-- Customer Management
UPDATE permission SET display_name = 'Voir les clients', description = 'Consulter les informations des clients' WHERE code = 'CUSTOMER_VIEW';
UPDATE permission SET display_name = 'Créer des clients', description = 'Ajouter de nouveaux clients' WHERE code = 'CUSTOMER_CREATE';
UPDATE permission SET display_name = 'Modifier les clients', description = 'Modifier les informations des clients' WHERE code = 'CUSTOMER_UPDATE';
UPDATE permission SET display_name = 'Supprimer les clients', description = 'Supprimer ou désactiver des clients' WHERE code = 'CUSTOMER_DELETE';
UPDATE permission SET display_name = 'Exporter les clients', description = 'Exporter les données des clients en Excel/CSV' WHERE code = 'CUSTOMER_EXPORT';

-- Parts Management
UPDATE permission SET display_name = 'Voir les pièces', description = 'Consulter le catalogue et les détails des pièces' WHERE code = 'PART_VIEW';
UPDATE permission SET display_name = 'Créer des pièces', description = 'Ajouter de nouvelles pièces au catalogue' WHERE code = 'PART_CREATE';
UPDATE permission SET display_name = 'Modifier les pièces', description = 'Modifier les informations et les prix des pièces' WHERE code = 'PART_UPDATE';
UPDATE permission SET display_name = 'Supprimer les pièces', description = 'Supprimer ou désactiver des pièces' WHERE code = 'PART_DELETE';
UPDATE permission SET display_name = 'Exporter les pièces', description = 'Exporter le catalogue de pièces en Excel/CSV' WHERE code = 'PART_EXPORT';
UPDATE permission SET display_name = 'Importer les pièces', description = 'Importer des pièces depuis Excel/CSV' WHERE code = 'PART_IMPORT';
UPDATE permission SET display_name = 'Gérer les tarifs', description = 'Modifier les prix et les remises des pièces' WHERE code = 'PART_PRICING';

-- Warehouse Settings
UPDATE permission SET display_name = 'Voir les paramètres', description = 'Consulter les paramètres et la configuration de l''entrepôt' WHERE code = 'SETTINGS_VIEW';
UPDATE permission SET display_name = 'Modifier les paramètres', description = 'Modifier les paramètres et la configuration de l''entrepôt' WHERE code = 'SETTINGS_UPDATE';
