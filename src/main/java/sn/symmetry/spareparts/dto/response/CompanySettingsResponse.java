package sn.symmetry.spareparts.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanySettingsResponse {

    private Long id;
    private String companyName;
    private String logoUrl;
    private String street;
    private String city;
    private String state;
    private String postalCode;
    private String country;
    private String taxId;
    private String ninea;
    private String rccm;
    private String phone;
    private String email;
    private BigDecimal defaultTaxRate;
    private String proformaPrefix;
    private String invoicePrefix;
    private String depositPrefix;
    private String creditNotePrefix;
    private String orderPrefix;
    private String poPrefix;
    private String transferPrefix;
    private String returnPrefix;
    private Integer defaultPaymentTerms;
    private Integer defaultProformaValidity;
    private String defaultInvoiceNotes;
    private Long defaultTemplateId;
    private Boolean sequentialResetYearly;
    private String currencySymbol;
    private String currencyPosition;
    private Integer currencyDecimals;
    private Long defaultWarehouseId;
    private Long portalWarehouseId;
    private Boolean portalEnabled;
    private BigDecimal portalMinOrderAmount;
    private BigDecimal portalShippingFlatRate;
    private BigDecimal portalFreeShippingAbove;
    private String portalTermsText;
    private LocalDateTime updatedAt;
}
