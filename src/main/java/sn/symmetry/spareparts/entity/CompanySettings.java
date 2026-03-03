package sn.symmetry.spareparts.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "company_settings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CompanySettings {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "company_name", length = 200)
    private String companyName;

    @Column(name = "logo_url", length = 500)
    private String logoUrl;

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

    @Column(name = "tax_id", length = 50)
    private String taxId;

    @Column(length = 50)
    private String ninea;

    @Column(length = 50)
    private String rccm;

    @Column(length = 50)
    private String phone;

    @Column(length = 200)
    private String email;

    @Column(name = "default_tax_rate", nullable = false, precision = 5, scale = 2)
    private BigDecimal defaultTaxRate = BigDecimal.ZERO;

    @Column(name = "proforma_prefix", nullable = false, length = 10)
    private String proformaPrefix = "PRO";

    @Column(name = "invoice_prefix", nullable = false, length = 10)
    private String invoicePrefix = "INV";

    @Column(name = "deposit_prefix", nullable = false, length = 10)
    private String depositPrefix = "DEP";

    @Column(name = "credit_note_prefix", nullable = false, length = 10)
    private String creditNotePrefix = "CN";

    @Column(name = "order_prefix", nullable = false, length = 10)
    private String orderPrefix = "ORD";

    @Column(name = "po_prefix", nullable = false, length = 10)
    private String poPrefix = "PO";

    @Column(name = "transfer_prefix", nullable = false, length = 10)
    private String transferPrefix = "TRF";

    @Column(name = "return_prefix", nullable = false, length = 10)
    private String returnPrefix = "RET";

    @Column(name = "default_payment_terms", nullable = false)
    private Integer defaultPaymentTerms = 30;

    @Column(name = "default_proforma_validity", nullable = false)
    private Integer defaultProformaValidity = 30;

    @Column(name = "default_invoice_notes", columnDefinition = "TEXT")
    private String defaultInvoiceNotes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "default_template_id")
    private InvoiceTemplate defaultTemplate;

    @Column(name = "sequential_reset_yearly", nullable = false)
    private Boolean sequentialResetYearly = true;

    @Column(name = "currency_symbol", nullable = false, length = 10)
    private String currencySymbol = "$";

    @Column(name = "currency_position", nullable = false, length = 10)
    private String currencyPosition = "BEFORE";

    @Column(name = "currency_decimals", nullable = false)
    private Integer currencyDecimals = 2;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "default_warehouse_id")
    private Warehouse defaultWarehouse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portal_warehouse_id")
    private Warehouse portalWarehouse;

    @Column(name = "portal_enabled", nullable = false)
    private Boolean portalEnabled = false;

    @Column(name = "portal_min_order_amount", precision = 12, scale = 2)
    private BigDecimal portalMinOrderAmount;

    @Column(name = "portal_shipping_flat_rate", precision = 12, scale = 2)
    private BigDecimal portalShippingFlatRate;

    @Column(name = "portal_free_shipping_above", precision = 12, scale = 2)
    private BigDecimal portalFreeShippingAbove;

    @Column(name = "portal_terms_text", columnDefinition = "TEXT")
    private String portalTermsText;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
