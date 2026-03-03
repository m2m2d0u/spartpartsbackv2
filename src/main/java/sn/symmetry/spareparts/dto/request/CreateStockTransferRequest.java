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
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateStockTransferRequest {

    @NotNull(message = "Source warehouse ID is required")
    private UUID sourceWarehouseId;

    @NotNull(message = "Destination warehouse ID is required")
    private UUID destinationWarehouseId;

    @NotNull(message = "Transfer date is required")
    private LocalDate transferDate;

    private String notes;

    @NotEmpty(message = "At least one item is required")
    @Valid
    private List<StockTransferItemRequest> items;
}
