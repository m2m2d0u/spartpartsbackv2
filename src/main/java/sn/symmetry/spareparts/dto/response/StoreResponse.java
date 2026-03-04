package sn.symmetry.spareparts.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreResponse {
    private UUID id;
    private String name;
    private String code;
    private String street;
    private String city;
    private String state;
    private String postalCode;
    private String country;
    private String phone;
    private String email;
    private String logoUrl;
    private String stampImageUrl;
    private String ninea;
    private String rccm;
    private String taxId;
    private String proformaPrefix;
    private String invoicePrefix;
    private String depositPrefix;
    private String creditNotePrefix;
    private String orderPrefix;
    private Integer defaultPaymentTerms;
    private Integer defaultProformaValidity;
    private UUID defaultTemplateId;
    private String defaultInvoiceNotes;
    private UUID defaultWarehouseId;
    private UUID portalWarehouseId;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
