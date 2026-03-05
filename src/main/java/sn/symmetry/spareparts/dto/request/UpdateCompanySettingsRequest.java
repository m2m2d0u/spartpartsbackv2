package sn.symmetry.spareparts.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCompanySettingsRequest {

    @Size(max = 200, message = "Company name must not exceed 200 characters")
    private String companyName;

    @Size(max = 500, message = "Logo URL must not exceed 500 characters")
    private String logoUrl;

    @Size(max = 300, message = "Street must not exceed 300 characters")
    private String street;

    @Size(max = 100, message = "City must not exceed 100 characters")
    private String city;

    @Size(max = 100, message = "State must not exceed 100 characters")
    private String state;

    @Size(max = 20, message = "Postal code must not exceed 20 characters")
    private String postalCode;

    @Size(max = 100, message = "Country must not exceed 100 characters")
    private String country;

    @Size(max = 50, message = "Tax ID must not exceed 50 characters")
    private String taxId;

    @Size(max = 50, message = "NINEA must not exceed 50 characters")
    private String ninea;

    @Size(max = 50, message = "RCCM must not exceed 50 characters")
    private String rccm;

    @Size(max = 50, message = "Phone must not exceed 50 characters")
    private String phone;

    @Email(message = "Email must be valid")
    @Size(max = 200, message = "Email must not exceed 200 characters")
    private String email;

    @DecimalMin(value = "0", message = "Default tax rate must be at least 0")
    private BigDecimal defaultTaxRate;

    @Size(max = 10, message = "Proforma prefix must not exceed 10 characters")
    private String proformaPrefix;

    @Size(max = 10, message = "Invoice prefix must not exceed 10 characters")
    private String invoicePrefix;

    @Size(max = 10, message = "Deposit prefix must not exceed 10 characters")
    private String depositPrefix;

    @Size(max = 10, message = "Credit note prefix must not exceed 10 characters")
    private String creditNotePrefix;

    @Size(max = 10, message = "Order prefix must not exceed 10 characters")
    private String orderPrefix;

    @Size(max = 10, message = "PO prefix must not exceed 10 characters")
    private String poPrefix;

    @Size(max = 10, message = "Transfer prefix must not exceed 10 characters")
    private String transferPrefix;

    @Size(max = 10, message = "Return prefix must not exceed 10 characters")
    private String returnPrefix;

    @Min(value = 0, message = "Default payment terms must be at least 0")
    private Integer defaultPaymentTerms;

    @Min(value = 0, message = "Default proforma validity must be at least 0")
    private Integer defaultProformaValidity;

    private String defaultInvoiceNotes;

    private UUID defaultTemplateId;

    private Boolean sequentialResetYearly;

    @Size(max = 10, message = "Currency symbol must not exceed 10 characters")
    private String currencySymbol;

    @Size(max = 10, message = "Currency position must not exceed 10 characters")
    private String currencyPosition;

    private Integer currencyDecimals;

    @Size(max = 5, message = "Thousands separator must not exceed 5 characters")
    private String thousandsSeparator;

    private UUID defaultWarehouseId;

    private UUID portalWarehouseId;

    private Boolean portalEnabled;

    @DecimalMin(value = "0", message = "Portal minimum order amount must be at least 0")
    private BigDecimal portalMinOrderAmount;

    @DecimalMin(value = "0", message = "Portal shipping flat rate must be at least 0")
    private BigDecimal portalShippingFlatRate;

    @DecimalMin(value = "0", message = "Portal free shipping above must be at least 0")
    private BigDecimal portalFreeShippingAbove;

    private String portalTermsText;
}
