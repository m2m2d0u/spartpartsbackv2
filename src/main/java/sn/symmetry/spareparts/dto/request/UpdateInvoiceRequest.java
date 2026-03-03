package sn.symmetry.spareparts.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateInvoiceRequest {

    private Long orderId;

    private Long proformaId;

    private Long depositId;

    private Long templateId;

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
