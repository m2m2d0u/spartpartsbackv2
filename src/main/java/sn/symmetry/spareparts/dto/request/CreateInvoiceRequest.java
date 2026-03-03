package sn.symmetry.spareparts.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sn.symmetry.spareparts.enums.InvoiceType;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateInvoiceRequest {

    @NotNull(message = "Invoice type is required")
    private InvoiceType invoiceType;

    @NotNull(message = "Customer ID is required")
    private Long customerId;

    private Long orderId;

    private Long proformaId;

    private Long depositId;

    private Long templateId;

    @NotNull(message = "Issued date is required")
    private LocalDate issuedDate;

    private LocalDate dueDate;

    private LocalDate validityDate;

    private Long sourceWarehouseId;

    private String notes;

    private String internalNotes;

    @NotEmpty(message = "At least one invoice item is required")
    @Valid
    private List<InvoiceItemRequest> items;
}
