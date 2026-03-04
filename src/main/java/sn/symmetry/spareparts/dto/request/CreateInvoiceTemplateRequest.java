package sn.symmetry.spareparts.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sn.symmetry.spareparts.enums.InvoiceDesign;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateInvoiceTemplateRequest {

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String name;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    private Boolean isDefault;

    @Size(max = 7, message = "Primary color must not exceed 7 characters")
    private String primaryColor;

    @Size(max = 7, message = "Accent color must not exceed 7 characters")
    private String accentColor;

    @Size(max = 50, message = "Font family must not exceed 50 characters")
    private String fontFamily;

    private InvoiceDesign design;

    @Size(max = 20, message = "Header layout must not exceed 20 characters")
    private String headerLayout;

    @Size(max = 500, message = "Logo URL must not exceed 500 characters")
    private String logoUrl;

    @Size(max = 500, message = "Header image URL must not exceed 500 characters")
    private String headerImageUrl;

    @Size(max = 500, message = "Footer image URL must not exceed 500 characters")
    private String footerImageUrl;

    @Size(max = 500, message = "Stamp image URL must not exceed 500 characters")
    private String stampImageUrl;

    @Size(max = 500, message = "Signature image URL must not exceed 500 characters")
    private String signatureImageUrl;

    @Size(max = 500, message = "Watermark image URL must not exceed 500 characters")
    private String watermarkImageUrl;

    private Boolean showNinea;
    private Boolean showRccm;
    private Boolean showTaxId;
    private Boolean showWarehouseAddress;
    private Boolean showCustomerTaxId;
    private Boolean showPaymentTerms;
    private Boolean showDiscountColumn;

    private String defaultNotes;
}
