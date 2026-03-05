package sn.symmetry.spareparts.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sn.symmetry.spareparts.enums.InvoiceType;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateInvoiceRequest {

    private InvoiceType invoiceType;

    private UUID orderId;

    private UUID proformaId;

    private UUID depositId;

    private UUID templateId;

    private LocalDate issuedDate;

    private LocalDate dueDate;

    private LocalDate validityDate;

    private UUID sourceWarehouseId;

    private String notes;

    private String internalNotes;

    @NotEmpty(message = "At least one invoice item is required")
    @Valid
    private List<InvoiceItemRequest> items;
}
