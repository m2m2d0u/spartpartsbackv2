package sn.symmetry.spareparts.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sn.symmetry.spareparts.enums.InvoiceDesign;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceTemplateResponse {

    private UUID id;
    private String name;
    private String description;
    private Boolean isDefault;
    private String primaryColor;
    private String accentColor;
    private String fontFamily;
    private InvoiceDesign design;
    private String headerLayout;
    private String logoUrl;
    private String headerImageUrl;
    private String footerImageUrl;
    private String stampImageUrl;
    private String signatureImageUrl;
    private String watermarkImageUrl;
    private Boolean showNinea;
    private Boolean showRccm;
    private Boolean showTaxId;
    private Boolean showWarehouseAddress;
    private Boolean showCustomerTaxId;
    private Boolean showPaymentTerms;
    private Boolean showDiscountColumn;
    private UUID taxRateId;
    private String taxRateLabel;
    private String defaultNotes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
