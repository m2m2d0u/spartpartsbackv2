package sn.symmetry.spareparts.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "store")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(nullable = false, unique = true, length = 20)
    private String code;

    @Column(length = 300)
    private String street;

    @Column(length = 100)
    private String city;

    @Column(length = 100)
    private String state;

    @Column(name = "postal_code", length = 20)
    private String postalCode;

    @Column(length = 100)
    private String country;

    @Column(length = 50)
    private String phone;

    @Column(length = 200)
    private String email;

    @Column(name = "logo_url", length = 500)
    private String logoUrl;

    @Column(name = "stamp_image_url", length = 500)
    private String stampImageUrl;

    @Column(length = 50)
    private String ninea;

    @Column(length = 50)
    private String rccm;

    @Column(name = "tax_id", length = 50)
    private String taxId;

    @Column(name = "proforma_prefix", length = 10)
    private String proformaPrefix;

    @Column(name = "invoice_prefix", length = 10)
    private String invoicePrefix;

    @Column(name = "deposit_prefix", length = 10)
    private String depositPrefix;

    @Column(name = "credit_note_prefix", length = 10)
    private String creditNotePrefix;

    @Column(name = "order_prefix", length = 10)
    private String orderPrefix;

    @Column(name = "default_payment_terms")
    private Integer defaultPaymentTerms;

    @Column(name = "default_proforma_validity")
    private Integer defaultProformaValidity;

    @Column(name = "default_template_id")
    private UUID defaultTemplateId;

    @Column(name = "default_invoice_notes", columnDefinition = "TEXT")
    private String defaultInvoiceNotes;

    @Column(name = "default_warehouse_id")
    private UUID defaultWarehouseId;

    @Column(name = "portal_warehouse_id")
    private UUID portalWarehouseId;

    @Column(name = "currency_symbol", length = 10)
    private String currencySymbol;

    @Column(name = "currency_position", length = 10)
    private String currencyPosition;

    @Column(name = "currency_decimals")
    private Integer currencyDecimals;

    @Column(name = "thousands_separator", length = 5)
    private String thousandsSeparator;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
