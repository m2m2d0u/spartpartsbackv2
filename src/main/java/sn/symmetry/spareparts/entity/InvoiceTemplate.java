package sn.symmetry.spareparts.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "invoice_template")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(name = "is_default", nullable = false)
    private Boolean isDefault = false;

    @Column(name = "primary_color", nullable = false, length = 7)
    private String primaryColor = "#000000";

    @Column(name = "accent_color", nullable = false, length = 7)
    private String accentColor = "#4F46E5";

    @Column(name = "font_family", nullable = false, length = 50)
    private String fontFamily = "Helvetica";

    @Column(name = "header_layout", nullable = false, length = 20)
    private String headerLayout = "LOGO_LEFT";

    @Column(name = "logo_url", length = 500)
    private String logoUrl;

    @Column(name = "header_image_url", length = 500)
    private String headerImageUrl;

    @Column(name = "footer_image_url", length = 500)
    private String footerImageUrl;

    @Column(name = "stamp_image_url", length = 500)
    private String stampImageUrl;

    @Column(name = "signature_image_url", length = 500)
    private String signatureImageUrl;

    @Column(name = "watermark_image_url", length = 500)
    private String watermarkImageUrl;

    @Column(name = "show_ninea", nullable = false)
    private Boolean showNinea = true;

    @Column(name = "show_rccm", nullable = false)
    private Boolean showRccm = true;

    @Column(name = "show_tax_id", nullable = false)
    private Boolean showTaxId = true;

    @Column(name = "show_warehouse_address", nullable = false)
    private Boolean showWarehouseAddress = false;

    @Column(name = "show_customer_tax_id", nullable = false)
    private Boolean showCustomerTaxId = true;

    @Column(name = "show_payment_terms", nullable = false)
    private Boolean showPaymentTerms = true;

    @Column(name = "show_discount_column", nullable = false)
    private Boolean showDiscountColumn = true;

    @Column(name = "default_notes", columnDefinition = "TEXT")
    private String defaultNotes;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
