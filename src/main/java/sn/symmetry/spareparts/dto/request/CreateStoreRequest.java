package sn.symmetry.spareparts.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateStoreRequest {

    @NotBlank
    @Size(max = 200)
    private String name;

    @NotBlank
    @Size(max = 20)
    private String code;

    @Size(max = 300)
    private String street;

    @Size(max = 100)
    private String city;

    @Size(max = 100)
    private String state;

    @Size(max = 20)
    private String postalCode;

    @Size(max = 100)
    private String country;

    @Size(max = 50)
    private String phone;

    @Email
    @Size(max = 200)
    private String email;

    @Size(max = 500)
    private String logoUrl;

    @Size(max = 500)
    private String stampImageUrl;

    @Size(max = 50)
    private String ninea;

    @Size(max = 50)
    private String rccm;

    @Size(max = 50)
    private String taxId;

    @Size(max = 10)
    private String proformaPrefix;

    @Size(max = 10)
    private String invoicePrefix;

    @Size(max = 10)
    private String depositPrefix;

    @Size(max = 10)
    private String creditNotePrefix;

    @Size(max = 10)
    private String orderPrefix;

    private Integer defaultPaymentTerms;

    private Integer defaultProformaValidity;

    private UUID defaultTemplateId;

    private String defaultInvoiceNotes;

    private UUID defaultWarehouseId;

    private UUID portalWarehouseId;

    @Size(max = 10)
    private String currencySymbol;

    @Size(max = 10)
    private String currencyPosition;

    private Integer currencyDecimals;
}
