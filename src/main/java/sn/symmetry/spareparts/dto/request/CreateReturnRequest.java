package sn.symmetry.spareparts.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
public class CreateReturnRequest {

    private Long invoiceId;

    private Long orderId;

    @NotNull(message = "Customer ID is required")
    private Long customerId;

    @NotNull(message = "Return date is required")
    private LocalDate returnDate;

    private String notes;

    @NotEmpty(message = "At least one return item is required")
    @Valid
    private List<ReturnItemRequest> items;
}
