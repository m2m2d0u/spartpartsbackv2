package sn.symmetry.spareparts.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sn.symmetry.spareparts.enums.RestockAction;
import sn.symmetry.spareparts.enums.ReturnReason;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReturnItemRequest {

    @NotNull(message = "Part ID is required")
    private Long partId;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    @NotNull(message = "Reason is required")
    private ReturnReason reason;

    private RestockAction restockAction;

    private Long warehouseId;
}
