package sn.symmetry.spareparts.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceTemplateResponse {

    private Long id;
    private String name;
    private String description;
    private Boolean isDefault;
    private String primaryColor;
    private String accentColor;
    private String fontFamily;
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
    private String defaultNotes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
