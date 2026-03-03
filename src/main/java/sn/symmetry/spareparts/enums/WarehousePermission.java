package sn.symmetry.spareparts.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Warehouse permissions for granular access control.
 * Each permission represents a specific action a user can perform in a warehouse.
 */
@Getter
public enum WarehousePermission {

    // Stock Management Permissions
    STOCK_VIEW("View Stock", "View warehouse stock levels and details", PermissionCategory.STOCK, PermissionLevel.READ),
    STOCK_CREATE("Add Stock", "Add new stock items to the warehouse", PermissionCategory.STOCK, PermissionLevel.WRITE),
    STOCK_UPDATE("Update Stock", "Update stock information (min levels, locations)", PermissionCategory.STOCK, PermissionLevel.WRITE),
    STOCK_ADJUST("Adjust Stock", "Adjust stock quantities (increase/decrease)", PermissionCategory.STOCK, PermissionLevel.WRITE),
    STOCK_DELETE("Delete Stock", "Remove stock items from the warehouse", PermissionCategory.STOCK, PermissionLevel.DELETE),
    STOCK_EXPORT("Export Stock", "Export stock data to Excel/CSV", PermissionCategory.STOCK, PermissionLevel.READ),

    // Order Management Permissions
    ORDER_VIEW("View Orders", "View warehouse orders and order details", PermissionCategory.ORDER, PermissionLevel.READ),
    ORDER_CREATE("Create Orders", "Create new orders for the warehouse", PermissionCategory.ORDER, PermissionLevel.WRITE),
    ORDER_UPDATE("Update Orders", "Update order information and status", PermissionCategory.ORDER, PermissionLevel.WRITE),
    ORDER_DELETE("Delete Orders", "Cancel or delete orders", PermissionCategory.ORDER, PermissionLevel.DELETE),
    ORDER_APPROVE("Approve Orders", "Approve or reject orders", PermissionCategory.ORDER, PermissionLevel.APPROVE),
    ORDER_FULFILL("Fulfill Orders", "Mark orders as fulfilled and process shipments", PermissionCategory.ORDER, PermissionLevel.WRITE),
    ORDER_EXPORT("Export Orders", "Export order data to Excel/CSV", PermissionCategory.ORDER, PermissionLevel.READ),

    // Invoice Management Permissions
    INVOICE_VIEW("View Invoices", "View warehouse invoices and invoice details", PermissionCategory.INVOICE, PermissionLevel.READ),
    INVOICE_CREATE("Create Invoices", "Create new invoices", PermissionCategory.INVOICE, PermissionLevel.WRITE),
    INVOICE_UPDATE("Update Invoices", "Update invoice information", PermissionCategory.INVOICE, PermissionLevel.WRITE),
    INVOICE_DELETE("Delete Invoices", "Delete or void invoices", PermissionCategory.INVOICE, PermissionLevel.DELETE),
    INVOICE_SEND("Send Invoices", "Send invoices to customers via email", PermissionCategory.INVOICE, PermissionLevel.WRITE),
    INVOICE_PRINT("Print Invoices", "Print or download invoice PDFs", PermissionCategory.INVOICE, PermissionLevel.READ),
    INVOICE_PAYMENT("Record Payments", "Record and manage invoice payments", PermissionCategory.INVOICE, PermissionLevel.WRITE),
    INVOICE_EXPORT("Export Invoices", "Export invoice data to Excel/CSV", PermissionCategory.INVOICE, PermissionLevel.READ),

    // Procurement Permissions
    PROCUREMENT_VIEW("View Procurement", "View purchase orders and procurement details", PermissionCategory.PROCUREMENT, PermissionLevel.READ),
    PROCUREMENT_CREATE("Create Purchase Orders", "Create new purchase orders", PermissionCategory.PROCUREMENT, PermissionLevel.WRITE),
    PROCUREMENT_UPDATE("Update Purchase Orders", "Update purchase order information", PermissionCategory.PROCUREMENT, PermissionLevel.WRITE),
    PROCUREMENT_DELETE("Delete Purchase Orders", "Delete or cancel purchase orders", PermissionCategory.PROCUREMENT, PermissionLevel.DELETE),
    PROCUREMENT_APPROVE("Approve Purchase Orders", "Approve or reject purchase orders", PermissionCategory.PROCUREMENT, PermissionLevel.APPROVE),
    PROCUREMENT_RECEIVE("Receive Goods", "Receive and process incoming shipments", PermissionCategory.PROCUREMENT, PermissionLevel.WRITE),
    PROCUREMENT_EXPORT("Export Procurement", "Export procurement data to Excel/CSV", PermissionCategory.PROCUREMENT, PermissionLevel.READ),

    // Transfer Management Permissions
    TRANSFER_VIEW("View Transfers", "View stock transfers between warehouses", PermissionCategory.TRANSFER, PermissionLevel.READ),
    TRANSFER_CREATE("Create Transfers", "Create new stock transfers", PermissionCategory.TRANSFER, PermissionLevel.WRITE),
    TRANSFER_UPDATE("Update Transfers", "Update transfer information", PermissionCategory.TRANSFER, PermissionLevel.WRITE),
    TRANSFER_DELETE("Delete Transfers", "Cancel or delete transfers", PermissionCategory.TRANSFER, PermissionLevel.DELETE),
    TRANSFER_APPROVE("Approve Transfers", "Approve or reject transfer requests", PermissionCategory.TRANSFER, PermissionLevel.APPROVE),
    TRANSFER_SEND("Send Transfers", "Ship transfers from the warehouse", PermissionCategory.TRANSFER, PermissionLevel.WRITE),
    TRANSFER_RECEIVE("Receive Transfers", "Receive incoming transfers", PermissionCategory.TRANSFER, PermissionLevel.WRITE),
    TRANSFER_EXPORT("Export Transfers", "Export transfer data to Excel/CSV", PermissionCategory.TRANSFER, PermissionLevel.READ),

    // Return Management Permissions
    RETURN_VIEW("View Returns", "View product returns and return details", PermissionCategory.RETURN, PermissionLevel.READ),
    RETURN_CREATE("Create Returns", "Process new product returns", PermissionCategory.RETURN, PermissionLevel.WRITE),
    RETURN_UPDATE("Update Returns", "Update return information", PermissionCategory.RETURN, PermissionLevel.WRITE),
    RETURN_DELETE("Delete Returns", "Cancel or delete returns", PermissionCategory.RETURN, PermissionLevel.DELETE),
    RETURN_APPROVE("Approve Returns", "Approve or reject return requests", PermissionCategory.RETURN, PermissionLevel.APPROVE),
    RETURN_REFUND("Process Refunds", "Process refunds for returns", PermissionCategory.RETURN, PermissionLevel.WRITE),
    RETURN_RESTOCK("Restock Returns", "Add returned items back to stock", PermissionCategory.RETURN, PermissionLevel.WRITE),
    RETURN_EXPORT("Export Returns", "Export return data to Excel/CSV", PermissionCategory.RETURN, PermissionLevel.READ),

    // Report Permissions
    REPORT_VIEW("View Reports", "View warehouse reports and analytics", PermissionCategory.REPORT, PermissionLevel.READ),
    REPORT_EXPORT("Export Reports", "Export reports to Excel/PDF", PermissionCategory.REPORT, PermissionLevel.READ),
    REPORT_SALES("Sales Reports", "View and export sales reports", PermissionCategory.REPORT, PermissionLevel.READ),
    REPORT_INVENTORY("Inventory Reports", "View and export inventory reports", PermissionCategory.REPORT, PermissionLevel.READ),
    REPORT_FINANCIAL("Financial Reports", "View and export financial reports", PermissionCategory.REPORT, PermissionLevel.READ),

    // Customer Management Permissions
    CUSTOMER_VIEW("View Customers", "View customer information", PermissionCategory.CUSTOMER, PermissionLevel.READ),
    CUSTOMER_CREATE("Create Customers", "Add new customers", PermissionCategory.CUSTOMER, PermissionLevel.WRITE),
    CUSTOMER_UPDATE("Update Customers", "Update customer information", PermissionCategory.CUSTOMER, PermissionLevel.WRITE),
    CUSTOMER_DELETE("Delete Customers", "Delete or deactivate customers", PermissionCategory.CUSTOMER, PermissionLevel.DELETE),
    CUSTOMER_EXPORT("Export Customers", "Export customer data to Excel/CSV", PermissionCategory.CUSTOMER, PermissionLevel.READ),

    // Parts Management Permissions
    PART_VIEW("View Parts", "View parts catalog and part details", PermissionCategory.PART, PermissionLevel.READ),
    PART_CREATE("Create Parts", "Add new parts to the catalog", PermissionCategory.PART, PermissionLevel.WRITE),
    PART_UPDATE("Update Parts", "Update part information and pricing", PermissionCategory.PART, PermissionLevel.WRITE),
    PART_DELETE("Delete Parts", "Delete or deactivate parts", PermissionCategory.PART, PermissionLevel.DELETE),
    PART_EXPORT("Export Parts", "Export parts catalog to Excel/CSV", PermissionCategory.PART, PermissionLevel.READ),
    PART_IMPORT("Import Parts", "Import parts from Excel/CSV", PermissionCategory.PART, PermissionLevel.WRITE),
    PART_PRICING("Manage Pricing", "Update part prices and discounts", PermissionCategory.PART, PermissionLevel.WRITE),

    // Warehouse Settings Permissions
    SETTINGS_VIEW("View Settings", "View warehouse settings and configuration", PermissionCategory.SETTINGS, PermissionLevel.READ),
    SETTINGS_UPDATE("Update Settings", "Update warehouse settings and configuration", PermissionCategory.SETTINGS, PermissionLevel.WRITE);

    private final String displayName;
    private final String description;
    private final PermissionCategory category;
    private final PermissionLevel level;

    WarehousePermission(String displayName, String description, PermissionCategory category, PermissionLevel level) {
        this.displayName = displayName;
        this.description = description;
        this.category = category;
        this.level = level;
    }

    /**
     * Get all permissions for a specific category.
     */
    public static List<WarehousePermission> getByCategory(PermissionCategory category) {
        return Arrays.stream(values())
                .filter(p -> p.category == category)
                .collect(Collectors.toList());
    }

    /**
     * Get all permissions of a specific level.
     */
    public static List<WarehousePermission> getByLevel(PermissionLevel level) {
        return Arrays.stream(values())
                .filter(p -> p.level == level)
                .collect(Collectors.toList());
    }

    /**
     * Get all active permissions (all permissions are now active - legacy removed).
     */
    public static List<WarehousePermission> getActivePermissions() {
        return Arrays.asList(values());
    }

    /**
     * Get all read-only permissions.
     */
    public static List<WarehousePermission> getReadPermissions() {
        return getByLevel(PermissionLevel.READ);
    }

    /**
     * Get all write permissions (excluding read-only).
     */
    public static List<WarehousePermission> getWritePermissions() {
        return Arrays.stream(values())
                .filter(p -> p.level == PermissionLevel.WRITE ||
                            p.level == PermissionLevel.DELETE ||
                            p.level == PermissionLevel.APPROVE)
                .collect(Collectors.toList());
    }

    /**
     * Check if this is a legacy permission (always false - legacy permissions removed).
     */
    public boolean isLegacy() {
        return false;
    }

    /**
     * Check if this is a read-only permission.
     */
    public boolean isReadOnly() {
        return this.level == PermissionLevel.READ;
    }

    /**
     * Permission categories for grouping.
     */
    public enum PermissionCategory {
        STOCK("Stock Management", "Permissions related to inventory and stock management"),
        ORDER("Order Management", "Permissions related to customer orders"),
        INVOICE("Invoice Management", "Permissions related to invoicing and billing"),
        PROCUREMENT("Procurement", "Permissions related to purchasing and receiving goods"),
        TRANSFER("Transfer Management", "Permissions related to stock transfers between warehouses"),
        RETURN("Return Management", "Permissions related to product returns and refunds"),
        REPORT("Reports & Analytics", "Permissions related to viewing and exporting reports"),
        CUSTOMER("Customer Management", "Permissions related to customer data"),
        PART("Parts Management", "Permissions related to parts catalog and pricing"),
        SETTINGS("Warehouse Settings", "Permissions related to warehouse configuration");

        @Getter
        private final String displayName;
        @Getter
        private final String description;

        PermissionCategory(String displayName, String description) {
            this.displayName = displayName;
            this.description = description;
        }
    }

    /**
     * Permission levels indicating the type of access.
     */
    public enum PermissionLevel {
        READ("Read Only", "Can only view data"),
        WRITE("Write", "Can create and modify data"),
        DELETE("Delete", "Can delete data"),
        APPROVE("Approve", "Can approve or reject requests");

        @Getter
        private final String displayName;
        @Getter
        private final String description;

        PermissionLevel(String displayName, String description) {
            this.displayName = displayName;
            this.description = description;
        }
    }
}
