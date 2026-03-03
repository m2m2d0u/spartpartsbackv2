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
public class UpdateReturnRequest {

    private Long invoiceId;

    private Long orderId;

    private LocalDate returnDate;

    private String notes;

    @NotEmpty(message = "At least one return item is required")
    @Valid
    private List<ReturnItemRequest> items;
}
